package HundirLaFlota.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import HundirLaFlota.gui.MainWindow;
import HundirLaFlota.misc.BoatCoordsSet;

/*Clase que hace de servidor para partidas de Hundir la Flota. Su funcion es escuchar por conexiones entrantes, comprobar que sean clientes de HLF y asignarles
 * la partida que desean. Una vez son dos jugadores en una partida esta comienza y se ejecuta de manera independiente al servidor (thread) mientras este
 * sigue escuchando por si hay mas conexiones. Genera a la vez un thread con un menu con opciones basicas para controlar algunos detalles del servidor en runtime
 * a traves de la consola.*/
public class HLFServer extends Thread{
	
	static final String HANDSHAKETEXT = "HLF"; //TEXTO QUE ENVIARAN TANTO EL CLIENTE COMO EL SERVIDOR EL UNO AL OTRO PARA SABER QUE SE ESTAN COMUNICANDO ENTRE SI Y NO ES UN PROGRAMA ALEATORIO
	public static final int DEFAULTPORT = 4522;
	public static final long MAXAMOUNTFORPUBLICGAMES = 100000000; //Pueden haber estas partidas publicas (por encima seran privadas)
	public static final long MAXGAMEID = 999999999; 
	public static int POSICIONESTOTALESBARCOS; //NOTA: No sera dinamico por partida, cambiar a lo mejor...

	private int port;
	private boolean running = true;
	private static long actualGameId;
	private static ArrayList<GameHandlerThread> concurrentGames = new ArrayList<GameHandlerThread>(); //Asigna a cada gameId un objeto GameHandlerThread que controla el progreso de la partida
	private static boolean debug = true; //Para mensajes sobre la ejecucion y progreso partidas
	
	public HLFServer(int customPort) {
		POSICIONESTOTALESBARCOS = MainWindow.getShipTotalPositions();
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
			ConnReaperThread reaperThread = new ConnReaperThread();
			reaperThread.start();
			while (running){
					clientConn = listener.accept();
					GameAssigner GA = new GameAssigner(clientConn);
					GA.start();
			}					
			listener.close();
			reaperThread.stopExecution();
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
			if (concurrentGames.get(i).getGameID() <= actualGameId) {  //Si es una partida publica (no tiene una custom gameID...)
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
			if (concurrentGames.get(i).getGameID() == gameID) {
				log("Finishing game with ID: "+ gameID);
				concurrentGames.remove(i);
			}
		}
	}
	
	/*Devuelve la partida con gameID determinada*/
	public static synchronized GameHandlerThread findCustomGame(long cGameID){ //Se podria crear una lista de partidas publicas i una de privadas para hacer mas rapida esta buskeda...?
		for (int i = 0; i < concurrentGames.size(); i++){
			if (concurrentGames.get(i).getGameID() == (cGameID + MAXAMOUNTFORPUBLICGAMES)) {  
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
	public static synchronized void createNewPublicGame(Socket playerConn,BoatCoordsSet playerSet){
		GameHandlerThread newGame = new GameHandlerThread(actualGameId++, playerConn);
		newGame.setPlayer1Positions(playerSet);
		log("Obtenidas posiciones jugador 1: " + playerSet.toString());
		concurrentGames.add(newGame);
		log("Assigning player to new game: " + (actualGameId-1) + ", gameId is now: " + actualGameId);
		if (actualGameId >= MAXAMOUNTFORPUBLICGAMES) { actualGameId = 0; } //reinicializamos el numero de las partidas publicas
	}
	
	/*Crea una nueva partida custom (con un gameID del orden de 10000000+, esta es la teoria pero es facil hacer que 
	 * se acepten letras tambien (FUTURO supongo mas que nada para future proof)	 */
	public static synchronized void createNewCustomGame(long cGameID, Socket playerConn, BoatCoordsSet playerSet){
		if(cGameID + MAXAMOUNTFORPUBLICGAMES == getHighestNonOccupiedGameID()) { cGameID++;} //Las partidas privadas deben estar por encima del numero maximo de publicas para que no haya colisiones (eso o hacer dos listas...)
		GameHandlerThread newGame = new GameHandlerThread((cGameID + MAXAMOUNTFORPUBLICGAMES), playerConn);
		log("Obtenidas posiciones jugador 1: " + playerSet.toString());
		newGame.setPlayer1Positions(playerSet);
		concurrentGames.add(newGame);
		log("Assigning player to new custom game with ID: " + cGameID);
	}
	
	/*Funcion para reconectar a un usuario a una partida con gameID determinada*/
	public static void reconnectUser(boolean isP1, long gameID, Socket conn){
		for (int i = 0; i < concurrentGames.size(); i++){
			if (concurrentGames.get(i).getGameID() == gameID) {  //Si es una partida publica (no tiene una custom gameID...)
				concurrentGames.get(i).reconnectUser(isP1, conn);
				}
			}
	}
	
	/*Funcion para escribir los detalles de la ejecucion si la variable debug es verdadera*/
	public static void log(String text){ //en el futuro escribir a file, solo private (public para debugear conexion con resta cosas
		if (debug){	System.out.println(text); }
	}
	
	public static synchronized ArrayList<GameHandlerThread> getGames(){
		return concurrentGames;
	}
	
	public static synchronized long getHighestNonOccupiedGameID(){
		long max = 0;
		if (concurrentGames.size() == 0) { return max; }
		for (int i = 0; i < concurrentGames.size(); i++){
				if (concurrentGames.get(i).getGameID() > max) {
					max = concurrentGames.get(i).getGameID();
				}
		}
		return max;
	}
	
	/*Lista los juegos ejecutandose ahora mismo y el turno que hay*/
	public static void listGames() {
		ArrayList<GameHandlerThread> games = getGames();
		for (int i = 0; i < games.size(); i++){
			log(games.get(i).toString());
		}
	}
	
	/*Funcion para cerrar el server*/
	public void close(){
		running = false;
	}
	
	/*Codigo de testeo standalone (sin usar threads) */
	
	public static void main(String[] args){
		//System.out.println(System.getProperty("user.dir"));
		HLFServer server = new HLFServer();
		server.listenForClients();
	}
}
