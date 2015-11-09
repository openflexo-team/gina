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

package org.openflexo.gina.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.model.widget.FIBFile;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBFileWidget.FileSelectorPanel;
import org.openflexo.gina.view.widget.impl.FIBFileWidgetImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * Swing implementation for a widget able to edit or select a File
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 *
 * @author sylvain
 */
public class JFIBFileWidget extends FIBFileWidgetImpl<FileSelectorPanel>implements FocusListener {

	static final Logger LOGGER = Logger.getLogger(JFIBFileWidget.class.getPackage().getName());

	protected int columns;
	private static final int DEFAULT_COLUMNS = 10;

	public static class FileSelectorPanel extends JPanel {

		protected File selectedFile = null;
		protected JButton chooseButton;
		protected JTextField currentDirectoryLabel;

		private final JFIBFileWidget widget;

		public FileSelectorPanel(JFIBFileWidget widget) {
			super(new BorderLayout());
			this.widget = widget;
			setOpaque(false);
			chooseButton = new JButton();
			chooseButton.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "choose", chooseButton));
			addActionListenerToChooseButton();
			currentDirectoryLabel = new JTextField("");
			currentDirectoryLabel.setColumns(widget.getWidget().getColumns() != null ? widget.getWidget().getColumns() : DEFAULT_COLUMNS);
			currentDirectoryLabel.setMinimumSize(MINIMUM_SIZE);
			currentDirectoryLabel.setPreferredSize(MINIMUM_SIZE);
			currentDirectoryLabel.setEditable(false);
			currentDirectoryLabel.setEnabled(true);
			currentDirectoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
			add(currentDirectoryLabel, BorderLayout.CENTER);
			add(chooseButton, BorderLayout.EAST);
			addFocusListener(widget);
			if (!ToolBox.isMacOSLaf()) {
				setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
						RIGHT_COMPENSATING_BORDER));
			}
		}

		protected void configureFileChooser(FlexoFileChooser chooser) {
			if (!widget.isDirectory) {
				// System.out.println("Looking for files");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setDialogTitle(StringUtils.isEmpty(widget.title)
						? FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "select_a_file")
						: FlexoLocalization.localizedForKey(widget.getController().getLocalizerForComponent(widget.getWidget()),
								widget.title));
				chooser.setFileFilterAsString(widget.filter);
				chooser.setDialogType(widget.mode.getMode());
				System.setProperty("apple.awt.fileDialogForDirectories", "false");
			}
			else {
				// System.out.println("Looking for directories");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle(StringUtils.isEmpty(widget.title)
						? FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "select_directory")
						: FlexoLocalization.localizedForKey(widget.getController().getLocalizerForComponent(widget.getWidget()),
								widget.title));
				chooser.setFileFilterAsString(widget.filter);
				chooser.setDialogType(widget.mode.getMode());
				System.setProperty("apple.awt.fileDialogForDirectories", "true");
			}
		}

		protected void addActionListenerToChooseButton() {
			chooseButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Window parent = SwingUtilities.getWindowAncestor(chooseButton);
					// get destination directory
					FlexoFileChooser chooser = new FlexoFileChooser(parent);
					if (selectedFile != null) {
						chooser.setCurrentDirectory(selectedFile);
						if (!selectedFile.isDirectory()) {
							chooser.setSelectedFile(selectedFile);
						}
					}
					configureFileChooser(chooser);

					switch (widget.mode) {
						case OpenMode:
							if (chooser.showOpenDialog(chooseButton) == JFileChooser.APPROVE_OPTION) {
								// a dir has been picked...

								try {
									widget.setSelectedFile(chooser.getSelectedFile());
									widget.updateModelFromWidget();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
							else {
								// cancelled, return.
							}
							break;

						case SaveMode:
							if (chooser.showSaveDialog(chooseButton) == JFileChooser.APPROVE_OPTION) {
								// a dir has been picked...
								try {
									widget.setSelectedFile(chooser.getSelectedFile());
									widget.updateModelFromWidget();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
							else {
								// cancelled, return.
							}
							break;

						default:
							break;
					}
				}
			});
		}

		public File getSelectedFile() {
			return selectedFile;
		}

		public void setSelectedFile(File aFile) {
			selectedFile = aFile;
			if (selectedFile != null) {
				currentDirectoryLabel.setEnabled(true);
				currentDirectoryLabel.setText(selectedFile.getAbsolutePath());
				currentDirectoryLabel.setToolTipText(selectedFile.getAbsolutePath());
			}
			else {
				currentDirectoryLabel.setEnabled(false);
				currentDirectoryLabel.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "no_file"));
			}
		}

		@Override
		public void setFont(Font font) {
			super.setFont(font);
			if (chooseButton != null) {
				chooseButton.setFont(getFont());
			}
		}

	}

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JLabel<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingFileWidgetRenderingAdapter extends SwingRenderingAdapter<FileSelectorPanel>
			implements FileWidgetRenderingAdapter<FileSelectorPanel> {

		@Override
		public File getSelectedFile(FileSelectorPanel component) {
			return component.getSelectedFile();
		}

		@Override
		public void setSelectedFile(FileSelectorPanel component, File aFile) {
			component.setSelectedFile(aFile);
		}

	}

	public static SwingFileWidgetRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingFileWidgetRenderingAdapter();

	public JFIBFileWidget(FIBFile model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	@Override
	protected FileSelectorPanel makeTechnologyComponent() {
		return new FileSelectorPanel(this);
	}

	@Override
	public JPanel getJComponent() {
		return getTechnologyComponent();
	}

}
