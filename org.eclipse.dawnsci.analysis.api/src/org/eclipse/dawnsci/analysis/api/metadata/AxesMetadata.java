/*
 * Copyright (c) 2014 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.dawnsci.analysis.api.metadata;

import org.eclipse.dawnsci.analysis.api.dataset.ILazyDataset;

/**
 * This metadata describes any axis information associated with a dataset.
 * Dimension numbers are zero-based, i.e. the first dimension is numbered
 * zero.
 */
public interface AxesMetadata extends MetadataType {

	/**
	 * Get axis datasets
	 * @return all axis datasets, any nulls represent default integer indexes, 
	 * each axis is the main specified axis. i.e the result of getAxis(n)[0]
	 */
	public ILazyDataset[] getAxes();

	/**
	 * Get all axis datasets for the given dimension
	 * @param axisDim dimension (n.b. this is zero-based)
	 * @return axis datasets, null represent default integer indexes, the order is in inverse importance.
	 */
	public ILazyDataset[] getAxis(int axisDim);
	
	public void setAxis(int axisDim, ILazyDataset[] axisData);
	
	public AxesMetadata createAxesMetadata(int rank);
}
