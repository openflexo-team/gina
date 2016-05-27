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

package org.openflexo.gina.swing.view.widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.ImageObserver;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBImage;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.widget.FIBImageWidget;
import org.openflexo.gina.view.impl.FIBViewImpl;
import org.openflexo.gina.view.widget.impl.FIBImageWidgetImpl;

/**
 * Swing implementation for a simple widget allowing to display an image (a JLabel)
 * 
 * @author sylvain
 */
public class JFIBImageWidget extends FIBImageWidgetImpl<JLabel>implements ImageObserver, JFIBView<FIBImage, JLabel> {

	private static final Logger LOGGER = Logger.getLogger(JFIBImageWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JLabel image<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingImageRenderingAdapter extends SwingRenderingAdapter<JLabel>implements ImageRenderingAdapter<JLabel> {

		@Override
		public Image getImage(JLabel component, FIBImageWidget<JLabel> widget) {
			if (component.getIcon() instanceof ImageIcon) {
				return ((ImageIcon) component.getIcon()).getImage();
			}
			return null;
		}

		@Override
		public void setImage(JLabel component, Image anImage, FIBImageWidget<JLabel> widget) {
			if (!((FIBViewImpl)widget).isUpdating()) {
				component.setIcon(((JFIBImageWidget) widget).makeImageIcon(anImage));
				}
		}

		@Override
		public int getHorizontalAlignment(JLabel component) {
			return component.getHorizontalAlignment();
		}

		@Override
		public void setHorizontalAlignment(JLabel component, int align) {
			component.setHorizontalAlignment(align);
		}

		@Override
		public Color getDefaultForegroundColor(JLabel component) {
			return UIManager.getColor("Label.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JLabel component) {
			return UIManager.getColor("Label.background");
		}

	}

	public static SwingImageRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingImageRenderingAdapter();

	public JFIBImageWidget(FIBImage model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	@Override
	public SwingImageRenderingAdapter getRenderingAdapter() {
		return (SwingImageRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	// private Dimension size;

	// private boolean imageResizeRequested = false;

	@Override
	protected JLabel makeTechnologyComponent() {

		// We implement here a special scheme to listen to resize events that request for a given size
		// We do that for image size adaptation schemes
		final JLabel labelWidget = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				// System.out.println("Preferred size for " + getSize() + " return " + super.getPreferredSize());
				/*if (size != null) {
					return size;
				}*/
				return super.getPreferredSize();
			}
		};

		labelWidget.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				Rectangle b = (e.getSource() != null ? ((Component) e.getSource()).getBounds() : null);
				// size = b.getSize();
				updateImageSizeAdjustment();

				/*System.out.println("Resize called");
				
				if (!imageResizeRequested) {
					System.out.println("OK je demande de mettre a jour");
					imageResizeRequested = true;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if (imageResizeRequested) {
								System.out.println("On met a jour pour de vrai");
								updateImageSizeAdjustment();
								imageResizeRequested = false;
							}
						}
					});
				}*/

			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		labelWidget.setFocusable(false); // There is not much point in giving
											// focus to a label since there is
											// no KeyBindings nor KeyListener
											// on it.
		if (getWidget().getData().isValid()) {
			labelWidget.setText(" ");
		}
		return labelWidget;
	}

	private ImageIcon makeImageIcon(Image image) {
		if (image == null) {
			return null;
		}
		if (getWidget() == null) {
			return null;
		}
		int currentWidth = getRenderingAdapter().getWidth(getTechnologyComponent());
		int currentHeight = getRenderingAdapter().getHeight(getTechnologyComponent());
		switch (getWidget().getSizeAdjustment()) {
			case OriginalSize:
				return new ImageIcon(image);
			case FitToAvailableSize:
				return new ImageIcon(image.getScaledInstance(currentWidth, currentHeight, Image.SCALE_SMOOTH));
			case FitToAvailableSizeRespectRatio:
				int imageWidth = image.getWidth(this);
				int imageHeight = image.getHeight(this);
				if (imageWidth <= 0 || imageHeight <= 0) {
					synchronized (this) {
						LOGGER.fine("Image is not ready, waiting...");
						computeImageLater = true;
						return null;
					}
				}
				// This is just looking for troubles because it makes a loop in
				// layout
				//
				if (currentWidth == 0 || currentHeight == 0) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							// updateImage();
							update();
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
				return new ImageIcon(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH));
			case AdjustDimensions:
				return new ImageIcon(
						image.getScaledInstance(getWidget().getImageWidth(), getWidget().getImageHeight(), Image.SCALE_SMOOTH));
			case AdjustWidth:
				return new ImageIcon(image.getScaledInstance(getWidget().getImageWidth(), -1, Image.SCALE_SMOOTH));
			case AdjustHeight:
				return new ImageIcon(image.getScaledInstance(-1, getWidget().getImageHeight(), Image.SCALE_SMOOTH));
			default:
				return null;
		}
	}

	private boolean computeImageLater = false;

	@Override
	protected void updateImageDefaultSize(Image image) {
		if (getWidget() == null || image == null) {
			return;
		}
		if (getWidget().getImageWidth() == null) {
			getWidget().setImageWidth(image.getWidth(this));
		}
		if (getWidget().getImageHeight() == null) {
			getWidget().setImageHeight(image.getHeight(this));
		}

	}

	@Override
	public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		updateImageDefaultSize(img);
		if (computeImageLater) {
			LOGGER.fine("Image can now be displayed");
			computeImageLater = false;
			// updateImage();
			update();
		}
		return false;
	}

}
