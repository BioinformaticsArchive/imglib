
 * @author Stephan Saalfeld
package net.imglib2.algorithm.gauss;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.cursor.LocalizableCursor;
import net.imglib2.function.RealTypeConverter;
import net.imglib2.image.Image;
import net.imglib2.image.ImageFactory;
import net.imglib2.interpolation.Interpolator;
import net.imglib2.interpolation.nearestneighbor.NearestNeighborInterpolatorFactory;
import net.imglib2.outofbounds.OutOfBoundsStrategyMirrorFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 */
public class DownSample<T extends RealType<T>> implements MultiThreaded, OutputAlgorithm<T>, Benchmark
{
	Image<T> input;
	Image<T> downSampled;
	
	float sourceSigma, targetSigma;
	int[] newSize, imgSize;
	float[] scaling;

	String errorMessage = "";
	int numThreads;
	long processingTime;

	public DownSample( final Image<T> image, final int[] newSize, final float sourceSigma, final float targetSigma )
	{
		this.input = image;
		this.newSize = newSize.clone();

		setSourceSigma( sourceSigma );
		setTargetSigma( targetSigma );
		
		if ( input != null )
		{
			this.imgSize = image.getDimensions();

			this.scaling = new float[ image.getNumDimensions() ];
			for ( int d = 0; d < image.getNumDimensions(); ++d )
				this.scaling[ d ] = (float)imgSize[ d ] / (float)newSize[ d ];
		}
		else
		{
			this.imgSize = null;
			this.scaling = null;
		}
					         
		setNumThreads();
		this.processingTime = -1;
	}
	
	public DownSample( final Image<T> image, final float downSamplingFactor )
	{
		setInputImage( image );
		
		if ( input != null )
			setDownSamplingFactor( downSamplingFactor );
		
		setSourceSigma( 0.5f );
		setTargetSigma( 0.5f );

		setNumThreads();
		this.processingTime = -1;
	}
	
	public void setSourceSigma( final float sourceSigma ) { this.sourceSigma = sourceSigma; }
	public void setTargetSigma( final float targetSigma ) { this.targetSigma = targetSigma; }
	public void setDownSamplingFactor( final float factor )
	{
		newSize = new int[ input.getNumDimensions() ];
		scaling = new float[ input.getNumDimensions() ];
		
		for ( int d = 0; d < input.getNumDimensions(); ++d )
		{
			newSize[ d ] = Util.round( input.getDimension(d) * factor );
			scaling[ d ] = 1.0f / factor;
		}
	}
	public void setNewSize( final int[] newSize ) { this.newSize = newSize.clone(); }
	public void setInputImage( final Image<T> image )
	{
		this.input = image;
		if ( input != null )
			this.imgSize = image.getDimensions();
		else
			this.imgSize = null;
	}
	
	public float getSourceSigma() { return sourceSigma; }
	public float getTargetSigma() { return targetSigma; }
	public int[] getNewSize() { return newSize.clone(); } 
	public Image<T> getInputImage() { return input; }

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		
		final int numDimensions = input.getNumDimensions();
		final double[] sigma = new double[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final double s = targetSigma * scaling[ d ]; 
			sigma[ d ] = Math.sqrt( s * s - sourceSigma * sourceSigma );
		}
		final ImageFactory<DoubleType> factory = new ImageFactory<DoubleType>( new DoubleType(), input.getContainerFactory() );
		final GaussianConvolution3<T,DoubleType,T> gauss = new GaussianConvolution3<T,DoubleType,T>( input, factory, input.getImageFactory(), new OutOfBoundsStrategyMirrorFactory<DoubleType>(), 
				new RealTypeConverter<T, DoubleType>(), new RealTypeConverter<DoubleType, T>(), sigma );
		gauss.setNumThreads( getNumThreads() );

		if ( !gauss.checkInput() || !gauss.process() )
		{
			errorMessage = "Gaussian Convolution failed: " + gauss.getErrorMessage();
			return false;
		}
		
		final Image<T> gaussConvolved = gauss.getResult();
		downSampled = input.createNewImage( newSize );
		
		final Interpolator<T> interpolator = gaussConvolved.createInterpolator( new NearestNeighborInterpolatorFactory<T>( new OutOfBoundsStrategyMirrorFactory<T>() ) );		
		final LocalizableCursor<T> cursor = downSampled.createLocalizableCursor();
		
		final int[] pos = new int[ numDimensions ];
		final float[] scaledPos = new float[ numDimensions ];		
		final float[] scalingDim = scaling.clone();
		
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.getPosition( pos );
			
			for ( int d = 0; d < numDimensions; ++d )
				scaledPos[ d ] = pos[ d ] * scalingDim[ d ];

			interpolator.moveTo( scaledPos );
			cursor.getType().set( interpolator.getType() );
		}
		
		cursor.close();
		interpolator.close();
		
		gaussConvolved.close();
		
		processingTime = System.currentTimeMillis() - startTime;
		return true;
	}

	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}

		if ( input == null )
		{
			errorMessage = "Input image is null";
			return false;
		}
		
		if ( newSize == null )
		{
			errorMessage = "New size of image is null";
			return false;			
		}

		for ( int d = 0; d < input.getNumDimensions(); ++d )
		{
			if ( newSize[ d ] > imgSize[ d ] )
			{
				errorMessage = "New image supposed to be bigger than input image in dimension " + d + ", " +
							   "this algorithm is only for downsampling (" + newSize[ d ] + " > " + imgSize[ d ] + " )";
				return false;
			}				
		}
		
		return true;
	}

	@Override
	public String getErrorMessage(){ return errorMessage; }

	@Override
	public Image<T> getResult() { return downSampled; }

	@Override
	public long getProcessingTime() { return processingTime; }

	@Override
	public int getNumThreads() { return numThreads; }

	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }
}
