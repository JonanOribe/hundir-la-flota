package HundirLaFlota;

public class Jugador {
	
	private String nombre="";
	private double puntuacion=0.0;
	
	public Jugador(String nombre){ //La puntuacion no se la pone el propio jugador si no que se le asigna al final de la partida en base al numero de hundidos y tocados entre los turnos gastados
		this.nombre=nombre;
	}
	
	public void setNombre(String nombre){
		this.nombre=nombre;
	}
	
	public String getNombre(){
		return nombre;
	}
		

}
