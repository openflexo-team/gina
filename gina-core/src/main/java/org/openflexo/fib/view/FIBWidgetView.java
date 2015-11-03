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

package org.openflexo.fib.view;

import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBMouseEvent;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.impl.FIBContainerViewImpl;
import org.openflexo.gina.event.GenericEventPerformer;
import org.openflexo.gina.event.GinaEventNotifier;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBEventDescription;
import org.openflexo.gina.manager.HasEventNotifier;

/**
 * Represent the "view" associated with a {@link FIBWidget} in a given rendering engine environment (eg Swing)<br>
 * A {@link FIBWidgetView} is a widget representing a particular data of type T<br>
 * Note that this view does not contains any sub-view
 * 
 * A default implementation is provided in this library, see {@link FIBContainerViewImpl}
 * 
 * @author sylvain
 *
 * @param <M>
 *            type of {@link FIBComponent} this view represents
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of data beeing represented by this view
 */
public interface FIBWidgetView<M extends FIBWidget, C, T>
		extends FIBView<M, C>, PropertyChangeListener, GenericEventPerformer<FIBEventDescription>, HasEventNotifier<FIBEventDescription> {

	public T getValue();

	public T getData();

	public void setData(T data);

	public boolean hasValue();

	public void updateData();

	@Override
	public GinaEventNotifier<FIBEventDescription> getNotifier();

	public void lockListening();

	public void allowListening();

	public boolean isListeningLocked();

	@Override
	public void executeEvent(EventDescription e);

	public M getWidget();

	/**
	 * Update the widget retrieving data from the model. This method is called when the observed property change.
	 * 
	 * @return boolean indicating if changes were required or not
	 */
	public boolean updateWidgetFromModel();

	/**
	 * Update the model given the actual state of the widget
	 * 
	 * @return boolean indicating if changes were required or not
	 */
	public boolean updateModelFromWidget();

	public boolean isReadOnly();

	public boolean isWidgetEnabled();

	public String getStringRepresentation(final Object value);

	public Icon getIconRepresentation(final Object value);

	/**
	 * Semantics of this method is not trivial: the goal is to aggregate some notifications within a given time (supplied as a
	 * aggregationTimeOut), to do it only once.<br>
	 * Within this delay, all requests to this method will simply reinitialize time-out, and will be ignored. Only the first call will be
	 * executed in a new thread which will die immediately after.
	 * 
	 * @param r
	 *            runnable to run after last request + timeout
	 * @param aggregationTimeOut
	 *            in milliseconds
	 */
	public void invokeLater(final Runnable r, final long aggregationTimeOut);

	/**
	 * Called after the platform-specific controller has detected a mouse event matching a single-click action<br>
	 * 
	 * @param event
	 *            platform-specific event which is translated into a generic FIBMouseEvent
	 */
	public void applySingleClickAction(FIBMouseEvent event);

	/**
	 * Called after the platform-specific controller has detected a mouse event matching a double-click action<br>
	 * 
	 * @param event
	 *            platform-specific event which is translated into a generic FIBMouseEvent
	 */
	public void applyDoubleClickAction(FIBMouseEvent event);

	/**
	 * Called after the platform-specific controller has detected a mouse event matching a right-click action<br>
	 * 
	 * @param event
	 *            platform-specific event which is translated into a generic FIBMouseEvent
	 */
	public void applyRightClickAction(FIBMouseEvent event);

}
