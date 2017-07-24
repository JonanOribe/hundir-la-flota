package HundirLaFlota.network;

import HundirLaFlota.gui.MainWindow;

/*Funciones que regulan la logica de la partida, desde las que pasan el turno a las que comprueban las acciones 
 * recibidas por el usuario, las que comprueban si hay usuarios desconectados y las que finalizan la partida
 */
public class GameLogic {
	
	static String[] legalCommands = {"a","d/c","h","hu","m","chat","r","R","start","error","timeout","win","dcwin","lose"};
	static String[] legalActions = {"a","d/c","chat"};
	static String[] legalResponses = {"h","hu","m","d/c","chat"};
	
	/*comprueba si el comando que el usuario quiere enviar es correcto dependiendo de si le toca atacar o 
	 * ser atacado. Si lo es se lo envia al otro jugador para que responda. Estos datos son usados
	 * por playerCommandInterrupt en GameHandlerThread*/
	public static int parsePlayerCommands(String command, boolean isPlayerTurn, int playerNum, GameHandlerThread game){
		if (command == null) { //El player se ha desconectado de manera involuntaria...
			checkForDCPlayers(false, false, game);
			return -2; //jugador DC pero esperando a que se reconecte...
		}
		String[] commandData = command.trim().split(",");
		String[] allowedActions = (isPlayerTurn) ? legalActions : legalResponses;
		
		if (isLegalCommand(commandData[0], allowedActions)){	
			if (commandData[0].equals("d/c")) { // current player wants to leave the game...
				return 0;
			}
			else if (commandData[0].equals("chat")){
				if (game.getP1Listener() != null && game.getP2Listener() != null) { //Si el otro jugador no esta conectado no enviar chat
					if (game.getP1Listener().isConnected() && game.getP2Listener().isConnected()){
						return 1; //send, but not advance turn
					}
				}
				return -1; 
			}
			else {
				//command to "a" or to h/m/hu
				//Error checking -- incorrect or badly formatted coordinates 
				int xPos,yPos;
				try {
					xPos = Integer.parseInt(commandData[1]);
					yPos = Integer.parseInt(commandData[2]);
					if (xPos < 1 || xPos > (MainWindow.DIMX-1) || yPos < 1 || yPos > (MainWindow.DIMY-1)) {
						HLFServer.log(game.thisGameID() + "OUT OF BOUNDS COORDS: " + xPos + ", " + yPos);
						return -1;
					}
				}catch (Exception e){
					HLFServer.log(game.thisGameID() + "ERROR PARSING COORDS: " + command);
					return -1;
				}	
				if (commandData[0].equals("h") || commandData[0].equals("m") || commandData[0].equals("hu")){
					return 3;
				}
				return 2; //envia y avanza turno
			}
		}
		return -1; //No envies, comando ilegal este turno (h o m cuando atacas por ejemplo...
	}
	
	/*Comprueba que el comando que ha introducido el usuario esta dentro de los legales*/
	private static boolean isLegalCommand(String command, String[] acceptedCommands){
		for (int i = 0; i < acceptedCommands.length; i++){
			if (command.equals(acceptedCommands[i])){
				return true;
			}
		}
		return false;
	}
	

	/*Pasa al siguiente jugador, avanza turno si ya se ha producido un 
	 * ciclo de atacar -> respuesta (agua o tocado) */
	public static void cyclePlayers(GameHandlerThread game){
		PlayerGameIntermediator mediator = game.getP1Listener();
		if (game.getActualPlayer() >= 2) {
			game.setActualPlayer(1);
			mediator.sendCommandToPlayer("t");
			mediator = game.getP2Listener();
			mediator.sendCommandToPlayer("nt");
			game.setTurn(game.getTurn()+1);
			HLFServer.log(game.thisGameID() +"Turn: " + game.getTurn() + ", player 1 goes");
		} else { 
			mediator.sendCommandToPlayer("nt");
			mediator = game.getP2Listener();
			mediator.sendCommandToPlayer("t");
			game.setActualPlayer(game.getActualPlayer()+1);
			HLFServer.log(game.thisGameID() +"Turn: " + game.getTurn() + ", player 2 goes");
		}
	}
	
	/*Comprueba si hay algun usuario desconectado y si ha sido a proposito. Si no es deliberado se iran haciendo
	 * comprobaciones periodicas hasta la reconexion o hasta que el timer (en la GUI del jugador conectado) 
	 * se active, se envie el comando al intermediario y este active la funcion de interrupcion por timer
	 * (vamos que si ha sido deliberado asigna ganador, si no espera 60s y si no se ha reconectado asigna ganador (ESTO
	 * HAY QUE IMPLEMENTARLO), si los dos estan desconectados finaliza la partida	 */
	protected synchronized static void checkForDCPlayers(boolean dcedOnPurpose, boolean reaperCheck, GameHandlerThread game){
		if (game == null) { return; }
		int playersDCed = whoDCed(game);
		game.setWaitingForRC(playersDCed);
		if (!game.running) { return; }
		//Comprovacion inactividad en enviar comandos, no en la conexion...
		int dcValue = 0;
		if (reaperCheck){
			if (playersDCed == 1 || playersDCed == 3) {
				if (game.player1Acted) {
					game.player1Acted = false;
					HLFServer.log(game.thisGameID() +"player 1 hasnt connected in a while");
				} else {
					game.disconnectPlayer(game.getP1Listener(), true);
					HLFServer.log(game.thisGameID() +"player 1 hasnt connected in too long. He loses");
					dcValue += 1;
				}
			}
			if (playersDCed == 2 || playersDCed == 3) {
				if (game.player2Acted) {
					game.player2Acted = false;
					HLFServer.log(game.thisGameID() +"player 2 hasnt connected in a while");
				} else {
					game.disconnectPlayer(game.getP2Listener(), true);
					HLFServer.log(game.thisGameID() +"player 2 hasnt connected in too long. He loses");
					dcValue += 2;
				}
			}
		}
		if (dcValue > 0){
			gameEndedInDC(dcValue, game);
		}
		
		if (playersDCed > 0) { //Si hay jugadores desconectados...
			if (playersDCed < 3) {
				if (dcedOnPurpose) {
					gameEndedInDC(playersDCed, game);
				} 
			}
			else { 
				HLFServer.log(game.thisGameID() + "BOTH PLAYERS DISCONNECTED"); gameEndedInDC(3, game); 
			}
		}

	}
	
	
	/*Funcion que comprueba si hay jugadores desconectados mirando sus canales de entrada/salida*/
	private static int whoDCed(GameHandlerThread game){
		int dced = 0;
		boolean P1r,P2r;
		if (game.getP1Listener() == null) { P1r = false; } 
		else { P1r = game.getP1Listener().isMyPlayerDC(); }
		if (game.getP2Listener() == null) { P2r = false; } 
		else { P2r = game.getP2Listener().isMyPlayerDC(); }
		HLFServer.log(game.thisGameID() + "Checking DC status -> P1: " + P1r + ", " + "P2 " + P2r +".");
		if (!P1r) {dced += 1; }  //Envia primer el char per ready up
		if (!P2r) { dced += 2;}
		return dced;
	}
	
	/*Asigna el ganador de la partida, ya sea por desconexion del oponente o por mas
	 * barcos destruidos*/
	public static void assignWinner(int winner, boolean dcWin, GameHandlerThread game){
		String msg =  (dcWin) ? "dcwin":"win";
		PlayerGameIntermediator winnerP = (winner == 1) ? game.getP1Listener() : game.getP2Listener();
		PlayerGameIntermediator loserP = (winner == 1) ? game.getP2Listener() : game.getP1Listener();
		if (winnerP != null) { 
			PlayerGameIntermediator.sendMsg(msg, winnerP.outgoingData);
		}
		if (loserP != null){
			PlayerGameIntermediator.sendMsg("lose", loserP.outgoingData);
		}
		if (winner == 1 || winner == 2) { HLFServer.log(game.thisGameID() + "WINNER IS PLAYER: " + winner); }
		else { HLFServer.log(game.thisGameID() + "Both players quit!!!"); }
		if (game.getP1Listener() != null) {
			game.disconnectPlayer(game.getP1Listener(),true);
			game.setP1Listener(null);
		}
		if (game.getP2Listener() != null) {
			game.disconnectPlayer(game.getP2Listener(),true);
			game.setP2Listener(null);
		}
		game.closeAll();
		HLFServer.finishGame(game.getGameID());
	}
	
	/*Esta funcion determina cuando uno o mas jugadores se han desconectado quien es el ganador
	 * en consecuencia*/
	private static void gameEndedInDC(int whoDCed, GameHandlerThread game){
		int winner = 0;
		if (whoDCed == 1) { winner = 2; }
		else if (whoDCed == 2) { winner = 1;}
		else if (whoDCed == 3) { winner = 3; }
		assignWinner(winner, true, game); 
	}
	


}
