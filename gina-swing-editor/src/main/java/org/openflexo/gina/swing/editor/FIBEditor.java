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

import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.openflexo.gina.ApplicationFIBLibrary;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorPalette;
import org.openflexo.gina.swing.utils.JFIBInspectorController;
import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.gina.swing.utils.localization.LocalizedEditor;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.FileSystemResourceLocatorImpl;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.ToolBox;

// TODO: switch to the right editor controller when switching tab
// 		getPalette().setEditorController(editorController);
public class FIBEditor implements FIBGenericEditor {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	// public static Resource COMPONENT_LOCALIZATION_FIB =
	// ResourceLocator.locateResource("Fib/ComponentLocalization.fib");

	/*
	 * static { try { FlexoLoggingManager.initialize(-1, true, null,
	 * Level.WARNING, null); } catch (SecurityException e) {
	 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } }
	 */

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		if (ToolBox.isMacOS()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "FIBEditor");
		}

		// Needs a FileSystemResourceLocator to locate Files
		// Not required anymore: done a little bit after in FIBEditor
		// constructor
		/*
		 * final FileSystemResourceLocatorImpl fsrl = new
		 * FileSystemResourceLocatorImpl();
		 * fsrl.appendToDirectories(System.getProperty("user.dir"));
		 * ResourceLocator.appendDelegate(fsrl);
		 */

		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		/*
		 * DefaultExpressionParser parser = new DefaultExpressionParser(); try {
		 * //Expression test = parser.parse(
		 * "data.name.substring(0,1).toUpperCase()+data.name.substring(1,6)");
		 * //Expression test = parser.parse("data.name.substring().a()+2");
		 * //Expression test = parser.parse("a.b().c()+2"); Expression test =
		 * parser.parse("a.c()+2"); System.out.println("test="+test); } catch
		 * (ParseException e1) { e1.printStackTrace(); } System.exit(-1);
		 */

		/*
		 * try { Class.forName("org.openflexo.Flexo"); } catch
		 * (ClassNotFoundException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 */

		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
			FlexoLocalization.initWith(FIBAbstractEditor.LOCALIZATION);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				FIBEditor editor = new FIBEditor();
				editor.showPanel();
			}
		});

	}

	private final JFrame frame;
	private final FIBEditorPalette palette;
	private final FlexoFileChooser fileChooser;

	private final JFIBInspectorController inspector;

	private FIBEditorController editorController;

	private LocalizedEditor localizedEditor;
	private ComponentValidationWindow componentValidationWindow;
	private ComponentLocalizationWindow componentLocalizationWindow;

	final FileSystemResourceLocatorImpl resourceLocator;

	private ApplicationFIBLibrary APP_FIB_LIBRARY = ApplicationFIBLibrary.instance();
	private FIBLibrary editorFIBLibrary;

	private MainPanel mainPanel;
	private MenuBar menuBar;

	public class EditedFIB {
		private String title;
		private File fibFile;
		private final FIBComponent fibComponent;
		// This is the factory used to edit the FIB component
		private final FIBModelFactory factory;

		public EditedFIB(String title, File fibFile, FIBComponent fibComponent, FIBModelFactory factory) {
			super();
			this.title = title;
			this.fibFile = fibFile;
			this.fibComponent = fibComponent;
			this.factory = factory;

		}

		public FIBComponent getFIBComponent() {
			return fibComponent;
		}

		public FIBModelFactory getFactory() {
			return factory;
		}

		public void save() {
			logger.info("Save to file " + fibFile.getAbsolutePath());

			editorFIBLibrary.save(fibComponent, fibFile);
		}

	}

	private EditedFIB editedFIB;

	public FIBEditor() {
		super();

		editorFIBLibrary = new FIBLibrary();

		frame = new JFrame();
		frame.setBounds(JFIBPreferences.getFrameBounds());
		new ComponentBoundSaver(frame) {

			@Override
			public void saveBounds(Rectangle bounds) {
				JFIBPreferences.setFrameBounds(bounds);
			}
		};
		fileChooser = new FlexoFileChooser(frame);
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

		resourceLocator = new FileSystemResourceLocatorImpl();
		resourceLocator.appendToDirectories(JFIBPreferences.getLastDirectory().getAbsolutePath());
		resourceLocator.appendToDirectories(System.getProperty("user.home"));
		ResourceLocator.appendDelegate(resourceLocator);

		inspector = new JFIBInspectorController(frame, ResourceLocator.locateResource("EditorInspectors"), APP_FIB_LIBRARY,
				FIBAbstractEditor.LOCALIZATION);

		palette = new FIBEditorPalette(frame);
		palette.setVisible(true);
	}

	@Override
	public JFIBInspectorController getInspector() {
		return inspector;
	}

	@Override
	public FIBEditorPalette getPalette() {
		return palette;
	}

	private void updateFrameTitle() {
		frame.setTitle("Flexo Interface Builder Editor");
	}

	public void showPanel() {
		frame.setTitle("Flexo Interface Builder Editor");
		mainPanel = new MainPanel();

		menuBar = new MenuBar();

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(mainPanel);
		mainPanel.revalidate();
		frame.setVisible(true);
	}

	public void quit() {

		// TODO: Check if a component needs to be saved.
		/*
		 * for(FIBEditor.EditedFIB c: mainPanel.editedComponents) { }
		 */
		frame.dispose();
		System.exit(0);
	}

	public void closeFIB() {
		logger.warning("Not implemented yet");
	}

	public void newFIB() {

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
		EditedFIB newEditedFIB = new EditedFIB("New.fib", new File("NewComponent.fib"), fibComponent, factory);

		editorController = new FIBEditorController(factory, fibComponent, this);
		getPalette().setEditorController(editorController);

		mainPanel.newEditedComponent(newEditedFIB, editorController);

	}

	public void loadFIB() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File fibFile = fileChooser.getSelectedFile();
			loadFIB(fibFile);
		}
	}

	public void loadFIB(File fibFile) {

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

		FIBModelFactory factory = null;
		try {
			factory = new FIBModelFactory(fibFile.getParentFile());
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			return;
		}
		FIBComponent fibComponent = editorFIBLibrary.retrieveFIBComponent(fibResource, false, factory);
		EditedFIB newEditedFIB = new EditedFIB(fibFile.getName(), fibFile, fibComponent, factory);
		editorController = new FIBEditorController(factory, fibComponent, this);
		getPalette().setEditorController(editorController);
		mainPanel.newEditedComponent(newEditedFIB, editorController);
	}

	public void saveFIB() {
		if (editedFIB == null) {
			return;
		}
		if (editedFIB.fibFile == null) {
			saveFIBAs();
		}
		else {
			editedFIB.save();
		}
	}

	public void saveFIBAs() {
		if (editedFIB == null) {
			return;
		}
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith(".fib")) {
				file = new File(file.getParentFile(), file.getName() + ".fib");
			}
			JFIBPreferences.setLastFile(file);
			editedFIB.fibFile = file;
			editedFIB.title = file.getName();
			mainPanel.setTitleAt(mainPanel.getSelectedIndex(), editedFIB.title);
			updateFrameTitle();
			editedFIB.save();
		}
		else {
			return;
		}
	}

	public void testFIB() {
		JFIBView<?, ? extends JComponent> view = (JFIBView<?, ? extends JComponent>) FIBController.makeView(editedFIB.fibComponent,
				SwingViewFactory.INSTANCE, FIBAbstractEditor.LOCALIZATION);

		// Class testClass = null;
		if (editedFIB.fibComponent instanceof FIBContainer && ((FIBContainer) editedFIB.fibComponent).getDataClass() != null) {
			try {
				// testClass =
				// Class.forName(editedFIB.fibComponent.getDataClassName());
				FIBContainer container = (FIBContainer) editedFIB.fibComponent;
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

	public void localizeFIB() {

		if (editorController == null) {
			return;
		}

		if (editedFIB != null && editedFIB.fibComponent != null) {
			editedFIB.fibComponent.searchAndRegisterAllLocalized();
			getLocalizationWindow(editedFIB.fibComponent).setVisible(true);
		}

	}

	public void validateFIB() {
		if (editedFIB != null && editedFIB.fibComponent != null) {
			try {
				getValidationWindow(editedFIB.fibComponent).validateAndDisplayReportForComponent(editedFIB.fibComponent);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected ComponentValidationWindow getValidationWindow(FIBComponent component) {
		if (componentValidationWindow != null && componentValidationWindow.getFIBComponent() != component) {
			componentValidationWindow.dispose();
			componentValidationWindow = null;
		}
		if (componentValidationWindow == null) {
			componentValidationWindow = new ComponentValidationWindow(frame, editorController, APP_FIB_LIBRARY);
		}
		return componentValidationWindow;
	}

	protected ComponentLocalizationWindow getLocalizationWindow(FIBComponent component) {
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

	@Override
	public JFrame getFrame() {
		return frame;
	}

	@Override
	public File getEditedComponentFile() {
		return editedFIB.fibFile;
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

	public class MenuBar extends JMenuBar implements PreferenceChangeListener {
		private final JMenu fileMenu;

		private final JMenu editMenu;

		private final JMenu toolsMenu;

		private final JMenu helpMenu;

		private final JMenuItem newItem;

		private final JMenuItem loadItem;

		private final JMenuItem saveItem;

		private final JMenuItem saveAsItem;

		private final JMenuItem closeItem;

		private final JMenuItem quitItem;

		private final JMenu languagesItem;

		private final JMenuItem inspectItem;

		private final JMenuItem logsItem;

		private final JMenuItem localizedItem;

		private final JMenuItem displayFileItem;

		private final JMenuItem testInterfaceItem;

		private final JMenuItem componentLocalizationItem;
		private final JMenuItem componentValidationItem;

		private final JMenu openRecent;

		private final JMenuItem showPaletteItem;

		public MenuBar() {
			fileMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "file"));
			editMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "edit"));
			toolsMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "tools"));
			helpMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "help"));

			newItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "new_interface"));
			newItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					newFIB();
				}
			});

			loadItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "open_interface"));
			loadItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadFIB();
				}
			});

			openRecent = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "open_recent"));
			JFIBPreferences.addPreferenceChangeListener(this);
			updateOpenRecent();
			saveItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "save_interface"));
			saveItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveFIB();
				}
			});

			saveAsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "save_interface_as"));
			saveAsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveFIBAs();
				}
			});

			closeItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "close_interface"));
			closeItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					closeFIB();
				}
			});

			quitItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "quit"));
			quitItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					quit();
				}
			});
			testInterfaceItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "test_interface"));
			testInterfaceItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					testFIB();
				}
			});

			componentLocalizationItem = new JMenuItem(
					FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "component_localization"));
			componentLocalizationItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					localizeFIB();
				}
			});

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

			fileMenu.add(newItem);
			fileMenu.add(loadItem);
			fileMenu.add(openRecent);
			fileMenu.add(saveItem);
			fileMenu.add(saveAsItem);
			fileMenu.add(closeItem);
			fileMenu.addSeparator();
			fileMenu.add(testInterfaceItem);
			fileMenu.add(componentLocalizationItem);

			fileMenu.add(lafsItem);
			languagesItem = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "switch_to_language"));
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
			fileMenu.addSeparator();
			fileMenu.add(quitItem);

			inspectItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "inspect"));
			inspectItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getInspector().setVisible(true);
				}
			});
			inspectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
					ToolBox.getPLATFORM() != ToolBox.MACOS ? InputEvent.CTRL_MASK : InputEvent.META_MASK));
			showPaletteItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "show_palette"));
			showPaletteItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPalette().setVisible(true);
				}
			});
			logsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "logs"));
			logsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), APP_FIB_LIBRARY, frame);
				}
			});

			localizedItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "localized_editor"));
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

			displayFileItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "display_file"));
			displayFileItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					logger.info("Getting this " + editedFIB.getFactory().stringRepresentation(editedFIB.fibComponent));
				}
			});

			componentValidationItem = new JMenuItem(
					FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "validate_component"));
			componentValidationItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					validateFIB();
				}
			});

			toolsMenu.add(inspectItem);
			toolsMenu.add(showPaletteItem);
			toolsMenu.add(logsItem);
			toolsMenu.add(localizedItem);
			toolsMenu.add(displayFileItem);
			toolsMenu.addSeparator();
			toolsMenu.add(componentValidationItem);

			add(fileMenu);
			add(editMenu);
			add(toolsMenu);
			add(helpMenu);
		}

		private boolean willUpdate = false;

		@Override
		public void preferenceChange(PreferenceChangeEvent evt) {
			if (evt.getKey().startsWith(JFIBPreferences.LAST_FILE)) {
				if (willUpdate) {
					return;
				}
				else {
					willUpdate = true;
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						willUpdate = false;
						updateOpenRecent();
					}
				});
			}
		}

		private void updateOpenRecent() {
			openRecent.removeAll();
			List<File> files = JFIBPreferences.getLastFiles();
			openRecent.setEnabled(files.size() != 0);
			for (final File file : files) {
				JMenuItem item = new JMenuItem(file.getName());
				item.setToolTipText(file.getAbsolutePath());
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadFIB(file);
					}
				});
				openRecent.add(item);
			}
		}
	}

	public class MainPanel extends JTabbedPane implements ChangeListener {
		private final Vector<EditedFIB> editedComponents;
		private final Hashtable<EditedFIB, FIBEditorController> controllers;

		public MainPanel() {
			super();
			editedComponents = new Vector<EditedFIB>();
			controllers = new Hashtable<EditedFIB, FIBEditorController>();
			addChangeListener(this);
		}

		public void newEditedComponent(EditedFIB edited, FIBEditorController controller) {
			editedComponents.add(edited);
			controllers.put(edited, controller);
			add(controller.getEditorPanel(), edited.title);
			revalidate();
			setSelectedIndex(getComponentCount() - 1);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			logger.info("Change for " + e);
			int index = getSelectedIndex();
			editedFIB = editedComponents.get(index);
		}
	}

}
