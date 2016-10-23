/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexographicutils, a component of the software infrastructure 
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

package org.openflexo.swing;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class FlexoCollabsiblePanelGroup extends JScrollPane {

	private static final long serialVersionUID = 1L;
	private final List<FlexoCollabsiblePanel> panels;
	private FlexoCollabsiblePanel openedPanel;
	private final JPanel mainPane;

	/**
	 * Specific implementation of a Scrollable scrolling only vertically.<br>
	 * Width automatically adapts itself to available space
	 * 
	 * @author sylvain
	 *
	 */
	public class JScrollablePanel extends JPanel implements Scrollable {

		public JScrollablePanel(LayoutManager lm) {
			super(lm);
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 10;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			return (orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			// We always return true here, as we want this panel always take the available
			// width space
			/*if (getParent() instanceof JViewport) {
				return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
			}
			return false;*/
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			if (getParent() instanceof JViewport) {
				return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
			}
			return false;
		}
	}

	public FlexoCollabsiblePanelGroup() {
		super();
		mainPane = new JScrollablePanel(new VerticalLayout());
		panels = new ArrayList<FlexoCollabsiblePanel>();
		openedPanel = null;
		setViewportView(mainPane);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(BorderFactory.createEmptyBorder());
	}

	public void addContents(final String title, JComponent contents) {
		FlexoCollabsiblePanel collabsiblePanel = new FlexoCollabsiblePanel(title, contents) {
			@Override
			public void setCollapsed(boolean val) {
				if (!val) {
					setOpenedPanel(this);
				}
				super.setCollapsed(val);
			}
		};
		addContents(collabsiblePanel);
	}

	public void addContents(FlexoCollabsiblePanel collabsiblePanel) {
		panels.add(collabsiblePanel);
		mainPane.add(collabsiblePanel);
		setOpenedPanel(collabsiblePanel);
	}

	public void setOpenedPanel(FlexoCollabsiblePanel p) {
		if (openedPanel != p) {
			openedPanel = p;
			for (FlexoCollabsiblePanel p2 : panels) {
				if (p2 != p) {
					p2.setCollapsed(true);
				}
			}
			p.setCollapsed(false);
		}
	}

	public void setOpenedPanel(int index) {
		if (index >= 0 && index < panels.size()) {
			setOpenedPanel(panels.get(index));
		}
	}
}
