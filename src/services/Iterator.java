package services;

import objects.ComplexNumber;

public class Iterator {

	// returns the number of iterations when the result leaves the 2 units radius circle (max iteration 400)
	public static int iterate(ComplexNumber z, ComplexNumber c) {
		ComplexNumber result;
		ComplexNumber zn = z.clone();
		int i = 0;
		for (; i < 400; i++) {
			result = ComplexNumber.add(ComplexNumber.multiply(zn, zn), c);
			if((result.real*result.real + result.imag*result.imag) >= 4) {
				return i;
			}
			zn = result;
		}
		return i;
	}
}
