package HundirLaFlota.ai;

import java.util.Arrays;

import HundirLaFlota.gui.LabelGridCombate;

/*He creado una clase barco para llevar la cuenta de los barcos que la maquina pone sobre el tablero
 * Guardara tanto una array de Labels (las posiciones fisicas en el tablero) como sus coordenadas en
 * el tablero. La lista de nombres ordenados por tamaño son una constante i se asignan en el constructor.
 * Puede usarse tambien para los barcos que el usuario pone en el tablero ya vere. */
public class Barco {

	private LabelGridCombate[] posicionesEnTablero;
	private String nombre;
	private int[][] coordsTableroInt;
	private static final String[] nombresPorTamanio = {"Lancha", "Buque", "Destructor", "Portaaviones"}; //Nombre para barco tamanio 1, 2, 3...

	public Barco(LabelGridCombate[] posiciones){
		this.posicionesEnTablero = posiciones;
		this.nombre = setNombre(posiciones);
		getCoordsFromLabels(posiciones);
	}
	//NOTA: tamanio sera implicita (length de la array posicionesEnTablero), getTamanio para obtenerlo, para cambiarlo cambia el numero de posiciones...
	
	private String setNombre(LabelGridCombate[] posiciones){
		if (posiciones.length > 0 && posiciones.length <= nombresPorTamanio.length) {
			nombre = nombresPorTamanio[posiciones.length-1];
		}
		else {
			nombre = "Barco desconocido";
		}
		return nombre;
	}
	
	private void getCoordsFromLabels(LabelGridCombate[] posiciones){
		int i = 0;
		this.coordsTableroInt = new int[posiciones.length][2];
		for (LabelGridCombate posicion : posiciones){
			this.coordsTableroInt[i] = posicion.getCoords();
			i++;
		}
	}
	
	public LabelGridCombate[] getPosicionesEnTablero() {
		return posicionesEnTablero;
	}

	public int getTamanio(){
		return this.posicionesEnTablero.length;
	}

	public void setPosicionesEnTablero(LabelGridCombate[] posicionesEnTablero) {
		this.posicionesEnTablero = posicionesEnTablero;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public int[][] getCoordsTableroInt() {
		return coordsTableroInt;
	}


	public void setCoordsTableroInt(int[][] coordsTableroInt) {
		this.coordsTableroInt = coordsTableroInt;
	}
	
	public void toConsole() {
		System.out.println("Nombre del barco: " + this.nombre + ".");
		System.out.print("Posiciones en tablero: ");
		for (int i = 0; i < this.coordsTableroInt.length; i++){
			if (i != this.coordsTableroInt.length-1){
				System.out.print(Arrays.toString(this.coordsTableroInt[i]) + ", ");
			}else {
				System.out.println(Arrays.toString(this.coordsTableroInt[i]) + ".");
			}
		}
	}
}
