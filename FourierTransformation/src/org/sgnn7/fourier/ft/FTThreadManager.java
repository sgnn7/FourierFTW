package org.sgnn7.fourier.ft;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FTThreadManager<T> {
	private final Map<T, ComplexNumber[][]> imageDataMap;
	private final T[] mapValues;
	private final boolean isInverse;

	private Map<T, Future<ComplexNumber[][]>> runnerThreadMap = new HashMap<T, Future<ComplexNumber[][]>>();
	private ExecutorService executor;

	public FTThreadManager(Map<T, ComplexNumber[][]> imageDataMap, T[] mapValues, boolean isInverse) {
		this.imageDataMap = imageDataMap;
		this.mapValues = mapValues;
		this.isInverse = isInverse;

		runThreads();
	}

	private void runThreads() {
		executor = Executors.newFixedThreadPool(mapValues.length);

		for (T mapValue : mapValues) {
			ComplexNumber[][] channelImage = imageDataMap.get(mapValue);
			boolean useFFT = imageHasPowerOf2Dimensions(channelImage);
			ITransformer transformer = useFFT ? new FFT() : new DFT();
			FTCallableRunner dftRunner = new FTCallableRunner(channelImage, transformer, isInverse);
			System.out.println("Calculating transform for " + mapValue + " channel using "
					+ transformer.getClass().getSimpleName() + "[inverse = " + isInverse + "]");
			Future<ComplexNumber[][]> runnerFuture = executor.submit(dftRunner);
			runnerThreadMap.put(mapValue, runnerFuture);
		}

		executor.shutdown();
	}

	private boolean imageHasPowerOf2Dimensions(ComplexNumber[][] channelImage) {
		return isPowerOf2(channelImage.length) && isPowerOf2(channelImage[0].length);
	}

	private boolean isPowerOf2(int length) {
		return (length & (length - 1)) == 0;
	}

	public ComplexNumber[][] getFT(T channel) {
		ComplexNumber[][] convertedChannelImage = null;
		try {
			Future<ComplexNumber[][]> future = runnerThreadMap.get(channel);
			while (!future.isDone()) {
				Thread.yield();
				Thread.sleep(200);
			}
			convertedChannelImage = future.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedChannelImage;
	}

	private class FTCallableRunner implements Callable<ComplexNumber[][]> {
		private final boolean inverse;
		private final ComplexNumber[][] originalImage;
		private final ITransformer transformer;

		public FTCallableRunner(ComplexNumber[][] originalImage, ITransformer transformer, boolean inverse) {
			this.originalImage = originalImage;
			this.transformer = transformer;
			this.inverse = inverse;
		}

		@Override
		public ComplexNumber[][] call() throws Exception {
			return transformer.calculateFT(originalImage, inverse);
		}
	}
}
