package HundirLaFlota.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import HundirLaFlota.misc.Utilities;

@SuppressWarnings("serial")
public class LabelBarco extends JLabel implements MouseListener, MouseMotionListener{

	private final static String CRUZIMG = "img/cruzroja.png";
	
	private String imagePath;
	private int tamanio; 
	private boolean cancel = false;
	private TableroBarcos contenedor; //Referencia al tablero
	private LabelGrid[] labelsBarco;
	private boolean seleccionada; //Para saber si la label ha estado seleccionada ya
	private int initialW = 0, initialH = 0; //Usado al resizear el tablero para poner el dibujo del tamanyo k toke
	
	public LabelBarco (String imagePath, int tamanio, TableroBarcos contenedor) {
		super(new ImageIcon(imagePath), JLabel.LEFT);
		try {
			this.imagePath = imagePath;
			this.tamanio = tamanio;
			this.contenedor = contenedor;
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			
		}catch (Exception e){
			System.out.println("Error al cargar la imagen del barco " + e.getMessage());
		}
	}
	
	/*Por ahora cada vez que se cambia el tamaño de la GUI se redibuja adaptandose al tamaño de su contenedor*/
	public void paintComponent(Graphics g){
		if (initialW != this.getWidth() || initialH != this.getHeight()){
			this.setIcon(Utilities.scaleIconTo(new ImageIcon(imagePath),0,0,this.getWidth(), this.getHeight()));
			this.setVisible(true);
			initialW = this.getWidth();
			initialH = this.getHeight();
		}
		super.paintComponent(g);
	}
	
	
	public static void main(String[] args){
		//System.out.println(System.getProperty("user.dir"));
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

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
				TableroBarcos.cleanBoatPaintedLabels();
				TableroBarcos.resetBoatLabels();
				TableroBarcos.setTipoBarcoArrastrado(this.tamanio);
				this.seleccionada = true;
				this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			else {
				TableroBarcos.cleanBoatPaintedLabels();
				TableroBarcos.setTipoBarcoArrastrado(0);
				TableroBarcos.setAcceptedPos(false);
				this.seleccionada = !this.seleccionada;
				this.setBorder(null);
			}
			contenedor.repaint();
		}
		else { //Si se quiere cancelar el posicionamiento del barco
			this.setIcon(Utilities.scaleIconTo(new ImageIcon(imagePath), 0,0,this.getWidth(),this.getHeight()));
			this.setHorizontalAlignment(JLabel.LEFT);
			this.cancel = false;
			System.out.println("Reseteando posicion, el barco estaba en las coordenadas: "); //Comprobacion simple
			for (int i = 0; i < labelsBarco.length; i++){
				labelsBarco[i].toConsole();    //Eliminar en el futuro
				labelsBarco[i].resetDraw();
			}
			contenedor.repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
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

	
	public void setCancelIcon(){
		this.cancel = true;
		this.setBorder(null);
		this.seleccionada = false;
		this.setIcon(Utilities.scaleIconTo(new ImageIcon(CRUZIMG), 0,0,this.getWidth()/4,this.getHeight()));
		this.setHorizontalAlignment(JLabel.CENTER);
	}
}
