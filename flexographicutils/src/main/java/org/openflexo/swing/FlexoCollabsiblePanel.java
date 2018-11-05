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
import org.openflexo.icon.ImageIconResource;
import org.openflexo.rm.ResourceLocator;

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
