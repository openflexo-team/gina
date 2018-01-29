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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.ImageObserver;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBIteration;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.FIBLayoutManager;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.container.layout.JAbsolutePositionningLayout;
import org.openflexo.gina.swing.view.container.layout.JBorderLayout;
import org.openflexo.gina.swing.view.container.layout.JBoxLayout;
import org.openflexo.gina.swing.view.container.layout.JButtonLayout;
import org.openflexo.gina.swing.view.container.layout.JFlowLayout;
import org.openflexo.gina.swing.view.container.layout.JGridBagLayout;
import org.openflexo.gina.swing.view.container.layout.JGridLayout;
import org.openflexo.gina.swing.view.container.layout.JTwoColsLayout;
import org.openflexo.gina.view.container.FIBIterationView;
import org.openflexo.gina.view.container.impl.FIBIterationViewImpl;
import org.openflexo.toolbox.StringUtils;

public class JFIBIterationView extends FIBIterationViewImpl<JPanel, JComponent> implements ImageObserver, JFIBView<FIBIteration, JPanel> {

	private static final Logger logger = Logger.getLogger(JFIBIterationView.class.getPackage().getName());

	public JFIBIterationView(FIBIteration model, FIBController controller) {
		super(model, controller, new SwingIterationRenderingAdapter());
	}

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JPanel with a given layout<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingIterationRenderingAdapter extends SwingRenderingAdapter<JPanel>
			implements IterationRenderingAdapter<JPanel, JComponent> {

		@Override
		public Color getDefaultForegroundColor(JPanel component) {
			return UIManager.getColor("Panel.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JPanel component) {
			return UIManager.getColor("Panel.background");
		}

		@Override
		public Image getBackgroundImage(JPanel component, FIBIterationView<JPanel, JComponent> panelView) {
			return ((JFIBIterationView) panelView).backgroundImage;
		}

		@Override
		public void setBackgroundImage(JPanel component, Image anImage, FIBIterationView<JPanel, JComponent> panelView) {
			if (anImage != null) {
				Image scaledImage = ((JFIBIterationView) panelView).makeScaledImage(anImage);
				((JFIBIterationView) panelView).backgroundImage = scaledImage;
			}
			else {
				((JFIBIterationView) panelView).backgroundImage = null;
			}
			revalidateAndRepaint(component);
		}

	}

	private Image backgroundImage = null;

	@Override
	protected void performUpdate() {
		// TODO Auto-generated method stub
		super.performUpdate();
	}

	@Override
	public SwingIterationRenderingAdapter getRenderingAdapter() {
		return (SwingIterationRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	@Override
	public void updateBorder() {
		if (getComponent() == null) {
			System.out.println("QUI fait un updateBorder() sans composant ????  deleted=" + isDeleted());
			return;
		}
		switch (getComponent().getBorder()) {
			case empty:
				getTechnologyComponent().setBorder(
						BorderFactory.createEmptyBorder(getComponent().getBorderTop() != null ? getComponent().getBorderTop() : 0,
								getComponent().getBorderLeft() != null ? getComponent().getBorderLeft() : 0,
								getComponent().getBorderBottom() != null ? getComponent().getBorderBottom() : 0,
								getComponent().getBorderRight() != null ? getComponent().getBorderRight() : 0));
				break;
			case etched:
				getTechnologyComponent().setBorder(BorderFactory.createEtchedBorder());
				break;
			case line:
				getTechnologyComponent().setBorder(BorderFactory
						.createLineBorder(getComponent().getBorderColor() != null ? getComponent().getBorderColor() : Color.black));
				break;
			case lowered:
				getTechnologyComponent().setBorder(BorderFactory.createLoweredBevelBorder());
				break;
			case raised:
				getTechnologyComponent().setBorder(BorderFactory.createRaisedBevelBorder());
				break;
			case titled:
				getTechnologyComponent().setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
						getLocalized(getComponent().getBorderTitle()), TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION,
						getComponent().retrieveValidFont(), getComponent().retrieveValidForegroundColor()));
				break;
			case rounded3d:
				getTechnologyComponent().setBorder(new RoundedBorder(
						StringUtils.isNotEmpty(getComponent().getBorderTitle()) ? getLocalized(getComponent().getBorderTitle()) : null,
						getComponent().getBorderTop() != null ? getComponent().getBorderTop() : 0,
						getComponent().getBorderLeft() != null ? getComponent().getBorderLeft() : 0,
						getComponent().getBorderBottom() != null ? getComponent().getBorderBottom() : 0,
						getComponent().getBorderRight() != null ? getComponent().getBorderRight() : 0, getComponent().getTitleFont(),
						getComponent().retrieveValidForegroundColor(), getComponent().getDarkLevel()));
				break;
			default:
				break;
		}
	}

	@Override
	protected JPanel makeTechnologyComponent() {
		class ScrollablePanel extends JPanel implements Scrollable {

			@Override
			public Dimension getPreferredScrollableViewportSize() {
				return getPreferredSize();
			}

			@Override
			public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
				switch (orientation) {
					case SwingConstants.VERTICAL:
						return visibleRect.height / 10;
					case SwingConstants.HORIZONTAL:
						return visibleRect.width / 10;
					default:
						throw new IllegalArgumentException("Invalid orientation: " + orientation);
				}
			}

			@Override
			public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
				switch (orientation) {
					case SwingConstants.VERTICAL:
						return visibleRect.height;
					case SwingConstants.HORIZONTAL:
						return visibleRect.width;
					default:
						throw new IllegalArgumentException("Invalid orientation: " + orientation);
				}
			}

			@Override
			public boolean getScrollableTracksViewportWidth() {
				Container parent = getParent();
				try {
					if (parent instanceof JViewport && getPreferredSize() != null && JFIBIterationView.this.getComponent() != null) {

						return parent.getWidth() > getPreferredSize().width && JFIBIterationView.this.getComponent().isTrackViewPortWidth();
					}
					if (JFIBIterationView.this.getComponent() != null) {
						return JFIBIterationView.this.getComponent().isTrackViewPortWidth();
					}
				} catch (NullPointerException e) {
					// TODO: remove debug
					System.out.println("parent=" + parent);
					System.out.println("getPreferredSize()=" + getPreferredSize());
					System.out.println("FIBPanelView.this.getComponent()=" + JFIBIterationView.this.getComponent());
				}
				return false;
			}

			@Override
			public boolean getScrollableTracksViewportHeight() {
				Container parent = getParent();
				try {
					if (parent instanceof JViewport && getPreferredSize() != null && JFIBIterationView.this.getComponent() != null) {
						return parent.getHeight() > getPreferredSize().height
								&& JFIBIterationView.this.getComponent().isTrackViewPortHeight();
					}
					if (JFIBIterationView.this.getComponent() != null) {
						return JFIBIterationView.this.getComponent().isTrackViewPortHeight();
					}
				} catch (NullPointerException e) {
					// TODO: remove debug
					System.out.println("parent=" + parent);
					System.out.println("getPreferredSize()=" + getPreferredSize());
					System.out.println("FIBPanelView.this.getComponent()=" + JFIBIterationView.this.getComponent());
				}
				return false;
			}

			@Override
			protected void paintComponent(Graphics g) {
				if (backgroundImage != null) {
					// TODO: handle align and valign !!!
					g.drawImage(backgroundImage, 0, 0, backgroundImage.getWidth(null), backgroundImage.getHeight(null), this);
				}
				super.paintComponent(g);
				// g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
			}

		}

		ScrollablePanel panel = new ScrollablePanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				paintAdditionalInfo(g);
			}
		};
		panel.setOpaque(false);

		panel.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				updateBackgroundImageSizeAdjustment();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		return panel;
	}

	protected void paintAdditionalInfo(Graphics g) {
	}

	@Override
	protected void clearContainer() {
		getJComponent().removeAll();
	}

	@Override
	protected void addSubComponentsAndDoLayout() {
		super.addSubComponentsAndDoLayout();
		getTechnologyComponent().revalidate();
		getTechnologyComponent().repaint();
	}

	@Override
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
	}

	@Override
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
	}

}
