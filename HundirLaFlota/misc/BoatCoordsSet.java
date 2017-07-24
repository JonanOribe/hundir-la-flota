package HundirLaFlota.misc;

import java.util.ArrayList;

/*Clase que hace las funciones de un set de objetos int[] 
 * 
 * La precondicion en el Set es que no puede haber elementos repetidos, esto se comprueba
 * haciendo contains cada vez que hagas un add.
 * En el diseño hemos dejado que los elementos introducidos puedan ser de diferente tamaño,
 * a lo mejor habria que obligar a que fueran de un tamaño unico (ej no dejar [1,2,3] y [1,2] aunque 
 * los dos sean arrays de ints...
 * 
 * Creada para el servidor de hundir la flota para que guarde las coordenadas de los barcos de los 
 * jugadores que se obtienen como una serie de array de dos enteros que no se pueden repetir.
 */
public class BoatCoordsSet {
	
	private ArrayList<int[]> elements;
	private int size;
	
	public BoatCoordsSet(){
		elements = new ArrayList<int[]>();
		size = 0;
	}
	
	public boolean contains(int[] element) {
		int sameElement;
		int[] tmp;
		if (size == 0) {
			return false;
		} 
		for (int i = 0; i < size; i++) {
			tmp = elements.get(i);
			if (tmp.length != element.length) {
				continue; //No seran iguales seguro ya que su tamaño es diferente. NOTA: Dejar que haya elementos(arrays) de diferente tamanyo o no?
			}
			sameElement = 0;
			for (int j = 0; j < element.length; j++) {
				if (tmp[j] == element[j]) {
					sameElement++;
				}
			}
			if (sameElement == element.length) { return true; }
		}
		return false;
	}
	
	
	public boolean add(int[] element) {
		if (!contains(element)) {
			int[] deepCopy = new int[element.length];
			for (int i = 0; i < element.length; i++) {
				deepCopy[i] = element[i];
			}
			elements.add(deepCopy);
			size++;
			return true;
		} else {
			return false;
		}	
	}
	
	public int findIndex(int[] element) {
		if (!contains(element)) { return -1; }
		int[] tmp;
		int sameElement;
		for (int i = 0; i < size; i++) {
			tmp = elements.get(i);
			sameElement = 0;
			for (int j = 0; j < element.length; j++){
				if (tmp[j] != element[j]) {
					break;
				}
				sameElement++;
			}
			if (sameElement == element.length) {
				return i;
			}
		}
		return -1;
	}
	
	public int[] remove(int index) {
		return elements.remove(index);
	}
	
	//Necessaries?
	public int[] remove(int[] element) {
		int index = findIndex(element);
		if (index >= 0) {
			return remove(index);
		}
		return null;
	}
	
	public int[] get(int index){
		return elements.get(index);
	}
	
	public int size(){
		return this.size;
	}

	public String toString(){
		String all = "";
		int[] tmp;
		for (int i = 0; i < size; i++) {
			tmp = elements.get(i);
			for (int j = 0; j < tmp.length; j++) {
				all += tmp[j] + ", ";
			}
		}
		if (all.length() > 2){
			all = all.substring(0, (all.length()-2));
		}
		return all;
	}
	
}

