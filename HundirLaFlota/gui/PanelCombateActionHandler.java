package HundirLaFlota.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import HundirLaFlota.misc.Utilities;

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
				JButton src = (JButton)evt.getSource();
				JFrame window = (JFrame)src.getTopLevelAncestor();
				PanelCombate panel = (PanelCombate)window.getContentPane();
				if (panel.isUserConnected()){
					panel.writeInChat("Ya estas conectado a una partida!");
					return;
				}
				String[] chosenIPPort = Utilities.createCustomDialog(new JFrame(""), 1, parent.getChosenIP(), parent.getChosenPort());
				
				//Control de errores minimo... esto habria que cambiarlo
				if (chosenIPPort[0].equals("")) { chosenIPPort[0] = parent.getChosenIP(); }
				if (chosenIPPort[1].equals("")) { chosenIPPort[1] = Integer.toString(parent.getChosenPort()); }
				
				panel.startConnection(chosenIPPort[0],Integer.parseInt(chosenIPPort[1]));
				panel.cambiaQuitButton("Desconectar");
			}
			else if (cmd.equals("Salir") || cmd.equals("Desconectar")){
				JButton src = (JButton)evt.getSource();
				JFrame window = (JFrame)src.getTopLevelAncestor();
				PanelCombate panel = (PanelCombate)window.getContentPane();
				if (panel.isJugadorDC()) {
					window.dispose(); //CERRAMOS EL PROGRAMA
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
				panel.stopAll();
				PanelSituaBarcos.createNewPSBWindow(panel.getChosenIP(), panel.getChosenPort(), false);
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
