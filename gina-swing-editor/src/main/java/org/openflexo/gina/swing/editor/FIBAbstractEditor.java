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

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.openflexo.gina.ApplicationFIBLibrary;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.FIBLibrary.FIBLibraryImpl;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorPalettes;
import org.openflexo.gina.swing.editor.controller.FIBEditorPalettesDialog;
import org.openflexo.gina.swing.utils.JFIBInspectorController;
import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.gina.swing.utils.localization.LocalizedEditor;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.ToolBox;

//TODO: switch to the right editor controller when switching tab
//	getPalette().setEditorController(editorController);
public abstract class FIBAbstractEditor implements FIBGenericEditor {

	/*
	 * public static <T extends FIBAbstractEditor> void main(final Class<T>
	 * editor) { SwingUtilities.invokeLater(new Runnable() {
	 * 
	 * @Override public void run() { T instance; try { instance =
	 * editor.newInstance(); instance.launch(); } catch (InstantiationException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (IllegalAccessException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } } }); }
	 */

	private static final Logger logger = FlexoLogger.getLogger(FIBAbstractEditor.class.getPackage().getName());

	// Instanciate a new localizer in directory
	// src/dev/resources/FIBEditorLocalizer
	// linked to parent localizer (which is Openflexo main localizer)
	public static LocalizedDelegate LOCALIZATION = FlexoLocalization.getLocalizedDelegate(
			ResourceLocator.locateResource("FIBEditorLocalized"),
			FlexoLocalization.getLocalizedDelegate(ResourceLocator.locateResource("Localized"), null, false, false), true, true);

	public static Resource COMPONENT_LOCALIZATION_FIB = ResourceLocator.locateResource("Fib/ComponentLocalization.fib");

	final JFrame frame;
	// private JPanel mainPanel;
	private final FIBEditorPalettesDialog paletteDialog;

	private final JFIBInspectorController inspector;

	// private Resource fibResource;
	private FIBComponent fibComponent;
	private FIBEditorController editorController;

	// This is the factory used to edit the FIB component
	private FIBModelFactory factory;

	private final JMenu actionMenu;

	private LocalizedEditor localizedEditor;
	private ComponentValidationWindow componentValidationWindow;
	private ComponentLocalizationWindow componentLocalizationWindow;

	private ApplicationFIBLibrary APP_FIB_LIBRARY = ApplicationFIBLibraryImpl.instance();
	private FIBLibrary editorFIBLibrary;

	public FIBAbstractEditor() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
			FlexoLocalization.initWith(LOCALIZATION);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		editorFIBLibrary = FIBLibraryImpl.createInstance();

		frame = new JFrame();
		frame.setPreferredSize(new Dimension(1200, 800));

		inspector = new JFIBInspectorController(frame, ResourceLocator.locateResource("EditorInspectors"), APP_FIB_LIBRARY, LOCALIZATION);

		paletteDialog = new FIBEditorPalettesDialog(frame, new FIBEditorPalettes());
		paletteDialog.setVisible(true);

		frame.setTitle("Flexo Interface Builder Editor");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		// mainPanel = new JPanel();

		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "file"));
		JMenu editMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "edit"));
		actionMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "actions"));
		JMenu toolsMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "tools"));
		JMenu helpMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "help"));

		JMenuItem saveItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "save_interface"));
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFIB();
			}
		});

		JMenuItem quitItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "quit"));
		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		JMenuItem testInterfaceItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "test_interface"));
		testInterfaceItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testFIB();
			}
		});

		JMenuItem componentLocalizationItem = new JMenuItem(
				FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "component_localization"));
		componentLocalizationItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				localizeFIB();
			}
		});

		fileMenu.add(saveItem);
		fileMenu.add(testInterfaceItem);
		fileMenu.addSeparator();

		fileMenu.add(componentLocalizationItem);
		JMenu languagesItem = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "switch_to_language"));
		for (Language lang : Language.availableValues()) {
			JMenuItem languageItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, lang.getName()));
			final Language language = lang;
			languageItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchToLanguage(language);
				}
			});
			languagesItem.add(languageItem);
		}
		fileMenu.add(languagesItem);

		final JMenu lafsItem = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "look_and_feel"));
		final Vector<LAFMenuItem> lafsItems = new Vector<LAFMenuItem>();
		for (final LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			LAFMenuItem lafItem = new LAFMenuItem(laf);
			lafsItems.add(lafItem);
			lafItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						UIManager.setLookAndFeel(laf.getClassName());
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InstantiationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UnsupportedLookAndFeelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Window windows[] = frame.getOwnedWindows();
					for (int j = 0; j < windows.length; j++) {
						SwingUtilities.updateComponentTreeUI(windows[j]);
					}
					SwingUtilities.updateComponentTreeUI(frame);
					for (LAFMenuItem me : lafsItems) {
						me.updateState();
					}
				}
			});
			lafsItem.add(lafItem);
		}

		fileMenu.add(lafsItem);

		fileMenu.addSeparator();

		for (Object data : getData()) {
			final Object d = data;
			if (d != null) {
				JMenuItem switchDataItem = new JMenuItem(data.toString());
				switchDataItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						switchToData(d);
					}
				});
				fileMenu.add(switchDataItem);
			}
		}

		if (showExitMenuItem()) {
			fileMenu.addSeparator();
			fileMenu.add(quitItem);
		}

		JMenuItem inspectItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "inspect"));
		inspectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inspector.setVisible(true);
			}
		});
		inspectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				ToolBox.getPLATFORM() != ToolBox.MACOS ? InputEvent.CTRL_MASK : InputEvent.META_MASK));

		JMenuItem paletteItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "show_palette"));
		paletteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paletteDialog.setVisible(true);
			}
		});
		inspectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				ToolBox.getPLATFORM() != ToolBox.MACOS ? InputEvent.CTRL_MASK : InputEvent.META_MASK));

		JMenuItem logsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "logs"));
		logsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), APP_FIB_LIBRARY, frame);
			}
		});

		JMenuItem localizedItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "localized_editor"));
		localizedItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (localizedEditor == null) {
					localizedEditor = new LocalizedEditor(getFrame(), "localized_editor", FIBAbstractEditor.LOCALIZATION,
							FIBAbstractEditor.LOCALIZATION, APP_FIB_LIBRARY, true, false);
				}
				localizedEditor.setVisible(true);
			}
		});

		JMenuItem displayFileItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "display_file"));
		displayFileItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				logger.info("Getting this " + factory.stringRepresentation(fibComponent));

			}
		});

		JMenuItem validateItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "validate_component"));
		validateItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateFIB();
			}
		});

		toolsMenu.add(inspectItem);
		toolsMenu.add(paletteItem);
		toolsMenu.add(logsItem);
		toolsMenu.add(localizedItem);
		toolsMenu.add(displayFileItem);
		toolsMenu.addSeparator();
		toolsMenu.add(validateItem);

		mb.add(fileMenu);
		mb.add(editMenu);
		mb.add(actionMenu);
		mb.add(toolsMenu);
		mb.add(helpMenu);

		frame.setJMenuBar(mb);

		// frame.getContentPane().add(mainPanel);

	}

	public FIBEditorController getEditorController() {
		return editorController;
	}

	// @Override
	public JFrame getFrame() {
		return frame;
	}

	@Override
	public JFIBInspectorController getInspector() {
		return inspector;
	}

	@Override
	public FIBEditorPalettes getPalettes() {
		if (paletteDialog != null) {
			return paletteDialog.getPalettes();
		}
		return null;
	}

	public FIBEditorPalettesDialog getPaletteDialog() {
		return paletteDialog;
	}

	protected boolean confirmExit() {
		int ret = JOptionPane.showOptionDialog(frame,
				FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "would_you_like_to_save_before_quit?"),
				FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "Exit dialog"), JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
		if (ret == JOptionPane.YES_OPTION) {
			return saveFIB();
		}
		else if (ret == JOptionPane.NO_OPTION) {
			return true;
		}
		return false;
	}

	public abstract Object[] getData();

	public abstract Resource getFIBResource();

	private FIBController controller;

	/**
	 * Override when required
	 * 
	 * @return
	 */
	public FIBController getController() {
		return controller;
	}

	/**
	 * Override when required
	 * 
	 * @return
	 */
	public FIBController makeNewController(FIBComponent component) {
		return FIBController.instanciateController(component, SwingViewFactory.INSTANCE, LOCALIZATION);
	}

	public void loadFIB() {
		try {
			if (getFIBResource() instanceof FileResourceImpl) {
				factory = new FIBModelFactory(((FileResourceImpl) getFIBResource()).getFile().getParentFile());
			}
			else {
				factory = new FIBModelFactory();
			}
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("fibResource=" + getFIBResource());
		fibComponent = editorFIBLibrary.retrieveFIBComponent(getFIBResource());

		if (fibComponent == null) {
			logger.log(Level.SEVERE, "Fib component not found ! Path: '" + getFIBResource() + "'");
			throw new RuntimeException("Fib component not found ! Path: '" + getFIBResource() + "'");
		}

		Object dataObject = null;
		Object[] data = getData();
		if (data != null && data.length > 0) {
			dataObject = data[0];
		}

		if (getController() == null) {
			controller = makeNewController(fibComponent);
		}

		if (getController() != null) {
			editorController = new FIBEditorController(factory, fibComponent, this, dataObject, getController(), getFrame());
		}
		else {
			editorController = new FIBEditorController(factory, fibComponent, this, dataObject, getFrame());
		}
		getPaletteDialog().getPalettes().setEditorController(editorController);
		frame.getContentPane().add(editorController.getEditorPanel());
		frame.pack();

		if (getEditedComponentFile() != null) {
			JFIBPreferences.setLastFile(getEditedComponentFile());
		}
	}

	public void switchToData(Object data) {
		editorController.setDataObject(data);
	}

	public boolean saveFIB() {
		if (getEditedComponentFile() != null) {
			logger.info("Save to file " + getEditedComponentFile().getAbsolutePath());
			return editorFIBLibrary.save(fibComponent, getEditedComponentFile());
		}
		else {
			logger.warning("Cannot save READ-ONLY resource: " + getFIBResource());
			return false;
		}
	}

	public void testFIB() {
		JFIBView<?, ? extends JComponent> view;
		FIBController controller = makeNewController(fibComponent);
		if (controller != null) {
			view = (JFIBView<?, ? extends JComponent>) FIBController.makeView(fibComponent, SwingViewFactory.INSTANCE, controller);
		}
		else {
			view = (JFIBView<?, ? extends JComponent>) FIBController.makeView(fibComponent, SwingViewFactory.INSTANCE, LOCALIZATION);
		}
		view.getController().setDataObject(editorController.getDataObject());
		JDialog testInterface = new JDialog(frame, "Test", false);
		testInterface.getContentPane().add(view.getResultingJComponent());
		testInterface.pack();
		testInterface.setVisible(true);
	}

	public void localizeFIB() {

		/*
		 * fibComponent.searchLocalized(fibComponent.getLocalizedDictionary());
		 * 
		 * System.out.println("On cherche les locales pour: "); FIBComponent c =
		 * editorController.getController().getRootComponent();
		 * System.out.println(c.getFactory().stringRepresentation(c));
		 * 
		 * editorController.getController().searchNewLocalizationEntries();
		 */

		// getController().searchNewLocalizationEntries();

		if (fibComponent != null) {
			fibComponent.searchAndRegisterAllLocalized();
			getLocalizationWindow().setVisible(true);
		}
	}

	public void validateFIB() {
		if (fibComponent != null) {
			try {
				getValidationWindow().validateAndDisplayReportForComponent(fibComponent);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected ComponentValidationWindow getValidationWindow() {
		if (componentValidationWindow == null) {
			componentValidationWindow = new ComponentValidationWindow(frame, editorController, APP_FIB_LIBRARY);
		}
		return componentValidationWindow;
	}

	protected ComponentLocalizationWindow getLocalizationWindow() {
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

	public void quit() {
		frame.dispose();
		if (confirmExit() && exitOnDispose()) {
			System.exit(0);
		}
	}

	public boolean exitOnDispose() {
		return true;
	}

	@Deprecated
	public void launch() {
		if (!SwingUtilities.isEventDispatchThread()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Launch was not called from EDT. Doing so now, but consider using using FIBSbstractEditor.main(Class)");
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					launch();
				}
			});
			return;
		}
		logger.info(">>>>>>>>>>> Loading FIB...");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				init(FIBAbstractEditor.this);
			}
		});
	}

	public static Object[] makeArray(Object... o) {
		return o;
	}

	public void addAction(String string, ActionListener actionListener) {
		JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, string));
		menuItem.addActionListener(actionListener);
		actionMenu.add(menuItem);

	}

	final public boolean showExitMenuItem() {
		return true;
	}

	@Override
	public File getEditedComponentFile() {
		if (getFIBResource() instanceof FileResourceImpl) {
			return ((FileResourceImpl) getFIBResource()).getFile();
		}
		return null;
	}

	public static <T extends FIBAbstractEditor> void init(T abstractEditor) {
		abstractEditor.loadFIB();
		abstractEditor.getFrame().setVisible(true);
	}

	class LAFMenuItem extends JCheckBoxMenuItem {
		private final LookAndFeelInfo laf;

		public LAFMenuItem(LookAndFeelInfo laf) {
			super(laf.getName(), UIManager.getLookAndFeel().getClass().getName().equals(laf.getClassName()));
			this.laf = laf;
		}

		public void updateState() {
			setState(UIManager.getLookAndFeel().getClass().getName().equals(laf.getClassName()));
		}
	}

	public static <T extends FIBAbstractEditor> void main(final Class<T> klass) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					T abstractEditor = klass.newInstance();
					init(abstractEditor);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}
}
