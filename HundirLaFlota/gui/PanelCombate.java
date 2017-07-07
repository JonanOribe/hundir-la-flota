package HundirLaFlota.gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import HundirLaFlota.ai.BoatHandling;
import HundirLaFlota.misc.Utilities;

/*Panel que contendra los paneles con la grid del usuario y un contrincante y gestionara
 * la comunicacion entre estos */
@SuppressWarnings("serial")
public class PanelCombate extends JPanel{
	
	private LabelGridCombate[][] gridCoordsTop, gridCoordsBot;
	
    private javax.swing.JPanel bottomLeftPanel;
    private javax.swing.JTextArea chatScrollablePanel;
    private textInputArea inputTextArea;
    private javax.swing.JPanel leftMidSmallPanel;
    private javax.swing.JButton chatButton;
    private javax.swing.JButton reconnectButton;
    private javax.swing.JButton quitButton;
    private javax.swing.JScrollPane chatTextPane;
    private javax.swing.JScrollPane inputPane;
    private javax.swing.JPanel topLeftPanel;

	public PanelCombate(int dimX, int dimY, PanelSituaBarcos ancestor){
		super();
		initcomponents(dimX, dimY, ancestor);
	
}
	private void initcomponents(int dimX, int dimY, PanelSituaBarcos ancestor) {
		topLeftPanel = new javax.swing.JPanel();
        bottomLeftPanel = new javax.swing.JPanel();
        leftMidSmallPanel = new javax.swing.JPanel();
        chatTextPane = new javax.swing.JScrollPane();
        chatScrollablePanel = new javax.swing.JTextArea();
        chatButton = new javax.swing.JButton();
        reconnectButton = new javax.swing.JButton();
        quitButton = new javax.swing.JButton();
        inputPane = new javax.swing.JScrollPane();
        inputTextArea = new textInputArea();
        
		LabelGridCombate.setContainer(this);
		
        javax.swing.GroupLayout topLeftPanelLayout = new javax.swing.GroupLayout(topLeftPanel);
        topLeftPanel.setLayout(topLeftPanelLayout);
        topLeftPanelLayout.setHorizontalGroup(
            topLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        topLeftPanelLayout.setVerticalGroup(
            topLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout BottomLeftPanelLayout = new javax.swing.GroupLayout(bottomLeftPanel);
        bottomLeftPanel.setLayout(BottomLeftPanelLayout);
        BottomLeftPanelLayout.setHorizontalGroup(
            BottomLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 257, Short.MAX_VALUE)
        );
        BottomLeftPanelLayout.setVerticalGroup(
            BottomLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 139, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout LeftMidSmallPanelLayout = new javax.swing.GroupLayout(leftMidSmallPanel);
        leftMidSmallPanel.setLayout(LeftMidSmallPanelLayout);
        LeftMidSmallPanelLayout.setHorizontalGroup(
            LeftMidSmallPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        LeftMidSmallPanelLayout.setVerticalGroup(
            LeftMidSmallPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 27, Short.MAX_VALUE)
        );

        chatScrollablePanel.setColumns(20);
        chatScrollablePanel.setRows(5);
        chatTextPane.setViewportView(chatScrollablePanel);

        chatButton.setText("Send");

        reconnectButton.setText("Reconnect");

        quitButton.setText("Quit");

        inputPane.setViewportView(inputTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, true)
                    .addComponent(bottomLeftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(topLeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(leftMidSmallPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, 50)
                        .addComponent(reconnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, 150)
                        .addComponent(quitButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, 150))
                    .addComponent(chatButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, 200)
                    .addComponent(chatTextPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, 300)
                    .addComponent(inputPane,  javax.swing.GroupLayout.PREFERRED_SIZE, 100, 300))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(topLeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(leftMidSmallPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(bottomLeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chatTextPane, javax.swing.GroupLayout.PREFERRED_SIZE, 238, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, 20)
                        .addComponent(inputPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, 40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chatButton)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(reconnectButton)
                            .addComponent(quitButton))))
                .addContainerGap())
        );
        
        chatButton.setFocusCycleRoot(true);
        chatButton.requestFocus();
        
        chatScrollablePanel.setEditable(false);
        
        inputTextArea.setText("...");
       
        leftMidSmallPanel.setLayout(new GridLayout(1,5,5,5));
        leftMidSmallPanel.add(new JLabel("TIEMPO: "), SwingUtilities.CENTER);
        leftMidSmallPanel.add(new JLabel("PUNTOS: "), SwingUtilities.CENTER);
        leftMidSmallPanel.add(new JLabel("ACIERTOS: "), SwingUtilities.CENTER);
        leftMidSmallPanel.add(new JLabel("TURNO: "), SwingUtilities.CENTER);
        leftMidSmallPanel.add(new JLabel("JUGADOR: "), SwingUtilities.CENTER);
		topLeftPanel.setLayout((new GridLayout(dimX,dimY,-1,-1)));
		bottomLeftPanel.setLayout((new GridLayout(dimX,dimY,-1,-1)));
		gridCoordsTop = (LabelGridCombate[][])Utilities.createGrid(dimX,dimY,topLeftPanel,1,null);
		gridCoordsBot = (LabelGridCombate[][])Utilities.createGrid(dimX,dimY,bottomLeftPanel,1, ancestor.exportaGrid()); //Creamos la grid para dibujarla en el panel con valores 0
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
		window.setPreferredSize(new Dimension(1100,700));
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
