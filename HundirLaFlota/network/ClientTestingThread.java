package HundirLaFlota.network;

/*Clase per testejar comandos de client en el hundir la flota, per ara dos comandos:
 * pedir bombardejar una posicio (4,4) y desconectarte (nomes et desconecta de debo
 * si es el teu torn...*/
public class ClientTestingThread extends DiagnosticThread{

	private ClientConnection client;
	private boolean exit = false;
	
	public ClientTestingThread(ClientConnection client){
		this.client = client;
	}
	
	public void run(){
		mainLoop();
	}
	
	/*Funcion solo para escribir el menu en la consola*/
	public static void writeMenu(){
		System.out.println("Menu del sistema");
		System.out.println("----------------");
		System.out.println("OPCIONES:");
		System.out.println("1.Atacar posicion.");
		System.out.println("1.Pedir desconexion.");
		System.out.println("5.Salir.");
	}
	
	public void mainLoop(){
		int userElection;
		while (!exit){
			writeMenu();
			userElection = DiagnosticThread.pideEntero("Elige la opcion que quieras ejecutar");
			switch(userElection){
			case 1:
				ClientConnection.sendMsg("a,4,4", client.outgoingDataP1);
				break;
			case 2:
				ClientConnection.sendMsg("d/c", client.outgoingDataP1);
				if (client.myTurn()){
					client.closeAll();
				}
				break;
			case 5:
				exit = true;
				System.out.println("Adios...");
				break;
			default:
				System.out.println("Opcion incorrecta.");
			}
		}
		sc.close();
	}
	
	public void kill(){
		exit = true;
	}
	
	public void executeAction(int tipo){
	}
}
