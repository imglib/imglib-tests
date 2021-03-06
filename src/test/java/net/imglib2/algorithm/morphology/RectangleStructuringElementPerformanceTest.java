/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
package net.imglib2.algorithm.morphology;

import ij.ImageJ;

import java.util.Arrays;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class RectangleStructuringElementPerformanceTest
{

	public static void main( final String[] args )
	{
		benchmark2DPerformance( args );
//		benchmark3DPerformance( args );
	}

	public static void benchmark3DPerformance( final String[] args )
	{
		ImageJ.main( args );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 40, 40, 40 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new int[] { 19, 19, 19 } );
		ra.get().set( 255 );

		{
			System.out.println( "Square structuring element, 3D, standard, 1 thread." );
			System.out.println( "Radius\tTime (ms)" );
			// warm up
			perform( img, 1, false, 1 );
			perform( img, 1, false, 1 );
			// run test
			for ( int r = 1; r < 20; r++ )
			{
				final Object[] objects = perform( img, r, false, 1 );
				final Long dt = ( Long ) objects[ 0 ];
				System.out.println( "" + r + '\t' + dt );
			}
		}

		{
			System.out.println();
			System.out.println( "Square structuring element, 3D, optimized, 1 thread." );
			System.out.println( "Radius\tTime (ms)" );
			// warm up
			perform( img, 1, true, 1 );
			perform( img, 1, true, 1 );
			// run test
			for ( int r = 1; r < 20; r++ )
			{
				final Object[] objects = perform( img, r, true, 1 );
				final Long dt = ( Long ) objects[ 0 ];
				System.out.println( "" + r + '\t' + dt );
			}
		}

	}

	public static void benchmark2DPerformance( final String[] args )
	{
		ImageJ.main( args );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 100, 100 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new int[] { 49, 49 } );
		ra.get().set( 255 );


		{
			final ArrayImg< UnsignedByteType, ByteArray > target = ArrayImgs.unsignedBytes( 100, 100, 48 );
			final ArrayRandomAccess< UnsignedByteType > randomAccess = target.randomAccess();

			System.out.println( "Square structuring element, 2D, standard, 1 thread." );
			System.out.println( "Radius\tTime (ms)" );
			// warm up
			perform( img, 1, false, 1 );
			perform( img, 1, false, 1 );
			// run test
			for ( int r = 1; r < 49; r++ )
			{
				final Object[] objects = perform( img, r, false, 1 );
				final Long dt = ( Long ) objects[ 0 ];
				@SuppressWarnings( "unchecked" )
				final Img< UnsignedByteType > dilated = ( Img< UnsignedByteType > ) objects[ 1 ];
				System.out.println( "" + r + '\t' + dt );

				// copy
				randomAccess.setPosition( r - 1, 2 );
				final Cursor< UnsignedByteType > cursor = dilated.cursor();
				while ( cursor.hasNext() )
				{
					cursor.fwd();
					randomAccess.setPosition( cursor );
					randomAccess.get().set( cursor.get() );
				}
			}

			ImageJFunctions.show( target, "standard" );
		}

		{
			final ArrayImg< UnsignedByteType, ByteArray > target = ArrayImgs.unsignedBytes( 100, 100, 48 );
			final ArrayRandomAccess< UnsignedByteType > randomAccess = target.randomAccess();

			System.out.println();
			System.out.println( "Square structuring element, 2D, optimized, 1 thread." );
			System.out.println( "Radius\tTime (ms)" );
			// warm up
			perform( img, 1, true, 1 );
			perform( img, 1, true, 1 );
			// run test
			for ( int r = 1; r < 49; r++ )
			{
				final Object[] objects = perform( img, r, true, 1 );
				final Long dt = ( Long ) objects[ 0 ];
				@SuppressWarnings( "unchecked" )
				final Img< UnsignedByteType > dilated = ( Img< UnsignedByteType > ) objects[ 1 ];
				System.out.println( "" + r + '\t' + dt );

				// copy
				randomAccess.setPosition( r - 1, 2 );
				final Cursor< UnsignedByteType > cursor = dilated.cursor();
				while ( cursor.hasNext() )
				{
					cursor.fwd();
					randomAccess.setPosition( cursor );
					randomAccess.get().set( cursor.get() );
				}
			}

			ImageJFunctions.show( target, "optimized" );
		}

	}

	private static final Object[] perform( final Img< UnsignedByteType > img, final int radius, final boolean optimize, final int numThreads )
	{
		final long start = System.currentTimeMillis();
		final int[] radii = new int[ img.numDimensions() ];
		Arrays.fill( radii, radius );
		final List< Shape > strels = StructuringElements.rectangle( radii, optimize );
		final Img< UnsignedByteType > dilated = Dilation.dilate( img, strels, numThreads );
		final long end = System.currentTimeMillis();
		return new Object[] { Long.valueOf( end - start ), dilated };
	}

}
