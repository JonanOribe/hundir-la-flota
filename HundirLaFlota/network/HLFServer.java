package HundirLaFlota.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/*Clase que hace de servidor para partidas de Hundir la Flota. Su funcion es escuchar por conexiones entrantes, comprobar que sean clientes de HLF y asignarles
 * la partida que desean. Una vez son dos jugadores en una partida esta comienza y se ejecuta de manera independiente al servidor (thread) mientras este
 * sigue escuchando por si hay mas conexiones. Genera a la vez un thread con un menu con opciones basicas para controlar algunos detalles del servidor en runtime
 * a traves de la consola.*/
public class HLFServer extends Thread{
	
	static final String HANDSHAKETEXT = "HLF"; //TEXTO QUE ENVIARAN TANTO EL CLIENTE COMO EL SERVIDOR EL UNO AL OTRO PARA SABER QUE SE ESTAN COMUNICANDO ENTRE SI Y NO ES UN PROGRAMA ALEATORIO
	public static final int DEFAULTPORT = 4522;
	private int port;
	private boolean running = true;
	private static long actualGameId;
	private static ArrayList<GameHandlerThread> concurrentGames = new ArrayList<GameHandlerThread>(); //Asigna a cada gameId un objeto GameHandlerThread que controla el progreso de la partida
	private static boolean debug = true; //Para mensajes sobre la ejecucion y progreso partidas
	
	public HLFServer(int customPort) {
		actualGameId = 0;
		port = customPort;
	}
	
	public HLFServer(){
		this(DEFAULTPORT);
	}
	
	protected void changePort(int port) {
		this.port = port;
	}
	
	/*Bucle principal del programa, escucha por si hay alguna conexion entrante y si la hay y es un cliente
	 * verificado se le une a una partida y se comienza la partida si son dos jugadores. Los primeros datos
	 * seran enviados/recibidos a partir de un objeto gameAssigner y luego la conexion sera gestionada
	 * mediante los PlayerGameIntermediator de la partida asignada	 */
	public void listenForClients(){
		DiagnosticThread menu = new DiagnosticThread(this);
		menu.start();
		try {
			ServerSocket listener = new ServerSocket(port);
			Socket clientConn;

			log("Listening for connections...");
			GameAssigner GA = new GameAssigner();
			while (running){
					clientConn = listener.accept();
					if (!GA.openConnection(clientConn)) { continue; }
					if (!GA.performHandshake()) { continue; }
					long[] playerAction = GA.playerIntention();
					switch ((int)playerAction[0]){
						case 0:
							GA.assignPlayerToGame((playerAction[1] != 0), playerAction[1]); //join game
							break;
						case 1:
							reconnectUser(true, playerAction[1], clientConn); //rejoin as P1
							break;
						case 2:
							reconnectUser(false, playerAction[1], clientConn); //rejoin as P2
							break;
						case 3:
							break;
						default:
							log("Unknown response to: " + Arrays.toString(playerAction));
					}
			}
			listener.close();
			close();
		} catch(Exception e) {
			System.out.println("Error en la conexion con los clientes: " + e.getMessage());
			close();
		}
	}
	
	/*Si quieres que no se ejecute como programa principal sino como thread...*/
	public void run(){
		listenForClients();
	}
	
	/*Funcion para retornar la siguiente partida publica vacia*/
	public static synchronized GameHandlerThread nextEmptyPublicGame(){
		for (int i = 0; i < concurrentGames.size(); i++){
			if (concurrentGames.get(i).gameID <= actualGameId) {  //Si es una partida publica (no tiene una custom gameID...)
				if (!concurrentGames.get(i).hasP2()) { 
					return concurrentGames.get(i);
				}
			}
		}
		log("No empty games");
		return null;
	}
	
	/*Elimina una partida de la array de partidas en ejecucion*/
	public static void finishGame(long gameID){
		for (int i = 0; i < concurrentGames.size(); i++){
			if (concurrentGames.get(i).gameID == gameID) {
				log("Finishing game with ID: "+ gameID);
				concurrentGames.remove(i);
			}
		}
	}
	
	/*Devuelve la partida con gameID determinada*/
	public static synchronized GameHandlerThread findCustomGame(long cGameID){ //Se podria crear una lista de partidas publicas i una de privadas para hacer mas rapida esta buskeda...?
		for (int i = 0; i < concurrentGames.size(); i++){
			if (concurrentGames.get(i).gameID == cGameID) {  //Si es una partida publica (no tiene una custom gameID...)
				if (!concurrentGames.get(i).hasP2()) { 
					return concurrentGames.get(i);
				}
				else { 
					log("The custom game is filled already");
					return null;
				}
			}
		}
		log("There is not an existant game with that ID");
		return null;
	}
	
	/*Crea una nueva partida publica (con un gameID asigando a partir de la actualGameID que comienza a 0 y va incrementando
	 * en 1 por cada nueva partida creada)	 */
	public static synchronized void createNewPublicGame(Socket playerConn){
		GameHandlerThread newGame = new GameHandlerThread(actualGameId++, playerConn);
		concurrentGames.add(newGame);
		log("Assigning player to new game: " + (actualGameId-1) + ", gameId is now: " + actualGameId);
	}
	
	/*Crea una nueva partida custom (con un gameID del orden de 4000000+, esta es la teoria pero es facil hacer que 
	 * se acepten letras tambien (FUTURO supongo mas que nada para future proof)	 */
	public static synchronized void createNewCustomGame(long cGameID, Socket playerConn){
		GameHandlerThread newGame = new GameHandlerThread(cGameID, playerConn);
		concurrentGames.add(newGame);
		log("Assigning player to new custom game with ID: " + cGameID);
	}
	
	/*Funcion para reconectar a un usuario a una partida con gameID determinada*/
	public static void reconnectUser(boolean isP1, long gameID, Socket conn){
		for (int i = 0; i < concurrentGames.size(); i++){
			if (concurrentGames.get(i).gameID == gameID) {  //Si es una partida publica (no tiene una custom gameID...)
				concurrentGames.get(i).reconnectUser(isP1, conn);
				}
			}
	}
	
	/*Funcion para escribir los detalles de la ejecucion si la variable debug es verdadera*/
	public static void log(String text){ //en el futuro escribir a file, solo private (public para debugear conexion con resta cosas
		if (debug){	System.out.println(text); }
	}
	
	/*Lista los juegos ejecutandose ahora mismo y el turno que hay*/
	public static void listGames() {
		for (int i = 0; i < concurrentGames.size(); i++){
			log(concurrentGames.get(i).toString());
		}
	}
	
	/*Funcion para cerrar el server*/
	public void close(){
		running = false;
	}
	
	public static void main(String[] args){
		//System.out.println(System.getProperty("user.dir"));
		HLFServer server = new HLFServer();
		server.listenForClients();
	}
}
