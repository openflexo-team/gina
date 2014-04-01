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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.ImageIconResource;

public class FlexoCollabsiblePanel extends JPanel {

	public static final ImageIconResource ARROW_RIGHT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/ArrowRight.gif"));
	public static final ImageIconResource ARROW_DOWN_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/ArrowDown.gif"));

	private final FlexoCollabsiblePanelHeader header;
	private final JXCollapsiblePane collabsiblePane;
	private final String title;
	private final JComponent contents;

	public FlexoCollabsiblePanel(String title, JComponent contents) {
		super(new BorderLayout());
		this.title = title;
		this.contents = contents;
		header = new FlexoCollabsiblePanelHeader(title);
		add(header, BorderLayout.NORTH);
		collabsiblePane = new JXCollapsiblePane(Direction.DOWN);
		collabsiblePane.add(contents);
		add(collabsiblePane, BorderLayout.CENTER);
		collabsiblePane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl F"),
				JXCollapsiblePane.TOGGLE_ACTION);
	}

	public void toggleState() {
		setCollapsed(!isCollapsed());
	}

	public void setCollapsed(boolean val) {
		if (isCollapsed() == val) {
			return;
		}
		header.setCollapsed(val);
		collabsiblePane.setCollapsed(val);
	}

	public boolean isCollapsed() {
		return collabsiblePane.isCollapsed();
	}

	public String getTitle() {
		return title;
	}

	public JComponent getContents() {
		return contents;
	}

	public class FlexoCollabsiblePanelHeader extends JPanel {

		private final JButton button;
		private final JLabel label;

		public FlexoCollabsiblePanelHeader(String title) {
			super(new BorderLayout());
			button = new JButton(ARROW_DOWN_ICON);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					toggleState();
				}
			});
			button.setBorder(null);
			label = new JLabel("<html><font color=\"#000099\"<u>" + title + "</u></font></html>");
			// label.setText("<HTML>Click the <FONT color=\"#000099\"><U>link</U></FONT>" + " to go to the Java website.</HTML>");
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					toggleState();
				}
			});
			add(button, BorderLayout.WEST);
			add(label, BorderLayout.CENTER);
		}

		public void setCollapsed(boolean val) {
			button.setIcon(val ? ARROW_RIGHT_ICON : ARROW_DOWN_ICON);
			button.setBorder(null);
		}

	}
}
