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

import java.awt.AWTError;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.BorderLayoutConstraints;
import org.openflexo.gina.model.container.BoxLayoutConstraints;
import org.openflexo.gina.model.container.ButtonLayoutConstraints;
import org.openflexo.gina.model.container.ComponentConstraints;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.FlowLayoutConstraints;
import org.openflexo.gina.model.container.GridBagLayoutConstraints;
import org.openflexo.gina.model.container.GridLayoutConstraints;
import org.openflexo.gina.model.container.NoneLayoutConstraints;
import org.openflexo.gina.model.container.SplitLayoutConstraints;
import org.openflexo.gina.model.container.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.container.impl.FIBPanelViewImpl;
import org.openflexo.gina.view.impl.FIBViewImpl;
import org.openflexo.swing.ButtonLayout;
import org.openflexo.toolbox.StringUtils;

/**
 * Swing implementation for a basic panel, as a container of some children component, with a given layout, and a border<br>
 * Implementation is based on JPanel
 * 
 * @author sylvain
 */
public class JFIBPanelView extends FIBPanelViewImpl<JPanel, JComponent> {

	private static final Logger logger = Logger.getLogger(JFIBPanelView.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JPanel with a given layout<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingPanelRenderingAdapter extends SwingRenderingAdapter<JPanel>
			implements PanelRenderingAdapter<JPanel, JComponent> {

		@Override
		public void addComponent(JComponent child, JPanel parent, Object constraints) {
			if (constraints instanceof ComponentConstraints) {
				// ((ComponentConstraints) constraint).performConstrainedAddition(getTechnologyComponent(), c);
				performContrainedAddition(parent, child, (ComponentConstraints) constraints);
			}
			else {
				if (constraints == null) {
					parent.add(child);
				}
				else {
					parent.add(child, constraints);
				}
			}
		}

		private void performContrainedAddition(JPanel container, JComponent contained, ComponentConstraints constraints) {
			if (constraints instanceof BorderLayoutConstraints) {
				BorderLayoutConstraints borderConstraints = (BorderLayoutConstraints) constraints;
				container.add(contained, borderConstraints.getLocation().getConstraint());
			}
			else if (constraints instanceof BoxLayoutConstraints) {
				BoxLayoutConstraints boxConstraints = (BoxLayoutConstraints) constraints;
				contained.setAlignmentX(boxConstraints.getAlignmentX());
				contained.setAlignmentY(boxConstraints.getAlignmentY());
				try {
					container.add(contained);
				} catch (AWTError e) {
					System.out.println("prout alors");
					System.out.println("container.lm=" + container.getLayout());
					System.out.println("container.lm.target=" + ((BoxLayout) container.getLayout()).getTarget());
					e.printStackTrace();
					System.out.println("hop");
				}
			}
			else if (constraints instanceof ButtonLayoutConstraints) {
				ButtonLayoutConstraints buttonConstraints = (ButtonLayoutConstraints) constraints;
				container.add(contained);
			}
			else if (constraints instanceof FlowLayoutConstraints) {
				FlowLayoutConstraints flowConstraints = (FlowLayoutConstraints) constraints;
				container.add(contained);
			}
			else if (constraints instanceof GridBagLayoutConstraints) {
				GridBagLayoutConstraints gridBagConstraints = (GridBagLayoutConstraints) constraints;
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = gridBagConstraints.getGridX();
				c.gridy = gridBagConstraints.getGridY();
				c.gridwidth = gridBagConstraints.getGridWidth();
				c.gridheight = gridBagConstraints.getGridHeight();
				c.weightx = gridBagConstraints.getWeightX();
				c.weighty = gridBagConstraints.getWeightY();
				c.anchor = gridBagConstraints.getAnchor().getAnchor();
				c.fill = gridBagConstraints.getFill().getFill();
				c.insets = new Insets(gridBagConstraints.getInsetsTop(), gridBagConstraints.getInsetsLeft(),
						gridBagConstraints.getInsetsBottom(), gridBagConstraints.getInsetsRight());
				c.ipadx = gridBagConstraints.getPadX();
				c.ipady = gridBagConstraints.getPadY();
				container.add(contained, c);
			}
			else if (constraints instanceof GridLayoutConstraints) {
				GridLayoutConstraints gridConstraints = (GridLayoutConstraints) constraints;
				container.add(contained);
			}
			else if (constraints instanceof NoneLayoutConstraints) {
				NoneLayoutConstraints noneConstraints = (NoneLayoutConstraints) constraints;
				contained.setLocation(noneConstraints.getX(), noneConstraints.getY());
				contained.setSize(contained.getPreferredSize());
				container.add(contained);
			}
			else if (constraints instanceof SplitLayoutConstraints) {
				SplitLayoutConstraints splitConstraints = (SplitLayoutConstraints) constraints;
				container.add(contained);
			}
			else if (constraints instanceof TwoColsLayoutConstraints) {
				TwoColsLayoutConstraints twoColsConstraints = (TwoColsLayoutConstraints) constraints;
				GridBagConstraints c = new GridBagConstraints();
				// c.insets = new Insets(3, 3, 3, 3);
				c.insets = new Insets(twoColsConstraints.getInsetsTop(), twoColsConstraints.getInsetsLeft(),
						twoColsConstraints.getInsetsBottom(), twoColsConstraints.getInsetsRight());
				if (twoColsConstraints.getLocation() == TwoColsLayoutLocation.left) {
					c.fill = GridBagConstraints.NONE;
					c.weightx = 0; // 1.0;
					c.gridwidth = 1;
					c.anchor = GridBagConstraints.NORTHEAST;
					if (twoColsConstraints.getExpandVertically()) {
						// c.weighty = 1.0;
						c.fill = GridBagConstraints.VERTICAL;
					}
					else {
						// c.insets = new Insets(5, 2, 0, 2);
					}
				}
				else {
					if (twoColsConstraints.getExpandHorizontally()) {
						c.fill = GridBagConstraints.BOTH;
						c.anchor = GridBagConstraints.CENTER;
						if (twoColsConstraints.getExpandVertically()) {
							c.weighty = 1.0;
						}
					}
					else {
						c.fill = GridBagConstraints.NONE;
						c.anchor = GridBagConstraints.WEST;
					}
					c.weightx = 1.0; // 2.0;
					c.gridwidth = GridBagConstraints.REMAINDER;
				}

				container.add(contained, c);
			}
		}

	}

	public JFIBPanelView(FIBPanel model, FIBController controller) {
		super(model, controller, new SwingPanelRenderingAdapter());

		updateBorder();
	}

	@Override
	public SwingPanelRenderingAdapter getRenderingAdapter() {
		return (SwingPanelRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	public void updateBorder() {
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
				getTechnologyComponent()
						.setBorder(new RoundedBorder(
								StringUtils.isNotEmpty(getComponent().getBorderTitle()) ? getLocalized(getComponent().getBorderTitle())
										: null,
								getComponent().getBorderTop() != null ? getComponent().getBorderTop() : 0,
								getComponent().getBorderLeft() != null ? getComponent().getBorderLeft() : 0,
								getComponent().getBorderBottom() != null ? getComponent().getBorderBottom() : 0,
								getComponent().getBorderRight() != null ? getComponent().getBorderRight() : 0,
								getComponent().getTitleFont(), getComponent().retrieveValidForegroundColor(),
								getComponent().getDarkLevel()));
				break;
			default:
				break;
		}
	}

	@Override
	protected void setPanelLayoutParameters(JPanel technologyComponent) {
		switch (getComponent().getLayout()) {
			case none:
				technologyComponent.setLayout(null);
				break;
			case flow:
				technologyComponent.setLayout(
						new FlowLayout(getComponent().getFlowAlignment().getAlign(), getComponent().getHGap(), getComponent().getVGap()));
				break;
			case border:
				technologyComponent.setLayout(new BorderLayout());
				break;
			case grid:
				// logger.info("rows="+getComponent().getRows()+" cols="+getComponent().getCols());
				technologyComponent.setLayout(new GridLayout(getComponent().getRows(), getComponent().getCols(), getComponent().getHGap(),
						getComponent().getVGap()));
				break;
			case box:
				technologyComponent.setLayout(new BoxLayout(technologyComponent, getComponent().getBoxLayoutAxis().getAxis()));
				break;
			case twocols:
				technologyComponent.setLayout(new GridBagLayout());
				break;
			case gridbag:
				technologyComponent.setLayout(new GridBagLayout());
				break;
			case buttons:
				technologyComponent.setLayout(
						new ButtonLayout(getComponent().getFlowAlignment() != null ? getComponent().getFlowAlignment().getAlign() : -1,
								getComponent().getHGap() != null ? getComponent().getHGap() : 5));
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

		ScrollablePanel panel = new ScrollablePanel();
		panel.setOpaque(false);
		// updateGraphicalProperties();

		setPanelLayoutParameters(panel);

		return panel;
	}

	@Override
	protected JComponent makeEmptyPanel() {
		JPanel returned = new JPanel();
		returned.setOpaque(false);
		return returned;
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	protected void clearContainer() {
		getRenderingAdapter().getJComponent(getTechnologyComponent()).removeAll();
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {
		Vector<FIBComponent> allSubComponents = new Vector<FIBComponent>();
		allSubComponents.addAll(getNotHiddenSubComponents());

		if (getComponent().getLayout() == Layout.flow || getComponent().getLayout() == Layout.box
				|| getComponent().getLayout() == Layout.buttons || getComponent().getLayout() == Layout.twocols
				|| getComponent().getLayout() == Layout.gridbag) {

			/*System.out.println("Apres le retrieve: ");
			for (FIBComponent c : allSubComponents) {
				if (c.getConstraints() != null) {
					if (!c.getConstraints().hasIndex()) {
						System.out.println("> Index: ? "+c);
					}
					else {
						System.out.println("> Index: "+c.getConstraints().getIndex()+" "+c);
					}
				}
			}
			
			System.out.println("*********************************************");*/

		}

		if (getComponent().getLayout() == Layout.grid) {

			for (FIBComponent subComponent : getNotHiddenSubComponents()) {
				FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) getController().viewForComponent(subComponent);
				if (subView == null) {
					subView = (FIBViewImpl<?, C2>) getController().buildView(subComponent);
				}
				// FIBViewImpl subView = getController().buildView(c);
				registerViewForComponent(subView, subComponent);
			}

			for (int i = 0; i < getComponent().getRows(); i++) {
				for (int j = 0; j < getComponent().getCols(); j++) {
					registerComponentWithConstraints(getChildComponent(j, i), null);
				}
			}
		}

		else {
			for (FIBComponent subComponent : allSubComponents) {
				FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) getController().viewForComponent(subComponent);
				if (subView == null) {
					subView = (FIBViewImpl<?, C2>) getController().buildView(subComponent);
				}
				// FIBViewImpl subView = getController().buildView(c);
				registerViewForComponent(subView, subComponent);

				// TODO: please handle issue with getResultingJComponent()
				registerComponentWithConstraints((C2) subView.getResultingJComponent(), subComponent.getConstraints());
			}
		}
	}

	/*@Override
	public JPanel getJComponent() {
		return (JPanel) getTechnologyComponent();
	}*/

	// Special case for GridLayout
	@Override
	protected C2 getChildComponent(int col, int row) {
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			GridLayoutConstraints glc = (GridLayoutConstraints) subComponent.getConstraints();
			if (glc.getX() == col && glc.getY() == row) {
				return (C2) getController().viewForComponent(subComponent).getResultingJComponent();
			}
		}
		// Otherwise, it's an empty cell
		return makeEmptyPanel();

	}

}
