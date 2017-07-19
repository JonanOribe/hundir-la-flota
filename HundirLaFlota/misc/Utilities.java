package HundirLaFlota.misc;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import HundirLaFlota.gui.LabelGrid;
import HundirLaFlota.gui.LabelGridCombate;
import HundirLaFlota.gui.PanelCombate;
import HundirLaFlota.network.HLFServer;

/*Clase con funciones auxiliares para las demas clases de Hundir la flota*/
public class Utilities {

	private static JTextField gameID = null;

	public static ImageIcon scaleIconTo(ImageIcon originalIcon, int initialX, int initialY, int width, int height){
		try {
			Image resizer = originalIcon.getImage();
			BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(resizer, initialX, initialY, width, height, null);
			g.setComposite(AlphaComposite.Src);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g.dispose();
			resizer = resizedImage;
			return new ImageIcon(resizer);
			}
			catch(Exception e){
				System.out.println("Problems converting the image to another size! " + e.getMessage());
				return null;
			}
		}
	
	
	/*Funcion para crear un grid de labels que heredan de JLabel de dos tipos por ahora, tipo 0 => LabelGridBarcos
	 * que serian las posiciones del grid superior cuando colocas barcos y tipo 1 => LabelGridCombate que serian
	 * las posiciones de los dos grids de una ventana de combate (por ahora).
	 *La 2D array que obtienes deberas castearla al tipo de Label que quieras, supongo que es mejor hacerlo
	 *asi y tener menos lineas de codigo que las dos funciones separadas antiguas*/
    public static JLabel[][] createGrid(int rows, int cols, JPanel target, int tipo, JLabel[][] exportedInnerGrid){
        String[] col_S = new String[cols-1];
        for (int i = 0; i < col_S.length; i++){
            col_S[i] = "" + (i+1);
        }
        String[] row_S = new String[rows-1];
        for (int i = 0; i < row_S.length; i++){
            row_S[i] = Character.toString((char)('A'+i));
        }
        int colN = 0,rowL = 0;      
        JLabel[][] myGrid;
        if (tipo == 0) {
            myGrid = new LabelGrid[rows-1][cols-1];
        }else {
            myGrid = new LabelGridCombate[rows-1][cols-1];
        }
        JLabel coordsSquare;
        JLabel gridSquare;
        
        for (int i = 0 ; i < rows ; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0) { 
                    if (j == 0) {
                        coordsSquare = new JLabel("");
                        target.add(coordsSquare);
                        continue;
                    }
                    else {
                        coordsSquare = new JLabel(col_S[colN], SwingConstants.CENTER);
                        coordsSquare.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        colN++;
                    }
                }
                else if (j == 0) { 
                    coordsSquare = new JLabel(row_S[rowL], SwingConstants.CENTER);
                    coordsSquare.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    rowL++;
                }
                else {
                    if (tipo == 0) {
                        gridSquare = new LabelGrid(i, j);
                    }
                    else {
                        if (exportedInnerGrid == null){
                            gridSquare = new LabelGridCombate(i, j, true);
                            myGrid[i-1][j-1] = gridSquare;
                        }else {
                            gridSquare = exportedInnerGrid[i-1][j-1];
                            myGrid[i-1][j-1] = exportedInnerGrid[i-1][j-1]; 
                        }
                    }
                    myGrid[i-1][j-1] = gridSquare;
                    target.add(gridSquare);
                    continue;
                }
                target.add(coordsSquare);
            }
        }
        return myGrid;
    }
    
    /*Funcion para crear Paneles de menus con multiples botones y colores, hay un par de formatos implementados (vamos que se decoran diferente)*/
    @SuppressWarnings("unused")
    public static JPanel createCustomJPanel(Color[] backgroundColors, int layoutType, ActionListener parent, String... buttonNames) { //Macro for creating the layouts
		/*Layout type 0 = single JPanel in the centre of the frame with Buttons  
		 *Layout type 1 = 2 JPanels on top of each other, buttons in the lower one
		 * 
		 *#Backgroundcolors == #JPanels it'll default to gray, sanitized the array beforehand with grays instead of nulls
		*/
		
		int numberOfButtons = 0;
		
		JPanel MainPanel = new JPanel();
		if (backgroundColors == null) { backgroundColors = new Color[3]; backgroundColors[0] = Color.GRAY; backgroundColors[1] = Color.GRAY; backgroundColors[2] = Color.GRAY;}
		MainPanel.setBackground(backgroundColors[0]);
		MainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		
		if (layoutType == 0) {
			MainPanel.setLayout(new GridLayout(3,3,10,10));
			JPanel AuxPanel = new JPanel();
			for (String buttonName : buttonNames) {
				numberOfButtons++;
			}
			AuxPanel.setLayout(new GridLayout(numberOfButtons,1,5,5));
			for (String buttonName : buttonNames) { //Had to do it in two for loops, maybe can improve? Its not that long
				JButton Button = new JButton(buttonName);
				AuxPanel.add(Button);
				if (parent != null){
					Button.addActionListener(parent);
				}
			}
			AuxPanel.setBackground(backgroundColors[1]);
			int i;
			for (i = 1;i < 10; i++) { //Molt cutre, no em sortia aixi sino
				if(i == 5) {
					MainPanel.add(AuxPanel);
				}
				else {
					JPanel shittyPanel = new JPanel();
					shittyPanel.setVisible(false);
					MainPanel.add(shittyPanel);
				}
				
			}
		}
		else {
			MainPanel.setLayout(new GridLayout(2,1,20,20));
			JPanel AuxPanelTop = new JPanel();
			JPanel AuxPanelBottom = new JPanel();
			for (String buttonName : buttonNames) {
				numberOfButtons++;
			}
			AuxPanelBottom.setLayout(new GridLayout(numberOfButtons,1,5,5));
			for (String buttonName : buttonNames) { //Had to do it in two for loops, maybe can improve? Its not that long
				JButton Button = new JButton(buttonName);
				AuxPanelBottom.add(Button);
				if (parent != null){
					Button.addActionListener(parent);
				}
			}
			AuxPanelTop.setBackground(backgroundColors[1]);
			AuxPanelBottom.setBackground(backgroundColors[2]);
			MainPanel.add(AuxPanelTop);
			MainPanel.add(AuxPanelBottom);
		}
		
		return MainPanel;
	}
    
    /*Funcion para crear un dialogo especifico, por ahora hay un dialogo para pedir puerto de escucha del servidor y otro
     * para pedir IP y puerto a las que conectarse como cliente  */
    public static String[] createCustomDialog(JFrame frame, int tipo, String defaultIP, int defaultPort) {
    	
	    String[] IPPort = new String[3];
	    JPanel panel = new JPanel(new BorderLayout(5, 5));

	    JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
	    JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
	    JTextField IP = null;
	    gameID = null;
	    
	    label.add(new JLabel("Puerto", SwingConstants.RIGHT));
	    JTextField  Puerto = new JTextField();
	    Puerto.setText(Integer.toString(defaultPort));
	    controls.add(Puerto);
	    
	    if (tipo > 0){
		    label.add(new JLabel("IP", SwingConstants.RIGHT));
		    IP = new JTextField();
		    controls.add(IP);
		    IP.setText(defaultIP); //Cambiar en el futuro, detectar IP usuario o tener lista de IPs de servers dedicados o nose...
		    label.add(new JLabel("ID Partida:", SwingConstants.RIGHT));
		    gameID = new JTextField();
		    controls.add(gameID);
		    gameID.setText("0");
		    gameID.setEnabled(false);
		    JCheckBox gameType = new JCheckBox("Partida publica");
		    gameType.setSelected(true);
		    gameType.addActionListener(new ActionListener () {
		    	public void actionPerformed(ActionEvent e) {
					JCheckBox src = (JCheckBox) e.getSource();
					if (src.isSelected()) {
						src.setSelected(true);
						gameID.setEnabled(false);
						gameID.setText("0");
					} else {
						src.setSelected(false);
						gameID.setEnabled(true);
						gameID.setText("");
					}
		    	}
		    });
		    label.add(new JLabel(""));
		    controls.add(gameType);
	    }
	 
	    panel.add(label, BorderLayout.WEST);
	    panel.add(controls, BorderLayout.CENTER);
	    String message;
	    if (tipo > 0) {
	    	message = "Introduce los datos de la conexion";
	    } else {
	    	message = "En que puerto escuchara el servidor?";
	    }
        UIManager.put("OptionPane.noButtonText", "Cancelar");
        UIManager.put("OptionPane.yesButtonText", "Aceptar");
	    int eleccion = JOptionPane.showConfirmDialog(frame, panel,message,JOptionPane.YES_NO_OPTION);
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.yesButtonText", "Yes");
        if (eleccion == 1) { return null; } //usuario eligio cancelar, volvemos atras...
	    try {
	    if (tipo > 0){
	    	IPPort[0] = IP.getText().trim();
	    	IPPort[2] = gameID.getText().trim();
	    }
	    IPPort[1] = Puerto.getText().trim();
	    }
	    catch(Exception e){
	    	System.out.println("Error en la introduccion de datos. " + e.getMessage());
	    }
	    return IPPort;
	}
    
	/*Presenta al usuario el menu para que este introduzca los datos de la partida a la que quiere
	 * unirse y le asigna los datos una vez los haya introducido correctamente. El booleano
	 * determina si obliga al user a poner valores o no */
	public static boolean inputConnectionToBoard(PanelCombate gameGUI, boolean mustGetValues){
		boolean done = false;
		while(!done){
			try {
				String[] chosenIPPort = Utilities.createCustomDialog(new JFrame(""), 1);
				
				if (chosenIPPort == null) {
					if (mustGetValues) { continue; }
					else {return false;}
				} 
				
				//Control de errores minimo... esto habria que cambiarlo
				if (chosenIPPort[0].equals("")) { chosenIPPort[0] = "127.0.0.1"; }
				if (chosenIPPort[1].equals("")) { chosenIPPort[1] = Integer.toString(HLFServer.DEFAULTPORT); }
				long customGameID = Long.parseLong(chosenIPPort[2]);
				if (customGameID < 0 || customGameID >= HLFServer.MAXGAMEID) { continue; }
				gameGUI.setChosenIP(chosenIPPort[0]);
				gameGUI.setChosenPort(Integer.parseInt(chosenIPPort[1]));
				gameGUI.setGameID(customGameID);
				done = true;
			}catch(Exception e) {
				continue;
			}
		}
		gameID = null;
		return true;
	}

    public static String[] createCustomDialog(JFrame frame, int tipo) {
    	return createCustomDialog(frame,tipo,"127.0.0.1",HLFServer.DEFAULTPORT);
	}
}
