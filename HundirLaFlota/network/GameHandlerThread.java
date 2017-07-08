package HundirLaFlota.network;

import java.net.Socket;

/*Clase Threaded (ejecucion independiente) que gestiona una partida de HLF y gestiona la conexion con dos
 * clientes (P1 y P2) a traves de dos objetos PlayerGameIntermediator que seran ellos los encargados de la conexion.
 * Gestiona tambien la logica de la partida, verificando comandos, dando turno, mirando condiciones de victoria, desconexiones... */
public class GameHandlerThread extends Thread{
	

	private PlayerGameIntermediator P1Listener;
	private PlayerGameIntermediator P2Listener;
	long gameID;
	int turn = 0;
	int actualPlayer;
	static String[] legalCommands = {"a","d/c","h","m","d/c","chat","r","R"};
	static String[] legalActions = {"a","d/c","chat"};
	static String[] legalResponses = {"h","m","d/c","chat"};
	boolean running = true;
	int waitingForRC = 0;
	
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
		HLFServer.log("Reconnexion completada");
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
			checkForDCPlayers(false);
			PlayerGameIntermediator.sendMsg("start", P2Listener.outgoingData);
			PlayerGameIntermediator.sendMsg("start", P1Listener.outgoingData);
			try { sleep(500);} catch(Exception e){}
			actualPlayer = 2; //Solo para la inicializacion
			turn = 0;
			cyclePlayers();
			while(running){
				try {
				sleep(500);
				} catch(Exception e){
					HLFServer.log("Error on the main thread. " + e.getMessage());
				}
			}
			HLFServer.log(thisGameID() + "Ending game.");
	}
	
	/*comprueba si el comando que el usuario quiere enviar es correcto dependiendo de si le toca atacar o 
	 * ser atacado. Si lo es se lo envia al otro jugador para que responda*/
	private int parseCommands(String command, boolean isPlayerTurn, int playerNum){
		if (command == null) { //El player se ha desconectado de manera involuntaria...
			checkForDCPlayers(false);
			return -1;
		}
		String[] commandData = command.trim().split(",");
		String[] allowedActions = (isPlayerTurn) ? legalActions : legalResponses;
		
		if (isLegalCommand(commandData[0], allowedActions)){	
			if (commandData[0].equals("d/c")) { // current player wants to leave the game...
				return 0;
			}
			else if (commandData[0].equals("chat")){
				if (P1Listener != null && P2Listener != null) { //Si el otro jugador no esta conectado no enviar chat
					if (P1Listener.isConnected() && P2Listener.isConnected()){
						return 1; //send, but not advance turn
					}
				}
				return -1; 
			}
			else {
				if (commandData[0].equals("h") || commandData[0].equals("m")){
					return 3;
				}
				return 2; //envia y avanza turno
			}
		}
		return -1; //No envies, comando ilegal este turno (h o m cuando atacas por ejemplo...
	}
	
	/*Funcion solo accesible por un solo thread (intermediario en este caso) que a partir del comando enviado por el 
	 * usuario gestiona la resupesta que se le dara, siendo en la mayor parte de los casos enviar el comando al otro 
	 * usuario a traves de su intermediario para que este responda y le envie el comando de respuesta a la partida */
	public synchronized void playerCommandInterrupt(String command, int fromPlayer) {
		int todo = parseCommands(command, (fromPlayer == actualPlayer), fromPlayer);
		HLFServer.log(thisGameID() + "received data from player " + fromPlayer + ": " + command +" , its code is: " + todo);
		//String[] commandParts;
		PlayerGameIntermediator mediator;
		switch (todo) {
		case 0: //d/c
			mediator = (fromPlayer == 1) ? P1Listener : P2Listener;
			disconnectPlayer(mediator);
			checkForDCPlayers(true);
			break;
		case 1: //chat
		case 2: //a (when you're allowed to use a, else you go to -1)
			mediator = (fromPlayer == 1) ? P2Listener : P1Listener;
			mediator.sendCommandToPlayer(command);
			break;
		case 3: //h ("                                             ")
			mediator = (fromPlayer == 1) ? P2Listener : P1Listener;
			mediator.sendCommandToPlayer(command);
			try{ sleep(500);} catch(Exception e){}
			cyclePlayers(); //We got an attack -> hit or miss cycle complete, next player can choose a position now...
			break;
		case 4: //m ("                                             ")

		default: //incorrect command, don't send
		}
	}

	
	/*Comprueba que el comando que ha introducido el usuario esta dentro de los legales*/
	private boolean isLegalCommand(String command, String[] acceptedCommands){
		for (int i = 0; i < acceptedCommands.length; i++){
			if (command.equals(acceptedCommands[i])){
				return true;
			}
		}
		return false;
	}
	
	/*Funcion para desconectar la conexion que mantiene el intermediario con el jugador
	 * (efectivamente desconectandolo de la partida) */
	private void disconnectPlayer(PlayerGameIntermediator thisPlayer){
		thisPlayer.sendCommandToPlayer("d/c");
		thisPlayer.closeAll();
		thisPlayer.resetBuffer();
		try{ sleep(500);} catch(Exception e){}
	}

	/*Pasa al siguiente jugador, avanza turno si ya se ha producido un 
	 * ciclo de atacar -> respuesta (agua o tocado) */
	private void cyclePlayers(){
		PlayerGameIntermediator mediator = P1Listener;
		if (actualPlayer >= 2) {
			actualPlayer = 1;
			mediator.sendCommandToPlayer("t");
			mediator =  P2Listener;
			mediator.sendCommandToPlayer("nt");
			turn++;
			HLFServer.log("Turn: " + turn + ", player 1 goes");
		} else { 
			mediator.sendCommandToPlayer("nt");
			mediator =  P2Listener;
			mediator.sendCommandToPlayer("t");
			actualPlayer++;
			HLFServer.log("Turn: " + turn + ", player 2 goes");
		}
	}

	/*Comprueba si hay algun usuario desconectado y si ha sido a proposito. Si no es deliberado se iran haciendo
	 * comprobaciones periodicas hasta la reconexion o hasta que el timer (en la GUI del jugador conectado) 
	 * se active, se envie el comando al intermediario y este active la funcion de interrupcion por timer
	 * (vamos que si ha sido deliberado asigna ganador, si no espera 60s y si no se ha reconectado asigna ganador (ESTO
	 * HAY QUE IMPLEMENTARLO), si los dos estan desconectados finaliza la partida	 */
	protected int checkForDCPlayers(boolean onPurpose){
		int playersDCed = whoDCed();
		waitingForRC = playersDCed;
		if (playersDCed > 0) { 
			if (playersDCed == 1) { HLFServer.log(thisGameID() + "C1 DISCONNECTED"); if (onPurpose) {gameEndCheck(1);}}//Nomes C1 desconnectat
			else if(playersDCed == 2) { HLFServer.log(thisGameID() + "C2 DISCONNECTED");  if (onPurpose) {gameEndCheck(2);} } //Nomes C2 desconnectat
			else { HLFServer.log(thisGameID() + "BOTH PLAYERS DISCONNECTED"); gameEndCheck(3);}//Cap                                                                   connectat.
			return playersDCed;
		}
		return 0;
	}
	
	/*Funcion que comprueba si hay jugadores desconectados mirando sus canales de entrada/salida*/
	private int whoDCed(){
		int dced = 0;
		boolean P1r,P2r;
		if (P1Listener == null) { P1r = false; } 
		else { P1r = P1Listener.isMyPlayerDC(); }
		if (P2Listener == null) { P2r = false; } 
		else { P2r = P2Listener.isMyPlayerDC(); }
		HLFServer.log(thisGameID() + "Checking DC status -> P1: " + P1r + ", " + "P2 " + P2r +".");
		if (!P1r) {dced += 1; }  //Envia primer el char per ready up
		if (!P2r) { dced += 2;}
		return dced;
	}
	
	/*Cierra el programa (thread), todas sus conexiones y su ejecucion */
	protected void closeAll(){
		running = false;
	}
	
	/*Funcion para obtener una descripcion de la partida actual*/
	public String toString() { 
		if (P1Listener != null && P2Listener != null){
			return (thisGameID() + " -> P1: " + P1Listener.myIP() + ", P2: " + P2Listener.myIP() + ", turn " + turn);
		}
		else {
			return (thisGameID() + " Problems detecting players.");
		}
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
		int dcer = checkForDCPlayers(false);
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
		else if (whoDCed == 3) { winner = 3; }
		assignWinner(winner,true); 
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

	/*Asigna el ganador de la partida, hay que expandirla para ganar por mas barcos destruidos...*/
	private void assignWinner(int winner, boolean dcWin){
		String msg =  (dcWin) ? "dcwin":"win";
		if (winner == 1 && P1Listener != null ) { PlayerGameIntermediator.sendMsg(msg, P1Listener.outgoingData); } 
		else if (winner == 2 && P2Listener != null) { PlayerGameIntermediator.sendMsg(msg, P2Listener.outgoingData); }  //Si es diferente no habra winner (los dos se desconectan wtf... o cambiar
		if (winner == 1 || winner == 2) { HLFServer.log(thisGameID() + "Winner is player: " + winner); }
		else { HLFServer.log(thisGameID() + "Both players quit!!!"); }
		if (P1Listener != null) {
			disconnectPlayer(P1Listener);
			P1Listener = null;
		}
		if (P2Listener != null) {
			disconnectPlayer(P2Listener);
			P2Listener = null;
		}
		this.closeAll();
		HLFServer.finishGame(this.gameID);
	}


}
