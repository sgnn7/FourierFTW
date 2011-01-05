package org.sgnn7.fourier.ft;

public interface ITransformer {
	ComplexNumber[][] calculateFT(ComplexNumber[][] originalImage, boolean inverse);
}
