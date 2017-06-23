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
	
	/*Funcion para crear un grid de labels de barcos para que el usuario pueda seleccionar las posiciones
	 * de los barcos a su antojo */
	public static LabelGridBarcos[][] createLGrid(int rows, int cols, JPanel target){
		String[] col_S = new String[cols-1];
		for (int i = 0; i < col_S.length; i++){
			col_S[i] = "" + (i+1);
		}
		String[] row_S = new String[rows-1];
		for (int i = 0; i < row_S.length; i++){
			row_S[i] = Character.toString((char)('A'+i));
		}
		int colN = 0,rowL = 0;		
		LabelGridBarcos[][] myGrid = new LabelGridBarcos[rows-1][cols-1];
		JLabel coordsSquare;
		LabelGridBarcos gridSquare;
		
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
					gridSquare = new LabelGridBarcos(i, j);
					myGrid[i-1][j-1] = gridSquare;
					target.add(gridSquare);
					continue;
				}
				target.add(coordsSquare);
			}
		}
		return myGrid;
	}
	
	/*Funcion para crear una grid de LabelGridCombate con opcional exportacion de una grid interior (vamos, todos los elementos
	 * que no son A...Z i 1...9). MUy similar a la existente de arriba pero igualarlas es complicado debido a multiples castings */
	public static LabelGridCombate[][] createCGrid(int rows, int cols, JPanel targetPanel, LabelGridCombate[][] exportedInnerGrid){
		String[] col_S = new String[cols-1];
		for (int i = 0; i < col_S.length; i++){
			col_S[i] = "" + (i+1);
		}
		String[] row_S = new String[rows-1];
		for (int i = 0; i < row_S.length; i++){
			row_S[i] = Character.toString((char)('A'+i));
		}
		int colN = 0,rowL = 0;		
		LabelGridCombate[][] myGrid = new LabelGridCombate[rows-1][cols-1];
		JLabel coordsSquare;
		LabelGridCombate gridSquare;
		
		for (int i = 0 ; i < rows ; i++) {
			for (int j = 0; j < cols; j++) {
				if (i == 0) { 
					if (j == 0) {
						coordsSquare = new JLabel("");
						targetPanel.add(coordsSquare);
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
					if (exportedInnerGrid == null){
						gridSquare = new LabelGridCombate(i, j, true);
						myGrid[i-1][j-1] = gridSquare;
					}else {
						gridSquare = exportedInnerGrid[i-1][j-1];
						myGrid[i-1][j-1] = exportedInnerGrid[i-1][j-1];	
					}
					targetPanel.add(gridSquare);
					continue;
				}
				targetPanel.add(coordsSquare);
			}
		}
		return myGrid;
	}
	}
