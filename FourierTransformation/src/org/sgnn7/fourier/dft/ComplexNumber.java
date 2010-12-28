package org.sgnn7.fourier.dft;

public class ComplexNumber {
	private final double real;
	private final double imaginary;

	public ComplexNumber(double real, double imag) {
		this.real = real;
		this.imaginary = imag;
	}

	public double getRealPart() {
		return real;
	}

	public double getImaginaryPart() {
		return imaginary;
	}

	public ComplexNumber plus(ComplexNumber second) {
		ComplexNumber first = this; // invoking object
		double real = first.real + second.real;
		double imag = first.imaginary + second.imaginary;
		return new ComplexNumber(real, imag);
	}

	public ComplexNumber times(double mutiplier) {
		return new ComplexNumber(real * mutiplier, imaginary * mutiplier);
	}
}