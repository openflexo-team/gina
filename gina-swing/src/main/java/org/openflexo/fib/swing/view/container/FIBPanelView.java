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

package org.openflexo.fib.swing.view.container;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.ComponentConstraints;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.GridLayoutConstraints;
import org.openflexo.fib.view.FIBView;
import org.openflexo.swing.ButtonLayout;
import org.openflexo.toolbox.StringUtils;

public class FIBPanelView<C extends FIBPanel, T> extends FIBContainerView<C, JPanel, T> {

	private static final Logger logger = Logger.getLogger(FIBPanelView.class.getPackage().getName());

	private JPanel panel;

	public FIBPanelView(C model, FIBController controller) {
		super(model, controller);

		updateBorder();
	}

	public void updateBorder() {
		switch (getComponent().getBorder()) {
			case empty:
				panel.setBorder(BorderFactory.createEmptyBorder(getComponent().getBorderTop() != null ? getComponent().getBorderTop() : 0,
						getComponent().getBorderLeft() != null ? getComponent().getBorderLeft() : 0,
						getComponent().getBorderBottom() != null ? getComponent().getBorderBottom() : 0,
						getComponent().getBorderRight() != null ? getComponent().getBorderRight() : 0));
				break;
			case etched:
				panel.setBorder(BorderFactory.createEtchedBorder());
				break;
			case line:
				panel.setBorder(BorderFactory
						.createLineBorder(getComponent().getBorderColor() != null ? getComponent().getBorderColor() : Color.black));
				break;
			case lowered:
				panel.setBorder(BorderFactory.createLoweredBevelBorder());
				break;
			case raised:
				panel.setBorder(BorderFactory.createRaisedBevelBorder());
				break;
			case titled:
				panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
						getLocalized(getComponent().getBorderTitle()), TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION,
						getComponent().retrieveValidFont(), getComponent().retrieveValidForegroundColor()));
				break;
			case rounded3d:
				panel.setBorder(
						new RoundedBorder(
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
	public void updateLanguage() {
		super.updateLanguage();
		updateBorder();
	}

	private void _setPanelLayoutParameters() {
		switch (getComponent().getLayout()) {
			case none:
				panel.setLayout(null);
				break;
			case flow:
				panel.setLayout(
						new FlowLayout(getComponent().getFlowAlignment().getAlign(), getComponent().getHGap(), getComponent().getVGap()));
				break;
			case border:
				panel.setLayout(new BorderLayout());
				break;
			case grid:
				// logger.info("rows="+getComponent().getRows()+" cols="+getComponent().getCols());
				panel.setLayout(new GridLayout(getComponent().getRows(), getComponent().getCols(), getComponent().getHGap(),
						getComponent().getVGap()));
				break;
			case box:
				panel.setLayout(new BoxLayout(panel, getComponent().getBoxLayoutAxis().getAxis()));
				break;
			case twocols:
				panel.setLayout(new GridBagLayout());
				break;
			case gridbag:
				panel.setLayout(new GridBagLayout());
				break;
			case buttons:
				panel.setLayout(
						new ButtonLayout(getComponent().getFlowAlignment() != null ? getComponent().getFlowAlignment().getAlign() : -1,
								getComponent().getHGap() != null ? getComponent().getHGap() : 5));
				break;
			default:
				break;
		}
	}

	@Override
	protected JPanel createJComponent() {
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
					if (parent instanceof JViewport && getPreferredSize() != null && FIBPanelView.this.getComponent() != null) {

						return parent.getWidth() > getPreferredSize().width && FIBPanelView.this.getComponent().isTrackViewPortWidth();
					}
					if (FIBPanelView.this.getComponent() != null) {
						return FIBPanelView.this.getComponent().isTrackViewPortWidth();
					}
				} catch (NullPointerException e) {
					// TODO: remove debug
					System.out.println("parent=" + parent);
					System.out.println("getPreferredSize()=" + getPreferredSize());
					System.out.println("FIBPanelView.this.getComponent()=" + FIBPanelView.this.getComponent());
				}
				return false;
			}

			@Override
			public boolean getScrollableTracksViewportHeight() {
				Container parent = getParent();
				try {
					if (parent instanceof JViewport && getPreferredSize() != null && FIBPanelView.this.getComponent() != null) {
						return parent.getHeight() > getPreferredSize().height && FIBPanelView.this.getComponent().isTrackViewPortHeight();
					}
					if (FIBPanelView.this.getComponent() != null) {
						return FIBPanelView.this.getComponent().isTrackViewPortHeight();
					}
				} catch (NullPointerException e) {
					// TODO: remove debug
					System.out.println("parent=" + parent);
					System.out.println("getPreferredSize()=" + getPreferredSize());
					System.out.println("FIBPanelView.this.getComponent()=" + FIBPanelView.this.getComponent());
				}
				return false;
			}

		}

		panel = new ScrollablePanel();
		panel.setOpaque(false);
		updateGraphicalProperties();

		_setPanelLayoutParameters();

		return panel;
	}

	@Override
	public synchronized void updateLayout() {
		logger.info("relayout panel " + getComponent());

		/*if (getSubViews() != null) {
			for (FIBViewImpl v : getSubViews().values()) {
				if (v.getComponent().isDeleted()) {
					v.delete();
				}
			}
		}*/
		getJComponent().removeAll();

		_setPanelLayoutParameters();
		buildSubComponents();
		// updateDataObject(getDataObject());
		update();
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {
		Vector<FIBComponent> allSubComponents = new Vector<FIBComponent>();
		allSubComponents.addAll(getNotHiddenSubComponents());
		// Vector<FIBComponent> allSubComponents = getComponent().getSubComponents();

		// if (fibComponent.getParameter("hidden") == null
		// || fibComponent.getParameter("hidden").equalsIgnoreCase("false")) {

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
				FIBView subView = getController().viewForComponent(subComponent);
				if (subView == null) {
					subView = getController().buildView(subComponent);
				}
				// FIBViewImpl subView = getController().buildView(c);
				registerViewForComponent(subView, subComponent);
			}

			for (int i = 0; i < getComponent().getRows(); i++) {
				for (int j = 0; j < getComponent().getCols(); j++) {
					registerComponentWithConstraints(_getJComponent(j, i), null);
				}
			}
		}

		else {
			for (FIBComponent subComponent : allSubComponents) {
				FIBView subView = getController().viewForComponent(subComponent);
				if (subView == null) {
					subView = getController().buildView(subComponent);
				}
				// FIBViewImpl subView = getController().buildView(c);
				registerViewForComponent(subView, subComponent);
				registerComponentWithConstraints(subView.getResultingJComponent(), subComponent.getConstraints());
			}
		}
	}

	@Override
	protected void addJComponent(JComponent c) {
		Object constraint = getConstraints().get(c);
		if (constraint instanceof ComponentConstraints) {
			((ComponentConstraints) constraint).performConstrainedAddition(getJComponent(), c);
		}
		else {
			super.addJComponent(c);
		}
	}

	@Override
	public JPanel getJComponent() {
		return panel;
	}

	// Special case for GridLayout
	protected JComponent _getJComponent(int col, int row) {
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			GridLayoutConstraints glc = (GridLayoutConstraints) subComponent.getConstraints();
			if (glc.getX() == col && glc.getY() == row) {
				return getController().viewForComponent(subComponent).getResultingJComponent();
			}
		}
		// Otherwise, it's an empty cell
		JPanel returned = new JPanel();
		returned.setOpaque(false);
		return returned;

	}

	@Override
	public void delete() {
		super.delete();
	}

}