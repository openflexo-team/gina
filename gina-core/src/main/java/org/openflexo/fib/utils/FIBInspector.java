/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.fib.utils;

import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBInspector.FIBInspectorImpl.class)
@XMLElement(xmlTag = "Inspector")
public interface FIBInspector extends FIBPanel {

	public void appendSuperInspectors(InspectorGroup inspectorGroup);

	public FIBTabPanel getTabPanel();

	public String getXMLRepresentation();

	public static abstract class FIBInspectorImpl extends FIBPanelImpl implements FIBInspector {

		// private final Map<InspectorGroup, Boolean> superInspectorWereAppended = new HashMap<InspectorGroup, Boolean>();
		private boolean superInspectorWereAppened = false;

		@Override
		public void appendSuperInspectors(InspectorGroup inspectorGroup) {

			/*Boolean alreadyDone = superInspectorWereAppended.get(inspectorGroup);
			if (alreadyDone == null) {
				alreadyDone = false;
			}

			if (!alreadyDone) {*/

			if (!superInspectorWereAppened) {

				if (getDataType() == null) {
					return;
				}
				if (getDataType() instanceof Class) {
					FIBInspector superInspector = inspectorGroup.inspectorForClass(((Class) getDataType()).getSuperclass());
					if (superInspector != null) {
						superInspector.appendSuperInspectors(inspectorGroup);
						appendSuperInspector(superInspector, inspectorGroup.getFIBModelFactory());
					}
					for (Class superInterface : ((Class) getDataType()).getInterfaces()) {
						FIBInspector superInterfaceInspector = inspectorGroup.inspectorForClass(superInterface);
						if (superInterfaceInspector != null) {
							superInterfaceInspector.appendSuperInspectors(inspectorGroup);
							appendSuperInspector(superInterfaceInspector, inspectorGroup.getFIBModelFactory());
						}
					}
				}
				// superInspectorWereAppended.put(inspectorGroup, true);
				superInspectorWereAppened = true;
			}

		}

		@Override
		public String toString() {
			return "Inspector[" + getDataType() + "]";
		}

		protected void appendSuperInspector(FIBInspector superInspector, FIBModelFactory factory) {
			// TODO: i dont't know if this clone is still required (check this)
			FIBInspector clonedSuperInspector = (FIBInspector) superInspector.cloneObject();
			// FIBInspector clonedSuperInspector = superInspector;

			append(clonedSuperInspector);

		}

		@Override
		public FIBTabPanel getTabPanel() {
			return (FIBTabPanel) getSubComponents().get(0);
		}

		@Override
		public String getXMLRepresentation() {
			// TODO: we use here the default factory !!!
			return getFactory().stringRepresentation(this);
		}
	}
}
