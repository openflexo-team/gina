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

package org.openflexo.gina.model.container;

import java.util.Vector;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBTabPanel.FIBTabPanelImpl.class)
@XMLElement(xmlTag = "TabPanel")
public interface FIBTabPanel extends FIBContainer {

	@PropertyIdentifier(type = boolean.class)
	public static final String RESTRICT_PREFERRED_SIZE_TO_SELECTED_COMPONENT_KEY = "restrictPreferredSizeToSelectedComponent";

	@Getter(value = RESTRICT_PREFERRED_SIZE_TO_SELECTED_COMPONENT_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isRestrictPreferredSizeToSelectedComponent();

	@Setter(RESTRICT_PREFERRED_SIZE_TO_SELECTED_COMPONENT_KEY)
	public void setRestrictPreferredSizeToSelectedComponent(boolean restrictPreferredSizeToSelectedComponent);

	public static abstract class FIBTabPanelImpl extends FIBContainerImpl implements FIBTabPanel {

		private boolean restrictPreferredSizeToSelectedComponent = false;

		public FIBTabPanelImpl() {
		}

		/*@Override
		public String getIdentifier() {
			return null;
		}*/

		public Vector<FIBTab> getTabs() {
			Vector<FIBTab> returned = new Vector<FIBTab>();
			for (FIBComponent subComponent : getSubComponents()) {
				if (subComponent instanceof FIBTab) {
					returned.add((FIBTab) subComponent);
				}
			}
			return returned;
		}

		@Override
		public boolean isRestrictPreferredSizeToSelectedComponent() {
			return restrictPreferredSizeToSelectedComponent;
		}

		@Override
		public void setRestrictPreferredSizeToSelectedComponent(boolean restrictPreferredSizeToSelectedComponent) {
			this.restrictPreferredSizeToSelectedComponent = restrictPreferredSizeToSelectedComponent;
			getPropertyChangeSupport().firePropertyChange("restrictPreferredSizeToSelectedComponent",
					!restrictPreferredSizeToSelectedComponent, restrictPreferredSizeToSelectedComponent);
		}

	}
}
