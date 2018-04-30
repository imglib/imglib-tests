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
package net.imglib2.converter;

import ij.ImageJ;

import io.scif.img.IO;
import io.scif.img.ImgIOException;

import net.imglib2.Cursor;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.BenchmarkHelper;

public class RealARGBConverterBenchmark
{
	final Img< UnsignedByteType > img;

	final Img< ARGBType > argbImg;

	public RealARGBConverterBenchmark( final String filename ) throws ImgIOException, IncompatibleTypeException
	{
		// open with SCIFIO using an ArrayImgFactory
		final ArrayImgFactory< UnsignedByteType > factory = new ArrayImgFactory<>( new UnsignedByteType() );
		img = IO.openImgs( filename, factory ).get( 0 );
		argbImg = new ArrayImgFactory<>( new ARGBType() ).create( img );

		BenchmarkHelper.benchmarkAndPrint( 15, true, new Runnable()
		{
			@Override
			public void run()
			{
				for ( int i = 0; i < 10; ++i )
					convert( img, argbImg );
			}
		} );

		ImageJFunctions.show( argbImg );
	}

	public < T extends RealType< T > > void convert( final Img< T > in,  final Img< ARGBType > out )
	{
		final Cursor< T > cin = in.cursor();
		final Cursor< ARGBType > cout = out.cursor();
		final RealARGBConverter< T > converter = new RealARGBConverter<>( 0, 1000 );
		while( cin.hasNext() )
			converter.convert( cin.next(), cout.next() );
	}

	public static void main( final String[] args ) throws IncompatibleTypeException, ImgIOException
	{
		new ImageJ();
		new RealARGBConverterBenchmark( "/Users/pietzsch/workspace/data/DrosophilaWing.tif" );
	}

}
