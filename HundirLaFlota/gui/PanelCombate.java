package HundirLaFlota.gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import HundirLaFlota.ai.BoatHandling;
import HundirLaFlota.misc.Utilities;
import HundirLaFlota.network.ClientConnector;
import HundirLaFlota.network.HLFServer;

/*Panel que contendra los paneles con la grid del usuario y un contrincante y gestionara
 * la comunicacion entre estos */
@SuppressWarnings("serial")
public class PanelCombate extends JPanel{
	
	private LabelGridCombate[][] gridCoordsTop, gridCoordsBot;
	static final String DEFAULTPREFIX = ">> ";   //Prefijo que antepondra a los mensajes de chat
	private PanelCombateActionHandler eventHandler;
	private String chosenIP;
	private int chosenPort;
	private boolean jugadorDC = false;
	
    private javax.swing.JPanel bottomLeftPanel;
    javax.swing.JTextArea chatScrollablePanel;
    textInputArea inputTextArea;
    private javax.swing.JPanel leftMidSmallPanel;
    private javax.swing.JButton chatButton;
    private javax.swing.JButton reconnectButton;
    private javax.swing.JButton quitButton;
    private javax.swing.JScrollPane chatTextPane;
    private javax.swing.JScrollPane inputPane;
    private javax.swing.JPanel topLeftPanel;
    private ClientConnector connector;

	public PanelCombate(int dimX, int dimY, PanelSituaBarcos ancestor){
		this(dimX,dimY,ancestor,"127.0.0.1",HLFServer.DEFAULTPORT);
	}
	
	public PanelCombate(int dimX, int dimY, PanelSituaBarcos ancestor, String IP, int port){
		super();
		this.chosenIP = IP;
		this.chosenPort = port;
		initcomponents(dimX, dimY, ancestor);
		this.connector = new ClientConnector(this, chosenIP, chosenPort);
	}
	
	/*A partir de una IP y un puerto inicializa el conector y se intenta conectar a un servidor de 
	 * Hundir la flota*/
	public void startConnection(String newIP, int newPort){
		this.chosenIP = newIP;
		this.chosenPort = newPort;
		this.connector.setIP(newIP);
		this.connector.setPort(newPort);
		startConnection();
	}
	
	public void startConnection() { 
		try {
			this.connector.connectAndStart();
		}catch(Exception e) {
			System.out.println("ERROR AL INICIAR LA CONEXION: " + e.getMessage());
		}
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
        inputTextArea = new textInputArea(this);  //CAMBIAR A UNA CLASE DEDICADA A LISTENER:..
        
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
        chatScrollablePanel.setLineWrap(true);
        chatScrollablePanel.setWrapStyleWord(true);
        chatTextPane.setViewportView(chatScrollablePanel);

        eventHandler = new PanelCombateActionHandler(this);
        chatButton.setText("Enviar");
        chatButton.addActionListener(eventHandler);
        chatButton.addKeyListener(eventHandler);
        

        reconnectButton.setText("Reconectar");
        reconnectButton.addActionListener(eventHandler);

        quitButton.setText("Salir");
        quitButton.addActionListener(eventHandler);

        inputPane.setViewportView(inputTextArea);
        
        inputTextArea.addKeyListener(eventHandler);

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
	
	/*Funcion de testeo para probar la colocacion de barcos por la AI*/
	public void drawAIShips(){ //cambiar a private, etc.
		BoatHandling.placeAllBoatsOnGrid(gridCoordsTop);
	}
	
	/*Funcion para escribir texto en el chat principal del programa (el bloque de texto grande
	 * a la derecha), usa un prefijo constante y salto de linea por cada bloque de texto enviado/recibido */
	public void writeInChat(String text){
		this.chatScrollablePanel.append(DEFAULTPREFIX + text + "\n");
	}
	
	/*Envia un mensaje al servidor a traves del conector*/
	public void sendMsgThroughConnector(String msg){
		this.connector.sendMsgFromGUI(msg);
	}
	
	public boolean isUserConnected(){
		return connector.isConnected();
	}
	
	/*Funcion que determina si un ataque en una posicion concreta toca barco o agua,
	 * se basa en los datos recibidos por el conector del ataque enemigo
	 * y modifica la LabelGridCombate determinada, que ademas redibuja	 */
	public boolean enemyAttacksPos(int coordX, int coordY){
		if (coordX > 0 && coordX < 9 && coordY > 0 && coordY < 9) { //Cambiar a usar dimx/dimy
			if (this.gridCoordsBot[coordX-1][coordY-1].hasAShipPart()) {
				this.gridCoordsBot[coordX-1][coordY-1].setDrawHitorMiss(true);
				return true;
			}
		}
		this.gridCoordsBot[coordX-1][coordY-1].setDrawHitorMiss(false);
		return false;
	}
	
	/*Funcion que llama el conector dependiendo de los datos recibidos por si tu ataque
	 * ha tocado agua o un trozo de barco */
	public void drawMyAttackResults(int coordX, int coordY, boolean itHit) {
		this.gridCoordsTop[coordX-1][coordY-1].setDrawHitorMiss(itHit);
	}
	
	public String getChosenIP(){
		return this.chosenIP;
	}
	
	public int getChosenPort(){
		return this.chosenPort;
	}
	
	public void setJugadorDC(boolean DC){
		this.jugadorDC = DC;
	}
	
	public boolean isJugadorDC(){
		return jugadorDC;
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

	/*Funciones para cambiar los textos de los botones por el gestor de eventos*/
	public void cambiaQuitButton(String newText){
		this.quitButton.setText(newText);
	}
	
	public void cambiaReconnectButton(String newText){
		this.reconnectButton.setText(newText);
	}
	
	public static void startNewCombat(PanelSituaBarcos situator, String IP, int port){
		JFrame window = new JFrame("test combate");
		PanelCombate content = new PanelCombate(9,9,situator, IP, port);
		window.setContentPane(content);
		window.setPreferredSize(new Dimension(1100,700));
		window.setVisible(true);
		window.pack();
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.getRootPane().setDefaultButton(content.chatButton);
		content.startConnection("127.0.0.1", HLFServer.DEFAULTPORT);
	}
	
	public static void main(String[] args){ /*codigo de testeo standalone*/
		PanelSituaBarcos situator = new PanelSituaBarcos();
		PanelSituaBarcos.setTipoBarcoArrastrado(2);
		situator.drawSelBoatOnGrid(2,4,true); //Testing
		PanelSituaBarcos.setTipoBarcoArrastrado(3);
		situator.drawSelBoatOnGrid(5,2,true);
		startNewCombat(situator,"127.0.0.1", 4522);
		
	}
}
