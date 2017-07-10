package HundirLaFlota.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import HundirLaFlota.ai.BoatHandling;
import HundirLaFlota.misc.Utilities;
import HundirLaFlota.network.ClientConnector;
import HundirLaFlota.network.HLFServer;

/*Panel que contendra los paneles con la grid del usuario y un contrincante y gestionara
 * la comunicacion entre estos 
 * Agregar checkeo al cerrar ventana para cerrar el conector tb*/
@SuppressWarnings("serial")
public class PanelCombate extends JPanel{
	
	private LabelGridCombate[][] gridCoordsTop, gridCoordsBot;
	static final String DEFAULTPREFIX = ">> ";   //Prefijo que se antepondra a los mensajes de chat
	private PanelCombateActionHandler eventHandler;
	private String chosenIP;
	private int chosenPort;
	private boolean jugadorDC;

	private int puntos = 0;
	private int turno = 1;
	private int aciertos = 0;
	private int aguas = 0;
	
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
    public JLabel puntosLabel;
    public JLabel turnosLabel;
    public JLabel aciertosAguasLabel;
    public JLabel jugadorLabel;
    
    /*Constructor para 1 jugador sin conector*/
	public PanelCombate(int dimX, int dimY, PanelSituaBarcos ancestor){
		this(dimX,dimY,ancestor,false);
	}
    
	public PanelCombate(int dimX, int dimY, PanelSituaBarcos ancestor, boolean startConnector){
		this(dimX,dimY,ancestor,"127.0.0.1",HLFServer.DEFAULTPORT, startConnector);
	}
	
	public PanelCombate(int dimX, int dimY, PanelSituaBarcos ancestor, String IP, int port, boolean startConnector){
		super();
		this.chosenIP = IP;
		this.chosenPort = port;
		initcomponents(dimX, dimY, ancestor);
		if(startConnector){
			this.connector = new ClientConnector(this, chosenIP, chosenPort);
		}
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
	
	public void stopAll(){
		this.connector.stopRunning();
		this.connector = null;
		this.eventHandler = null;
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
        
        JLabel enemyShipsLabel = new JLabel("Cuadrante del enemigo", JLabel.CENTER);
        JLabel myShipsLabel = new JLabel("Mis barcos", JLabel.CENTER);

        
        chatScrollablePanel.setColumns(30);
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
                	.addComponent(enemyShipsLabel,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE,Short.MAX_VALUE)
                	.addGap(5,5,5)
                    .addComponent(bottomLeftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(topLeftPanel,javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                	.addGap(5,5,5)
                	.addComponent(myShipsLabel,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
                	.addGap(5,5,5)
                    .addComponent(leftMidSmallPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, 40)
                        .addComponent(reconnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, 150)
                        .addComponent(quitButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, 150))
                    .addComponent(chatButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, 200)
                    .addComponent(chatTextPane, javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE, 300)
                    .addComponent(inputPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, 300))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(enemyShipsLabel)
                        .addGap(10)
                        .addComponent(topLeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(leftMidSmallPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, 40)
                        .addGap(15)
                        .addComponent(myShipsLabel)
                        .addGap(11, 11, 11)
                        .addComponent(bottomLeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chatTextPane, javax.swing.GroupLayout.PREFERRED_SIZE, 238, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, 5)
                        .addComponent(inputPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, 20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chatButton)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(reconnectButton)
                            .addComponent(quitButton))))
                .addContainerGap())
        );
        
        enemyShipsLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        enemyShipsLabel.setBackground(Color.BLUE);
        myShipsLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        myShipsLabel.setBackground(Color.BLUE);


        
        chatButton.setFocusCycleRoot(true);
        chatButton.requestFocus();
        
        chatScrollablePanel.setEditable(false);
        
        inputTextArea.setText("...");
       
        leftMidSmallPanel.setLayout(new GridLayout(0,4,5,5));
        puntosLabel = new JLabel("PUNTOS: 0", JLabel.CENTER);
        puntosLabel.setBorder(BorderFactory.createMatteBorder(2,4,2,4,Color.BLACK));
        leftMidSmallPanel.add(puntosLabel);
        turnosLabel = new JLabel("TURNO: 1", JLabel.CENTER);
        turnosLabel.setBorder(BorderFactory.createMatteBorder(2,4,2,4,Color.BLACK));
        leftMidSmallPanel.add(turnosLabel);
        aciertosAguasLabel = new JLabel("ACIERTOS: 0/0", JLabel.CENTER);
        aciertosAguasLabel.setBorder(BorderFactory.createMatteBorder(2,4,2,4,Color.BLACK));
        leftMidSmallPanel.add(aciertosAguasLabel);
        jugadorLabel = new JLabel("Jug. 1", JLabel.CENTER);
        jugadorLabel.setBorder(BorderFactory.createMatteBorder(2,4,2,4,Color.BLACK));
        leftMidSmallPanel.add(jugadorLabel);
     
        leftMidSmallPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
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
	
	public int getPuntos() {
		return puntos;
	}

	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}

	public int getTurno() {
		return turno;
	}

	public void setTurno(int turno) {
		this.turno = turno;
	}

	public int getAciertos() {
		return aciertos;
	}

	public void setAciertos(int aciertos) {
		this.aciertos = aciertos;
	}

	public int getAguas() {
		return aguas;
	}

	public void setAguas(int aguas) {
		this.aguas = aguas;
	}

	public static void startNewCombat(PanelSituaBarcos situator, String IP, int port){
		JFrame window = new JFrame("test combate");
		PanelCombate content = new PanelCombate(9,9,situator, IP, port, true);
		window.setContentPane(content);
		window.setPreferredSize(new Dimension(1100,800));
		window.setVisible(true);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getRootPane().setDefaultButton(content.chatButton);
		content.startConnection();
	}
	
	public static void main(String[] args){ /*codigo de testeo standalone*/
		PanelSituaBarcos situator = new PanelSituaBarcos();
		PanelSituaBarcos.setTipoBarcoArrastrado(2);
		situator.drawSelBoatOnGrid(2,4,true); //Testing
		PanelSituaBarcos.setTipoBarcoArrastrado(3);
		situator.drawSelBoatOnGrid(5,2,true);
		PanelSituaBarcos.setTipoBarcoArrastrado(1);
		situator.drawSelBoatOnGrid(7,7,true);
		PanelSituaBarcos.setTipoBarcoArrastrado(4);
		situator.drawSelBoatOnGrid(7,2,true);
		startNewCombat(situator,"127.0.0.1", 4522);
		
	}
}
