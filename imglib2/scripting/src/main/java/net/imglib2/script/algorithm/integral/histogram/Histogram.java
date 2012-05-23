package net.imglib2.script.algorithm.integral.histogram;

import net.imglib2.Localizable;

public abstract class Histogram
{
	public final double min, max, range;
	public final long[] bins;
	public final double[] binValues;
	public final long[] maxPositions, minPositions;
	public long nPixels;

	public Histogram(
			final long[] bins,
			final int numDimensions,
			final double min,
			final double max)
	{
		this.bins = bins;
		this.maxPositions = new long[numDimensions];
		this.minPositions = new long[numDimensions];
		this.min = min;
		this.max = max;
		this.range = max - min;
		this.binValues = new double[bins.length];
	}
	
	public abstract int computeBin(final double value);
	
	public final int nBins() { return bins.length; }

	public final void clearBins() {
		for (int i=0; i<bins.length; ++i) {
			bins[i] = 0;
		}
	}
	
	public final void updatePixelCount() {
		nPixels = maxPositions[0] - minPositions[0];
		for (int d=1; d<maxPositions.length; ++d) {
			nPixels *= maxPositions[d] - minPositions[d];
		}
	}
	
	public final void initPositions(final Localizable l, final int offset) {
		for (int d=0; d<maxPositions.length; ++d) {
			final long p = l.getLongPosition(d);
			maxPositions[d] = p + offset;
			minPositions[d] = p + offset;
		}
	}
	
	public final void updatePositions(final long position, final int d) {
		maxPositions[d] = Math.max(maxPositions[d], position);
		minPositions[d] = Math.min(minPositions[d], position);
	}
}