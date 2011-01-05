package org.sgnn7.fourier.ft;

public class FFT implements ITransformer {
	@Override
	public ComplexNumber[][] calculateFT(ComplexNumber[][] originalImage, boolean inverse) {
		int imageWidth = originalImage.length;
		int imageHeight = originalImage[0].length;

		ComplexNumber[][] transformedImage = new ComplexNumber[imageWidth][imageHeight];

		int log2Width = findLog2Of(imageWidth);
		int log2Height = findLog2Of(imageHeight);

		// copy original image to transformed one
		for (int x = 0; x < imageWidth; x++)
			for (int y = 0; y < imageHeight; y++)
				transformedImage[x][y] = new ComplexNumber(originalImage[x][y]);

		// Bit reversal of each row
		for (int y = 0; y < imageHeight; y++) {
			// for each row
			int offset = 0;
			for (int i = 0; i < imageWidth - 1; i++) {
				transformedImage[i][y] = new ComplexNumber(originalImage[offset][y]);
				int halfPoint = imageWidth / 2;
				while (halfPoint <= offset) {
					offset -= halfPoint;
					halfPoint /= 2;
				}
				offset += halfPoint;
			}
		}
		// Bit reversal of each column
		for (int x = 0; x < imageWidth; x++) {
			// for each column
			int j = 0;
			for (int i = 0; i < imageHeight - 1; i++) {
				if (i < j) {
					ComplexNumber tempComplex = new ComplexNumber(transformedImage[x][i]);
					transformedImage[x][i] = new ComplexNumber(transformedImage[x][j]);
					transformedImage[x][j] = tempComplex;
				}
				int halfPoint = imageHeight / 2;
				while (halfPoint <= j) {
					j -= halfPoint;
					halfPoint /= 2;
				}
				j += halfPoint;
			}
		}

		// columns
		for (int x = 0; x < imageWidth; x++) {
			double cosine = -1.0;
			double sine = 0.0;
			int l1 = 1, l2 = 1;
			for (int l = 0; l < log2Width; l++) {
				l1 = l2;
				l2 <<= 1;
				double u1 = 1.0;
				double u2 = 0.0;
				for (int j = 0; j < l1; j++) {
					for (int i = j; i < imageWidth; i += l2) {
						int i1 = i + l1;
						double t1 = u1 * transformedImage[x][i1].getRealPart() - u2
								* transformedImage[x][i1].getImaginaryPart();
						double t2 = u1 * transformedImage[x][i1].getImaginaryPart() + u2
								* transformedImage[x][i1].getRealPart();
						transformedImage[x][i1] = transformedImage[x][i].plus(new ComplexNumber(-t1, -t2));
						transformedImage[x][i] = transformedImage[x][i].plus(new ComplexNumber(t1, t2));
					}
					double z = u1 * cosine - u2 * sine;
					u2 = u1 * sine + u2 * cosine;
					u1 = z;
				}
				sine = Math.sqrt((1.0 - cosine) / 2.0);
				if (!inverse)
					sine = -sine;
				cosine = Math.sqrt((1.0 + cosine) / 2.0);
			}
		}

		// rows
		for (int y = 0; y < imageHeight; y++) {
			double cosine = -1.0;
			double sine = 0.0;
			int l1 = 1, l2 = 1;
			for (int l = 0; l < log2Height; l++) {
				l1 = l2;
				l2 <<= 1;
				double u1 = 1.0;
				double u2 = 0.0;
				for (int j = 0; j < l1; j++) {
					for (int i = j; i < imageWidth; i += l2) {
						int i1 = i + l1;
						double t1 = u1 * transformedImage[i1][y].getRealPart() - u2
								* transformedImage[i1][y].getImaginaryPart();
						double t2 = u1 * transformedImage[i1][y].getImaginaryPart() + u2
								* transformedImage[i1][y].getRealPart();
						transformedImage[i1][y] = transformedImage[i][y].plus(new ComplexNumber(-t1, -t2));
						transformedImage[i][y] = transformedImage[i][y].plus(new ComplexNumber(t1, t2));
					}
					double z = u1 * cosine - u2 * sine;
					u2 = u1 * sine + u2 * cosine;
					u1 = z;
				}
				sine = Math.sqrt((1.0 - cosine) / 2.0);
				if (!inverse)
					sine = -sine;
				cosine = Math.sqrt((1.0 + cosine) / 2.0);
			}
		}

		int dimension;
		if (inverse)
			dimension = imageWidth;
		else
			dimension = imageHeight;
		for (int x = 0; x < imageWidth; x++)
			for (int y = 0; y < imageHeight; y++) {
				transformedImage[x][y] = transformedImage[x][y].times(1 / (double) dimension);
			}

		return transformedImage;
	}

	private int findLog2Of(int imageWidth) {
		int powerCount = 0;
		while ((imageWidth >>= 1) > 0)
			powerCount++;
		return powerCount;
	}
}
