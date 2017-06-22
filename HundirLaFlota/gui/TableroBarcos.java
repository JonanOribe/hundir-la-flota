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
	private static LabelGrid[] lastLabelsDrawn; //Ultimas labels pintadas, usado para borrar sin tener que ciclar todas las posiciones
	private static int tipoBarcoArrastrado = 0;
	private static boolean horizontal = true;  //Dibuja los barcos (no permanentemente dibujados) en horiz/vertical
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
	 * onMouseEnter a una label de la grid superior con un barco seleccionado, esta funcion setea los valores
	 * para que se dibujen correctamente la label elegida i las adyacentes). Se llama con drawPermanently cierto desde
	 * mouseClick y pone los valores permanentes de la label entonces*/
	public static void findAffectedGridLabels(int firstGridRow, int firstGridCol, boolean drawPermanently){
		int i, counter, tmp, dim, draw;
		if (horizontal) { tmp = firstGridCol;  dim = dimX;  }
		else { tmp = firstGridRow; dim = dimY; }
		i = tipoBarcoArrastrado-1;
		lastLabelsDrawn = new LabelGrid[tipoBarcoArrastrado];
		lastLabelsDrawn[0] = topGrid[firstGridRow-1][firstGridCol-1];
		if (!topGrid[firstGridRow-1][firstGridCol-1].hayBarcoPermanente()) {
			acceptedPos = true;
			if (i == 0) { topGrid[firstGridRow-1][firstGridCol-1].setDrawingShipPart(4, drawPermanently); return;} //Si es un barco de un solo elemento...
			else { topGrid[firstGridRow-1][firstGridCol-1].setDrawingShipPart(1, drawPermanently); } // Sino, pinta la label en la que esta el raton con el grafico de mas a la izkierda
		} 
		else { 
			acceptedPos = false; 
		}
		counter = 1;
		while ( i > 0) {
			if (i == 1) { draw = 3; }
			else { draw = 2;}
			tmp++;
			if (tmp < (dim)){
				//System.out.println("Drawing to Row: " + firstGridRow + " , Col: " + firstGridCol + " , dim (x): " + dimX);
				if (horizontal) { 
					if (!topGrid[firstGridRow-1][tmp-1].hayBarcoPermanente()){
						topGrid[firstGridRow-1][tmp-1].setDrawingShipPart(draw, drawPermanently);
						lastLabelsDrawn[counter] = topGrid[firstGridRow-1][tmp-1];
					} else {
						acceptedPos = false;
					}
				} else {
					if (!topGrid[tmp-1][firstGridCol-1].hayBarcoPermanente()){
						topGrid[tmp-1][firstGridCol-1].setDrawingShipPart(draw, drawPermanently);
						lastLabelsDrawn[counter] = topGrid[tmp-1][firstGridCol-1];
					} else {
						acceptedPos = false;
					}
				}
				counter++;
			}
			else {
				acceptedPos = false;
				break;
			}
			i--;
		}
	}
	
	/*Intenta dibujar como vacias las labels guardadas como pintadas en el ultimo ciclo
	 * (si estan dibujadas permanentemente no podra pero por la funcion) y pone acceptedPos en falso*/
	public static void cleanBoatPaintedLabels(){
		if (lastLabelsDrawn == null) { return;}
		for (LabelGrid label : lastLabelsDrawn){
			if (label != null){
				label.setDrawingShipPart(0, false);
			}
		}
		acceptedPos = false;
	}
	
	//No usada, pero puede que en el futuro para resetear el tablero... (usar resetDraw())
	public static void resetBoardLabels(){
		for (int i = 0; i < topGrid.length; i++){
			for (int j = 0; j < topGrid[i].length; j++){
				if (!topGrid[i][j].hayBarcoPermanente()) {
					topGrid[i][j].setDrawingShipPart(0, false);
				}
			}
		}
	}
	
	/*Funcion usada para pasar las labels con las posiciones del barco dibujado permanentemente
	 * a la labeldebarco que le corresponde (que ahora tendra el icono de cancelar el posicionamiento)
	 * para que asi si se aprieta otra vez sepa que labels resetear. Resetea tambien las labels guardadas
	 * ya que no las necesitamos mas */
	public static void passCoordsToBarcoLabel(LabelBarco LB){
		LB.setLabelsBarco(lastLabelsDrawn);
		lastLabelsDrawn = null;
	}
	
	//Podria hacerse de otra forma pero para 4-6 elementos total esta bien
	public static void resetBoatLabels(){
		for (int i = 0; i < flota.length; i++){
			flota[i].setBorder(null);
			flota[i].setSeleccionada(false);
		}
	}
	
	/*Encuentra el barco seleccionado por el usuario actualmente*/
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
		window.setPreferredSize(new Dimension(700,700));
		window.setVisible(true);
		window.pack();
	}
}
