package HundirLaFlota.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class LabelGrid extends JLabel implements MouseListener, MouseMotionListener{

	private static TableroBarcos contenedor;
	private int i,j;
	private final static String BARCOIZQPATH = "img/Barco-izq.png";
	private final static String BARCODERPATH = "img/Barco-der.png";
	private final static String BARCOMEDPATH = "img/Barco-med.png";
	private final static String BARCOARRIBAPATH = "img/Barco-vert-arr.png";
	private final static String BARCOMEDVERTPATH = "img/Barco-vert-med.png";
	private final static String BARCOABAJOPATH = "img/Barco-vert-abaj.png";
	private final static String BARCOSOLOUNAPOSPATH = "img/Barco-1.png";
	private final static String BARCOSOLOUNAPOSVERTPATH = "img/Barco-1-vert.png";
	private boolean selected = false; //Para cuando hay un barco puesto ahi para siempre hasta que se resetee el board;
	private int drawingShipPart = 0;
	
	public int getDrawingShipPart(){
		return this.drawingShipPart;
	}
	
	public void setDrawingShipPart(int newDSP){
		this.drawingShipPart = newDSP;
	}
	
	public void setSelected(boolean newS){
		this.selected = newS;
	}
	
	public boolean getSelected() {
		return this.selected;
	}
	
	public int[] getCoords(){
		int[] coords = new int[2];
		coords[0] = this.i;
		coords[1] = this.j;
		return coords;
	}
	public LabelGrid(String text, int i, int j){
		super(text);
		this.i = i;
		this.j = j;
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
	}
	
	/*Cada momento que se esta arrastrando desde tablerobarcos, o bien se redibuja tal como esta o 
	 * si se esta arrastrando un barco y el raton esta dentro de la label se dibuja otra cosa.
	 * Si se comienza a arrastrar desde fuera de tablerobarcos arrastrandoBarco no sera cierto	 */
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		if (TableroBarcos.getTipoBarcoArrastrado() != 0 && this.drawingShipPart != 0){
			try {
				Image barco;
				if (TableroBarcos.isHorizontal()){ //Pasarlo a una array supongo
					switch (this.drawingShipPart){
					case 1:
						barco = ImageIO.read(new File(BARCOIZQPATH));
						break;
					case 2:
						barco = ImageIO.read(new File(BARCOMEDPATH));
						break;
					case 3:
						barco = ImageIO.read(new File(BARCODERPATH));
						break;
					default: //case barco tamanyo 1 y por si hay error (wtf)
						barco = ImageIO.read(new File(BARCOSOLOUNAPOSPATH));
					}
				} else {
					switch (this.drawingShipPart){
					case 1:
						barco = ImageIO.read(new File(BARCOARRIBAPATH));
						break;
					case 2:
						barco = ImageIO.read(new File(BARCOMEDVERTPATH));
						break;
					case 3:
						barco = ImageIO.read(new File(BARCOABAJOPATH));
						break;
					default: //case barco tamanyo 1 y por si hay error (wtf)
						barco = ImageIO.read(new File(BARCOSOLOUNAPOSVERTPATH));
					}
				}

				BufferedImage imagenTransp = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
				imagenTransp = (BufferedImage) barco;
				g2d.drawImage(imagenTransp, 0, 0, this.getWidth(), this.getHeight(), this);
				//System.out.println("dibujando?");
			}catch (Exception e){
				System.out.println(e.getMessage() + " ???");
			}	
		}
	}
	public static void setContainer(TableroBarcos newCont){
		contenedor = newCont;
	}
	
	public void toConsole(){
		System.out.println((this.i-1) + " , " + (this.j-1));
	}

	
	public void mouseClicked(MouseEvent mouseEvent) {
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		paintBoat(false);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		TableroBarcos.resetBoardLabels();
		
	}

	@Override
	public void mousePressed(MouseEvent mouseAction) {
		if (SwingUtilities.isRightMouseButton(mouseAction)){
			TableroBarcos.resetBoardLabels();
			TableroBarcos.setHorizontal(!TableroBarcos.isHorizontal());
			paintBoat(false);
		}
		//else if (TableroBarcos.isAcceptedPos()){
			//this.selected = true;
			//LabelBarco barcoElegido = TableroBarcos.findSelectedBoatLabel();
			////barcoElegido.setVisible(false);
			//paintBoat(true);
		//}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void paintBoat(boolean permanent){
		if (TableroBarcos.getTipoBarcoArrastrado() > 1){
			this.drawingShipPart = 1;
			TableroBarcos.findAffectedGridLabels(this.i, this.j, permanent);
		}
		else {
			this.drawingShipPart = 4; //Caso barco tamanyo 1
		}
		contenedor.repaint();
	}
	

}
