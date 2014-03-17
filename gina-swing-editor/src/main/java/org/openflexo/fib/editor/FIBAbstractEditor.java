/*
 * (c) Copyright 2010-2011 AgileBirds
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
package org.openflexo.fib.editor;

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

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.controller.FIBEditorPalette;
import org.openflexo.fib.editor.controller.FIBInspectorController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.utils.FlexoLoggingViewer;
import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.Resource;
import org.openflexo.rm.CompositeResourceLocatorImpl;
import org.openflexo.toolbox.ToolBox;

//TODO: switch to the right editor controller when switching tab
//	getPalette().setEditorController(editorController);
public abstract class FIBAbstractEditor implements FIBGenericEditor {


	private static CompositeResourceLocatorImpl rl = CompositeResourceLocatorImpl.getResourceLocator();
	
	/*public static <T extends FIBAbstractEditor> void main(final Class<T> editor) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				T instance;
				try {
					instance = editor.newInstance();
					instance.launch();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}*/

	private static final Logger logger = FlexoLogger.getLogger(FIBAbstractEditor.class.getPackage().getName());

	// Instanciate a new localizer in directory src/dev/resources/FIBEditorLocalizer
	// linked to parent localizer (which is Openflexo main localizer)
	public static LocalizedDelegateGUIImpl LOCALIZATION = new LocalizedDelegateGUIImpl(rl.locateResource("FIBEditorLocalized"),
			new LocalizedDelegateGUIImpl(rl.locateResource("Localized"), null, false), true);

	public static Resource COMPONENT_LOCALIZATION_FIB=  rl.locateResource("Fib/ComponentLocalization.fib");

	final JFrame frame;
	// private JPanel mainPanel;
	private final FIBEditorPalette palette;

	private final FIBInspectorController inspector;

	private File fibFile;
	private FIBComponent fibComponent;
	private FIBEditorController editorController;

	// This is the factory used to edit the FIB component
	private FIBModelFactory factory;

	private final JMenu actionMenu;

	public FIBEditorController getEditorController() {
		return editorController;
	}

	@Override
	public JFrame getFrame() {
		return frame;
	}

	@Override
	public FIBInspectorController getInspector() {
		return inspector;
	}

	@Override
	public FIBEditorPalette getPalette() {
		return palette;
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

		frame = new JFrame();
		frame.setPreferredSize(new Dimension(1200, 800));

		inspector = new FIBInspectorController(frame);

		palette = new FIBEditorPalette(frame);
		palette.setVisible(true);

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

		JMenuItem componentLocalizationItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,
				"component_localization"));
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
		inspectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ToolBox.getPLATFORM() != ToolBox.MACOS ? InputEvent.CTRL_MASK
				: InputEvent.META_MASK));

		JMenuItem paletteItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "show_palette"));
		paletteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				palette.setVisible(true);
			}
		});
		inspectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ToolBox.getPLATFORM() != ToolBox.MACOS ? InputEvent.CTRL_MASK
				: InputEvent.META_MASK));

		JMenuItem logsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "logs"));
		logsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), frame);
			}
		});

		JMenuItem localizedItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "localized_editor"));
		localizedItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LOCALIZATION.showLocalizedEditor(frame);
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

	protected boolean confirmExit() {
		int ret = JOptionPane.showOptionDialog(frame,
				FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "would_you_like_to_save_before_quit?"),
				FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "Exit dialog"), JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
		if (ret == JOptionPane.YES_OPTION) {
			return saveFIB();
		} else if (ret == JOptionPane.NO_OPTION) {
			return true;
		}
		return false;
	}

	public abstract Object[] getData();

	public abstract File getFIBFile();

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
		return null;
	}

	public void loadFIB() {
		fibFile = getFIBFile();

		try {
			factory = new FIBModelFactory(fibFile.getParentFile());
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibFile);

		if (fibComponent == null) {
			logger.log(Level.SEVERE, "Fib component not found ! Path: '" + fibFile.getAbsolutePath() + "'");
			throw new RuntimeException("Fib component not found ! Path: '" + fibFile.getAbsolutePath() + "'");
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
			editorController = new FIBEditorController(factory, fibComponent, this, dataObject, getController());
		} else {
			editorController = new FIBEditorController(factory, fibComponent, this, dataObject);
		}
		getPalette().setEditorController(editorController);
		frame.getContentPane().add(editorController.getEditorPanel());
		frame.pack();

		FIBPreferences.setLastFile(fibFile);
	}

	public void switchToData(Object data) {
		editorController.setDataObject(data);
	}

	public boolean saveFIB() {
		logger.info("Save to file " + fibFile.getAbsolutePath());
		return FIBLibrary.save(fibComponent, fibFile);
	}

	public void testFIB() {
		FIBView view;
		FIBController controller = makeNewController(fibComponent);
		if (controller != null) {
			view = FIBController.makeView(fibComponent, controller);
		} else {
			view = FIBController.makeView(fibComponent, LOCALIZATION);
		}
		view.getController().setDataObject(editorController.getDataObject());
		JDialog testInterface = new JDialog(frame, "Test", false);
		testInterface.getContentPane().add(view.getResultingJComponent());
		testInterface.pack();
		testInterface.setVisible(true);
	}

	public void localizeFIB() {
		FIBComponent componentLocalizationComponent = FIBLibrary.instance().retrieveFIBComponent(COMPONENT_LOCALIZATION_FIB,true);

		FIBView view = FIBController.makeView(componentLocalizationComponent, LOCALIZATION);
		view.getController().setDataObject(editorController.getController());
		JDialog localizationInterface = new JDialog(frame, FlexoLocalization.localizedForKey(LOCALIZATION, "component_localization"), false);
		localizationInterface.getContentPane().add(view.getResultingJComponent());
		localizationInterface.pack();
		localizationInterface.setVisible(true);
	}

	public void validateFIB() {
		if (fibComponent != null) {
			getValidationWindow().validateAndDisplayReportForComponent(fibComponent);
		}
	}

	private ValidationWindow validationWindow;

	protected ValidationWindow getValidationWindow() {
		if (validationWindow == null) {
			validationWindow = new ValidationWindow(frame, editorController);
		}
		return validationWindow;
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

	public boolean showExitMenuItem() {
		return true;
	}

	@Override
	public File getEditedComponentFile() {
		return getFIBFile();
	}

	public static <T extends FIBAbstractEditor> void init(T abstractEditor) {
		abstractEditor.loadFIB();
		abstractEditor.getFrame().setVisible(true);
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
