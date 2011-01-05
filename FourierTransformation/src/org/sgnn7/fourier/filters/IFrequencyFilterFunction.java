package org.sgnn7.fourier.filters;

import org.sgnn7.fourier.ft.ComplexNumberImage;

public interface IFrequencyFilterFunction {

	String getName();

	int getAcceleratorKey();

	void applyFilterToImage(ComplexNumberImage complexImage);
}
