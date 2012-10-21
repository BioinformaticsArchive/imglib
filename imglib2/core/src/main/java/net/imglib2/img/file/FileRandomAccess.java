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

package net.imglib2.img.file;

import net.imglib2.AbstractLocalizableInt;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;

/**
 * {@link RandomAccess} on a {@link FileImg}.
 * 
 * @param <T>
 * 
 * @author ImgLib2 developers
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class FileRandomAccess<T extends ExternalizableType<T>> extends
		AbstractLocalizableInt implements RandomAccess<T> {
	private int i;

	private final FileImg<T> container;

	public FileRandomAccess(final FileRandomAccess<T> randomAccess) {
		super(randomAccess.numDimensions());

		container = randomAccess.container;

		for (int d = 0; d < n; ++d)
			position[d] = randomAccess.position[d];

		i = randomAccess.i;
	}

	public FileRandomAccess(final FileImg<T> container) {
		super(container.numDimensions());

		this.container = container;

		i = 0;
	}

	@Override
	public T get() {
		return container.get(i);
	}

	public void set(final T t) {
		container.set(t, i);
	}

	@Override
	public void fwd(final int d) {
		i += container.step[d];
		++position[d];
	}

	@Override
	public void bck(final int d) {
		i -= container.step[d];
		--position[d];
	}

	@Override
	public void move(final int distance, final int d) {
		i += container.step[d] * distance;
		position[d] += distance;
	}

	@Override
	public void move(final long distance, final int d) {
		move((int) distance, d);
	}

	@Override
	public void move(final Localizable localizable) {
		for (int d = 0; d < n; ++d)
			move(localizable.getIntPosition(d), d);
	}

	@Override
	public void move(final int[] distance) {
		for (int d = 0; d < n; ++d)
			move(distance[d], d);
	}

	@Override
	public void move(final long[] distance) {
		for (int d = 0; d < n; ++d)
			move((int) distance[d], d);
	}

	@Override
	public void setPosition(final Localizable localizable) {
		localizable.localize(position);
		i = position[0];
		for (int d = 1; d < n; ++d)
			i += position[d] * container.step[d];
	}

	@Override
	public void setPosition(final int[] position) {
		i = position[0];
		this.position[0] = i;
		for (int d = 1; d < n; ++d) {
			final int p = position[d];
			i += p * container.step[d];
			this.position[d] = p;
		}
	}

	@Override
	public void setPosition(final long[] position) {
		i = (int) position[0];
		this.position[0] = i;
		for (int d = 1; d < n; ++d) {
			final int p = (int) position[d];
			i += p * container.step[d];
			this.position[d] = p;
		}
	}

	@Override
	public void setPosition(final int position, final int d) {
		i += container.step[d] * (position - this.position[d]);
		this.position[d] = position;
	}

	@Override
	public void setPosition(final long position, final int d) {
		i += container.step[d] * (position - this.position[d]);
		this.position[d] = (int) position;
	}

	@Override
	public FileRandomAccess<T> copy() {
		return new FileRandomAccess<T>(this);
	}

	@Override
	public FileRandomAccess<T> copyRandomAccess() {
		return copy();
	}
}
