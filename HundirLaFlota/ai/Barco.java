package HundirLaFlota.ai;

import java.util.Arrays;

/*Clase generalista barco, crea un barco y si lo haces a partir de sus coordenadas le asigna un nombre si tiene
 * entre 1-4 coordenadas */
public class Barco {
	
	protected String nombre;
	protected int[][] coordsTableroInt;
	protected static final String[] nombresPorTamanio = {"Lancha", "Buque", "Destructor", "Portaaviones"}; //Nombre para barco tamanio 1, 2, 3...
	
	public Barco(int[][] coordsTableroInt){
		this.nombre = setNombrePorTamanio(coordsTableroInt.length);
		this.coordsTableroInt = coordsTableroInt;
	}
	
	public Barco(){
		this.nombre = "";
	}
	
	protected String setNombrePorTamanio(int tamanio){
		if (tamanio > 0 && tamanio <= nombresPorTamanio.length) {
			nombre = nombresPorTamanio[tamanio-1];
		}
		else {
			nombre = "Barco desconocido";
		}
		return nombre;
	}
	
	/*Por si es necesario poner un nombre especial para el barco*/
	protected void setNombreCustom(String nombreEspecial){
		this.nombre = nombreEspecial;
	}
	
	public int getTamanio(){
		return this.coordsTableroInt.length;
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
