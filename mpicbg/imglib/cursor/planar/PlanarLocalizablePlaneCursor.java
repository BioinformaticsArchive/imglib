/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
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
package mpicbg.imglib.cursor.planar;

import mpicbg.imglib.container.planar.PlanarContainer;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * @param <T>
 *
 * @author Stephan Preibisch, Curtis Rueden and Stephan Saalfeld
 */
public class PlanarLocalizablePlaneCursor< T extends Type<T>> extends PlanarLocalizableCursor< T > implements LocalizablePlaneCursor< T >
{
	private int xIndex, yIndex, xSize, ySize, pos, maxPos;
	final private int[] sliceSteps;
	private int width, planeSize;
	
	public PlanarLocalizablePlaneCursor( final PlanarContainer< T,? > container, final Image< T > image, final T type ) 
	{
		super( container, image, type );
		sliceSteps = new int[ image.getNumDimensions() + 1 ];
		if ( sliceSteps.length > 2 )
		{
			sliceSteps[ 2 ] = 1;
			for ( int i = 3; i < sliceSteps.length; ++i )
			{
				final int j = i - 1;
				sliceSteps[ i ] = dimensions[ j ] * sliceSteps[ j ];
			}
		}
		width = image.getDimension( 0 );
		planeSize = width * image.getDimension( 1 );
	}	
	
	@Override 
	public boolean hasNext()
	{
		if ( pos < maxPos )
			return true;
		else
			return false;
	}
	
	@Override
	public void fwd()
	{
		++pos;
		
		boolean needUpdate = false;
		
		if ( ++position[ xIndex ] == xSize )
		{
			/* reset x */
			position[ xIndex ] = 0;
			switch ( xIndex )
			{
			case 0:
				type.decIndex( width );
				break;
			case 1:
				type.decIndex( planeSize );
				break;
			default:
				sliceIndex -= sliceSteps[ xIndex + 1 ];
				needUpdate = true;
			}
			
			/* increase y */
			++position[ yIndex ];
			switch ( yIndex )
			{
			case 0:
				type.incIndex();
				break;
			case 1:
				type.incIndex( width );
				break;
			default:
				sliceIndex += sliceSteps[ yIndex ];
				needUpdate = true;
			}
		}
		else
		{
			/* increase x */
			switch ( xIndex )
			{
			case 0:
				type.incIndex();
				break;
			case 1:
				type.incIndex( width );
				break;
			default:
				sliceIndex += sliceSteps[ xIndex ];
				needUpdate = true;
			}
		}
		
		if ( needUpdate )
			type.updateContainer( this );
	}

	@Override
	public void reset( final int planeDimA, final int planeDimB, final int[] dimensionPositions )
	{
		this.xIndex = planeDimA;
		this.yIndex = planeDimB;

		// store the current position
    	final int[] dimPos = dimensionPositions.clone();

		dimPos[ planeDimA ] = 0;
		dimPos[ planeDimB ] = 0;
		setPosition( dimPos );				
    	
    	xSize = image.getDimension( planeDimA );
    	ySize = image.getDimension( planeDimB );
    	
    	maxPos = xSize * ySize;
		pos = 0;
		
		isClosed = false;

		switch ( planeDimA )
		{
		case 0:
			type.decIndex();
			break;
		case 1:
			type.decIndex( -width );
			break;
		default:
			sliceIndex -= sliceSteps[ planeDimA ];
		}
		
		position[ planeDimA ] = -1;
	}

	@Override
	public void reset( final int planeDimA, final int planeDimB )
	{
		if ( dimensions == null )
			return;

		reset( planeDimA, planeDimB, new int[ numDimensions ] );
	}
	
	@Override
	public void reset()
	{
		if ( dimensions == null )
			return;
		
		reset( 0, 1, new int[ numDimensions ] );		
	}

	@Override
	public void getPosition( final int[] p )
	{
		for ( int d = 0; d < numDimensions; d++ )
			p[ d ] = this.position[ d ];
	}
	
	@Override
	public int[] getPosition(){ return position.clone(); }
	
	@Override
	public int getPosition( final int dim ){ return position[ dim ]; }
	
	private void setPosition( final int[] position )
	{
		type.updateIndex( container.getIndex( position ) );
		
		for ( int d = 0; d < numDimensions; d++ )
			this.position[ d ] = position[ d ];
		
		sliceIndex = 0;
		for ( int d = 2; d < numDimensions; ++d )
			sliceIndex += position[ d ] * sliceSteps[ d ];
		
		type.updateContainer( this );
	}
	
}
