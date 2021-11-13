package services;

import objects.ComplexNumber;
import objects.OutputImage;

public class PatternGenerator {
	
	// pattern generator
	public static void generate(ComplexNumber center, double width, boolean isColored) {
		ComplexNumber c = center.clone();
		OutputImage outputJulia = new OutputImage("Julia");
		OutputImage outputMandelbrot = new OutputImage("Mandelbrot");
		ComplexNumber z = new ComplexNumber();
		double step;
		System.out.println("Pattern generation started ...");
		
		// Julia set
		step = 0.0003d;
		z.value(0.0d - step*6000.0d, 0.0d + step*5000.0d);
		System.out.println("Julia set generation -------------------------------------------------------------------------------|100%");
		for(int i = 0; i < 12000; i++) {
			if (i%120 == 0)
				System.out.print("-");
			for (int j = 0; j < 10000; j++) {
				outputJulia.plot(i, j, Iterator.iterate(z, c), isColored);
				z.imag = z.imag - step;
			}
			z.real = z.real + step;
			z.imag = z.imag + step*10000.0d;
		}
		System.out.println("\nSaving output image ...");
		outputJulia.save();
		outputJulia.g2d.dispose();
		
		// Mandelbrot set
		step = width/12000.0d;
		z.value(c.real - step*6000.0d, c.imag + step*5000.0d);
		c.value(0.0d, 0.0d);
		System.out.println("Mandelbrot set generation --------------------------------------------------------------------------|100%");
		for(int i = 0; i < 12000; i++) {
			if (i%120 == 0)
				System.out.print("-");
			for (int j = 0; j < 10000; j++) {
				outputMandelbrot.plot(i, j, Iterator.iterate(c, z), isColored);
				z.imag = z.imag - step;
			}
			z.real = z.real + step;
			z.imag = z.imag + step*10000.0d;
		}
		System.out.println("\nSaving output image ...");
		outputMandelbrot.save();
		outputMandelbrot.g2d.dispose();
		
		System.out.println("Pattern generated successfully ...");
	}

}
