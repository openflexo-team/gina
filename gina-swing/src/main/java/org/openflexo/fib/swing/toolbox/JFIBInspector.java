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

package org.openflexo.fib.swing.toolbox;

import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(JFIBInspector.JFIBInspectorImpl.class)
@XMLElement(xmlTag = "Inspector")
public interface JFIBInspector extends FIBPanel {

	public void appendSuperInspectors(JFIBInspectorController controller);

	public FIBTabPanel getTabPanel();

	public static abstract class JFIBInspectorImpl extends FIBPanelImpl implements JFIBInspector {

		private boolean superInspectorWereAppended = false;

		@Override
		public void appendSuperInspectors(JFIBInspectorController controller) {

			if (!superInspectorWereAppended) {

				if (getDataType() == null) {
					return;
				}
				if (getDataType() instanceof Class) {
					JFIBInspector superInspector = controller.inspectorForClass(((Class) getDataType()).getSuperclass());
					if (superInspector != null) {
						superInspector.appendSuperInspectors(controller);
						appendSuperInspector(superInspector, controller.INSPECTOR_FACTORY);
					}
					for (Class superInterface : ((Class) getDataType()).getInterfaces()) {
						JFIBInspector superInterfaceInspector = controller.inspectorForClass(superInterface);
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

		protected void appendSuperInspector(JFIBInspector superInspector, FIBModelFactory factory) {
			// System.out.println(">>>>>>> BEGIN Append " + superInspector.getDataClass() + " to " + this.getDataClass());
			// System.out.println("Append "+superInspector+" to "+this);

			/*if (superInspector.getDataType().equals(FIBWidget.class) && getDataType().equals(FIBTextField.class)) {
				System.out.println("was:");
				System.out.println(factory.stringRepresentation(((FIBContainer) this.getSubComponentNamed("Tab"))
						.getSubComponentNamed("ControlsTab")));
			}*/

			//System.out.println(factory.stringRepresentation(superInspector));

			// TODO: i dont't know if this clone is still required (check this)
			// JFIBInspector clonedSuperInspector = (JFIBInspector) superInspector.cloneObject();
			JFIBInspector clonedSuperInspector = superInspector;

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
