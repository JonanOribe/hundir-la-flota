package HundirLaFlota.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/*Elementos (Labels) de la grid en la GUI del combate*/
@SuppressWarnings("serial")
public class LabelGridCombate extends LabelGrid implements MouseListener{

	private static PanelCombate contenedor;
	private boolean isTopGrid = false;
	
	public LabelGridCombate(int i, int j, int tipo, int drawnShip, boolean horizontal, boolean topGrid){
		super(i,j);
		this.drawingShipPart = drawnShip;
		this.keepPaintingH = horizontal;
		this.isTopGrid = topGrid;
		this.barcoID = tipo;
	}
	
	public LabelGridCombate(int i, int j, boolean topGrid){
		super(i,j);
		this.isTopGrid = topGrid;
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		try{
			g2d.drawImage(getGridBackgroundImage(isTopGrid), 0, 0, this.getWidth(), this.getHeight(), this);
			if (drawForeground()) {
				g2d.drawImage(getGridForegroundImage(isTopGrid), 0, 0,this.getWidth(), this.getHeight(), this);
			}
		}catch (Exception e){
			System.out.println(e.getMessage() + " ???");
		}	
	}
	
	public boolean hasBeenShot(){
		if (drawingShipPart > 4) {
			return true;
		}
		return false;
	}
	
	/*Funcion para que se dibuje tocado o agua en la label*/
	public void setDrawHitorMiss(boolean isHit){
		if (isHit) { this.drawingShipPart = 5; } //grafico tocado
		else { this.drawingShipPart = 6;} //grafico agua
		repaint();
	}
		
	public void mousePressed(MouseEvent mouseAction) {
		//contenedor.drawAIShips(); //Cambiar de sitio a cuando comienza la ronda, esto es para testeo de la AI
		LabelGridCombate src = (LabelGridCombate)mouseAction.getSource();
		if (src.isTopGrid && !hasBeenShot()) {
			contenedor.playerActed(); //Evitar inactividad en el turno
			if (contenedor.isMyTurn()){
				contenedor.sendMsgThroughConnector("a," + this.i + "," + this.j); //Le envia al servidor "a," + la posicion de esta label en el grid (ej: a,3,5)
				if (!contenedor.pressingDelay() && contenedor.playerSentMsg){
					contenedor.writeInChat("Problemas con la conexion del otro usuario...");
					contenedor.setSeconds(61);
					contenedor.setDelay(3);
					}
			} else {
				if (!contenedor.pressingDelay() && contenedor.playerSentMsg){
					contenedor.writeInChat("No es tu turno...");
					contenedor.setDelay(3);
				}
			}
		}
		contenedor.playerSentMsg = true;
	}
	
		
	public void mouseEnter(MouseEvent mouseAction) {}
	public void mouseExit(MouseEvent mouseAction) {}

	public static void setContainer(PanelCombate newCont){
		contenedor = newCont;
	}
}
