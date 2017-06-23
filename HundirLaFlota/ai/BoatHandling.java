package HundirLaFlota.ai;

import java.util.Random;

import HundirLaFlota.gui.LabelGridCombate;

public class BoatHandling {

	private static Barco[] flotaAI;
	private static final int[] tamaniosFlota = {1,2,2,3,4};
	private static Random randomGen = new Random(); //Para hacer pruebas de insercion aleatoria de la AI, borrar en futuro

	
	public static boolean placeAllBoatsOnGrid(LabelGridCombate[][] grid) {
		flotaAI = new Barco[tamaniosFlota.length]; 
		LabelGridCombate[] posBarco = null;
		for (int i = 0; i < flotaAI.length; i++){
			while (posBarco == null) {
				posBarco =  BoatHandling.placeBoatOnGrid(randomGen.nextInt(7), randomGen.nextInt(7), tamaniosFlota[i], (randomGen.nextInt(3) > 1), grid);
			}
			flotaAI[i] = new Barco(posBarco);
			posBarco = null;
		}
		listFlota(); //Comprobacion
		return true;
	}
	
	public static void listFlota(){
		for (Barco barco : flotaAI){
			barco.toConsole();
		}
	}
	
	/*Asumiendo que initialpos estan dentro de la grid, por tanto que la primera posicion es legal. Podriamos rehacer la existente
	en panelSituaBarcos por una mas similar a esta la verdad...*/
	public static LabelGridCombate[] placeBoatOnGrid(int initialPosX, int initialPosY, int tamanio, boolean horizontal, LabelGridCombate[][] grid){
		int dim = (horizontal) ? grid.length : grid[0].length;
		int compruebaEje = (horizontal) ? initialPosY : initialPosX;
		int i = tamanio-1;
		int counter = 1;
		LabelGridCombate[] labelsBarco = new LabelGridCombate[tamanio];
		labelsBarco[0] = grid[initialPosX][initialPosY];
		while (i > 0){
			if ((compruebaEje + counter) >= dim) { //Fuera de la grid...
				return null;
			}
			labelsBarco[counter] = (horizontal) ? grid[initialPosX][initialPosY+counter] : grid[initialPosX+counter][initialPosY];
			i--;
			counter++;
		}
		return labelsBarco;
	}
}
