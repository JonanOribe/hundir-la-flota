package HundirLaFlota.network;

import java.io.IOException;
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
	
	private volatile boolean running;
	private PanelCombate contenedor;
	private String IP = "127.0.0.1";
	private int port = HLFServer.DEFAULTPORT;
	private long assignedGameID;
	private int myPlayerNum;
	private boolean halfTurn = false;
	
	public ClientConnector(PanelCombate contenedor, String IP, int port, long customGameID) {
		this.contenedor = contenedor;
		this.IP = IP;
		this.port = port;
		this.assignedGameID = customGameID;
	}
	
	public ClientConnector(Socket conn){
		openConnection(conn);
	}
	
	/*Bucle de ejecucion, esperara que lleguen datos de la partida a traves de la conexion
	 * con el intermediario y actuara acorde a ellos */
	public void run(){
		if (!connectToServer(assignedGameID)) { 
			running = false; 
			contenedor.writeInChat("No se ha podido conectar al servidor.");
		} else { running = true; }
		while (running){
			try {
			waitForAction();
			sleep(500);
			}catch(Exception e){
				System.out.println("Error en la conexion con el servidor: " + e.getMessage());
			}
		}
		contenedor.writeInChat("Cerrada la conexion con el servidor.");
		ClientLogic.jugadorSeDesconecta(true, false, contenedor);
		closeAll();
	}

	public boolean sendMsgFromGUI(String msg){
		if (outgoingData != null) {
			sendMsg(msg, outgoingData);
			return true;
		}
		return false;
	}
	
	public boolean connectToServer(){
		return connectToServer(0);
	}
	
	public boolean connectToServer(long customGameID){
		try{
			openConnection(new Socket(IP, port));
			sendMsg(HLFServer.HANDSHAKETEXT, this.outgoingData);
			String msg;
				while (true){
				if ((msg = this.incomingData.readLine()) == null) { continue; }  
				if (!(msg.equals(HLFServer.HANDSHAKETEXT))) {
					HLFServer.log("wrong client handshake: " + msg); 
					contenedor.writeInChat("Version de HLF obsoleta o con errores");
					ClientLogic.jugadorSeDesconecta(true, false, contenedor);
					this.stopRunning();
					return false;
				}
				if (!sendJoinCustomGame(customGameID)) { return false; }
				ClientLogic.jugadorSeDesconecta(false, false, contenedor);
				//EN EL FUTURO PONER AQUI EL ENVIAR LAS POSICIONES BARCOS USUARIO...
				return true;
				}
		}catch(java.net.ConnectException CE){
			ClientLogic.jugadorSeDesconecta(true, false, contenedor); 
		}catch(Exception e){
			//System.out.println("uhhh error: " + e.getMessage());
		}
		return false;
	}
	
	/*Esta funcion hace esperar infinitamente al cliente hasta que llegan datos a traves de su
	 * conexion de entrada (vendran del intermediario) y una vez recibidos los comprueba y ejecuta
	 * lo que pidan si son legales */
	private void waitForAction(){
		try { 
			String msg;
			while ((msg = incomingData.readLine()) != null && running){
				ClientLogic.parseClientData(msg, this);
			}
		}catch(java.net.ConnectException CE){
			contenedor.writeInChat("El servidor ha cerrado su conexion.");
		}catch (IOException IE){
		} catch(Exception e) {
			System.out.println("Error en la conexion con el servidor: " + e.getMessage());
		}
		stopRunning();	

	}
	
	public PanelCombate getContenedor() {
		return contenedor;
	}
	
	public int getPlayerNum() {
		return this.myPlayerNum;
	}
	
	public boolean getHalfTurn(){
		return this.halfTurn;
	}
	
	public void setHalfTurn(boolean newT){
		halfTurn = newT;
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
	public boolean sendJoinCustomGame(long gameID){
		composeServerMsg(0, Long.toString(gameID));
		try {
			String msg = incomingData.readLine();
			String[] msgValues = msg.trim().split(",");
			String player = " como Jugador 2.";
			this.assignedGameID = Long.parseLong(msgValues[0]);
			String gameType = (assignedGameID == 0) ? "publica" : "privada"; 
			String customGameID = "";
			if (assignedGameID >= HLFServer.MAXAMOUNTFORPUBLICGAMES) {
				customGameID = Long.toString(assignedGameID - HLFServer.MAXAMOUNTFORPUBLICGAMES);
			}
			else {
				customGameID = Long.toString(assignedGameID);
			}
			contenedor.setJugadorDC(false);
			myPlayerNum = 2;
			if (msgValues[1].equals("1")) { player = " como Jugador 1, esperando a un oponente..."; myPlayerNum = 1;}
			contenedor.writeInChat("Conectado a la partida " + gameType +" con ID " + customGameID + player); //assignar reconnect token en el futur?
			return true;
		} catch (Exception e){
			System.out.println("ID asignada equivocada " + e.getMessage());
			return false; //Wont start waiting for the game...
		}
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
		return (this.conn != null);
	}
	
	public long getGameID() {
		return this.assignedGameID;
	}
	
	public void setGameID(long newID){
		this.assignedGameID = newID;
	}
	
	/*Para ejecutarlo standalone... testeo, en teoria lo generara la GUI*/
	public static void main(String[] args){
		try{
			ClientConnector cConnect = new ClientConnector(new Socket("127.0.0.1", HLFServer.DEFAULTPORT));
			cConnect.start();
		} catch(Exception e){}
	}
}
