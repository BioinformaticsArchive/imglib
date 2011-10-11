/*

Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.ops.example;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.Condition;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.Real;
import net.imglib2.ops.RealOutput;
import net.imglib2.ops.condition.AndCondition;
import net.imglib2.ops.function.general.ConditionalFunction;
import net.imglib2.ops.image.RealImageAssignment;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

// a complicated conditional pixel assignment example
//   RealImageAssignment tagged with two conditions
//     (x,y) in a circle and (x+y) > constant
//   assign with ConditionalFunction
//     condition : x < constant
//     if true: return x^2
//     if false: return 3*y + 19

/**
 * 
 * @author Barry DeZonia
 *
 */
public class Example7 {

	private static final long XSIZE = 1201;
	private static final long YSIZE = 1201;
	private static final long CIRCLE_RADIUS = 200;
	private static final long LINE_CONSTANT = 650;
	private static final long X_CONSTANT = 400;
	
	private static Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE}, new DoubleType());
	}

	public static class XValueCondition implements Condition<long[]> {

		@Override
		public boolean isTrue(Neighborhood<long[]> neigh, long[] point) {
			return point[0] < X_CONSTANT;
		}
		
		@Override
		public XValueCondition duplicate() {
			return new XValueCondition();
		}
	}
	
	public static class XSquaredFunction extends RealOutput implements Function<long[],Real> {

		@Override
		public void evaluate(Neighborhood<long[]> neigh, long[] point, Real output) {
			output.setReal(point[0]*point[0]);
		}
		
		@Override
		public XSquaredFunction duplicate() {
			return new XSquaredFunction();
		}
		
	}

	public static class YLineFunction extends RealOutput implements Function<long[],Real> {

		@Override
		public void evaluate(Neighborhood<long[]> neigh, long[] point, Real output) {
			output.setReal(3*point[1]+19);
		}
		
		@Override
		public YLineFunction duplicate() {
			return new YLineFunction();
		}
		
	}
	
	public static class CircularCondition implements Condition<long[]> {

		long ctrX = XSIZE / 2;
		long ctrY = YSIZE / 2;
		
		public CircularCondition() {
			// nothing to do
		}
		
		@Override
		public boolean isTrue(Neighborhood<long[]> neigh, long[] point) {
			long dx = point[0] - ctrX;
			long dy = point[1] - ctrY;
			double dist = Math.sqrt(dx*dx + dy*dy);
			return dist <= CIRCLE_RADIUS;
		}
		
		@Override
		public CircularCondition duplicate() {
			return new CircularCondition();
		}
	}
	
	public static class XYSumCondition implements Condition<long[]> {

		public XYSumCondition() {
			// nothing to do
		}
		
		@Override
		public boolean isTrue(Neighborhood<long[]> neigh, long[] point) {
			return (point[0] + point[1]) > LINE_CONSTANT;
		}
		
		@Override
		public XYSumCondition duplicate() {
			return new XYSumCondition();
		}
	}

	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private static double expectedValue(int x, int y) {
		double ctrX = XSIZE/2;
		double ctrY = YSIZE/2;
		double dx = x - ctrX;
		double dy = y - ctrY;
		double distFromCtr = Math.sqrt(dx*dx + dy*dy);
		if (distFromCtr > CIRCLE_RADIUS) return 0;
		if ((x+y) <= LINE_CONSTANT) return 0;
		if (x < X_CONSTANT)
			return x*x;
		return 3*y + 19;
	}

	private static boolean testValues(Img<? extends RealType<?>> image) {
		boolean success = true;
		RandomAccess<? extends RealType<?>> accessor = image.randomAccess();
		long[] pos = new long[2];
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				pos[0] = x;
				pos[1] = y;
				accessor.setPosition(pos);
				double value = accessor.get().getRealDouble();
				if (!veryClose(value, expectedValue(x, y))) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+expectedValue(x,y)+") actual ("+value+")");
					success = false;
				}
			}
		}
		return success;
	}
	
	private static boolean testComplicatedAssignment() {
		Condition<long[]> xValCond = new XValueCondition();
		Function<long[],Real> xSquaredFunc = new XSquaredFunction();
		Function<long[],Real> yLineFunc = new YLineFunction();
		Function<long[],Real> function =
			new ConditionalFunction<long[],Real>(xValCond, xSquaredFunc, yLineFunc);
		Img<? extends RealType<?>> image = allocateImage();
		RealImageAssignment assigner = new RealImageAssignment(image, new long[2], new long[]{XSIZE,YSIZE}, function, new long[2], new long[2]);
		Condition<long[]> circleCond = new CircularCondition();
		Condition<long[]> sumCond = new XYSumCondition();
		Condition<long[]> compositeCondition = new AndCondition<long[]>(circleCond,sumCond);
		assigner.setCondition(compositeCondition);
		assigner.assign();
		return testValues(image);
	}
	
	public static void main(String[] args) {
		System.out.println("Example7");
		if (testComplicatedAssignment())
			System.out.println(" Successful test");
	}
}