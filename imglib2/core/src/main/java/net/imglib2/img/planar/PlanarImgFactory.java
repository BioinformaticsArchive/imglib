/**
 * Copyright (c) 2009--2010, Funke, Preibisch, Saalfeld & Schindelin
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
package net.imglib2.img.planar;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;

/**
 * Factory that creates an appropriate {@link PlanarImg}.
 * 
 * @author Jan Funke, Stephan Preibisch, Stephan Saalfeld, Johannes Schindelin
 */
public class PlanarImgFactory< T extends NativeType<T> > extends NativeImgFactory< T >
{
	@Override
	public PlanarImg< T, ? > create( final long[] dim, final T type )
	{
		return ( PlanarImg< T, ? > ) type.createSuitableNativeImg( this, dim );
	}
	
	@Override
	public NativeImg< T, BitArray > createBitInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarImg< T, BitArray >( new BitArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, ByteArray > createByteInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarImg< T , ByteArray >( new ByteArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, CharArray > createCharInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarImg< T, CharArray >( new CharArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, DoubleArray > createDoubleInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarImg< T, DoubleArray >( new DoubleArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, FloatArray > createFloatInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarImg< T, FloatArray >( new FloatArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, IntArray > createIntInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarImg< T, IntArray >( new IntArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, LongArray > createLongInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarImg< T, LongArray >( new LongArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public NativeImg< T, ShortArray > createShortInstance( final long[] dimensions, final int entitiesPerPixel )
	{
		return new PlanarImg< T, ShortArray >( new ShortArray( 1 ), dimensions, entitiesPerPixel );
	}
	
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public <S> ImgFactory<S> imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new PlanarImgFactory();
		else
			throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}	
}