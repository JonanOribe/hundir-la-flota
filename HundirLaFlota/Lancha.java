package HundirLaFlota;

public class Lancha extends Flota {

	public Lancha() {
		super.tipo = "lancha";
		super.tamanio = 1;
	}

	public String getTipo() {
		return tipo;
	}

	public int getTamanio() {
		return tamanio;
	}
}
