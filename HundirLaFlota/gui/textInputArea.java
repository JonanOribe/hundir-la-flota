package HundirLaFlota.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*Clase para definir una ventana de texto con un maximo de caracteres a escribir, en teoria
 * al apretar el boton de enviar el texto se le enviara al servidor para poder mantener
 * un chat con el enemigo si se quiere. (El texto presente se obtendra con getText desde las acciones
 * del boton... */
public class textInputArea extends JTextArea{
	
	private static final long serialVersionUID = 1L;
	private static final String DEFAULTTEXT = "...";
	private static final int MAXROWS = 4;
	private static final int MAXCOLS = 40;
	private static PanelCombate contenedor;
	
	public textInputArea(PanelCombate container){
		super();
		contenedor = container;
		this.setLineWrap(true);
		this.setRows(MAXROWS);
		this.setColumns(MAXCOLS);
		this.setWrapStyleWord(true); //Intentara no cortar palabras para pasar a la siguiente linea
		
		this.addFocusListener(new FocusListener() {
		    public void focusGained(FocusEvent e) {
		    	if (getText().equals(DEFAULTTEXT)){
		    	setText("");
		    	}
		    }
		    public void focusLost(FocusEvent e) {
		    	if (getText().equals("")){
		    		setText(DEFAULTTEXT);
		    	}
		    }

		});
		
		this.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				textExcessTest();
			 }
			public void removeUpdate(DocumentEvent e) {
				  textExcessTest();
			}
			public void insertUpdate(DocumentEvent e) {
				textExcessTest();
			}
		});
	}

	/*Sacado de stack overflow ya que daba errores de GUI debido a que modificaba el 
	 * documento antes de que se blockeara en la sequencia de eventos de la GUI, debes
	 * hacer que la modificacion se de en un thread...	 */
	 private void textExcessTest() {
		 String currentText = this.getText();
         Runnable doAssist = new Runnable() {
             @Override
             public void run() {
         		if (currentText.length() > (MAXROWS * MAXCOLS)){
        			sText(currentText.substring(0, currentText.length()-1));
        		}
             }
         };
         SwingUtilities.invokeLater(doAssist);
         }
	 
	public PanelCombate getContenedor(){
		return contenedor;
	}
	 
	 private void sText(String newText){
		 this.setText(newText);
	 }
	
}
