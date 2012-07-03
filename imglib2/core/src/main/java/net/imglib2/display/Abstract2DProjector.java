package net.imglib2.display;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;

public abstract class Abstract2DProjector < A, B > implements Projector< A, B >, Positionable, Localizable {
	
		final protected RandomAccessible< A > source;
		final protected long[] position;
		final long[] min;
		final long[] max;
		
		public Abstract2DProjector( final RandomAccessible< A > source)
		{
			this.source = source;
		
			// as this is an 2D projector, we need at least two dimensions,
			// even if the source is one-dimensional
			position = new long[ Math.max( 2, source.numDimensions() ) ];
			min = new long[ position.length ];
			max = new long[ position.length ];
		}

		@Override
		public void bck( final int d )
		{
			position[ d ] -= 1;
		}

		@Override
		public void fwd( final int d )
		{
			position[ d ] += 1;
		}

		@Override
		public void move( final int distance, final int d )
		{
			position[ d ] += distance;
		}

		@Override
		public void move( final long distance, final int d )
		{
			position[ d ] += distance;
		}

		@Override
		public void move( final Localizable localizable )
		{
			for ( int d = 0; d < position.length; ++d )
				position[ d ] += localizable.getLongPosition( d );
		}

		@Override
		public void move( final int[] p )
		{
			for ( int d = 0; d < position.length; ++d )
				position[ d ] += p[ d ];
		}

		@Override
		public void move( final long[] p )
		{
			for ( int d = 0; d < position.length; ++d )
				position[ d ] += p[ d ];
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			for ( int d = 0; d < position.length; ++d )
				position[ d ] = localizable.getLongPosition( d );
		}

		@Override
		public void setPosition( final int[] p )
		{
			for ( int d = 0; d < position.length; ++d )
				position[ d ] = p[ d ];
		}

		@Override
		public void setPosition( final long[] p )
		{
			for ( int d = 0; d < position.length; ++d )
				position[ d ] = p[ d ];
		}

		@Override
		public void setPosition( final int p, final int d )
		{
			position[ d ] = p;
		}

		@Override
		public void setPosition( final long p, final int d )
		{
			position[ d ] = p;
		}

		@Override
		public int numDimensions()
		{
			return position.length;
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ( int )position[ d ];
		}

		@Override
		public long getLongPosition( final int d )
		{
			return position[ d ];
		}

		@Override
		public void localize( final int[] p )
		{
			for ( int d = 0; d < p.length; ++d )
				p[ d ] = ( int )position[ d ];
		}

		@Override
		public void localize( final long[] p )
		{
			for ( int d = 0; d < p.length; ++d )
				p[ d ] = position[ d ];
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return position[ d ];
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return position[ d ];
		}

		@Override
		public void localize( final float[] p )
		{
			for ( int d = 0; d < p.length; ++d )
				p[ d ] = position[ d ];
		}

		@Override
		public void localize( final double[] p )
		{
			for ( int d = 0; d < p.length; ++d )
				p[ d ] = position[ d ];
		}


}
