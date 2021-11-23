package app;

public class JuliaThread extends Thread {
	
	public ComplexPlaneCanvas canvas;
	public int threadId;
	public int xStart;
	public int yStart;
	public int xWidth;
	public int yWidth;
	public boolean isJulia;
	public boolean isMonoChrome;
	public int progress;
	
	public JuliaThread(int threadId, ComplexPlaneCanvas canvas, int xStart, int yStart, int xWidth, int yWidth) {
		this.threadId = threadId;
		this.canvas = canvas;
		this.xStart = xStart;
		this.yStart = yStart;
		this.xWidth = xWidth;
		this.yWidth = yWidth;
		this.isJulia = true;
		this.isMonoChrome = false;
		this.progress = 0;
	}
	
	// returns the number of iterations when the result leaves the 2 units radius circle (max iteration 400)
	public int iterate(ComplexNumber z, ComplexNumber c) {
		ComplexNumber result;
		ComplexNumber zn = z.clone();
		int i = 0;
		for (; i < 400; i++) {
			result = ComplexNumber.add(ComplexNumber.multiply(zn, zn), c);
			if(result.magnitude() >= 4) {
				return i;
			}
			zn = result;
		}
		return i;
	}
	
	public void run() {
		ComplexNumber z = new ComplexNumber();
		ComplexNumber c = new ComplexNumber();
		float colorIndex = 0.0f;
		
		if(this.isJulia) {
			c.value(canvas.juliaCenter.real, canvas.juliaCenter.imag);
			for(int i = 0; i < this.xWidth; ++i) {
				for(int j = 0; j < this.yWidth; ++j) {
					z.value(canvas.get_coordinate(i + xStart, true), canvas.get_coordinate(j + yStart, false));
					colorIndex = ((float)this.iterate(z, c)) / 400.0f;
					canvas.setColor(i + xStart, j + yStart, colorIndex, this.isMonoChrome);
				}
				this.progress = (100 * (i + 1)) / this.xWidth;
			}
		}
		else {
			z.value(0.0f, 0.0f);
			for(int i = 0; i < this.xWidth; ++i) {
				for(int j = 0; j < this.yWidth; ++j) {
					c.value(canvas.get_coordinate(i + xStart, true), canvas.get_coordinate(j + yStart, false));
					colorIndex = ((float)this.iterate(z, c)) / 400.0f;
					canvas.setColor(i + xStart, j + yStart, colorIndex, this.isMonoChrome);
				}
				this.progress = (100 * (i + 1)) / this.xWidth;
			}
		}
		
		// surround the computed area with a rectangle
		for(int i = 0; i < xWidth; ++i)
			canvas.setColor(i + xStart, yStart, 1, true);
		for(int i = 0; i < yWidth; ++i)
			canvas.setColor(xStart, i + yStart, 1, true);
		for(int i = 0; i < xWidth; ++i)
			canvas.setColor(i + xStart, yStart + yWidth - 1, 1, true);
		for(int i = 0; i < yWidth; ++i)
			canvas.setColor(xWidth + xStart - 1, i + yStart, 1, true);
	}
}
