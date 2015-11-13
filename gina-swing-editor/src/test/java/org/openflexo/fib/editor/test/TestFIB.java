/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.fib.editor.test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.editor.FIBAbstractEditor;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * Dialog allowing to automatically ask and edit parameters
 * 
 * @author sguerin
 * 
 */
public class TestFIB extends JPanel {

	static final Logger logger = Logger.getLogger(TestFIB.class.getPackage().getName());

	/*
	 * private TabModelView paramsPanel; private InspectableObject _inspected;
	 * 
	 * public FIBPanel(TabModel model, InspectableObject inspected) { super();
	 * _inspected = inspected; setLayout(new BorderLayout()); paramsPanel = new
	 * TabModelView(model,null,FIBController.instance());
	 * paramsPanel.performObserverSwitch(inspected);
	 * paramsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	 * paramsPanel.valueChange(inspected); add(paramsPanel,BorderLayout.CENTER);
	 * SwingUtilities.invokeLater(new Runnable() { public void run() {
	 * paramsPanel.requestFocusInFirstWidget(); } }); }
	 * 
	 * public DenaliWidget getInspectorWidgetForParameter (ParameterDefinition
	 * parameterDefinition) { return
	 * paramsPanel.getInspectorWidgetFor(parameterDefinition.getName()); }
	 * 
	 * public void update() { paramsPanel.valueChange(_inspected);
	 * paramsPanel.performObserverSwitch(_inspected); }
	 * 
	 * public void setBackground(Color aColor) { super.setBackground(aColor); if
	 * (paramsPanel != null) paramsPanel.setBackground(aColor); }
	 */

	public static void main(String[] args) {
		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JFrame frame = new JFrame();

		final ResourceLocator rl = ResourceLocator.getResourceLocator();

		Resource fibResource = ResourceLocator.locateSourceCodeResource("TestFIB/Test.fib");
		System.out.println("Fib: " + fibResource);

		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibResource);

		Coucou coucou = new Coucou();

		JFIBView<?, ?> testFibPanel = (JFIBView<?, ?>) FIBController.makeView(fibComponent, SwingViewFactory.INSTANCE,
				FIBAbstractEditor.LOCALIZATION);
		testFibPanel.getController().setDataObject(coucou);

		frame.getContentPane().add(testFibPanel.getResultingJComponent());
		frame.setVisible(true);

		/*
		 * TabModel tabModel = null;
		 * 
		 * try { tabModel =
		 * FIBController.instance().importInspectorFile(fibFile); } catch
		 * (FileNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * FIBPanel testFib = new FIBPanel(tabModel, new Coucou());
		 * 
		 * frame.getContentPane().add(testFib); frame.setVisible(true);
		 */
	}

}
