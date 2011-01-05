package org.sgnn7.fourier.ft;

/*
 Copyright (c) 2004-2007, Lode Vandevenne

 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
