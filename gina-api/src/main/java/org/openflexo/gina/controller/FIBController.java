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

package org.openflexo.gina.controller;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.manager.EventManager;
import org.openflexo.gina.manager.Registrable;
import org.openflexo.gina.manager.URID;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBLocalizedDictionary;
import org.openflexo.gina.model.FIBVariable;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.bindings.RuntimeContext;
import org.openflexo.gina.model.listener.FIBMouseClickListener;
import org.openflexo.gina.model.listener.FIBSelectionListener;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.FIBWidgetView;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.gina.view.operator.FIBIterationView.IteratedContents;
import org.openflexo.gina.view.widget.FIBReferencedComponentWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.PropertyChangedSupportDefaultImplementation;
import org.openflexo.toolbox.StringUtils;

/**
 * Represent the controller of an instantiation of a FIBComponent in a particular Window Toolkit context (eg Swing)
 * 
 * @author sylvain
 * 
 */
public class FIBController implements HasPropertyChangeSupport, Registrable {

	static final Logger LOGGER = Logger.getLogger(FIBController.class.getPackage().getName());

	public static FIBController instanciateController(FIBComponent fibComponent, GinaViewFactory<?> viewFactory,
			LocalizedDelegate parentLocalizer) {
		return instanciateController(fibComponent, viewFactory, parentLocalizer, null);
	}

	public static FIBController instanciateController(FIBComponent fibComponent, GinaViewFactory<?> viewFactory,
			LocalizedDelegate parentLocalizer, EventManager recorderManager) {
		FIBController returned = null;
		// System.out.println("Instanciate controller for component: " +
		// fibComponent);
		/*
		 * if (fibComponent != null) {
		 * fibComponent.getFactory().stringRepresentation(fibComponent); }
		 */
		if (fibComponent.getControllerClass() == null) {
			LOGGER.warning("No FIBController class declared for " + fibComponent);
		}

		if (fibComponent.getControllerClass() != null) {

			try {
				try {
					Constructor<? extends FIBController> c = fibComponent.getControllerClass().getConstructor(FIBComponent.class,
							GinaViewFactory.class);
					returned = c.newInstance(fibComponent, viewFactory);
				} catch (NoSuchMethodException e) {
					Constructor<? extends FIBController> c = fibComponent.getControllerClass().getConstructor(FIBComponent.class);
					// System.out.println("Constructor=" + c);
					returned = c.newInstance(fibComponent);
				}
			} catch (SecurityException e) {
				LOGGER.warning("SecurityException: Could not instanciate " + fibComponent.getControllerClass());
			} catch (NoSuchMethodException e) {
				LOGGER.warning("NoSuchMethodException: Could not instanciate " + fibComponent.getControllerClass());
			} catch (IllegalArgumentException e) {
				LOGGER.warning("IllegalArgumentException: Could not instanciate " + fibComponent.getControllerClass());
			} catch (InstantiationException e) {
				LOGGER.warning("InstantiationException: Could not instanciate " + fibComponent.getControllerClass());
			} catch (IllegalAccessException e) {
				LOGGER.warning("IllegalAccessException: Could not instanciate " + fibComponent.getControllerClass());
			} catch (InvocationTargetException e) {
				LOGGER.warning("InvocationTargetException: Could not instanciate " + fibComponent.getControllerClass());
			}
		}
		if (returned == null) {
			returned = new FIBController(fibComponent, viewFactory);
		}
		returned.setParentLocalizer(parentLocalizer);

		if (recorderManager != null) {
			returned.setEventManager(recorderManager);
		}

		return returned;
	}

	public static <F extends FIBComponent, C> FIBView<F, ? extends C> makeView(F fibComponent, GinaViewFactory<C> viewFactory,
			LocalizedDelegate parentLocalizer, IteratedContents<?> context, boolean updateNow) {
		return makeView(fibComponent, viewFactory, instanciateController(fibComponent, viewFactory, parentLocalizer), context, updateNow);
	}

	public static <F extends FIBComponent, C> FIBView<F, ? extends C> makeView(F fibComponent, GinaViewFactory<C> viewFactory,
			FIBController controller, IteratedContents<?> context, boolean updateNow) {
		return (FIBView<F, ? extends C>) controller.buildView(fibComponent, context, updateNow);
	}

	private final FIBComponent rootComponent;
	private final Hashtable<FIBComponent, FIBView<?, ?>> views;
	private FIBSelectable<?> selectionLeader;
	private FIBSelectable<?> lastFocusedSelectable;

	private FIBWidgetView<?, ?, ?> focusedWidget;

	private LocalizedDelegate parentLocalizer = null;

	private EventManager eventManager = null;
	private URID urid = null;

	private GinaViewFactory<?> viewFactory;

	public enum Status {
		RUNNING, VALIDATED, CANCELED, ABORTED, NEXT, BACK, RESET, YES, NO, QUIT, OTHER
	}

	private Status status = Status.RUNNING;

	private final Vector<FIBSelectionListener> selectionListeners;
	private final Vector<FIBMouseClickListener> mouseClickListeners;

	// TODO: check this: Is it this still usefull ?
	@Deprecated
	private MouseEvent mouseEvent;

	private boolean deleted = false;

	private final PropertyChangeSupport pcSupport;
	public static final String DELETED = "deleted";

	public FIBController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
		this.rootComponent = rootComponent;
		pcSupport = new PropertyChangeSupport(this);
		views = new Hashtable<>();
		selectionListeners = new Vector<>();
		mouseClickListeners = new Vector<>();
		this.viewFactory = viewFactory;
	}

	public void delete() {
		if (!deleted) {
			if (getRootView() != null) {
				getRootView().delete();
			}
			// Next for-block should not be necessary because deletion is
			// recursive, but just to be sure
			for (FIBView<?, ?> view : new ArrayList<>(views.values())) {
				view.delete();
			}
			deleted = true;
			getPropertyChangeSupport().firePropertyChange(DELETED, false, true);
		}
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public void setEventManager(EventManager eventManager) {
		if (this.eventManager != null) {
			this.eventManager.unregister(this);
		}
		this.eventManager = eventManager;
		if (this.eventManager != null) {
			this.eventManager.register(this);
		}
	}

	@Override
	public void setURID(URID urid) {
		this.urid = urid;
	}

	@Override
	public URID getURID() {
		return this.urid;
	}

	@Override
	public String getBaseIdentifier() {
		if (rootComponent == null)
			return this.getClass().getName();
		return rootComponent.getBaseIdentifier();
	}

	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	public FIBView<FIBComponent, ?> buildView() {
		FIBView<FIBComponent, ?> returned = buildView(rootComponent, null, true);
		// If data object was previously set, set the value to the root view
		if (dataObject != null) {
			setDataObject(dataObject);
			dataObject = null;
		}
		returned.update();
		return returned;
	}

	public GinaViewFactory<?> getViewFactory() {
		return viewFactory;
	}

	public void setViewFactory(GinaViewFactory<?> viewFactory) {
		this.viewFactory = viewFactory;
	}

	public void registerView(FIBView<?, ?> view) {
		views.put(view.getComponent(), view);
		getPropertyChangeSupport().firePropertyChange(((FIBComponent) view.getComponent()).getName(), null, view.getComponent());
	}

	public void unregisterView(FIBView<?, ?> view) {
		views.remove(view.getComponent());
	}

	public final <M extends FIBComponent> FIBView<M, ?> viewForComponent(M component) {
		if (component == null) {
			return null;
		}
		return (FIBView<M, ?>) views.get(component);
	}

	public FIBView<?, ?> viewForComponent(String componentName) {

		// Includes views from embedded components
		for (FIBView<?, ?> v : getAllViews()) {
			if (StringUtils.isNotEmpty(v.getComponent().getName()) && v.getComponent().getName().equals(componentName)) {
				return v;
			}
		}
		return null;

	}

	public <W extends FIBWidget> FIBWidgetView<W, ?, ?> viewForWidget(W widget) {
		return (FIBWidgetView<W, ?, ?>) views.get(widget);
	}

	public <M extends FIBContainer> FIBContainerView<M, ?, ?> viewForContainer(M container) {
		return (FIBContainerView<M, ?, ?>) views.get(container);
	}

	public Collection<FIBView<?, ?>> getViews() {
		return views.values();
	}

	// Includes views from embedded components
	public List<FIBView<?, ?>> getAllViews() {
		List<FIBView<?, ?>> l = new ArrayList<>();
		l.addAll(views.values());
		for (FIBView<?, ?> v : new ArrayList<>(views.values())) {
			if (v instanceof FIBReferencedComponentWidget) {
				FIBReferencedComponentWidget<?> w = (FIBReferencedComponentWidget<?>) v;
				if (w.getReferencedComponentView() != null) {
					if (w.getReferencedComponentView().getController() == this) {
						System.out.println("Zut alors le controller de " + w.getReferencedComponentView() + " c'est " + this);
					}
					else {
						l.addAll(w.getReferencedComponentView().getController().getAllViews());
					}
				}
			}
		}
		return l;
	}

	public FIBComponent getRootComponent() {
		return rootComponent;
	}

	public FIBView<?, ?> getRootView() {
		return viewForComponent(getRootComponent());
	}

	public Object getVariableValue(String variableName) {
		FIBView<?, ?> rootView = getRootView();
		if (rootView != null) {
			FIBVariable<?> v = getRootComponent().getVariable(variableName);
			if (v != null)
				return rootView.getVariableValue(v);
		}
		return null;
	}

	public void setVariableValue(String variableName, Object aValue) {
		FIBView<?, ?> rootView = getRootView();
		if (rootView != null) {
			FIBVariable<Object> v = getRootComponent().getVariable(variableName);
			if (v != null)
				rootView.setVariableValue(v, aValue);
		}
	}

	// If the root view was not yet instantiated, stores the data object
	private Object dataObject = null;

	@Deprecated
	public Object getDataObject() {
		Object returned = getVariableValue(FIBComponent.DEFAULT_DATA_VARIABLE);
		if (returned != null) {
			return returned;
		}
		return dataObject;
	}

	@Deprecated
	public void setDataObject(Object anObject) {
		if (getRootView() != null && getRootComponent().getVariables().size() > 0) {
			getRootView().setVariableValue((FIBVariable) getRootComponent().getVariables().get(0), anObject);
		}
		else {
			dataObject = anObject;
		}
	}

	@Deprecated
	public void updateWithoutDataObject() {
		setDataObject(null, true);
	}

	@Deprecated
	public void setDataObject(Object anObject, boolean forceUpdate) {
		setDataObject(anObject);
	}

	public final <M extends FIBComponent> FIBView<M, ?> buildView(M fibComponent, RuntimeContext context, boolean updateNow) {
		if (fibComponent instanceof FIBContainer) {
			return (FIBView<M, ?>) getViewFactory().makeContainer((FIBContainer) fibComponent, this, context, updateNow);
		}
		else if (fibComponent instanceof FIBWidget) {
			return (FIBView<M, ?>) getViewFactory().makeWidget((FIBWidget) fibComponent, this, context, updateNow);
		}
		return null;
	}

	public void show() {
		getViewFactory().show(this);
	}

	public void hide() {
		getViewFactory().hide(this);
	}

	public void validateAndDispose() {
		status = Status.VALIDATED;
		getViewFactory().disposeWindow(this);
	}

	public void nextAndDispose() {
		status = Status.NEXT;
		getViewFactory().disposeWindow(this);
	}

	public void backAndDispose() {
		status = Status.BACK;
		getViewFactory().disposeWindow(this);
	}

	public void cancelAndDispose() {
		status = Status.CANCELED;
		getViewFactory().disposeWindow(this);
	}

	public void abortAndDispose() {
		status = Status.ABORTED;
		getViewFactory().disposeWindow(this);
	}

	public void resetAndDispose() {
		status = Status.RESET;
		getViewFactory().disposeWindow(this);
	}

	public void chooseYesAndDispose() {
		status = Status.YES;
		getViewFactory().disposeWindow(this);
	}

	public void chooseNoAndDispose() {
		status = Status.NO;
		getViewFactory().disposeWindow(this);
	}

	public void chooseQuitAndDispose() {
		status = Status.QUIT;
		getViewFactory().disposeWindow(this);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public FIBLocalizedDictionary getLocalizer() {
		return getLocalizerForComponent(null);
	}

	public FIBLocalizedDictionary getLocalizerForComponent(FIBComponent component) {
		if (getRootComponent() != null) {
			FIBLocalizedDictionary returned = getRootComponent().retrieveFIBLocalizedDictionary();
			if (getParentLocalizer() != null) {
				returned.setParent(getParentLocalizer());
			}
			return returned;
		}
		else {
			LOGGER.warning("Could not find localizer");
			return null;
		}
	}

	public String getLocalizedForKey(String key) {
		// TODO: we always are creating a new entry
		// Make this configurable because only relevant in dev mode
		String returned = getLocalizer().localizedForKeyAndLanguage(key, FlexoLocalization.getCurrentLanguage(), true);
		if (returned == null) {
			return key;
		}
		return returned;
	}

	/**
	 * Return parent localizer for component localizer
	 * 
	 * @return
	 */
	public final LocalizedDelegate getParentLocalizer() {
		return parentLocalizer;
	}

	/**
	 * Sets parent localizer for component localizer
	 * 
	 * @param parentLocalizer
	 */
	public void setParentLocalizer(LocalizedDelegate parentLocalizer) {
		this.parentLocalizer = parentLocalizer;
	}

	public void switchToLanguage(Language language) {
		FlexoLocalization.setCurrentLanguage(language);
		getRootView().updateLanguage();
	}

	public void refreshLocalized() {
		getRootComponent().retrieveFIBLocalizedDictionary().refresh();
	}

	public FIBSelectable getSelectionLeader() {
		if (isEmbedded()) {
			return getEmbeddingController().getSelectionLeader();
		}
		return selectionLeader;
	}

	public void setSelectionLeader(FIBSelectable<?> selectionLeader) {
		LOGGER.fine("Selection LEADER is now " + selectionLeader);
		if (isEmbedded()) {
			getEmbeddingController().setSelectionLeader(selectionLeader);
			return;
		}
		this.selectionLeader = selectionLeader;
	}

	public FIBSelectable getLastFocusedSelectable() {
		if (isEmbedded()) {
			return getEmbeddingController().getLastFocusedSelectable();
		}
		return lastFocusedSelectable;
	}

	public void setLastFocusedSelectable(FIBSelectable<?> lastFocusedSelectable) {
		if (isEmbedded()) {
			getEmbeddingController().setLastFocusedSelectable(lastFocusedSelectable);
			return;
		}
		this.lastFocusedSelectable = lastFocusedSelectable;
	}

	public FIBWidgetView getFocusedWidget() {
		if (isEmbedded()) {
			return getEmbeddingController().getFocusedWidget();
		}
		return focusedWidget;
	}

	public void setFocusedWidget(FIBWidgetView newFocusedWidget) {
		if (isEmbedded()) {
			getEmbeddingController().setFocusedWidget(newFocusedWidget);
			return;
		}

		LOGGER.fine("Focused widget is now " + newFocusedWidget.getComponent() + " was="
				+ (focusedWidget != null ? focusedWidget.getComponent() : null));
		if (newFocusedWidget != focusedWidget) {
			FIBWidgetView oldFocusedWidget = focusedWidget;
			focusedWidget = newFocusedWidget;
			if (oldFocusedWidget != null) {
				oldFocusedWidget.getRenderingAdapter().repaint(oldFocusedWidget.getTechnologyComponent());
			}
			if (newFocusedWidget != null) {
				newFocusedWidget.getRenderingAdapter().repaint(newFocusedWidget.getTechnologyComponent());
				if (newFocusedWidget instanceof FIBSelectable) {
					FIBSelectable<?> newFocusedWidgetS = (FIBSelectable) newFocusedWidget;
					setLastFocusedSelectable(newFocusedWidgetS);
					if (getLastFocusedSelectable().synchronizedWithSelection()) {
						setSelectionLeader(newFocusedWidgetS);
						fireSelectionChanged(newFocusedWidgetS);
					}
				}
			}
		}
	}

	public boolean isFocused(FIBWidgetView<?, ?, ?> widget) {
		return focusedWidget == widget;
	}

	public boolean isEmbedded() {
		return (getRootView().getEmbeddingComponent() != null);
	}

	public FIBController getEmbeddingController() {
		if (isEmbedded()) {
			return getRootView().getEmbeddingComponent().getController();
		}
		return null;
	}

	/**
	 * Called from the passed widget.<br>
	 * This means that the widget has a new selection and notifies the FIBController of that.<br>
	 * If the caller (the widget) is the selection leader, then the new selection is reflected all over the whole component.<br>
	 * 
	 * @param widget
	 * @param oldSelection
	 * @param newSelection
	 */
	public <T> void updateSelection(FIBSelectable<T> widget, List<T> oldSelection, List<T> newSelection) {

		// LOGGER.info("updateSelection() dans FIBController with " +
		// newSelection);
		// LOGGER.info("widget=" + widget);
		// LOGGER.info("selectionLeader=" + getSelectionLeader());

		if (isEmbedded() && getEmbeddingController() != null) {
			// When component is embedded, forward this to the parent
			getEmbeddingController().updateSelection(widget, oldSelection, newSelection);
			return;
		}

		// System.out.println("widget=" + widget);
		// System.out.println("getSelectionLeader()=" + getSelectionLeader());

		// Fixed issue with selection not taken under account when component has
		// just been initialized
		if (getSelectionLeader() == null) {
			setSelectionLeader(widget);
		}

		if (widget == getSelectionLeader()) {

			// LOGGER.info("*************** I'm the SELECTION LEADER: " +
			// getSelectionLeader());

			// The caller widget is the selection leader, and should fire
			// selection change event all over the world !
			fireSelectionChanged(widget);
			List<Object> objectsToRemoveFromSelection = new Vector<>();
			List<Object> objectsToAddToSelection = new Vector<>();
			if (oldSelection != null) {
				objectsToRemoveFromSelection.addAll(oldSelection);
			}
			if (newSelection != null) {
				for (Object o : newSelection) {
					if (oldSelection != null && oldSelection.contains(o)) {
						objectsToRemoveFromSelection.remove(o);
					}
					else {
						objectsToAddToSelection.add(o);
					}
				}
			}

			for (FIBView<?, ?> v : getAllViews()) {
				if (v instanceof FIBWidgetView && v instanceof FIBSelectable && v != getSelectionLeader()
						&& ((FIBSelectable<?>) v).synchronizedWithSelection()) {
					FIBSelectable<Object> selectableComponent = (FIBSelectable) v;
					for (Object o : objectsToAddToSelection) {
						if (selectableComponent.mayRepresent(o)) {
							selectableComponent.objectAddedToSelection(o);
						}
					}
					for (Object o : objectsToRemoveFromSelection) {
						// Don't do this for FIBSelectable which are not
						// SelectionLeader !!!
						// Otherwise, if this selectable'selection is the cause
						// of displaying of selection leader
						// the selection leader might disappear
						if (selectableComponent.mayRepresent(o) && selectableComponent == getSelectionLeader()) {
							selectableComponent.objectRemovedFromSelection(o);
						}
					}
				}
			}
		}
	}

	public void objectAddedToSelection(Object o) {

		// LOGGER.info("************** objectAddedToSelection() dans FIBController with "
		// + o);

		LOGGER.fine("FIBController: objectAddedToSelection(): " + o);

		for (FIBView<?, ?> v : getViews()) {
			if (v instanceof FIBWidgetView && v instanceof FIBSelectable && ((FIBSelectable<?>) v).synchronizedWithSelection()) {
				FIBSelectable<Object> selectableComponent = (FIBSelectable) v;
				if (selectableComponent.mayRepresent(o)) {
					selectableComponent.objectAddedToSelection(o);
					if (getSelectionLeader() == null) {
						setSelectionLeader(selectableComponent);
					}
					if (getLastFocusedSelectable() == null) {
						setLastFocusedSelectable(getSelectionLeader());
					}
				}
			}
		}

	}

	public void objectRemovedFromSelection(Object o) {

		// LOGGER.info("************** objectRemovedFromSelection() dans FIBController with "
		// + o);

		LOGGER.fine("FIBController: objectRemovedFromSelection(): " + o);
		for (FIBView<?, ?> v : getViews()) {
			if (v instanceof FIBWidgetView && v instanceof FIBSelectable && ((FIBSelectable<?>) v).synchronizedWithSelection()) {
				FIBSelectable<Object> selectableComponent = (FIBSelectable) v;
				if (selectableComponent.mayRepresent(o)) {
					selectableComponent.objectRemovedFromSelection(o);
				}
			}
		}
	}

	public void selectionCleared() {
		LOGGER.fine("FIBController: selectionCleared()");
		for (FIBView<?, ?> v : getViews()) {
			if (v instanceof FIBWidgetView && v instanceof FIBSelectable && ((FIBSelectable<?>) v).synchronizedWithSelection()) {
				FIBSelectable<Object> selectableComponent = (FIBSelectable) v;
				selectableComponent.selectionResetted();
			}
		}
	}

	/**
	 * Called when a selection leader (a widget managing a selection and declared as the selection leader) has a new selection.<br>
	 * Notify the while world of this new selection (well, use the FIBSelectionListener scheme ;-) ).
	 * 
	 * @param leader
	 */
	private void fireSelectionChanged(FIBSelectable<?> leader) {
		// External synchronization
		for (FIBSelectionListener l : selectionListeners) {
			if (getSelectionLeader() != null) {
				l.selectionChanged(getSelectionLeader().getSelection());
			}
		}
	}

	public void fireMouseClicked(FIBView<?, ?> view, int clickCount) {
		for (FIBMouseClickListener l : mouseClickListeners) {
			l.mouseClicked(view, clickCount);
		}
	}

	public void addSelectionListener(FIBSelectionListener l) {
		selectionListeners.add(l);
	}

	public void removeSelectionListener(FIBSelectionListener l) {
		selectionListeners.remove(l);
	}

	public void addMouseClickListener(FIBMouseClickListener l) {
		mouseClickListeners.add(l);
	}

	public void removeMouseClickListener(FIBMouseClickListener l) {
		mouseClickListeners.remove(l);
	}

	public MouseEvent getMouseEvent() {
		return mouseEvent;
	}

	public void setMouseEvent(MouseEvent mouseEvent) {
		this.mouseEvent = mouseEvent;
	}

	/**
	 * Called when a throwable has been raised during model code invocation. Requires to be overriden, this base implementation just log
	 * exception
	 * 
	 * @param t
	 * @return true is exception was correctely handled
	 */
	public boolean handleException(Throwable t) {
		LOGGER.warning("Unexpected exception raised: " + t.getMessage());
		t.printStackTrace();
		return false;
	}

	public void performCopyAction(Object focused, List<?> selection) {
		LOGGER.warning("COPY action not implemented. Please override this method");
	}

	public void performCutAction(Object focused, List<?> selection) {
		LOGGER.warning("CUT action not implemented. Please override this method");
	}

	public void performPasteAction(Object focused, List<?> selection) {
		LOGGER.warning("PASTE action not implemented. Please override this method");
	}

	public Resource getFIBPanelForObject(Object anObject) {
		if (anObject != null) {
			return getFIBPanelForClass(anObject.getClass());
		}
		return null;
	}

	private final Map<Class<?>, Resource> fibPanelsForClasses = new HashMap<Class<?>, Resource>() {
		@Override
		public Resource get(Object key) {
			if (containsKey(key)) {
				return super.get(key);
			}
			if (key instanceof Class) {
				Class<?> aClass = (Class<?>) key;
				if (aClass.getAnnotation(org.openflexo.gina.annotation.FIBPanel.class) != null) {
					String fibPanelName = aClass.getAnnotation(org.openflexo.gina.annotation.FIBPanel.class).value();
					Resource fibPanelResource = ResourceLocator.locateResource(fibPanelName);
					if (fibPanelResource != null) {
						put(aClass, fibPanelResource);
						return fibPanelResource;
					}
				}
				put(aClass, null);
				return null;
			}
			return null;
		}
	};

	public Resource getFIBPanelForClass(Class<?> aClass) {
		return TypeUtils.objectForClass(aClass, fibPanelsForClasses);
	}

	private SampleData sampleData;

	public SampleData getSampleData() {
		if (sampleData == null) {
			sampleData = new SampleData();
		}
		return sampleData;
	}

	public static class SampleData extends PropertyChangedSupportDefaultImplementation {
		public List<Family> families;
		public List<Person> persons;
		public Person martin, mary, john, martinJr1, martinJr2, martinJr3, martinJr4;

		public SampleData() {
			families = new ArrayList<>();
			persons = new ArrayList<>();
			families.add(makeFamily1());
			families.add(makeFamily2());
			families.add(makeFamily3());
			families.add(makeFamily4());
			families.add(makeFamily5());
		}

		public void logString(String s) {
			System.out.println(s);
		}

		public Family addNewFamily() {
			Person father, mother, child;
			List<Person> children = new ArrayList<>();
			persons.add(father = new Person("Father", 173, 73.7));
			persons.add(mother = new Person("Mother", 165, 57.0));
			persons.add(child = new Person("Child", 107, 26.3));
			children.add(child);
			Family returned = new Family(father, mother, children);
			families.add(returned);
			System.out.println("New family created: " + returned);
			getPropertyChangeSupport().firePropertyChange("families", null, returned);
			return returned;
		}

		private Family makeFamily1() {
			List<Person> children = new ArrayList<>();
			persons.add(martin = new Person("Martin", 173, 73.7));
			persons.add(mary = new Person("Mary", 165, 57.0));
			persons.add(john = new Person("John", 107, 26.3));
			persons.add(martinJr1 = new Person("Martin Jr 1", 97, 19.2));
			persons.add(martinJr2 = new Person("Martin Jr 2", 95, 18.7));
			persons.add(martinJr3 = new Person("Martin Jr 3", 74, 10.2));
			persons.add(martinJr4 = new Person("Martin Jr 4", 57, 5.2));
			children.add(john);
			children.add(martinJr1);
			children.add(martinJr2);
			children.add(martinJr3);
			children.add(martinJr4);
			return new Family(martin, mary, children);
		}

		private static Family makeFamily2() {
			Person tarzan, jane, cheeta;
			List<Person> children = new ArrayList<>();
			tarzan = new Person("Tarzan", 187, 92.7);
			jane = new Person("Jane", 175, 62.0);
			cheeta = new Person("Cheeta", 88, 26.3);
			children.add(cheeta);
			return new Family(tarzan, jane, children);
		}

		private static Family makeFamily3() {
			Person romeo, juliette, romeoJr1, romeoJr2, romeoJr3;
			List<Person> children = new ArrayList<>();
			romeo = new Person("Romeo", 173, 82.7);
			juliette = new Person("Juliette", 165, 52.0);
			romeoJr1 = new Person("Romeo Jr 1", 97, 19.2);
			romeoJr2 = new Person("Romeo Jr 2", 95, 18.7);
			romeoJr3 = new Person("Romeo Jr 3", 74, 10.2);
			children.add(romeoJr1);
			children.add(romeoJr2);
			children.add(romeoJr3);
			return new Family(romeo, juliette, children);
		}

		private static Family makeFamily4() {
			Person donald, daisy, riri, fifi, loulou;
			List<Person> children = new ArrayList<>();
			donald = new Person("Donald", 135, 53.5);
			daisy = new Person("Daisy", 123, 48.6);
			riri = new Person("Riri", 74, 10.3);
			fifi = new Person("Fifi", 74, 10.1);
			loulou = new Person("Loulou", 74, 10.2);
			children.add(riri);
			children.add(fifi);
			children.add(loulou);
			return new Family(donald, daisy, children);
		}

		private static Family makeFamily5() {
			Person adam, eve, cain, abel, seth;
			List<Person> children = new ArrayList<>();
			adam = new Person("Adam", 178, 84.7);
			eve = new Person("Eve", 168, 54.0);
			cain = new Person("Cain", 130, 37.2);
			abel = new Person("Abel", 127, 28.1);
			seth = new Person("Seth", 107, 10.2);
			children.add(cain);
			children.add(abel);
			children.add(seth);
			return new Family(adam, eve, children);
		}

		private int personId = 2;

		public Person addPerson() {
			Person newPerson = new Person("Person" + personId, 170, 60);
			personId++;
			persons.add(newPerson);
			getPropertyChangeSupport().firePropertyChange("persons", null, newPerson);
			return newPerson;
		}

		public Person deletePerson(Person person) {
			persons.remove(person);
			getPropertyChangeSupport().firePropertyChange("persons", person, null);
			return person;
		}

		public static class Person extends PropertyChangedSupportDefaultImplementation {
			private String name;
			private int size;
			private double weight;

			public Person(String name, int size, double weight) {
				super();
				this.name = name;
				this.size = size;
				this.weight = weight;
			}

			public void maryTo(Person otherPerson) {
				System.out.println("Mary " + this + " with " + otherPerson);
			}

			public boolean canMaryWith(Person otherPerson) {
				System.out.println("Can i mary with " + otherPerson + " ?");
				/*if ((this == mary && otherPerson == john) || (this == mary && otherPerson == john)) {
					return true;
				}*/
				return true;
			}

			@Override
			public String toString() {
				return name;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				if ((name == null && this.name != null) || (name != null && !name.equals(this.name))) {
					String oldValue = this.name;
					this.name = name;
					getPropertyChangeSupport().firePropertyChange("name", oldValue, name);
				}
			}

			public int getSize() {
				return size;
			}

			public void setSize(int size) {
				if (this.size != size) {
					int oldValue = this.size;
					this.size = size;
					getPropertyChangeSupport().firePropertyChange("size", oldValue, size);
				}
			}

			public double getWeight() {
				return weight;
			}

			public void setWeight(double weight) {
				if (this.weight != weight) {
					double oldValue = this.weight;
					this.weight = weight;
					getPropertyChangeSupport().firePropertyChange("weight", oldValue, weight);
				}
			}
		}

		public static class Family extends PropertyChangedSupportDefaultImplementation {
			public Person father;
			public Person mother;
			public List<Person> children;
			public List<Person> persons;

			public Family(Person father, Person mother, List<Person> children) {
				super();
				this.father = father;
				this.mother = mother;
				this.children = children;
				persons = new ArrayList<>();
				persons.add(father);
				persons.add(mother);
				persons.addAll(children);
			}

			private int personId = 2;

			public Person addPerson() {
				System.out.println("New person created in " + this);
				Person newPerson = new Person("Person" + personId, 170, 60);
				personId++;
				persons.add(newPerson);
				getPropertyChangeSupport().firePropertyChange("persons", null, newPerson);
				return newPerson;
			}

			public Person deletePerson(Person person) {
				persons.remove(person);
				getPropertyChangeSupport().firePropertyChange("persons", person, null);
				return person;
			}

			@Override
			public String toString() {
				return "Family:" + father;
			}
		}

	}

	public static String askForString(String msg) throws HeadlessException {
		return showInputDialog(msg, FlexoLocalization.getMainLocalizer().localizedForKey("information"), JOptionPane.QUESTION_MESSAGE);
	}

	private static String showInputDialog(Object message, String title, int messageType) throws HeadlessException {
		Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
		return (String) showInputDialog(activeWindow, message, title, messageType, null, null, null);
	}

	private static Object showInputDialog(Component parentComponent, Object message, String title, int messageType, Icon icon,
			Object[] selectionValues, Object initialSelectionValue) throws HeadlessException {
		Object[] availableOptions = new Object[] { FlexoLocalization.getMainLocalizer().localizedForKey("OK"),
				FlexoLocalization.getMainLocalizer().localizedForKey("cancel") };
		JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.OK_CANCEL_OPTION, icon, availableOptions, availableOptions[0]);
		pane.setWantsInput(true);
		pane.setSelectionValues(selectionValues);
		pane.setInitialSelectionValue(initialSelectionValue);
		// pane.setComponentOrientation((parentComponent == null ? FlexoFrame.getActiveFrame() :
		// parentComponent).getComponentOrientation());
		pane.setMessageType(messageType);
		JDialog dialog = pane.createDialog(parentComponent, title);
		pane.selectInitialValue();

		dialog.validate();
		dialog.pack();
		if (parentComponent == null) {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			dialog.setLocation((dim.width - dialog.getSize().width) / 2, (dim.height - dialog.getSize().height) / 2);
		}

		dialog.setVisible(true);
		dialog.dispose();

		Object val = pane.getValue();

		for (int counter = 0, maxCounter = availableOptions.length; counter < maxCounter; counter++) {
			if (availableOptions[counter].equals(val)) {
				if (counter == 1) {
					return null;
				}
			}

		}

		Object value = pane.getInputValue();
		if (value == JOptionPane.UNINITIALIZED_VALUE) {
			return null;
		}
		return value;
	}

}
