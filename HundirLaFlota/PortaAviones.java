package HundirLaFlota;

public class PortaAviones extends Flota {

	public PortaAviones(){
	super.tipo="PortaAviones";
	super.tamanio=5;
	
	}
	
	public String getTipo() {
		return tipo;
	}

	public int getTamanio() {
		return tamanio;
	}
	
}
