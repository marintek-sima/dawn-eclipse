/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.dawnsci.plotting.api.trace;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dawnsci.analysis.api.roi.IROI;


public interface IWindowTrace extends ITrace {
	
	/**
	 * Sets a window of the data visible.
	 * @param roi
	 * @param monitor
	 * @return status
	 */
	IStatus setWindow(IROI roi, IProgressMonitor monitor);
}
