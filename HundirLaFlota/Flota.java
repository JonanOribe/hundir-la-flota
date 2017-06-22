package HundirLaFlota;

import java.util.ArrayList;

public class Flota {

	// Definición de la clase FLOTA de la que heredaran TODOS los demás barcos

	protected String tipo = "";// Tipo de buque (Portaaviones,fragata,blablabla)
	protected int tamanio = 0; // Numero de casillas que ocupa el barco

	public static ArrayList<Flota> crearFlota(){
		
		ArrayList<Flota> flota1=new ArrayList<Flota>();
		
		PortaAviones portaAviones1=new PortaAviones();
		Buque buque1= new Buque();
		Buque buque2= new Buque();
		Buque buque3= new Buque();
		Lancha lancha1= new Lancha();
		Lancha lancha2= new Lancha();
		Lancha lancha3= new Lancha();
		Lancha lancha4= new Lancha();
		
		flota1.add(portaAviones1);
		flota1.add(buque1);
		flota1.add(buque2);
		flota1.add(buque3);
		flota1.add(lancha1);
		flota1.add(lancha2);
		flota1.add(lancha3);
		flota1.add(lancha4);
		
		return flota1;
		
	}
	
	
	@Override
	public String toString() {
		return "["+"Barco=" + tipo + ", tamanio=" + tamanio + "]";
	}


	public String getTipo() {
		return tipo;
	}

	public int getTamanio() {
		return tamanio;
	}

}
