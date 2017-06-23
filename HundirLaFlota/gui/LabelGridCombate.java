package HundirLaFlota.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

/*Elementos de la grid en la GUI del combate*/
@SuppressWarnings("serial")
public class LabelGridCombate extends LabelGridBarcos implements MouseListener{

	private static PanelCombate contenedor;
	private boolean isTopGrid = false;
	
	public LabelGridCombate(int i, int j, int drawnShip, boolean horizontal, boolean topGrid){
		super(i,j);
		this.drawingShipPart = drawnShip;
		this.keepPaintingH = horizontal;
		this.isTopGrid = topGrid;
	}
	
	public LabelGridCombate(int i, int j, boolean topGrid){
		super(i,j);
		this.isTopGrid = topGrid;
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		try{
			BufferedImage imagenTransp = drawShipImage(this.isTopGrid);
			g2d.drawImage(imagenTransp, 0, 0, this.getWidth(), this.getHeight(), this);
		}catch (Exception e){
			System.out.println(e.getMessage() + " ???");
		}	
	}
	
	//NOTA: Hay que cambiar el paintComponent de LabelGrindBarcos para que si drawingShipPart = 5 => explosion creo. Ya se vera.
	
	public void mousePressed(MouseEvent mouseAction) {
		contenedor.drawAIShips(); //Cambiar de sitio a cuando comienza la ronda, esto es para testeo
	}
	public void mouseEnter(MouseEvent mouseAction) {}
	public void mouseExit(MouseEvent mouseAction) {}

	public static void setContainer(PanelCombate newCont){
		contenedor = newCont;
	}
}
