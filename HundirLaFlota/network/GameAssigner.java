package HundirLaFlota.network;

import java.net.Socket;

import HundirLaFlota.gui.MainWindow;
import HundirLaFlota.misc.BoatCoordsSet;

/*Clase threaded que crea una conexion inicial con el cliente, comprueba que es un cliente legal y le deja unirse
 * a la partida deseada generada por HLFServer. */
public class GameAssigner extends ThreadedConnection{
	
	private volatile boolean running;
	
	public GameAssigner(Socket newConn){
		running = this.openConnection(newConn) ? true : false;
	}
	
	public void run() {
		while(running){
			//HandShake
			if (!performHandshake()) { running = false; continue; }
			
			//Determine player action;
			long[] playerAction = this.playerIntention();
			switch ((int)playerAction[0]){
				case 0:
					this.assignPlayerToGame((playerAction[1] != 0), playerAction); //join game
					break;
				case 1:
					HLFServer.reconnectUser(true, playerAction[1], this.conn); //rejoin as P1
					break;
				case 2:
					HLFServer.reconnectUser(false, playerAction[1],  this.conn); //rejoin as P2
					break;
				case 3:
					break;
				default:
					HLFServer.log("Unknown response to: " + playerAction[0]);
			}
			
			closeAll(); //Reached the end of the steps to connect/reconnect a player
		}
	}
	
	/*Funcion para hacer un "handshake" con el cliente, le envia unos datos y este debe de retornale
	 * otros concretos (en este caso los dos deben enviar una constante definida por el servidor.
	 * Si el cliente no envia el handshake es desconectado */
	protected boolean performHandshake(){
		try {
			//HANDSHAKE DEL CLIENTE
			HLFServer.log("Attempting handshake");
			String msg;
			
			if (!(msg = incomingData.readLine()).equals(HLFServer.HANDSHAKETEXT)) {
				HLFServer.log("wrong client handshake: " + msg); //canviar a enviar msg error al client
				conn.close();
			}
			//HANDSHAKE DEL SERVER
			sendMsg(HLFServer.HANDSHAKETEXT, this.outgoingData);
			HLFServer.log("Handshake completado..");
			return true;
		} catch(Exception e){
			System.out.println("Error con el handshake..." + e.getMessage());
		}
		return false;
	}
	
	private BoatCoordsSet getShipPositions(long[] userText){
		return getSetFromFormattedCoords(userText);
	}
	
	/*Funcion para asignar a un jugador su partida deseada (la primera libre o una con ID elegida).*/
	protected void assignPlayerToGame(boolean custom, long[] shipPos){
		
		long gameID = shipPos[1];
		HLFServer.log("asked to join game: " + gameID);
		
		GameHandlerThread assignedGame;
		BoatCoordsSet shipPositions = getShipPositions(shipPos);
		
		//Control errores en las posiciones de los barcos
		if (shipPositions == null || shipPositions.toString().equals("")) {
			HLFServer.log("Error, invalid ship positions.");
			return;
		}

		//Asignacion partida
		if (!custom) { //Asignalo a la siguiente partida publica si la hay, sino crea una nueva
			assignedGame = HLFServer.nextEmptyPublicGame();
			HLFServer.log("Assigning to a public game");
			if (assignedGame == null) {
				HLFServer.createNewPublicGame(conn, shipPositions);
				//HAY QUE ESPERAR AL P2
			} else {
				assignedGame.assignP2(conn, true, shipPositions);
			}
		} else {
			HLFServer.log("Assigning to custom game with ID: " + gameID);
			assignedGame = HLFServer.findCustomGame(gameID);
			if (assignedGame == null) {
				HLFServer.createNewCustomGame(gameID, conn, shipPositions);
				//HAY QUE ESPERAR AL P2
			} else {
				assignedGame.assignP2(conn, true, shipPositions);
			}
		}
	}
	
	public void closeAll() {
		this.running = false;
		this.conn = null;
		this.incomingData = null;
		this.outgoingData = null;
	}
	
	/*Funcion para retornar el primer numero de los datos de union al servidor
	 * de un jugador y la resta de numeros. (Normalmente el primer numero
	 * determina la intencion del jugador (unirse a partida, reconectarse...)
	 * y los demas numeros el gameID de la partida	 */
	protected long[] playerIntention(){
		String inText ="";
		long firstChar[] = new long[(2 + HLFServer.POSICIONESTOTALESBARCOS*2)];  
		firstChar[0] = -1;
		try {
			inText = incomingData.readLine();
			String[] userText = inText.trim().split(",");
			for (int i = 0; i < firstChar.length; i++) {
				firstChar[i] = Long.parseLong(userText[i]);
			}
		} catch(Exception e){
			HLFServer.log("Client sent a wrongly encoded game joining message " + inText); //canviar a enviar msg error al client
			closeAll();
			return firstChar;
		}
		return firstChar;
	}
	
	private static BoatCoordsSet getSetFromFormattedCoords(long[] positions) {
		if (positions == null) { return null; }
		BoatCoordsSet playerPosSet = new BoatCoordsSet();
		try {
			int[] tmpPos = new int[2];
			for (int i = 2; i < positions.length-1; i+=2) {
				tmpPos[0] = (int)(positions[i]);
				if (tmpPos[0] < 1 || tmpPos[0] > MainWindow.DIMX-1) { return null; } //Control para que no se usen coordenadas ilegales...
				tmpPos[1] = (int)(positions[i+1]);
				if (tmpPos[1] < 1 || tmpPos[1] > MainWindow.DIMY-1) { return null; }
				if(!playerPosSet.add(tmpPos)) {
					return null; //Error con las posiciones de los barcos, hay repetidas, no tendria que pasar. Trampas???
				}
			}
		}
		catch (Exception e) {
		}
		return playerPosSet;
	}


}
