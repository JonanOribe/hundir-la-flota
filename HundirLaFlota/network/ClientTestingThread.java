package HundirLaFlota.network;

import java.util.Scanner;

/*Classe per testejar comandos de client en el hundir la flota, per ara tres comandos:
 * demanar bombardejar una posicio (4,4), enviar chat, i desconectarte */
public class ClientTestingThread extends Thread{

	private static Scanner sc = new Scanner(System.in);
	private ClientConnector PL;
	private boolean exit = false;
	private int userElection;
	
	public ClientTestingThread(ClientConnector PL){
		this.PL = PL;
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
		System.out.println("2.Enviar mensaje chat.");
		System.out.println("3.Pedir desconexion.");
		System.out.println("5.Salir.");
	}
	
	public void mainLoop(){
		while (!exit){
			try {
			writeMenu();
				userElection = DiagnosticThread.pideEntero("Elige la opcion que quieras ejecutar");
				switch(userElection){
					case 1:
						ThreadedConnection.sendMsg("a,4,4", PL.outgoingData);
						break;
					case 2:
						ThreadedConnection.sendMsg("chat,loloara alo fuck you you piece of shit", PL.outgoingData);
						break;
					case 3:
						ThreadedConnection.sendMsg("d/c", PL.outgoingData);
						break;
					case 5:
						exit = true;
						System.out.println("Adios...");
						break;
					default:
						System.out.println("Opcion incorrecta.");
					}
			} catch(Exception e){
				this.kill();
			}
		}
	}
	
	public void kill(){
		exit = true;
		userElection = 5;
		sc.close();
		System.exit(0); //AIXO NO CALDRA POSARHO EN EL FUTUR JA QUE AQUESTA CLASSE ES NOMES PER TESTEJAR FINS QUE TOT ES FACI DESDE LA GUI
	}
	
}
