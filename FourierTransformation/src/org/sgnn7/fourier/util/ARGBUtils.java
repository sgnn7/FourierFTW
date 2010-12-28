package org.sgnn7.fourier.util;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ARGBUtils {
	public enum ColorChannel {
		RED(2), GREEN(1), BLUE(0);

		private int index;

		private ColorChannel(int index) {
			this.index = index;
		}

		public int getColorIndex() {
			return index;
		}
	}

	public static int shiftToARGBColorChannel(int color, ColorChannel channel) {
		int shiftAmount = channel.getColorIndex() * 8;
		return color << shiftAmount;
	}

	public static int maskARGBTransparency(int color) {
		int transparencyMask = 0xff << 24;
		return transparencyMask | color;
	}

	public static int addColors(int color1, int color2) {
		return color1 | color2;
	}

	public static int clampColorRange(int color) {
		int clippedColor = color;
		if (clippedColor < 0)
			clippedColor = 0;
		if (clippedColor > 255)
			clippedColor = 255;
		return clippedColor;
	}

	public static int getChannel(int combinedColor, ColorChannel channel) {
		Color color = getColorFromARGB(combinedColor);
		int channelValue = 0;
		switch (channel) {
		case RED:
			channelValue = color.getRed();
			break;
		case GREEN:
			channelValue = color.getGreen();
			break;
		case BLUE:
			channelValue = color.getBlue();
			break;
		}
		return channelValue;
	}

	private static Color getColorFromARGB(int colorValue) {
		int red = (colorValue >> 16) & 0xff;
		int green = (colorValue >> 8) & 0xff;
		int blue = colorValue & 0xff;
		return new Color(red, green, blue);
	}

	public static BufferedImage scaleImage(BufferedImage originalImage, int multiplier) {
		int imageWidth = originalImage.getWidth();
		int imageHeight = originalImage.getHeight();
		BufferedImage scaledImage = new BufferedImage(imageWidth * multiplier, imageHeight * multiplier,
				BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < scaledImage.getWidth(); x++) {
			for (int y = 0; y < scaledImage.getHeight(); y++) {
				int scaledX = new Double(x / (double) scaledImage.getWidth() * originalImage.getWidth()).intValue();
				int scaledY = new Double(y / (double) scaledImage.getHeight() * originalImage.getHeight()).intValue();
				scaledImage.setRGB(x, y, originalImage.getRGB(scaledX, scaledY));
			}
		}

		return scaledImage;
	}
}
