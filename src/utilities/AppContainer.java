package utilities;

import objects.ComplexNumber;
import services.PatternGenerator;

public class AppContainer {
	
	// main
	public static void main(String[] args) {
		double time = System.currentTimeMillis();
		ComplexNumber center = new ComplexNumber();
		double width = 3.0d;
		center.value(-0.5d, 0.0d);
		PatternGenerator.generate(center, width, true);
		System.out.println("Time taken ... " + (System.currentTimeMillis() - time)/60000.0d + " mins");
	}
	
}