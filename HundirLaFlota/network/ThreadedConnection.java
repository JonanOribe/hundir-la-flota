package HundirLaFlota.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/*Clase que representa una conexion con canales para enviar/recibir datos y que es capaz de
 * mantenerse ejecutando como thread independiente si es necesario*/
public class ThreadedConnection extends Thread{
	
	volatile protected BufferedReader incomingData;
	volatile protected PrintWriter outgoingData;
	protected Socket conn;
	
	protected boolean openConnection(Socket P1Conn){
		this.conn = P1Conn;
		try {		
			//P1Conn.setKeepAlive(true);    //????DO THIS?
			this.incomingData = new BufferedReader(new InputStreamReader(P1Conn.getInputStream()));
			this.outgoingData = new PrintWriter(new OutputStreamWriter(P1Conn.getOutputStream()));
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
			conn.close();
			incomingData.close();
			outgoingData.close();
		} catch (Exception e){
		}
	}
	
	protected boolean isConnected(){
		return (this.conn != null);
	}
	
	protected String fetchData(BufferedReader incomingData){
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


