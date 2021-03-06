/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.dawnsci.analysis.api.processing;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.eclipse.dawnsci.analysis.api.dataset.IDataset;
import org.eclipse.dawnsci.analysis.api.dataset.ILazyDataset;
import org.eclipse.dawnsci.analysis.api.metadata.AxesMetadata;
import org.eclipse.dawnsci.analysis.api.metadata.IDiffractionMetadata;
import org.eclipse.dawnsci.analysis.api.metadata.IMetadata;
import org.eclipse.dawnsci.analysis.api.metadata.MaskMetadata;
import org.eclipse.dawnsci.analysis.api.metadata.MetadataType;
import org.eclipse.dawnsci.analysis.api.metadata.OriginMetadata;
import org.eclipse.dawnsci.analysis.api.monitor.IMonitor;
import org.eclipse.dawnsci.analysis.api.processing.model.IOperationModel;

public abstract class AbstractOperation<T extends IOperationModel, D extends OperationData> implements IOperation<T, D> {

	protected T model;

	private String            name;
	private String            description;
	private OperationCategory category;
	
	private boolean storeOutput = false;
	private boolean passUnmodifiedData = false;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		if (description == null)
			return getId();
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public D execute(IDataset slice, IMonitor monitor) throws OperationException {
		
		IDataset view = slice.getSliceView().squeeze();
		
		D output = process(view,monitor);
		
		try {
			output.getData().setMetadata(slice.getMetadata(OriginMetadata.class).get(0));
		} catch (Exception e) {
			throw new OperationException(this,e);
		}
		
		return updateOutputToFullRank(output, slice);
		
	}
	
	private D updateOutputToFullRank(D output, IDataset original) throws OperationException {
		
		int outr = output.getData().getRank();
		int inr = original.getRank();
		
		//Check ranks acceptable for this step
		if (getOutputRank().equals(OperationRank.ZERO) || getOutputRank().equals(OperationRank.NONE) || getOutputRank().getRank() > 2) throw new OperationException(null, "Invalid Operation Rank!");
		
		int rankDif = 0;
		
		if (!getOutputRank().equals(OperationRank.SAME)) {
			
			rankDif = getInputRank().getRank() - getOutputRank().getRank();
		}
		
		if (inr == outr) return output;
		
		List<AxesMetadata> metadata = null;
		List<AxesMetadata> metaout = null;
		
		try {
			metadata = original.getMetadata(AxesMetadata.class);
		} catch (Exception e) {
			throw new OperationException(this, e);
		}
		
		int[] datadims = getOriginalDataDimensions(original).clone();
		Arrays.sort(datadims);
		
		AxesMetadata corMeta  = null;
		
		if (metadata != null && !metadata.isEmpty() && metadata.get(0) != null) {
			
			//update it all for new data;
			try {
				metaout = output.getData().getMetadata(AxesMetadata.class);
			} catch (Exception e) {
				throw new OperationException(this, e);
			}
			
			AxesMetadata inMeta = metadata.get(0);
			
			AxesMetadata axOut = null;
			if (metaout != null && !metaout.isEmpty()) axOut = metaout.get(0);
			
			
			AxesMetadata cloneMeta = (AxesMetadata) inMeta.clone();
			
			if (getInputRank().getRank() == getOutputRank().getRank()) {
				//axes will also be same rank
				corMeta = cloneMeta;
				int c = 0;
				int[] shape = new int[inr];
				
				for (int i : datadims) {
					if (c >= outr) continue;
					ILazyDataset[] axes = null;
					if (axOut != null) {
						Arrays.fill(shape, 1);
						axes = axOut.getAxis(c++);
						if (axes != null) for (ILazyDataset axis : axes) {
							if (axis == null) continue;
							axis.squeeze();
							shape[i] = axis.getShape()[0];
							axis.setShape(shape);
						}
					}
					
					corMeta.setAxis(i, axes == null ? new ILazyDataset[1] : axes);
				}
				
			} else if (getInputRank().getRank() > getOutputRank().getRank()) {
				//made smaller
				
				//rankDif = getInputRank().getRank() - getOutputRank().getRank();
				int[] shape = new int[inr-rankDif];
				corMeta = inMeta.createAxesMetadata(inr-rankDif);
				int count = 0;
				for (int i = 0; i < inr; i++) {
					if ((Arrays.binarySearch(datadims, i) >= 0 || axOut == null)) {
						if (count < rankDif) {
							ILazyDataset[] axes = null;
							if (axOut != null) {
								Arrays.fill(shape, 1);
								axes = axOut.getAxis(count++);
								if (axes != null) for (ILazyDataset axis : axes) {
									axis.squeeze();
									shape[i] = axis.getShape()[0];
									axis.setShape(shape);
								}
							}
							if (i < shape.length) corMeta.setAxis(i, axes == null ? new ILazyDataset[1] : axes);
						}
					} else {
						ILazyDataset[] axes = null;
							Arrays.fill(shape, 1);
							axes = cloneMeta.getAxis(i);
							if (axes != null) for (ILazyDataset axis : axes) {
								if (axis == null) continue;
								ILazyDataset squeeze = axis.getSlice().squeeze();
								shape[i] = squeeze.getShape().length > 0 ? squeeze.getShape()[0] : 1;
								axis.setShape(shape);
							}
							corMeta.setAxis(i-count, axes == null ? new ILazyDataset[1] : axes);
						}
						
					}
				
			} else if (getInputRank().getRank() < getOutputRank().getRank())  {
				//made bigger
				//FIXME not actually working yet!!!!!
				//int rankDif = getInputRank().getRank() - getOutputRank().getRank();
				corMeta = inMeta.createAxesMetadata(inr-rankDif);
				int[] shape = new int[inr-rankDif];
				int count = 0;
				for (int i = 0; i < inr-rankDif; i++) {
					if ((Arrays.binarySearch(datadims, i) < 0 || axOut == null) && count > rankDif) {
						ILazyDataset[] axes = null;
						if (axOut != null) {
							Arrays.fill(shape, 1);
							axes = axOut.getAxis(i);
							for (ILazyDataset axis : axes) {
								ILazyDataset squeeze = axis.getSlice().squeeze();
								shape[i] = squeeze.getShape()[0];
								axis.setShape(shape);
							}
						}
						corMeta.setAxis(i, axes);
					} else {
						ILazyDataset[] axes = null;
						if (axOut != null) {
							Arrays.fill(shape, 1);
							axes = cloneMeta.getAxis(i);
							for (ILazyDataset axis : axes) {
								ILazyDataset squeeze = axis.getSlice().squeeze();
								shape[i] = squeeze.getShape()[0];
								axis.setShape(shape);
							}
						}
						corMeta.setAxis(i-count, axes);
					}
				}
			}
			
			output.getData().clearMetadata(AxesMetadata.class);
//			updateOutputDataShape(output.getData(), inr+rankDiff, datadims);
			
//			if (corMeta != null) output.getData().setMetadata(corMeta);
			
		}
		
		updateOutputDataShape(output.getData(), inr-rankDif, datadims);
		if (corMeta != null) output.getData().setMetadata(corMeta);
		
		updateAuxData(output.getAuxData(), original);
		
		return output;
	}
	
	private void updateOutputDataShape(IDataset out, int rank, int[] dataDims) {
		int[] shape = out.squeeze().getShape();
		
		int[] updated = new int[rank];
		Arrays.fill(updated, 1);
		
		for (int i = 0 ; i < Math.min(dataDims.length,shape.length); i++) {
			updated[dataDims[i]] = shape[i]; 
		}
		
		out.setShape(updated);
		
		
	}
	
	private void updateAuxData(Serializable[] auxData, IDataset original){
		
		if (auxData == null || auxData[0] == null) return;
		
		int[] datadims = getOriginalDataDimensions(original).clone();
		
		if (datadims.length > getOutputRank().getRank()) {
			datadims = new int[]{datadims[0]};
		}
		
		Arrays.sort(datadims);

		
		int[] shape = new int[original.getRank()-datadims.length];
		Arrays.fill(shape, 1);
		
		List<AxesMetadata> metadata = null;
		
		try {
			metadata = original.getMetadata(AxesMetadata.class);
		} catch (Exception e) {
			throw new OperationException(this, e);
		}
		
		
		for (int i = 0; i < auxData.length; i++) {
			if (!(auxData[i] instanceof IDataset) || ((IDataset)auxData[i]).getRank() != 0 ) {
				continue;
			}
			
			IDataset ds = (IDataset)auxData[i];
			ds.setShape(shape);
			
			if (metadata != null && !metadata.isEmpty() && metadata.get(0) != null) {
				AxesMetadata outMeta = metadata.get(0).createAxesMetadata(shape.length);
				AxesMetadata inMeta = metadata.get(0);
				int counter = 0;
				for (int j = 0; j < original.getRank();j++) {
					if (Arrays.binarySearch(datadims, j)<0) {
						ILazyDataset[] axes = inMeta.getAxis(j);
						if (axes != null && axes[0] != null) {
							ILazyDataset view = axes[0].getSliceView();
							view.setShape(shape);
							outMeta.setAxis(counter++, new ILazyDataset[]{view});
						}
						
					}
				}
				ds.setMetadata(outMeta);
			}
			
		}
	}
	
	@SuppressWarnings("unused")
	protected D process(IDataset input, IMonitor monitor) throws OperationException {
		return null;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractOperation other = (AbstractOperation) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractOperation [name=" + name + "]";
	}

	@Override
	public T getModel() {
		return model;
	}

	@Override
	public void setModel(T model) {
		this.model = model;
	}

	public static ILazyDataset[] getFirstAxes(IDataset slice) {
		List<AxesMetadata> metaList = null;

		try {
			metaList = slice.getMetadata(AxesMetadata.class);
			if (metaList == null || metaList.isEmpty())
				return null;
		} catch (Exception e) {
			return null;
		}

		AxesMetadata am = metaList.get(0);
		if (am == null)
			return null;

		return am.getAxes();
	}

	public static ILazyDataset getFirstMask(IDataset slice) {

		List<MaskMetadata> metaList = null;

		try {
			metaList = slice.getMetadata(MaskMetadata.class);
			if (metaList == null || metaList.isEmpty())
				return null;
		} catch (Exception e) {
			return null;
		}

		MaskMetadata mm = metaList.get(0);
		if (mm == null)
			return null;

		return mm.getMask();
	}

	public static IDiffractionMetadata getFirstDiffractionMetadata(IDataset slice) {

		List<IMetadata> metaList;

		try {
			metaList = slice.getMetadata(IMetadata.class);
			if (metaList == null || metaList.isEmpty())
				return null;
		} catch (Exception e) {
			return null;
		}

		for (IMetadata meta : metaList)
			if (meta instanceof IDiffractionMetadata)
				return (IDiffractionMetadata) meta;

		return null;
	}

	public static int[] getOriginalDataDimensions(IDataset slice) {

		OriginMetadata originMetadata = getOriginMetadata(slice);

		return originMetadata == null ? null : originMetadata.getDataDimensions();

	}
	
	public static OriginMetadata getOriginMetadata(IDataset slice){
		List<OriginMetadata> metaList = null;

		try {
			metaList = slice.getMetadata(OriginMetadata.class);
			if (metaList == null || metaList.isEmpty())
				return null;
		} catch (Exception e) {
			return null;
		}

		return metaList.get(0);
	}

	public void copyMetadata(IDataset original, IDataset out) {
		try {
			List<MetadataType> metadata = original.getMetadata(null);

			for (MetadataType m : metadata) {
				out.addMetadata(m);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setStoreOutput(boolean storeOutput) {
		this.storeOutput = storeOutput;
	}
	
	@Override
	public boolean isStoreOutput() {
		return storeOutput;
	}
	
	@Override
	public void setPassUnmodifiedData(boolean passUnmodifiedData) {
		this.passUnmodifiedData = passUnmodifiedData;
	}

	@Override
	public boolean isPassUnmodifiedData() {
		return passUnmodifiedData;
	}

	public OperationCategory getCategory() {
		return category;
	}

	public void setCategory(OperationCategory category) {
		this.category = category;
	}
}
