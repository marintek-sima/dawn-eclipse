/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.dawnsci.plotting.api.axis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dawnsci.plotting.api.IPlottingSystem;

public class AxisUtils {

	public static String getUniqueAxisTitle(String title, IPlottingSystem system) {
		String sugTitle = title;
		int iTitle      = 0;
		while (!isAxisUnique(sugTitle, system)) {
			iTitle++;
			sugTitle = title+iTitle;
		}
		return sugTitle;
	}

	public static boolean isAxisUnique(String title, IPlottingSystem system) {
		final List<IAxis> axes = system.getAxes();
		for (IAxis ia : axes) {
			if (ia.getTitle()!=null && ia.getTitle().equals(title)) {
			    return false;
			}
 		}
		return true;
	}

	/**
	 * 
	 * @return Non-primary axes
	 */
	public static List<IAxis> getUserAxes(IPlottingSystem system) {
		final List<IAxis>  axes = system.getAxes();
		final List<IAxis> avail = new ArrayList<IAxis>(axes.size());
		for (IAxis axis : axes) {
			if (axis.isPrimaryAxis()) continue;
			avail.add(axis);
		}
		return avail;
	}

}
