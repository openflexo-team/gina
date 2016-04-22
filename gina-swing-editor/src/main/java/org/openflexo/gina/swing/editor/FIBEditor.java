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

package org.openflexo.gina.swing.editor;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.openflexo.gina.ApplicationFIBLibrary;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorPalettes;
import org.openflexo.gina.swing.editor.controller.FIBEditorPalettesDialog;
import org.openflexo.gina.swing.utils.JFIBInspectorController;
import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.gina.swing.utils.localization.LocalizedEditor;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.FileSystemResourceLocatorImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.FlexoFileChooser;

// TODO: switch to the right editor controller when switching tab
// 		getPalette().setEditorController(editorController);
public class FIBEditor implements FIBGenericEditor {

	static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	// Instanciate a new localizer in directory
	// src/dev/resources/FIBEditorLocalizer
	// linked to parent localizer (which is Openflexo main localizer)
	public static LocalizedDelegate LOCALIZATION = FlexoLocalization.getLocalizedDelegate(
			ResourceLocator.locateResource("FIBEditorLocalized"),
			FlexoLocalization.getLocalizedDelegate(ResourceLocator.locateResource("Localized"), null, false, false), true, true);

	public static Resource COMPONENT_LOCALIZATION_FIB = ResourceLocator.locateResource("Fib/LocalizedPanel.fib");

	private FIBEditorPalettes palette;

	private JFIBInspectorController inspector;

	private FIBEditorController editorController;

	protected LocalizedEditor localizedEditor;
	private ComponentValidationWindow componentValidationWindow;
	private ComponentLocalizationWindow componentLocalizationWindow;

	final FileSystemResourceLocatorImpl resourceLocator;

	static ApplicationFIBLibrary APP_FIB_LIBRARY = ApplicationFIBLibraryImpl.instance();
	private FIBLibrary fibLibrary;

	private MainPanel mainPanel;
	private FIBEditorMenuBar menuBar;

	private EditedFIBComponent editedFIB;

	public FIBEditor(FIBLibrary fibLibrary) {
		super();

		this.fibLibrary = fibLibrary;

		resourceLocator = new FileSystemResourceLocatorImpl();
		resourceLocator.appendToDirectories(JFIBPreferences.getLastDirectory().getAbsolutePath());
		resourceLocator.appendToDirectories(System.getProperty("user.home"));
		ResourceLocator.appendDelegate(resourceLocator);

		mainPanel = new MainPanel();

	}

	private FlexoFileChooser getFileChooser(JFrame frame) {
		FlexoFileChooser fileChooser = new FlexoFileChooser(frame);
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "*.fib *.inspector";
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".fib") || f.getName().endsWith(".inspector");
			}
		});
		fileChooser.setCurrentDirectory(JFIBPreferences.getLastDirectory());
		return fileChooser;
	}

	public FIBEditorMenuBar makeMenuBar(JFrame frame) {
		menuBar = new FIBEditorMenuBar(this, frame);
		return menuBar;
	}

	public JFIBInspectorController makeInspector(JFrame frame) {
		inspector = new JFIBInspectorController(frame, ResourceLocator.locateResource("EditorInspectors"), APP_FIB_LIBRARY, LOCALIZATION);
		return inspector;
	}

	public FIBEditorPalettesDialog makePaletteDialog(JFrame frame) {
		if (palette == null) {
			palette = makePalette();
		}
		return new FIBEditorPalettesDialog(frame, palette);
	}

	public FIBEditorPalettes makePalette() {
		palette = new FIBEditorPalettes();
		return palette;
	}

	public FIBLibrary getFIBLibrary() {
		return fibLibrary;
	}

	@Override
	public JFIBInspectorController getInspector() {
		return inspector;
	}

	public FIBEditorPalettes getPalettes() {
		return palette;
	}

	public FIBEditorMenuBar getMenuBar() {
		return menuBar;
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	private void updateFrameTitle(JFrame frame) {
		frame.setTitle("Flexo Interface Builder Editor");
	}

	public void quit() {

		// TODO: Check if a component needs to be saved.
		/*
		 * for(FIBEditor.EditedFIB c: mainPanel.editedComponents) { }
		 */
		// frame.dispose();
		System.exit(0);
	}

	public void closeFIB(JFrame frame) {
		logger.warning("Not implemented yet");
	}

	public void newFIB(JFrame frame) {

		FIBModelFactory factory = null;

		try {
			factory = new FIBModelFactory();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			return;
		}

		FIBPanel fibComponent = factory.newInstance(FIBPanel.class);
		fibComponent.setLayout(Layout.border);
		fibComponent.finalizeDeserialization();
		EditedFIBComponent newEditedFIB = new EditedFIBComponent("New.fib", fibComponent, getFIBLibrary());

		editorController = new FIBEditorController(factory, fibComponent, this, frame);
		getPalettes().setEditorController(editorController);

		mainPanel.newEditedComponent(newEditedFIB, editorController);

	}

	public void loadFIB(JFrame frame) {
		if (getFileChooser(frame).showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File fibFile = getFileChooser(frame).getSelectedFile();
			loadFIB(fibFile, frame);
		}
	}

	public void loadFIB(File fibFile, JFrame frame) {

		if (!fibFile.exists()) {
			JOptionPane.showMessageDialog(frame, "File " + fibFile.getAbsolutePath() + " does not exist anymore");
			return;
		}
		JFIBPreferences.setLastFile(fibFile);

		FileResourceImpl fibResource = null;
		try {
			fibResource = new FileResourceImpl(resourceLocator, fibFile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		EditedFIBComponent newEditedFIB = new EditedFIBComponent(fibResource, getFIBLibrary());
		editorController = new FIBEditorController(newEditedFIB.getFactory(), newEditedFIB.getFIBComponent(), this, frame);
		getPalettes().setEditorController(editorController);
		mainPanel.newEditedComponent(newEditedFIB, editorController);
	}

	public void saveFIB(JFrame frame) {
		if (editedFIB == null) {
			return;
		}
		if (editedFIB.getSourceFile() != null) {
			editedFIB.save();
		}
		else {
			saveFIBAs(frame);
		}
	}

	public void saveFIBAs(JFrame frame) {
		if (editedFIB == null) {
			return;
		}
		if (getFileChooser(frame).showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = getFileChooser(frame).getSelectedFile();
			if (!file.getName().endsWith(".fib")) {
				file = new File(file.getParentFile(), file.getName() + ".fib");
			}
			JFIBPreferences.setLastFile(file);
			try {
				editedFIB.saveAs(new FileResourceImpl(resourceLocator, file));
				mainPanel.setTitleAt(mainPanel.getSelectedIndex(), editedFIB.getName());
				updateFrameTitle(frame);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LocatorNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void testFIB(JFrame frame) {
		JFIBView<?, ? extends JComponent> view = (JFIBView<?, ? extends JComponent>) FIBController.makeView(editedFIB.getFIBComponent(),
				SwingViewFactory.INSTANCE, LOCALIZATION);

		// Class testClass = null;
		if (editedFIB.getFIBComponent() instanceof FIBContainer && ((FIBContainer) editedFIB.getFIBComponent()).getDataClass() != null) {
			try {
				// testClass =
				// Class.forName(editedFIB.fibComponent.getDataClassName());
				FIBContainer container = (FIBContainer) editedFIB.getFIBComponent();
				Object testData = container.getDataClass().newInstance();
				view.getController().setDataObject(testData);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			view.getController().updateWithoutDataObject();
		}

		JDialog testInterface = new JDialog(frame, "Test", false);
		testInterface.getContentPane().add(view.getResultingJComponent());
		testInterface.pack();
		testInterface.setVisible(true);
	}

	public void localizeFIB(JFrame frame) {

		if (editorController == null) {
			return;
		}

		if (editedFIB != null && editedFIB.getFIBComponent() != null) {
			editedFIB.getFIBComponent().searchAndRegisterAllLocalized();
			getLocalizationWindow(editedFIB.getFIBComponent(), frame).setVisible(true);
		}

	}

	public void validateFIB(JFrame frame) {
		if (editedFIB != null && editedFIB.getFIBComponent() != null) {
			try {
				getValidationWindow(editedFIB.getFIBComponent(), frame).validateAndDisplayReportForComponent(editedFIB.getFIBComponent());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected ComponentValidationWindow getValidationWindow(FIBComponent component, JFrame frame) {
		if (componentValidationWindow != null && componentValidationWindow.getFIBComponent() != component) {
			componentValidationWindow.dispose();
			componentValidationWindow = null;
		}
		if (componentValidationWindow == null) {
			componentValidationWindow = new ComponentValidationWindow(frame, editorController, APP_FIB_LIBRARY);
		}
		return componentValidationWindow;
	}

	protected ComponentLocalizationWindow getLocalizationWindow(FIBComponent component, JFrame frame) {
		if (componentLocalizationWindow != null && componentLocalizationWindow.getFIBComponent() != component) {
			componentLocalizationWindow.dispose();
			componentLocalizationWindow = null;
		}
		if (componentLocalizationWindow == null) {
			componentLocalizationWindow = new ComponentLocalizationWindow(frame, editorController, APP_FIB_LIBRARY);
		}
		return componentLocalizationWindow;
	}

	public void switchToLanguage(Language lang) {
		FlexoLocalization.setCurrentLanguage(lang);
		if (editorController != null) {
			editorController.switchToLanguage(lang);
		}
	}

	public EditedFIBComponent getEditedFIB() {
		return editedFIB;
	}

	@Override
	public File getEditedComponentFile() {
		return editedFIB.getSourceFile();
	}

	public class MainPanel extends JTabbedPane implements ChangeListener {
		private final Vector<EditedFIBComponent> editedComponents;
		private final Hashtable<EditedFIBComponent, FIBEditorController> controllers;

		public MainPanel() {
			super();
			editedComponents = new Vector<EditedFIBComponent>();
			controllers = new Hashtable<EditedFIBComponent, FIBEditorController>();
			addChangeListener(this);
		}

		public void newEditedComponent(EditedFIBComponent edited, FIBEditorController controller) {
			editedComponents.add(edited);
			controllers.put(edited, controller);
			add(controller.getEditorPanel(), edited.getName());
			revalidate();
			setSelectedIndex(getComponentCount() - 1);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			logger.info("Change for " + e);
			if (editedFIB != null) {
				disactivate(editedFIB, controllers.get(editedFIB));
			}
			int index = getSelectedIndex();
			editedFIB = editedComponents.get(index);
			activate(editedFIB, controllers.get(editedFIB));
		}
	}

	public void activate(EditedFIBComponent editedFIB, FIBEditorController controller) {
	}

	public void disactivate(EditedFIBComponent editedFIB, FIBEditorController controller) {
	}

}
