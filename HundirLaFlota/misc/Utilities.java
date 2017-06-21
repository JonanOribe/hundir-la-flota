package HundirLaFlota.misc;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Utilities {

	public static ImageIcon scaleIconTo(ImageIcon originalIcon, int initialX, int initialY, int width, int height){
		try {
			Image resizer = originalIcon.getImage();
			BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(resizer, initialX, initialY, width, height, null);
			g.dispose();
			/*g.setComposite(AlphaComposite.Src);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);*/
			resizer = resizedImage;
			return new ImageIcon(resizer);
			}
			catch(Exception e){
				System.out.println("Problems converting the image to another size! " + e.getMessage());
				return null;
			}
		}
	}
