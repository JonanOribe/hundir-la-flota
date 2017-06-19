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
		Lancha lancha1= new Lancha();
		
		flota1.add(portaAviones1);
		flota1.add(buque1);
		flota1.add(lancha1);
		
		System.out.println(flota1.toString());
		
		return flota1;
		
	}
	
	
	@Override
	public String toString() {
		return "Flota [tipo=" + tipo + ", tamanio=" + tamanio + "]";
	}


	public String getTipo() {
		return tipo;
	}

	public int getTamanio() {
		return tamanio;
	}

}
