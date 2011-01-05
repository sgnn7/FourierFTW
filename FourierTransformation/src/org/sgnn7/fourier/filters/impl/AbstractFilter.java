package org.sgnn7.fourier.filters.impl;

import org.sgnn7.fourier.filters.IFrequencyFilterFunction;
import org.sgnn7.fourier.ft.ComplexNumberImage;

public abstract class AbstractFilter implements IFrequencyFilterFunction {
	private int imageWidth;
	private int imageHeight;

	protected abstract int getColorOfPixelAt(int x, int y, int rgb);

	protected abstract boolean isLocationInDomain(int x, int y);

	@Override
	public void applyFilterToImage(ComplexNumberImage complexImage) {
		imageWidth = complexImage.getWidth();
		imageHeight = complexImage.getHeight();

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (isLocationInDomain(x, y)) {
					complexImage.setFrequencyDomainPixel(x, y, true,
							getColorOfPixelAt(x, y, complexImage.getFrequencyDomainPixel(x, y, true)));
				}
			}
		}

	}

	protected int getHeight() {
		return imageHeight;
	}

	protected int getWidth() {
		return imageWidth;
	}

	protected double getDistanceFromCenter(int x, int y) {
		int middleX = getWidth() / 2;
		int middleY = getHeight() / 2;

		int offsetX = (x + getWidth()) % getWidth();
		int offsetY = (y + getHeight()) % getHeight();

		int differenceX = Math.abs(middleX - offsetX);
		int differenceY = Math.abs(middleY - offsetY);

		return Math.sqrt(Math.pow(differenceX, 2) + Math.pow(differenceY, 2));
	}
}
