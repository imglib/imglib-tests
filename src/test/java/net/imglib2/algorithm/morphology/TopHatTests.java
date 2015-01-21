package net.imglib2.algorithm.morphology;

import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.SCIFIOImgPlus;

import java.io.File;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.algorithm.region.localneighborhood.HyperSphereShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class TopHatTests
{
	public static void main( final String[] args )
	{
//		show( args );
		decomp( args );
	}

	private static void decomp( final String[] args )
	{
		ImageJ.main( args );
//		final File file = new File( "/Users/tinevez/Desktop/iconas/Data/Uneven.tif" );
//		final ImagePlus imp = IJ.openImage( file.getAbsolutePath() );
//		final Object obj = ImagePlusAdapter.wrap( imp );
//		@SuppressWarnings( "unchecked" )
//		final Img< UnsignedByteType > img = ( Img< UnsignedByteType > ) obj;
//		final List< Shape > strel = StructuringElements.diamond( 8, 2, true );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 50l, 50l );
		for ( final UnsignedByteType pixel : img )
		{
			pixel.set( 255 );
		}
		final ArrayRandomAccess< UnsignedByteType > randomAccess = img.randomAccess();
		randomAccess.setPosition( new int[] { 0, 25 } );
		randomAccess.get().set( 0 );

		/*
		 * To new Img
		 */

		ImageJFunctions.show( img, "Source" );

//		final Img< UnsignedByteType > topHat = TopHat.topHat( img, strel, 1 );
//		ImageJFunctions.show( topHat, "WhiteTopHatToNewImg" );

//		ImageJFunctions.show( Dilation.dilate( img, StructuringElements.diamond( 8, 2, true ), 1 ), "WhiteTopHatDecomp" );
//		ImageJFunctions.show( Dilation.dilate( img, StructuringElements.diamond( 8, 2, false ), 1 ), "WhiteTopHatStraight" );

		ImageJFunctions.show( Erosion.erode( img, StructuringElements.diamond( 8, 2, true ), 1 ), "WhiteTopHatDecomp" );
		ImageJFunctions.show( Erosion.erode( img, StructuringElements.diamond( 8, 2, false ), 1 ), "WhiteTopHatStraight" );

//		ImageJFunctions.show( TopHat.topHat( img, StructuringElements.diamond( 8, 2, true ), 1 ), "WhiteTopHatDecomp" );
//		ImageJFunctions.show( TopHat.topHat( img, StructuringElements.diamond( 8, 2, false ), 1 ), "WhiteTopHatStraight" );


		/*
		 * In place
		 */

//		final Interval interval = FinalInterval.createMinSize( new long[] { 30, 50, 88, 32 } );
//		final Img< UnsignedByteType > copy = img.copy();
//		TopHat.topHatInPlace( copy, interval, strel, 1 );
//		ImageJFunctions.show( copy, "WhiteTopHatInPlace" );
//
//		/*
//		 * To target
//		 */
//
//		final Img< UnsignedByteType > img2 = img.factory().create( interval, new UnsignedByteType() );
//		final long[] translation = new long[ interval.numDimensions() ];
//		interval.min( translation );
//		final IntervalView< UnsignedByteType > translate = Views.translate( img2, translation );
//		TopHat.topHat( img, translate, strel, 1 );
//		ImageJFunctions.show( img2, "WhiteTopHatToTarget" );
	}

	public static void show( final String[] args ) throws ImgIOException
	{
		ImageJ.main( args );
		final File file = new File( "/Users/tinevez/Desktop/iconas/Data/Uneven.tif" );
		final SCIFIOImgPlus img = new ImgOpener().openImgs( file.getAbsolutePath() ).get( 0 );

		final Shape strel = new HyperSphereShape( 5 );

		/*
		 * To new Img
		 */

		ImageJFunctions.show( img, "Source" );

		final Img topHat = TopHat.topHat( img, strel, 1 );
		ImageJFunctions.show( topHat, "WhiteTopHatToNewImg" );


		/*
		 * In place
		 */

		final Interval interval = FinalInterval.createMinSize( new long[] { 30, 50, 88, 32 } );
		final Img copy = img.copy();
		TopHat.topHatInPlace( copy, interval, strel, 1 );
		ImageJFunctions.show( copy, "WhiteTopHatInPlace" );

		/*
		 * To target
		 */

		final Img img2 = img.factory().create( interval, new UnsignedByteType() );
		final long[] translation = new long[ interval.numDimensions() ];
		interval.min( translation );
		final IntervalView translate = Views.translate( img2, translation );
		TopHat.topHat( img, translate, strel, 1 );
		ImageJFunctions.show( img2, "WhiteTopHatToTarget" );

	}

}
