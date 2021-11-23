package app;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ComplexPlaneCanvas {
	
	public BufferedImage canvas;
	public float xStart;
	public float yStart;
	public float pixelSide;
	public float xWidth;
	public float yWidth;
	public ComplexNumber juliaCenter;
	public File output;
	
	public ComplexPlaneCanvas(float xStart, float yStart, float pixelSide, int horizontalPixels, int verticalPixels, ComplexNumber juliaCenter, String fileName) {
		this.xStart = xStart;
		this.yStart = yStart;
		this.pixelSide = pixelSide;
		this.xWidth = horizontalPixels * pixelSide;
		this.yWidth = verticalPixels * pixelSide;
		this.juliaCenter = juliaCenter;
		this.canvas = new BufferedImage(horizontalPixels, verticalPixels, BufferedImage.TYPE_3BYTE_BGR);
		this.output = new File(fileName);
	}
	
	public float get_coordinate(int pixels, boolean isX) {
		if(isX) {
			return this.xStart + this.pixelSide * pixels - 0.5f * pixelSide;
		}
		else
			return this.yStart - this.pixelSide * pixels + 0.5f * pixelSide;
	}
	
	public void setColor(int xPixel, int yPixel, float colorIndex, boolean isMonochrome) {
		if(!isMonochrome)
			this.canvas.setRGB(xPixel, yPixel, Color.getHSBColor(0.15f - colorIndex, 1.0f, 1.0f - colorIndex).getRGB());
		else
			this.canvas.setRGB(xPixel, yPixel, Color.getHSBColor(1.0f, 0.0f, 1.0f - colorIndex).getRGB());
	}
	
	public void fileSave() {
		try {
			ImageIO.write(this.canvas, "png", this.output);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

}
