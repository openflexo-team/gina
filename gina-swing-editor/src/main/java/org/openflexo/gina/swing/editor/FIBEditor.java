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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.inspector.FIBEditorInspectorController;
import org.openflexo.gina.swing.editor.palette.FIBEditorPalettes;
import org.openflexo.gina.swing.editor.palette.FIBEditorPalettesDialog;
import org.openflexo.gina.swing.editor.validation.ComponentValidationWindow;
import org.openflexo.gina.swing.utils.FIBEditorLoadingProgress;
import org.openflexo.gina.swing.utils.JFIBDialogInspectorController;
import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.gina.swing.utils.localization.LocalizedEditor;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.Clipboard;
import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.FileSystemResourceLocatorImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.ToolBox;

/**
 * This class provides a generic framework for managing {@link FIBComponent} edition<br>
 * 
 * 
 * @author sylvain
 *
 */
public class FIBEditor {

	static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	public static LocalizedDelegate EDITOR_LOCALIZATION = new LocalizedDelegateImpl(
			ResourceLocator.locateResource("GinaLocalization/GinaSwingEditor"), FIBModelObjectImpl.GINA_LOCALIZATION, true, true);

	public static Resource COMPONENT_LOCALIZATION_FIB = ResourceLocator.locateResource("Fib/LocalizedPanel.fib");

	public static final int META_MASK = ToolBox.isMacOS() ? InputEvent.META_MASK : InputEvent.CTRL_MASK;
	public static final int MULTI_SELECTION_MASK = ToolBox.isMacOS() ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
	public static final int DELETE_KEY_CODE = ToolBox.isMacOS() ? KeyEvent.VK_BACK_SPACE : KeyEvent.VK_DELETE;
	public static final int BACKSPACE_DELETE_KEY_CODE = ToolBox.isMacOS() ? KeyEvent.VK_DELETE : KeyEvent.VK_BACK_SPACE;

	private FIBEditorPalettes palette;
	private JFIBDialogInspectorController inspector;
	private FIBEditorInspectorController inspectors;

	protected LocalizedEditor localizedEditor;
	private ComponentValidationWindow componentValidationWindow;
	private ComponentLocalizationWindow componentLocalizationWindow;

	final FileSystemResourceLocatorImpl resourceLocator;

	static ApplicationFIBLibrary APP_FIB_LIBRARY = ApplicationFIBLibraryImpl.instance();
	private final FIBLibrary fibLibrary;

	private MainPanel mainPanel;
	private FIBEditorMenuBar menuBar;

	// This map stores all editors (FIBEditorController - editor of an EditedFIBComponent) managed by this FIBEditor
	private final Map<EditedFIBComponent, FIBEditorController> controllers = new HashMap<>();

	private FIBEditorController activeEditorController = null;

	private final FIBEditorLoadingProgress progress;

	// Clipboard shared by all components opened in this FIBEditor
	private Clipboard clipboard;

	public FIBEditor(FIBLibrary fibLibrary) {
		this(fibLibrary, null);
	}

	public FIBEditor(FIBLibrary fibLibrary, FIBEditorLoadingProgress progress) {
		super();

		this.fibLibrary = fibLibrary;
		this.progress = progress;

		resourceLocator = new FileSystemResourceLocatorImpl();
		if (JFIBPreferences.getLastDirectory() != null) {
			resourceLocator.appendToDirectories(JFIBPreferences.getLastDirectory().getAbsolutePath());
		}
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

	private static FlexoFileChooser getFileChooser(JFrame frame) {
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
			progress.progress(EDITOR_LOCALIZATION.localizedForKey("make_menu_bar"));
		}
		menuBar = new FIBEditorMenuBar(this, frame);
		return menuBar;
	}

	public JFIBDialogInspectorController makeInspector(JFrame frame) {
		inspector = new JFIBDialogInspectorController(frame, ResourceLocator.locateResource("EditorInspectors"), APP_FIB_LIBRARY,
				EDITOR_LOCALIZATION, progress);
		return inspector;
	}

	public FIBEditorPalettesDialog makePaletteDialog(JFrame frame) {
		if (palette == null) {
			if (progress != null) {
				progress.progress(EDITOR_LOCALIZATION.localizedForKey("make_palette_dialog"));
			}
			palette = makePalette();
		}
		return new FIBEditorPalettesDialog(frame, palette);
	}

	public FIBEditorPalettes makePalette() {
		if (progress != null) {
			progress.progress(EDITOR_LOCALIZATION.localizedForKey("make_palette"));
		}
		palette = new FIBEditorPalettes();
		return palette;
	}

	public FIBEditorInspectorController makeInspectors() {
		if (progress != null) {
			progress.progress(EDITOR_LOCALIZATION.localizedForKey("make_inspectors"));
		}
		inspectors = new FIBEditorInspectorController();
		return inspectors;
	}

	public MainPanel makeMainPanel() {
		if (progress != null) {
			progress.progress(EDITOR_LOCALIZATION.localizedForKey("make_main_panel"));
		}
		mainPanel = new MainPanel(this);
		return mainPanel;
	}

	public FIBLibrary getFIBLibrary() {
		return fibLibrary;
	}

	public JFIBDialogInspectorController getInspector() {
		return inspector;
	}

	public FIBEditorInspectorController getInspectors() {
		return inspectors;
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

	private static void updateFrameTitle(JFrame frame) {
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

	// Edited components, stored using their source resource
	private final List<EditedFIBComponent> editedComponents = new ArrayList<>();

	private EditedFIBComponent retrieveEditedFIBComponent(Resource resource) {
		return retrieveEditedFIBComponent(resource, null);
	}

	private EditedFIBComponent retrieveEditedFIBComponent(Resource resource, FIBComponent fibComponent) {

		EditedFIBComponent returned = null;

		Resource sourceResource = ResourceLocator.locateSourceCodeResource(resource);

		// First, attempt to lookup considering resource as source resource or production resource
		for (EditedFIBComponent e : editedComponents) {
			if (e.getSourceResource() != null && e.getSourceResource() == resource) {
				returned = e;
				break;
			}
			if (e.getProductionResource() != null && e.getProductionResource() == resource) {
				returned = e;
				break;
			}
		}

		if (returned == null) {
			// OK, we have to instanciate it
			Resource productionResource = null;
			if (sourceResource == null || sourceResource.equals(resource)) {
				// no source resource means that resource is already source resource
				sourceResource = resource;
			}
			else {
				productionResource = resource;
			}
			if (fibComponent == null) {
				returned = new EditedFIBComponent(sourceResource, productionResource, getFIBLibrary());
			}
			else {
				returned = new EditedFIBComponent(fibComponent, getFIBLibrary());
				returned.setSourceResource(sourceResource);
				returned.setProductionResource(productionResource);
			}
			editedComponents.add(returned);
		}

		return returned;
	}

	/**
	 * Internally used to create and register a new {@link FIBEditorController} managing the edition of a {@link EditedFIBComponent}
	 * 
	 * @param newEditedFIB
	 * @param frame
	 * @return
	 */
	private FIBEditorController openEditedComponent(EditedFIBComponent newEditedFIB, Object dataObject, JFrame frame) {

		newEditedFIB.setDataObject(dataObject);

		FIBEditorController returned = controllers.get(newEditedFIB);

		if (returned == null) {
			returned = new FIBEditorController(newEditedFIB, this, frame);
			controllers.put(newEditedFIB, returned);
			if (getMainPanel() != null) {
				mainPanel.newEditedComponent(returned);
			}
		}

		if (dataObject != null) {
			returned.setDataObject(dataObject);
		}

		activate(returned);
		return returned;
	}

	/**
	 * Called to launch the edition of a FIBComponent in the editor<br>
	 * Editor might be retrieved from cache, using resource attached to component.
	 * 
	 * @param component
	 * @param frame
	 * @return
	 */
	public FIBEditorController openFIBComponent(FIBComponent component, Object dataObject, JFrame frame) {

		EditedFIBComponent editedFIB = null;
		if (component.getResource() == null) {
			editedFIB = new EditedFIBComponent("New" + (newIndex > 0 ? newIndex + 1 : "") + ".fib", component, getFIBLibrary());
			editedComponents.add(editedFIB);
			newIndex++;
		}
		else {
			if (component.getResource() instanceof FileResourceImpl) {
				File fibFile = ((FileResourceImpl) component.getResource()).getFile();
				JFIBPreferences.setLastFile(fibFile);
			}
			editedFIB = retrieveEditedFIBComponent(component.getResource(), component);
		}

		return openEditedComponent(editedFIB, dataObject, frame);
	}

	/**
	 * Called to launch the edition of a FIBComponent - identified by its resource - in the editor<br>
	 * Editor might be retrieved from cache
	 * 
	 * @param fibResource
	 * @param frame
	 * @return
	 */
	public FIBEditorController openFIBComponent(Resource fibResource, FIBComponent fibComponent, Object dataObject, JFrame frame) {

		if (fibResource instanceof FileResourceImpl) {
			File fibFile = ((FileResourceImpl) fibResource).getFile();
			JFIBPreferences.setLastFile(fibFile);
		}

		EditedFIBComponent editedFIB = retrieveEditedFIBComponent(fibResource, fibComponent);

		return openEditedComponent(editedFIB, dataObject, frame);
	}

	private int newIndex = 0;

	public FIBEditorController newFIB(JFrame frame) {

		FIBModelFactory factory = null;

		try {
			factory = new FIBModelFactory(null);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			return null;
		}

		FIBPanel fibComponent = factory.newInstance(FIBPanel.class);
		fibComponent.setLayout(Layout.border);
		fibComponent.finalizeDeserialization();
		EditedFIBComponent newEditedFIB = new EditedFIBComponent("New" + (newIndex > 0 ? newIndex + 1 : "") + ".fib", fibComponent,
				getFIBLibrary());
		editedComponents.add(newEditedFIB);
		newIndex++;

		return openEditedComponent(newEditedFIB, null, frame);
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

		EditedFIBComponent editedFIB = retrieveEditedFIBComponent(fibResource);
		editedFIB.setDataObject(dataObject);

		return openEditedComponent(editedFIB, dataObject, frame);
	}

	public void saveFIB(EditedFIBComponent editedFIB, JFrame frame) {
		if (editedFIB == null) {
			return;
		}
		if (editedFIB.getSourceResource() != null) {
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
				mainPanel.getTabbedPane().setTitleAt(mainPanel.getTabbedPane().getSelectedIndex(), editedFIB.getName());
				updateFrameTitle(frame);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (LocatorNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeFIB(EditedFIBComponent editedFIB, JFrame frame) {
		logger.warning("Not implemented yet");
	}

	public void testFIB(EditedFIBComponent editedFIB, JFrame frame) {
		JFIBView<?, ? extends JComponent> view = (JFIBView<?, ? extends JComponent>) FIBController.makeView(editedFIB.getFIBComponent(),
				SwingViewFactory.INSTANCE, EDITOR_LOCALIZATION, null, true);

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
					e.printStackTrace();
				} catch (IllegalAccessException e) {
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

	public EditedFIBComponent getEditedFIBComponent(FIBComponent component) {
		for (EditedFIBComponent editedFIB : editedComponents) {
			if (editedFIB.getFIBComponent() == component) {
				return editedFIB;
			}
		}
		return null;
	}

	/**
	 * Get clipboard shared by all components opened in this FIBEditor
	 * 
	 * @return
	 */
	public Clipboard getClipboard() {
		return clipboard;
	}

	/**
	 * Sets clipboard shared by all components opened in this FIBEditor
	 * 
	 * @param clipboard
	 */
	public void setClipboard(Clipboard clipboard) {
		this.clipboard = clipboard;
	}

}
