package HundirLaFlota.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import HundirLaFlota.misc.Utilities;

@SuppressWarnings("serial")
public class LabelTipoBarco extends JLabel implements MouseListener{

	private final static String CRUZIMG = "img/cruzroja.png";
	private static int barcosIDs = 1; //Variable que va asignando un unico valor a cada barco
	
	private String imagePath;
	private int tamanio; 
	private boolean cancel = false;
	private PanelSituaBarcos contenedor; //Referencia al tablero
	private LabelGrid[] labelsBarco;
	private boolean seleccionada; //Para saber si la label ha estado seleccionada ya
	private int initialW = 0, initialH = 0; //Usado al resizear el tablero para poner el dibujo del tamanyo k toke
	private int myBarcoID;
	
	public LabelTipoBarco (String imagePath, int tamanio, PanelSituaBarcos contenedor) {
		super(new ImageIcon(imagePath), JLabel.LEFT);
		try {
			this.imagePath = imagePath;
			this.tamanio = tamanio;
			this.contenedor = contenedor;
			this.myBarcoID = barcosIDs++;
			this.addMouseListener(this);
			
		}catch (Exception e){
			System.out.println("Error al cargar la imagen del barco " + e.getMessage());
		}
	}
	
	/*Por ahora cada vez que se cambia el tamaño de la GUI se redibuja adaptandose al tamaño de su contenedor*/
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if ((initialW != this.getWidth() || initialH != this.getHeight()) && !cancel){
			this.setIcon(Utilities.scaleIconTo(new ImageIcon(imagePath),0,0,this.getWidth(), this.getHeight()));
			this.setVisible(true);
			updateSize();
		}
	}
	
	public int[] getFirstPosition(){
		return this.labelsBarco[0].getCoords();
	}
	
	public boolean isHorizontal(){
		return this.labelsBarco[0].isHorizontal();
	}
	
	public int getTamanio(){
		return this.tamanio;
	}
	
	private void updateSize(){
		initialW = this.getWidth();
		initialH = this.getHeight();
	}
	public static void main(String[] args){
		//System.out.println(System.getProperty("user.dir"));
	}

	@Override
	public void mouseClicked(MouseEvent e) {	}

	@Override
	public void mouseEntered(MouseEvent e) {	}

	@Override
	public void mouseExited(MouseEvent e) {		}

	/*Al apretar el mouse, si la label no tiene el valor de cancelar determina si habia sido apretada
	 * antes (con el booleano seleccionar), sino se selecciona y elige este tipo de barco como el que
	 * se dibujara en el tablero superior, si estaba seleccionada se deselecciona.
	 * En el caso que el barco se haya situado bien en el tablero la label tendra una icona de cancelar
	 * y al apretarse borrara el dibujo del barco permanente y volvera a poner su icono normal (resetea
	 * posicion barco)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (!cancel){
			if (!seleccionada){
				PanelSituaBarcos.cleanBoatPaintedLabels();
				PanelSituaBarcos.resetBoatLabels();
				PanelSituaBarcos.setTipoIDBarcoArrastrado(this.tamanio,this.myBarcoID);
				this.seleccionada = true;
				this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				this.setIcon(Utilities.scaleIconTo(new ImageIcon(imagePath),0,0,this.getWidth()-2, this.getHeight()-2));
			}
			else {
				PanelSituaBarcos.cleanBoatPaintedLabels();
				PanelSituaBarcos.setTipoIDBarcoArrastrado(0,0);
				PanelSituaBarcos.setAcceptedPos(false);
				this.seleccionada = !this.seleccionada;
				this.setBorder(null);
			}
			contenedor.repaint();
		}
		else { //Si se quiere cancelar el posicionamiento del barco
			cancelBoatPosition();
			contenedor.repaint();
		}
	}
	
	public void cancelBoatPosition(){
		if (cancel){
			this.setIcon(Utilities.scaleIconTo(new ImageIcon(imagePath), 0,0,initialW,initialH));
			this.setHorizontalAlignment(JLabel.LEFT);
			this.cancel = false;
			for (int i = 0; i < labelsBarco.length; i++){
				labelsBarco[i].resetDraw();
			}
			contenedor.checkAceptarButton();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {	}
	
	public void setSeleccionada(boolean newS){
		this.seleccionada = newS;
	}
	
	public boolean getSeleccionada(){
		return this.seleccionada;
	}
	
	public LabelGrid[] getLabelsBarco() {
		return labelsBarco;
	}

	public void setLabelsBarco(LabelGrid[] labelsBarco) {
		this.labelsBarco = labelsBarco;
	}
	
	public void listStoredLabelCoords(){
		for (int i = 0; i < labelsBarco.length; i++){
			labelsBarco[i].toConsole();
		}
	}
	
	public int[][] getStoredLabelCoords() {
		int[][] posiciones = new int[labelsBarco.length][2];
		for (int i = 0; i < labelsBarco.length; i++) {
			posiciones[i] = labelsBarco[i].getCoords();
		}
		return posiciones;
	}
	
	public boolean hasChosenPos(){
		return this.cancel;
	}
	
	public int getBarcoID() {
		return this.myBarcoID;
	}
	
	public void setCancelIcon(){
		this.cancel = true;
		this.setBorder(null);
		this.seleccionada = false;
		this.setIcon(Utilities.scaleIconTo(new ImageIcon(CRUZIMG), 0,0,this.getWidth()/2,this.getHeight()));
		this.setHorizontalAlignment(JLabel.CENTER);
	}
}
