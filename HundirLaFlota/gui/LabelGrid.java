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

	
	private final static String BARCOIZQPATH = "img/Barco-izq.png";
	private final static String BARCODERPATH = "img/Barco-der.png";
	private final static String BARCOMEDPATH = "img/Barco-med.png";
	private final static String BARCOARRIBAPATH = "img/Barco-vert-arr.png";
	private final static String BARCOMEDVERTPATH = "img/Barco-vert-med.png";
	private final static String BARCOABAJOPATH = "img/Barco-vert-abaj.png";
	private final static String BARCOSOLOUNAPOSPATH = "img/Barco-1.png";
	private final static String BARCOSOLOUNAPOSVERTPATH = "img/Barco-1-vert.png";
	
	private static TableroBarcos contenedor;
	private int i,j;
	private boolean hayBarco = false;
	private boolean keepPaintingH = true; //Si hay un barco de manera permanente en la posicion mantiene el valor en el que hay que dibujarlo para siempre sino se updatea al apretar el raton
	private int drawingShipPart = 0;
	
	public LabelGrid(String text, int i, int j){
		super(text);
		this.i = i;
		this.j = j;
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
	}
	
	/*Cada vez que se redibuje el componente dibujara su imagen correspondiente dependiendo de los datos
	 * existentes en el tablero y en la propia casilla. */
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		if ( (TableroBarcos.getTipoBarcoArrastrado() != 0 && this.drawingShipPart != 0 ) || this.hayBarco){
			try {
				Image barco;
				if (this.keepPaintingH){ //Pasarlo a una array supongo
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
		System.out.println((this.i) + " , " + (this.j));
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
		TableroBarcos.cleanBoatPaintedLabels();
	}

	/*Al apretar el raton, si hemos elegido un barco de los de abajo (barcoArrastrado != 0) o bien
	 * cambiara su orientacion horiz/vert al apretar mouse derecho o intentara dibujarlo permanentemente
	 * si estamos en una posicion acceptada (que se determina al hacer mouseEnter en cada casilla, mirandola
	 * a ella y las adyacentes)	 */
	@Override
	public void mousePressed(MouseEvent mouseAction) {
		if (TableroBarcos.getTipoBarcoArrastrado() != 0) { //Si estamos arrastrando algun barco...
			if (SwingUtilities.isRightMouseButton(mouseAction)){
				TableroBarcos.cleanBoatPaintedLabels();
				TableroBarcos.setHorizontal(!TableroBarcos.isHorizontal());
				paintBoat(false);
				contenedor.repaint();
			}
			else if (TableroBarcos.isAcceptedPos()){
				LabelBarco barcoElegido = TableroBarcos.findSelectedBoatLabel();
				barcoElegido.setCancelIcon();
				TableroBarcos.cleanBoatPaintedLabels();
				paintBoat(true);
				TableroBarcos.passCoordsToBarcoLabel(barcoElegido);
				TableroBarcos.setTipoBarcoArrastrado(0);
				contenedor.repaint();
			}		
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean hayBarcoPermanente(){
		return this.hayBarco;
	}
	
	/*Pintara el barco en esta casilla y sus adyacentes dependiendo de si es en horizontal o vertical, si
	 * permanente es cierto se pintara el barco para siempre en estas casillas si son posiciones legales.*/
	private void paintBoat(boolean permanent){
		if (TableroBarcos.getTipoBarcoArrastrado() != 0) {
			TableroBarcos.findAffectedGridLabels(this.i, this.j, permanent);
			contenedor.repaint();
		}
	}
	
	public int getDrawingShipPart(){
		return this.drawingShipPart;
	}
	
	/*Solo cambia el grafico del barco si el booleano hayBarco (permanentemente) es falso, es decir que el usuario
	 * no ha elegido esta posicion para situar un barco ya. Para volver a poder cambiar el grafico de esta label
	 * si ha pasado eso hay que llamar a resetDraw()*/
	public void setDrawingShipPart(int newDSP, boolean permanent){
		if (!this.hayBarco) {
			this.drawingShipPart = newDSP;
			this.hayBarco = permanent;
			this.keepPaintingH = TableroBarcos.isHorizontal();
		}
	}
	/*Usada cuando se quiere que la label resetee su grafico y pueda volver a dibujar barcos por encima de ella
	 * es decir que si habia un barco de manera permanente en esta posicion se olvidara de el y se kedara vacia.	 */
	public void resetDraw(){
		this.drawingShipPart = 0;
		this.hayBarco = false;
		this.keepPaintingH = true;
	}
	
	/*Para obtener sus posiciones en la matriz (habra que restarle 1 debido a que las primeras posiciones
	 * no cuentan ya que son A...Z y 1...9, asi que o dejarlo asi o cambiarlo en el futuro.*/
	public int[] getCoords(){
		int[] coords = new int[2];
		coords[0] = this.i;
		coords[1] = this.j;
		return coords;
	}
	

}
