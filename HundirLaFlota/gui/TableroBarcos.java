package HundirLaFlota.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class TableroBarcos extends JPanel{

	private static LabelBarco[] flota;
	private static int tipoBarcoArrastrado = 0;
	private static boolean horizontal = true;
	private static boolean acceptedPos = false;
	private static final int dimX = 9; //Dimensiones del grid de posiciones (sera la real +1 por el extra de numeros/letras asi que 8 (9) default)
	private static final int dimY = 9;
	public static LabelGrid[][] topGrid; //cambiar a solo usar dentro del package, la dejo publica por comodidad
	
	public TableroBarcos(){
		super();
		flota = new LabelBarco[4];
		flota[0] = new LabelBarco("img/Barco-1.png",1, this);
		flota[1] = new LabelBarco("img/Barco-2.png",2, this);
		flota[2] = new LabelBarco("img/Barco-3.png",3, this);
		flota[3] = new LabelBarco("img/Barco-4.png",4, this);
		this.setLayout(new GridLayout(2,0,10,10));
		JPanel whitePanel = new JPanel();
		whitePanel.setBackground(Color.WHITE);
		whitePanel.setPreferredSize(new Dimension(200,200));
		int dim1 = 4;
		int dim2 = 4;
		whitePanel.setLayout(new GridLayout(dim1,dim2,10,10));
		for (int i = 0; i < flota.length; i++){
			whitePanel.add(flota[i]);
		}
		whitePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		for (int i = 0; i < (dim1*dim2)-4; i++){
			whitePanel.add(new JLabel("BARCO"));
		}
		
		TableroTop topPanel = new TableroTop(this, dimX, dimY);
		this.add(topPanel);
		this.add(whitePanel);
		//b1.setBounds(10,10,200,200);
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
	}
	
	/*No hay inserciones diagonales, solo setea para que se dibujen las labels adyacentes en el grid
	 * en vertical o en horizontal y les dice que dibujar, si no se pueden dibujar alguno de los trozos
	 * porque esta fuera del grid el booleano acceptedPos lo avisa. (se llama a esta funcion al hacer
	 * onMouseEnter a una label de la grid de arriba  con un barco seleccionado, la propia label setea sus valores
	 * por si misma esta funcion las adyacentes). El reset a lo de dibujar se produce onMouseExit de la label con la funcion
	 * de abajo */
	public static void findAffectedGridLabels(int firstGridRow, int firstGridCol, boolean selecting){
		int i, tmp, dim, draw;
		if (horizontal) { i = tipoBarcoArrastrado-1; tmp = firstGridCol;  dim = dimX;  }
		else { i = tipoBarcoArrastrado-1; tmp = firstGridRow; dim = dimY; }
		while ( i > 0) {
			if (i == 1) { draw = 3; }
			else { draw = 2;}
			tmp++;
			if (tmp < (dim)){
				//System.out.println("Drawing to Row: " + firstGridRow + " , Col: " + firstGridCol + " , dim (x): " + dimX);
				acceptedPos = true;
				if (horizontal) { 
					topGrid[firstGridRow-1][tmp-1].setDrawingShipPart(draw);
				} else {
					topGrid[tmp-1][firstGridCol-1].setDrawingShipPart(draw);
				}	
			}
			else {
				acceptedPos = false;
				break;
			}
			i--;
		}
	}
	
	public static void resetBoardLabels(){
		for (int i = 0; i < topGrid.length; i++){
			for (int j = 0; j < topGrid[i].length; j++){
				if (!topGrid[i][j].getSelected()) {
					topGrid[i][j].setDrawingShipPart(0);
				}
			}
		}
	}
	
	//seguramente podria hacerse de otra forma?
	public static void resetBoatLabels(){
		for (int i = 0; i < flota.length; i++){
			flota[i].setBorder(null);
			flota[i].setSeleccionada(false);
		}
	}
	
	public static LabelBarco findSelectedBoatLabel(){
		for (int i = 0; i < flota.length; i++){
			if (flota[i].getSeleccionada()) { 
				return flota[i];
				}
		}
		return null;
	}
	
	public static int getTipoBarcoArrastrado() {
		return tipoBarcoArrastrado;
	}

	public static void setTipoBarcoArrastrado(int tipoBarcoArrastrado) {
		TableroBarcos.tipoBarcoArrastrado = tipoBarcoArrastrado;
	}

	public static boolean isHorizontal() {
		return horizontal;
	}

	public static void setHorizontal(boolean horizontal) {
		TableroBarcos.horizontal = horizontal;
	}

	public static boolean isAcceptedPos() {
		return acceptedPos;
	}

	public static void setAcceptedPos(boolean acceptedPos) {
		TableroBarcos.acceptedPos = acceptedPos;
	}


	public static void main(String[] args){
		JFrame window = new JFrame("test");
		TableroBarcos content = new TableroBarcos();
		window.setContentPane(content);
		window.setPreferredSize(new Dimension(800,600));
		window.setVisible(true);
		window.pack();
	}
}
