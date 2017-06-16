package HundirLaFlota;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {
		
		Scanner sc=new Scanner(System.in);
		int opcion=0;
		 
		do{
			System.out.println("=======================================================");
			System.out.println("");
			System.out.println(" __    __   __   __   __    __   _ __     __   __  __ ");
			System.out.println("|  |  |  | |  | |  | |  |  |  | |     |  |  | |  | _  |");
			System.out.println("|  |__|  | |  | |  | |   | |  | |  ||  | |  | |   | |_|");
			System.out.println("|   __   | |  |_|  | |  | |   | |  ||  | |  | |  |");
			System.out.println("|__|  |__| |_ _ _ _| |__|  |__| |_ _ _|  |__| |__| ");		
			System.out.println("");
			System.out.println("==========================LA===========================");
			System.out.println("");		
			System.out.println("   _ _ __   __       _ _ _    _ _ _ _ _    _ _ _ ");
			System.out.println("  |  _ _ | |  |     |   _  | | _     _ |  |  _   |");
			System.out.println("  | |_     |  |     |  | | |    |  |     |  |_|  |");
			System.out.println("  |  _|    |  |_ _  |  |_| |    |  |     |   _   |");
			System.out.println("  |_|      |_ _ _ | |_ _ _ |    |__|     |__| |__|");
			System.out.println("");
			System.out.println("=======================================================");		
			System.out.println("");
			System.out.println("                      _( # )_");
			System.out.println("                  -==|_ --- _|==-              ");		
			System.out.println("                      _|| ||_");
			System.out.println("                 --==|_ --- _|==--");
			System.out.println("                     _ |   | _");
			System.out.println("           -==( _  {           }  _ )==-");
			System.out.println("            __'{ | O     o     O | }'__");
			System.out.println("     ====={           ==|0|==           }=====");
			System.out.println("     __________________ (o) __________________");		
			System.out.println("    |             ·-'         '-·             |");
			System.out.println("    |'--________-'          |    '-________--'|  ");
			System.out.println("    |                 |                       |");		
			System.out.println("     |                     |                 | ");
			System.out.println("      |                                     /");
			System.out.println("    -----_ _-------                      _ _-------");
			System.out.println("                     -------=--_--");
			System.out.println("");
			
			System.out.println("                    ============= ");
			System.out.println("                   |    -MENÚ-   |");
			System.out.println("                    ============= ");

			System.out.println("                    ------------- ");
			System.out.println("                   | 1-  START   |");
			System.out.println("                    ------------- ");
			System.out.println("                    ------------- ");		
			System.out.println("                   | 2- NORMAS   |");
			System.out.println("                    ------------- ");
			System.out.println("                    ------------- ");		
			System.out.println("                   | 3-  INFO    |");
			System.out.println("                    ------------- ");
			System.out.println("                    ------------- ");		
			System.out.println("                   | 4-  SALIR   |");
			System.out.println("                    ------------- ");
			System.out.println("");

			//Llamada a pideEntero para seleccionar opción del menú
			opcion = pedirOpcion("Por favor, introduce una de las opciones del menú:");
			switch (opcion)
				{
				case 1: {  ;break;} 
				case 2: {  ;break;}	 
				case 3: {  ;break;}	 
				case 4: {System.out.println("FIN DE PROGRAMA.");break;}
				default:{System.out.println("¡Opcion incorrecta!");}
				}
				}while (opcion!=4);
				}	
		//----------------------------------------------------------------------------------------------------------------------------------
			
		//FUNCIÓN pedirOpcion:
			
			/*Función que muestra por consola una pregunta como parámetro. 
			 *Se pide un valor del menú al usuario.
			 *Si no se introduce un valor del menú marca error y vuelve a pedir hasta que se introduzca un valor correcto.
			 */

			public static int pedirOpcion(String string) throws IOException
			{
			System.out.println();
			int opcion;String leer;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
							
			System.out.println(string);
			leer=br.readLine();
			System.out.println("Has introducido el valor: " + leer);
						    
			while(!esNumero(leer))
				{
				System.out.println("Error! El valor introducido es incorrecto.");
				System.out.println("Por favor, vuelve a introducir valor perteneciente al menú.");
				leer=br.readLine();
				} 
			opcion=Integer.parseInt(leer);
			System.out.println();
			return opcion;
			}	
					
			// funcion que comprueba el formato numerico
			public static boolean esNumero (String leer)
			{
			try	
				{
				Integer.parseInt(leer);
				return true;	
				}
			catch (NumberFormatException e){return false;}
			}

}
