package HundirLaFlota.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import HundirLaFlota.misc.Utilities;

/*NOTA: MODIFICAR EL MENU DE CONEXION CON UNA PESTANYITA PARA CUSTOM GAME O NO Y USALO EN VEZ
 * DEL ACTUAL, ASI CADA CONEXION VEMOS SI SON CUSTOM O NO
 */
public class PanelCombateActionHandler implements ActionListener, KeyListener{
	
	private PanelCombate parent;
	
	public PanelCombateActionHandler(PanelCombate panel){
		parent = panel;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		if (cmd == null) { //timer
			
		}
		else {
			if (cmd.equals("Reconectar")){
				if (parent.isUserConnected()){
					parent.writeInChat("Ya estas conectado a una partida!");
					return;
				}
			
				if(Utilities.obtainConnectionValues(parent, false)) {
					parent.resetChat();
					parent.startConnection();
					parent.cambiaQuitButton("Desconectar");
				}
			}
			else if (cmd.equals("Salir") || cmd.equals("Desconectar")){
				JButton src = (JButton)evt.getSource();
				JFrame window = (JFrame)src.getTopLevelAncestor();
				PanelCombate panel = (PanelCombate)window.getContentPane();
				if (cmd.equals("Salir")) {
					panel.stopAll();
					window.dispose();//CERRAMOS EL PROGRAMA
				}
				else {
					panel.sendMsgThroughConnector("d/c"); //DESCONECTAMOS AL JUGADOR
				}
			}
			else if (cmd.equals("Volver a jugar")){
				JButton src = (JButton)evt.getSource();
				JFrame window = (JFrame)src.getTopLevelAncestor();
				PanelCombate panel = (PanelCombate)window.getContentPane();
				window.dispose();
				PanelSituaBarcos.createNewPSBWindow(false, false);
				panel.stopAll();
			}
			else if (cmd.equals("Volver al menu")){
				JButton src = (JButton)evt.getSource();
				JFrame window = (JFrame)src.getTopLevelAncestor();
				PanelCombate panel = (PanelCombate)window.getContentPane();
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				window.dispose();
				panel.stopAll();
				MenuInicial.createMenuInitialWindow();
			}
			else {
				chatButtonPress();
			}
		}
	}
	
	private void chatButtonPress(){ //MOVER A CLASE AUXILIAR PQ CLUTTEREA EL CODIGO
		String text = parent.inputTextArea.getText();
		if (!text.equals("...") && !text.equals("")){
			if (!parent.isJugadorDC()){
				parent.inputTextArea.getContenedor().sendMsgThroughConnector("chat," + text);
				parent.chatScrollablePanel.append(PanelCombate.DEFAULTPREFIX  + "Tu: " + text + "\n");
				parent.inputTextArea.setText("");
			}
			else {
				parent.chatScrollablePanel.append(PanelCombate.DEFAULTPREFIX  + "funcion de chat no disponible.\n");
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_ENTER){
			chatButtonPress();
		}		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_ENTER){
			parent.inputTextArea.setText("");
		}				
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
