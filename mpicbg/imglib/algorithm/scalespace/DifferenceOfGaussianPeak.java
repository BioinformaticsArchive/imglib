/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Stephan Preibisch
 */
package mpicbg.imglib.algorithm.scalespace;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.algorithm.scalespace.DifferenceOfGaussian.SpecialPoint;
import mpicbg.imglib.cursor.Localizable;
import mpicbg.imglib.type.ComparableType;
import mpicbg.imglib.type.numeric.NumericType;

public class DifferenceOfGaussianPeak< T extends NumericType<T> & ComparableType<T> > implements Localizable
{
	SpecialPoint specialPoint;
	String errorMessage;
	
	final protected int[] pixelLocation;
	final protected float[] subPixelLocationOffset;
	final protected T value, fitValue, sumValue;
	
	public DifferenceOfGaussianPeak( final int[] pixelLocation, final T value, final SpecialPoint specialPoint )
	{
		this.specialPoint = specialPoint;
		this.pixelLocation = pixelLocation.clone();
		this.subPixelLocationOffset = new float[ pixelLocation.length ];
		
		this.value = value.clone();
		this.sumValue = value.clone();
		this.fitValue = value.createVariable();
		this.fitValue.setZero();
		
		this.errorMessage = "";
	}
	
	public boolean isMin() { return specialPoint == SpecialPoint.MIN; }
	public boolean isMax() { return specialPoint == SpecialPoint.MAX; }
	public boolean isValid() { return specialPoint != SpecialPoint.INVALID; }
	public SpecialPoint getPeakType() { return specialPoint; }
	public float[] getSubPixelPositionOffset() { return subPixelLocationOffset.clone(); }
	public float getSubPixelPositionOffset( final int dim ) { return subPixelLocationOffset[ dim ]; }
	public float[] getSubPixelPosition() 
	{
		final float[] loc = subPixelLocationOffset.clone();
		
		for ( int d = 0; d < loc.length; ++d )
			loc[ d ] += pixelLocation[ d ];
		
		return loc; 
	}
	public float getSubPixelPosition( final int dim ) { return subPixelLocationOffset[ dim ] + pixelLocation[ dim ]; }
	public T getValue() { return sumValue; }
	public T getImgValue() { return value; }
	public T getFitValue() { return fitValue; }
	public String getErrorMessage() { return errorMessage; }
	
	public void setPeakType( final SpecialPoint specialPoint ) { this.specialPoint = specialPoint; }
	public void setSubPixelLocationOffset( final float subPixelLocationOffset, final int dim ) { this.subPixelLocationOffset[ dim ] = subPixelLocationOffset; }
	public void setSubPixelLocationOffset( final float[] subPixelLocationOffset )
	{
		for ( int d = 0; d < pixelLocation.length; ++d )
			this.subPixelLocationOffset[ d ] = subPixelLocationOffset[ d ];
	}
	public void setPixelLocation( final int location, final int dim ) { pixelLocation[ dim ] = location; }
	public void setPixelLocation( final int[] pixelLocation )
	{
		for ( int d = 0; d < pixelLocation.length; ++d )
			this.pixelLocation[ d ] = pixelLocation[ d ];
	}
	public void setImgValue( final T value ) 
	{ 
		this.value.set( value );
		
		sumValue.set( this.value );
		sumValue.add( this.fitValue );
	}
	public void setFitValue( final T value ) 
	{
		this.fitValue.set( value ); 

		sumValue.set( this.value );
		sumValue.add( this.fitValue );
	}
	public void setErrorMessage( final String errorMessage ) { this.errorMessage = errorMessage; }

	@Override
	public void getPosition( final int[] position )
	{
		for ( int d = 0; d < pixelLocation.length; ++d )
			position[ d ] = pixelLocation[ d ];
	}

	@Override
	public int[] getPosition() { return pixelLocation.clone(); }

	@Override
	public int getPosition( final int dim ) { return pixelLocation[ dim ]; }

	@Override
	public String getPositionAsString() { return MathLib.printCoordinates( pixelLocation );	}

	@Override
	public void fwd( final long steps ) {}

	@Override
	public void fwd() {}
}