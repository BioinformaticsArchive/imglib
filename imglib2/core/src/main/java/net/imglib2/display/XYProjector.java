/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.imglib2.display;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class XYProjector< A, B > extends AbstractXYProjector< A, B >
{
	final protected IterableInterval< B > target;
	final int numDimensions;
	
	public XYProjector( final RandomAccessible< A > source, final IterableInterval< B > target, final Converter< A, B > converter )
	{
		super( source, converter );
		this.target = target;
		this.numDimensions = source.numDimensions();
	}

	@Override
	public void map()
	{
		for ( int d = 2; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];
		
		min[ 0 ] = target.min( 0 );
		min[ 1 ] = target.min( 1 );
		max[ 0 ] = target.max( 0 );
		max[ 1 ] = target.max( 1 );
		final FinalInterval sourceInterval = new FinalInterval( min, max );
		
		final Cursor< B > targetCursor = target.cursor();
		final RandomAccess< A > sourceRandomAccess = source.randomAccess( sourceInterval );
		sourceRandomAccess.setPosition( position );
		while ( targetCursor.hasNext() )
		{
			final B b = targetCursor.next();
			sourceRandomAccess.setPosition( targetCursor.getLongPosition( 0 ), 0 );
			if ( numDimensions > 1 )
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( 1 ), 1 );
			converter.convert( sourceRandomAccess.get(), b );
		}
	}
}
