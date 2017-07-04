package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/*Clase Threaded (ejecucion independiente) que gestiona una partida de HLF y gestiona la conexion con dos
 * clientes (P1 y P2) y la logica de la partida, dando turno y verificando condiciones de victoria, desconexiones... */
public class GameHandlerThread extends ThreadedConnection{
	

	protected Socket P2Conn;
	protected BufferedReader incomingDataP2;
	protected PrintWriter outgoingDataP2;
	long gameID;
	int turn = 1;
	int actualPlayer = 1;
	String lastActionCommand = "";
	String lastResponseCommand = "";
	private static String[] legalActions = {"a","d/c"};
	private static String[] legalResponses = {"h","m","d/c"};
	boolean running = true;
	
	public GameHandlerThread(long gameID, Socket P1Conn){ 
		openConnection(P1Conn);
		sendMsg(Long.toString(gameID)+",1", outgoingDataP1); //Anyadir un token perk es pugui reconnectar?
		this.gameID = gameID;
	}
	
	/*Implementar... hacer checkeo con primera parte de la antigua conexion (mantener una string con eso?)
	 * comprobar gameID y tal que el usuario deberia guardar*/
	public void reconnectUser(boolean P1, Socket newConn){
		if (P1) { openConnection(newConn); }
		else { assignP2(newConn, false); }
		HLFServer.log("Reconnexion completada");
	}
	
	//Comienza cuando hay las dos conexiones...
	public void run() {
			HLFServer.log("Starting game - notifying clients");
			
			//Eliminado timer porque creo que cosas de swing + threads hay conflictos (o no? nose), el timer
			//Estara en la GUI i llamara a la funcion que toque para comprobar DCs (isAnyoneDC si en x segundos
			//el cliente no responde... A lo mejor si creamos clase auxiliar solo para el timer rula (probar)
			
			isAnyoneDC(false);
			while(running){
				sendReceiveCycle();
				sendReceiveCycle();
				turn++;
			}
			
	}
	
	/*Funcion que contiene la logica del programa, es un ciclo P1 -> envia datos a este programa, este los envia al P2 que mira
	 * si habia barco o no y lo representa en pantalla, este resultado es enviado de vuelta a este programa y 
	 * finalmente el programa reenvia el resultado al atacante para que lo represente en pantalla...
	 * Dos ataques/respuestas (uno por cada jugador) hacen un turno, seguramente habra que hacerla mas modular
	 * y con mejor control de errores... */
	protected void sendReceiveCycle(){
		boolean P1Turn = (actualPlayer == 1) ? sendMsg("t", outgoingDataP1) : sendMsg("t", outgoingDataP2);
		P1Turn = (actualPlayer == 1) ? sendMsg("nt", outgoingDataP2) : sendMsg("nt", outgoingDataP1);
		P1Turn = (actualPlayer == 1); //Lo otro daria siempre true por la funcion sendMsg
		//Timer aqui tambe
		boolean cycleCompleted = false;
		String actionCommand;
		String responseCommand;
		String[] attackedPos;
		this.lastActionCommand = ""; //Esto es por si el bucle se interrumpe para mantener los comandos ya obtenidos correctamente por el usuario cuando el bucle vuelva aunque creo que no es necesario, veremos...
		this.lastResponseCommand = "";
		while(!cycleCompleted){
			try {
				HLFServer.log("Entering the cycle, player " + actualPlayer + " goes , actual turn: " + turn);
				
				if (this.lastActionCommand.equals("")){
					actionCommand = askDataFromPlayer(P1Turn, legalActions);
					if (actionCommand.equals("d/c")) { isAnyoneDC(true); break;}
					this.lastActionCommand = actionCommand; 
				}
				else {
					actionCommand = this.lastActionCommand;
				}
				
				HLFServer.log(thisGameID() +  "player " + (actualPlayer) + " sent command: " + actionCommand + ", last one: " + lastActionCommand);
				
				attackedPos = actionCommand.trim().split(",");
				
				//Sending the action to P2/P1
				if (this.lastResponseCommand.equals("")){
					if (P1Turn) { sendMsg(actionCommand, outgoingDataP2);  }
					else { sendMsg(actionCommand, outgoingDataP1); }

					HLFServer.log(thisGameID() + "sending " + actionCommand +" to other player, awaiting response");
					
					
					//Obtaining the response to the action from P2/P1
					responseCommand = askDataFromPlayer(!P1Turn, legalResponses);
					if (responseCommand.equals("d/c")) { isAnyoneDC(true); break;}
					this.lastResponseCommand = responseCommand;
					HLFServer.log(thisGameID() + "Obtained " + responseCommand +" from other player, sending to player " + actualPlayer);
				}
				else {
					responseCommand = this.lastResponseCommand;
				}
	
				//Sending that response to P1/P2 so they can update their gamestate, itll be their turn now
				if (attackedPos.length > 1) { //Hi ha posicio...
					responseCommand = responseCommand + "," + attackedPos[1] + "," + attackedPos[2]; 
				}
				if (P1Turn) { sendMsg(responseCommand, outgoingDataP1);}
				else {sendMsg(responseCommand, outgoingDataP2);}
			
				HLFServer.log(thisGameID() + "Final data sent to player " + actualPlayer + " (" + responseCommand +")");

				this.lastActionCommand = "";
				this.lastResponseCommand = "";
				cycleCompleted = true;
								
				nextPlayerCycle();
				

			}
			catch (Exception e) {
				HLFServer.log("Error in the main turn cycle. " + e.getMessage());
				if(isAnyoneDC(false) == 0){
					continue; //Si el error no viene por una desconexion reintentar
				}
				if (!running) { cycleCompleted = true; break; }
			}
		}
	}
	
	/*Funcion para obtener datos de un jugador dentro de los aceptados, como solo puede ser
	 * P1 o P2 he usado un booleano, sino poner el BufferedReader como parametro	 */
	private String askDataFromPlayer(boolean isP1, String[] legalCommands){
		String playerAction ="";
		boolean correctInput = false;
		try{
			while (!correctInput){
				if (isP1) {  playerAction = incomingData(incomingDataP1); }   
				else { playerAction = incomingData(incomingDataP2); }
				if (isLegalCommand(playerAction, legalCommands)) {
					correctInput = true;
				}
			}
			return playerAction;
		}catch (Exception e){
			HLFServer.log(thisGameID() + "Client " + (actualPlayer) + " sent an unrecognizable command. " + e.getMessage());
			return null;
		}
	}
	
	/*Pasa al siguiente jugador*/
	private void nextPlayerCycle(){
		if (actualPlayer >= 2) {
			actualPlayer = 1;
		} else { 
			actualPlayer++;
		}
	}
	
	/*Comprueba que el comando que ha introducido el usuario esta dentro de los legales*/
	private boolean isLegalCommand(String command, String[] acceptedCommands){
		String[] realCommand = command.trim().split(",");
		for (int i = 0; i < acceptedCommands.length; i++){
			if (realCommand[0].equals(acceptedCommands[i])){
				return true;
			}
		}
		return false;
	}
	
	/*Comprueba si hay algun usuario desconectado y si ha sido a proposito el otro/niguno si los dos estan
	 * desconectados gana la partida si ha sido deliberado. Si no es deliberado la funcion que asigne ganador sera 
	 * la que llama el timer	 */
	protected int isAnyoneDC(boolean onPurpose){
		int playersDCed = checkForPlayersDC();
		if (playersDCed > 0) { 
			if (playersDCed == 1) { HLFServer.log(thisGameID() + "C1 DISCONNECTED"); if (onPurpose) {gameEndCheck(1);}}//Nomes C1 desconnectat
			else if(playersDCed == 2) { HLFServer.log(thisGameID() + "C2 DISCONNECTED");  if (onPurpose) {gameEndCheck(2);} } //Nomes C2 desconnectat
			else { HLFServer.log(thisGameID() + "BOTH PLAYERS DISCONNECTED"); gameEndCheck(3);}//Cap client connectat.
			return playersDCed;
		}
		return 0;
	}

	
	/*Funcion que comprueba si hay jugadores desconectados mirando sus canales de entrada/salida*/
	private int checkForPlayersDC(){
		int sendingError = 0, receivingError = 0;
		HLFServer.log("Checking for DCed players...");
		boolean P1s = sendMsg("R",outgoingDataP1);
		boolean P2s = sendMsg("R", outgoingDataP2);
		try { sleep(100); }catch(Exception e){}
		String P1r,P2r;
		P1r = incomingData(incomingDataP1);
		P2r = incomingData(incomingDataP2);
		HLFServer.log("Checking DC status -> S: " + P1s + ", " + P2s + ". R: " + P1r + ", " + P2r + ".");
		if (!P1s) {sendingError += 1; }  //Envia primer el char per ready up
		if (!P2s) { sendingError += 2;}
		if (P1r == null) { receivingError += 1;}
		if (P2r == null) { receivingError += 2;}
		if (receivingError > sendingError){ return receivingError;}
		else return sendingError;
	}
	
	/*Cierra el programa (thread), todas sus conexiones y su ejecucion */
	protected void closeAll(){
		super.closeAll();
		running = false;
		try {
			P2Conn.close();
		} catch (Exception e){
		}
	}
	
	/*Funcion para obtener una descripcion de la partida actual*/
	public String toString() { 
		if (P2Conn != null) {
			return (thisGameID() + " -> P1: " + P1Conn.getRemoteSocketAddress().toString() + ", P2: " + (P2Conn.getRemoteSocketAddress().toString()) + ", turn " + turn);
		} else {
			return (thisGameID() + " -> P1: " + P1Conn.getRemoteSocketAddress().toString() + ", P2: not connected" + ", turn " + turn);
		}
	}
	
	public String thisGameID(){
		return ("-- Game with ID: " + gameID + ": ");
	}
	
	/*Funcion para asignar a un jugador el gameID de esta partida para que se pueda reconectar*/
	public void assignGameIDToPlayer(long gameID){
		sendMsg(Long.toString(gameID), outgoingDataP1);
	}

	/*Creo que hay problemas con los timer + threads, por lo que el timer se ejecutara en el
	 * cliente y llamara a esta funcion...
	 */
	public void timerCheck() {
		HLFServer.log(thisGameID() + "Timer fired...");
		int dcer = isAnyoneDC(false);
		if (dcer > 0){
			//Han pasado (timerdelay) segundos y hay alguien desconectado... 
			gameEndCheck(dcer);
		}
	}
	
	/*Hay que expandir esta funcion para anyadir ganar por puntos, asigna ganadores y termina la ejecucion del programa..*/
	private void gameEndCheck(int whoDCed){
		int winner = 0;
		if (whoDCed == 1) { winner = 2; }
		else if (whoDCed == 2) { winner = 1;}
		assignWinner(winner,true); 
		this.closeAll();
		HLFServer.finishGame(this.gameID);
	}
	
	/*Asigna un cliente al jugador 2, abre sus conexiones y comienza la partida si es necesario*/
	public void assignP2(Socket P2Conn, boolean start){
		this.P2Conn = P2Conn;
		try {			
			this.incomingDataP2 = new BufferedReader(new InputStreamReader(P2Conn.getInputStream()));
			this.outgoingDataP2 = new PrintWriter(new OutputStreamWriter(P2Conn.getOutputStream()));
			sendMsg(Long.toString(gameID)+",2", outgoingDataP2); //Anyadir un token perk es pugui reconnectar?
		} catch (Exception e){
			System.out.println("Problemas conectandose con el segundo cliente, " + e.getMessage());
		}
		if (start){
			this.start();
		}
	}
	
	public boolean hasP2(){
		boolean hasP2 = (P2Conn == null) ? false : true;
		return hasP2;
	}
	
	/*Asigna el ganador de la partida, hay que expandirla para ganar por mas barcos destruidos...*/
	private boolean assignWinner(int winner, boolean dcWin){
		String msg =  (dcWin) ? "dcwin":"win";
		if (winner == 1) { sendMsg(msg, outgoingDataP1); } 
		else if (winner == 2) { sendMsg(msg, outgoingDataP2); }  //Si es diferente no habra winner (los dos se desconectan wtf... o cambiar
		HLFServer.log(thisGameID() + "Winner is player: " + winner);
		this.closeAll();
		HLFServer.finishGame(this.gameID);
		return true;
	}


}
