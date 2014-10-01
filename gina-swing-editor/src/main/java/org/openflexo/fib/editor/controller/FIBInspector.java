/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.editor.controller;

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

	public void appendSuperInspectors(FIBInspectorController controller);

	public FIBTabPanel getTabPanel();

	public static abstract class FIBInspectorImpl extends FIBPanelImpl implements FIBInspector {

		private boolean superInspectorWereAppended = false;

		@Override
		public void appendSuperInspectors(FIBInspectorController controller) {

			if (!superInspectorWereAppended) {

				if (getDataType() == null) {
					return;
				}
				if (getDataType() instanceof Class) {
					FIBInspector superInspector = controller.inspectorForClass(((Class) getDataType()).getSuperclass());
					if (superInspector != null) {
						superInspector.appendSuperInspectors(controller);
						appendSuperInspector(superInspector, controller.INSPECTOR_FACTORY);
					}
					for (Class superInterface : ((Class) getDataType()).getInterfaces()) {
						FIBInspector superInterfaceInspector = controller.inspectorForClass(superInterface);
						if (superInterfaceInspector != null) {
							superInterfaceInspector.appendSuperInspectors(controller);
							appendSuperInspector(superInterfaceInspector, controller.INSPECTOR_FACTORY);
						}
					}
				}
				superInspectorWereAppended = true;
			}

		}

		@Override
		public String toString() {
			return "Inspector[" + getDataType() + "]";
		}

		protected void appendSuperInspector(FIBInspector superInspector, FIBModelFactory factory) {
			// System.out.println(">>>>>>> BEGIN Append " + superInspector.getDataClass() + " to " + this.getDataClass());
			// System.out.println("Append "+superInspector+" to "+this);

			/*if (superInspector.getDataType().equals(FIBWidget.class) && getDataType().equals(FIBTextField.class)) {
				System.out.println("was:");
				System.out.println(factory.stringRepresentation(((FIBContainer) this.getSubComponentNamed("Tab"))
						.getSubComponentNamed("ControlsTab")));
			}*/

			//System.out.println(factory.stringRepresentation(superInspector));

			// TODO: i dont't know if this clone is still required (check this)
			// FIBInspector clonedSuperInspector = (FIBInspector) superInspector.cloneObject();
			FIBInspector clonedSuperInspector = superInspector;

			/*if (superInspector.getDataType().equals(FIBWidget.class) && getDataType().equals(FIBTextField.class)) {
				System.out.println("to be appened:");
				System.out.println(factory.stringRepresentation(((FIBContainer) clonedSuperInspector.getSubComponentNamed("Tab"))
						.getSubComponentNamed("ControlsTab")));
			}*/

			append(clonedSuperInspector);

			/*if (superInspector.getDataType().equals(FIBWidget.class) && getDataType().equals(FIBTextField.class)) {
				System.out.println("now:");
				System.out.println(factory.stringRepresentation(((FIBContainer) this.getSubComponentNamed("Tab"))
						.getSubComponentNamed("ControlsTab")));
				// System.exit(-1);
			}*/
			// System.out.println("<<<<<<< END Append " + superInspector.getDataClass() + " to " + this.getDataClass());

		}

		@Override
		public FIBTabPanel getTabPanel() {
			return (FIBTabPanel) getSubComponents().get(0);
		}

		public String getXMLRepresentation() {
			// TODO: we use here the default factory !!!
			return getFactory().stringRepresentation(this);
		}
	}
}
