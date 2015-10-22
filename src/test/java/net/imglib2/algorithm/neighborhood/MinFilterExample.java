/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package net.imglib2.algorithm.neighborhood;

import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class MinFilterExample
{

	public static < T extends Type< T > & Comparable< T > >
	void minFilter( final RandomAccessibleInterval< T > input, final RandomAccessibleInterval< T > output, final Shape shape )
	{
		final RandomAccess< T > out = output.randomAccess();
		for ( final Neighborhood< T > neighborhood : shape.neighborhoods( input ) )
		{
			out.setPosition( neighborhood );
			final T o = out.get();
			o.set( neighborhood.firstElement() );
			for ( final T i : neighborhood )
				if ( i.compareTo( o ) < 0 )
					o.set( i );
		}
	}

	public static void main( final String[] args ) throws ImgIOException
	{
		final String fn = "/home/tobias/workspace/data/DrosophilaWing.tif";
		final int span = 3;

		final ArrayImgFactory< FloatType > factory = new ArrayImgFactory< FloatType >();
		final FloatType type = new FloatType();
		final Img< FloatType > imgInput = new ImgOpener().openImg( fn, factory, type );
		final Img< FloatType > imgOutput = factory.create( imgInput, type );

		final Interval computationInterval = Intervals.expand( imgInput, -span );
		final RandomAccessibleInterval< FloatType > input = Views.interval( imgInput, computationInterval );
		final RandomAccessibleInterval< FloatType > output = Views.interval( imgOutput, computationInterval );

		minFilter( input, output, new RectangleShape( span, false ) );
//		minFilter( input, output, new HyperSphereShape( span ) );

		ImageJFunctions.show( imgInput, "input" );
		ImageJFunctions.show( imgOutput, "min filtered" );
	}

}
