package HundirLaFlota.ai;

import HundirLaFlota.gui.LabelGridCombate;

/* Clase barco que hereda de la original y ademas te permite guardar las labels que
 * guardan su posicion en uno de los grids. Tiene una funcion para obtener las 
 * coordenadas numericas de las labels que contiene.  */
public class BarcoEnGrid extends Barco{

	private LabelGridCombate[] posicionesEnTablero;

	public BarcoEnGrid(LabelGridCombate[] posiciones){
		super(getCoordsFromLabels(posiciones));
		this.posicionesEnTablero = posiciones;
	}
	//NOTA: tamanio sera implicita (length de la array posicionesEnTablero), getTamanio para obtenerlo, para cambiarlo cambia el numero de posiciones...

	private static int[][] getCoordsFromLabels(LabelGridCombate[] posiciones){
		int i = 0;
		int[][] coordsInt = new int[posiciones.length][2];
		for (LabelGridCombate posicion : posiciones){
			coordsInt[i] = posicion.getCoords();
			i++;
		}
		return coordsInt;
	}
	
	public LabelGridCombate[] getPosicionesEnTablero() {
		return posicionesEnTablero;
	}

	public void setPosicionesEnTablero(LabelGridCombate[] posicionesEnTablero) {
		this.posicionesEnTablero = posicionesEnTablero;
	}

	

}
