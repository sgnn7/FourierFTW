package org.sgnn7.fourier.filters.impl;

import java.awt.event.KeyEvent;

public class HiPassFilter extends AbstractFilter {
	@Override
	public String getName() {
		return "Hi-Pass";
	}

	@Override
	public int getAcceleratorKey() {
		return KeyEvent.VK_H;
	}

	@Override
	protected int getColorOfPixelAt(int x, int y, int rgb) {
		return 0;
	}

	@Override
	protected boolean isLocationInDomain(int x, int y) {
		double radius = getWidth() / 30;
		return getDistanceFromCenter(x, y) < radius;
	}
}
