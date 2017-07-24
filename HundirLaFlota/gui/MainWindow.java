package HundirLaFlota.gui;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import HundirLaFlota.misc.Utilities;

/*Clase que sera la ventana principal del programa y ira cambiando sus contenidos dependiendo
 * de lo que el usuario elija, usando las demas clases de la GUI como contenido. Creada
 * para hacer mas modular el paso de un menu a otro y para controlar mejor como cerrar los menus.
 * 
 * FUTURO: Leer las dimensiones de la pantalla del usuario y poner todos los menus en posiciones concretas
 * y con tamaños concretos a partir de esa.
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame implements WindowListener{
	
	enum windowState {MAINMENU, PLACEBOATS, SPGAMEBOARD, MPGAMEBOARD };
	private windowState myState;
	private boolean singlePlayer;
	private boolean createdServer = false;
	private Dimension userScreenSize;
	private static LabelTipoBarco[] flotaUser;
	public static final int DIMX = 9; //Dimensiones del grid de posiciones (sera la real +1 por el extra de numeros/letras asi que 8 (9) default)
	public static final int DIMY = 9;
	public static final int[] TAMANYOBARCOSFLOTA = {1,2,2,3,4}; //Tamanyos de los barcos en la flota (solo 5? npi)
	public static int POSICIONESTOTALESBARCOS;


	public MainWindow(String title) {
		super(title);
		userScreenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		POSICIONESTOTALESBARCOS = getShipTotalPositions();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //custom closing operations...
		this.setLocation(50, 10); //Situamos la GUI en esta primera posicion
		myState = windowState.MAINMENU;
	}
	
	public static int getShipTotalPositions(){
		int totalPos = 0;
		for(int i = 0; i < TAMANYOBARCOSFLOTA.length; i++) {
			totalPos += TAMANYOBARCOSFLOTA[i];
		}
		return totalPos;
	}
	
	public void resetMPBoard(){
		if (this.myState != windowState.MPGAMEBOARD) { return; }
		PanelCombate panel = (PanelCombate)this.getContentPane();
		int decision = MainWindow.yesNoDialogSpanish("Quieres usar las mismas posiciones?", "Repetir posiciones?");
		panel.stopAll();
		if (decision == JOptionPane.YES_OPTION) { //Cambiar a que funcione...
			PanelSituaBarcos dummy = new PanelSituaBarcos(false);
			this.setContentPane(dummy);
			this.setMyState(MainWindow.windowState.PLACEBOATS);
			for (LabelTipoBarco barco : flotaUser) {
				PanelSituaBarcos.setHorizontal(barco.isHorizontal());
				PanelSituaBarcos.setTipoIDBarcoArrastrado(barco.getTamanio(), barco.getBarcoID());
				int[] drawingPos = barco.getFirstPosition();
				dummy.drawSelBoatOnGrid(drawingPos[0], drawingPos[1], true);
			}
			this.goToState(MainWindow.windowState.MPGAMEBOARD);
			
		} else {
			this.goToState(MainWindow.windowState.PLACEBOATS);
		}
	}
	
	/*Funcion para abrir un dialogo para el usuario que le dejara cerrar esta ventana, si forceQuit esta activado le hara salir
	 * obligado (lo unico es que podras cerrar o no el servidor en segundo plano si lo tienes), si no esta activado el usuario
	 * puede decir que no quiere salir	 */
	public void closeThisWOptions(boolean forceQuit){
		if (forceQuit) {
			if (createdServer) {
				closeServerDialog();
			} else  {
				System.exit(0);
			}
		} else {
			int decision = yesNoDialogSpanish("Estas seguro de querer salir?", "Salir?");
			if (decision == JOptionPane.YES_OPTION) {
				if (createdServer) {
					closeServerDialog();
				} else {
					System.exit(0);
				}
			}
		}
	}
	
	/*Funcion auxiliar ya que el codigo se repite para darle al usuario la opcion de 
	 * cerrar o no el servidor en segundo plano	 */
	private void closeServerDialog() {
		int decision = yesNoDialogSpanish("Un servidor se esta ejecutando en segundo plano.\nDeseas cerrarlo?", "Salir?");
		if (decision == JOptionPane.YES_OPTION) {
			System.exit(0);
		} else {
			this.dispose();
		}
	}
	
	/*Funcion para poner el dialogo que pide input al usuario con los textos en castellano presumiblemente, 
	 * a lo mejor expandirla si ponemos mas idiomas */
	public static int yesNoDialogSpanish(String mainText, String title) {
        UIManager.put("OptionPane.noButtonText", "Cancelar");
        UIManager.put("OptionPane.yesButtonText", "Aceptar"); //Para no poner acentos... :P
		int decision = JOptionPane.showConfirmDialog(null, mainText, title, JOptionPane.YES_NO_OPTION);
        UIManager.put("OptionPane.noButtonTetxt", "No");
        UIManager.put("OptionPane.yesButtonText", "Yes");
		return decision;
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {	}

	@Override
	public void windowClosed(WindowEvent e) { }

	@Override
	public void windowClosing(WindowEvent e) {
		closeThisWOptions(false);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) { }

	@Override
	public void windowDeiconified(WindowEvent arg0) { }

	@Override
	public void windowIconified(WindowEvent arg0) {	}

	@Override
	public void windowOpened(WindowEvent arg0) { }
	
	/*Funcion que lleva la logica de la transicion de un menu al otro, el unico que tiene 
	 * un menu anterior obligado es MP/SPGAMEBOARD	 */
	public void goToState(windowState newState){
		if (newState == windowState.PLACEBOATS) {
				this.setTitle("Situa tus barcos");
				PanelSituaBarcos content = new PanelSituaBarcos(this.singlePlayer);
				fillWindow(content);
		} 
		else if (newState == windowState.MPGAMEBOARD){ 
			if (myState != windowState.PLACEBOATS) { 
				System.out.println("Debes estar en el menu de situar barcos primero."); 
				return; 
			}
			this.setTitle("Multijugador");
			PanelSituaBarcos situator = (PanelSituaBarcos)this.getContentPane();
			PanelCombate content = new PanelCombate(situator);
			Utilities.inputConnectionToBoard(content, true);
			this.getRootPane().setDefaultButton(content.chatButton);
			content.startConnection();
			fillWindow(content, new Dimension(1100,650));
		} 
		else if (newState == windowState.SPGAMEBOARD) {
			if (myState != windowState.PLACEBOATS) { 
				System.out.println("Debes estar en el menu de situar barcos primero."); 
				return; }
			this.setTitle("Jugar contra la AI");
			
			/*FALTA RELLENAR ESTO CON EL CODIGO PERTINENTE....*/
			return;

		} 
		else if (newState == windowState.MAINMENU){ //Estamos en el main menu, cambiar si agregamos mas estados...
			this.setTitle("Menu Principal");
			MenuInicial content = new MenuInicial(this);
			fillWindow(content);
		}
		myState = newState;
	}
	
	/*Falta testearla mas, es para evitar que la GUI se haga demasiado grande...*/
	private void setMaximumSize() {
		int maxW = this.getWidth(),maxH = this.getHeight();
		userScreenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize(); //Reseteamos por si cambio...
		if (maxW >= userScreenSize.width) {
			maxW = userScreenSize.width-20;
		}
		if (maxH >= userScreenSize.height) {
			maxH = userScreenSize.height-40;
		}
		this.setPreferredSize(new Dimension(maxW,maxH));
	}
	
	public windowState getMyState() {
		return myState;
	}

	public void setMyState(windowState myState) {
		this.myState = myState;
	}
	
	public boolean hasCreatedServer() {
		return createdServer;
	}

	public void setCreatedServer(boolean createdServer) {
		this.createdServer = createdServer;
	}

	public boolean isSinglePlayer() {
		return singlePlayer;
	}

	public void setSinglePlayer(boolean singlePlayer) {
		this.singlePlayer = singlePlayer;
	}

	public static LabelTipoBarco[] getFlotaUser() {
		return flotaUser;
	}

	public static void setFlotaUser(LabelTipoBarco[] flotaUser) {
		MainWindow.flotaUser = flotaUser;
	}
	

	/*Funcion para llenar esta ventana con el contenido determinado (que ha de ser subclasse de JPanel)*/
	public void fillWindow(JPanel content){
		this.setPreferredSize(null);
		this.setContentPane(content);
		this.setVisible(true);
		this.pack();
		setMaximumSize();
	}
	
	/*Funcion para llenar la ventana con contenido y ponerle unas dimensiones determinadas*/
	public void fillWindow(JPanel content, Dimension preferredDim){
		this.setContentPane(content);
		this.setPreferredSize(preferredDim);
		this.setVisible(true);
		this.pack();
		setMaximumSize();
	}

}
