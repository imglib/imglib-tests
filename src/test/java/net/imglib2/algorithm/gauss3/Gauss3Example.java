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

package net.imglib2.algorithm.gauss3;

import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * Example for using {@link Gauss3}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Gauss3Example
{
	public static void main( final String[] args ) throws ImgIOException
	{
		final String fn = "/home/tobias/workspace/data/DrosophilaWing.tif";
		final Img< FloatType > img = new ImgOpener().openImg( fn, new ArrayImgFactory< FloatType >(), new FloatType() );

		final long[] dims = new long[ img.numDimensions() ];
		img.dimensions( dims );
		final Img< FloatType > convolved = ArrayImgs.floats( dims );

		try
		{
			Gauss3.gauss( 3, Views.extendMirrorSingle( img ), convolved );
//			Gauss3.gauss( 5, img, Views.interval( convolved, Intervals.createMinSize( 200, 100, 200, 150 ) ) );
		}
		catch ( final IncompatibleTypeException e )
		{
			e.printStackTrace();
		}
		ImageJFunctions.show( convolved );
	}
}
