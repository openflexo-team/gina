/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

package org.openflexo.gina.model.widget;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.gina.model.FIBWidgetType;
import org.openflexo.gina.model.bindings.BrowserSelectedPathElement;
import org.openflexo.gina.model.bindings.WidgetPathElement;
import org.openflexo.gina.view.widget.FIBBrowserWidget;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent the type of a {@link FIBBrowserWidget} representing a given {@link FIBBrowser}<br>
 * Extends base {@link FIBWidgetType} by exposing "selected" dynamic property
 * 
 * @author sylvain
 * 
 */
public class FIBBrowserType extends FIBWidgetType<FIBBrowser> {

	protected static final Logger logger = FlexoLogger.getLogger(FIBBrowserType.class.getPackage().getName());

	public FIBBrowserType(FIBBrowser aWidget) {
		super(aWidget);
	}

	@Override
	public Class<FIBBrowserWidget> getBaseClass() {
		return FIBBrowserWidget.class;
	}

	@Override
	public List<WidgetPathElement<? super FIBBrowser>> getAccessibleSimplePathElements(BindingPathElement parent) {

		if (parent != null && parent.getType() instanceof FIBBrowserType) {

			List<WidgetPathElement<? super FIBBrowser>> returned = pathElements.get(parent);
			if (returned == null) {
				returned = super.getAccessibleSimplePathElements(parent);
				returned.add(new BrowserSelectedPathElement(parent, getFIBComponent()));
				pathElements.put(parent, returned);
			}
			return returned;
		}

		return super.getAccessibleSimplePathElements(parent);
	}

}
