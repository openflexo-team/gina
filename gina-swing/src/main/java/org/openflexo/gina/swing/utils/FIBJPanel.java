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
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomComponent;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This is the default implementation for a Swing panel defined using a FIB model<br>
 * This class is an instanceof JPanel in which Swing FIB instantiation is performed
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of object beeing represented as 'data' in FIB
 */
@SuppressWarnings("serial")
public abstract class FIBJPanel<T> extends JPanel implements FIBCustomComponent<T>, HasPropertyChangeSupport {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(FIBJPanel.class.getPackage().getName());

	private final Vector<ApplyCancelListener> applyCancelListener;

	private T editedObject;

	protected FIBComponent fibComponent;
	protected JFIBView<?, ?> fibView;
	protected FIBController controller;
	protected LocalizedDelegate localizer;

	private PropertyChangeSupport pcSupport;

	public FIBJPanel(FIBComponent component, T editedObject, LocalizedDelegate parentLocalizer) {
		super();
		localizer = parentLocalizer;
		setOpaque(false);
		pcSupport = new PropertyChangeSupport(this);

		this.editedObject = editedObject;

		fibComponent = component;
		controller = makeFIBController(fibComponent, parentLocalizer);
		fibView = (JFIBView<?, ?>) controller.buildView(fibComponent, null, true);

		controller.setDataObject(editedObject);

		setLayout(new BorderLayout());
		JComponent resultingJComponent = fibView.getResultingJComponent();
		add(resultingJComponent, BorderLayout.CENTER);

		applyCancelListener = new Vector<>();
	}

	public FIBJPanel(Resource fibFileName, T editedObject, FIBLibrary fibLibrary, LocalizedDelegate parentLocalizer) {
		this(fibLibrary.retrieveFIBComponent(fibFileName, true), editedObject, parentLocalizer);
	}

	/*
	 * public FIBJPanel(File fibFile, T editedObject, LocalizedDelegate
	 * parentLocalizer) {
	 * this(FIBLibrary.instance().retrieveFIBComponent(fibFile), editedObject,
	 * parentLocalizer); }
	 */
	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	protected FIBController makeFIBController(FIBComponent fibComponent, LocalizedDelegate parentLocalizer) {
		return FIBController.instanciateController(fibComponent, SwingViewFactory.INSTANCE, parentLocalizer);
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void addApplyCancelListener(ApplyCancelListener l) {
		applyCancelListener.add(l);
	}

	@Override
	public void removeApplyCancelListener(ApplyCancelListener l) {
		applyCancelListener.remove(l);
	}

	@Override
	public T getEditedObject() {
		return editedObject;
	}

	@Override
	public void setEditedObject(T object) {
		if ((editedObject == null && object != null) || (editedObject != null && !editedObject.equals(object))) {
			editedObject = object;
			fireEditedObjectChanged();
		}
	}

	public void fireEditedObjectChanged() {
		controller.setDataObject(editedObject, true);
	}

	@Override
	public T getRevertValue() {
		// Not implemented here, implement in sub-classes
		return getEditedObject();
	}

	@Override
	public void setRevertValue(T oldValue) {
		// Not implemented here, implement in sub-classes
	}

	public FIBComponent getFIBComponent() {
		return fibComponent;
	}

	public FIBController getController() {
		return controller;
	}
}
