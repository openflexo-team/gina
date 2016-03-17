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

package org.openflexo.gina.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBVariable;
import org.openflexo.gina.view.widget.FIBReferencedComponentWidget;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizationListener;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Represent the "view" associated with a {@link FIBComponent} in a given rendering engine environment (eg Swing)<br>
 * 
 * A default implementation is provided in this library, see {@link FIBViewImpl}
 * 
 * @author sylvain
 *
 * @param <M>
 *            type of {@link FIBComponent} this view represents
 * @param <C>
 *            type of technology-specific component this view manage
 */
public interface FIBView<M extends FIBComponent, C> extends LocalizationListener, HasPropertyChangeSupport, PropertyChangeListener {

	public static final String DATA = "data";
	public static final String VISIBLE = "visible";

	public static final String DELETED_PROPERTY = "Deleted";

	/**
	 * Return the controller that manages this view
	 * 
	 * @return
	 */
	public FIBController getController();

	/**
	 * Return value of supplied variable
	 * 
	 * @param variable
	 * @return
	 */
	public <T> T getVariableValue(FIBVariable<T> variable);

	/**
	 * Sets value of supplied variable
	 * 
	 * @param variable
	 * @param value
	 */
	public <T> void setVariableValue(FIBVariable<T> variable, T value);

	/**
	 * Return boolean indicating if this view is visible
	 * 
	 * @return
	 */
	public boolean isViewVisible();

	/**
	 * Return the BindingEvaluationContext valid in the context of current widget.<br>
	 * Note that embedded component (components used in the context of FIBReferencedComponent) should point to the BindingEvaluationContext
	 * of their embedding component
	 * 
	 * @return
	 */
	public BindingEvaluationContext getBindingEvaluationContext();

	public Object getDataObject();

	// TODO renamed to getFIBComponent()
	public M getComponent();

	public void updateLanguage();

	/**
	 * Return technology-specific component representing widget<br>
	 * Note that, depending on the underlying technology, this technology-specific component might be embedded in an other component before
	 * to be added in component hierarchy (for example if component need to be embedded in a scroll pane)
	 * 
	 * @return C
	 */
	public abstract C getTechnologyComponent();

	/**
	 * Return technology component for supplied FIBComponent<br>
	 * Search is deeply performed inside the whole component hierarchy
	 * 
	 * @param component
	 * @return
	 */
	public Object getTechnologyComponentForFIBComponent(FIBComponent component);

	/**
	 * This method is called to update view representing a {@link FIBComponent}.<br>
	 * Usually, this method should be called only once, when the component has been added to the whole hierarchy.
	 * 
	 * @return a flag indicating if component has been updated
	 */
	public boolean update();

	public boolean isComponentVisible();

	public FIBContainerView<?, ?, ?> getParentView();

	public Font getFont();

	public void updateFont();

	public String getLocalized(String key);

	@Override
	public void languageChanged(Language language);

	// public void updateGraphicalProperties();

	public FIBReferencedComponentWidget<C> getEmbeddingComponent();

	public void setEmbeddingComponent(FIBReferencedComponentWidget<C> embeddingComponent);

	public void delete();

	public void requestFocus();

	public void requestFocusInWindow();

	public void requestFocusInParent();

	public RenderingAdapter<C> getRenderingAdapter();

	public boolean isDeleted();

	/**
	 * Specification of an adapter for a given rendering technology (eg Swing)
	 * 
	 * @author sylvain
	 *
	 * @param <C>
	 */
	public static interface RenderingAdapter<C> {

		public boolean isVisible(C component);

		public void setVisible(C component, boolean visible);

		public boolean getEnable(C component);

		public void setEnabled(C component, boolean enabled);

		public int getWidth(C component);

		public int getHeight(C component);

		public Dimension getPreferredSize(C component);

		public void setPreferredSize(C component, Dimension size);

		public Dimension getMinimumSize(C component);

		public void setMinimumSize(C component, Dimension size);

		public Dimension getMaximumSize(C component);

		public void setMaximumSize(C component, Dimension size);

		public Color getForegroundColor(C component);

		public void setForegroundColor(C component, Color aColor);

		public Color getBackgroundColor(C component);

		public void setBackgroundColor(C component, Color aColor);

		public Font getFont(C component);

		public void setFont(C component, Font aFont);

		public String getToolTipText(C component);

		public void setToolTipText(C component, String aText);

		public boolean isOpaque(C component);

		public void setOpaque(C component, boolean opaque);

		public void requestFocus(C component);

		public void requestFocusInWindow(C component);

		public void requestFocusInParent(C component);

		public void repaint(C component);

		public void revalidateAndRepaint(C component);

		public boolean newFocusedComponentIsDescendingFrom(C technologyComponent, FocusEvent event);
	}

}
