package HundirLaFlota.gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import HundirLaFlota.ai.BoatHandling;
import HundirLaFlota.misc.Utilities;

/*Panel que contendra los paneles con la grid del usuario y un contrincante y gestionara
 * la comunicacion entre estos */
@SuppressWarnings("serial")
public class PanelCombate extends JPanel{
	
	private LabelGridCombate[][] gridCoordsTop, gridCoordsBot;

	public PanelCombate(int dimX, int dimY, PanelSituaBarcos ancestor){
		super();
		this.setLayout(new GridLayout(2,1,10,10));
		LabelGridCombate.setContainer(this);
		JPanel topPanel = new JPanel();
		topPanel.setLayout((new GridLayout(dimX,dimY,-1,-1)));
		JPanel botPanel = new JPanel();
		botPanel.setLayout((new GridLayout(dimX,dimY,-1,-1)));
		gridCoordsTop = Utilities.createCGrid(dimX,dimY,topPanel,null);
		gridCoordsBot = Utilities.createCGrid(dimX,dimY,botPanel,ancestor.exportaGrid()); //Creamos la grid para dibujarla en el panel con valores 0
		this.add(topPanel);

		this.add(botPanel);
	
}

	public LabelGridCombate[][] getGridCoordsTop() {
		return gridCoordsTop;
	}
	
	public void setGridCoordsTop(LabelGridCombate[][] gridCoords) {
		this.gridCoordsTop = gridCoords;
	}
	
	public LabelGridCombate[][] getGridCoordsBot() {
		return gridCoordsBot;
	}
	
	public void setGridCoordsBot(LabelGridCombate[][] gridCoords) {
		this.gridCoordsBot = gridCoords;
	}
	
	public void drawAIShips(){ //cambiar a private, etc.
		BoatHandling.placeAllBoatsOnGrid(gridCoordsTop);
	}
	
	public static void startNewCombat(PanelSituaBarcos situator){
		JFrame window = new JFrame("test combate");
		PanelCombate content = new PanelCombate(9,9,situator);
		window.setContentPane(content);
		window.setPreferredSize(new Dimension(700,700));
		window.setVisible(true);
		window.pack();
	}
	
	public static void main(String[] args){ /*codigo de testeo standalone*/
		PanelSituaBarcos situator = new PanelSituaBarcos();
		PanelSituaBarcos.setTipoBarcoArrastrado(2);
		situator.drawSelBoatOnGrid(2,4,true); //Testing
		PanelSituaBarcos.setTipoBarcoArrastrado(3);
		situator.drawSelBoatOnGrid(5,2,true);
		startNewCombat(situator);
	}
}
