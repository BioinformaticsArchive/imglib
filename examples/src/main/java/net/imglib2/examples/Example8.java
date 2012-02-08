package net.imglib2.examples;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import ij.ImageJ;
import mpicbg.imglib.algorithm.fft.FourierConvolution;
import mpicbg.imglib.algorithm.math.NormalizeImageFloat;

/**
 * Perform a gaussian convolution using fourier convolution
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example8
{
	public Example8() throws ImgIOException, IncompatibleTypeException
	{
		// open with ImgOpener using an ArrayImgFactory
		Img< FloatType > image = new ImgOpener().openImg( "DrosophilaWing.tif", new ArrayImgFactory< FloatType >(), new FloatType() );
		Img< FloatType > kernel = new ImgOpener().openImg( "kernelGauss.tif", new ArrayImgFactory< FloatType >(), new FloatType() );

		// normalize the kernel
		NormalizeImageFloat< FloatType > normImage = new NormalizeImageFloat< FloatType >( kernel );

		if ( !normImage.checkInput() || !normImage.process() )
		{
			System.out.println( "Cannot normalize kernel: " + normImage.getErrorMessage() );
			return;
		}

		kernel.close();
		kernel = normImage.getResult();

		// display all
		ImageJFunctions.copyToImagePlus( kernel ).show().setTitle( "kernel" );

		ImageJFunctions.copyToImagePlus( image ).show();

		// compute fourier convolution
		FourierConvolution< FloatType, FloatType > fourierConvolution = new FourierConvolution< FloatType, FloatType >( image, kernel );

		if ( !fourierConvolution.checkInput() || !fourierConvolution.process() )
		{
			System.out.println( "Cannot compute fourier convolution: " + fourierConvolution.getErrorMessage() );
			return;
		}

		Img< FloatType > convolved = fourierConvolution.getResult();

		final String name = "(" + fourierConvolution.getProcessingTime() + " ms) Convolution of " + image.getName();
		ImageJFunctions.copyToImagePlus( convolved ).show().setTitle( name );

	}

	public static void main( String[] args ) throws ImgIOException, IncompatibleTypeException
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example8();
	}
}
