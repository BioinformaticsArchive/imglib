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


package net.imglib2.type.numeric.integer;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;

/**
 * A {@link Type} with arbitrary bit depth up to maximum 64 bits.
 * The behavior beyond 64 bits is undefined.
 * 
 * The performance of this type is traded off for the gain in memory storage.
 * The {@link #set(long)} operation takes have the time as the {@link #get} operation.
 * The performance may degrade very slightly with increasing bit depth, but the decrease is barely noticeable.
 *
 * @author Albert Cardona
 */
public class Unsigned2BitType extends AbstractIntegerType<Unsigned2BitType> implements NativeType<Unsigned2BitType>
{
	private int i = 0;

	final protected NativeImg<Unsigned2BitType, ? extends LongAccess> img;

	// the DataAccess that holds the information
	protected LongAccess dataAccess;

	// A mask for bit and, containing nBits of 1
	private final long mask;

	// this is the constructor if you want it to read from an array
	public Unsigned2BitType(
			final NativeImg<Unsigned2BitType,
			? extends LongAccess> bitStorage)
	{
		img = bitStorage;
		this.mask = 3; // 11 in binary
		updateIndex( 0 );
	}

	// this is the constructor if you want it to be a variable
	public Unsigned2BitType( final long value )
	{
		this( (NativeImg<Unsigned2BitType, ? extends LongAccess>)null );
		updateIndex( 0 );
		dataAccess = new LongArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public Unsigned2BitType( final LongAccess access )
	{
		this( (NativeImg<Unsigned2BitType, ? extends LongAccess>)null );
		updateIndex( 0 );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public Unsigned2BitType() { this( 0 ); }

	@Override
	public NativeImg<Unsigned2BitType, ? extends LongAccess> createSuitableNativeImg( final NativeImgFactory<Unsigned2BitType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<Unsigned2BitType, ? extends LongAccess> container = storageFactory.createLongInstance( dim, 32 );

		// create a Type that is linked to the container
		final Unsigned2BitType linkedType = new Unsigned2BitType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public void updateContainer( final Object c ) { dataAccess = img.update( c ); }

	@Override
	public Unsigned2BitType duplicateTypeOnSameNativeImg() { return new Unsigned2BitType( img ); }

	public long get() {
		final int k = i * 2;
		return (dataAccess.getValue(k >>> 6) >>> (k % 64)) & mask;
	}

	// Crops value to within mask
	public void set( final long value ) {
		final int k = i * 2;
		final int i1 = k >>> 6; // k / 64;
		final long shift = k % 64;
		// Clear the bits first, then and the masked value
		dataAccess.setValue(i1, (dataAccess.getValue(i1) & ~(mask << shift)) | ((value & mask) << shift));
	}

	@Override
	public int getInteger() { return (int)get(); }

	@Override
	public long getIntegerLong() { return get(); }

	@Override
	public void setInteger( final int f ) { set( f ); }

	@Override
	public void setInteger( final long f ) { set( f ); }

	/** The maximum value that can be stored is {@code Math.pow(2, nBits) -1}. */
	@Override
	public double getMaxValue() { return 3; }
	@Override
	public double getMinValue()  { return 0; }

	@Override
	public int getIndex() { return i; }

	@Override
	public void updateIndex( final int index )
	{
		i = index;
	}

	@Override
	public void incIndex()
	{
		++i;
	}
	@Override
	public void incIndex( final int increment )
	{
		i += increment;
	}
	@Override
	public void decIndex()
	{
		--i;
	}
	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
	}

	@Override
	public Unsigned2BitType createVariable(){ return new Unsigned2BitType( 0 ); }

	@Override
	public Unsigned2BitType copy(){ return new Unsigned2BitType( get() ); }

	@Override
	public int getEntitiesPerPixel() { return 1; }

	@Override
	public int getBitsPerPixel() { return 2; }

	@Override
	public void inc() {
		set(get() + 1);
	}

	@Override
	public void dec() {
		set(get() - 1);
	}

	@Override
	public void add(final Unsigned2BitType t) {
		set(get() + t.get());
	}

	@Override
	public void sub(final Unsigned2BitType t) {
		set(get() - t.get());
	}

	@Override
	public void mul(final Unsigned2BitType t) {
		set(get() * t.get());
	}

	@Override
	public void div(final Unsigned2BitType t) {
		set(get() / t.get());
	}
}