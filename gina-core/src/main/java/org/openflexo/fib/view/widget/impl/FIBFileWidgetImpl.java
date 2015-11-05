/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.fib.view.widget.impl;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.widget.FIBFile;
import org.openflexo.fib.view.impl.FIBWidgetViewImpl;
import org.openflexo.fib.view.widget.FIBFileWidget;

/**
 * Default base implementation for a widget able to edit or select a File
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 *
 * @author sylvain
 */
public abstract class FIBFileWidgetImpl<C> extends FIBWidgetViewImpl<FIBFile, C, File>implements FIBFileWidget<C> {

	static final Logger LOGGER = Logger.getLogger(FIBFileWidgetImpl.class.getPackage().getName());

	protected FIBFile.FileMode mode;
	protected String filter;
	protected String title;
	protected Boolean isDirectory;
	protected File defaultDirectory;

	public FIBFileWidgetImpl(FIBFile model, FIBController controller, FileWidgetRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);

		mode = model.getMode() != null ? model.getMode() : FIBFile.FileMode.OpenMode;
		filter = model.getFilter();
		title = model.getTitle();
		isDirectory = model.isDirectory();
		defaultDirectory = model.getDefaultDirectory() != null ? model.getDefaultDirectory() : new File(System.getProperty("user.dir"));
		setSelectedFile(null);

	}

	@Override
	public FileWidgetRenderingAdapter<C> getRenderingAdapter() {
		return (FileWidgetRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (notEquals(getValue(), getSelectedFile())) {
			widgetUpdating = true;
			if (getValue() instanceof File) {
				setSelectedFile(getValue());
			}
			else if (getValue() == null) {
				setSelectedFile(null);
			}
			widgetUpdating = false;
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), getSelectedFile())) {
			modelUpdating = true;
			setValue(getSelectedFile());
			modelUpdating = false;
			return true;
		}
		return false;
	}

	public File getSelectedFile() {
		return getRenderingAdapter().getSelectedFile(getTechnologyComponent());
	}

	protected void setSelectedFile(File aFile) {
		getRenderingAdapter().setSelectedFile(getTechnologyComponent(), aFile);
	}

}
