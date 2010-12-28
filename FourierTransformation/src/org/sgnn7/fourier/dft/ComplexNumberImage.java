package org.sgnn7.fourier.dft;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.sgnn7.fourier.util.ARGBUtils;
import org.sgnn7.fourier.util.ARGBUtils.ColorChannel;

public class ComplexNumberImage {

	Map<ColorChannel, ComplexNumber[][]> spatialImageData = new HashMap<ColorChannel, ComplexNumber[][]>();
	Map<ColorChannel, ComplexNumber[][]> frequencyImageData = new HashMap<ColorChannel, ComplexNumber[][]>();

	private final BufferedImage originalImage;

	public ComplexNumberImage(BufferedImage originalImage) {
		this.originalImage = originalImage;
		for (ColorChannel colorChannel : ColorChannel.values()) {
			spatialImageData.put(colorChannel, getChannelImageData(originalImage, colorChannel));
		}
	}

	private ComplexNumber[][] getChannelImageData(BufferedImage image, ColorChannel channel) {
		ComplexNumber channelData[][] = new ComplexNumber[image.getWidth()][image.getHeight()];
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				channelData[x][y] = new ComplexNumber(ARGBUtils.getChannel(image.getRGB(x, y), channel), 0);
			}
		}
		return channelData;
	}

	public BufferedImage getFrequencyDomainImage(boolean isOffset, boolean doRegenerate) {
		int imageWidth = originalImage.getWidth();
		int imageHeight = originalImage.getHeight();
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		DFTThreadManager<ColorChannel> dftThreadManager = null;
		if (doRegenerate) {
			dftThreadManager = new DFTThreadManager<ColorChannel>(spatialImageData, ColorChannel.values(), false);
		}

		for (ColorChannel colorChannel : ColorChannel.values()) {
			ComplexNumber[][] transformedImage = null;
			if (doRegenerate) {
				transformedImage = dftThreadManager.getDFT(colorChannel);
				frequencyImageData.put(colorChannel, transformedImage);
			} else {
				transformedImage = frequencyImageData.get(colorChannel);
			}

			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int offsetX = isOffset ? (x + (imageWidth / 2)) % imageWidth : x;
					int offsetY = isOffset ? (y + (imageHeight / 2)) % imageHeight : y;
					int color = (int) Math.abs(transformedImage[offsetX][offsetY].getRealPart());
					color = ARGBUtils.clampColorRange(color);
					color = ARGBUtils.shiftToARGBColorChannel(color, colorChannel);
					image.setRGB(x, y, ARGBUtils.addColors(image.getRGB(x, y), ARGBUtils.maskARGBTransparency(color)));
				}
			}
		}

		return image;
	}

	public void setFrequencyDomainPixel(int x, int y, boolean isOffset, int color) {
		int imageWidth = originalImage.getWidth();
		int imageHeight = originalImage.getHeight();
		int offsetX = isOffset ? (x + (imageWidth / 2)) % imageWidth : x;
		int offsetY = isOffset ? (y + (imageHeight / 2)) % imageHeight : y;
		for (ColorChannel colorChannel : ColorChannel.values()) {
			ComplexNumber[][] channelImage = frequencyImageData.get(colorChannel);
			int channelColorValue = ARGBUtils.getChannel(color, colorChannel);
			channelImage[offsetX][offsetY] = new ComplexNumber(channelColorValue, channelColorValue);
		}
	}

	public BufferedImage getSpatialDomainImage() {
		int imageWidth = originalImage.getWidth();
		int imageHeight = originalImage.getHeight();
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		if (frequencyImageData.isEmpty()) {
			System.out.println("Regenerating frequency data");
			getFrequencyDomainImage(true, true);
		}

		DFTThreadManager<ColorChannel> dftThreadManager = new DFTThreadManager<ColorChannel>(frequencyImageData,
				ColorChannel.values(), true);

		for (ColorChannel colorChannel : ColorChannel.values()) {
			ComplexNumber[][] transformedImage = dftThreadManager.getDFT(colorChannel);
			spatialImageData.put(colorChannel, transformedImage);

			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int color = (int) Math.abs(transformedImage[x][y].getRealPart());
					color = ARGBUtils.clampColorRange(color);
					color = ARGBUtils.shiftToARGBColorChannel(color, colorChannel);
					image.setRGB(x, y, ARGBUtils.addColors(image.getRGB(x, y), ARGBUtils.maskARGBTransparency(color)));
				}
			}
		}

		System.out.println("Finished calculating spatial domain image");

		return image;
	}

	double getRawPixelValue(int x, int y, ColorChannel channel) {
		return spatialImageData.get(channel)[x][y].getRealPart();
	}

	public int getWidth() {
		return originalImage.getWidth();
	}

	public int getHeight() {
		return originalImage.getHeight();
	}

}
