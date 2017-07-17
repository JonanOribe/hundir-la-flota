package HundirLaFlota.network;

import java.net.Socket;

/*Clase Threaded (ejecucion independiente) que gestiona una partida de HLF y gestiona la conexion con dos
 * clientes (P1 y P2) a traves de dos objetos PlayerGameIntermediator que seran ellos los encargados de la conexion.
 * Gestiona tambien la logica de la partida, verificando comandos, dando turno, mirando condiciones de victoria, desconexiones... */
public class GameHandlerThread extends Thread{
	

    private PlayerGameIntermediator P1Listener;
	private PlayerGameIntermediator P2Listener;
	
	private long gameID;
	private int turn = 0;
	private int actualPlayer;
	private int player1Hits = 0;
	private int player2Hits = 0;
	private int waitingForRC = 0;
	boolean player1Acted = true;
	boolean player2Acted = true;
	volatile boolean running = true;

	public GameHandlerThread(long gameID, Socket P1Conn){ 
		P1Listener = new PlayerGameIntermediator(this, 1,P1Conn);
		P1Listener.start();
		PlayerGameIntermediator.sendMsg(Long.toString(gameID)+",1", P1Listener.outgoingData); //Anyadir un token perk es pugui reconnectar?
		this.gameID = gameID;
	}
	
	/*No testeada, funcion para reconectar un usuario a esta partida, lo reconecta a su intermediario*/
	public void reconnectUser(boolean P1, Socket newConn){
		if (P1) { P1Listener.openConnection(newConn); }
		else { P2Listener.openConnection(newConn); }
		waitingForRC = 0;
		HLFServer.log("Reconexion completada");
	}
	
	/*Bucle de ejecucion, la partida conecta una vez dos jugadores esten conectados. Comprueba
	 * que los dos estan conectados de verdad y comienza el ciclo de turnos y jugador atacante/defensor
	 * (el primer turno P1 es atacante y P2 defensor siempre, P1 y P2 dependen del orden de conexion)
	 * La logica se gestiona a traves de las llamadas que el intermediario hace al programa (cuando le
	 * llegan datos de un cliente) y el programa actuara en consecuencia llamando a otra funcion. Por eso
	 * y como el contador de tiempo lo llevara la GUI (que enviara los datos al intermediario y este avisara
	 * a esta clase) la unica funcion de este bucle es inicializacion de partida, espera infinita y reaccion
	 * mediante interrupciones y finalmente finalizacion cuando las interrupciones hayan llevado a ello
	 */
	public void run() {
			HLFServer.log("Starting game - notifying clients");
			GameLogic.checkForDCPlayers(false, false, this);
			PlayerGameIntermediator.sendMsg("start", P2Listener.outgoingData);
			PlayerGameIntermediator.sendMsg("start", P1Listener.outgoingData);
			try { sleep(500);} catch(Exception e){}
			actualPlayer = 2; //Solo para la inicializacion
			turn = 0;
			GameLogic.cyclePlayers(this);
			while(running){
				try {
				sleep(500);
				} catch(Exception e){
					HLFServer.log("Error on the main thread. " + e.getMessage());
				}
			}
			HLFServer.log(thisGameID() + "Ending game.");
	}
	
	
	/*Funcion solo accesible por un solo thread (intermediario en este caso) que a partir del comando enviado por el 
	 * usuario gestiona la resupesta que se le dara, siendo en la mayor parte de los casos enviar el comando al otro 
	 * usuario a traves de su intermediario para que este responda y le envie el comando de respuesta a la partida */
	public synchronized void playerCommandInterrupt(String command, int fromPlayer) {
		int action = GameLogic.parsePlayerCommands(command, (fromPlayer == actualPlayer), fromPlayer, this);
		HLFServer.log(thisGameID() + "received data from player " + fromPlayer + ": " + command +" , its code is: " + action);
		if (fromPlayer == 1 && ( action > 1 || action < 0) ) { player1Acted = true; } //Si recibimos un comando que no sea chat de un usuario...
		if (fromPlayer == 2 && ( action > 1 || action < 0) ) { player2Acted = true; }
		PlayerGameIntermediator mediator;
		switch (action) {
		case 0: //d/c
			mediator = (fromPlayer == 1) ? P1Listener : P2Listener;
			disconnectPlayer(mediator,false);
			GameLogic.checkForDCPlayers(true, false, this);
			break;
		case 1: //chat
		case 2: //a (when you're allowed to use a, else you go to -1)
			mediator = (fromPlayer == 1) ? P2Listener : P1Listener;
			mediator.sendCommandToPlayer(command);
			break;
		case 3: //h / m       
			mediator = (fromPlayer == 1) ? P2Listener : P1Listener;
			mediator.sendCommandToPlayer(command);
			if (command.substring(0,1).equals("h")){
				if (fromPlayer == 2){ 
					this.player1Hits += 1;
					if (player1Hits >= HLFServer.TOTALSHIPPOSITIONS) {
						GameLogic.assignWinner(1,false, this);
					}
				} else { 
					this.player2Hits += 1; 
					if (player2Hits >= HLFServer.TOTALSHIPPOSITIONS) {
						GameLogic.assignWinner(2,false, this);
					}
				}
			}
			try{ sleep(500);} catch(Exception e){}
			GameLogic.cyclePlayers(this); //We got an attack -> hit or miss cycle complete, next player can choose a position now...
			break;
		case 4: //m ("                                             ")

		default: //incorrect command, don't send
		}
	}
	
	/*Funcion para desconectar la conexion que mantiene el intermediario con el jugador
	 * (efectivamente desconectandolo de la partida) */
	void disconnectPlayer(PlayerGameIntermediator thisPlayer, boolean timedOut){
		String msg = (timedOut) ? "timeout" : "d/c";
		thisPlayer.sendCommandToPlayer(msg);
		try{ sleep(500);} catch(Exception e){}
		thisPlayer.closeAll();
		thisPlayer.resetBuffer();
	}

	
	/*Cierra el programa (thread), todas sus conexiones y su ejecucion */
	protected void closeAll(){
		running = false;
	}
	
	/*Funcion para obtener una descripcion de la partida actual*/
	public String toString() { 
		String P1Text = "no conectado";
		String P2Text = "no conectado";
		if (P1Listener != null){
			P1Text = P1Listener.myIP();
		}
		if (P2Listener != null) {
			P2Text = P2Listener.myIP();
		}
		return (thisGameID() + " -> P1: " + P1Text + ", P2: " + P2Text + ", turn " + turn);
	}
	
	public String thisGameID(){
		return ("-- Game with ID: " + gameID + ": ");
	}
	
	/*Funcion para asignar a un jugador el gameID de esta partida para que se pueda reconectar*/
	public void assignGameIDToPlayer(long gameID, int playerNumber){
		PlayerGameIntermediator player = (playerNumber == 1) ? P1Listener : P2Listener;
		PlayerGameIntermediator.sendMsg(Long.toString(gameID), player.outgoingData);
	}

	/*Creo que hay problemas con los timer + threads, por lo que el timer se ejecutara en el
	 * cliente y llamara a esta funcion...	 */
	public void timerCheck() {
		HLFServer.log(thisGameID() + "Timer fired...");
		GameLogic.checkForDCPlayers(false, true, this);
	}
	
	/*Asigna un cliente al jugador 2, abre sus conexiones y comienza la partida si es necesario*/
	protected void assignP2(Socket P2Conn, boolean start){
		P2Listener = new PlayerGameIntermediator(this, 2, P2Conn);
		P2Listener.start();
		PlayerGameIntermediator.sendMsg(Long.toString(gameID)+",2", P2Listener.outgoingData); //Anyadir un token perk es pugui reconnectar?
		if (start){
			this.start();
			try{ sleep(500);} catch(Exception e){}
		}
	}
	
	public boolean hasP2(){
		boolean hasP2 = (P2Listener == null) ? false : true;
		return hasP2;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public PlayerGameIntermediator getP1Listener() {
		return P1Listener;
	}

	public PlayerGameIntermediator getP2Listener() {
		return P2Listener;
	}
	
	public void setP1Listener(PlayerGameIntermediator newListener) {
		this.P1Listener = newListener;
	}

	public void setP2Listener(PlayerGameIntermediator newListener) {
		this.P2Listener = newListener;
	}
	
	public int getActualPlayer() {
		return actualPlayer;
	}

	public void setActualPlayer(int actualPlayer) {
		this.actualPlayer = actualPlayer;
	}
	
	public long getGameID() {
		return gameID;
	}	
	
	public void setWaitingForRC(int waitingForRC) {
		this.waitingForRC = waitingForRC;
	}
	
	public int getWaitingForRC() {
		return waitingForRC;
	}

}
