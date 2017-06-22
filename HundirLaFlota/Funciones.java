package HundirLaFlota;

import java.util.ArrayList;

public class Funciones {

	static ArrayList<Flota> flota;

	public static void imprimirNormas() {

		System.out.println("Las normas de este juego son las siguientes:");
		System.out.println(" ");
		System.out.println("********************************************");
		System.out.println("Dos jugadores se enfrentan cara a cara en un duelo épico.");
		System.out.println("Cada uno situa sobre el tablero 8 embarcaciones e intenta hundir las del rival antes de que sea tarde");
		System.out.println("El primero en dejar sin barcos al contrincante,gana");
		System.out.println("********************************************");

		System.out.println(" ");

		// INTRODUCIR NORMAS (PRIMERO HABRA QUE VER COMO ORGANIZAMOS EL JUEGO)

	}

	public static ArrayList<Flota> lanzarFlota() {
		flota = Flota.crearFlota();
		return flota;
	}

	public static void jugar() {
		System.out.println("Comienza la partida,situa tus barcos");
		lanzarFlota();
		System.out.println("Tu flota se compone de " + flota);

	}

}
