/*
 * (c) Copyright 2014 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class FlexoCollabsiblePanelGroup extends JPanel {

	private final List<FlexoCollabsiblePanel> panels;
	private FlexoCollabsiblePanel openedPanel;

	public FlexoCollabsiblePanelGroup() {
		super(new VerticalLayout());
		panels = new ArrayList<FlexoCollabsiblePanel>();
		openedPanel = null;
	}

	public void addContents(final String title, JComponent contents) {
		FlexoCollabsiblePanel newPanel = new FlexoCollabsiblePanel(title, contents) {
			@Override
			public void setCollapsed(boolean val) {
				if (!val) {
					setOpenedPanel(this);
				}
				super.setCollapsed(val);
			}
		};
		panels.add(newPanel);
		add(newPanel);
		setOpenedPanel(newPanel);
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
}
