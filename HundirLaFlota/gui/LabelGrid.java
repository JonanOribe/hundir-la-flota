package HundirLaFlota.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


@SuppressWarnings("serial")
public class LabelGrid extends JLabel implements MouseListener{

	
	protected final static String BARCOIZQPATH = "img/Barco-izq.png";
	protected final static String BARCODERPATH = "img/Barco-der.png";
	protected final static String BARCOMED1PATH = "img/Barco-med1.png";
	protected final static String BARCOMED2PATH = "img/Barco-med2.png";
	protected final static String BARCOARRIBAPATH = "img/Barco-vert-arr.png";
	protected final static String BARCOMEDVERT1PATH = "img/Barco-vert-med1.png";
	protected final static String BARCOMEDVERT2PATH = "img/Barco-vert-med2.png";
	protected final static String BARCOABAJOPATH = "img/Barco-vert-abaj.png";
	protected final static String BARCOSOLOUNAPOSPATH = "img/Lancha.png";
	protected final static String BARCOSOLOUNAPOSVERTPATH = "img/Lancha.png";
	protected final static String EXPLOSION = "img/Explosion.png";
	protected final static String AGUA = "img/fallo.jpg"; //para disparos fallidos...
	protected final static String MAR1 = "img/mar1.jpg";
	protected final static String MAR2 = "img/mar2.jpg";

	
	private static PanelSituaBarcos contenedor;   //Referencia a la clase que lo contiene para poder leer y cambiar sus datos
	
	protected int i,j;
	private boolean hayBarco = false;
	protected boolean keepPaintingH = true; //Si hay un barco de manera permanente en la posicion mantiene el valor en el que hay que dibujarlo para siempre sino se updatea al apretar el raton
	protected int drawingShipPart = 0;
	protected int barcoID;
	
	public LabelGrid(int i, int j){
		super("");
		this.i = i;
		this.j = j;
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.addMouseListener(this);
	}
	
	/*Cada vez que se redibuje el componente dibujara su imagen correspondiente dependiendo de los datos
	 * existentes en el tablero y en la propia casilla. */
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		g2d.drawImage(getGridBackgroundImage(false), 0, 0, this.getWidth(), this.getHeight(), this);
		if ( (PanelSituaBarcos.getTipoBarcoArrastrado() != 0 && this.drawingShipPart != 0 ) || this.hayBarco){
			try{
				g2d.drawImage(getGridForegroundImage(false), 0, 0,this.getWidth(), this.getHeight(), this);
			}catch (Exception e){
				System.out.println(e.getMessage() + " ???");
			}	
		}
	}
	
	/*Funcion para determinar si hay que dibujar la capa de background (mar) y una
	 * segunda (p.ej: barco) o no */
	protected boolean drawForeground(){
		return (this.drawingShipPart != 0);
	}
	
	/*Funcion que retorna verdadero si la posicion tiene un trozo de barco, depende
	 * de la variable interna drawingShipPart	 */
	protected boolean hasAShipPart(){
		if (drawingShipPart >= 1 && drawingShipPart <= 5){
			return true;
		}
		return false;
	}
	
	
	protected BufferedImage getGridBackgroundImage(boolean isTopGrid){
		try {
			Image fondo;
			if (isTopGrid) { fondo = (this.i == MainWindow.DIMX-1) ?  ImageIO.read(new File(MAR2)) : ImageIO.read(new File(MAR1)); } 
			else { fondo = (this.i == 1) ?  ImageIO.read(new File(MAR2)) : ImageIO.read(new File(MAR1)); }
			BufferedImage fondoImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			fondoImg = (BufferedImage)fondo;
			return fondoImg;
			}
		catch (Exception e){
			System.out.println("Error al dibujar la imagen de fondo. " + e.getMessage());
			return null;
		}	
	}
	
	public boolean isAgua() {
		return (this.drawingShipPart == 0 || this.drawingShipPart == 6); //Sacar numeros magicos, esto es o no barco o si dibuja fallo en el disparo oponente
	}

	/*Funcion para encontrar la imagen correcta a dibujar del barco en su coordenada, la 
	 * la comprobacion si hay que dibujar es externa a esta	 
	 * He dejado agua y tocado en foreground por si en el futuro queremos poner dos capas,
	 * el agua y la explosion*/
	protected BufferedImage getGridForegroundImage(boolean isTopGrid){
			try {
				Image barco = null;
				//Asignacion de la imagen de fondo, cambiar a no usar numeros magicos...
				switch (this.drawingShipPart){ //Esto elegira la imagen superpuesta a la de fondo
				case 0:  
					break;
				case 1:
					barco = (this.keepPaintingH) ? ImageIO.read(new File(BARCOIZQPATH)) : ImageIO.read(new File(BARCOARRIBAPATH));
					break;
				case 2:
					barco = (this.keepPaintingH) ? ImageIO.read(new File(BARCOMED1PATH)) : ImageIO.read(new File(BARCOMEDVERT1PATH));
					break;
				case 3:
					barco = (this.keepPaintingH) ? ImageIO.read(new File(BARCOMED2PATH)) : ImageIO.read(new File(BARCOMEDVERT2PATH));
					break;
				case 4:
					barco = (this.keepPaintingH) ? ImageIO.read(new File(BARCODERPATH)) : ImageIO.read(new File(BARCOABAJOPATH));
					break;
				case 5: //case barco tamanyo 1
					barco = (this.keepPaintingH) ? ImageIO.read(new File(BARCOSOLOUNAPOSPATH)) :  ImageIO.read(new File(BARCOSOLOUNAPOSVERTPATH));
					break;
				case 6: //case tocado
					barco = (this.keepPaintingH) ? ImageIO.read(new File(EXPLOSION)) :  ImageIO.read(new File(EXPLOSION));
					break;
				case 7: //case agua 
					barco = (this.keepPaintingH) ? ImageIO.read(new File(AGUA)) :  ImageIO.read(new File(AGUA));
					break;
				default:
				}
				BufferedImage imagenFinal = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
				imagenFinal = (barco == null) ? null : (BufferedImage)barco;
				return imagenFinal;
			}catch (Exception e){
				System.out.println("Error al dibujar las imagenes superpuestas. " + e.getMessage());
				return null;
			}	
	}
		
	public static void setContainer(PanelSituaBarcos newCont){
		contenedor = newCont;
	}
	
	public void toConsole(){
		System.out.println((this.i) + " , " + (this.j));
	}

	
	public void mouseClicked(MouseEvent mouseEvent) {	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		paintBoat(false);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		PanelSituaBarcos.cleanBoatPaintedLabels();
	}

	/*Al apretar el raton, si hemos elegido un barco de los de abajo (barcoArrastrado != 0) o bien
	 * cambiara su orientacion horiz/vert al apretar mouse derecho o intentara dibujarlo permanentemente
	 * si estamos en una posicion acceptada (que se determina al hacer mouseEnter en cada casilla, mirandola
	 * a ella y las adyacentes)	 */
	@Override
	public void mousePressed(MouseEvent mouseAction) {
		if (PanelSituaBarcos.getTipoBarcoArrastrado() != 0) { //Si estamos arrastrando algun barco...
			if (SwingUtilities.isRightMouseButton(mouseAction)){
				PanelSituaBarcos.cleanBoatPaintedLabels();
				PanelSituaBarcos.setHorizontal(!PanelSituaBarcos.isHorizontal());
				paintBoat(false);
				contenedor.repaint();
			}
			else if (PanelSituaBarcos.isAcceptedPos()){
				LabelTipoBarco barcoElegido = PanelSituaBarcos.findSelectedBoatLabel();
				barcoElegido.setCancelIcon();
				PanelSituaBarcos.cleanBoatPaintedLabels();
				paintBoat(true);
				PanelSituaBarcos.passCoordsToBarcoLabel(barcoElegido);
				PanelSituaBarcos.setTipoIDBarcoArrastrado(0,0);
				contenedor.checkAceptarButton();
				contenedor.repaint();
			}		
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {	}
	
	public boolean hayBarcoPermanente(){
		return this.hayBarco;
	}
	
	/*Pintara el barco en esta casilla y sus adyacentes dependiendo de si es en horizontal o vertical, si
	 * permanente es cierto se pintara el barco para siempre en estas casillas si son posiciones legales.*/
	private void paintBoat(boolean permanent){
		if (PanelSituaBarcos.getTipoBarcoArrastrado() != 0) {
			contenedor.drawSelBoatOnGrid(this.i, this.j, permanent);
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
			if (permanent){	this.barcoID = PanelSituaBarcos.getIDBarcoArrastrado(); }
			this.keepPaintingH = PanelSituaBarcos.isHorizontal();
		}
	}
	/*Usada cuando se quiere que la label resetee su grafico y pueda volver a dibujar barcos por encima de ella
	 * es decir que si habia un barco de manera permanente en esta posicion se olvidara de el y se kedara vacia.	 */
	public void resetDraw(){
		this.drawingShipPart = 0;
		this.hayBarco = false;
		this.barcoID = 0;
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
	
	public int getBarcoID() {
		return barcoID;
	}
	
	public boolean isHorizontal(){
		return this.keepPaintingH;
	}

	public void setBarcoID(int barcoID) {
		this.barcoID = barcoID;
	}

	
	public LabelGridCombate exportToLGC(){
		if (this.hayBarco){
			return new LabelGridCombate(this.i, this.j, this.barcoID, this.drawingShipPart, this.keepPaintingH, false);
		}
		else {
			return new LabelGridCombate(this.i,this.j, false);
		}
	}
}
