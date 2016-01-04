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

package org.openflexo.gina.utils;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * Extends {@link FIBPanel} by providing composition facilities using a multiple inheritance scheme based on inspected object type, for
 * inspectors contained in a {@link InspectorGroup}<br>
 * 
 * Super inspectors are automatically merged according to inspector layout.<br>
 * Merge is based on component names (package-merge scheme)
 * 
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(FIBInspector.FIBInspectorImpl.class)
@XMLElement(xmlTag = "Inspector")
public interface FIBInspector extends FIBPanel {

	public String getInspectorTitle();

	public void setInspectorTitle(String inspectorTitle);

	public Class<?> getInspectedClass();

	public void setInspectedClass(Class<?> inspectedClass);

	public void identifySuperInspectors(InspectorGroup inspectorGroup);

	public FIBInspector getUnmergedComponent();

	public List<FIBInspector> getSuperInspectors();

	public void mergeWithParentInspectors();

	public boolean isMerged();

	// public void appendSuperInspector(FIBInspector superInspector, FIBModelFactory factory);

	public FIBTabPanel getTabPanel();

	public String getXMLRepresentation();

	public static abstract class FIBInspectorImpl extends FIBPanelImpl implements FIBInspector {

		private static final String TITLE_KEY = "title";

		// private final Map<InspectorGroup, Boolean> superInspectorWereAppended = new HashMap<InspectorGroup, Boolean>();
		private boolean isMerged = false;

		private FIBInspector unmergedComponent;
		private final List<FIBInspector> superInspectors = new ArrayList<>();

		@Override
		public String getInspectorTitle() {
			return getParameter(TITLE_KEY);
		}

		@Override
		public void setInspectorTitle(String inspectorTitle) {
			setParameter(TITLE_KEY, inspectorTitle);
		}

		@Override
		public Class<?> getInspectedClass() {
			return getDataClass();
		}

		@Override
		public void setInspectedClass(Class<?> inspectedClass) {
			setDataClass(inspectedClass);
		}

		@Override
		public void identifySuperInspectors(InspectorGroup inspectorGroup) {

			superInspectors.clear();

			List<Class<?>> parentClasses = new ArrayList<Class<?>>();

			for (FIBInspector inspector : inspectorGroup.getAllAccessiblesInspectors()) {
				if (!inspector.getInspectedClass().equals(getInspectedClass())
						&& inspector.getInspectedClass().isAssignableFrom(getInspectedClass())) {
					parentClasses.add(inspector.getInspectedClass());
				}
			}

			TypeUtils.reduceToMostSpecializedClasses(parentClasses);

			// System.out.println("OK, donc pour " + getInspectedClass() + " j'ai comme parent " + parentClasses);

			for (Class<?> inspectorClass : parentClasses) {
				superInspectors.add(inspectorGroup.inspectorForClass(inspectorClass));
				// System.out.println("inspector " + inspectorGroup.inspectorForClass(inspectorClass).getInspectedClass());
			}

			// TODO: reinit current with container found in unmergedComponent
			// reset merge
			// isMerged = false;
		}

		@Override
		public FIBInspector getUnmergedComponent() {
			if (unmergedComponent == null) {
				return this;
			}
			return unmergedComponent;
		}

		@Override
		public List<FIBInspector> getSuperInspectors() {
			return superInspectors;
		}

		@Override
		public void mergeWithParentInspectors() {

			if (isMerged()) {
				return;
			}

			// unmergedComponent = (FIBInspector) cloneObject();

			for (FIBInspector superInspector : superInspectors) {
				superInspector.mergeWithParentInspectors();
				appendSuperInspector(superInspector);
			}

			isMerged = true;

		}

		private void appendSuperInspector(FIBInspector superInspector) {
			// TODO: i dont't know if this clone is still required (check this)
			FIBInspector clonedSuperInspector = (FIBInspector) superInspector.cloneObject();
			// FIBInspector clonedSuperInspector = superInspector;

			//System.out.println(">>>>> On append " + clonedSuperInspector.getInspectedClass() + " a " + getInspectedClass());

			append(clonedSuperInspector);

		}

		@Override
		public boolean isMerged() {
			return isMerged;
		}

		// @Override
		/*public void appendSuperInspectors(InspectorGroup inspectorGroup) {
		
			if (!isMerged) {
		
				if (getDataClass() == null) {
					return;
				}
				else {
					FIBInspector superInspector = inspectorGroup.inspectorForClass(((Class) getDataClass()).getSuperclass());
					if (superInspector != null) {
						superInspector.appendSuperInspectors(inspectorGroup);
						appendSuperInspector(superInspector, inspectorGroup.getFIBModelFactory());
					}
					for (Class superInterface : ((Class) getDataClass()).getInterfaces()) {
						FIBInspector superInterfaceInspector = inspectorGroup.inspectorForClass(superInterface);
						if (superInterfaceInspector != null) {
							superInterfaceInspector.appendSuperInspectors(inspectorGroup);
							appendSuperInspector(superInterfaceInspector, inspectorGroup.getFIBModelFactory());
						}
					}
				}
				isMerged = true;
			}
		
		}*/

		@Override
		public String toString() {
			return "Inspector[" + getDataClass() + "]";
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
