package org.openflexo.gina.swing.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.gina.swing.utils.localization.LocalizedEditor;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.ToolBox;

public class FIBEditorMenuBar extends JMenuBar implements PreferenceChangeListener {

	private final FIBEditor fibEditor;

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

	private JFrame frame;

	public FIBEditorMenuBar(FIBEditor aFIBEditor, JFrame aFrame) {
		this.fibEditor = aFIBEditor;
		this.frame = aFrame;
		fileMenu = new JMenu(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "file"));
		editMenu = new JMenu(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "edit"));
		toolsMenu = new JMenu(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "tools"));
		helpMenu = new JMenu(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "help"));

		newItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "new_component"));
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.newFIB(frame);
			}
		});

		loadItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "open_component"));
		loadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.loadFIB(frame);
			}
		});

		openRecent = new JMenu(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "open_recent"));
		JFIBPreferences.addPreferenceChangeListener(this);
		updateOpenRecent();
		saveItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "save_component"));
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.saveFIB(frame);
			}
		});

		saveAsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "save_component_as"));
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.saveFIBAs(frame);
			}
		});

		closeItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "close"));
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.closeFIB(frame);
			}
		});

		quitItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "quit"));
		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.quit();
			}
		});
		testInterfaceItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "test_component"));
		testInterfaceItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.testFIB(frame);
			}
		});

		componentLocalizationItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "component_localization"));
		componentLocalizationItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.localizeFIB(frame);
			}
		});

		final JMenu lafsItem = new JMenu(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "look_and_feel"));
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
		languagesItem = new JMenu(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "switch_to_language"));
		for (Language lang : Language.availableValues()) {
			JMenuItem languageItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, lang.getName()));
			final Language language = lang;
			languageItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FIBEditorMenuBar.this.fibEditor.switchToLanguage(language);
				}
			});
			languagesItem.add(languageItem);
		}
		fileMenu.add(languagesItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);

		inspectItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "inspect"));
		inspectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.getInspector().setVisible(true);
			}
		});
		inspectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				ToolBox.getPLATFORM() != ToolBox.MACOS ? InputEvent.CTRL_MASK : InputEvent.META_MASK));
		showPaletteItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "show_palette"));
		showPaletteItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.getPalettes().setVisible(true);
			}
		});
		logsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "logs"));
		logsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), FIBEditor.APP_FIB_LIBRARY, frame);
			}
		});

		localizedItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "localized_editor"));
		localizedItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (FIBEditorMenuBar.this.fibEditor.localizedEditor == null) {
					FIBEditorMenuBar.this.fibEditor.localizedEditor = new LocalizedEditor(frame, "localized_editor", FIBEditor.LOCALIZATION,
							FIBEditor.LOCALIZATION, FIBEditor.APP_FIB_LIBRARY, true, false);
				}
				FIBEditorMenuBar.this.fibEditor.localizedEditor.setVisible(true);
			}
		});

		displayFileItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "display_file"));
		displayFileItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditor.logger.info("Getting this "
						+ fibEditor.getEditedFIB().getFactory().stringRepresentation(fibEditor.getEditedFIB().getFIBComponent()));
			}
		});

		componentValidationItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "validate_component"));
		componentValidationItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FIBEditorMenuBar.this.fibEditor.validateFIB(frame);
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

		frame.setJMenuBar(this);
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
					FIBEditorMenuBar.this.fibEditor.loadFIB(file, frame);
				}
			});
			openRecent.add(item);
		}
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

}
