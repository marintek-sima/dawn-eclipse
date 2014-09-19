/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.dawnsci.analysis.api.processing.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * By default all fields in the model are editable. This annotation can be used to 
 * mark fields as invisible or just read only. This is used in the GUI to determine which
 * fields of the model should be editable in the UI.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationModelField {

	/**
	 * 
	 * @return true if the field is visible in the UI
	 */
	public boolean visible() default true;
	
	/**
	 * 
	 * @return true if the field is editable or false for read only.
	 */
	public boolean editable() default true;

	/**
	 * The label attribute. If unset, uses the name of the field for the label.
	 */
	public String label() default "";
	
	/**
	 * 
	 * @return maximum allowed legal value for field
	 */
	public double max() default 1000d;
	
	/**
	 * 
	 * @return minimum allowed legal value for field
	 */
	public double min() default 0d;

	/**
	 * 
	 * @return the unit that the fields value should be in.
	 */
	public String unit() default "";
	
	/**
	 * 
	 * @return the string hint which is shown to the user when they first edit the value.
	 */
	public String hint() default "";
	
	/**
	 * If the field is a String, java.io.File, java.nio.file.Path or IResource
	 * you may use this annotation to define the type of checking which will be done.
	 * 
	 * If this field is not used and your field is a File for instance, the NEW_FILE
	 * option will be the default, rather than NONE
	 * 
	 * @return the file type.
	 */
	public FileType file() default FileType.NONE;
}