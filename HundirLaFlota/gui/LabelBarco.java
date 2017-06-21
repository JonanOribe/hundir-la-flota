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

	private String imagePath;
	private int tamanio; 
	private int barcosAPoner = 1;
	private TableroBarcos contenedor; //Referencia al tablero
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
		if (!seleccionada){
			TableroBarcos.resetBoatLabels();
			TableroBarcos.setTipoBarcoArrastrado(this.tamanio);
			this.seleccionada = true;
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		else {
			TableroBarcos.resetBoatLabels();
			TableroBarcos.setTipoBarcoArrastrado(0);
			TableroBarcos.setAcceptedPos(false);
			this.setBorder(null);
		}
		contenedor.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
	
	public int getBarcosAPoner(){
		return this.barcosAPoner;
	}
	
	public void setBarcosAPoner(int newBAP){
		this.barcosAPoner = newBAP;
	}
}
