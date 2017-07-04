package HundirLaFlota.network;

public class GameAssigner extends ThreadedConnection{

	protected boolean performHandshake(){
		try {
			//HANDSHAKE DEL CLIENTE
			HLFServer.log("Attempting handshake");
			String msg;
			
			if (!(msg = incomingDataP1.readLine()).equals(HLFServer.HANDSHAKETEXT)) {
				HLFServer.log("wrong client handshake: " + msg); //canviar a enviar msg error al client
				P1Conn.close();
			}
		
			//HANDSHAKE DEL SERVER
			sendMsg(HLFServer.HANDSHAKETEXT, this.outgoingDataP1);
			HLFServer.log("Handshake completado..");
			return true;
		} catch(Exception e){
			System.out.println("Error con el handshake..." + e.getMessage());
		}
		return false;
	}
	
	protected void assignPlayerToGame(boolean custom, long gameID){
		
		HLFServer.log("asked to join game: " + gameID);
		//Comprovar si el usuario esta ya conectado a otra partida????? hmmmmmmmmmm AÑADIR POR AQUI!

		GameHandlerThread assignedGame;
		
		if (!custom) { //Asignalo a la siguiente partida publica si la hay, sino crea una nueva
			assignedGame = HLFServer.nextEmptyPublicGame();
			HLFServer.log("Assigning to a public game");
			if (assignedGame == null) {
				HLFServer.createNewPublicGame(P1Conn);
				//HAY QUE ESPERAR AL P2
			} else {
				assignedGame.assignP2(P1Conn, true);
			}
		} else {
			HLFServer.log("Assigning to custom game with ID: " + gameID);
			assignedGame = HLFServer.findCustomGame(gameID);
			if (assignedGame == null) {
				HLFServer.createNewCustomGame(gameID, P1Conn);
				//HAY QUE ESPERAR AL P2
			} else {
				assignedGame.assignP2(P1Conn, true);
			}
		}
	}
	
	protected long[] playerIntention(){
		String inText ="";
		long firstChar[] = new long[2];
		firstChar[0] = -1;
		try {
			inText = incomingDataP1.readLine();
			firstChar[0] = Long.parseLong(inText.substring(0, 1));
			firstChar[1] = Long.parseLong(inText.substring(1));
		} catch(Exception e){
			HLFServer.log("Client sent a wrongly encoded game joining message " + inText); //canviar a enviar msg error al client
			closeAll();
			return firstChar;
		}
		return firstChar;
	}
	


}
