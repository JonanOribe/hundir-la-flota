package HundirLaFlota.gui;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import HundirLaFlota.misc.Utilities;

@SuppressWarnings("serial")
public class PanelSituaBarcos extends JPanel{

	private static LabelTipoBarco[] flota;
	private static LabelGridBarcos[] lastLabelsDrawn; //Ultimas labels pintadas, usado para borrar sin tener que ciclar todas las posiciones
	private static int tipoBarcoArrastrado = 0;
	private static boolean horizontal = true;  //Dibuja los barcos (no permanentemente dibujados) en horiz/vertical
	private static boolean acceptedPos = false; 
	public static final int dimX = 9; //Dimensiones del grid de posiciones (sera la real +1 por el extra de numeros/letras asi que 8 (9) default)
	public static final int dimY = 9;
	private String chosenIP; //For playing MP...
	private int chosenPort;
	private LabelGridBarcos[][] topGrid; //Referencia al grid de arriba con las posiciones deseadas de los barcos
	private JButton BotonAceptar;
	
	public PanelSituaBarcos(){
		super();
		initComponents();
	}
	
	public PanelSituaBarcos(String IP, int port){
		this();
		this.chosenIP = IP;
		this.chosenPort = port;
	}
                      
	   	private void initComponents() {
			LabelGridBarcos.setContainer(this);	

	    	flota = new LabelTipoBarco[4];
			flota[0] = new LabelTipoBarco("img/Barco-1.png",1, this);
			flota[1] = new LabelTipoBarco("img/Barco-2.png",2, this);
			flota[2] = new LabelTipoBarco("img/Barco-3.png",3, this);
			
			flota[3] = new LabelTipoBarco("img/Barco-4.png",4, this);
			JPanel PanelBarcos = new JPanel();
			JPanel PanelGrid = new JPanel();
			JPanel allContainerPanel = new JPanel();
	        JPanel PanelBotones = new javax.swing.JPanel();

	        javax.swing.GroupLayout PanelBarcosLayout = new javax.swing.GroupLayout(PanelBarcos);
	        PanelBarcos.setLayout(PanelBarcosLayout);
	        PanelBarcosLayout.setHorizontalGroup(
	            PanelBarcosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGap(0, 256, Short.MAX_VALUE)
	        );
	        PanelBarcosLayout.setVerticalGroup(
	            PanelBarcosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGap(0, 100, Short.MAX_VALUE)
	        );

	        javax.swing.GroupLayout PanelGridLayout = new javax.swing.GroupLayout(PanelGrid);
	        PanelGrid.setLayout(PanelGridLayout);
	        PanelGridLayout.setHorizontalGroup(
	            PanelGridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGap(0, 0, Short.MAX_VALUE)
	        );
	        PanelGridLayout.setVerticalGroup(
	            PanelGridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGap(0, 127, Short.MAX_VALUE)
	        );

	        BotonAceptar = new JButton("Aceptar");
	        BotonAceptar.setVisible(false);
	        
	        BotonAceptar.addActionListener(new ActionListener () {
       	    	public void actionPerformed (ActionEvent e) {
       	    		JButton src = (JButton)e.getSource();
       	    		JFrame contenedor = (JFrame)src.getTopLevelAncestor();
       	    		PanelSituaBarcos thisPanel = (PanelSituaBarcos)contenedor.getContentPane();
       	    		PanelCombate.startNewCombat(thisPanel, chosenIP, chosenPort);
       	    		contenedor.dispose();
       	    	}
	        });
	        	
	        
	        JButton BotonReset = new JButton("Resetea posiciones");
	        BotonReset.addActionListener(new ActionListener () {
       	    	public void actionPerformed (ActionEvent e) {
       	    		for (LabelTipoBarco barco : flota){
       	    			barco.cancelBoatPosition();
       	    		}
       	    		repaint();
       	    	}
	        	
	        });
	        
	        JButton BotonAtras = new JButton("Atras"); //Implementarlo una vez tengas el menu
	        BotonAtras.addActionListener(new ActionListener () {
       	    	public void actionPerformed (ActionEvent e) {
	       	 		JButton src = (JButton)e.getSource();
	    			JFrame window = (JFrame)src.getTopLevelAncestor();
	    			MenuInicial.createMenuInitialWindow();
	    			window.dispose();
       	    	}
	        	
	        });

	        javax.swing.GroupLayout PanelBotonesLayout = new javax.swing.GroupLayout(PanelBotones);
	        PanelBotones.setLayout(PanelBotonesLayout);
	        PanelBotonesLayout.setHorizontalGroup(
	            PanelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(PanelBotonesLayout.createSequentialGroup()
	                .addContainerGap()
	                .addGap(100)
	                .addGroup(PanelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                        .addComponent(BotonAceptar)
	                        .addGap(0, 0, Short.MAX_VALUE)             
	                          .addComponent(BotonReset)
		                        .addGap(0, 0, Short.MAX_VALUE)             
	                            .addComponent(BotonAtras))
	                .addContainerGap())
	        );
	        PanelBotonesLayout.setVerticalGroup(
	            PanelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(PanelBotonesLayout.createSequentialGroup()
	  		        .addGap(40)
	                .addComponent(BotonAceptar)
	                .addGap(40)
	                .addComponent(BotonReset)
	                .addGap(40)
	                .addComponent(BotonAtras)
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );

	        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(allContainerPanel);
	        allContainerPanel.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(PanelGrid, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addComponent(PanelBarcos, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addGap(18, 18, 18)
	                        .addComponent(PanelBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addGap(0, 9, Short.MAX_VALUE)))
	                .addContainerGap())
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(PanelGrid, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
	                .addGap(18, 18, 18)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(PanelBarcos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(PanelBotones, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addContainerGap())
	        );

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
	        this.setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(allContainerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(allContainerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	        );
	        
	        
	        PanelBarcos.setLayout(new GridLayout(4,0,10,10));
	        PanelBarcos.setBackground(Color.WHITE);
			for (int i = 0; i < flota.length; i++){
				PanelBarcos.add(flota[i]);
			}
			PanelBarcos.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			/*for (int i = 0; i < 12; i++){
				PanelBarcos.add(new JLabel("BARCO"));
			}*/
			PanelGrid.setLayout(new GridLayout(dimX,dimY,-1,-1));
			
			topGrid = (LabelGridBarcos[][])Utilities.createGrid(dimX,dimY, PanelGrid,0,null);
			
	    }
	
	public void checkAceptarButton(){
   		boolean allChosen = true;
   		for (LabelTipoBarco barco : flota){
   			allChosen = allChosen && barco.hasChosenPos();
   		}
   		if (allChosen) {
   			BotonAceptar.setVisible(true);
   		} else {
   			BotonAceptar.setVisible(false);
   		}
	}
	
	/*No hay inserciones diagonales, solo setea para que se dibujen las labels adyacentes en el grid
	 * en vertical o en horizontal y les dice que dibujar, si no se pueden dibujar alguno de los trozos
	 * porque esta fuera del grid el booleano acceptedPos lo avisa. (se llama a esta funcion al hacer
	 * onMouseEnter a una label de la grid superior con un barco seleccionado, esta funcion setea los valores
	 * para que se dibujen correctamente la label elegida i las adyacentes). Se llama con drawPermanently cierto desde
	 * mouseClick y pone los valores permanentes de la label entonces. Hacemos que devuelva un booleano
	 * ahora para que la maquina pueda ir probando hasta poner los barcos donde quiera (tecnicamente con
	 * esto podriamos eliminar la variable estatica acceptedPos pero ehhhh*/
	public boolean drawSelBoatOnGrid(int firstGridRow, int firstGridCol, boolean drawPermanently){
		int i, counter, tmp, dim, draw;
		if (horizontal) { tmp = firstGridCol;  dim = dimX;  }
		else { tmp = firstGridRow; dim = dimY; }
		i = tipoBarcoArrastrado-1;
		lastLabelsDrawn = new LabelGridBarcos[tipoBarcoArrastrado];
		lastLabelsDrawn[0] = topGrid[firstGridRow-1][firstGridCol-1];
		if (!topGrid[firstGridRow-1][firstGridCol-1].hayBarcoPermanente()) {
			acceptedPos = true;
			if (i == 0) { topGrid[firstGridRow-1][firstGridCol-1].setDrawingShipPart(4, drawPermanently); return acceptedPos;} //Si es un barco de un solo elemento...
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
				return acceptedPos;
			}
			i--;
		}
		return acceptedPos;
	}
	
	/*Intenta dibujar como vacias las labels guardadas como pintadas en el ultimo ciclo
	 * (si estan dibujadas permanentemente no podra pero por la funcion) y pone acceptedPos en falso*/
	public static void cleanBoatPaintedLabels(){
		if (lastLabelsDrawn == null) { return;}
		for (LabelGridBarcos label : lastLabelsDrawn){
			if (label != null){
				label.setDrawingShipPart(0, false);
			}
		}
		acceptedPos = false;
	}
	
	/*Devuelve un grid con las posiciones de los barcos puestos durante el proceso de seleccion
	 * de posiciones compatible con la grid de PanelCombate */
	public LabelGridCombate[][] exportaGrid(){
		LabelGridCombate[][] export = new LabelGridCombate[topGrid.length][topGrid[0].length];
		for (int i = 0; i < topGrid.length; i++){
			for (int j = 0; j < topGrid[i].length; j++){
					export[i][j] = topGrid[i][j].exportToLGC();
				}
			}
		return export;
	}
	
	/*Funcion usada para pasar las labels con las posiciones del barco dibujado permanentemente
	 * a la labeldebarco que le corresponde (que ahora tendra el icono de cancelar el posicionamiento)
	 * para que asi si se aprieta otra vez sepa que labels resetear. Resetea tambien las labels guardadas
	 * ya que no las necesitamos mas */
	public static void passCoordsToBarcoLabel(LabelTipoBarco LB){
		LB.setLabelsBarco(lastLabelsDrawn);
		lastLabelsDrawn = null;
	}
	
	/*Podria hacerse de otra forma pero para 4-6 elementos total esta bien, deselecciona
	todos los boatLabels del tablero, usada cuando el usuario selecciona otro boatlabel
	cuando tenia seleccionado uno antes	*/
	public static void resetBoatLabels(){
		for (int i = 0; i < flota.length; i++){
			flota[i].setBorder(null);
			flota[i].setSeleccionada(false);
		}
	}
	
	/*Encuentra el barco seleccionado por el usuario actualmente*/
	public static LabelTipoBarco findSelectedBoatLabel(){
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
		PanelSituaBarcos.tipoBarcoArrastrado = tipoBarcoArrastrado;
	}

	public static boolean isHorizontal() {
		return horizontal;
	}

	public static void setHorizontal(boolean horizontal) {
		PanelSituaBarcos.horizontal = horizontal;
	}

	public static boolean isAcceptedPos() {
		return acceptedPos;
	}

	public static void setAcceptedPos(boolean acceptedPos) {
		PanelSituaBarcos.acceptedPos = acceptedPos;
	}

	public static JFrame createNewPSBWindow(String IP, int port){
		JFrame window = new JFrame("Posiciona tus barcos");
		PanelSituaBarcos content = new PanelSituaBarcos(IP, port);
		window.setContentPane(content);
		window.setResizable(false);
		//window.setPreferredSize(new Dimension(700,700));
		window.setVisible(true);
		window.pack();
		return window;
	}

	public static void main(String[] args){
		JFrame window = new JFrame("test");
		PanelSituaBarcos content = new PanelSituaBarcos();
		window.setContentPane(content);
		window.setResizable(false);
		//window.setPreferredSize(new Dimension(700,700));
		window.setVisible(true);
		window.pack();
	}
}
