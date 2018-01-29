/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.gina.swing.utils;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.gina.FIBInspector;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.swing.view.container.JFIBTabPanelView;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;
import org.openflexo.swing.ComponentBoundSaver;

/**
 * Implementation of a inspector controller packed in a {@link JDialog}
 * 
 * @author sylvain
 *
 */
public class JFIBDialogInspectorController extends JFIBInspectorController implements ChangeListener {

	static final Logger logger = Logger.getLogger(JFIBDialogInspectorController.class.getPackage().getName());

	private final JDialog inspectorDialog;

	public JFIBDialogInspectorController(JFrame frame, Resource inspectorDirectory, FIBLibrary fibLibrary, LocalizedDelegate localizer) {
		this(frame, inspectorDirectory, fibLibrary, localizer, null);
	}

	public JFIBDialogInspectorController(JFrame frame, Resource inspectorDirectory, FIBLibrary fibLibrary, LocalizedDelegate localizer,
			final FIBEditorLoadingProgress progress) {
		super(inspectorDirectory, fibLibrary, localizer, progress);
		inspectorDialog = new JDialog(frame, "Inspector", false);
		inspectorDialog.setBounds(JFIBPreferences.getInspectorBounds());
		new ComponentBoundSaver(inspectorDialog) {

			@Override
			public void saveBounds(Rectangle bounds) {
				JFIBPreferences.setInspectorBounds(bounds);
			}
		};
		inspectorDialog.getContentPane().setLayout(new BorderLayout());
		inspectorDialog.getContentPane().add(getRootPane(), BorderLayout.CENTER);

		switchToEmptyContent();
		inspectorDialog.setResizable(true);

		inspectorDialog.revalidate();
		inspectorDialog.setVisible(true);
	}

	@Override
	protected boolean switchToInspector(FIBInspector newInspector) {

		boolean returned = super.switchToInspector(newInspector);

		if (returned) {

			inspectorDialog.setTitle(newInspector.getParameter("title"));

			JTabbedPane tabPanelViewJComponent = null;

			if (tabPanelView != null) {
				tabPanelViewJComponent = tabPanelView.getJComponent();
				tabPanelViewJComponent.removeChangeListener(this);
				// System.out.println("removeChangeListener for "+tabPanelView.getJComponent());
			}

			tabPanelView = (JFIBTabPanelView) currentInspectorView.getController().viewForComponent(currentInspector.getTabPanel());
			tabPanelViewJComponent = tabPanelView.getJComponent();
			if (lastInspectedTabIndex >= 0 && lastInspectedTabIndex < tabPanelViewJComponent.getTabCount()) {
				tabPanelViewJComponent.setSelectedIndex(lastInspectedTabIndex);
			}
			tabPanelViewJComponent.addChangeListener(this);
		}

		return returned;
	}

	private int lastInspectedTabIndex = -1;
	private JFIBTabPanelView tabPanelView;

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabPanelViewJComponent = tabPanelView.getJComponent();
		lastInspectedTabIndex = tabPanelViewJComponent.getSelectedIndex();
		// System.out.println("Change for index "+lastInspectedTabIndex);
	}

	public void setVisible(boolean flag) {
		inspectorDialog.setVisible(flag);
	}
}
