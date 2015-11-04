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

import java.awt.Font;

import javax.swing.JComponent;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.impl.FIBViewImpl;
import org.openflexo.fib.view.widget.FIBReferencedComponentWidget;
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
public interface FIBView<M extends FIBComponent, C> extends LocalizationListener, HasPropertyChangeSupport {

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
	 * Return the effective base component to be added to swing hierarchy This component may be encapsulated in a JScrollPane
	 * 
	 * @return JComponent
	 */
	public abstract JComponent getJComponent();

	/**
	 * Return technology-specific component representing widget<br>
	 * Note that, depending on the underlying technology, this technology-specific component might be embedded in an other component before
	 * to be added in component hierarchy (for example if component need to be embedded in a scroll pane)
	 * 
	 * @return C
	 */
	public abstract C getTechnologyComponent();

	/**
	 * Return the effective component to be added to swing hierarchy This component may be the same as the one returned by
	 * {@link #getJComponent()} or a encapsulation in a JScrollPane
	 * 
	 * @return JComponent
	 */
	public JComponent getResultingJComponent();

	/**
	 * Return technology component for supplied FIBComponent<br>
	 * Search is deeply performed inside the whole component hierarchy
	 * 
	 * @param component
	 * @return
	 */
	public Object getTechnologyComponentForFIBComponent(FIBComponent component);

	/**
	 * This method is called to update view representing a FIBComponent.<br>
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

	public void updateGraphicalProperties();

	public FIBReferencedComponentWidget<C> getEmbeddingComponent();

	public void setEmbeddingComponent(FIBReferencedComponentWidget<C> embeddingComponent);

	public void delete();

	public RenderingAdapter<C> getRenderingAdapter();

	/**
	 * Specification of an adapter for a given rendering technology (eg Swing)
	 * 
	 * @author sylvain
	 *
	 * @param <C>
	 */
	public static interface RenderingAdapter<C> {

		public void setOpaque(C component, boolean opaque);

		public void setFont(C component, Font aFont);

		public void setToolTipText(C component, String aText);

		public void requestFocus(C component);

		public void requestFocusInParent(C component);

	}

}
