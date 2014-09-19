/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.dawnsci.analysis.dataset.roi;

import java.util.ArrayList;

import org.eclipse.dawnsci.analysis.api.roi.IROI;


/**
 * Wrapper for a list of rectangular ROIs
 */
public class RectangularROIList extends ArrayList<RectangularROI> implements ROIList<RectangularROI> {

	@Override
	public boolean add(IROI roi) {
		if (roi instanceof RectangularROI)
			return super.add((RectangularROI) roi);
		return false;
	}

	@Override
	public boolean append(RectangularROI roi) {
		return super.add(roi);
	}
}