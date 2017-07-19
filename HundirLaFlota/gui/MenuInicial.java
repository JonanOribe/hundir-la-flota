package HundirLaFlota.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import HundirLaFlota.misc.Utilities;
import HundirLaFlota.network.HLFServer;

/*Clase simple que funcionara como menu inicial para el juego. De la implementacion de la partida contra la AI se encarga Jonan, yo he montado
 * las demas opciones que te permiten conectarte a un servidor en localhost (la partida LAN) o a otro con IP/puerto determinada, la 
 * creacion de servidor dedicado (cierra el menu, se ejecuta el servidor) o no dedicado (no cierra el menu, pero se cerrara cuando cierres
 * la aplicacion (cambiar puede).
 * 
 * QUE FALTA(19/07): REFINAR EL SERVIDOR (comprobar que todo vaya de perlas, sobretodo con multiples usuarios y conectandose
 * a IPs externas).
 * 
 * FALTA TAMBIEN MEJORAR LA GUI (chapuzera por ahora, poner cosas bonitas, Y SEGURO QUE ALGO MAS
 * 
 * <<<<LO MAS IMPORTANTE: QUE EL SERVIDOR NO SE FIE DEL HIT OR MISS DEL PANEL, QUE PRIMERO LOS JUGADORES ENVIEN LAS POSICIONES
 * SE COMPRUEBE QUE SON EL NUMERO QUE TOCAN Y LUEGO LA PARTIDA DIGA SI ES HIT OR MISS -> ( falta un tocado y hundido en vez de tocado solo)>>>>>>>>>>>>>>>
 */
@SuppressWarnings("serial")
public class MenuInicial extends JPanel implements ActionListener{
	
	private MainWindow linkedWindow;
	
	public MenuInicial(MainWindow myWindow){
		super();
		linkedWindow = myWindow;
		JPanel mainPanel = Utilities.createCustomJPanel(null, 1, this,"Jugar contra la AI", "Multijugador", "Crear un servidor", "Crear un servidor dedicado");
		this.add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();		
		
		if ((cmd.equals("Crear un servidor dedicado") || cmd.equals("Crear un servidor"))){
			if (linkedWindow.hasCreatedServer()) { 
				JOptionPane.showMessageDialog(null, "Ya hay un servidor ejecutandose en segundo plano","Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String[] chosenIPPort = Utilities.createCustomDialog(new JFrame(""), 0);
			
			if (chosenIPPort == null) {return;} //Ha elegido cancelar...
			
			//Control de errores minimo... esto habria que cambiarlo
			if (chosenIPPort[1].equals("")) { chosenIPPort[1] = Integer.toString(HLFServer.DEFAULTPORT); }
			
			//Estaria bien montar una GUI minima para el servidor donde el output vaya a una textarea y system.in a un textinput abajo...futuro...
			HLFServer server = new HLFServer(Integer.parseInt(chosenIPPort[1]));
			
			if (cmd.equals("Crear un servidor")){
				linkedWindow.setCreatedServer(true);
				server.start();
			}
			else {
				linkedWindow.dispose();
				server.listenForClients();
			}
		}
		else if (cmd.equals("Multijugador")) {
			linkedWindow.setSinglePlayer(false);
			linkedWindow.goToState(MainWindow.windowState.PLACEBOATS);
			
		}else if(cmd.equals("Jugar contra la AI")){
			linkedWindow.setSinglePlayer(true);
			linkedWindow.goToState(MainWindow.windowState.PLACEBOATS);			
		}
	}
	
	public static void createMenuInitialWindow(){
		MainWindow window = new MainWindow("Menu inicial");
		MenuInicial content = new MenuInicial(window);
		window.fillWindow(content);
		window.addWindowListener(window);
	}
	
	
	public static void main(String[] args){
		createMenuInitialWindow();
	}

}
