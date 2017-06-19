package HundirLaFlota;

public class Buque extends Flota {

	public Buque() {
		super.tipo = "Buque";
		super.tamanio = 3;
	}

	public String getTipo() {
		return tipo;
	}

	public int getTamanio() {
		return tamanio;
	}

}
