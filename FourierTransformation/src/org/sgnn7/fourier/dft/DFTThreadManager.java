package org.sgnn7.fourier.dft;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class DFTThreadManager<T> {
	private final Map<T, ComplexNumber[][]> imageDataMap;
	private final T[] mapValues;
	private final boolean isInverse;
	private Map<T, Future<ComplexNumber[][]>> runnerThreadMap = new HashMap<T, Future<ComplexNumber[][]>>();
	private ExecutorService executor;

	public DFTThreadManager(Map<T, ComplexNumber[][]> imageDataMap, T[] mapValues, boolean isInverse) {
		this.imageDataMap = imageDataMap;
		this.mapValues = mapValues;
		this.isInverse = isInverse;

		runThreads();
	}

	private void runThreads() {
		executor = Executors.newFixedThreadPool(mapValues.length);

		for (T mapValue : mapValues) {
			ComplexNumber[][] channelImage = imageDataMap.get(mapValue);
			DFTCallableRunner dftRunner = new DFTCallableRunner(channelImage, isInverse);
			System.out.println("Calculating DFT for " + mapValue + " channel");
			Future<ComplexNumber[][]> runnerFuture = executor.submit(dftRunner);
			runnerThreadMap.put(mapValue, runnerFuture);
		}

		executor.shutdown();
	}

	public ComplexNumber[][] getDFT(T channel) {
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

	private class DFTCallableRunner implements Callable<ComplexNumber[][]> {
		private final boolean inverse;
		private final ComplexNumber[][] originalImage;

		public DFTCallableRunner(ComplexNumber[][] originalImage, boolean inverse) {
			this.originalImage = originalImage;
			this.inverse = inverse;
		}

		@Override
		public ComplexNumber[][] call() throws Exception {
			return new DFT().calculateDFT(originalImage, inverse);
		}
	}
}
