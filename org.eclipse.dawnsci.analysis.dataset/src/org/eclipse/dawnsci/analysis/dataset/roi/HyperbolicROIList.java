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
 * Wrapper for a list of hyperbolic ROIs
 */
public class HyperbolicROIList extends ArrayList<HyperbolicROI> implements ROIList<HyperbolicROI> {

	@Override
	public boolean add(IROI roi) {
		if (roi instanceof HyperbolicROI)
			return super.add((HyperbolicROI) roi);
		return false;
	}

	@Override
	public boolean append(HyperbolicROI roi) {
		return super.add(roi);
	}
}