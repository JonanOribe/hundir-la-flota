package HundirLaFlota.misc;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import HundirLaFlota.gui.LabelGridBarcos;
import HundirLaFlota.gui.LabelGridCombate;

public class Utilities {

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
            myGrid = new LabelGridBarcos[rows-1][cols-1];
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
                        gridSquare = new LabelGridBarcos(i, j);
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
    
	}
