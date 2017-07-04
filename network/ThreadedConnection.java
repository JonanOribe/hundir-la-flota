package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/*Clase que representa una conexion con canales para enviar/recibir datos y que es capaz de
 * mantenerse ejecutando como thread independiente si es necesario*/
public class ThreadedConnection extends Thread{
	
	volatile protected BufferedReader incomingDataP1;
	volatile protected PrintWriter outgoingDataP1;
	protected Socket P1Conn;
	
	protected boolean openConnection(Socket P1Conn){
		this.P1Conn = P1Conn;
		try {		
			//P1Conn.setKeepAlive(true);    //????DO THIS?
			this.incomingDataP1 = new BufferedReader(new InputStreamReader(P1Conn.getInputStream()));
			this.outgoingDataP1 = new PrintWriter(new OutputStreamWriter(P1Conn.getOutputStream()));
			return true;
		} catch (Exception e){
			System.out.println("Problemas abriendo los canales de datos, " + e.getMessage());
		}
		return false;
	}
	
	protected static boolean sendMsg(String msg, PrintWriter outgoingData){
		try {
			outgoingData.println(msg);
			outgoingData.flush();
			return true;
		} catch(Exception e){
			return false;
		}
	}
		
	protected void closeAll(){
		try {
			P1Conn.close();
			incomingDataP1.close();
			outgoingDataP1.close();
		} catch (Exception e){
		}
	}
	
	protected String incomingData(BufferedReader incomingData){
		String data;
		try {
			if ((data = incomingData.readLine()) != null){
				return data;
			}
			return null;
		} catch (Exception e){
			return null;
		}
	}

}


