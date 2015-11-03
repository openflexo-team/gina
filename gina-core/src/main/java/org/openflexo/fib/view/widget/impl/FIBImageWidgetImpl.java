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

import java.awt.Image;
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBImage;
import org.openflexo.fib.view.impl.FIBWidgetViewImpl;
import org.openflexo.fib.view.widget.FIBImageWidget;
import org.openflexo.swing.ImageUtils;

/**
 * Default base implementation for a widget allowing to display an image<br>
 * Image can be statically or dynamically retrieved
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBImageWidgetImpl<C> extends FIBWidgetViewImpl<FIBImage, C, Image>implements FIBImageWidget<C> {

	private static final Logger LOGGER = Logger.getLogger(FIBImageWidgetImpl.class.getPackage().getName());

	public FIBImageWidgetImpl(FIBImage model, FIBController controller, ImageRenderingTechnologyAdapter<C> renderingTechnologyAdapter) {
		super(model, controller, renderingTechnologyAdapter);
		updateAlign();
		updateImage();
	}

	@Override
	public ImageRenderingTechnologyAdapter<C> getRenderingTechnologyAdapter() {
		return (ImageRenderingTechnologyAdapter) super.getRenderingTechnologyAdapter();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (modelUpdating) {
			return false;
		}
		widgetUpdating = true;
		updateImage();
		widgetUpdating = false;
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		// Read only component
		return false;
	}

	final protected void updateAlign() {
		if (getWidget().getAlign() == null) {
			return;
		}
		getRenderingTechnologyAdapter().setHorizontalAlignment(getTechnologyComponent(), getWidget().getAlign().getAlign());
	}

	final protected void updateImage() {
		if (getWidget().getData().isValid()) {
			Image image = getValue();
			updateImageDefaultSize(image);
			getRenderingTechnologyAdapter().setImage(getTechnologyComponent(), image, this);
		}
		else if (getWidget().getImageFile() != null) {
			if (getWidget().getImageFile().exists()) {
				Image image = ImageUtils.loadImageFromFile(getWidget().getImageFile());
				updateImageDefaultSize(image);
				getRenderingTechnologyAdapter().setImage(getTechnologyComponent(), image, this);
			}
		}
	}

	protected abstract void updateImageDefaultSize(Image image);

}
