package HundirLaFlota.gui;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*Usado solo para construir y mantener en su sitio las labels con la matriz de posiciones del tablero*/
@SuppressWarnings("serial")
public class TableroTop extends JPanel{
	
	public TableroTop(TableroBarcos contenedor, int dimX, int dimY){
		super();
		this.setLayout(new GridLayout(dimX,dimY,-1,-1));
		JLabel coordsSquare;
		LabelGrid gridSquare;
		String[] columns = {"1","2","3","4","5","6","7","8"};
		String[] rows = {"A","B","C","D","E","F","G","H"};
		int colN = 0,rowL = 0;
		
		LabelGrid.setContainer(contenedor);
		
		TableroBarcos.topGrid = new LabelGrid[dimX-1][dimY-1];
		
		for (int i = 0 ; i < dimX ; i++) {
			for (int j = 0; j < dimY; j++) {
				if (i == 0) { 
					if (j == 0) {
						coordsSquare = new JLabel("");
						this.add(coordsSquare);
						continue;
					}
					else {
						coordsSquare = new JLabel(columns[colN], SwingConstants.CENTER);
						coordsSquare.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						colN++;
					}
				}
				else if (j == 0) { 
					coordsSquare = new JLabel(rows[rowL], SwingConstants.CENTER);
					coordsSquare.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					rowL++;
				}
				else {
					gridSquare = new LabelGrid(" ", i, j);
					TableroBarcos.topGrid[i-1][j-1] = gridSquare;
					this.add(gridSquare);
					continue;
				}
				this.add(coordsSquare);
			}
		}
	}

}
