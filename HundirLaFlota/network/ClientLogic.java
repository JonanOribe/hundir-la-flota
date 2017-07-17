package HundirLaFlota.network;

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
				contenedor.setJugadorDC(false);
				contenedor.jugadorLabel.setText("Jug. " + client.getPlayerNum());
			}
			else if (commands[0].equals("chat")) { //Win by default, other client d/ced...
				int otherPlayerNum = (client.getPlayerNum() == 1) ? 2:1;
				contenedor.writeInChat("Jugador " + otherPlayerNum + ": " + command.substring(5));
			}
			else if (commands[0].equals("t")){
				contenedor.writeInChat("Es tu turno.");
				contenedor.jugadorLabel.setText("Jug. " + client.getPlayerNum() +" (atacante)");
			}
			else if (commands[0].equals("nt")){
				contenedor.writeInChat("Turno del oponente.");
				contenedor.jugadorLabel.setText("Jug. " + client.getPlayerNum() + " (defensor)");
			}
			else if (commands[0].equals("d/c") || commands[0].equals("timeout")){
				String msg = (commands[0].equals("d/c")) ? "Desconectando..." : "Desconectado por inactividad...";
				contenedor.writeInChat(msg);
				client.closeAll();
				client.stopRunning();
				jugadorSeDesconecta(true, false, contenedor);
			}
			else if (commands[0].equals("a")) {
				//Primero comprueba si la posicion en la grid es barco o agua (y la misma funcion isAHit cambiara los graficos
				//Y luego escribe en el chat y envia el mensaje correspondiente al servidor de si ha sido agua o tocado
				String HorM = (contenedor.enemyAttacksPos(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]))) ? "h":"m";
				contenedor.writeInChat("El oponente ataca la posicion -> " + ((char)('A' + Integer.parseInt(commands[1]) - 1)) + "," + commands[2]);
				ThreadedConnection.sendMsg((HorM + "," + commands[1] + "," + commands[2]), client.outgoingData); //Aleatori per ara, en el futur comprovacions....
				if (client.getHalfTurn()) {
					contenedor.setTurno(contenedor.getTurno() + 1);
					contenedor.turnosLabel.setText("TURNO: " + contenedor.getTurno());
					client.setHalfTurn(!client.getHalfTurn());
				} else {
					client.setHalfTurn(!client.getHalfTurn());
				}
			} 
			else if (commands[0].equals("h") || commands[0].equals("m")) {
				int puntos;
				String result;
				boolean acierto;
				if (commands[0].equals("h")) {
					puntos = 1000;
					result = "Tocado!"; 
					acierto = true;
				}else {
					puntos = 100;
					result = "Agua!"; 
					acierto = false;
				}
				contenedor.writeInChat("Resultados de mi ataque en ("+((char)('A' + Integer.parseInt(commands[1]) - 1)) + "," + commands[2] + ") -> " + result);
				//Dependiendo de si era agua o tocado cambiara los graficos en la label correspondiente del grid superior (donde estan los barcos enemigos)
				contenedor.drawMyAttackResults(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]), (commands[0].equals("h")));
				if (acierto) { contenedor.setAciertos(contenedor.getAciertos() + 1); 
				} else { contenedor.setAguas(contenedor.getAguas() +1); }
				client.getContenedor().setPuntos(client.getContenedor().getPuntos() + puntos);
				contenedor.aciertosAguasLabel.setText("ACIERTOS: " + contenedor.getAciertos() + "/" + contenedor.getAguas());
				contenedor.puntosLabel.setText("PUNTOS: " + contenedor.getPuntos());
				if (client.getHalfTurn()){
					contenedor.setTurno(contenedor.getTurno() + 1);
					contenedor.turnosLabel.setText("TURNO: " + contenedor.getTurno());
					client.setHalfTurn(!client.getHalfTurn());
				} else {
					client.setHalfTurn(!client.getHalfTurn());
				}
			}
			else if (commands[0].equals("dcwin")) { //Win by default, other client d/ced...
				contenedor.writeInChat("El oponente se desconecto. Has ganado!");
				jugadorSeDesconecta(true, true, contenedor);
				client.stopRunning();
			}
			else if (commands[0].equals("win")){
				contenedor.writeInChat("Has hundido la flota de tu oponente. Has ganado. Felicidades!");
				jugadorSeDesconecta(true, true, contenedor);
				client.stopRunning();
			}
			else if (commands[0].equals("lose")){
				contenedor.writeInChat("Tu oponente ha hundido todos tus barcos. Has perdido!");
				jugadorSeDesconecta(true, true, contenedor);
				client.stopRunning();
			}
		} catch(Exception e){
			client.getContenedor().writeInChat("Error en el formato de los datos enviados " + e.getMessage());
		}
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
	

}
