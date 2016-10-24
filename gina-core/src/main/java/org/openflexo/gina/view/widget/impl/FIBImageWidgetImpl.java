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

package org.openflexo.gina.view.widget.impl;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBImage;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBImageWidget;
import org.openflexo.rm.Resource;
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

	public FIBImageWidgetImpl(FIBImage model, FIBController controller, ImageRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
		// updateAlign();
		// updateImage();
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateAlign();
		updateImageFile();
		updateImageSizeAdjustment();
		// updateImage();
		// TODO: Check this
		// This is a quick and dirty fixed caused by a deadlock
		// during java.awt.MediaTracker.waitForID(MediaTracker.java:651)
		/*SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// updateVisibility();
				// isUpdating() = true;
				updateData();
				// widgetUpdating = false;
			}
		});*/
	}

	@Override
	public ImageRenderingAdapter<C> getRenderingAdapter() {
		return (ImageRenderingAdapter) super.getRenderingAdapter();
	}

	/*@Override
	public boolean updateWidgetFromModel() {
		if (modelUpdating) {
			return false;
		}
		// TODO: Check this
		// This is a quick and dirty fixed caused by a deadlock
		// during java.awt.MediaTracker.waitForID(MediaTracker.java:651)
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateVisibility();
				widgetUpdating = true;
				updateImage();
				widgetUpdating = false;
			}
		});
		return false;
	}*/

	/**
	 * Update the model given the actual state of the widget
	 */
	/*@Override
	public boolean updateModelFromWidget() {
		// Read only component
		return false;
	}*/

	final protected void updateAlign() {
		if (getWidget().getAlign() == null) {
			return;
		}
		getRenderingAdapter().setHorizontalAlignment(getTechnologyComponent(), getWidget().getAlign().getAlign());
	}

	@Override
	public Image updateData() {
		Image dataImage = super.updateData();
		if (getWidget().getData().isValid()) {
			originalImage = dataImage;
			updateImageDefaultSize(originalImage);
			getRenderingAdapter().setImage(getTechnologyComponent(), originalImage, this);
		}
		/*else if (getWidget().getImageFile() != null) {
			if (getWidget().getImageFile().exists()) {
				Image image = ImageUtils.loadImageFromFile(getWidget().getImageFile());
				updateImageDefaultSize(image);
				getRenderingAdapter().setImage(getTechnologyComponent(), image, this);
			}
		}*/
		return originalImage;
	}

	private Image originalImage;
	private Resource loadedImageResource = null;

	protected void updateImageSizeAdjustment() {
		// System.out.println("originalImage = " + originalImage);
		if (originalImage != null) {
			updateImageDefaultSize(originalImage);
			getRenderingAdapter().setImage(getTechnologyComponent(), originalImage, this);
		}
	}

	protected void updateImageFile() {
		if (notEquals(getWidget().getImageFile(), loadedImageResource)) {
			if (getWidget().getImageFile() != null) {
				originalImage = ImageUtils.loadImageFromResource(getWidget().getImageFile());
				loadedImageResource = getWidget().getImageFile();
				updateImageDefaultSize(originalImage);
				getRenderingAdapter().setImage(getTechnologyComponent(), originalImage, this);
			}
			else {
				originalImage = null;
				loadedImageResource = null;
			}
		}
	}

	protected abstract void updateImageDefaultSize(Image image);

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if ((evt.getPropertyName().equals(FIBImage.ALIGN_KEY))) {
			updateAlign();
		}
		else if (evt.getPropertyName().equals(FIBImage.IMAGE_FILE_KEY)) {
			updateImageFile();
			relayoutParentBecauseImageChanged();
		}
		else if ((evt.getPropertyName().equals(FIBImage.SIZE_ADJUSTMENT_KEY)) || (evt.getPropertyName().equals(FIBImage.IMAGE_HEIGHT_KEY))
				|| (evt.getPropertyName().equals(FIBImage.IMAGE_WIDTH_KEY))) {
			updateImageSizeAdjustment();
			relayoutParentBecauseImageChanged();
		}
		super.propertyChange(evt);
	}

	protected void relayoutParentBecauseImageChanged() {
		FIBContainerView<?, ?, ?> parentView = getParentView();
		if (parentView != null) {
			parentView.updateLayout();
		}
		// FIBEditorController controller = getEditorController();
		// controller.notifyFocusedAndSelectedObject();
	}

}
