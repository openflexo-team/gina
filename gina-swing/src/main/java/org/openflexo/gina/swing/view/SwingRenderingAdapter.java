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

package org.openflexo.gina.swing.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.FIBView.RenderingAdapter;

/**
 * A {@link RenderingAdapter} implementation dedicated for Swing
 * 
 * @author sylvain
 * 
 */
public abstract class SwingRenderingAdapter<J extends JComponent> implements RenderingAdapter<J> {

	// private JScrollPane scrolledComponent;

	/*
	 * private final JFIBView<?, J> view;
	 * 
	 * public SwingRenderingAdapter(JFIBView<?, J> view) { this.view = view; }
	 */

	/**
	 * Return the effective component to be added to swing hierarchy<br>
	 * This component may be the same as the one returned by {@link #getJComponent()} (if useScrollBar set to false) or an encapsulation in
	 * a JScrollPane
	 * 
	 * @return JComponent
	 */
	public JComponent getResultingJComponent(FIBView<?, J> view) {
		if (view.getComponent().getUseScrollBar()) {
			// System.out.println("parent=" +
			// view.getTechnologyComponent().getParent());
			if (view.getTechnologyComponent().getParent() instanceof JViewport) {
				// System.out.println("on retourne la scrollpane deja faite pour "
				// + view);
				return (JScrollPane) ((JViewport) view.getTechnologyComponent().getParent()).getParent();
			}
			else {
				// System.out.println("on se refait une scrollpane pour " +
				// view);
				JScrollPane scrolledComponent = new JScrollPane(getJComponent(view.getTechnologyComponent()),
						view.getComponent().getVerticalScrollbarPolicy().getPolicy(),
						view.getComponent().getHorizontalScrollbarPolicy().getPolicy());
				scrolledComponent.setOpaque(false);
				scrolledComponent.getViewport().setOpaque(false);
				scrolledComponent.setBorder(BorderFactory.createEmptyBorder());
				scrolledComponent.revalidate();
				return scrolledComponent;
			}
		}
		else {
			return getJComponent(view.getTechnologyComponent());
		}
	}

	/**
	 * Return the Technology Component defined for this view. In Swing this is always a JComponent
	 * 
	 * @param technologyComponent
	 * @return
	 */
	public final JComponent getJComponent(J technologyComponent) {
		return technologyComponent;
	}

	/**
	 * Return the dynamic Technology Component defined for this view. In Swing this is always a JComponent<br>
	 * The dynamic JComponent is the component on which controls applies (sometimes, for some widgets, the dynamic component is embedded in
	 * a panel or something else, this panel is accessible through {@link #getJComponent(JComponent)})
	 * 
	 * Default behaviour is to return the technology component itself
	 * 
	 * @param technologyComponent
	 * @return
	 */
	public JComponent getDynamicJComponent(J technologyComponent) {
		return technologyComponent;
	}

	@Override
	public boolean isVisible(J component) {
		return getJComponent(component).isVisible();
	}

	@Override
	public void setVisible(J component, boolean visible) {
		JComponent jComponent = getJComponent(component);
		if (jComponent.getParent() instanceof JViewport) {
			JScrollPane jScrollPane = (JScrollPane) jComponent.getParent().getParent();
			jScrollPane.setVisible(visible);
			if (jScrollPane.getParent() != null) {
				jScrollPane.getParent().revalidate();
				jScrollPane.getParent().repaint();
			}
		}
		else {
			jComponent.setVisible(visible);
			if (jComponent.getParent() != null) {
				jComponent.getParent().revalidate();
				jComponent.getParent().repaint();
			}
		}
		// getResultingJComponent(component).setVisible(visible);

	}

	@Override
	public boolean getEnable(J component) {
		return getJComponent(component).isEnabled();
		/*
		 * if (scrolledComponent != null) { return
		 * scrolledComponent.isEnabled(); } return
		 * getJComponent(component).isEnabled();
		 */
	}

	@Override
	public void setEnabled(J component, boolean enabled) {
		/*
		 * if (scrolledComponent != null) { if (enabled) {
		 * enableComponent(scrolledComponent); } else {
		 * disableComponent(scrolledComponent); } }
		 */
		if (enabled) {
			enableComponent(getJComponent(component));
		}
		else {
			disableComponent(getJComponent(component));
		}
	}

	private void enableComponent(Component component) {
		if (component instanceof JScrollPane) {
			component = ((JScrollPane) component).getViewport().getView();
			if (component == null) {
				return;
			}
		}
		component.setEnabled(true);
		// System.out.println("Enable component " + component);
		if (component instanceof Container) {
			for (Component c : ((Container) component).getComponents()) {
				enableComponent(c);
			}
		}
	}

	private void disableComponent(Component component) {
		if (component instanceof JScrollPane) {
			component = ((JScrollPane) component).getViewport().getView();
			if (component == null) {
				return;
			}
		}
		if (component != null) {
			component.setEnabled(false);
			// System.out.println("Disable component " + component);
		}
		if (component instanceof Container) {
			for (Component c : ((Container) component).getComponents()) {
				disableComponent(c);
			}
		}
	}

	@Override
	public int getWidth(J component) {
		return component.getWidth();
	}

	@Override
	public int getHeight(J component) {
		return component.getHeight();
	}

	@Override
	public Dimension getPreferredSize(J component) {
		return component.getPreferredSize();
	}

	@Override
	public void setPreferredSize(J component, Dimension size) {
		component.setPreferredSize(size);
	}

	@Override
	public Dimension getMinimumSize(J component) {
		return component.getMinimumSize();
	}

	@Override
	public void setMinimumSize(J component, Dimension size) {
		component.setMinimumSize(size);
	}

	@Override
	public Dimension getMaximumSize(J component) {
		return component.getMaximumSize();
	}

	@Override
	public void setMaximumSize(J component, Dimension size) {
		component.setMaximumSize(size);
	}

	@Override
	public Color getForegroundColor(J component) {
		return component.getForeground();
	}

	@Override
	public void setForegroundColor(J component, Color aColor) {
		component.setForeground(aColor);
	}

	@Override
	public Color getBackgroundColor(J component) {
		return component.getBackground();
	}

	@Override
	public void setBackgroundColor(J component, Color aColor) {
		component.setBackground(aColor);
	}

	@Override
	public Font getFont(J component) {
		return component.getFont();
	}

	@Override
	public void setFont(J component, Font aFont) {
		component.setFont(aFont);
	}

	@Override
	public String getToolTipText(J component) {
		return component.getToolTipText();
	}

	@Override
	public void setToolTipText(J component, String aText) {
		component.setToolTipText(aText);
	}

	@Override
	public boolean isOpaque(J component) {
		return component.isOpaque();
	}

	@Override
	public void setOpaque(J component, boolean opaque) {
		component.setOpaque(opaque);
	}

	@Override
	public void requestFocus(J component) {
		component.requestFocus();
	}

	@Override
	public void requestFocusInWindow(J component) {
		component.requestFocusInWindow();
	}

	@Override
	public void requestFocusInParent(J component) {
		component.getParent().requestFocus();
	}

	@Override
	public void repaint(J component) {
		component.repaint();
	}

	@Override
	public void revalidateAndRepaint(J component) {
		component.revalidate();
		component.repaint();
	}

	@Override
	public boolean newFocusedComponentIsDescendingFrom(J component, FocusEvent event) {
		// TODO Auto-generated method stub
		return event.getOppositeComponent() != null
				&& SwingUtilities.isDescendingFrom(event.getOppositeComponent(), getJComponent(component));
	}
}
