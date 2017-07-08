package HundirLaFlota.network;

import java.net.Socket;

import HundirLaFlota.gui.PanelCombate;

/* Clase con doble funcion: En primer lugar tiene las funciones para conectarse/reconectarse con el servidor que 
 * es quien le asignara una partida libre o con ID determinada para jugar con amigos (o le dejara reconectarse a esta).
 * 
 * En segundo lugar, la clase te deja mantener la conexion entre el jugador y la partida asignada(gameHandlerThread) a 
 * traves de un intermediaro (PlayerGameIntermediator, que sera el que mantiene de verdad la conexion y le pasa
 * los datos de manera ordenada a la partida). Guarda la ID de la partida actual por si hay que reconectarse.
 * Recibe/Envia datos de la partida a traves del intermediario y una vez procesados reenvia las respuestas.
 * Los inputs normalmente vendran por interrupts del raton o al enviar chat, estos llamaran a alguna funcion 
 * y luego la partida se los enviara al otro cliente que nos retornara los resultados 
 * (aprieto una posicion, me dira si barco/agua) */

public class ClientConnector extends ThreadedConnection{
	
	private boolean running = true;
	private PanelCombate contenedor;
	private String IP = "127.0.0.1";
	private int port = HLFServer.DEFAULTPORT;
	private long assignedGameID;
	private int myPlayerNum;
	
	public ClientConnector(PanelCombate contenedor, String IP, int port) {
		this.contenedor = contenedor;
		this.IP = IP;
		this.port = port;
	}
	
	public ClientConnector(Socket conn){
		openConnection(conn);
	}

	public void sendMsgFromGUI(String msg){
		if (outgoingData != null) sendMsg(msg, outgoingData);
	}
	
	public void connectAndStart(){ //NOTA: Cambiar. NO ESTA THREADED HASTA QUE RECIBE EL HANDSHAKE, SI EL SERVIDOR TARDA MUCHO SE PUEDE LIAR? (SI EL SERVIDOR ESTA D/C PETA POR ESO)
		try{
			openConnection(new Socket(IP, port));
			sendMsg(HLFServer.HANDSHAKETEXT, this.outgoingData);
			String msg;
				while (true){
				if ((msg = this.incomingData.readLine()) == null) { continue; }  
				if (!(msg.equals(HLFServer.HANDSHAKETEXT))) {
					HLFServer.log("wrong client handshake: " + msg); 
					contenedor.writeInChat("Version de HLF obsoleta o con errores");
					jugadorSeDesconecta(true);
					this.stopRunning();
					break;
				}
				this.sendJoinGame();
				jugadorSeDesconecta(false);
				this.start();
				break;
				}
		}catch(java.net.ConnectException CE){
			contenedor.writeInChat("Conexion al servidor rechazada.");
			jugadorSeDesconecta(true);
			return;
		}catch(Exception e){
			//System.out.println("uhhh error: " + e.getMessage());
		}
	}
	/*Bucle de ejecucion, esperara que lleguen datos de la partida a traves de la conexion
	 * con el intermediario y actuara acorde a ellos */
	public void run(){
		while (running){
			try {
			waitForAction();
			sleep(500);
			} catch(Exception e){}
		}
		System.out.println("Finalizando conexion");
		closeAll();
	}
	
	/*Esta funcion hace esperar infinitamente al cliente hasta que llegan datos a traves de su
	 * conexion de entrada (vendran del intermediario) y una vez recibidos los comprueba y ejecuta
	 * lo que pidan si son legales */
	private void waitForAction(){
		try { 
			String msg;
			while ((msg = incomingData.readLine()) != null && running){
				checkReceivedData(msg);
			}
		} catch(Exception e) {
			//System.out.println("Error en la obtencion de datos: " + e.getMessage());
			stopRunning();
		}
	}
	
	/*Actua en base a los comandos recibidos*/
	private void checkReceivedData(String command){
		try {
			//System.out.println("--Client: received command on stream " + Arrays.toString(commands));
			String[] commands = command.trim().split(",");
			//if (commands[0].equals("R")){
				//contenedor.writeInChat("Connection check");
				sendMsg("r", outgoingData);
		//	}
			if (commands[0].equals("start")){
				contenedor.writeInChat("Dos jugadores conectados. Comenzando la partida!");
			}
			else if (commands[0].equals("chat")) { //Win by default, other client d/ced...
				int otherPlayerNum = (myPlayerNum == 1) ? 2:1;
				contenedor.writeInChat("Jugador " + otherPlayerNum + ": " + command.substring(5));
			}
			else if (commands[0].equals("t")){
				contenedor.writeInChat("Es tu turno.");
			}
			else if (commands[0].equals("nt")){
				contenedor.writeInChat("Turno del oponente.");
			}
			else if (commands[0].equals("d/c")){
				contenedor.writeInChat("Desconectando...");
				closeAll();
				this.stopRunning();
				contenedor.writeInChat("Desconectado.");
				jugadorSeDesconecta(true);
			}
			else if (commands[0].equals("a")) {
				//Primero comprueba si la posicion en la grid es barco o agua (y la misma funcion isAHit cambiara los graficos
				//Y luego escribe en el chat y envia el mensaje correspondiente al servidor de si ha sido agua o tocado
				String HorM = (contenedor.enemyAttacksPos(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]))) ? "h":"m";
				contenedor.writeInChat("El oponente ataco la posicion: " + ((char)('A' + Integer.parseInt(commands[1]) - 1)) + "," + commands[2]);
				sendMsg((HorM + "," + commands[1] + "," + commands[2]), outgoingData); //Aleatori per ara, en el futur comprovacions....
			} 
			else if (commands[0].equals("h") || commands[0].equals("m")) {
				String result = (commands[0].equals("h")) ? "Tocado!" : "Agua!";  //!!!!!Hacer futura comprobacion de hundido (enviar tyh o algo para diferenciar)!!!!!!
				contenedor.writeInChat("Resultados de mi ataque-> " + result);
				//Dependiendo de si era agua o tocado cambiara los graficos en la label correspondiente del grid superior (donde estan los barcos enemigos)
				contenedor.drawMyAttackResults(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]), (commands[0].equals("h")));
			}
			else if (commands[0].equals("dcwin")) { //Win by default, other client d/ced...
				contenedor.writeInChat("El oponente se desconecto. Has ganado!");
				this.stopRunning();
			}
		} catch(Exception e){
			contenedor.writeInChat("Error en el formato de los datos enviados " + e.getMessage());
		}
	}	
	
	/*Funcion que cambia el texto de los botones por si el jugador aprieta el boton de desconectar/salir
	 * o el de reconectar (si se desconecta -> salir (que cerrara el programa), si se reconecta -> desconectar)*/
	private void jugadorSeDesconecta(boolean desconectando){
		if (desconectando){
			contenedor.cambiaReconnectButton("Reconectar");
			contenedor.cambiaQuitButton("Salir");
			contenedor.setJugadorDC(true);
		} else {
			contenedor.cambiaQuitButton("Desconectar");
			contenedor.cambiaReconnectButton(" ");
			contenedor.setJugadorDC(false);
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
			String player = " como Jugador 2.";
			this.assignedGameID = Long.parseLong(msgValues[0]);
			myPlayerNum = 2;
			if (msgValues[1].equals("1")) { player = " como Jugador 1."; myPlayerNum = 1;}
			contenedor.writeInChat("Conectado a partida con ID " + msgValues[0] + player); //assignar reconnect token en el futur?
		} catch (Exception e){
			System.out.println("ID asignada equivocada " + e.getMessage());
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
	
	public void setIP(String newIP){
		this.IP = newIP;
	}
	
	public void setPort(int newPort){
		this.port = newPort;
	}
	
	public boolean isConnected(){
		return this.conn != null;
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
