package HundirLaFlota.network;

import java.net.Socket;

/* Clase con doble funcion: En primer lugar tiene las funciones para conectarse/reconectarse con el servidor que 
 * es quien le asignara una partida libre o con ID determinada para jugar con amigos (o le dejara reconectarse a esta).
 * 
 * En segundo lugar, esta la clase te deja mantener la conexion entre el jugador y la partida asignada(gameHandlerThread) a traves de un 
 * intermediaro (PlayerGameIntermediator, que sera el que mantiene de verdad la conexion y le pasa
 * los datos de manera ordenada a la partida). Guarda la ID de la partida actual por si hay que reconectarse.
 * Recibe/Envia datos de la partida a traves del intermediario y una vez procesados reenvia las respuestas.
 * Los inputs normalmente vendran por interrupts del raton o al enviar chat, estos llamaran a alguna funcion 
 * y luego la partida se los enviara al otro cliente que nos retornara los resultados 
 * (aprieto una posicion, me dira si barco/agua) */

public class ClientConnector extends ThreadedConnection{
	
	private boolean running = true;
	private long assignedGameID;
	
	public ClientConnector(Socket conn){
		openConnection(conn);
	}

	/*Bucle de ejecucion, esperara que lleguen datos de la partida a traves de la conexion
	 * con el intermediario y actuara acorde a ellos */
	public void run(){
		ClientTestingThread testThread = new ClientTestingThread(this);
		testThread.start();
		while (running){
			try {
			waitForAction();
			sleep(500);
			} catch(Exception e){}
		}
		System.out.println("--Client: Finishing connection");
		testThread.kill();
		closeAll();
	}
	
	/*Esta funcion hace esperar infinitamente al cliente hasta que llegan datos a traves de su
	 * conexion de entrada (vendran del intermediario) y una vez recibidos los comprueba y ejecuta
	 * lo que pidan si son legales */
	private void waitForAction(){
		try { 
			String msg;
			while ((msg = incomingData.readLine()) != null && running){
				String[] actionData = msg.trim().split(",");
				checkReceivedData(actionData);
			}
		} catch(Exception e) {
			//System.out.println("Error en la obtencion de datos: " + e.getMessage());
			stopRunning();
		}
	}
	
	/*Actua en base a los comandos recibidos*/
	private void checkReceivedData(String[] commands){
		try {
			//System.out.println("--Client: received command on stream " + Arrays.toString(commands));
			if (commands[0].equals("R")){
				System.out.println("--Client: Game with ID: " + this.assignedGameID + " -> connection check");
				sendMsg("r", outgoingData);
			}
			else if (commands[0].equals("chat")) { //Win by default, other client d/ced...
				System.out.println("--Client: Received a chat message: " + commands[1]);
			}
			else if (commands[0].equals("t")){
				System.out.println("--Client: My turn.");
			}
			else if (commands[0].equals("nt")){
				System.out.println("--Client: Not my turn.");
			}else if (commands[0].equals("d/c")){
				System.out.println("--Client: Disconnecting.");
				closeAll();
				this.stopRunning();
			}
			else if (commands[0].equals("a")) {
				//System.out.println("--Client: Enemy attacking pos: " + actionCommands[1] + ", " + actionCommands[2]);
				String HorM = (Math.random() > 0.49) ? "h" : "m";
				System.out.println("--Client: Got attacked, sending back: " + HorM + "," + commands[1] + "," + commands[2]);
				sendMsg((HorM + "," + commands[1] + "," + commands[2]), outgoingData); //Aleatori per ara, en el futur comprovacions....
			} 
			else if (commands[0].equals("h") || commands[0].equals("m")) {
				System.out.println("--Client: Results of my attack -> " + commands[0] + "," + commands[1] + "," + commands[2]);
			}
			else if (commands[0].equals("dcwin")) { //Win by default, other client d/ced...
				System.out.println("YOU WIN!!!!");
				this.stopRunning();
			}
		} catch(Exception e){
			System.out.println("Data packet isnt formatted properly " + e.getMessage());
		}
	}
	
	
	/*Funcion para hacer que los mensajes salientes hacia el servidor tengan el tipo correspondiente 
	 * a la accion que llevan asociados */
	private void composeServerMsg(int type, String msg){
		msg = Integer.toString(type) + msg;
		sendMsg(msg,this.outgoingData);
	}
	
	/*Comando para unirse a la primera partida que este libre o montar una nueva*/
	public void sendJoinGame(){
		sendJoinCustomGame(0);
	}
	
	/*Comando para unirse a una partida con una ID determinada*/
	public void sendJoinCustomGame(long gameID){
		composeServerMsg(0, Long.toString(gameID));
		try {
			String msg = incomingData.readLine();
			String[] msgValues = msg.trim().split(",");
			String player = " as player 2.";
			this.assignedGameID = Long.parseLong(msgValues[0]);
			if (msgValues[1].equals("1")) { player = " as player 1.";}
			System.out.println("--Client: Connected to game with ID: " + msgValues[1] + player); //assignar reconnect token en el futur?
		} catch (Exception e){
			System.out.println("Incorrect game ID assigned to client" + e.getMessage());
			return; //Wont start waiting for the game...
		}
		this.start(); // Starts the wait for the game to start...
	}

	/*Comando para parar la ejecucion de esta clase*/
	public void stopRunning(){
		this.running = false;
	}

	public void sendReconnectGame(){
		composeServerMsg(1, Long.toString(this.assignedGameID));
	}
	
	/*Para ejecutarlo standalone... testeo, en teoria lo generara la GUI*/
	public static void main(String[] args){
		try{
			ClientConnector cConnect = new ClientConnector(new Socket("127.0.0.1", HLFServer.DEFAULTPORT));
			sendMsg(HLFServer.HANDSHAKETEXT, cConnect.outgoingData);
			String msg;
			if (!(msg = cConnect.incomingData.readLine()).equals(HLFServer.HANDSHAKETEXT)) {
				HLFServer.log("wrong client handshake: " + msg); //canviar a enviar msg error al client
				cConnect.stopRunning();
			}
			cConnect.sendJoinGame();
			
		} catch(Exception e){}
	}
}
