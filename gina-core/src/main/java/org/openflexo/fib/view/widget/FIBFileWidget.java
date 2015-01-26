/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBFile;
import org.openflexo.fib.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit a File, or a String representing a File or a StringConvertable object
 * 
 * @author sguerin
 */
public class FIBFileWidget extends FIBWidgetView<FIBFile, JTextField, File> {

	static final Logger LOGGER = Logger.getLogger(FIBFileWidget.class.getPackage().getName());

	protected JPanel fileChooserPanel;

	protected JButton chooseButton;

	protected JTextField currentDirectoryLabel;

	protected File _file = null;

	protected FIBFile.FileMode mode;
	protected String filter;
	protected String title;
	protected Boolean isDirectory;
	protected File defaultDirectory;
	protected int columns;

	private static final int DEFAULT_COLUMNS = 10;

	public FIBFileWidget(FIBFile model, FIBController controller) {
		super(model, controller);

		mode = model.getMode() != null ? model.getMode() : FIBFile.FileMode.OpenMode;
		filter = model.getFilter();
		title = model.getTitle();
		isDirectory = model.isDirectory();
		defaultDirectory = model.getDefaultDirectory() != null ? model.getDefaultDirectory() : new File(System.getProperty("user.dir"));

		fileChooserPanel = new JPanel(new BorderLayout());
		fileChooserPanel.setOpaque(false);
		chooseButton = new JButton();
		chooseButton.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "choose", chooseButton));
		addActionListenerToChooseButton();
		currentDirectoryLabel = new JTextField("");
		currentDirectoryLabel.setColumns(model.getColumns() != null ? model.getColumns() : DEFAULT_COLUMNS);
		currentDirectoryLabel.setMinimumSize(MINIMUM_SIZE);
		currentDirectoryLabel.setPreferredSize(MINIMUM_SIZE);
		currentDirectoryLabel.setEditable(false);
		currentDirectoryLabel.setEnabled(true);
		currentDirectoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		fileChooserPanel.add(currentDirectoryLabel, BorderLayout.CENTER);
		fileChooserPanel.add(chooseButton, BorderLayout.EAST);
		fileChooserPanel.addFocusListener(this);
		if (!ToolBox.isMacOSLaf()) {
			fileChooserPanel.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER,
					BOTTOM_COMPENSATING_BORDER, RIGHT_COMPENSATING_BORDER));
		}
		setFile(null);

	}

	@Override
	public void updateFont() {
		super.updateFont();
		if (getFont() != null) {
			chooseButton.setFont(getFont());
		}
	}

	protected void configureFileChooser(FlexoFileChooser chooser) {
		if (!isDirectory) {
			// System.out.println("Looking for files");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogTitle(StringUtils.isEmpty(title) ? FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION,
					"select_a_file") : FlexoLocalization.localizedForKey(getController().getLocalizerForComponent(getWidget()), title));
			chooser.setFileFilterAsString(filter);
			chooser.setDialogType(mode.getMode());
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
		} else {
			// System.out.println("Looking for directories");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogTitle(StringUtils.isEmpty(title) ? FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION,
					"select_directory") : FlexoLocalization.localizedForKey(getController().getLocalizerForComponent(getWidget()), title));
			chooser.setFileFilterAsString(filter);
			chooser.setDialogType(mode.getMode());
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
		}
	}

	final public void addActionListenerToChooseButton() {
		chooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Window parent = SwingUtilities.getWindowAncestor(chooseButton);
				// get destination directory
				FlexoFileChooser chooser = new FlexoFileChooser(parent);
				if (_file != null) {
					chooser.setCurrentDirectory(_file);
					if (!_file.isDirectory()) {
						chooser.setSelectedFile(_file);
					}
				}
				configureFileChooser(chooser);

				switch (mode) {
				case OpenMode:
					if (chooser.showOpenDialog(chooseButton) == JFileChooser.APPROVE_OPTION) {
						// a dir has been picked...

						try {
							setFile(chooser.getSelectedFile());
							updateModelFromWidget();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else {
						// cancelled, return.
					}
					break;

				case SaveMode:
					if (chooser.showSaveDialog(chooseButton) == JFileChooser.APPROVE_OPTION) {
						// a dir has been picked...
						try {
							setFile(chooser.getSelectedFile());
							updateModelFromWidget();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else {
						// cancelled, return.
					}
					break;

				default:
					break;
				}
			}
		});
	}

	public void performUpdate(Object newValue) {
		if (newValue instanceof File) {
			setFile((File) newValue);
		} else if (newValue instanceof String) {
			setFile(new File((String) newValue));
		}
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (notEquals(getValue(), _file)) {
			widgetUpdating = true;
			if (getValue() instanceof File) {
				setFile(getValue());
			} else if (getValue() == null) {
				setFile(null);
			}
			widgetUpdating = false;
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), _file)) {
			modelUpdating = true;
			setValue(_file);
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public JPanel getJComponent() {
		return fileChooserPanel;
	}

	@Override
	public JTextField getDynamicJComponent() {
		return currentDirectoryLabel;
	}

	protected void setFile(File aFile) {
		_file = aFile;
		if (_file != null) {
			currentDirectoryLabel.setEnabled(true);
			currentDirectoryLabel.setText(_file.getAbsolutePath());
			currentDirectoryLabel.setToolTipText(_file.getAbsolutePath());
		} else {
			currentDirectoryLabel.setEnabled(false);
			currentDirectoryLabel.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "no_file"));
		}
	}

}
