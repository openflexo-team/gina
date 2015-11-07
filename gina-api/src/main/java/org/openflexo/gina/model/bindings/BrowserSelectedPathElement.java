/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina, a component of the software infrastructure 
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

package org.openflexo.gina.model.bindings;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.view.widget.FIBBrowserWidget;

/**
 * BindingPathElement representing "selected" dynamic property for {@link FIBTable}<br>
 * 
 * @author sylvain
 *
 */
public class BrowserSelectedPathElement extends WidgetPathElement<FIBBrowser> {

	private static final Logger logger = Logger.getLogger(BrowserSelectedPathElement.class.getPackage().getName());

	public static final String SELECTED = "selected";

	public BrowserSelectedPathElement(BindingPathElement parent, FIBBrowser browser) {
		super(parent, browser);
	}

	@Override
	protected String getDynamicPropertyName(FIBBrowser browser) {
		return SELECTED;
	}

	@Override
	protected Type getDynamicPropertyType(FIBBrowser browser) {
		return browser.getIteratorClass();
	}

	@Override
	protected String getDynamicPropertyTooltip(FIBBrowser browser) {
		return "value_being_currently_selected_in_browser";
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		if (target instanceof FIBBrowserWidget) {
			return ((FIBBrowserWidget<?, ?>) target).getSelected();
		}
		logger.warning("Unexpected target=" + target + " context=" + context);
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException {
		if (target instanceof FIBBrowserWidget) {
			((FIBBrowserWidget) target).performSelect(value, true);
			return;
		}
		logger.warning("Unexpected target=" + target + " context=" + context);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getWidget()) {
			if (evt.getPropertyName().equals(FIBBrowser.ITERATOR_CLASS_KEY)) {
				handleTypeMightHaveChanged();
			}
		}
		super.propertyChange(evt);
	}

}
