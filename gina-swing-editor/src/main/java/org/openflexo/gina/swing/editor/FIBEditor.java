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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
import org.openflexo.gina.swing.editor.inspector.FIBInspectors;
import org.openflexo.gina.swing.editor.palette.FIBEditorPalettes;
import org.openflexo.gina.swing.editor.palette.FIBEditorPalettesDialog;
import org.openflexo.gina.swing.utils.FIBEditorLoadingProgress;
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

/**
 * This class provides a generic framework for managing {@link FIBComponent} edition<br>
 * 
 * 
 * @author sylvain
 *
 */
public class FIBEditor {

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
	private FIBInspectors inspectors;

	protected LocalizedEditor localizedEditor;
	private ComponentValidationWindow componentValidationWindow;
	private ComponentLocalizationWindow componentLocalizationWindow;

	final FileSystemResourceLocatorImpl resourceLocator;

	static ApplicationFIBLibrary APP_FIB_LIBRARY = ApplicationFIBLibraryImpl.instance();
	private final FIBLibrary fibLibrary;

	private MainPanel mainPanel;
	private FIBEditorMenuBar menuBar;

	// This map stores all editors (FIBEditorController - editor of an EditedFIBComponent) managed by this FIBEditor
	private final Map<EditedFIBComponent, FIBEditorController> controllers = new HashMap<EditedFIBComponent, FIBEditorController>();

	private FIBEditorController activeEditorController = null;

	private FIBEditorLoadingProgress progress;

	public FIBEditor(FIBLibrary fibLibrary) {
		this(fibLibrary, null);
	}

	public FIBEditor(FIBLibrary fibLibrary, FIBEditorLoadingProgress progress) {
		super();

		this.fibLibrary = fibLibrary;
		this.progress = progress;

		resourceLocator = new FileSystemResourceLocatorImpl();
		resourceLocator.appendToDirectories(JFIBPreferences.getLastDirectory().getAbsolutePath());
		resourceLocator.appendToDirectories(System.getProperty("user.home"));
		ResourceLocator.appendDelegate(resourceLocator);

	}

	/**
	 * Return a collection storing all components beeing edited in this FIBEditor
	 * 
	 * @return a collection of {@link EditedFIBComponent}
	 */
	public Collection<EditedFIBComponent> getEditedFIBComponents() {
		return controllers.keySet();
	}

	/**
	 * Return {@link FIBEditorController} managing edition of {@link EditedFIBComponent}, asserting that supplied edited component is edited
	 * in this {@link FIBEditor}
	 * 
	 * @param editedComponent
	 * @return
	 */
	public FIBEditorController getControllerForEditedFIBComponent(EditedFIBComponent editedComponent) {
		return controllers.get(editedComponent);
	}

	/**
	 * Return active {@link FIBEditorController}
	 * 
	 * @return
	 */
	public FIBEditorController getActiveEditorController() {
		return activeEditorController;
	}

	/**
	 * Return active {@link EditedFIBComponent}
	 * 
	 * @return
	 */
	public EditedFIBComponent getActiveEditedComponent() {
		return activeEditorController.getEditedComponent();
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
		if (progress != null) {
			progress.progress(FlexoLocalization.localizedForKey("make_menu_bar"));
		}
		menuBar = new FIBEditorMenuBar(this, frame);
		return menuBar;
	}

	public JFIBInspectorController makeInspector(JFrame frame) {
		inspector = new JFIBInspectorController(frame, ResourceLocator.locateResource("EditorInspectors"), APP_FIB_LIBRARY, LOCALIZATION,
				progress);
		return inspector;
	}

	public FIBEditorPalettesDialog makePaletteDialog(JFrame frame) {
		if (palette == null) {
			if (progress != null) {
				progress.progress(FlexoLocalization.localizedForKey("make_palette_dialog"));
			}
			palette = makePalette();
		}
		return new FIBEditorPalettesDialog(frame, palette);
	}

	public FIBEditorPalettes makePalette() {
		if (progress != null) {
			progress.progress(FlexoLocalization.localizedForKey("make_palette"));
		}
		palette = new FIBEditorPalettes();
		return palette;
	}

	public FIBInspectors makeInspectors() {
		if (progress != null) {
			progress.progress(FlexoLocalization.localizedForKey("make_inspectors"));
		}
		inspectors = new FIBInspectors();
		return inspectors;
	}

	public MainPanel makeMainPanel() {
		if (progress != null) {
			progress.progress(FlexoLocalization.localizedForKey("make_main_panel"));
		}
		mainPanel = new MainPanel(this);
		return mainPanel;
	}

	public FIBLibrary getFIBLibrary() {
		return fibLibrary;
	}

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

	/**
	 * Internally used to create and register a new {@link FIBEditorController} managing the edition of a {@link EditedFIBComponent}
	 * 
	 * @param newEditedFIB
	 * @param frame
	 * @return
	 */
	private FIBEditorController newEditedComponent(EditedFIBComponent newEditedFIB, JFrame frame) {
		FIBEditorController editorController = new FIBEditorController(newEditedFIB, this, frame);
		if (getMainPanel() != null) {
			mainPanel.newEditedComponent(editorController);
		}
		activate(editorController);
		return editorController;
	}

	public FIBEditorController openFIBComponent(FIBComponent component, Resource fibResource, JFrame frame) {

		if (fibResource instanceof FileResourceImpl) {
			File fibFile = ((FileResourceImpl) fibResource).getFile();
			JFIBPreferences.setLastFile(fibFile);
		}

		EditedFIBComponent newEditedFIB = new EditedFIBComponent(fibResource.getRelativePath(), component, getFIBLibrary());
		newEditedFIB.setSourceResource(fibResource);

		return newEditedComponent(newEditedFIB, frame);
	}

	private int newIndex = 0;

	public FIBEditorController newFIB(JFrame frame) {

		FIBModelFactory factory = null;

		try {
			factory = new FIBModelFactory();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			return null;
		}

		FIBPanel fibComponent = factory.newInstance(FIBPanel.class);
		fibComponent.setLayout(Layout.border);
		fibComponent.finalizeDeserialization();
		EditedFIBComponent newEditedFIB = new EditedFIBComponent("New" + (newIndex > 0 ? newIndex + 1 : "") + ".fib", fibComponent,
				getFIBLibrary());
		newIndex++;

		return newEditedComponent(newEditedFIB, frame);
	}

	public FIBEditorController loadFIB(JFrame frame) {

		FlexoFileChooser fileChooser = getFileChooser(frame);

		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File fibFile = fileChooser.getSelectedFile();
			return loadFIB(fibFile, frame);
		}

		return null;
	}

	public FIBEditorController loadFIB(File fibFile, JFrame frame) {
		return loadFIB(fibFile, null, frame);
	}

	public FIBEditorController loadFIB(Resource fibResource, JFrame frame) {
		return loadFIB(fibResource, null, frame);
	}

	public FIBEditorController loadFIB(File fibFile, Object dataObject, JFrame frame) {

		if (fibFile != null && !fibFile.exists()) {
			JOptionPane.showMessageDialog(frame, "File " + fibFile.getAbsolutePath() + " does not exist anymore");
			return null;
		}
		JFIBPreferences.setLastFile(fibFile);

		FileResourceImpl fibResource = null;
		try {
			fibResource = new FileResourceImpl(resourceLocator, fibFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return loadFIB(fibResource, dataObject, frame);
	}

	public FIBEditorController loadFIB(Resource fibResource, Object dataObject, JFrame frame) {

		Resource productionResource = fibResource;
		Resource sourceResource = ResourceLocator.locateSourceCodeResource(productionResource);
		EditedFIBComponent newEditedFIB = new EditedFIBComponent(sourceResource, productionResource, getFIBLibrary());
		newEditedFIB.setDataObject(dataObject);

		return newEditedComponent(newEditedFIB, frame);
	}

	public void saveFIB(EditedFIBComponent editedFIB, JFrame frame) {
		if (editedFIB == null) {
			return;
		}
		if (editedFIB.getSourceFile() != null) {
			editedFIB.save();
		}
		else {
			saveFIBAs(editedFIB, frame);
		}
	}

	public void saveFIBAs(EditedFIBComponent editedFIB, JFrame frame) {
		if (editedFIB == null) {
			return;
		}

		FlexoFileChooser fileChooser = getFileChooser(frame);

		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			System.out.println("file=" + file);
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

	public void closeFIB(EditedFIBComponent editedFIB, JFrame frame) {
		logger.warning("Not implemented yet");
	}

	public void testFIB(EditedFIBComponent editedFIB, JFrame frame) {
		JFIBView<?, ? extends JComponent> view = (JFIBView<?, ? extends JComponent>) FIBController.makeView(editedFIB.getFIBComponent(),
				SwingViewFactory.INSTANCE, LOCALIZATION, true);

		if (editedFIB.getDataObject() != null) {
			view.getController().setDataObject(editedFIB.getDataObject());
		}
		else {
			if (editedFIB.getFIBComponent() instanceof FIBContainer
					&& ((FIBContainer) editedFIB.getFIBComponent()).getDataClass() != null) {
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
		}

		JDialog testInterface = new JDialog(frame, "Test", false);
		testInterface.getContentPane().add(view.getResultingJComponent());
		testInterface.pack();
		testInterface.setVisible(true);
	}

	public void localizeFIB(EditedFIBComponent editedFIB, JFrame frame) {

		if (editedFIB != null && editedFIB.getFIBComponent() != null) {
			editedFIB.getFIBComponent().searchAndRegisterAllLocalized();
			getLocalizationWindow(editedFIB, frame).setVisible(true);
		}

	}

	public void validateFIB(EditedFIBComponent editedFIB, JFrame frame) {
		if (editedFIB != null && editedFIB.getFIBComponent() != null) {
			try {
				getValidationWindow(editedFIB, frame).validateAndDisplayReportForComponent(editedFIB.getFIBComponent());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected ComponentValidationWindow getValidationWindow(EditedFIBComponent editedComponent, JFrame frame) {
		if (componentValidationWindow != null && componentValidationWindow.getEditedComponent() != editedComponent) {
			componentValidationWindow.dispose();
			componentValidationWindow = null;
		}
		FIBEditorController editorController = getControllerForEditedFIBComponent(editedComponent);
		if (componentValidationWindow == null && editorController != null) {
			componentValidationWindow = new ComponentValidationWindow(frame, editorController, APP_FIB_LIBRARY);
		}
		return componentValidationWindow;
	}

	protected ComponentLocalizationWindow getLocalizationWindow(EditedFIBComponent editedComponent, JFrame frame) {
		if (componentLocalizationWindow != null && componentLocalizationWindow.getEditedComponent() != editedComponent) {
			componentLocalizationWindow.dispose();
			componentLocalizationWindow = null;
		}
		FIBEditorController editorController = getControllerForEditedFIBComponent(editedComponent);
		if (componentLocalizationWindow == null && editorController != null) {
			componentLocalizationWindow = new ComponentLocalizationWindow(frame, editorController);
		}
		return componentLocalizationWindow;
	}

	public void switchToLanguage(Language lang) {
		FlexoLocalization.setCurrentLanguage(lang);
		for (EditedFIBComponent editedComponent : getEditedFIBComponents()) {
			FIBEditorController editorController = getControllerForEditedFIBComponent(editedComponent);
			if (editorController != null) {
				editorController.switchToLanguage(lang);
			}
		}

	}

	public boolean activate(FIBEditorController editorController) {
		if (activeEditorController == editorController) {
			return false;
		}
		if (activeEditorController != null) {
			disactivate(activeEditorController);
		}
		System.out.println("Activate edition of " + editorController.getEditedComponent().getName());
		activeEditorController = editorController;
		if (getPalettes() != null) {
			getPalettes().setEditorController(editorController);
		}
		if (getMainPanel() != null) {
			mainPanel.focusOnEditedComponent(editorController);
		}
		return true;
	}

	public boolean disactivate(FIBEditorController editorController) {
		if (activeEditorController == editorController) {
			System.out.println("Desactivate edition of " + editorController.getEditedComponent().getName());
			activeEditorController = null;
			if (getPalettes() != null) {
				getPalettes().setEditorController(null);
			}
			return true;
		}
		return false;
	}

}
