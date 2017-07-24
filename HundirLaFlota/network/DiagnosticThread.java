package HundirLaFlota.network;

import java.util.Scanner;

import HundirLaFlota.gui.MainWindow;

/*Clase para mantener un menu en el servidor que por ahora te deja solo listar partidas y salir,
 * se podrian expandir las propiedades de diagnostico en el futuro (listar puntos partidas, aciertos ...)*/

/*A lo mejor clear superclase ThreadedMenu para diagnostic i clienttestingthread...*/
public class DiagnosticThread extends Thread {
	
	private static Scanner sc;
	private HLFServer server;

	public DiagnosticThread(HLFServer server){
		this.server = server;
	}
	public void run(){
		mainLoop();
	}
		/*Funcion solo para escribir el menu en la consola*/
		public static void writeMenu(){
			System.out.println("Menu del sistema");
			System.out.println("----------------");
			System.out.println("OPCIONES:");
			System.out.println("1.Listar partidas.");
			System.out.println("2.Comprobar posiciones totales barcos.");
			System.out.println("5.Salir.");
		}
		
		private void mainLoop(){
			boolean exit = false;
			int userElection;
			while (!exit){
				writeMenu();
				userElection = pideEntero("Elige la opcion que quieras ejecutar");
				switch(userElection){
				case 1:
					HLFServer.listGames();
					break;
				case 2:
					HLFServer.POSICIONESTOTALESBARCOS = MainWindow.getShipTotalPositions();
					HLFServer.log("Numero posiciones a destruir: " + HLFServer.POSICIONESTOTALESBARCOS);
					break;
				case 5:
					exit = true;
					System.out.println("Adios...");
					server.close();
					break;
				default:
					System.out.println("Opcion incorrecta.");
				}
				System.out.println("-----------------------------------");
				System.out.println("-----------------------------------");
			}
			sc.close();
		}
		
		/*Funcion reciclada del anterior ejercicio para pedir un numero entero, con una string pregunta
		 * como parametro para personalizar que le pedira al usuario	 */
		public static int pideEntero(String pregunta){
			String userInput;
			int desiredInput = 0;
			boolean goodInput = false;
			if (pregunta.equals("")){
				pregunta = "Introduce un numero entero:";
			}
			System.out.println(pregunta + " ");
			sc = new Scanner(System.in);
			userInput = sc.nextLine();
			do {
				try {
				desiredInput = Integer.parseInt(userInput); 
				goodInput = true;
				}
				catch(Exception e){
					System.out.println("Introduce un numero entero por favor");
					userInput = sc.nextLine();
				}
			}while (!goodInput);
			return desiredInput;
		}
}
