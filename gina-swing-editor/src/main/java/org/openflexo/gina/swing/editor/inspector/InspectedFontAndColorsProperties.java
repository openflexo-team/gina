/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.gina.swing.editor.inspector;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBProperty;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;

/**
 * Implementation of {@link ShadowStyle}, as a container of graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedFontAndColorsProperties extends InspectedProperties<FIBComponent> {

	public static FIBProperty<Color> FOREGROUND_COLOR = FIBProperty.getFIBProperty(FIBComponent.class, FIBComponent.FOREGROUND_COLOR_KEY,
			Color.class);

	public InspectedFontAndColorsProperties(FIBEditorController controller) {
		super(controller, null);
	}

	@Override
	public List<FIBComponent> getSelection() {
		return Collections.singletonList(getController().getSelectedObject());
	}

	@Override
	public FIBComponent getStyle(FIBModelObject object) {
		return object.getComponent();
	}

	/*@Override
	public FIBComponent getStyle(FIBModelObject object) {
		return node.getGraphicalRepresentation();
	*/

	/*public boolean areLocationPropertiesApplicable() {
		return getController().getSelectedShapes().size() > 0;
	}*/

	/*@Override
	protected void fireChangedProperties() {
		// We replace here super code, because we have to fire changed properties for all properties
		// as the union of properties of all possible types
		List<FIBProperty<?>> paramsList = new ArrayList<FIBProperty<?>>();
		paramsList.addAll(FIBProperty.getFIBProperties(DrawingGraphicalRepresentation.class));
		paramsList.addAll(FIBProperty.getFIBProperties(GeometricGraphicalRepresentation.class));
		paramsList.addAll(FIBProperty.getFIBProperties(ShapeGraphicalRepresentation.class));
		paramsList.addAll(FIBProperty.getFIBProperties(ConnectorGraphicalRepresentation.class));
		Set<FIBProperty<?>> allParams = new HashSet<FIBProperty<?>>(paramsList);
		for (FIBProperty<?> p : allParams) {
			fireChangedProperty(p);
		}
	}*/

	/*@Override
	public void fireSelectionUpdated() {
		super.fireSelectionUpdated();
		getPropertyChangeSupport().firePropertyChange("areLocationPropertiesApplicable", !areLocationPropertiesApplicable(),
				areLocationPropertiesApplicable());
	}*/

	public Color getForegroundColor() {
		return getPropertyValue(FOREGROUND_COLOR);
	}

	public void setForegroundColor(Color value) {
		setPropertyValue(FOREGROUND_COLOR, value);
	}

}
