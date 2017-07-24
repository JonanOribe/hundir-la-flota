package HundirLaFlota.ai;

import java.util.Random;

import HundirLaFlota.gui.LabelGridCombate;
import HundirLaFlota.gui.MainWindow;

public class BoatHandling {

	private static BarcoEnGrid[] flotaAI;
	private static Random randomGen = new Random(); //Para hacer pruebas de insercion aleatoria de la AI

	
	public static boolean placeAIBoatsOnGrid(LabelGridCombate[][] grid) {
		flotaAI = new BarcoEnGrid[MainWindow.TAMANYOBARCOSFLOTA.length]; 
		LabelGridCombate[] posBarco = null;
		for (int i = 0; i < flotaAI.length; i++){ //Para cada barco en la flota...
			while (posBarco == null) { //Probar posiciones aleatorias hasta que aparezca una legal
				posBarco =  BoatHandling.placeBoatOnGrid(randomGen.nextInt(7), randomGen.nextInt(7), MainWindow.TAMANYOBARCOSFLOTA[i], (randomGen.nextInt(3) > 1), grid);
			}
			flotaAI[i] = new BarcoEnGrid(posBarco);
			posBarco = null;
		}
		listFlota(); //Comprobacion que todas las posiciones son legales
		return true;
	}
	
	public static void listFlota(){
		for (BarcoEnGrid barco : flotaAI){
			barco.toConsole();
		}
	}
	
	/*Asumiendo que initialpos estan dentro de la grid, por tanto que la primera posicion es legal. Podriamos rehacer la existente
	en panelSituaBarcos por una mas similar a esta la verdad...
	Devuelve null si la orientacion y tamanio del barco no dan una posicion legal*/
	public static LabelGridCombate[] placeBoatOnGrid(int initialPosX, int initialPosY, int tamanio, boolean horizontal, LabelGridCombate[][] grid){
		int dim = (horizontal) ? grid.length : grid[0].length;			//Dependiendo de si es horizontal o no solo haremos cambios en una dimension (i o j)
		int compruebaEje = (horizontal) ? initialPosY : initialPosX;	//Dependiendo de la orientacion tambien los cambios comenzaran en la posicion actual de i o de j
		int i = tamanio-1;		//Miraremos todas las posiciones excepto la actual que se supone correcta
		int counter = 1;
		LabelGridCombate[] labelsBarco = new LabelGridCombate[tamanio]; //Guardaremos todas las posiciones comprobadas correctamente en la array
		labelsBarco[0] = grid[initialPosX][initialPosY];
		while (i > 0){
			if ((compruebaEje + counter) >= dim) { //Fuera de la grid...
				return null; //Devolvemos null, sino seguimos probando la siguiente posicion
			}
			labelsBarco[counter] = (horizontal) ? grid[initialPosX][initialPosY+counter] : grid[initialPosX+counter][initialPosY];
			i--;
			counter++;
		}
		return labelsBarco;
	}
	

}
