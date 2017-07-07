package HundirLaFlota.network;

/*Clase que crea una conexion inicial con el cliente, comprueba que es un cliente legal y le deja unirse
 * a la partida deseada generada por HLFServer. */
public class GameAssigner extends ThreadedConnection{
	
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
	
	/*Funcion para asignar a un jugador su partida deseada (la primera libre o una con ID elegida).*/
	protected void assignPlayerToGame(boolean custom, long gameID){
		
		HLFServer.log("asked to join game: " + gameID);
		//Comprovar si el usuario esta ya conectado a otra partida????? hmmmmmmmmmm AÑADIR POR AQUI!

		GameHandlerThread assignedGame;
		
		if (!custom) { //Asignalo a la siguiente partida publica si la hay, sino crea una nueva
			assignedGame = HLFServer.nextEmptyPublicGame();
			HLFServer.log("Assigning to a public game");
			if (assignedGame == null) {
				HLFServer.createNewPublicGame(conn);
				//HAY QUE ESPERAR AL P2
			} else {
				assignedGame.assignP2(conn, true);
			}
		} else {
			HLFServer.log("Assigning to custom game with ID: " + gameID);
			assignedGame = HLFServer.findCustomGame(gameID);
			if (assignedGame == null) {
				HLFServer.createNewCustomGame(gameID, conn);
				//HAY QUE ESPERAR AL P2
			} else {
				assignedGame.assignP2(conn, true);
			}
		}
	}
	
	/*Funcion para retornar el primer numero de los datos de union al servidor
	 * de un jugador y la resta de numeros. (Normalmente el primer numero
	 * determina la intencion del jugador (unirse a partida, reconectarse...)
	 * y los demas numeros el gameID de la partida	 */
	protected long[] playerIntention(){
		String inText ="";
		long firstChar[] = new long[2];
		firstChar[0] = -1;
		try {
			inText = incomingData.readLine();
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
