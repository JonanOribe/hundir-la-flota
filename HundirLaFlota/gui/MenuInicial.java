package HundirLaFlota.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import HundirLaFlota.misc.Utilities;
import HundirLaFlota.network.HLFServer;

/*Clase simple que funcionara como menu inicial para el juego. De la implementacion de la partida contra la AI se encarga Jonan, yo he montado
 * las demas opciones que te permiten conectarte a un servidor en localhost (la partida LAN) o a otro con IP/puerto determinada, la 
 * creacion de servidor dedicado (cierra el menu, se ejecuta el servidor) o no dedicado (no cierra el menu, pero se cerrara cuando cierres
 * la aplicacion (cambiar puede).
 * 
 * Para jugar -> monta un servidor dedicado o no ypon jugar por LAN a menos que juegues entre dos ordenadores que entonces tendras que poner su IP. 
 * Por tanto necesitaras ejecutar como minimo dos procesos para comenzar a jugar (yo normalmente abro tres siendo uno el servidor
 * dedicado). Todo esto es por testeo y tal.
 * 
 * QUE FALTA(08/07): REFINAR EL SERVIDOR (comprobar que todo vaya de perlas, sobretodo con multiples usuarios y conectandose
 * a IPs externas) , AÑADIR CONTADOR DE TIEMPO (para desconexion automatica), PUNTOS, ETC. (Y falta un tocado y hundido en vez de tocado solo).
 * FALTA TAMBIEN MEJORAR LA GUI (chapuzera por ahora, poner cosas bonitas, que haya conexion entre las ventanas ya que
 * por ahora es super tenue (si le doy a salir que vaya al menu principal etc.)
 * Y SEGURO QUE ALGO MAS
 * 
 * <<<<LO MAS IMPORTANTE: QUE EL SERVIDOR NO SE FIE DEL HIT OR MISS DEL PANEL, QUE PRIMERO LOS JUGADORES ENVIEN LAS POSICIONES
 * SE COMPRUEBE QUE SON EL NUMERO QUE TOCAN Y LUEGO LA PARTIDA DIGA SI ES HIT OR MISS>>>>>>>>>>>>>>>
 */
@SuppressWarnings("serial")
public class MenuInicial extends JPanel implements ActionListener{
	
	private boolean createdServer = false; //El control si existe ya un servidor deberia ser mayor...
	
	public MenuInicial(){
		JPanel mainPanel = Utilities.createCustomJPanel(null, 1, this,"Jugar contra la AI", "Jugar en LAN", "Jugar por internet", "Crear un servidor", "Crear un servidor dedicado");
		this.add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		/*Faltara implementar partidas para 1 Jugador contra la maquina sin usar el server ni naaaa*/
		
		
		if (!createdServer && (cmd.equals("Crear un servidor dedicado") || cmd.equals("Crear un servidor"))){
			String[] chosenIPPort = Utilities.createCustomDialog(new JFrame(""), 0);
			
			if (chosenIPPort == null) {return;} //Ha elegido cancelar...
			
			//Control de errores minimo... esto habria que cambiarlo
			if (chosenIPPort[1].equals("")) { chosenIPPort[1] = Integer.toString(HLFServer.DEFAULTPORT); }
			
			//Estaria bien montar una GUI minima para el servidor donde el output vaya a una textarea y system.in a un textinput abajo...futuro...
			HLFServer server = new HLFServer(Integer.parseInt(chosenIPPort[1]));
			JButton src = (JButton)e.getSource();
			JFrame window = (JFrame)src.getTopLevelAncestor();
			
			if (cmd.equals("Crear un servidor")){
				createdServer = true;
				window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Para evitar que cerrar la GUI cierre el server...
				server.start();
			}
			else {
				window.dispose();
				server.listenForClients();
			}
		}
		else if (cmd.equals("Jugar en LAN")) {
			JButton src = (JButton)e.getSource();
			JFrame window = (JFrame)src.getTopLevelAncestor();
			window.dispose();
			PanelSituaBarcos.createNewPSBWindow("127.0.0.1",HLFServer.DEFAULTPORT, createdServer);
		}
		else if (cmd.equals("Jugar por internet")){
			String[] chosenIPPort = Utilities.createCustomDialog(new JFrame(""), 1);
			
			if (chosenIPPort == null) {return;} 
			
			//Control de errores minimo... esto habria que cambiarlo
			if (chosenIPPort[0].equals("")) { chosenIPPort[0] = "127.0.0.1"; }
			if (chosenIPPort[1].equals("")) { chosenIPPort[1] = Integer.toString(HLFServer.DEFAULTPORT); }
			
			JButton src = (JButton)e.getSource();
			JFrame window = (JFrame)src.getTopLevelAncestor();
			window.dispose();
			PanelSituaBarcos.createNewPSBWindow(chosenIPPort[0], Integer.parseInt(chosenIPPort[1]), createdServer);
			
		}else if(cmd.equals("Jugar contra la AI")){
			JButton src = (JButton)e.getSource();
			JFrame window = (JFrame)src.getTopLevelAncestor();
			window.dispose();
			PanelSituaBarcos.createNewPSBWindow("127.0.0.1",HLFServer.DEFAULTPORT, createdServer);
			
		}
	}
	
	public static void createMenuInitialWindow(){
		JFrame window = new JFrame("Menu inicial");
		MenuInicial content = new MenuInicial();
		window.setContentPane(content);
		window.setVisible(true);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void main(String[] args){
		createMenuInitialWindow();
	}

}
