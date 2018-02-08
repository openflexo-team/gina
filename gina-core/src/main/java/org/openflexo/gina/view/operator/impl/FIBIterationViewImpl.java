/**
 * 
 * Copyright (c) 2013-2015, Openflexo
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

package org.openflexo.gina.view.operator.impl;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.bindings.RuntimeContext;
import org.openflexo.gina.model.operator.FIBIteration;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;
import org.openflexo.gina.view.impl.FIBOperatorViewImpl;
import org.openflexo.gina.view.impl.FIBViewImpl;
import org.openflexo.gina.view.operator.FIBIterationView;

/**
 * Base implementation for an enumeration
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <C2>
 *            type of technology-specific component this view contains
 * 
 * @author sylvain
 */
public abstract class FIBIterationViewImpl<C, C2> extends FIBOperatorViewImpl<FIBIteration, C, C2> implements FIBIterationView<C, C2> {

	private static final Logger logger = Logger.getLogger(FIBIterationViewImpl.class.getPackage().getName());

	private BindingValueListChangeListener<Object, List<Object>> listBindingValueChangeListener;

	public FIBIterationViewImpl(FIBIteration model, FIBController controller, RuntimeContext context) {
		super(model, controller);
		setRuntimeContext(context);
		buildSubComponents();
	}

	@Override
	public synchronized void delete() {
		if (listBindingValueChangeListener != null) {
			listBindingValueChangeListener.stopObserving();
			listBindingValueChangeListener.delete();
		}
		super.delete();
	}

	public class IteratedContentsImpl<I> implements IteratedContents<I> {
		private final I iteratedValue;
		protected Map<FIBComponent, FIBViewImpl<?, C2>> subViewsMap;

		public IteratedContentsImpl(I iteratedValue) {
			this.iteratedValue = iteratedValue;
			subViewsMap = new HashMap<>();
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals(getComponent().getIteratorName())) {
				return getIteratedValue();
			}
			return FIBIterationViewImpl.this.getValue(variable);
		}

		@Override
		public I getIteratedValue() {
			return iteratedValue;
		}

		@Override
		public FIBController getFIBController() {
			return getController();
		}

		@Override
		public Map<FIBComponent, FIBViewImpl<?, C2>> getSubViewsMap() {
			return subViewsMap;
		}

		@Override
		public boolean containsView(FIBView<?, ?> view) {
			for (FIBViewImpl<?, C2> fibViewImpl : subViewsMap.values()) {
				if (fibViewImpl == view) {
					return true;
				}
				if (fibViewImpl instanceof FIBContainerView) {
					if (((FIBContainerView) fibViewImpl).containsView(view)) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return "IteratedContents[" + Integer.toHexString(hashCode()) + "]/" + iteratedValue;
		}
	}

	protected Map<Object, IteratedContents<?>> iteratedSubViewsMap = new LinkedHashMap<>();

	private Map<FIBView<?, ?>, IteratedContents<?>> iteratedContentsMap = new HashMap<>();

	@Override
	public IteratedContents<?> getIteratedContents(FIBView<?, ?> view) {
		IteratedContents<?> returned = iteratedContentsMap.get(view);
		if (returned == null) {
			for (IteratedContents<?> contents : getIteratedSubViewsMap().values()) {
				if (contents.containsView(view)) {
					returned = contents;
					iteratedContentsMap.put(view, contents);
					break;
				}
			}
		}
		return returned;
	}

	@Override
	public Map<Object, IteratedContents<?>> getIteratedSubViewsMap() {
		return iteratedSubViewsMap;
	}

	@Override
	public boolean handleIteration() {
		return true;
	}

	@Override
	public void buildSubComponents() {

		if (!handleIteration()) {
			// In Edit mode, return super (don't execute the iteration)
			super.buildSubComponents();
			return;
		}

		iteratedContentsMap.clear();
		iteratedSubViewsMap.clear();

		List<?> accessedList = null;
		try {
			accessedList = getComponent().getList().getBindingValue(getBindingEvaluationContext());
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		// System.out.println(
		// "Recomputing whole iteration for " + getComponent() + " accessedList=" + accessedList + " for " + getComponent().getList());

		if (accessedList != null) {
			for (Object iteratedValue : accessedList) {
				// System.out.println("> On itere pour " + iteratedValue + " dans " + getComponent());
				internallyBuildChildComponents(iteratedValue);
			}
		}

		addSubComponentsAndDoLayout();
		performUpdateSubViews();
	}

	/*private void debug() {
		for (Object iterator : iteratedSubViewsMap.keySet()) {
			System.out.println(" ************** " + iterator);
			IteratedContents<?> contents = iteratedSubViewsMap.get(iterator);
			debug(contents);
		}
	}
	
	private void debug(IteratedContents<?> contents) {
		for (FIBComponent component : contents.getSubViewsMap().keySet()) {
			System.out.println(" > " + component + " -> ");
			debug(contents.getSubViewsMap().get(component), 2);
		}
	}
	
	private void debug(FIBView<?, ?> view, int indent) {
		System.out.println(StringUtils.buildWhiteSpaceIndentation(indent) + "> " + view + " " + view.getParentView() + " "
				+ ((FIBViewImpl) view).getRuntimeContext());
		if (view instanceof FIBContainerView) {
			for (FIBView<?, ?> v : ((FIBContainerView<?, ?, ?>) view).getSubViews()) {
				debug(v, indent + 2);
			}
		}
	}*/

	private <I> IteratedContents<I> internallyBuildChildComponents(I iteratedValue) {

		// System.out.println("internallyBuildChildComponents for " + iteratedValue);

		IteratedContents<I> context = (IteratedContents<I>) iteratedSubViewsMap.get(iteratedValue);

		if (context == null) {
			context = new IteratedContentsImpl<I>(iteratedValue);
			iteratedSubViewsMap.put(iteratedValue, context);
		}

		Vector<FIBComponent> allSubComponents = new Vector<>();
		allSubComponents.addAll(getNotHiddenSubComponents());

		for (FIBComponent subComponent : allSubComponents) {
			FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) context.getSubViewsMap().get(subComponent);
			if (subView == null) {
				subView = (FIBViewImpl<?, C2>) getController().buildView(subComponent, context, false);
				if (subView instanceof FIBContainerView) {
					((FIBContainerViewImpl) subView).buildSubComponents();
				}
				registerViewForComponent(subView, subComponent, iteratedValue);
			}
		}

		return context;
	}

	public <I> void registerViewForComponent(FIBViewImpl<?, C2> view, FIBComponent component, I iteratedValue) {
		IteratedContents<I> returned = (IteratedContents<I>) iteratedSubViewsMap.get(iteratedValue);
		if (returned != null) {
			((Map) returned.getSubViewsMap()).put(component, view);
		}
		// subViewsMap.put(component, view);

		view.setRuntimeContext(returned);

		subViewsList.add(view);
	}

	public <I> void unregisterViewForComponent(FIBViewImpl<?, C2> view, FIBComponent component, I iteratedValue) {
		IteratedContents<I> returned = (IteratedContents<I>) iteratedSubViewsMap.get(iteratedValue);
		if (returned != null) {
			((Map) returned.getSubViewsMap()).remove(component);
		}
		subViewsList.remove(view);
	}

	@Override
	protected void performUpdateSubViews() {
		for (IteratedContents<?> contents : iteratedSubViewsMap.values()) {
			for (FIBView<?, ?> v : new ArrayList<>(contents.getSubViewsMap().values())) {
				if (!v.isDeleted() /*&& v.isViewVisible()*/) {
					v.update();
				}
			}
		}

	}

	private List<FIBViewImpl<?, C2>> subViewsList = new ArrayList<>();

	@Override
	public Collection<FIBViewImpl<?, C2>> getSubViews() {

		if (!handleIteration()) {
			return super.getSubViews();
		}

		return subViewsList;
	}

	@Override
	public FIBIteration getComponent() {
		return super.getComponent();
	}

	private void listenListValueChange() {
		if (listBindingValueChangeListener != null) {
			listBindingValueChangeListener.stopObserving();
			listBindingValueChangeListener.delete();
		}

		// TODO: investigate on this bug and workaround
		// Binding should be notified and we should not force revalidate
		if (getComponent().getList() != null && getComponent().getList().isSet() && !getComponent().getList().isValid()) {
			String invalidBindingReason = getComponent().getList().invalidBindingReason();
			getComponent().getList().forceRevalidate();
			logger.warning("binding was not valid: " + getComponent().getList() + " reason: " + invalidBindingReason);
			if (getComponent().getList().isValid()) {
				logger.warning("Binding has been force revalidated and is now valid. Please investigate.");
			}
		}

		if (getComponent().getList() != null && getComponent().getList().forceRevalidate()) {
			listBindingValueChangeListener = new BindingValueListChangeListener<Object, List<Object>>(
					((DataBinding) getComponent().getList()), getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, List<Object> newValue) {
					// System.out.println(" bindingValueChanged() detected for list=" + getComponent().getList() + " with newValue=" +
					// newValue
					// + " source=" + source);
					updateIteration();
				}
			};
		}
	}

	private void stopListenListValueChange() {
		if (listBindingValueChangeListener != null) {
			listBindingValueChangeListener.stopObserving();
			listBindingValueChangeListener.delete();
			listBindingValueChangeListener = null;
		}
	}

	protected void updateIteration() {

		List<?> accessedList = null;
		try {
			accessedList = getComponent().getList().getBindingValue(getBindingEvaluationContext());
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		System.out.println("Hop, faudrait iterer sur " + accessedList);

	}

	@Override
	protected void componentBecomesVisible() {
		super.componentBecomesVisible();
		listenListValueChange();
	}

	@Override
	protected void componentBecomesInvisible() {
		super.componentBecomesInvisible();
		stopListenListValueChange();
	}

	/*@Override
	protected void performUpdate() {
		if (getRenderingAdapter() != null) {
			super.performUpdate();
		}
		else {
			performUpdateSubViews();
			if (layoutIsInvalid) {
				updateLayout();
				layoutIsInvalid = false;
			}
		}
	}*/

	@Override
	public void addSubComponentsAndDoLayout() {
		FIBContainerView<?, ?, ?> containerView = getConcreteContainerView();
		if (containerView instanceof FIBContainerViewImpl) {
			((FIBContainerViewImpl<?, ?, ?>) containerView).addSubComponentsAndDoLayout();
		}
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		update();
	}

	/*@Override
	public void changeLayout() {
		logger.info("relayout panel " + getComponent());
	
		// TODO: please reimplement this and make it more efficient !!!!
	
		clearContainer();
	
		if (layoutManager != null) {
			layoutManager.delete();
		}
		layoutManager = null;
	
		layoutManager = makeFIBLayoutManager(getComponent().getLayout());
		getLayoutManager().setLayoutManager(getTechnologyComponent());
	
		buildSubComponents();
		// updateDataObject(getDataObject());
		update();
	}*/

	@Override
	public void changeLayout() {
		if (getConcreteContainerView() != null) {
			getConcreteContainerView().changeLayout();
		}
	}

	@Override
	public void updateLayout() {

		// logger.info("relayout panel (caution !!! this is really costly !)" + getComponent());

		if (isDeleted()) {
			return;
		}

		// TODO: please reimplement this and make it more efficient !!!!

		clearContainer();

		// getLayoutManager().setLayoutManager(getTechnologyComponent());
		buildSubComponents();

		if (getConcreteContainerView() != null) {
			getConcreteContainerView().updateLayout();
		}
	}

	/**
	 * Remove all components present in this container
	 */
	protected void clearContainer() {
		// getJComponent().removeAll();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (isDeleted()) {
			return;
		}
		if (evt.getPropertyName().equals(FIBContainer.SUB_COMPONENTS_KEY)) {
			// System.out.println("Rebuild whole iteration");
			rebuildTechnologyComponent();
			getConcreteContainerView().updateLayout();
		}

		super.propertyChange(evt);
	}

}
