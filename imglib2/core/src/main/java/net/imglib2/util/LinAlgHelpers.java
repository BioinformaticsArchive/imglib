package net.imglib2.util;

/**
 * Basic vector and matrix operations implemented on double[] and double[][].
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LinAlgHelpers
{
	public static int rows( final double[] a )
	{
		return a.length;
	}

	public static int rows( final double[][] A )
	{
		return A.length;
	}

	public static int cols( final double[][] A )
	{
		return A[ 0 ].length;
	}

	/**
	 * get the squared length of a.
	 *
	 * @param a
	 */
	public static double squareLength( final double[] a )
	{
		final int rows = rows( a );
		double squ_len = 0.0;
		for ( int i = 0; i < rows; ++i )
			squ_len += a[ i ] * a[ i ];
		return squ_len;
	}

	/**
	 * get the length of a.
	 *
	 * @param a
	 */
	public static double length( final double[] a )
	{
		return Math.sqrt( squareLength( a ) );
	}

	/**
	 * set c = a * b, where a is a vector and b is scalar.
	 * Dimensions of a and c must match.
	 * In place scaling (c==a) is permitted.
	 *
	 * @param a
	 * @param b
	 * @param c
	 */
	public static void scale( final double[] a, final double b, final double[] c )
	{
		assert rows( a ) == rows( c );

		final int rows = rows( a );

		for ( int i = 0; i < rows; ++i )
			c[ i ] = a[ i ] * b;
	}

	/**
	 * set C = A * b, where A is a matrix and b is scalar.
	 * Dimensions of A and C must match.
	 * In place scaling (C==A) is permitted.
	 *
	 * @param A
	 * @param b
	 * @param C
	 */
	public static void scale( final double[][] A, final double b, final double[][] C )
	{
		assert rows( A ) == rows( C ) && cols( A ) == cols( C );

		final int rows = rows( A );
		final int cols = cols( A );

		for ( int i = 0; i < rows; ++i )
			for ( int j = 0; j < cols; ++j )
				C[ i ][ j ] = A[ i ][ j ] * b;
	}

	/**
	 * set C = A, where A is a matrix.
	 * Dimensions of A and C must match.
	 *
	 * @param A
	 * @param C
	 */
	public static void copy( final double[][] A, final double[][] C )
	{
		assert rows( A ) == rows( C ) && cols( A ) == cols( C );

		final int rows = rows( A );
		final int cols = cols( A );

		for ( int i = 0; i < rows; ++i )
			for ( int j = 0; j < cols; ++j )
				C[ i ][ j ] = A[ i ][ j ];
	}

	/**
	 * set c = a - b. Dimensions of a, b, and c must match.
	 * In place subtraction (c==a) is permitted.
	 *
	 * @param a
	 * @param b
	 * @param c
	 */
	public static void subtract( final double[] a, final double[] b, final double[] c )
	{
		assert ( rows( a ) == rows( b ) ) && ( rows( a ) == rows( c ) );

		final int rows = rows( a );

		for ( int i = 0; i < rows; ++i )
			c[ i ] = a[ i ] - b[ i ];
	}

	/**
	 * set c = A * b.
	 *
	 * Dimensions of a, B, and c must match. That is, cols(A) == rows(b), and
	 * rows(c) == rows(A).
	 *
	 * @param A
	 * @param B
	 * @param C
	 */
	public static void mult( final double[][] A, final double[] b, final double[] c )
	{
		assert cols( A ) == rows( b );
		assert rows( c ) == rows( A );

		final int rows = rows( c );
		final int Acols = cols( A );

		for ( int i = 0; i < rows; ++i )
		{
			double sum = 0;
			for ( int k = 0; k < Acols; ++k )
				sum += A[ i ][ k ] * b[ k ];
			c[ i ] = sum;
		}
	}

	/**
	 * set C = A * B.
	 *
	 * Dimensions of A, B, and C must match. That is, cols(A) == rows(B),
	 * rows(C) == rows(A), and cols(C) == cols(B).
	 *
	 * @param A
	 * @param B
	 * @param C
	 */
	public static void mult( final double[][] A, final double[][] B, final double[][] C )
	{
		assert cols( A ) == rows( B );
		assert ( rows( C ) == rows( A ) ) && ( cols( C ) == cols( B ) );

		final int cols = cols( C );
		final int rows = rows( C );
		final int Acols = cols( A );

		for ( int i = 0; i < rows; ++i )
		{
			for ( int j = 0; j < cols; ++j )
			{
				double sum = 0;
				for ( int k = 0; k < Acols; ++k )
					sum += A[ i ][ k ] * B[ k ][ j ];
				C[ i ][ j ] = sum;
			}
		}
	}

	/**
	 * set C = A * B^T.
	 *
	 * Dimensions of A, B, and C must match. That is, cols(A) == cols(B),
	 * rows(C) == rows(A), and cols(C) == rows(B).
	 *
	 * @param A
	 * @param B
	 * @param C
	 */
	public static void multABT( final double[][] A, final double[][] B, final double[][] C )
	{
		assert cols( A ) == cols( B );
		assert ( rows( C ) == rows( A ) ) && ( cols( C ) == rows( B ) );

		final int cols = cols( C );
		final int rows = rows( C );
		final int Acols = cols( A );

		for ( int i = 0; i < rows; ++i )
		{
			for ( int j = 0; j < cols; ++j )
			{
				double sum = 0;
				for ( int k = 0; k < Acols; ++k )
					sum += A[ i ][ k ] * B[ j ][ k ];
				C[ i ][ j ] = sum;
			}
		}
	}

	/**
	 * set C = A^T * B.
	 *
	 * Dimensions of A, B, and C must match. That is, rows(A) == rows(B),
	 * rows(C) == cols(A), and cols(C) == cols(B).
	 *
	 * @param A
	 * @param B
	 * @param C
	 */
	public static void multATB( final double[][] A, final double[][] B, final double[][] C )
	{
		assert rows( A ) == rows( B );
		assert ( rows( C ) == cols( A ) ) && ( cols( C ) == cols( B ) );

		final int cols = cols( C );
		final int rows = rows( C );
		final int Arows = rows( A );

		for ( int i = 0; i < rows; ++i )
		{
			for ( int j = 0; j < cols; ++j )
			{
				double sum = 0;
				for ( int k = 0; k < Arows; ++k )
					sum += A[ k ][ i ] * B[ k ][ j ];
				C[ i ][ j ] = sum;
			}
		}
	}

	/**
	 * set C = A + B.
	 *
	 * Dimensions of A, B, and C must match.
	 * In place addition (C==A or C==B) is permitted.
	 *
	 * @param A
	 * @param B
	 * @param C
	 */
	public static void add( final double[][] A, final double[][] B, final double[][] C )
	{
		assert rows( A ) == rows( B ) && rows( A ) == rows( C );
		assert cols( A ) == cols( B ) && cols( A ) == cols( C );

		final int rows = rows( A );
		final int cols = cols( A );

		for ( int i = 0; i < rows; ++i )
			for ( int j = 0; j < cols; ++j )
				C[ i ][ j ] = A[ i ][ j ] + B[ i ][ j ];
	}

	/**
	 * normalize a, i.e., scale to unit length.
	 *
	 * @param a
	 */
	public static void normalize( final double[] a )
	{
		final int rows = rows( a );
		final double len = length( a );
		for ( int i = 0; i < rows; ++i )
			a[ i ] /= len;
	}

	/**
	 * compute dot product a * b.
	 *
	 * Dimensions of a and b must match.
	 *
	 * @param a
	 * @param b
	 */
	public static double dot( final double[] a, final double[] b )
	{
		assert rows( a ) == rows( b );

		final int rows = rows( a );

		double sum = 0;
		for ( int i = 0; i < rows; ++i )
			sum += a[ i ] * b[ i ];

		return sum;
	}

	/**
	 * compute cross product, set c = a ^ b.
	 *
	 * Dimensions of a, b, and c must equal 3.
	 *
	 * @param a
	 * @param b
	 */
	public static void cross( final double[] a, final double[] b, final double[] c )
	{
		c[ 0 ] = a[ 1 ] * b[ 2 ] - a[ 2 ] * b[ 1 ];
		c[ 1 ] = a[ 2 ] * b[ 0 ] - a[ 0 ] * b[ 2 ];
		c[ 2 ] = a[ 0 ] * b[ 1 ] - a[ 1 ] * b[ 0 ];
	}

	/**
	 * compute outer product, set C = a * b^T.
	 *
	 * Dimensions of a, b, and C must match. That is, rows(a) == rows(C),
	 * and rows(b) == cols(C).
	 *
	 * @param a
	 * @param b
	 * @param C
	 */
	public static void outer( final double[] a, final double[] b, final double[][] C )
	{
		assert rows( a ) == rows( C ) && rows( b ) == cols( C );

		final int rows = rows( a );
		final int cols = rows( b );

		for ( int i = 0; i < rows; ++i )
			for ( int j = 0; j < cols; ++j )
				C[ i ][ j ] = a[ i ] * b[ j ];
	}

	/**
	 * compute the angle of rotation from a rotation matrix.
	 * The returned value is in the range [0, PI].
	 *
	 * @param R
	 *            rotation matrix
	 */
	public static double angleFromR( final double[][] R )
	{
		assert cols( R ) >= 3;
		assert rows( R ) >= 3;

		final double tr = R[ 0 ][ 0 ] + R[ 1 ][ 1 ] + R[ 2 ][ 2 ];
		final double theta = Math.acos( ( tr - 1.0 ) / 2.0 );
		return theta;
	}

	/**
	 * compute the axis of rotation from a rotation matrix.
	 *
	 * @param R
	 *            rotation matrix
	 * @param a
	 *            rotation axis is stored here
	 */
	public static void axisFromR( final double[][] R, final double[] a )
	{
		assert cols( R ) >= 3;
		assert rows( R ) >= 3;
		assert rows( a ) >= 3;

		final double s = 1.0 / ( 2.0 * Math.sin( angleFromR( R ) ) );
		a[ 0 ] = s * ( R[ 2 ][ 1 ] - R[ 1 ][ 2 ] );
		a[ 1 ] = s * ( R[ 0 ][ 2 ] - R[ 2 ][ 0 ] );
		a[ 2 ] = s * ( R[ 1 ][ 0 ] - R[ 0 ][ 1 ] );
	}

	/**
	 * compute a unit quaternion from a rotation matrix.
	 *
	 * @param R
	 *            rotation matrix.
	 * @param q
	 *            unit quaternion (w, x, y, z) is stored here.
	 */
	public static void quaternionFromR( final double[][] R, final double[] q )
	{
		assert cols( R ) >= 3;
		assert rows( R ) >= 3;
		assert rows( q ) >= 4;

		// The trace determines the method of decomposition
		final double d0 = R[ 0 ][ 0 ], d1 = R[ 1 ][ 1 ], d2 = R[ 2 ][ 2 ];
		final double rr = 1.0 + d0 + d1 + d2;
		if ( rr > 0 )
		{
			final double s = 0.5 / Math.sqrt( rr );
			q[ 1 ] = ( R[ 2 ][ 1 ] - R[ 1 ][ 2 ] ) * s;
			q[ 2 ] = ( R[ 0 ][ 2 ] - R[ 2 ][ 0 ] ) * s;
			q[ 3 ] = ( R[ 1 ][ 0 ] - R[ 0 ][ 1 ] ) * s;
			q[ 0 ] = 0.25 / s;
		}
		else
		{
			// Trace is less than zero, so need to determine which
			// major diagonal is largest
			if ( ( d0 > d1 ) && ( d0 > d2 ) )
			{
				final double s = 0.5 / Math.sqrt( 1 + d0 - d1 - d2 );
				q[ 1 ] = 0.5 * s;
				q[ 2 ] = ( R[ 0 ][ 1 ] + R[ 1 ][ 0 ] ) * s;
				q[ 3 ] = ( R[ 0 ][ 2 ] + R[ 2 ][ 0 ] ) * s;
				q[ 0 ] = ( R[ 1 ][ 2 ] + R[ 2 ][ 1 ] ) * s;
			}
			else if ( d1 > d2 )
			{
				final double s = 0.5 / Math.sqrt( 1 + d0 - d1 - d2 );
				q[ 1 ] = ( R[ 0 ][ 1 ] + R[ 1 ][ 0 ] ) * s;
				q[ 2 ] = 0.5 * s;
				q[ 3 ] = ( R[ 1 ][ 2 ] + R[ 2 ][ 1 ] ) * s;
				q[ 0 ] = ( R[ 0 ][ 2 ] + R[ 2 ][ 0 ] ) * s;
			}
			else
			{
				final double s = 0.5 / Math.sqrt( 1 + d0 - d1 - d2 );
				q[ 1 ] = ( R[ 0 ][ 2 ] + R[ 2 ][ 0 ] ) * s;
				q[ 2 ] = ( R[ 1 ][ 2 ] + R[ 2 ][ 1 ] ) * s;
				q[ 3 ] = 0.5 * s;
				q[ 0 ] = ( R[ 0 ][ 1 ] + R[ 1 ][ 0 ] ) * s;
			}
		}
	}

	/**
	 * compute a rotation matrix from a unit quaternion.
	 *
	 * @param q
	 *            unit quaternion (w, x, y, z).
	 * @param R
	 *            rotation matrix is stored here.
	 */
	public static void quaternionToR( final double[] q, final double[][] R )
	{
		assert rows( q ) >= 4;
		assert cols( R ) >= 3;
		assert rows( R ) >= 3;

		final double w = q[ 0 ];
		final double x = q[ 1 ];
		final double y = q[ 2 ];
		final double z = q[ 3 ];

		R[ 0 ][ 0 ] = w * w + x * x - y * y - z * z;
		R[ 0 ][ 1 ] = 2.0 * ( x * y - w * z );
		R[ 0 ][ 2 ] = 2.0 * ( x * z + w * y );

		R[ 1 ][ 0 ] = 2.0 * ( y * x + w * z );
		R[ 1 ][ 1 ] = w * w - x * x + y * y - z * z;
		R[ 1 ][ 2 ] = 2.0 * ( y * z - w * x );

		R[ 2 ][ 0 ] = 2.0 * ( z * x - w * y );
		R[ 2 ][ 1 ] = 2.0 * ( z * y + w * x );
		R[ 2 ][ 2 ] = w * w - x * x - y * y + z * z;
	}

	/**
	 * compute a quaternion from rotation axis and angle.
	 *
	 * @param axis
	 *            rotation axis as a unit vector.
	 * @param angle
	 *            rotation angle [rad].
	 * @param q
	 *            unit quaternion (w, x, y, z) is stored here.
	 */
	public static void quaternionFromAngleAxis( final double[] axis, final double angle, final double[] q )
	{
		assert rows( axis ) >= 3;
		assert rows( q ) >= 4;

		final double s = Math.sin( 0.5 * angle );
		q[ 0 ] = Math.cos( 0.5 * angle );
		q[ 1 ] = s * axis[ 0 ];
		q[ 2 ] = s * axis[ 1 ];
		q[ 3 ] = s * axis[ 2 ];
	}

	/**
	 * compute the quaternion product pq = p * q. applying rotation pq
	 * corresponds to applying first q, then p (i.e. same as multiplication of
	 * rotation matrices).
	 *
	 * @param p
	 *            unit quaternion (w, x, y, z).
	 * @param q
	 *            unit quaternion (w, x, y, z).
	 * @param pq
	 *            quaternion product p * q is stored here.
	 */
	public static void quaternionMultiply( final double[] p, final double[] q, final double[] pq )
	{
		assert rows( p ) >= 4;
		assert rows( q ) >= 4;
		assert rows( pq ) >= 4;

		final double pw = p[ 0 ];
		final double px = p[ 1 ];
		final double py = p[ 2 ];
		final double pz = p[ 3 ];

		final double qw = q[ 0 ];
		final double qx = q[ 1 ];
		final double qy = q[ 2 ];
		final double qz = q[ 3 ];

		pq[ 0 ] = pw * qw - px * qx - py * qy - pz * qz;
		pq[ 1 ] = pw * qx + px * qw + py * qz - pz * qy;
		pq[ 2 ] = pw * qy + py * qw + pz * qx - px * qz;
		pq[ 3 ] = pw * qz + pz * qw + px * qy - py * qx;
	}


	/**
	 * invert quaternion, set q = p^{-1}.
	 * In place inversion (p==q) is permitted.
	 *
	 * @param p
	 *            unit quaternion (w, x, y, z).
	 * @param q
	 *            inverse of p is stored here.
	 */
	public static void quaternionInvert( final double[] p, final double[] q )
	{
		assert rows( p ) >= 4;
		assert rows( q ) >= 4;

		q[ 0 ] = p[ 0 ];
		q[ 1 ] = -p[ 1 ];
		q[ 2 ] = -p[ 2 ];
		q[ 3 ] = -p[ 3 ];
	}

	/**
	 * Apply quaternion rotation q to 3D point p, set qp = q * p.
	 * In place rotation (p==qp) is permitted.
	 *
	 * @param q
	 *            unit quaternion (w, x, y, z).
	 * @param p
	 *            3D point.
	 * @param qp
	 *            rotated 3D point is stored here.
	 */
	public static void quaternionApply( final double[] q, final double[] p, final double[] qp )
	{
		assert rows( q ) >= 4;
		assert rows( p ) >= 3;
		assert rows( qp ) >= 3;

		final double w = q[ 0 ];
		final double x = q[ 1 ];
		final double y = q[ 2 ];
		final double z = q[ 3 ];

		final double q0 = -x * p[ 0 ] - y * p[ 1 ] - z * p[ 2 ];
		final double q1 = w * p[ 0 ] + y * p[ 2 ] - z * p[ 1 ];
		final double q2 = w * p[ 1 ] + z * p[ 0 ] - x * p[ 2 ];
		final double q3 = w * p[ 2 ] + x * p[ 1 ] - y * p[ 0 ];

		qp[ 0 ] = -q0 * x + q1 * w - q2 * z + q3 * y;
		qp[ 1 ] = -q0 * y + q2 * w - q3 * x + q1 * z;
		qp[ 2 ] = -q0 * z + q3 * w - q1 * y + q2 * x;
	}

	public static String toString( final double[][] A )
	{
		return toString( A, "%6.3f " );
	}

	public static String toString( final double[][] A, final String format )
	{
		final int rows = rows( A );
		final int cols = cols( A );

		String result = "";
		for ( int i = 0; i < rows; ++i )
		{
			for ( int j = 0; j < cols; ++j )
				result += String.format( format, A[ i ][ j ] );
			result += "\n";
		}
		return result;
	}

	public static String toString( final double[] a )
	{
		return toString( a, "%6.3f " );
	}

	public static String toString( final double[] a, final String format )
	{
		final int rows = rows( a );

		String result = "";
		for ( int i = 0; i < rows; ++i )
			result += String.format( format, a[ i ] );
		result += "\n";
		return result;
	}
}
