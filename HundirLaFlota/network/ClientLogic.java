package HundirLaFlota.network;

import HundirLaFlota.gui.LabelGridCombate;
import HundirLaFlota.gui.MainWindow;
import HundirLaFlota.gui.PanelCombate;

/*Funciones que regulan la logica del cliente, en este caso comprueba los datos que
 * le llegan del servidor a traves del intermediario y responde a los comandos
 */
public class ClientLogic {
	
	/*Actua en base a los comandos recibidos*/
	public static void parseClientData(String command, ClientConnector client){
		try {
			//System.out.println("--Client: received command on stream " + command);
			String[] commands = command.trim().split(",");
			PanelCombate contenedor = client.getContenedor();
			if (commands[0].equals("R")){
				//contenedor.writeInChat("Connection check");
				ThreadedConnection.sendMsg("r", client.outgoingData);
			}
			if (commands[0].equals("start")){
				contenedor.writeInChat("Dos jugadores conectados. Comenzando la partida!");
				contenedor.setGameStarted(true);
				contenedor.jugadorLabel.setText("Jug. " + client.getPlayerNum());
			}
			else if (commands[0].equals("chat")) { //Win by default, other client d/ced...
				int otherPlayerNum = (client.getPlayerNum() == 1) ? 2:1;
				contenedor.writeInChat("Jugador " + otherPlayerNum + ": " + command.substring(5));
			}
			else if (commands[0].equals("t")){
				contenedor.writeInChat("Es tu turno.");
				contenedor.setMyTurn(true);
				contenedor.jugadorLabel.setText("Jug. " + client.getPlayerNum() +" (atacante)");
			}
			else if (commands[0].equals("nt")){
				contenedor.writeInChat("Turno del oponente.");
				contenedor.setMyTurn(false);
				contenedor.jugadorLabel.setText("Jug. " + client.getPlayerNum() + " (defensor)");
			}
			else if (commands[0].equals("d/c") || commands[0].equals("timeout")){
				String msg = (commands[0].equals("d/c")) ? "Desconectando..." : "Desconectado por inactividad...";
				contenedor.writeInChat(msg);
				contenedor.resetTimer();
				client.closeAll();
				client.stopRunning();
				jugadorSeDesconecta(true, true, contenedor);
			}
			else if (commands[0].equals("a")) {
				int[] hitPos = new int[2]; 
				hitPos[0] = Integer.parseInt(commands[1]);
				hitPos[1] = Integer.parseInt(commands[2]);					
				String HorM = (contenedor.enemyAttacksPos(hitPos[0], hitPos[1])) ? "h":"m"; 
				if (HorM.equals("h")) {
					LabelGridCombate[][] positions = contenedor.getGridCoordsBot();
					int IDBarco = positions[hitPos[0]-1][hitPos[1]-1].getBarcoID();
					hitPos[0] -= 1; //Esto se hace asi debido a que la correspondencia con la grid no es directa (la grid tiene las posiciones con letras/numeros por lo que hay que restarle uno al "valor real"
					hitPos[1] -= 1;
					boolean tyh = checkTocadoYHundido(hitPos, IDBarco, positions, hitPos);
					if (tyh) HorM = "hu";
				}
				contenedor.writeInChat("El oponente ataca la posicion -> " + ((char)('A' + hitPos[0])) + "," + (hitPos[1]+1));
				ThreadedConnection.sendMsg((HorM + "," + commands[1] + "," + commands[2]), client.outgoingData); 
				if (client.getHalfTurn()) {
					contenedor.setTurno(contenedor.getTurno() + 1);
					contenedor.turnosLabel.setText("TURNO: " + contenedor.getTurno());
					client.setHalfTurn(!client.getHalfTurn());
				} else {
					client.setHalfTurn(!client.getHalfTurn());
				}
				contenedor.resetTimer();
			} 
			else if (commands[0].equals("h") || commands[0].equals("m") || commands[0].equals("hu")) {
				int incrementoPuntos;
				int[] hitPos = new int[2]; 
				hitPos[0] = Integer.parseInt(commands[1])-1;
				hitPos[1] = Integer.parseInt(commands[2])-1;
				String result;
				boolean acierto;
				if (commands[0].equals("h")) {
					incrementoPuntos = 1000;
					result = "Tocado!"; 
					acierto = true;
				} else if (commands[0].equals("hu")) {
					incrementoPuntos = 2000;
					result = "Tocado y hundido!"; 
					acierto = true;
				}else {
					incrementoPuntos = 100;
					result = "Agua!"; 
					acierto = false;
				}
				contenedor.writeInChat("Resultados de mi ataque en ("+((char)('A' + hitPos[0])) + "," +  (hitPos[1]+1) + ") -> " + result);
				//Dependiendo de si era agua o tocado cambiara los graficos en la label correspondiente del grid superior (donde estan los barcos enemigos)
				contenedor.drawMyAttackResults(hitPos[0]+1, hitPos[1]+1, ( (commands[0].equals("h") || commands[0].equals("hu"))));
				if (acierto) { contenedor.setAciertos(contenedor.getAciertos() + 1); 
				} else { contenedor.setAguas(contenedor.getAguas() +1); }
				client.getContenedor().setPuntos(client.getContenedor().getPuntos() + incrementoPuntos);
				contenedor.aciertosAguasLabel.setText("ACIERTOS: " + contenedor.getAciertos() + "/" + contenedor.getAguas());
				contenedor.puntosLabel.setText("PUNTOS: " + contenedor.getPuntos());
				if (client.getHalfTurn()){
					contenedor.setTurno(contenedor.getTurno() + 1);
					contenedor.turnosLabel.setText("TURNO: " + contenedor.getTurno());
					client.setHalfTurn(!client.getHalfTurn());
				} else {
					client.setHalfTurn(!client.getHalfTurn());
				}
				contenedor.resetTimer();
			}
			else if (commands[0].equals("dcwin")) { //Win by default, other client d/ced...
				contenedor.writeInChat("El oponente se desconecto. Has ganado!");
				acabaPartida(contenedor, client);
			}
			else if (commands[0].equals("win")){
				contenedor.writeInChat("Has hundido la flota de tu oponente. Has ganado. Felicidades!");
				acabaPartida(contenedor, client);
			}
			else if (commands[0].equals("lose")){
				contenedor.writeInChat("Tu oponente ha hundido todos tus barcos. Has perdido!");
				acabaPartida(contenedor, client);
			}			
			else if (commands[0].equals("error")){
				contenedor.writeInChat("Servidor(error): " + commands[1]);
			}
		} catch(Exception e){
			client.getContenedor().writeInChat("Error en el formato de los datos enviados " + e.getMessage());
		}
	}	
	
	private static void acabaPartida(PanelCombate contenedor, ClientConnector client) {
		jugadorSeDesconecta(true, true, contenedor);
		contenedor.resetTimer();
		client.stopRunning();
	}
	
	/*Funcion que cambia el texto de los botones por si el jugador aprieta el boton de desconectar/salir
	 * o el de reconectar (si se desconecta -> salir (que cerrara el programa), si se reconecta -> desconectar)*/
	public static void jugadorSeDesconecta(boolean desconectando, boolean finPartida, PanelCombate contenedor){
		if (!finPartida){
			if (desconectando){
				contenedor.cambiaReconnectButton("Reconectar");
				contenedor.cambiaQuitButton("Salir");
			} else {
				contenedor.cambiaQuitButton("Desconectar");
				contenedor.cambiaReconnectButton(" ");
			}
		} else {
			contenedor.cambiaQuitButton("Volver a jugar");
			contenedor.cambiaReconnectButton("Volver al menu");
		}
		contenedor.setJugadorDC(true);
	}
	
	/*Puede que demasiado overhead (de parametros tiene una array2d de JLabels modificadas..., en cualquier caso maximo iteraciones
	 * sera el tamanyo del barco mas grande.
	 */
	private static boolean checkTocadoYHundido(int[] hitPosition, int IDBarco, LabelGridCombate[][] positions, int[] firstPosition) {
		boolean tyh;
		tyh = checkOneSide(hitPosition, false, positions, IDBarco, firstPosition);
		//System.out.println("Tyh -- First one passed");
		tyh = tyh && checkOneSide(hitPosition, true, positions, IDBarco, firstPosition);
		//System.out.println("Tyh -- Second one passed");
		return tyh;
	}
	
	private static boolean checkOneSide(int[] hitPosition, boolean verticalCheck, LabelGridCombate[][] positions, int IDBarcoOriginal, int[] firstPosition){ 
		LabelGridCombate testPos;
		int changeInPos;
		int[] foundDestroyedPart = null;
		for (int i = -1; i < 2; i+=2) {
			if (verticalCheck) {
				changeInPos = hitPosition[0]+i;
				if (changeInPos < 0 || changeInPos > (MainWindow.DIMX-2)) { //System.out.println("out of vertical bounds"); 
				continue; 
				}
				testPos = positions[ changeInPos ][ hitPosition[1] ]; 
			} else {  
				changeInPos = hitPosition[1]+i;
				if (changeInPos < 0 || changeInPos > (MainWindow.DIMY-2)) { //System.out.println("out of horizontal bounds");
					continue;
				}
				testPos = positions[ (hitPosition[0]) ][ changeInPos ]; 
			}
			if (testPos.isAgua()) { //Si es agua (o agua con tiro fallido) pasamos a comprobar la siguiente posicion
				continue;
			}
			if (!testPos.hasBeenShot()) { //Esto es solo si es tocado ya que hemos eliminado fallo en el tiro con esAgua()
				if (testPos.getBarcoID() == IDBarcoOriginal) { //Si el barco sin disparar es el nuestro salimos ya que querra decir que quedan trozos sin destruir
				return false;
				} else {
					continue; //Si no es el mismo barco (otro barco adyacente al nuestro) lo tratamos como agua, seguimos comprobando
				}
			}
			if (testPos.hasBeenShot() && testPos.getBarcoID() == IDBarcoOriginal) { //Si es nuestro barco y ha sido disparado...
				int[] thisPos = testPos.getCoords();
				thisPos[0] -= 1;
				thisPos[1] -= 1;
				//Comprobamos que no es la posicion de la que veniamos por la funcion recursiva, si lo es seguimos comprobando
				if (firstPosition[0] == thisPos[0] && firstPosition[1] == thisPos[1]) { 
					continue; 
				}
				foundDestroyedPart = thisPos; //Si no lo es guardamos la posicion pero seguiremos comprobando las demas posiciones por si hay alguna pieza no destruida...
				continue; 
			}
		}
		if (foundDestroyedPart != null) { //Si hemos encontrado una posicion disparada de nuestro barco y ninguna sin disparar ejecutamos la funcion para esa posicion con 
										  //La posicion actual como la posicion de la que veniamos
			return checkTocadoYHundido(foundDestroyedPart, IDBarcoOriginal, positions, hitPosition);
		}
		return true; //Si hemos comprobado todas las posiciones y todo son aguas o trozos de barco disaparados devolvemos verdadero
	}
		
		
}
