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

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.manager.EventManager;
import org.openflexo.gina.manager.Registerable;
import org.openflexo.gina.manager.URID;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBLocalizedDictionary;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.listener.FIBMouseClickListener;
import org.openflexo.gina.model.listener.FIBSelectionListener;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.FIBWidgetView;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.gina.view.widget.FIBCustomWidget;
import org.openflexo.gina.view.widget.FIBReferencedComponentWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;

/**
 * Represent the controller of an instantiation of a FIBComponent in a particular Window Toolkit context (eg Swing)
 * 
 * @author sylvain
 * 
 */
public class FIBController implements BindingEvaluationContext, Observer, PropertyChangeListener, HasPropertyChangeSupport, Registerable {

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
		if (fibComponent.getControllerClass() != null) {

			try {
				// System.out.println("Class=" +
				// fibComponent.getControllerClass());
				Constructor<? extends FIBController> c = fibComponent.getControllerClass().getConstructor(FIBComponent.class,
						GinaViewFactory.class);
				// System.out.println("Constructor=" + c);
				returned = c.newInstance(fibComponent, viewFactory);
				// System.out.println("returned=" + returned);
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
			LocalizedDelegate parentLocalizer) {
		return makeView(fibComponent, viewFactory, instanciateController(fibComponent, viewFactory, parentLocalizer));
	}

	public static <F extends FIBComponent, C> FIBView<F, ? extends C> makeView(F fibComponent, GinaViewFactory<C> viewFactory,
			FIBController controller) {
		return (FIBView<F, ? extends C>) controller.buildView(fibComponent);
	}

	private Object dataObject;
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
		views = new Hashtable<FIBComponent, FIBView<?, ?>>();
		selectionListeners = new Vector<FIBSelectionListener>();
		mouseClickListeners = new Vector<FIBMouseClickListener>();
		this.viewFactory = viewFactory;
	}

	public void delete() {
		if (!deleted) {
			if (getRootView() != null) {
				getRootView().delete();
			}
			// Next for-block should not be necessary because deletion is
			// recursive, but just to be sure
			for (FIBView<?, ?> view : new ArrayList<FIBView<?, ?>>(views.values())) {
				view.delete();
			}
			if (dataObject instanceof Observable) {
				((Observable) dataObject).deleteObserver(this);
			}
			dataObject = null;
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
		FIBView<FIBComponent, ?> returned = buildView(rootComponent);
		returned.update();
		return returned;
	}

	public GinaViewFactory<?> getViewFactory() {
		return viewFactory;
	}

	public void setViewFactory(GinaViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}

	public void registerView(FIBView<?, ?> view) {
		views.put(view.getComponent(), view);
		getPropertyChangeSupport().firePropertyChange(((FIBComponent) view.getComponent()).getName(), null, view.getComponent());
	}

	public void unregisterView(FIBView<?, ?> view) {
		views.remove(view.getComponent());
	}

	public <M extends FIBComponent> FIBView<M, ?> viewForComponent(M component) {
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
		List<FIBView<?, ?>> l = new ArrayList<FIBView<?, ?>>();
		l.addAll(views.values());
		for (FIBView<?, ?> v : views.values()) {
			if (v instanceof FIBReferencedComponentWidget) {
				FIBReferencedComponentWidget<?> w = (FIBReferencedComponentWidget<?>) v;
				if (w.getReferencedComponentView() != null) {
					l.addAll(w.getReferencedComponentView().getController().getAllViews());
				} /*
					* else { System.out.println("No view for " +
					* FIBLibrary.instance
					* ().getFIBModelFactory().stringRepresentation
					* (w.getReferencedComponent())); }
					*/
			}
		}
		return l;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName() == null) {
			return null;
		}
		if (variable.getVariableName().equals("data")) {
			return dataObject;
		}
		for (FIBComponent c : new ArrayList<FIBComponent>(views.keySet())) {
			if (variable.getVariableName().equals(c.getName())) {
				FIBView<?, ?> returned = viewForComponent(c);
				if (returned instanceof FIBCustomWidget) {
					return ((FIBCustomWidget<?, ?, ?>) returned).getTechnologyComponent();
				}
				return returned;
			}
		}
		if (variable.getVariableName().equals("controller")) {
			return this;
		}
		return null;
	}

	public FIBComponent getRootComponent() {
		return rootComponent;
	}

	public FIBView getRootView() {
		return viewForComponent(getRootComponent());
	}

	public Object getDataObject() {
		return dataObject;
	}

	public void setDataObject(Object anObject) {
		setDataObject(anObject, false);
	}

	public void updateWithoutDataObject() {
		setDataObject(null, true);
	}

	public void setDataObject(Object anObject, boolean forceUpdate) {
		if (forceUpdate || anObject != dataObject) {
			Object oldDataObject = dataObject;
			if (oldDataObject instanceof HasPropertyChangeSupport
					&& ((HasPropertyChangeSupport) oldDataObject).getPropertyChangeSupport() != null) {
				((HasPropertyChangeSupport) oldDataObject).getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			else if (oldDataObject instanceof Observable) {
				((Observable) oldDataObject).deleteObserver(this);
			}
			dataObject = anObject;

			// Attempt to reduce time required to update component
			// I suspect that if the 'data' notification is correctely handled,
			// this is no more necessary
			/*
			 * if (getRootView() != null) { getRootView().update(); }
			 */
			if (dataObject instanceof HasPropertyChangeSupport) {
				((HasPropertyChangeSupport) dataObject).getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			else if (dataObject instanceof Observable) {
				((Observable) dataObject).addObserver(this);
			}
			// setChanged();
			// notifyObservers();
			getPropertyChangeSupport().firePropertyChange("data", oldDataObject, anObject);
		}
	}

	public final <M extends FIBComponent> FIBView<M, ?> buildView(M fibComponent) {
		if (fibComponent instanceof FIBContainer) {
			return (FIBView<M, ?>) getViewFactory().makeContainer((FIBContainer) fibComponent, this);
		}
		else if (fibComponent instanceof FIBWidget) {
			return (FIBView<M, ?>) getViewFactory().makeWidget((FIBWidget) fibComponent, this);
		}
		return null;
	}

	/**
	 * Build FIBWidgetView given supplied {@link FIBWidget}
	 * 
	 * Also add MouseListenener and KeyListener
	 * 
	 * @param fibWidget
	 * @return
	 */
	@Override
	public void update(Observable o, Object arg) {
		// System.out.println("Received "+arg);
		// getRootView().updateDataObject(dataObject);
		// getRootView().update();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// getRootView().update();
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
		String returned = getLocalizer().getLocalizedForKeyAndLanguage(key, FlexoLocalization.getCurrentLanguage());
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

	/*
	 * public void searchNewLocalizationEntries() {
	 * logger.fine("Search new localization entries"); Language currentLanguage
	 * = FlexoLocalization.getCurrentLanguage();
	 * getRootComponent().retrieveFIBLocalizedDictionary
	 * ().beginSearchNewLocalizationEntries(); for (Language language :
	 * Language.availableValues()) { switchToLanguage(language); }
	 * getRootComponent
	 * ().retrieveFIBLocalizedDictionary().endSearchNewLocalizationEntries();
	 * getRootComponent().retrieveFIBLocalizedDictionary().refresh();
	 * switchToLanguage(currentLanguage); // setChanged(); // notifyObservers();
	 * }
	 */

	public void refreshLocalized() {
		getRootComponent().retrieveFIBLocalizedDictionary().refresh();
	}

	public FIBSelectable getSelectionLeader() {
		if (isEmbedded()) {
			return getEmbeddingController().getSelectionLeader();
		}
		return selectionLeader;
	}

	public void setSelectionLeader(FIBSelectable selectionLeader) {
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

	public void setLastFocusedSelectable(FIBSelectable lastFocusedSelectable) {
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
					setLastFocusedSelectable((FIBSelectable) newFocusedWidget);
					if (getLastFocusedSelectable().synchronizedWithSelection()) {
						setSelectionLeader((FIBSelectable) newFocusedWidget);
						fireSelectionChanged((FIBSelectable) newFocusedWidget);
					}
				}
			}
		}
	}

	public boolean isFocused(FIBWidgetView widget) {
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
			List<Object> objectsToRemoveFromSelection = new Vector<Object>();
			List<Object> objectsToAddToSelection = new Vector<Object>();
			if (oldSelection != null) {
				objectsToRemoveFromSelection.addAll(oldSelection);
			}
			for (Object o : newSelection) {
				if (oldSelection != null && oldSelection.contains(o)) {
					objectsToRemoveFromSelection.remove(o);
				}
				else {
					objectsToAddToSelection.add(o);
				}
			}

			for (FIBView<?, ?> v : getAllViews()) {
				if (v instanceof FIBWidgetView && v instanceof FIBSelectable && v != getSelectionLeader()
						&& ((FIBSelectable) v).synchronizedWithSelection()) {
					FIBSelectable selectableComponent = (FIBSelectable) ((FIBWidgetView<?, ?, ?>) v);
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
						// the selection leader might disapppear
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
			if (v instanceof FIBWidgetView && v instanceof FIBSelectable && ((FIBSelectable) v).synchronizedWithSelection()) {
				FIBSelectable selectableComponent = (FIBSelectable) v;
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
			if (v instanceof FIBWidgetView && v instanceof FIBSelectable && ((FIBSelectable) v).synchronizedWithSelection()) {
				FIBSelectable selectableComponent = (FIBSelectable) v;
				if (selectableComponent.mayRepresent(o)) {
					selectableComponent.objectRemovedFromSelection(o);
				}
			}
		}
	}

	public void selectionCleared() {
		LOGGER.fine("FIBController: selectionCleared()");
		for (FIBView<?, ?> v : getViews()) {
			if (v instanceof FIBWidgetView && v instanceof FIBSelectable && ((FIBSelectable) v).synchronizedWithSelection()) {
				FIBSelectable selectableComponent = (FIBSelectable) v;
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
	private void fireSelectionChanged(FIBSelectable leader) {
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

		/*
		 * System.out .println("Searching FIBPanel for " + anObject + (anObject
		 * != null ? " class=" + anObject.getClass() + " returning " +
		 * getFIBPanelForClass(anObject.getClass()) : ""));
		 */

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
				// System.out.println("Searching FIBPanel for " + aClass);
				if (aClass.getAnnotation(org.openflexo.gina.annotation.FIBPanel.class) != null) {
					// System.out.println("Found annotation " +
					// aClass.getAnnotation(org.openflexo.gina.annotation.FIBPanel.class));
					String fibPanelName = aClass.getAnnotation(org.openflexo.gina.annotation.FIBPanel.class).value();
					Resource fibPanelResource = ResourceLocator.locateResource(fibPanelName);
					// System.out.println("fibPanelResource=" +
					// fibPanelResource);
					if (fibPanelResource != null) {
						// logger.info("Found " + fibPanel);
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

	/*
	 * public static void main(String[] args) { FlexoFIBController newController
	 * = new FlexoFIBController(null); System.out.println("Result: " +
	 * newController.getFIBPanelForClass(DeclareFlexoRole.class)); }
	 */

	public Resource getFIBPanelForClass(Class<?> aClass) {

		return TypeUtils.objectForClass(aClass, fibPanelsForClasses);
	}

}
