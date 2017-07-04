package network;

import java.net.Socket;

/*Clase para mantener la conexion entre el y la partida (gameHandlerThread), guarda algunos datos
 * de la partida actual y por si hay que reconectarse. Recibe/Envia datos de la partida y una vez
 * procesados reenvia las respuestas. Los inputs normalmente vendran por interrupts del raton que
 * llamaran a alguna funcion y luego la partida se los enviara al otro cliente que nos retornara
 * los resultados (aprieto una posicion, me dira si barco/agua) y lo mismo para el otro cliente 
 * que hara lo mismo y este respondera acorde a su grid. */
public class ClientConnection extends ThreadedConnection{
	
	private boolean running = true;
	private long assignedGameID;
	private boolean isMyTurn = false;
	private boolean isP1 = false; // for reconnecting
	
	public ClientConnection(Socket conn){
		openConnection(conn);
	}

	public void run(){
		ClientTestingThread testThread = new ClientTestingThread(this); //Inicializa la clase para enviar custom msgs al server sin usar el raton o comenzar el programa principal
		testThread.start();
		while (running){
			waitForAction();
		}
		testThread.kill();
	}
	
	/*Esta funcion hace esperar infinitamente al cliente hasta que llegan datos del servidor 
	 * (para que el cliente envia cosas nos basamos en interrupts via mouse o teclado)	 */
	public void waitForAction(){
		try { //Wait for a msg back from the server
			String msg;
			while ((msg = incomingDataP1.readLine()) != null){
				System.out.println("--Client: Received data: " + msg);
				String[] actionData = msg.trim().split(",");
				checkForActionData(actionData);
			}
		} catch(Exception e) {
			System.out.println("error en la obtencion de datos: " + e.getMessage());
			closeAll();
		}
	}
		
	
	/*Funcion usada por los interrupts del raton del usuario (Ej: clicka en la posicion 
	 * determinada en el grid para disparar a esa posicion cuando es su turno etc */
	public void sendAction(String action){
		
	}
	
	/*Traduce las acciones que llegan del servidor a reacciones para el cliente (añadir)
	 * Introducirlas como constantes en el futuro...*/
	public int checkForActionData(String[] actionCommands) { 
		try {
			sleep(100);
			if (actionCommands[0].equals("R")){
				System.out.println("--Client: Game with ID: " + this.assignedGameID + " connection check");
				sendMsg("r", outgoingDataP1);
			}
			else if (actionCommands[0].equals("t")){
				System.out.println("--Client: My turn.");
				isMyTurn = true; //per futures comprobacions???
			}
			else if (actionCommands[0].equals("nt")){
				System.out.println("--Client: Not my turn.");
				isMyTurn = false;
			}
			else if (actionCommands[0].equals("a")) {
				//System.out.println("--Client: Enemy attacking pos: " + actionCommands[1] + ", " + actionCommands[2]);
				String HorM = (Math.random() > 0.49) ? "h" : "m";
				System.out.println("--Sending back: " + HorM);
				sendMsg(HorM, outgoingDataP1); //Aleatori per ara, en el futur comprovacions....
			} 
			else if (actionCommands[0].equals("dcwin")) { //Win by default, other client d/ced...
				this.closeAll();
				System.out.println("YOU WIN!!!!");
			}
		} catch(Exception e){
			System.out.println("Data packet isnt formatted properly " + e.getMessage());
		}
		return 0;
	}
	
	public boolean myTurn(){
		return isMyTurn;
	}
	
	/*Funcion para hacer que los mensajes salientes tengan el tipo correspondiente a la accion que llevan
	 * asociados (cambiar, añadir acciones o modificar protocolo?) */
	private void composeOutGoingMsg(int type, String msg){
		if (type == 0) { //join
			msg = '0' + msg;
		} else if (type == 1){ //rejoin as P1
			msg = '1' + msg;
		} else if (type == 2){ //rejoin as P2
		msg = '2' + msg;
		}
		sendMsg(msg,this.outgoingDataP1);
	}
	
	/*Comando para unirse a la primera partida que este libre o montar una nueva*/
	public void sendJoinGame(){
		sendJoinCustomGame(0);
	}
	
	/*Comando para unirse a una partida con una ID determinada*/
	public void sendJoinCustomGame(long gameID){
		composeOutGoingMsg(0, Long.toString(gameID));
		try {
			String msg = incomingDataP1.readLine();
			String[] msgValues = msg.trim().split(",");
			assignedGameID = Long.parseLong(msgValues[0]);
			String player = " as player 2.";
			if (msgValues[1].equals("1")) { this.isP1 = true; player = " as player 1.";}
			System.out.println("--Client: Connected to game with ID: " + assignedGameID + player); //assignar reconnect token en el futur?
		} catch (Exception e){
			System.out.println("Incorrect game ID assigned to client" + e.getMessage());
			return; //Wont start waiting for the game...
		}
		this.start(); // Starts the wait for the game to start...
	}

	@Override
	public void closeAll(){
		this.running = false;
		super.closeAll();
	}
	
	public void setP1Status(boolean stat){
		this.isP1 = stat;
	}
	
	public boolean isP1(){
		return isP1;
	}
	
	public void sendReconnectGame(){
		composeOutGoingMsg(1, Long.toString(this.assignedGameID));
	}
	
	/*Para ejecutarlo standalone... en teoria lo generara la GUI*/
	public static void main(String[] args){
		try{
			ClientConnection cConnect = new ClientConnection(new Socket("127.0.0.1", HLFServer.DEFAULTPORT));
			sendMsg(HLFServer.HANDSHAKETEXT, cConnect.outgoingDataP1);
			String msg;
			if (!(msg = cConnect.incomingDataP1.readLine()).equals(HLFServer.HANDSHAKETEXT)) {
				HLFServer.log("wrong client handshake: " + msg); //canviar a enviar msg error al client
				cConnect.closeAll();
			}
			cConnect.sendJoinGame();
			
		} catch(Exception e){}
	}
}
