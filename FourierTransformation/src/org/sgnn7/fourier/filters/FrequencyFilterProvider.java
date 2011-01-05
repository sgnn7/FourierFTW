package org.sgnn7.fourier.filters;

import java.util.ArrayList;
import java.util.List;

import org.sgnn7.fourier.filters.impl.BandPassFilter;
import org.sgnn7.fourier.filters.impl.HiPassFilter;
import org.sgnn7.fourier.filters.impl.InverseBandPassFilter;
import org.sgnn7.fourier.filters.impl.LowPassFilter;

public class FrequencyFilterProvider {
	List<IFrequencyFilterFunction> filters = new ArrayList<IFrequencyFilterFunction>();

	public FrequencyFilterProvider() {
		filters.add(new LowPassFilter());
		filters.add(new HiPassFilter());
		filters.add(new BandPassFilter());
		filters.add(new InverseBandPassFilter());
	}

	public List<IFrequencyFilterFunction> getFilters() {
		return filters;
	}
}
