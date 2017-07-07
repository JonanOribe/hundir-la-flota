package HundirLaFlota.network;

import java.net.Socket;

/*Clase threaded que hace de intermediario entre la partida (GameHandlerThread) y el cliente:
 * (tiene una conexion abierta con el cliente mientras que se ejecuta en el mismo
 * ordenador que la partida). Basicamente cuando le llegan datos del cliente se los 
 * transmite a la partida y esta le hace devolver otros datos al cliente dependiendo de lo que haya pasado. 
 * 
 * De forma grafica: 
 * 
 * cliente <- - -(conexion via internet) - - -> Intermediario(yo) <--(conexion directa, mismo ordenador)---> GameHandlerThread
 * 
 * Y la misma estructura seguira con el otro cliente al que GameHandlerThread esta conectado, dejando que se intercambien datos*/
public class PlayerGameIntermediator extends ThreadedConnection{
	
	private GameHandlerThread boss;
	private boolean running = true;
	private volatile String bufferedText = ""; //Volatile sera que no se fia de la cache, es decir siempre se comprueba el valor existente
	int myPlayerNum;
	
	
	public PlayerGameIntermediator(Socket conn){
		this.openConnection(conn);
	}
	
	/*Constructor para crear un intermediario, usa la partida para tener la referencia, el numero del player
	 * para los controles de logica de turnos (ya que no podemos acceder a las variables de este al estar
	 * en el otro lado de la conexion) y uno de los sockets de la conexion con el jugador*/
	public PlayerGameIntermediator(GameHandlerThread boss, int myPlayerNum, Socket P1Conn){
		this.openConnection(P1Conn);
		this.boss = boss;
		this.myPlayerNum = myPlayerNum;
	}
	
	/*Bucle principal de ejecucion, espera a que le lleguen datos del usuario por la conexion*/
	public void run(){
		String playerAction;
		while(running){
			playerAction = waitForLegalCommands();
			actOnClientCommands(playerAction);
		}
	}
	
	/*Funcion para detectar y guardar de forma temporal los datos obtenidos y que sean
	 * comandos legales de la partida. si hay un problema con la conexion del jugador 
	 * devolvera null lo que nos deja verificar el estado de la conexion. Guardara los 
	 * datos recibidos en una variable volatil para hacer comprobacion de d/c sin
	 * tener que pedir datos otra vez */
	private String waitForLegalCommands(){
		boolean correctInput = false;
		bufferedText = ""; 
		while (!correctInput && running){
			bufferedText = fetchData(incomingData);
			if (bufferedText == null) { //Esto se dara si se ha cortado la conexion...
				return null;
			}
			if (isLegalCommand(bufferedText, GameHandlerThread.legalCommands)) {
				correctInput = true;
			}
		}
		return bufferedText;
	}
	
	/*Comprueba que el comando enviado este dentro de la lista de comandos legales (solo comprueba la primera
	 * palabra del comando)*/
	private boolean isLegalCommand(String command, String[] acceptedCommands){
		try {
			String[] realCommand = command.trim().split(",");
			for (int i = 0; i < acceptedCommands.length; i++){
				if (realCommand[0].equals(acceptedCommands[i])){
					return true;
				}
			}
			return false;
		} catch (Exception e){
			//System.out.println("--Client: enviado comando ilegal. " + e.getMessage() );
			return false;
		}
	}
	
	/*Actua en base al comando recibido del cliente (90% de los casos se lo pasa al objeto
	 * GameHandlerThread para que este se lo envie al otro cliente via su Intermediator...)	 */
	private void actOnClientCommands(String clientInput){
		if (!running) { return;}
		if (clientInput == null) { communicateCommandToGame(null); return; }
		String[] commands = clientInput.trim().split(",");
		try {
			if (commands[0].equals("r")){
				System.out.println(this.myPlayer() + "Cleared the DC check.");
			}
			else {
				communicateCommandToGame(clientInput);
			}
		} catch(Exception e){
			System.out.println("Data packet isnt formatted properly " + e.getMessage());
		}
	}
	
	/*Le envia el comando al objeto GHT, solo uno de los dos intermediaros puede transmitir
	 * informacion a la vez...	 */
	private synchronized void communicateCommandToGame(String command) {
		boss.playerCommandInterrupt(command, this.myPlayerNum);
	}
	
	/*Le envia un mensaje al jugador del que es intermediario via la conexion*/
	public void sendCommandToPlayer(String command) {
		sendMsg(command, this.outgoingData);
		HLFServer.log(myPlayer() + "received " + command + " , sending to player");
	}
	
	/*Funcion para formatear los mensajes de debug*/
	private String myPlayer(){
		return (boss.thisGameID() + "--Player " + myPlayerNum + " mediator: ");
	}
	
	/*Funcion para saber si al usuario le toca atacar o recibir ataque en este tipo de juego
	 * (determinara que comandos son posibles)	 */
	public boolean isMyTurn(){
		if (boss.actualPlayer == this.myPlayerNum) {
			return true;
		}
		return false;
	}
	
	String myIP(){
		if (conn == null) { return "Not connected"; }
		return conn.getRemoteSocketAddress().toString();
	}
	
	/*Resetea la variable volatil que guarda los datos recibidos a null*/
	public void resetBuffer(){
		this.bufferedText = null;
	}
	
	/*Cierra las conexiones y para la ejecucion*/
	public void closeAll(){
		this.running = false;
		super.closeAll();
	}
	
	/*Comprueba si la conexion con el usuario esta abierta*/
	public boolean isMyPlayerDC(){
		boolean connected = true;
		boolean P1s = sendMsg("R",outgoingData); //Envia al cliente el comando para "ready up" (contestara immediatamente una vez le llega)
		try { sleep(500); }catch(Exception e){}
		String P1r = bufferedText;
		if (!P1s || P1r == null) {connected = false; } 
		return connected;
	}
	
}
