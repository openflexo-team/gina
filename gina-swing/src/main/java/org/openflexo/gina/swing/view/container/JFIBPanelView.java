/**
 * 
 * Copyright (c) 2013-2015, Openflexo
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
import java.awt.Rectangle;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBPanel;
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
import org.openflexo.gina.view.container.impl.FIBPanelViewImpl;
import org.openflexo.toolbox.StringUtils;

/**
 * Swing implementation for a basic panel, as a container of some children component, with a given layout, and a border<br>
 * Implementation is based on JPanel
 * 
 * @author sylvain
 */
public class JFIBPanelView extends FIBPanelViewImpl<JPanel, JComponent> implements JFIBView<FIBPanel, JPanel> {

	private static final Logger logger = Logger.getLogger(JFIBPanelView.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JPanel with a given layout<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingPanelRenderingAdapter extends SwingRenderingAdapter<JPanel>
			implements PanelRenderingAdapter<JPanel, JComponent> {

		/*
		 * @Override public void addComponent(JComponent child, JPanel parent,
		 * Object constraints) { if (constraints instanceof
		 * ComponentConstraints) { // ((ComponentConstraints) //
		 * constraint).performConstrainedAddition(getTechnologyComponent(), //
		 * c); performContrainedAddition(parent, child, (ComponentConstraints)
		 * constraints); } else { if (constraints == null) { parent.add(child);
		 * } else { parent.add(child, constraints); } } }
		 */

		@Override
		public Color getDefaultForegroundColor(JPanel component) {
			return UIManager.getColor("Panel.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JPanel component) {
			return UIManager.getColor("Panel.background");
		}

	}

	public JFIBPanelView(FIBPanel model, FIBController controller) {
		super(model, controller, new SwingPanelRenderingAdapter());

		// updateBorder();
	}

	@Override
	protected void performUpdate() {
		// TODO Auto-generated method stub
		super.performUpdate();
	}

	@Override
	public SwingPanelRenderingAdapter getRenderingAdapter() {
		return (SwingPanelRenderingAdapter) super.getRenderingAdapter();
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

	/*
	 * @Override protected void setPanelLayoutParameters(JPanel
	 * technologyComponent) { switch (getComponent().getLayout()) { case none:
	 * technologyComponent.setLayout(null); break; case flow:
	 * technologyComponent.setLayout(new
	 * FlowLayout(getComponent().getFlowAlignment().getAlign(), getComponent()
	 * .getHGap(), getComponent().getVGap())); break; case border:
	 * technologyComponent.setLayout(new BorderLayout()); break; case grid: //
	 * logger
	 * .info("rows="+getComponent().getRows()+" cols="+getComponent().getCols
	 * ()); technologyComponent.setLayout(new
	 * GridLayout(getComponent().getRows(), getComponent().getCols(),
	 * getComponent().getHGap(), getComponent().getVGap())); break; case box:
	 * technologyComponent.setLayout(new BoxLayout(technologyComponent,
	 * getComponent().getBoxLayoutAxis() .getAxis())); break; case twocols:
	 * technologyComponent.setLayout(new GridBagLayout()); break; case gridbag:
	 * technologyComponent.setLayout(new GridBagLayout()); break; case buttons:
	 * technologyComponent.setLayout(new
	 * ButtonLayout(getComponent().getFlowAlignment() != null ? getComponent()
	 * .getFlowAlignment().getAlign() : -1, getComponent().getHGap() != null ?
	 * getComponent().getHGap() : 5)); break; default: break; } }
	 */

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
					if (parent instanceof JViewport && getPreferredSize() != null && JFIBPanelView.this.getComponent() != null) {

						return parent.getWidth() > getPreferredSize().width && JFIBPanelView.this.getComponent().isTrackViewPortWidth();
					}
					if (JFIBPanelView.this.getComponent() != null) {
						return JFIBPanelView.this.getComponent().isTrackViewPortWidth();
					}
				} catch (NullPointerException e) {
					// TODO: remove debug
					System.out.println("parent=" + parent);
					System.out.println("getPreferredSize()=" + getPreferredSize());
					System.out.println("FIBPanelView.this.getComponent()=" + JFIBPanelView.this.getComponent());
				}
				return false;
			}

			@Override
			public boolean getScrollableTracksViewportHeight() {
				Container parent = getParent();
				try {
					if (parent instanceof JViewport && getPreferredSize() != null && JFIBPanelView.this.getComponent() != null) {
						return parent.getHeight() > getPreferredSize().height && JFIBPanelView.this.getComponent().isTrackViewPortHeight();
					}
					if (JFIBPanelView.this.getComponent() != null) {
						return JFIBPanelView.this.getComponent().isTrackViewPortHeight();
					}
				} catch (NullPointerException e) {
					// TODO: remove debug
					System.out.println("parent=" + parent);
					System.out.println("getPreferredSize()=" + getPreferredSize());
					System.out.println("FIBPanelView.this.getComponent()=" + JFIBPanelView.this.getComponent());
				}
				return false;
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
		// updateGraphicalProperties();

		// setPanelLayoutParameters(panel);

		return panel;
	}

	protected void paintAdditionalInfo(Graphics g) {
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	protected void clearContainer() {
		getJComponent().removeAll();
	}

	/*
	 * @Override protected void retrieveContainedJComponentsAndConstraints() {
	 * Vector<FIBComponent> allSubComponents = new Vector<FIBComponent>();
	 * allSubComponents.addAll(getNotHiddenSubComponents());
	 * 
	 * if (getComponent().getLayout() == Layout.flow ||
	 * getComponent().getLayout() == Layout.box || getComponent().getLayout() ==
	 * Layout.buttons || getComponent().getLayout() == Layout.twocols ||
	 * getComponent().getLayout() == Layout.gridbag) {
	 * 
	 * }
	 * 
	 * if (getComponent().getLayout() == Layout.grid) {
	 * 
	 * for (FIBComponent subComponent : getNotHiddenSubComponents()) {
	 * FIBViewImpl<?, JComponent> subView = (FIBViewImpl<?, JComponent>)
	 * getController().viewForComponent( subComponent); if (subView == null) {
	 * subView = (FIBViewImpl<?, JComponent>)
	 * getController().buildView(subComponent); } // FIBViewImpl subView =
	 * getController().buildView(c); registerViewForComponent(subView,
	 * subComponent); }
	 * 
	 * for (int i = 0; i < getComponent().getRows(); i++) { for (int j = 0; j <
	 * getComponent().getCols(); j++) {
	 * registerComponentWithConstraints(getChildComponent(j, i), null); } } }
	 * 
	 * else { for (FIBComponent subComponent : allSubComponents) {
	 * FIBViewImpl<?, JComponent> subView = (FIBViewImpl<?, JComponent>)
	 * getController().viewForComponent( subComponent); if (subView == null) {
	 * subView = (FIBViewImpl<?, JComponent>)
	 * getController().buildView(subComponent); } // FIBViewImpl subView =
	 * getController().buildView(c); registerViewForComponent(subView,
	 * subComponent);
	 * 
	 * // TODO: please handle issue with getResultingJComponent()
	 * registerComponentWithConstraints(((JFIBView<?, JComponent>)
	 * subView).getResultingJComponent(), subComponent.getConstraints()); } } }
	 */

	/*
	 * protected JComponent getResultingJComponent(FIBComponent component) {
	 * System.out.println("on veut ajouter " + component); JFIBView<?,
	 * JComponent> componentView = (JFIBView<?, JComponent>)
	 * getController().viewForComponent(component);
	 * System.out.println("ce qu'on retourne comme vue, c'est " +
	 * componentView);
	 * System.out.println("ce qu'on retourne comme JComponent, c'est " +
	 * componentView.getResultingJComponent()); return
	 * componentView.getResultingJComponent();
	 * 
	 * }
	 */

	// Special case for GridLayout
	/*
	 * protected JComponent getChildComponent(int col, int row) { for
	 * (FIBComponent subComponent : getComponent().getSubComponents()) {
	 * GridLayoutConstraints glc = (GridLayoutConstraints)
	 * subComponent.getConstraints(); if (glc.getX() == col && glc.getY() ==
	 * row) { return getResultingJComponent(subComponent); } } // Otherwise,
	 * it's an empty cell return makeEmptyPanel();
	 * 
	 * }
	 */

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

}
