package org.sgnn7.fourier.dft;


public class DFT {
	static final double PI = 3.1415926535897932384626433832795;
	static final double TWO_PI = 2 * PI;

	public ComplexNumber[][] calculateDFT(ComplexNumber[][] originalImage, boolean inverse) {
		int imageWidth = originalImage.length;
		int imageHeight = originalImage[0].length;

		ComplexNumber[][] tempAccumulatorImage = new ComplexNumber[imageWidth][imageHeight];
		ComplexNumber[][] transformedImage = new ComplexNumber[imageWidth][imageHeight];

		initializeArray(transformedImage, imageWidth, imageHeight);

		// columns
		for (int x = 0; x < imageWidth; x++) {
			for (int y2 = 0; y2 < imageHeight; y2++) {
				tempAccumulatorImage[x][y2] = new ComplexNumber(0, 0);
				for (int y = 0; y < imageHeight; y++) {
					double a = TWO_PI * y2 * y / imageHeight;
					a = inverse ? -a : a;
					ComplexNumber addedValue = getAdditives(originalImage[x][y], a);
					tempAccumulatorImage[x][y2] = tempAccumulatorImage[x][y2].plus(addedValue);
				}
			}
		}

		// rows
		for (int y = 0; y < imageHeight; y++) {
			for (int x2 = 0; x2 < imageWidth; x2++) {
				for (int x = 0; x < imageWidth; x++) {
					double a = TWO_PI * x2 * x / imageWidth;
					a = inverse ? -a : a;
					ComplexNumber addedValue = getAdditives(tempAccumulatorImage[x][y], a);
					transformedImage[x2][y] = transformedImage[x2][y].plus(addedValue);
				}
				if (inverse) {
					transformedImage[x2][y] = transformedImage[x2][y].times(1 / (double) imageWidth);
				} else {
					transformedImage[x2][y] = transformedImage[x2][y].times(1 / (double) imageHeight);
				}
			}
		}

		return transformedImage;
	}

	private ComplexNumber getAdditives(ComplexNumber complex, double a) {
		double sineValue = Math.sin(a);
		double cosineValue = Math.cos(a);
		return new ComplexNumber(getRealAdditive(complex, sineValue, cosineValue), getImaginaryAdditive(complex, sineValue,
				cosineValue));
	}

	private double getRealAdditive(ComplexNumber complex, double sineValue, double cosineValue) {
		return complex.getRealPart() * cosineValue - complex.getImaginaryPart() * sineValue;
	}

	private double getImaginaryAdditive(ComplexNumber complex, double sineValue, double cosineValue) {
		return complex.getRealPart() * sineValue + complex.getImaginaryPart() * cosineValue;
	}

	private void initializeArray(ComplexNumber[][] transformed, int imageWidth, int imageHeight) {
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				transformed[x][y] = new ComplexNumber(0, 0);
			}
		}
	}
}
