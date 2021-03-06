package org.eclipse.dawnsci.analysis.examples.dataset;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.eclipse.dawnsci.analysis.dataset.impl.Dataset;
import org.eclipse.dawnsci.analysis.dataset.impl.DatasetFactory;
import org.eclipse.dawnsci.analysis.dataset.impl.DoubleDataset;
import org.eclipse.dawnsci.analysis.dataset.impl.IndexIterator;
import org.eclipse.dawnsci.analysis.dataset.impl.PositionIterator;
import org.eclipse.dawnsci.analysis.dataset.impl.StrideIterator;
import org.junit.Test;


/**
 * Code to show how to use iterators over data.
 * Written as tests but not testing code, just for code examples.
 * 
 * @author Matthew Gerring
 *
 */
public class IterationExamples {

	/**
	 * Simple position iterator.
	 */
	@Test
	public void positionIterator() {
		
		final Dataset data = new DoubleDataset(new double[]{1,2,3,4,5,6,7,8}, 2,2,2);
		
		// The position iterator allows you to iterate down an axis
		PositionIterator it = data.getPositionIterator(0);		
		while(it.hasNext()) {
			System.out.println("Location (Axis=0) = "+Arrays.toString(it.getPos()));
			System.out.println("Value (Axis=0) = "+data.getDouble(it.getPos()));
		}
		
		// Now iterate down the 1 axis
		it = data.getPositionIterator(1);
		while(it.hasNext()) {
			System.out.println("Location (Axis=1) = "+Arrays.toString(it.getPos()));
			System.out.println("Value (Axis=1) = "+data.getDouble(it.getPos()));
		}

	}
	
	/**
	 * Simple Stride Iterator
	 */
	@Test
	public void strideIterator() {

	   Dataset       ta   = DatasetFactory.createRange(0, 1024, 1, Dataset.FLOAT).reshape(16, 8, 1024 / (16 * 8));
	   IndexIterator iter = new StrideIterator(ta.getElementsPerItem(), ta.getShape());
	 
		for (int i = 0; iter.hasNext(); i++) {
			assertEquals(i, ta.getElementDoubleAbs(iter.index), 1e-5*i);
		}

		iter.reset();
		for (int i = 0; iter.hasNext(); i++) {
			assertEquals(i,  ta.getElementDoubleAbs(iter.index), 1e-5*i);
		}
	}

}
