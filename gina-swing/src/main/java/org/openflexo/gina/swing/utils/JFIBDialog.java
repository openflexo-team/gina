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

package org.openflexo.gina.swing.utils;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.controller.FIBController.Status;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;

/**
 * A JDialog (Swing implementation of a Dialog) representing a given FIBComponent and a data<br>
 * 
 * This implementation contains many helpers methods used to manage this.
 * 
 * @author sylvain
 *
 * @param <T>
 *            type of data beeing represented by this dialog
 * 
 */
@SuppressWarnings("serial")
public class JFIBDialog<T> extends JDialog {

	private static final Logger LOGGER = Logger.getLogger(FIBController.class.getPackage().getName());

	private JFIBView<?, ?> view;

	public static <T> JFIBDialog<T> instanciateDialog(Resource componentFile, T data, FIBLibrary fibLibrary, Window frame, boolean modal) {
		return instanciateDialog(componentFile, data, fibLibrary, frame, modal, null);
	}

	public static <T> JFIBDialog<T> instanciateDialog(Resource componentFile, T data, FIBLibrary fibLibrary, Window frame, boolean modal,
			LocalizedDelegate localizer) {
		FIBComponent fibComponent = fibLibrary.retrieveFIBComponent(componentFile);
		if (fibComponent == null) {
			LOGGER.warning("FileNotFoundException: " + componentFile.getURI());
			return null;
		}
		return instanciateDialog(fibComponent, data, frame, modal, localizer);
	}

	public static <T> JFIBDialog<T> instanciateDialog(FIBComponent fibComponent, T data, Window frame, boolean modal,
			LocalizedDelegate localizer) {
		return new JFIBDialog<T>(fibComponent, data, frame, modal, localizer);
	}

	public static <T> JFIBDialog<T> instanciateDialog(FIBComponent fibComponent, T data, Window frame, boolean modal,
			FIBController controller) {
		return new JFIBDialog<T>(fibComponent, data, frame, modal, controller);
	}

	public static <T> JFIBDialog<T> instanciateAndShowDialog(FIBComponent fibComponent, T data, Window frame, boolean modal) {
		return instanciateAndShowDialog(fibComponent, data, frame, modal, (LocalizedDelegate) null);
	}

	public static <T> JFIBDialog<T> instanciateAndShowDialog(FIBComponent fibComponent, T data, Window frame, boolean modal,
			LocalizedDelegate localizer) {
		JFIBDialog<T> dialog = instanciateDialog(fibComponent, data, frame, modal, localizer);
		dialog.showDialog();
		return dialog;
	}

	public static <T> JFIBDialog<T> instanciateAndShowDialog(FIBComponent fibComponent, T data, Window frame, boolean modal,
			FIBController controller) {
		JFIBDialog<T> dialog = instanciateDialog(fibComponent, data, frame, modal, controller);
		dialog.showDialog();
		return dialog;
	}

	public static <T> JFIBDialog<T> instanciateAndShowDialog(Resource componentFile, T data, FIBLibrary fibLibrary, Window frame,
			boolean modal, LocalizedDelegate localizer) {
		FIBComponent fibComponent = fibLibrary.retrieveFIBComponent(componentFile);
		if (fibComponent == null) {
			LOGGER.warning("FileNotFoundException: " + componentFile.getURI());
			return null;
		}
		return instanciateAndShowDialog(fibComponent, data, frame, modal, localizer);
	}

	/*
	 * public static <T> FIBDialog<T> instanciateAndShowDialog(String
	 * fibResourcePath, T data, Window frame, boolean modal, LocalizedDelegate
	 * localizer) { FIBComponent fibComponent =
	 * FIBLibrary.instance().retrieveFIBComponent(fibResourcePath); if
	 * (fibComponent == null) { logger.warning("ResourceNotFoundException: " +
	 * fibResourcePath); return null; } return
	 * instanciateAndShowDialog(fibComponent, data, frame, modal, localizer); }
	 */

	protected JFIBDialog(FIBComponent fibComponent, T data, Window frame, boolean modal, LocalizedDelegate localizer) {
		this(frame, modal, fibComponent, localizer);
		getController().setDataObject(data);
	}

	protected JFIBDialog(FIBComponent fibComponent, T data, Window frame, boolean modal, FIBController controller) {
		this(frame, modal, fibComponent, controller);
		getController().setDataObject(data);
	}

	private JFIBDialog(Window window, boolean modal, FIBComponent fibComponent, LocalizedDelegate localizer) {
		super(window, fibComponent.getParameter("title"), modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
		initDialog(fibComponent, localizer);
	}

	private JFIBDialog(Window window, boolean modal, FIBComponent fibComponent, FIBController controller) {
		super(window, fibComponent.getParameter("title"), modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
		initDialog(fibComponent, controller);
	}

	public void initDialog(FIBComponent fibComponent, LocalizedDelegate localizer) {
		initDialog(fibComponent, makeFIBController(fibComponent, SwingViewFactory.INSTANCE, localizer));
	}

	protected FIBController makeFIBController(FIBComponent fibComponent, GinaViewFactory<?> viewFactory,
			LocalizedDelegate parentLocalizer) {
		return FIBController.instanciateController(fibComponent, viewFactory, parentLocalizer);
	}

	public void initDialog(FIBComponent fibComponent, FIBController controller) {
		getRootPane().registerKeyboardAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFIBDialog.this.dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		view = (JFIBView<?, ?>) FIBController.makeView(fibComponent, SwingViewFactory.INSTANCE, controller);
		JComponent resultingJComponent = view.getResultingJComponent();
		getContentPane().add(resultingJComponent);
		List<FIBButton> def = fibComponent.getDefaultButtons();
		boolean defaultButtonSet = false;
		if (def.size() > 0 && view instanceof FIBContainerViewImpl) {
			JButton button = (JButton) ((FIBContainerViewImpl<?, ?, ?>) view).getTechnologyComponentForFIBComponent(def.get(0));
			if (button != null) {
				getRootPane().setDefaultButton(button);
				defaultButtonSet = true;
			}
		}
		if (!defaultButtonSet) {
			// TODO: choose a button
		}
		pack();
	}

	public FIBController getController() {
		return view.getController();
	}

	public T getData() {
		return (T) getController().getDataObject();
	}

	public void setData(T value) {
		getController().setDataObject(value);
	}

	public void setData(T value, boolean force) {
		getController().setDataObject(value, force);
	}

	public Status getStatus() {
		return getController().getStatus();
	}

	/**
	 * @param flexoFrame
	 */
	public void center() {
		setLocationRelativeTo(getOwner());
	}

	public void showDialog() {
		pack();
		center();
		setVisible(true);
	}

}
