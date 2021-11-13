package objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

public class OutputImage {

	// initialization of canvas
	int width = 12000;
	int height = 10000;
	BufferedImage buffer = new BufferedImage(this.width, this.height, BufferedImage.TYPE_3BYTE_BGR);
	public Graphics2D g2d = this.buffer.createGraphics();
	File file;
	
	// constructor
	public OutputImage(String name) {
		this.g2d.setColor(Color.white);
		this.g2d.fillRect(0, 0, this.width, this.height);
		this.file = new File("C:\\Users\\gokul\\Desktop\\output" + name + ".png");
	}
	
	// plots points
	public void plot(int x, int y, int color, boolean isColored) {
		float z = (float)color/400.0f;
		if(isColored)
			this.g2d.setColor(Color.getHSBColor(0.15f - z, 1.0f, 1.0f - z));
		else
			this.g2d.setColor(Color.getHSBColor(1.0f, 0.0f, 1.0f - z));
		this.g2d.drawLine(x, y, x, y);
	}
	
	// saves the file
	public void save() {
		try {
			ImageIO.write(this.buffer, "png", this.file);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
