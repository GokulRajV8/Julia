package app;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class ComplexPlaneCanvas {
    // canvas to draw the pattern
    public BufferedImage canvas;
    // co-ordinates of top-left corner
    public float xStart;
    public float yStart;
    // side length of a pixel in terms of units in Complex plane
    public float pixelSide;
    // size of the complex plane in terms of units
    public float width;
    // center used to generate Julia set
    public ComplexNumber juliaCenter;
    // file to save output
    public File output;
    // color palette
    private float[] redPoints   = {   0.0f,  0.0f,  32.0f, 237.0f, 255.0f,   0.0f,   0.0f,  32.0f};
    private float[] greenPoints = {   2.0f,  7.0f, 107.0f, 255.0f, 170.0f,   2.0f,   7.0f, 107.0f};
    private float[] bluePoints  = {   0.0f, 10.0f, 203.0f, 255.0f,   0.0f,   0.0f,  10.0f, 203.0f};
    private float[] indexPoints = {-14.25f,  0.0f,  16.0f,  42.0f, 64.25f, 85.75f, 100.0f, 116.0f};

    public ComplexPlaneCanvas(float xStart, float yStart, float pixelSide, int widthInPixels, ComplexNumber juliaCenter, String fileName) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.pixelSide = pixelSide;
        this.width = widthInPixels * pixelSide;
        this.juliaCenter = juliaCenter;
        this.canvas = new BufferedImage(widthInPixels, widthInPixels, BufferedImage.TYPE_3BYTE_BGR);
        this.output = new File(fileName);
    }

    // converts pixel position to complex plane position
    public float get_coordinate(int pixels, boolean isX) {
    if(isX) {
        return this.xStart + this.pixelSide * pixels - 0.5f * pixelSide;
    }
    else
        return this.yStart - this.pixelSide * pixels + 0.5f * pixelSide;
    }

    // mathematical interpolation
    public static float interpolate(float[] xValues, float[] yValues, float x) {
        float y = 0.0f;
        float weight = 1.0f;
        int length = xValues.length;
        for(int i = 0; i < length; ++i) {
            for(int j = 0; j < length; ++j) {
                if(i == j)
                    continue;
                weight = weight * (x - xValues[j]) / (xValues[i] - xValues[j]);
            }
            y += weight * yValues[i];
            weight = 1.0f;
        }
        return y;
    }

    public void setColor(int xPixel, int yPixel, float colorIndex) {
        // mapping 500 points to 100 colors
        colorIndex = (int)colorIndex % 100;
        int red = (int)interpolate(indexPoints, redPoints, colorIndex);
        if(red > 255)
            red = 255;
        else if(red < 0)
            red = 0;
        int green = (int)interpolate(indexPoints, greenPoints, colorIndex);
        if(green > 255)
            green = 255;
        else if(green < 0)
            green = 0;
        int blue = (int)interpolate(indexPoints, bluePoints, colorIndex);
        if(blue > 255)
            blue = 255;
        else if(blue < 0)
            blue = 0;
        this.canvas.setRGB(xPixel, yPixel, new Color(red, green, blue).getRGB());
    }

    public void fileSave() {
        try {
            javax.imageio.ImageIO.write(this.canvas, "png", this.output);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}