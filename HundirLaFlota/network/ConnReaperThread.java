package HundirLaFlota.network;

import java.util.ArrayList;

/*Programa ejecutado en paralelo que se activa cada x tiempo y comprueba
 * si algun usuario se ha desconectado para cerrar su conexion... usado
 * en paralelo con un timer desde la GUI, este comprueba desconexiones 
 * el timer de la GUI no estar activo */
public class ConnReaperThread extends Thread{

	public static final int WAKEUPMILLIS = 80000;
	private volatile boolean running = true;
	
	public void run(){
		try {
			while(running){
				sleep(WAKEUPMILLIS);
				HLFServer.log("--Reaper thread: Waking up, generating an inactivity check...");
				ArrayList<GameHandlerThread> games = HLFServer.getGames();
				if (games == null) { continue;}
				for (int i = 0; i < games.size(); i++){
					if (games.get(i).hasP2()){ //Solo comprueba cuando los dos jugadores estan dentro
						GameLogic.checkForDCPlayers(false, true, games.get(i)); 
					}
				}
			}
		}
		catch(Exception e){
			HLFServer.log("Error in the reaper thread. " + e.getMessage());
		}
	}
	
	public void stopExecution(){
		running = false;
	}
}
