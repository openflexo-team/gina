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

package org.openflexo.gina.swing.view.container;

import java.awt.Color;
import java.awt.Graphics;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.operator.FIBIteration;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.operator.impl.FIBIterationViewImpl;

public class JFIBIterationView extends FIBIterationViewImpl<JPanel, JComponent> implements JFIBView<FIBIteration, JPanel> {

	private static final Logger logger = Logger.getLogger(JFIBIterationView.class.getPackage().getName());

	public JFIBIterationView(FIBIteration model, FIBController controller) {
		super(model, controller);
	}

	@Override
	protected JPanel makeTechnologyComponent() {
		return null;
	}

	protected void paintAdditionalInfo(Graphics g) {
	}

	@Override
	public void revalidateAndRepaint() {
		// TODO Auto-generated method stub

	}

	@Override
	public JComponent getJComponent() {
		return null;
	}

	@Override
	public JComponent getResultingJComponent() {
		return null;
	}

	@Override
	public SwingIterationRenderingAdapter getRenderingAdapter() {
		return null;
	}

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JPanel with a given layout<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingIterationRenderingAdapter extends SwingRenderingAdapter<JPanel>
			implements ContainerRenderingAdapter<JPanel, JComponent> {

		@Override
		public Color getDefaultForegroundColor(JPanel component) {
			return UIManager.getColor("Panel.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JPanel component) {
			return UIManager.getColor("Panel.background");
		}

	}

	/*@Override
	public ContainerRenderingAdapter<JPanel, JComponent> getRenderingAdapter() {
		// TODO Auto-generated method stub
		return null;
	}*/

	/*@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}*/

	/*@Override
	public void addSubComponentsAndDoLayout() {
		super.addSubComponentsAndDoLayout();
		getTechnologyComponent().revalidate();
		getTechnologyComponent().repaint();
	}*/

	/*@Override
	public FIBLayoutManager<JPanel, JComponent, ?> makeFIBLayoutManager(Layout layoutType) {
		if (layoutType == null) {
			return new JAbsolutePositionningLayout(this);
		}
		switch (layoutType) {
			case none:
				return new JAbsolutePositionningLayout(this);
			case border:
				return new JBorderLayout(this);
			case box:
				return new JBoxLayout(this);
			case flow:
				return new JFlowLayout(this);
			case buttons:
				return new JButtonLayout(this);
			case twocols:
				return new JTwoColsLayout(this);
			case grid:
				return new JGridLayout(this);
			case gridbag:
				return new JGridBagLayout(this);
			default:
				return new JAbsolutePositionningLayout(this);
		}
	}*/

	/*@Override
	public void revalidateAndRepaint() {
		getRenderingAdapter().revalidateAndRepaint(getTechnologyComponent());
	}
	
	private boolean imageIsAsynchronouslyBuilding = false;
	
	private Image makeScaledImage(Image image) {
		if (image == null) {
			return null;
		}
		if (getComponent() == null) {
			return null;
		}
		if (getComponent().isDeleted()) {
			return null;
		}
		int currentWidth = getRenderingAdapter().getWidth(getTechnologyComponent());
		int currentHeight = getRenderingAdapter().getHeight(getTechnologyComponent());
		if (getComponent().getSizeAdjustment() != null) {
			switch (getComponent().getSizeAdjustment()) {
				case OriginalSize:
					return image;
				case FitToAvailableSize:
					if (currentWidth == 0) {
						currentWidth = image.getWidth(this);
					}
					if (currentHeight == 0) {
						currentHeight = image.getHeight(this);
					}
					return image.getScaledInstance(currentWidth, currentHeight, Image.SCALE_SMOOTH);
				case FitToAvailableSizeRespectRatio:
					int imageWidth = image.getWidth(this);
					int imageHeight = image.getHeight(this);
					if (imageWidth <= 0 || imageHeight <= 0) {
						synchronized (this) {
							logger.fine("Image is not ready, waiting...");
							computeImageLater = true;
							return null;
						}
					}
					// This is just looking for troubles because it makes a loop in
					// layout
					//
					if ((currentWidth == 0 || currentHeight == 0) && !imageIsAsynchronouslyBuilding) {
						imageIsAsynchronouslyBuilding = true;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								update();
								imageIsAsynchronouslyBuilding = false;
							}
						});
						// return new ImageIcon(image);
						// In this case, image is not ready yet
						return null;
					}
					double widthRatio = (double) currentWidth / imageWidth;
					double heightRatio = (double) currentHeight / imageHeight;
					double ratio = widthRatio < heightRatio ? widthRatio : heightRatio;
					int newWidth = (int) (imageWidth * ratio);
					int newHeight = (int) (imageHeight * ratio);
					if (newWidth <= 0) {
						newWidth = 1;
					}
					if (newHeight <= 0) {
						newHeight = 1;
					}
					return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
				case AdjustDimensions:
					return image.getScaledInstance(getComponent().getImageWidth(), getComponent().getImageHeight(), Image.SCALE_SMOOTH);
				case AdjustWidth:
					return image.getScaledInstance(getComponent().getImageWidth(), -1, Image.SCALE_SMOOTH);
				case AdjustHeight:
					return image.getScaledInstance(-1, getComponent().getImageHeight(), Image.SCALE_SMOOTH);
				default:
					return null;
			}
		}
		return null;
	}
	
	private boolean computeImageLater = false;
	
	@Override
	protected void updateBackgroundImageDefaultSize(Image image) {
		if (getComponent() == null || image == null) {
			return;
		}
		if (getComponent().getImageWidth() == null) {
			getComponent().setImageWidth(image.getWidth(this));
		}
		if (getComponent().getImageHeight() == null) {
			getComponent().setImageHeight(image.getHeight(this));
		}
	
	}
	
	@Override
	public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		updateBackgroundImageDefaultSize(img);
		if (computeImageLater) {
			logger.fine("Image can now be displayed");
			computeImageLater = false;
			// updateImage();
			update();
		}
		return false;
	}*/

}
