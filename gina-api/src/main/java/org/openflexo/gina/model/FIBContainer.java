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

package org.openflexo.gina.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.ComponentConstraints;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Finder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PastingPoint;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.toolbox.StringUtils;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBContainer.FIBContainerImpl.class)
public abstract interface FIBContainer extends FIBComponent {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String DATA_KEY = "data";
	@PropertyIdentifier(type = Class.class)
	public static final String DATA_CLASS_KEY = "dataClass";
	@PropertyIdentifier(type = List.class)
	public static final String SUB_COMPONENTS_KEY = "subComponents";

	@Getter(value = SUB_COMPONENTS_KEY, cardinality = Cardinality.LIST, inverse = FIBComponent.PARENT_KEY)
	@XMLElement
	@Embedded
	// REALLY IMPORTANT !!!!
	@CloningStrategy(StrategyType.CLONE)
	public List<FIBComponent> getSubComponents();

	@Setter(SUB_COMPONENTS_KEY)
	public void setSubComponents(List<FIBComponent> subComponents);

	@Adder(SUB_COMPONENTS_KEY)
	@PastingPoint
	public void addToSubComponents(FIBComponent aSubComponent);

	public void insertToSubComponentsAtIndex(FIBComponent aSubComponent, int index);

	public void insertToSubComponentsAtIndex(FIBComponent aSubComponent, ComponentConstraints someConstraints, int index);

	public void moveToSubComponentsAtIndex(FIBComponent aSubComponent, int index);

	public void addToSubComponents(FIBComponent aComponent, ComponentConstraints someConstraints);

	public void addToSubComponents(FIBComponent aComponent, ComponentConstraints someConstraints, int subComponentIndex);

	@Remover(SUB_COMPONENTS_KEY)
	public void removeFromSubComponents(FIBComponent aSubComponent);

	// Beware of null values !!!!
	@Finder(collection = SUB_COMPONENTS_KEY, attribute = FIBComponent.NAME_KEY)
	public FIBComponent getSubComponentNamed(String name);

	public void reorderComponents();

	/**
	 * Merge supplied container with this container<br>
	 * Containers should be of same type.<br>
	 * Merge is recursively performed. Lookup and merge strategy is based on names given to sub-components
	 * 
	 * Merging Policy: We append the children of the container to this.subComponents<br>
	 * 1. If child has no index, we insert it after all subcomponents with a negative index<br>
	 * 2. If child has a negative index, we insert before any subcomponent with a null or positive index, or a negative index that is equal
	 * or greater than the child index<br>
	 * 3. If the child has a positive index, we insert after all subcomponents with a negative or null index, or a positive index which is
	 * smaller or equal to the child index
	 * 
	 * Moreover, when inserting, we always verify that we are not inserting ourselves in a consecutive series of indexed components.
	 * Finally, when we insert the child, we also insert all the consecutive indexed components (two components with a null index are
	 * considered to be consecutive)
	 * 
	 * @param container
	 */
	public void append(FIBContainer container);

	/**
	 * Return a recursive list of all components beeing embedded in this container
	 * 
	 * @return
	 */
	public List<FIBComponent> getAllSubComponents();

	@Deprecated
	// TODO: check if this is still required
	public void notifyComponentMoved(FIBComponent aComponent);

	public Layout getLayout();

	public void componentFirst(FIBComponent c);

	public void componentUp(FIBComponent c);

	public void componentDown(FIBComponent c);

	public void componentLast(FIBComponent c);

	public boolean checkContainmentIntegrity();

	@Getter(value = DATA_KEY)
	@XMLAttribute
	// We ignore cloning since this will be performed in underlying FIBVariable
	@CloningStrategy(StrategyType.IGNORE)
	@Deprecated
	public DataBinding<?> getData();

	@Setter(DATA_KEY)
	@Deprecated
	public void setData(DataBinding<?> data);

	@Getter(value = DATA_CLASS_KEY)
	@XMLAttribute(xmlTag = "dataClassName")
	// We ignore cloning since this will be performed in underlying FIBVariable
	@CloningStrategy(StrategyType.IGNORE)
	@Deprecated
	public Class<?> getDataClass();

	@Setter(DATA_CLASS_KEY)
	@Deprecated
	public void setDataClass(Class<?> dataClass);

	@Deprecated
	public Type getDataType();

	@Deprecated
	public void setDataType(Type dataType);

	public void addToSubComponentsNoNotification(FIBComponent aComponent, ComponentConstraints someConstraints);

	public void fireSubComponentsChanged();

	public static abstract class FIBContainerImpl extends FIBComponentImpl implements FIBContainer {

		private static final Logger logger = Logger.getLogger(FIBContainer.class.getPackage().getName());

		/**
		 * Return a recursive list of all components beeing embedded in this container
		 * 
		 * @return
		 */
		@Override
		public List<FIBComponent> getAllSubComponents() {
			List<FIBComponent> returned = new ArrayList<>();
			appendAllSubComponents(this, returned);
			return returned;
		}

		private void appendAllSubComponents(FIBContainer container, List<FIBComponent> returned) {
			for (FIBComponent child : container.getSubComponents()) {
				returned.add(child);
				if (child instanceof FIBContainer) {
					appendAllSubComponents((FIBContainer) child, returned);
				}
			}
		}

		@Override
		protected void notifyBindingFactoryChanged() {
			for (FIBComponent child : getSubComponents()) {
				((FIBComponentImpl) child).notifyBindingFactoryChanged();
			}
			super.notifyBindingFactoryChanged();
		}

		@Override
		public void insertToSubComponentsAtIndex(FIBComponent aSubComponent, int index) {
			addToSubComponents(aSubComponent, null, index);
		}

		@Override
		public void insertToSubComponentsAtIndex(FIBComponent aSubComponent, ComponentConstraints constraints, int index) {
			addToSubComponents(aSubComponent, constraints, index);
		}

		@Override
		public void moveToSubComponentsAtIndex(FIBComponent aSubComponent, int index) {
			List<FIBComponent> subComponents = getSubComponents();
			subComponents.remove(aSubComponent);
			subComponents.add(index, aSubComponent);

		}

		@Override
		public void addToSubComponents(FIBComponent aSubComponent) {
			addToSubComponents(aSubComponent, null);
		}

		@Override
		public void addToSubComponents(FIBComponent aComponent, ComponentConstraints someConstraints) {
			addToSubComponents(aComponent, someConstraints, getSubComponents().size());
		}

		@Override
		public final void addToSubComponentsNoNotification(FIBComponent aComponent, ComponentConstraints someConstraints) {

			// TODO: i dont't like this code, we might do it without all these
			// hacks
			if (someConstraints != null && aComponent.getConstraints() != null) {
				aComponent.getConstraints().ignoreNotif = true;
				aComponent.getConstraints().setsWith(someConstraints);
				aComponent.getConstraints().ignoreNotif = false;
			}
			if (aComponent.getConstraints() == null) {
				aComponent.setConstraints(someConstraints);
			}

			performSuperAdder(SUB_COMPONENTS_KEY, aComponent);

		}

		@Override
		public void fireSubComponentsChanged() {
			reorderComponents();

			if (hasTemporarySize()) {
				// No more need for temporary size
				clearTemporarySize();
			}

			getPropertyChangeSupport().firePropertyChange(SUB_COMPONENTS_KEY, null, getSubComponents());

		}

		@Override
		public final void addToSubComponents(FIBComponent aComponent, ComponentConstraints someConstraints, int subComponentIndex) {

			// TODO: i dont't like this code, we might do it without all these
			// hacks
			if (someConstraints != null && aComponent.getConstraints() != null) {
				aComponent.getConstraints().ignoreNotif = true;
				aComponent.getConstraints().setsWith(someConstraints);
				aComponent.getConstraints().ignoreNotif = false;
			}
			if (aComponent.getConstraints() == null) {
				aComponent.setConstraints(someConstraints);
			}

			if (deserializationPerformed) {
				updateComponentIndexForInsertionIndex(aComponent, subComponentIndex);
			}

			performSuperAdder(SUB_COMPONENTS_KEY, aComponent, subComponentIndex);

			if (deserializationPerformed) {
				reorderComponents();
			}

			if (hasTemporarySize()) {
				// No more need for temporary size
				clearTemporarySize();
			}

			// generate a default unique name
			if (StringUtils.isEmpty(aComponent.getName())) {
				// System.out.println("generate unique name for :");
				// System.out.println(aComponent.getFactory().stringRepresentation(aComponent));
				aComponent.setName(aComponent.generateUniqueName(aComponent.getBaseName()));
				// System.out.println("New name: " + aComponent.getName());
			}

			getPropertyChangeSupport().firePropertyChange(SUB_COMPONENTS_KEY, null, aComponent);

		}

		private void updateComponentIndexForInsertionIndex(FIBComponent component, int insertionIndex) {
			if (getSubComponents().size() > 0) {
				FIBComponent previous = null;
				FIBComponent next = null;
				if (insertionIndex < getSubComponents().size()) {
					next = getSubComponents().get(insertionIndex);
				}
				if (insertionIndex > 0 && insertionIndex - 1 < getSubComponents().size()) {
					previous = getSubComponents().get(insertionIndex - 1);
				}

				if (previous != null) {
					if (previous.getIndex() == null) {
						if (component.getIndex() != null && component.getIndex() < 0) {
							component.setIndex(null);
						}
					}
					else if (previous.getIndex() < 0) {
						if (component.getIndex() != null && component.getIndex() < previous.getIndex()) {
							component.setIndex(previous.getIndex());
						}
					}
					else {
						if (component.getIndex() == null || component.getIndex() < previous.getIndex()) {
							component.setIndex(previous.getIndex());
						}
					}
				}
				if (next != null) {
					if (next.getIndex() == null) {
						if (component.getIndex() != null && component.getIndex() >= 0) {
							component.setIndex(null);
						}
					}
					else if (next.getIndex() < 0) {
						if (component.getIndex() == null || component.getIndex() > next.getIndex()) {
							component.setIndex(next.getIndex());
						}
					}
					else {
						if (component.getIndex() != null && component.getIndex() > next.getIndex()) {
							component.setIndex(next.getIndex());
						}
					}
				}
			}
		}

		/*
		 * @Override public FIBComponent getSubComponentNamed(String name) { for
		 * (FIBComponent c : getSubComponents()) { if (c.getName() != null &&
		 * c.getName().equals(name)) { return c; } } return null; }
		 */

		/*
		 * @Override public void removeFromSubComponents(FIBComponent
		 * aComponent) { removeFromSubComponentsNoNotification(aComponent);
		 * getPropertyChangeSupport
		 * ().firePropertyChange(Parameters.subComponents.name(), null,
		 * subComponents); }
		 * 
		 * public void removeFromSubComponentsNoNotification(FIBComponent
		 * aComponent) { aComponent.setParent(null);
		 * subComponents.remove(aComponent); }
		 */

		@Override
		@Deprecated
		// TODO: check if this is still required
		public void notifyComponentMoved(FIBComponent aComponent) {
			getPropertyChangeSupport().firePropertyChange(SUB_COMPONENTS_KEY, null, getSubComponents());
		}

		@Override
		public Enumeration<FIBComponent> children() {
			return (new Vector<>(getSubComponents())).elements();
		}

		@Override
		public boolean getAllowsChildren() {
			return true;
		}

		@Override
		public FIBComponent getChildAt(int childIndex) {
			return getSubComponents().get(childIndex);
		}

		@Override
		public int getChildCount() {
			return getSubComponents().size();
		}

		@Override
		public int getIndex(TreeNode node) {
			return getSubComponents().indexOf(node);
		}

		@Override
		public boolean isLeaf() {
			return getSubComponents().size() == 0;
		}

		/**
		 * Merge supplied container with this container<br>
		 * Containers should be of same type.<br>
		 * Merge is recursively performed. Lookup and merge strategy is based on names given to sub-components
		 * 
		 * Merging Policy: We append the children of the container to this.subComponents<br>
		 * 1. If child has no index, we insert it after all subcomponents with a negative index<br>
		 * 2. If child has a negative index, we insert before any subcomponent with a null or positive index, or a negative index that is
		 * equal or greater than the child index<br>
		 * 3. If the child has a positive index, we insert after all subcomponents with a negative or null index, or a positive index which
		 * is smaller or equal to the child index
		 * 
		 * Moreover, when inserting, we always verify that we are not inserting ourselves in a consecutive series of indexed components.
		 * Finally, when we insert the child, we also insert all the consecutive indexed components (two components with a null index are
		 * considered to be consecutive)
		 * 
		 * @param container
		 */
		@Override
		public void append(FIBContainer container) {
			List<FIBComponent> mergedComponents = new ArrayList<>();
			for (int i = container.getSubComponents().size() - 1; i >= 0; i--) {
				FIBComponent c2 = container.getSubComponents().get(i);
				if (c2.getName() != null && c2 instanceof FIBContainer) {
					for (FIBComponent c1 : getSubComponents()) {
						if (c2.getName().equals(c1.getName()) && c1 instanceof FIBContainer) {

							((FIBContainer) c1).append((FIBContainer) c2);

							mergedComponents.add(c2);
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("Merged " + c1 + " and " + c2);
							}
							break;
						}
					}
				}
			}

			for (int i = 0; i < container.getSubComponents().size(); i++) {

				FIBComponent child = container.getSubComponents().get(i);

				if (mergedComponents.contains(child)) {
					continue;
				}
				// Is there a component already named same as the one to be
				// added ?
				// (In this case, do NOT add it, the redefinition override
				// parent behaviour)
				FIBComponent overridingComponent = null;

				if (child.getName() != null) {
					overridingComponent = getSubComponentNamed(child.getName());
				}

				if (overridingComponent == null) {

					/**
					 * Merging Policy: We append the children of the container to this.subComponents<br>
					 * 1. If child has no index, we insert it after all subcomponents with a negative index<br>
					 * 2. If child has a negative index, we insert before any subcomponent with a null or positive index, or a negative
					 * index that is equal or greater than the child index<br>
					 * 3. If the child has a positive index, we insert after all subcomponents with a negative or null index, or a positive
					 * index which is smaller or equal to the child index
					 * 
					 * Moreover, when inserting, we always verify that we are not inserting ourselves in a consecutive series of indexed
					 * components. Finally, when we insert the child, we also insert all the consecutive indexed components (two components
					 * with a null index are considered to be consecutive)
					 */

					int indexInsertion;
					if (child.getIndex() == null) {
						indexInsertion = getSubComponents().size();
						for (int j = 0; j < getSubComponents().size(); j++) {
							FIBComponent c = getSubComponents().get(j);
							if (c.getIndex() == null || c.getIndex() > -1) {
								indexInsertion = j;
								break;
							}
						}
					}
					else if (child.getIndex() < 0) {
						indexInsertion = 0;
						for (int j = 0; j < getSubComponents().size(); j++) {
							FIBComponent c = getSubComponents().get(j);
							if (c.getIndex() == null || c.getIndex() >= child.getIndex()) {
								// We have found where to insert
								indexInsertion = j;
								if (c.getIndex() != null && c.getIndex() < 0 && j > 0) {
									// This is a complex case
									FIBComponent previousComponent = getSubComponents().get(j - 1);
									// If the component that is just before the
									// insertion point has an index which is
									// right before the
									// current
									// component one, then we need to skip all
									// the consecutives indexed component.
									if (previousComponent.getIndex() != null && previousComponent.getIndex() + 1 == c.getIndex()) {
										int previous = c.getIndex();
										j++;
										while (j < getSubComponents().size()) {
											c = getSubComponents().get(j);
											if (c.getIndex() != null && c.getIndex() == previous + 1) {
												previous = c.getIndex();
												j++;
											}
											else {
												break;
											}
										}
										indexInsertion = j;
										break;
									}
								}
								break;
							}
						}

					}
					else {

						indexInsertion = getSubComponents().size();
						for (int j = 0; j < getSubComponents().size(); j++) {
							FIBComponent c = getSubComponents().get(j);
							if (c.getIndex() != null && c.getIndex() > -1 && c.getIndex() >= child.getIndex()) {
								indexInsertion = j;
								if (j > 0) {
									// This is a complex case
									FIBComponent previousComponent = getSubComponents().get(j - 1);
									// If the component that is just before the
									// insertion point has an index which is
									// right before the
									// current
									// component one, then we need to skip all
									// the consecutives indexed component.
									if (previousComponent.getIndex() != null && previousComponent.getIndex() + 1 == c.getIndex()) {
										int previous = c.getIndex();
										j++;
										while (j < getSubComponents().size()) {
											c = getSubComponents().get(j);
											if (c.getIndex() != null && c.getIndex() == previous + 1) {
												previous = c.getIndex();
												j++;
											}
											else {
												break;
											}
										}
										indexInsertion = j;
										break;
									}
								}
								break;
							}
						}

					}
					boolean insert = true;
					Integer startIndex = child.getIndex();

					while (insert) {

						getSubComponents().add(indexInsertion, child);
						child.setParent(this);
						indexInsertion++;

						if (i + 1 < container.getSubComponents().size()) {
							Integer previousInteger = child.getIndex();
							child = container.getSubComponents().get(i + 1);
							insert = previousInteger == null && child.getIndex() == null
									|| previousInteger != null && child.getIndex() != null && previousInteger + 1 == child.getIndex()
									|| child.getIndex() != null
											&& (startIndex == null && child.getIndex() == startIndex
													|| startIndex != null && startIndex.equals(child.getIndex()))
											&& !mergedComponents.contains(child);
							if (insert) {
								i++;
								if (child.getName() != null) {
									overridingComponent = getSubComponentNamed(child.getName());
								}
								if (overridingComponent != null /*&& overridingComponent.isHidden()*/) {
									insert = false;
									/*if (overridingComponent.isHidden()) {
										removeFromSubComponents(overridingComponent);
									}*/
								}
							}
							else {
								break;
							}
						}
						else {
							break;
						}
					}
				}
				else if (overridingComponent.isHidden()) {
					removeFromSubComponents(overridingComponent);
				}
			}

			if (container.getLocalizedDictionary() != null) {
				retrieveFIBLocalizedDictionary().append(container.getLocalizedDictionary());
			}

			// TODO: Hack to be removed while refactoring BindingModel
			// management
			// deserializationPerformed = true;
			// updateBindingModel();
			// TODO: Hack to be removed while refactoring BindingModel
			// management
			// deserializationPerformed = false;

			finalizeDeserialization();
			for (FIBComponent c : new ArrayList<>(getSubComponents())) {
				recursivelyFinalizeDeserialization(c);
			}

		}

		private void recursivelyFinalizeDeserialization(FIBComponent c) {
			c.finalizeDeserialization();
			if (c instanceof FIBContainer) {
				for (FIBComponent c2 : ((FIBContainer) c).getSubComponents()) {
					recursivelyFinalizeDeserialization(c2);
				}
			}
		}

		// Default layout is built-in: only FIBPanel manage a custom layout,
		// where this method is overriden
		@Override
		public Layout getLayout() {
			return null;
		}

		// Not permitted since default layout is built-in: only FIBPanel
		// manage a custom layout, where this method is overriden
		public void setLayout(Layout layout) {
		}

		@Override
		public void componentFirst(FIBComponent c) {
			if (c == null) {
				return;
			}
			getSubComponents().remove(c);
			updateComponentIndexForInsertionIndex(c, 0);
			getSubComponents().add(0, c);
			notifyComponentIndexChanged(c);
		}

		@Override
		public void componentUp(FIBComponent c) {
			if (c == null) {
				return;
			}
			int index = getSubComponents().indexOf(c);
			if (index > 0) {
				getSubComponents().remove(c);
				updateComponentIndexForInsertionIndex(c, index - 1);
				getSubComponents().add(index - 1, c);
				notifyComponentIndexChanged(c);
			}
		}

		@Override
		public void componentDown(FIBComponent c) {
			if (c == null) {
				return;
			}
			int index = getSubComponents().indexOf(c);
			if (index < getSubComponents().size() - 1) {
				getSubComponents().remove(c);
			}
			updateComponentIndexForInsertionIndex(c, index + 1);
			getSubComponents().add(index + 1, c);
			notifyComponentIndexChanged(c);
		}

		@Override
		public void componentLast(FIBComponent c) {
			if (c == null) {
				return;
			}
			getSubComponents().remove(c);
			updateComponentIndexForInsertionIndex(c, getSubComponents().size());
			getSubComponents().add(c);
			notifyComponentIndexChanged(c);
		}

		private void notifyComponentIndexChanged(FIBComponent component) {
			FIBPropertyNotification<ComponentConstraints> notification = new FIBPropertyNotification<>(
					(FIBProperty<ComponentConstraints>) FIBProperty.getFIBProperty(getClass(), CONSTRAINTS_KEY), component.getConstraints(),
					component.getConstraints());
			component.notify(notification);
			getPropertyChangeSupport().firePropertyChange(SUB_COMPONENTS_KEY, null, getSubComponents());
		}

		private void notifySubcomponentsIndexChanged() {
			getPropertyChangeSupport().firePropertyChange(SUB_COMPONENTS_KEY, null, getSubComponents());
		}

		/*
		 * @Override public void notifiedBindingModelRecreated() {
		 * super.notifiedBindingModelRecreated(); for (FIBComponent c :
		 * getSubComponents()) { c.notifiedBindingModelRecreated(); } }
		 */

		@Override
		public void reorderComponents() {
			// Rules to sort sub components
			// 1. Smallest negative index is placed first
			// 2. All unindexed components (index==null) are then placed
			// 3. Eventually, all the other components are placed after
			// according to their defined index (0 is considered as a positive
			// index)
			Collections.sort(getSubComponents(), new Comparator<FIBComponent>() {

				@Override
				public int compare(FIBComponent o1, FIBComponent o2) {
					if (o1.getIndex() == null) {
						if (o2.getIndex() == null) {
							return 0;
						}
						if (o2.getIndex() < 0) {
							return 1;
						}
						else {
							return -1;
						}
					}
					else {
						if (o2.getIndex() == null) {
							return o1.getIndex();
						}
						else {
							return o1.getIndex() - o2.getIndex();
						}
					}
				}
			});
			notifySubcomponentsIndexChanged();
		}

		@Override
		public Collection<? extends FIBModelObject> getEmbeddedFIBModelObjects() {
			return getSubComponents();
		}

		@Deprecated
		private FIBVariable<?> getDefaultDataVariable(boolean createWhenNonExistant) {
			FIBVariable<?> returned = getVariable(DEFAULT_DATA_VARIABLE);
			if (returned == null && createWhenNonExistant) {
				returned = getModelFactory().newFIBVariable(this, DEFAULT_DATA_VARIABLE);
			}
			return returned;
		}

		@Deprecated
		private FIBVariable<?> getDefaultDataVariable() {
			return getDefaultDataVariable(false);
		}

		@Override
		@Deprecated
		public DataBinding<?> getData() {
			if (getDefaultDataVariable() != null) {
				return getDefaultDataVariable().getValue();
			}
			return null;
		}

		@Override
		@Deprecated
		public void setData(DataBinding<?> data) {
			getDefaultDataVariable(true).setValue((DataBinding) data);
		}

		@Override
		@Deprecated
		public Class<?> getDataClass() {
			if (isSerializing()) {
				return null;
			}
			if (isSettingDataClass) {
				return null;
			}
			if (getDefaultDataVariable() != null) {
				return TypeUtils.getBaseClass(getDefaultDataVariable().getType());
			}
			return null;
		}

		private boolean isSettingDataClass = false;

		@Override
		@Deprecated
		public void setDataClass(Class<?> dataClass) {
			isSettingDataClass = true;
			getDefaultDataVariable(true).setType(dataClass);
			isSettingDataClass = false;
		}

		@Override
		@Deprecated
		public void setDataType(Type dataType) {
			isSettingDataClass = true;
			getDefaultDataVariable(true).setType(dataType);
			isSettingDataClass = false;
		}

		@Override
		public Type getDataType() {
			if (getDefaultDataVariable() != null) {
				return getDefaultDataVariable().getType();
			}
			return null;
		}

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			for (FIBComponent subComponent : getSubComponents()) {
				subComponent.revalidateBindings();
			}
		}

		@Override
		public boolean checkContainmentIntegrity() {
			for (FIBComponent c : getSubComponents()) {
				if (c.getParent() != this) {
					System.out.println("checkContainmentIntegrity() FAILED because child " + c + " does not have for parent " + this
							+ " (hash=" + hashCode() + ") but " + c.getParent() + " (hash=" + c.getParent().hashCode() + ")");
					System.out.println("root component for c = " + c.getRootComponent());
					System.out.println("root component for this = " + getRootComponent());
					return false;
				}
				if (c instanceof FIBContainer) {
					if (!((FIBContainer) c).checkContainmentIntegrity()) {
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			for (FIBComponent c : getSubComponents()) {
				c.searchLocalized(retriever);
			}
		}

		@Override
		public boolean isOperator() {
			return false;
		}

		/**
		 * Return first found component named as supplied<br>
		 * Recursive lookup method for contained FIBComponent
		 * 
		 * @param name
		 * @return
		 */
		// TODO: move to FIBContainer
		@Override
		public FIBComponent getComponentNamed(String name) {

			FIBComponent returned = super.getComponentNamed(name);

			if (returned == null) {
				for (FIBComponent c : getSubComponents()) {
					returned = c.getComponentNamed(name);
					if (returned != null) {
						return returned;
					}
				}
			}

			return returned;
		}

		/**
		 * Return list of components named as supplied<br>
		 * Note that this is not a normal situation
		 * 
		 * @param name
		 * @return
		 */
		@Override
		public List<FIBComponent> getComponentsNamed(String name) {
			List<FIBComponent> result = new ArrayList<>();
			FIBComponent returned = super.getComponentNamed(name);
			if (returned != null) {
				result.add(returned);
			}
			for (FIBComponent c : getSubComponents()) {
				result.addAll(c.getComponentsNamed(name));
			}
			return result;
		}

		/**
		 * Assume that component name may not be unique<br>
		 * Translate name to a unique name in the scope of declared root component<br>
		 * Apply translating scheme to sub-components
		 * 
		 * @param component
		 */
		@Override
		public void translateNameWhenRequired() {
			super.translateNameWhenRequired();
			for (FIBComponent child : getSubComponents()) {
				child.translateNameWhenRequired();
			}
		}

	}
}
