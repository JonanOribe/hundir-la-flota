package HundirLaFlota;

import java.util.ArrayList;

public class Funciones {
	
	public static void imprimirNormas(){
		
		System.out.println("Las normas de este juego son las siguientes:");
		System.out.println(" ");
		System.out.println("********************************************");
		System.out.println("********************************************");
		System.out.println("********************************************");
		System.out.println("********************************************");
		System.out.println("********************************************");
		System.out.println(" ");
		
		
		//INTRODUCIR NORMAS (PRIMERO HABRA QUE VER COMO ORGANIZAMOS EL JUEGO)
		
	}
	
	public static ArrayList<Flota> lanzarFlota(){
		ArrayList<Flota> primeraflota=Flota.crearFlota();
		return primeraflota;
	}

}
