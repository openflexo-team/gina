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

package org.openflexo.gina.model.graph;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;

/**
 * Represents a 2D-base polar graph [r=f(A)] representing functions where:
 * <ul>
 * <li>angle is iterated over continuous or discrete values, expressed as radian angles between 0 and 360 degrees</li>
 * <li>radius is based on an expression using angle (iterated value, which can be discrete or continuous)</li><br>
 * </ul>
 * 
 * Such graphs allows only one parameter which is angle of the polar graph
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(FIBPolarFunctionGraph.FIBPolarFunctionGraphImpl.class)
public interface FIBPolarFunctionGraph extends FIBSingleParameteredGraph {

	@PropertyIdentifier(type = Boolean.class)
	public static final String DISPLAY_REFERENCE_MARKS_KEY = "displayReferenceMarks";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DISPLAY_LABELS_KEY = "displayLabels";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DISPLAY_GRID_KEY = "displayGrid";

	@Getter(value = DISPLAY_REFERENCE_MARKS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDisplayReferenceMarks();

	@Setter(DISPLAY_REFERENCE_MARKS_KEY)
	public void setDisplayReferenceMarks(boolean displayReferenceMarks);

	@Getter(value = DISPLAY_LABELS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDisplayLabels();

	@Setter(DISPLAY_LABELS_KEY)
	public void setDisplayLabels(boolean displayLabels);

	@Getter(value = DISPLAY_GRID_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getDisplayGrid();

	@Setter(DISPLAY_GRID_KEY)
	public void setDisplayGrid(boolean displayGrid);

	public static abstract class FIBPolarFunctionGraphImpl extends FIBSingleParameteredGraphImpl implements FIBPolarFunctionGraph {

	}
}
