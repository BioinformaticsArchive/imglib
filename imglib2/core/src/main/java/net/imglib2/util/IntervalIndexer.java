/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.util;


/**
 * N-dimensional data is often stored in a flat 1-dimensional array.
 * This class provides convenience methods to translate between N-dimensional
 * indices (positions) and 1-dimensional indices.
 *
 *
 * @author Tobias Pietzsch
 */
public class IntervalIndexer
{
	final static public int positionToIndex( final int[] position, final int[] dimensions )
	{
		final int maxDim = dimensions.length - 1;
		int i = position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ];
		return i;
	}

	final static public int positionToIndex( final long[] position, final int[] dimensions )
	{
		final int maxDim = dimensions.length - 1;
		int i = ( int )position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + ( int )position[ d ];
		return i;
	}

	final static public long positionToIndex( final long[] position, final long[] dimensions )
	{
		final int maxDim = dimensions.length - 1;
		long i = position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ];
		return i;
	}

	final static public long positionWithOffsetToIndex( final long[] position, final long[] dimensions, final long[] offsets )
	{
		final int maxDim = dimensions.length - 1;
		long i = position[ maxDim ] - offsets[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ] - offsets[ d ];
		return i;
	}

	final static public int positionWithOffsetToIndex( final int[] position, final int[] dimensions, final int[] offsets )
	{
		final int maxDim = dimensions.length - 1;
		int i = position[ maxDim ] - offsets[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ] - offsets[ d ];
		return i;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( long index, final long[] dimensions, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = ( int )( index - j * dimensions[ d ] );
			index = j;
		}
		position[ maxDim ] = ( int )index;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final long[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( long index, final long[] dimensions, final long[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final float[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( long index, final long[] dimensions, final float[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final double[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( long index, final long[] dimensions, final double[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPositionWithOffset( int index, final int[] dimensions, final int[] offsets, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final long[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = ( int )( index - j * dimensions[ d ] + offsets[ d ] );
			index = j;
		}
		position[ maxDim ] = ( int )( index + offsets[ maxDim ] );
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final float[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final double[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public int indexToPosition( final int index, final int[] dimensions, final int dimension )
	{
		int step = 1;
		for ( int d = 0; d < dimension; ++d )
			step *= dimensions[ d ];
		return ( index / step ) % dimensions[ dimension ];
	}

	final static public long indexToPosition( final long index, final long[] dimensions, final int dimension )
	{
		int step = 1;
		for ( int d = 0; d < dimension; ++d )
			step *= dimensions[ d ];
		return ( index / step ) % dimensions[ dimension ];
	}

	final static public int indexToPositionWithOffset( final int index, final int[] dimensions, final int[] offsets, final int dimension )
	{
		return indexToPosition( index, dimensions, dimension ) + offsets[ dimension ];
	}

	final static public int indexToPosition( final int index, final int[] dimensions, final int[] steps, final int dimension )
	{
		return ( index / steps[ dimension ] ) % dimensions[ dimension ];
	}

	final static public long indexToPosition( final long index, final long[] dimensions, final long[] steps, final int dimension )
	{
		return ( index / steps[ dimension ] ) % dimensions[ dimension ];
	}

	final static public int indexToPositionWithOffset( final int index, final int[] dimensions, final int[] steps, final int[] offset, final int dimension )
	{
		return indexToPosition( index, dimensions, steps, dimension ) + offset[ dimension ];
	}

	final static public long indexToPositionWithOffset( final long index, final long[] dimensions, final long[] steps, final long[] offsets, final int dimension )
	{
		return indexToPosition( index, dimensions, steps, dimension ) + offsets[ dimension ];
	}




	/**
	 * Create allocation step array from the dimensions of an N-dimensional array.
	 *
	 * @param dimensions
	 * @param steps
	 */
	public static void createAllocationSteps( final long[] dimensions, final long[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dimensions.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dimensions[ d - 1 ];
	}

	/**
	 * Create allocation step array from the dimensions of an N-dimensional array.
	 *
	 * @param dimensions
	 * @param steps
	 */
	public static void createAllocationSteps( final int[] dimensions, final int[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dimensions.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dimensions[ d - 1 ];
	}



}
