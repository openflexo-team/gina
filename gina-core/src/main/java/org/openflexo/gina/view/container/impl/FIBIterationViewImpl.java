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

package org.openflexo.gina.view.container.impl;

import java.awt.Image;
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
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBIteration;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.layout.FIBLayoutManager;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.FIBIterationView;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;
import org.openflexo.gina.view.impl.FIBViewImpl;
import org.openflexo.rm.Resource;
import org.openflexo.swing.ImageUtils;

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
public abstract class FIBIterationViewImpl<C, C2> extends FIBContainerViewImpl<FIBIteration, C, C2> implements FIBIterationView<C, C2> {

	private static final Logger logger = Logger.getLogger(FIBIterationViewImpl.class.getPackage().getName());

	private FIBLayoutManager<C, C2, ?> layoutManager;

	private BindingValueChangeListener<Image> dynamicBackgroundImageBindingValueChangeListener;

	private BindingValueListChangeListener<Object, List<Object>> listBindingValueChangeListener;

	public FIBIterationViewImpl(FIBIteration model, FIBController controller, IterationRenderingAdapter<C, C2> renderingAdapter) {
		super(model, controller, renderingAdapter);
		layoutManager = makeFIBLayoutManager(model.getLayout());
		layoutManager.setLayoutManager(getTechnologyComponent());
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
			if (variable.getVariableName().equals(FIBIteration.ITERATOR_NAME)) {
				return getIteratedValue();
			}
			return FIBIterationViewImpl.this.getValue(variable);
		}

		@Override
		public I getIteratedValue() {
			return iteratedValue;
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

	protected boolean handleIteration() {
		return true;
	}

	@Override
	protected void buildSubComponents() {

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

		System.out.println("On recalcule toute l'iteration !!!!!!!!!!!!!");

		if (accessedList != null) {
			for (Object iteratedValue : accessedList) {
				internallyBuildChildComponents(iteratedValue);
			}
		}

		addSubComponentsAndDoLayout();
		performUpdateSubViews();
	}

	private <I> IteratedContents<I> internallyBuildChildComponents(I iteratedValue) {

		IteratedContents<I> returned = (IteratedContents<I>) iteratedSubViewsMap.get(iteratedValue);

		if (returned == null) {
			returned = new IteratedContentsImpl<I>(iteratedValue);
			iteratedSubViewsMap.put(iteratedValue, returned);
		}

		Vector<FIBComponent> allSubComponents = new Vector<>();
		allSubComponents.addAll(getNotHiddenSubComponents());

		for (FIBComponent subComponent : allSubComponents) {

			FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) returned.getSubViewsMap().get(subComponent);
			if (subView == null) {
				subView = (FIBViewImpl<?, C2>) getController().buildView(subComponent, false);
				registerViewForComponent(subView, subComponent, iteratedValue);
			}
		}

		return returned;
	}

	public <I> void registerViewForComponent(FIBViewImpl<?, C2> view, FIBComponent component, I iteratedValue) {
		IteratedContents<I> returned = (IteratedContents<I>) iteratedSubViewsMap.get(iteratedValue);
		if (returned != null) {
			((Map) returned.getSubViewsMap()).put(component, view);
		}
		// subViewsMap.put(component, view);
		subViewsList.add(view);
	}

	public <I> void unregisterViewForComponent(FIBViewImpl<?, C2> view, FIBComponent component, I iteratedValue) {
		IteratedContents<I> returned = (IteratedContents<I>) iteratedSubViewsMap.get(iteratedValue);
		if (returned != null) {
			((Map) returned.getSubViewsMap()).remove(component);
		}
		subViewsList.remove(view);
	}

	private void performUpdateSubViews() {
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
		listenDynamicBackgroundImageValueChange();
		listenListValueChange();
	}

	@Override
	protected void componentBecomesInvisible() {
		super.componentBecomesInvisible();
		stopListenDynamicBackgroundImageValueChange();
		stopListenListValueChange();
	}

	private void listenDynamicBackgroundImageValueChange() {
		if (dynamicBackgroundImageBindingValueChangeListener != null) {
			dynamicBackgroundImageBindingValueChangeListener.stopObserving();
			dynamicBackgroundImageBindingValueChangeListener.delete();
		}
		if (getComponent().getDynamicBackgroundImage() != null && getComponent().getDynamicBackgroundImage().isValid()) {
			dynamicBackgroundImageBindingValueChangeListener = new BindingValueChangeListener<Image>(
					getComponent().getDynamicBackgroundImage(), getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, Image newValue) {
					System.out.println(" bindingValueChanged() detected for dynamicBackgroundImage="
							+ getComponent().getDynamicBackgroundImage() + " with newValue=" + newValue + " source=" + source);
					performUpdateBackgroundImage(newValue);
				}
			};
		}
	}

	private void stopListenDynamicBackgroundImageValueChange() {
		if (dynamicBackgroundImageBindingValueChangeListener != null) {
			dynamicBackgroundImageBindingValueChangeListener.stopObserving();
			dynamicBackgroundImageBindingValueChangeListener.delete();
			dynamicBackgroundImageBindingValueChangeListener = null;
		}
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateBorder();
		if (getComponent().getDynamicBackgroundImage().isSet() && getComponent().getDynamicBackgroundImage().isValid()) {
			updateDynamicBackgroundImage();
		}
		else {
			updateBackgroundImageFile();
		}
		updateBackgroundImageSizeAdjustment();
	}

	@Override
	public IterationRenderingAdapter<C, C2> getRenderingAdapter() {
		return (IterationRenderingAdapter<C, C2>) super.getRenderingAdapter();
	}

	public abstract FIBLayoutManager<C, C2, ?> makeFIBLayoutManager(Layout layoutType);

	@Override
	protected void addSubComponentsAndDoLayout() {
		try {
			getLayoutManager().doLayout();
		} catch (ClassCastException e) {
			logger.warning("Unexpected ClassCastException during doLayout: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public FIBLayoutManager<C, C2, ?> getLayoutManager() {
		return layoutManager;
	}

	public abstract void updateBorder();

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		update();
	}

	@Override
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
	}

	@Override
	public void updateLayout() {
		// logger.info("relayout panel (caution !!! this is really costly !)" + getComponent());

		if (isDeleted()) {
			return;
		}

		// TODO: please reimplement this and make it more efficient !!!!

		clearContainer();

		getLayoutManager().setLayoutManager(getTechnologyComponent());
		buildSubComponents();

		// updateDataObject(getDataObject());
		// update();
	}

	/**
	 * Remove all components present in this container
	 */
	protected abstract void clearContainer();

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (isDeleted()) {
			return;
		}
		if (evt.getPropertyName().equals(FIBPanel.BORDER_KEY) || evt.getPropertyName().equals(FIBPanel.BORDER_COLOR_KEY)
				|| evt.getPropertyName().equals(FIBPanel.BORDER_TITLE_KEY) || evt.getPropertyName().equals(FIBPanel.BORDER_TOP_KEY)
				|| evt.getPropertyName().equals(FIBPanel.BORDER_LEFT_KEY) || evt.getPropertyName().equals(FIBPanel.BORDER_RIGHT_KEY)
				|| evt.getPropertyName().equals(FIBPanel.BORDER_BOTTOM_KEY) || evt.getPropertyName().equals(FIBPanel.TITLE_FONT_KEY)
				|| evt.getPropertyName().equals(FIBPanel.DARK_LEVEL_KEY)) {
			updateBorder();
		}
		if (evt.getPropertyName().equals(FIBPanel.LAYOUT_KEY)) {
			changeLayout();
		}
		if (evt.getPropertyName().equals(FIBPanel.FLOW_ALIGNMENT_KEY) || evt.getPropertyName().equals(FIBPanel.BOX_LAYOUT_AXIS_KEY)
				|| evt.getPropertyName().equals(FIBPanel.V_GAP_KEY) || evt.getPropertyName().equals(FIBPanel.H_GAP_KEY)
				|| evt.getPropertyName().equals(FIBPanel.ROWS_KEY) || evt.getPropertyName().equals(FIBPanel.COLS_KEY)
				|| evt.getPropertyName().equals(FIBPanel.PROTECT_CONTENT_KEY)) {
			updateLayout();
		}
		if (getComponent() instanceof FIBTab && evt.getPropertyName().equals(FIBTab.TITLE_KEY)) {
			// Arghlll how do we update titles on this.
		}

		if (evt.getPropertyName().equals(FIBPanel.IMAGE_FILE_KEY)) {
			updateBackgroundImageFile();
			// relayoutParentBecauseBackgroundImageChanged();
		}
		else if (evt.getPropertyName().equals(FIBPanel.DYNAMIC_BACKGROUND_IMAGE_KEY)) {
			updateDynamicBackgroundImage();
			// relayoutParentBecauseBackgroundImageChanged();
		}
		else if ((evt.getPropertyName().equals(FIBPanel.SIZE_ADJUSTMENT_KEY)) || (evt.getPropertyName().equals(FIBPanel.IMAGE_HEIGHT_KEY))
				|| (evt.getPropertyName().equals(FIBPanel.IMAGE_WIDTH_KEY))) {
			updateBackgroundImageSizeAdjustment();
			// relayoutParentBecauseBackgroundImageChanged();
		}

		super.propertyChange(evt);
	}

	private Image originalBackgroundImage;
	private Resource loadedImageResource = null;

	protected void updateBackgroundImageSizeAdjustment() {
		// System.out.println("originalImage = " + originalImage);
		if (originalBackgroundImage != null) {
			updateBackgroundImageDefaultSize(originalBackgroundImage);
			getRenderingAdapter().setBackgroundImage(getTechnologyComponent(), originalBackgroundImage, this);
		}
	}

	private void performUpdateBackgroundImage(Image newImage) {
		originalBackgroundImage = newImage;
		updateBackgroundImageDefaultSize(originalBackgroundImage);
		getRenderingAdapter().setBackgroundImage(getTechnologyComponent(), originalBackgroundImage, this);
	}

	protected void updateDynamicBackgroundImage() {
		if (getComponent().getDynamicBackgroundImage().isSet() && getComponent().getDynamicBackgroundImage().isValid()) {
			try {
				Image image = getComponent().getDynamicBackgroundImage().getBindingValue(getBindingEvaluationContext());
				if (notEquals(image, originalBackgroundImage)) {
					performUpdateBackgroundImage(image);
				}
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void updateBackgroundImageFile() {
		if (notEquals(getComponent().getImageFile(), loadedImageResource)) {
			if (getComponent().getImageFile() != null) {
				loadedImageResource = getComponent().getImageFile();
				performUpdateBackgroundImage(ImageUtils.loadImageFromResource(getComponent().getImageFile()));
			}
			else {
				originalBackgroundImage = null;
				loadedImageResource = null;
			}
		}
	}

	protected abstract void updateBackgroundImageDefaultSize(Image image);

}
