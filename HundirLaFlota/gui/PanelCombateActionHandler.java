package HundirLaFlota.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;

import HundirLaFlota.misc.Utilities;

/*Clase que controla la logica de los eventos que suceden en un PanelCombate.
 * Seria: Los botones (enviar chat, desconectar, reconectar, salir, volver al menu
 * y volver a jugar) y el timer (se activa cada 1000ms por ahora)
 */
public class PanelCombateActionHandler implements ActionListener, KeyListener{
	
	private PanelCombate parent;
	private boolean playerActive = true;
	private int seconds = PanelCombate.SEGUNDOSPORTURNO;
	private int pressingDelay = 0;


	public PanelCombateActionHandler(PanelCombate panel){
		parent = panel;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		/*Timer -- cada segundo se activa y baja una unidad los segundos del usuario si es su turno,
		 * si llega a zero se le envia un msg por inactividad y si vuelve a zero se le desconecta...
		 */
		if (cmd == null) {
			if (pressingDelay > 0) {
				pressingDelay--;
			}
			if (parent.isMyTurn()){
				seconds--;
				if (seconds < 0) {
					seconds = PanelCombate.SEGUNDOSPORTURNO;
					if (!playerActive) {
						parent.sendMsgThroughConnector("d/c");
					} else {
						playerActive = !playerActive;
						parent.writeInChat("Aviso por inactividad");
					}
				}
				parent.timerLabel.setText("Tiempo: " + seconds);
			}
		}
		else {
			if (cmd.equals("Reconectar")){
				if (parent.isUserConnected()){
					parent.writeInChat("Ya estas conectado a una partida!");
					return;
				}
			
				if(Utilities.inputConnectionToBoard(parent, false)) {
					parent.resetChat();
					parent.startConnection();
					parent.cambiaQuitButton("Desconectar");
				}
			}
			else if (cmd.equals("Salir") || cmd.equals("Desconectar")){
				JButton src = (JButton)evt.getSource();
				MainWindow window = (MainWindow)src.getTopLevelAncestor();
				PanelCombate panel = (PanelCombate)window.getContentPane();
				if (cmd.equals("Salir")) {  //Salimos del todo
					panel.stopAll();
					window.closeThisWOptions(true);
				}
				else {
					panel.sendMsgThroughConnector("d/c"); //DESCONECTAMOS AL JUGADOR
				}
			}
			else if (cmd.equals("Volver a jugar")){
				JButton src = (JButton)evt.getSource();
				MainWindow window = (MainWindow)src.getTopLevelAncestor();
				window.resetMPBoard(); 
			}
			else if (cmd.equals("Volver al menu")){
				JButton src = (JButton)evt.getSource();
				MainWindow window = (MainWindow)src.getTopLevelAncestor();
				PanelCombate panel = (PanelCombate)window.getContentPane();
				panel.stopAll();
				window.goToState(MainWindow.windowState.MAINMENU);
			}
			else {
				chatButtonPress();
			}
		}
	}
	
	public boolean isDelayActive() {
		return (this.pressingDelay > 0);
	}
	
	public void setDelay(int seconds){
		this.pressingDelay = seconds;
	}
	/*Funcion auxiliar que gestiona el apretar el boton de enviar chat (se envia el texto al servidor y
	 * se logea en la ventana de chat)	 */
	private void chatButtonPress(){ 
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

	//Si se aprieta el boton de enter se ejecuta lo mismo que apretando el boton de enviar
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
	
	public boolean isPlayerActive() {
		return playerActive;
	}

	public void setPlayerActive(boolean inactivityCheck) {
		this.playerActive = inactivityCheck;
	}
	
	
	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	/*Funcion que resetea los segundos que tiene el usuario para actuar y cancela su turno para
	 * que el timer no baje */
	public void resetSeconds(){
		this.seconds = PanelCombate.SEGUNDOSPORTURNO;
		parent.timerLabel.setText("Tiempo: " + seconds);
		parent.setMyTurn(false);
	}

}
