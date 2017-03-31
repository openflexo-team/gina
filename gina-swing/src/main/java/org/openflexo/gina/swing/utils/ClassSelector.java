/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

package org.openflexo.gina.swing.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomComponent;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.swing.VerticalLayout;

/**
 * Widget allowing to select a class in a popup panel
 * 
 * @author sguerin
 * 
 */
public class ClassSelector extends TextFieldCustomPopup<Class> implements FIBCustomComponent<Class> {
	static final Logger LOGGER = Logger.getLogger(ClassSelector.class.getPackage().getName());

	public static Resource FIB_FILE_NAME = ResourceLocator.getResourceLocator().locateResource("Fib/ClassSelector.fib");

	private Class _revertValue;

	protected ClassSelectorDetailsPanel _selectorPanel;

	public ClassSelector(Class<?> editedObject) {
		super(editedObject);
		setRevertValue(editedObject);
		setFocusable(true);
	}

	@Override
	public void delete() {
		super.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
			_selectorPanel = null;
		}

	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void setRevertValue(Class oldValue) {
		// WARNING: we need here to clone to keep track back of previous data
		// !!!
		if (oldValue != null) {
			_revertValue = oldValue;
		}
		else {
			_revertValue = null;
		}
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public Class getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(Class editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected ClassSelectorDetailsPanel makeCustomPanel(Class<?> editedObject) {
		return new ClassSelectorDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(Class editedObject) {
		// logger.info("updateCustomPanel with "+editedObject+" _selectorPanel="+_selectorPanel);
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
	}

	public class ClassSelectorDetailsPanel extends ResizablePanel {
		private final FIBComponent fibComponent;
		private JFIBView<?, ?> fibView;
		private CustomFIBController controller;
		private ClassEditor classEditor;

		protected ClassSelectorDetailsPanel(Class<?> aClass) {
			super();

			fibComponent = ApplicationFIBLibraryImpl.instance().retrieveFIBComponent(FIB_FILE_NAME, true);
			controller = new CustomFIBController(fibComponent, SwingViewFactory.INSTANCE);
			fibView = (JFIBView<?, ?>) controller.buildView(fibComponent, true);

			classEditor = new ClassEditor(aClass);

			controller.setDataObject(classEditor);

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

		}

		public void delete() {
			controller.delete();
			fibView.delete();
			controller = null;
			fibView = null;
		}

		public void update() {
			classEditor.selectClass(getEditedObject());
			// controller.setDataObject(new LoadedClassesInfo(getEditedObject()));
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(300, 400);
		}

		public class CustomFIBController extends FIBController {
			public CustomFIBController(FIBComponent component, GinaViewFactory<?> viewFactory) {
				super(component, viewFactory);
			}

			public void apply() {
				// LoadedClassesInfo dataObject = (LoadedClassesInfo) getDataObject();
				setEditedObject(classEditor.getSelectedClassInfo().getRepresentedClass());
				ClassSelector.this.apply();
			}

			public void cancel() {
				ClassSelector.this.cancel();
			}

			public void reset() {
				setEditedObject(null);
				ClassSelector.this.apply();
			}

			public void classChanged() {
				System.out.println("Class changed !!!");
			}

		}

	}

	/*
	 * @Override public void setEditedObject(BackgroundStyle object) {
	 * logger.info("setEditedObject with "+object);
	 * super.setEditedObject(object); }
	 */

	@Override
	public void apply() {
		setRevertValue(getEditedObject());
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("CANCEL: revert to " + getRevertValue());
		}
		setEditedObject(getRevertValue());
		closePopup();
		super.cancel();
	}

	@Override
	protected void deletePopup() {
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	/*
	 * protected void pointerLeavesPopup() { cancel(); }
	 */

	public ClassSelectorDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	public Class<Class> getRepresentedType() {
		return Class.class;
	}

	@Override
	public String renderedString(Class editedObject) {
		if (editedObject == null) {
			return "";
		}
		return editedObject.getSimpleName();
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * FIBAbstractEditor editor = new FIBAbstractEditor() {
	 * 
	 * @Override public Object[] getData() { return
	 * FIBAbstractEditor.makeArray(new
	 * LoadedClassesInfo(java.lang.Object.class)); }
	 * 
	 * @Override public File getFIBFile() { return FIB_FILE; } };
	 * 
	 * editor.launch(); }
	 */

	/**
	 * This main allows to launch an application testing the TypeSelector
	 * 
	 * @param args
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SecurityException, IOException {

		Resource loggingFile = ResourceLocator.locateResource("Config/logging_INFO.properties");
		FlexoLoggingManager.initialize(-1, true, loggingFile, Level.INFO, null);
		final JDialog dialog = new JDialog((Frame) null, false);

		final ClassSelector selector = new ClassSelector(String.class) {
			@Override
			public void apply() {
				super.apply();
				System.out.println("Apply, getEditedObject()=" + getEditedObject());
			}

			@Override
			public void cancel() {
				super.cancel();
				System.out.println("Cancel, getEditedObject()=" + getEditedObject());
			}
		};
		selector.setRevertValue(Object.class);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selector.delete();
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), ApplicationFIBLibraryImpl.instance(), dialog);
			}
		});

		JPanel panel = new JPanel(new VerticalLayout());
		panel.add(selector);

		panel.add(closeButton);
		panel.add(logButton);

		dialog.setPreferredSize(new Dimension(550, 600));
		dialog.getContentPane().add(panel);
		dialog.pack();

		dialog.setVisible(true);
	}
}
