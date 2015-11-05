/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.fib.model.converter;

import java.util.logging.Logger;

import org.openflexo.fib.model.container.BorderLayoutConstraints;
import org.openflexo.fib.model.container.BoxLayoutConstraints;
import org.openflexo.fib.model.container.ComponentConstraints;
import org.openflexo.fib.model.container.FlowLayoutConstraints;
import org.openflexo.fib.model.container.GridBagLayoutConstraints;
import org.openflexo.fib.model.container.GridLayoutConstraints;
import org.openflexo.fib.model.container.SplitLayoutConstraints;
import org.openflexo.fib.model.container.TwoColsLayoutConstraints;
import org.openflexo.fib.model.container.FIBPanel.Layout;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;

public class ComponentConstraintsConverter extends Converter<ComponentConstraints> {

	static final Logger logger = Logger.getLogger(ComponentConstraintsConverter.class.getPackage().getName());

	public ComponentConstraintsConverter() {
		super(ComponentConstraints.class);
	}

	@Override
	public ComponentConstraints convertFromString(String aValue, ModelFactory factory) {
		try {
			// System.out.println("aValue="+aValue);
			String constraintType = aValue.substring(0, aValue.indexOf("("));
			String someConstraints = aValue.substring(aValue.indexOf("(") + 1, aValue.length() - 1);
			// System.out.println("constraintType="+constraintType);
			// System.out.println("someConstraints="+someConstraints);
			if (constraintType.equals(Layout.border.name())) {
				return new BorderLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.flow.name())) {
				return new FlowLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.grid.name())) {
				return new GridLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.box.name())) {
				return new BoxLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.border.name())) {
				return new BorderLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.twocols.name())) {
				return new TwoColsLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.gridbag.name())) {
				return new GridBagLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.split.name())) {
				return new SplitLayoutConstraints(someConstraints);
			}
		} catch (StringIndexOutOfBoundsException e) {
			logger.warning("Syntax error in ComponentConstraints: " + aValue);
		}
		return null;
	}

	@Override
	public String convertToString(ComponentConstraints value) {
		if (value == null) {
			return null;
		}
		return value.getStringRepresentation();
	};
}
