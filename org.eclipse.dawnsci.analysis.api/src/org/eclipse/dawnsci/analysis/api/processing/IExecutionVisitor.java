/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.dawnsci.analysis.api.processing;

import org.eclipse.dawnsci.analysis.api.dataset.Slice;
import org.eclipse.dawnsci.analysis.api.metadata.OriginMetadata;
import org.eclipse.dawnsci.analysis.api.monitor.IMonitor;
import org.eclipse.dawnsci.analysis.api.processing.model.IOperationModel;

/**
 * This interface is designed to be called when a series of operations has been 
 * completed on a dataset which is being processed. It is a visitor pattern
 * which is notified of the processed data in the stack. The user then has the option
 * of keeping the full data in memory (if possible), writing to file or plotting and
 * then letting the data go out of scope.
 */
public interface IExecutionVisitor {
	
	/**
	 * Initialise the execution visitor with the series of operation that are going to be run
	 * 
	 * @param series
	 * @throws Exception
	 */
	public void init(IOperation<? extends IOperationModel, ? extends OperationData>[] series, OriginMetadata origin) throws Exception;
	
	/**
	 * Tell the execution visitor to close, releasing its resources
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception;
	
    /**
     * Called when an execution in the pipeline has run, before the end	but after a given operation.
     * Provides the option of saving the steps information to a file if required.
     * 
     * @param intermediateData
     * @param data
     * @param dataDims TODO
     */
	public void notify(IOperation<? extends IOperationModel, ? extends OperationData> intermediateData, OperationData data, Slice[] slices, int[] shape, int[] dataDims);
	
	/**
	 * Called when the series of operations has been done, with the 
	 * @param result
	 * @param dataDims TODO
	 */
	public void executed(OperationData result, IMonitor monitor, Slice[] slices, int[] shape, int[] dataDims) throws Exception;
	
	
	public class Stub implements IExecutionVisitor {

		@Override
		public void executed(OperationData result, IMonitor monitor, Slice[] slices, int[] shape, int[] dataDims) throws Exception {
			
		}

		@Override
		public void notify(IOperation<? extends IOperationModel, ? extends OperationData> intermeadiateData, OperationData data, Slice[] slices, int[] shape, int[] dataDims) {
			
		}

		@Override
		public void init(IOperation<? extends IOperationModel, ? extends OperationData>[] series, OriginMetadata origin) {
			
		}

		@Override
		public void close() throws Exception {
			
		}
		
	}



}
