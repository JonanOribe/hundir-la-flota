package HundirLaFlota.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/*01/07 -> Hacer funcion para la reconexion de usuarios (comprobar que la IP sea la que tenia el antiguo user, reasignarlo al GameHandlerThread k le toca) * */
public class HLFServer{
	
	static final String HANDSHAKETEXT = "HLF"; //TEXTO QUE ENVIARAN TANTO EL CLIENTE COMO EL SERVIDOR EL UNO AL OTRO PARA SABER QUE SE ESTAN COMUNICANDO ENTRE SI Y NO ES UN PROGRAMA ALEATORIO
	static final char ACCION = '0';  //char situado en la posicion 0 de cada mensaje para saber que es una accion y no cerrar, puesto por si luego se quieren poner mas opciones (NO NECESARIO BORRAR)
	static final char JOINCHAR = '0';
	static final char ACTIONCHAR = '1';
	static final char CERRARCONEXION = '1'; //char situado en la posicion 0 de cada mensaje para cerrar conexion (NO NECESARIO YA QUE SOLO SE ENVIARA UN MSG POR CONEXION)
	static final String[] allowedActions = {"inicio", "desconectado", "bombardear", "tocado", "agua"};
	//private String defaultAddress = "127.0.0.1"; Esto es para el cliente
	static final int DEFAULTPORT = 4522;
	private int port;
	private static long actualGameId;
	private static ArrayList<GameHandlerThread> concurrentGames = new ArrayList<GameHandlerThread>(); //Asigna a cada gameId un objeto gameState que controla el progreso de la partida
	private static boolean debug = true;
	//MSG ENTRADA: CLIENT HANDSHAKE -> (WAIT FOR SERVER RESPONSE, IF CORRECT) -> CLIENT IP, Action, Pos(optional) -> SERVER (LOOK FOR MATCHING gameID otherPlayer)
	
	public HLFServer(int customPort) {
		actualGameId = 0;
		port = customPort;
		listenForClients();
	}
	
	public HLFServer(){
		this(DEFAULTPORT);
	}
	
	protected void changePort(int port) {
		this.port = port;
	}
	
	protected void listenForClients(){
		DiagnosticThread menu = new DiagnosticThread();
		menu.start();
		try {
			ServerSocket listener = new ServerSocket(port);
			Socket clientConn;

			log("Listening for connections...");
			GameAssigner GA = new GameAssigner();
			while (true){
					clientConn = listener.accept();
					if (!GA.openConnection(clientConn)) { continue; }
					if (!GA.performHandshake()) { continue; }
					long[] playerAction = GA.playerIntention();
					switch ((int)playerAction[0]){
						case 0:
							GA.assignPlayerToGame((playerAction[0] != 0), playerAction[1]);
							break;
						case 1:
							reconnectUser(true, playerAction[1], clientConn);
							break;
						case 2:
							reconnectUser(false, playerAction[1], clientConn);
							break;
						default:
							log("Unknown response to: " + Arrays.toString(playerAction));
					}
			}
		} catch(Exception e) {
			System.out.println("Error en la conexion con los clientes: " + e.getMessage());
			close();
		}
	}
	
	public static GameHandlerThread nextEmptyPublicGame(){
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
	
	public static void finishGame(long gameID){
		for (int i = 0; i < concurrentGames.size(); i++){
			if (concurrentGames.get(i).gameID == gameID) {
				log("Finishing game with ID: "+ gameID);
				concurrentGames.remove(i);
			}
		}
	}
	
	public static GameHandlerThread findCustomGame(long cGameID){ //Se podria crear una lista de partidas publicas i una de privadas para hacer mas rapida esta buskeda...?
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
	
	public static void createNewPublicGame(Socket playerConn){
		GameHandlerThread newGame = new GameHandlerThread(actualGameId++, playerConn);
		concurrentGames.add(newGame);
		log("Assigning player to new game: " + (actualGameId-1) + ", gameId is now: " + actualGameId);
	}
	
	public static void createNewCustomGame(long cGameID, Socket playerConn){
		GameHandlerThread newGame = new GameHandlerThread(cGameID, playerConn);
		concurrentGames.add(newGame);
		log("Assigning player to new custom game with ID: " + cGameID);
	}
	
	public static void reconnectUser(boolean isP1, long gameID, Socket conn){
		for (int i = 0; i < concurrentGames.size(); i++){
			if (concurrentGames.get(i).gameID == gameID) {  //Si es una partida publica (no tiene una custom gameID...)
				concurrentGames.get(i).reconnectUser(isP1, conn);
				}
			}
	}
	
	public static void log(String text){ //en el futuro escribir a file, solo private (public para debugear conexion con resta cosas
		if (debug){	System.out.println(text); }
	}
	
	public static void listGames() {
		for (int i = 0; i < concurrentGames.size(); i++){
			log(concurrentGames.get(i).toString());
		}
	}
	
	public static void printSeparator(){
		log("-----------------------------");
		log("-----------------------------");
	}
	
	public static void close(){
		System.exit(0); //cambiar
	}
	
	public static void main(String[] args){
		System.out.println(System.getProperty("user.dir"));
		HLFServer server = new HLFServer();
	}
}
