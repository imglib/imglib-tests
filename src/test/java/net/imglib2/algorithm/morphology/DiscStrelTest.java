package net.imglib2.algorithm.morphology;

import ij.ImageJ;

import java.util.Arrays;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class DiscStrelTest
{

	public static void main( final String[] args )
	{
		computeError();
		diskPlot2D( args );
		testDecompositionPerformance( args );
	}

	public static final void testDecompositionPerformance( final String[] args )
	{
		ImageJ.main( args );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 100, 100 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 49, 49 } );
		ra.get().set( 255 );

		System.out.println( "time(ms)" );
		System.out.println( "radius\tNo decomp.\t4 PL\t6 PL\t 8PL" );
		// Warm up
		Dilation.dilate( img, StructuringElements.disk( 1, img.numDimensions(), 0 ), 1 );
		Dilation.dilate( img, StructuringElements.disk( 1, img.numDimensions(), 4 ), 1 );
		Dilation.dilate( img, StructuringElements.disk( 1, img.numDimensions(), 6 ), 1 );
		Dilation.dilate( img, StructuringElements.disk( 1, img.numDimensions(), 8 ), 1 );

		final int[] decomp = new int[] { 0, 4, 6, 8 };

		for ( int i = 0; i < 40; i++ )
		{
			final int radius = i * 2 + 1;
			System.out.print( "" + radius );
			for ( final int dec : decomp )
			{
				final long start = System.currentTimeMillis();
				final List< Shape > strels = StructuringElements.disk( radius, 2, dec );
				Dilation.dilate( img, strels, 1 );
				final long end = System.currentTimeMillis();
				System.out.print( "\t" + ( end - start ) );
			}
			System.out.print( "\n" );
		}
	}

	public static final void computeError()
	{
		final long[] radiuses = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
		final int[] decomp = new int[] { 4, 6, 8 };
		final Img< UnsignedByteType > source = createSquarePointImage( 20, 2 );
		System.out.println( "radius\t4\t6\t8" );
		for ( final long radius : radiuses )
		{
			System.out.print( "" + radius + "\t" );
			final List< Shape > disk0 = StructuringElements.disk( radius, source.numDimensions(), 0 );
			final Img< UnsignedByteType > ref = Dilation.dilate( source, disk0, 1 );
			for ( final int n : decomp )
			{
				final List< Shape > disk = StructuringElements.disk( radius, source.numDimensions(), n );
				final Img< UnsignedByteType > out = Dilation.dilate( source, disk, 1 );
				final double[] error = error( out, ref );
				System.out.print( String.format( "%5.3f\t", error[ 0 ] ) );
			}
			System.out.println();
		}
	}

	private static double[] error( final Img< UnsignedByteType > out, final Img< UnsignedByteType > ref )
	{
		final Cursor< UnsignedByteType > cursor = ref.cursor();
		final RandomAccess< UnsignedByteType > ra = out.randomAccess();
		long iterated = 0;
		long mismatch = 0;
		long mismatchFP = 0;
		long mismatchFN = 0;
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			ra.setPosition( cursor );

			final int refVal = cursor.get().get();
			final int outVal = ra.get().get();

			if ( refVal > 0 )
			{
				iterated++;
				if ( outVal == 0 )
				{
					mismatch++;
					mismatchFN++;
				}
			}
			else
			{
				if ( outVal > 0 )
				{
					mismatch++;
					mismatchFP++;
				}
			}

		}
		return new double[] { ( double ) mismatch / ( double ) iterated, ( double ) mismatchFN / ( double ) iterated, ( double ) mismatchFP / ( double ) iterated };
	}

	/**
	 * Plot the disks in 2D.
	 */
	public static void diskPlot2D( final String[] args )
	{
		ImageJ.main( args );
		final long[] radiuses = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
		final int[] decomp = new int[] { 0, 4, 6, 8 };
		final Img< UnsignedByteType > source = createSquarePointImage( 20, 2 );
		for ( final int n : decomp )
		{
			final Img< UnsignedByteType > target = ArrayImgs.unsignedBytes( new long[] { 42, 42, radiuses.length } );

			int rindex = 0;
			for ( final long radius : radiuses )
			{
				final List< Shape > disc = StructuringElements.disk( radius, source.numDimensions(), n );
				final Img< UnsignedByteType > out = Dilation.dilate( source, disc, 1 );
				copyToSlice( out, target, rindex++ );
			}

			ImageJFunctions.show( target, "Decomp = " + n );
		}

	}

	private final static void copyToSlice( final Img< UnsignedByteType > source, final Img< UnsignedByteType > target, final long slice )
	{
		final RandomAccess< UnsignedByteType > ra = target.randomAccess();
		ra.setPosition( slice, source.numDimensions() );
		final Cursor< UnsignedByteType > c = source.cursor();
		while ( c.hasNext() )
		{
			c.fwd();
			ra.setPosition( c );
			ra.get().set( c.get() );
		}
	}

	private final static Img< UnsignedByteType > createSquarePointImage( final long radius, final int dim )
	{
		final long size = 2 * radius + 1;
		final long[] sizes = new long[ dim ];
		Arrays.fill( sizes, size );
		final long[] middle = new long[ dim ];
		Arrays.fill( middle, radius );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( sizes );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( middle );
		ra.get().set( 255 );

		return img;
	}

}
