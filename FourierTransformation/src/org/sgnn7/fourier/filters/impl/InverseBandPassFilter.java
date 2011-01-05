package org.sgnn7.fourier.filters.impl;

import java.awt.event.KeyEvent;

public class InverseBandPassFilter extends AbstractFilter {

	@Override
	public String getName() {
		return "Inverse Band-Pass";
	}

	@Override
	public int getAcceleratorKey() {
		return KeyEvent.VK_B;
	}

	@Override
	protected int getColorOfPixelAt(int x, int y, int rgb) {
		return 0;
	}

	@Override
	protected boolean isLocationInDomain(int x, int y) {
		double innerRadius = getWidth() / 30;
		double outerRadius = getWidth() / 3.5;
		double distanceFromCenter = getDistanceFromCenter(x, y);
		return distanceFromCenter > innerRadius && distanceFromCenter < outerRadius;
	}
}
