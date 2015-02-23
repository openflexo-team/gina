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

package org.openflexo.fib.controller;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBCheckboxList;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBEditor;
import org.openflexo.fib.model.FIBEditorPane;
import org.openflexo.fib.model.FIBFile;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBImage;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.model.FIBLocalizedDictionary;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.model.FIBReferencedComponent;
import org.openflexo.fib.model.FIBSplitPanel;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBTextPane;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.model.listener.FIBMouseClickListener;
import org.openflexo.fib.model.listener.FIBSelectionListener;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.fib.view.container.FIBSplitPanelView;
import org.openflexo.fib.view.container.FIBTabPanelView;
import org.openflexo.fib.view.container.FIBTabView;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.fib.view.widget.FIBButtonWidget;
import org.openflexo.fib.view.widget.FIBCheckBoxWidget;
import org.openflexo.fib.view.widget.FIBCheckboxListWidget;
import org.openflexo.fib.view.widget.FIBColorWidget;
import org.openflexo.fib.view.widget.FIBCustomWidget;
import org.openflexo.fib.view.widget.FIBDropDownWidget;
import org.openflexo.fib.view.widget.FIBEditorPaneWidget;
import org.openflexo.fib.view.widget.FIBEditorWidget;
import org.openflexo.fib.view.widget.FIBFileWidget;
import org.openflexo.fib.view.widget.FIBFontWidget;
import org.openflexo.fib.view.widget.FIBHtmlEditorWidget;
import org.openflexo.fib.view.widget.FIBImageWidget;
import org.openflexo.fib.view.widget.FIBLabelWidget;
import org.openflexo.fib.view.widget.FIBListWidget;
import org.openflexo.fib.view.widget.FIBNumberWidget;
import org.openflexo.fib.view.widget.FIBRadioButtonListWidget;
import org.openflexo.fib.view.widget.FIBReferencedComponentWidget;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.fib.view.widget.FIBTextAreaWidget;
import org.openflexo.fib.view.widget.FIBTextFieldWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * Represent the controller of an instantiation of a FIBComponent in a particular Window Toolkit context (eg Swing)
 * 
 * @author sylvain
 * 
 */
public class FIBController /*extends Observable*/implements BindingEvaluationContext, Observer, PropertyChangeListener,
		HasPropertyChangeSupport {

	private static final Logger LOGGER = Logger.getLogger(FIBController.class.getPackage().getName());

	private Object dataObject;
	private final FIBComponent rootComponent;
	private final Hashtable<FIBComponent, FIBView<?, ?, ?>> views;
	private FIBSelectable selectionLeader;
	private FIBSelectable lastFocusedSelectable;

	private FIBWidgetView<?, ?, ?> focusedWidget;

	private LocalizedDelegate parentLocalizer = null;

	private FIBViewFactory viewFactory;

	public enum Status {
		RUNNING, VALIDATED, CANCELED, ABORTED, NEXT, BACK, RESET, YES, NO, QUIT, OTHER
	}

	private Status status = Status.RUNNING;

	private final Vector<FIBSelectionListener> selectionListeners;
	private final Vector<FIBMouseClickListener> mouseClickListeners;

	private MouseEvent mouseEvent;

	private boolean deleted = false;

	private final PropertyChangeSupport pcSupport;
	public static final String DELETED = "deleted";

	public FIBController(FIBComponent rootComponent) {
		this.rootComponent = rootComponent;
		pcSupport = new PropertyChangeSupport(this);
		views = new Hashtable<FIBComponent, FIBView<?, ?, ?>>();
		selectionListeners = new Vector<FIBSelectionListener>();
		mouseClickListeners = new Vector<FIBMouseClickListener>();
		viewFactory = new DefaultFIBViewFactory();
	}

	public void delete() {
		if (!deleted) {
			if (getRootView() != null) {
				getRootView().delete();
			}
			// Next for-block should not be necessary because deletion is recursive, but just to be sure
			for (FIBView<?, ?, ?> view : new ArrayList<FIBView<?, ?, ?>>(views.values())) {
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

	public FIBView<FIBComponent, ?, ?> buildView() {

		FIBView<FIBComponent, ?, ?> returned = buildView(rootComponent);
		returned.update();
		return returned;
	}

	public FIBViewFactory getViewFactory() {
		return viewFactory;
	}

	public void setViewFactory(FIBViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}

	public void registerView(FIBView<?, ?, ?> view) {
		views.put(view.getComponent(), view);
		getPropertyChangeSupport().firePropertyChange(((FIBComponent) view.getComponent()).getName(), null, view.getComponent());
	}

	public void unregisterView(FIBView<?, ?, ?> view) {
		views.remove(view.getComponent());
	}

	public FIBView<?, ?, ?> viewForComponent(FIBComponent component) {
		return views.get(component);
	}

	public FIBView<?, ?, ?> viewForComponent(String componentName) {

		// Includes views from embedded components
		for (FIBView<?, ?, ?> v : getAllViews()) {
			if (StringUtils.isNotEmpty(v.getComponent().getName()) && v.getComponent().getName().equals(componentName)) {
				return v;
			}
		}
		return null;

	}

	public Collection<FIBView<?, ?, ?>> getViews() {
		return views.values();
	}

	// Includes views from embedded components
	public List<FIBView<?, ?, ?>> getAllViews() {
		List<FIBView<?, ?, ?>> l = new ArrayList<FIBView<?, ?, ?>>();
		l.addAll(views.values());
		for (FIBView<?, ?, ?> v : views.values()) {
			if (v instanceof FIBReferencedComponentWidget) {
				FIBReferencedComponentWidget w = (FIBReferencedComponentWidget) v;
				if (w.getReferencedComponentView() != null) {
					l.addAll(w.getReferencedComponentView().getController().getAllViews());
				} /*else {
					System.out.println("No view for "
							+ FIBLibrary.instance().getFIBModelFactory().stringRepresentation(w.getReferencedComponent()));
					}*/
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
		for (FIBComponent c : views.keySet()) {
			if (variable.getVariableName().equals(c.getName())) {
				return viewForComponent(c) /*.getDynamicModel()*/;
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
			if (oldDataObject instanceof HasPropertyChangeSupport) {
				((HasPropertyChangeSupport) oldDataObject).getPropertyChangeSupport().removePropertyChangeListener(this);
			} else if (oldDataObject instanceof Observable) {
				((Observable) oldDataObject).deleteObserver(this);
			}
			dataObject = anObject;
			if (getRootView() != null) {
				getRootView().update();
			}
			if (dataObject instanceof HasPropertyChangeSupport) {
				((HasPropertyChangeSupport) dataObject).getPropertyChangeSupport().addPropertyChangeListener(this);
			} else if (dataObject instanceof Observable) {
				((Observable) dataObject).addObserver(this);
			}
			// setChanged();
			// notifyObservers();
			getPropertyChangeSupport().firePropertyChange("data", oldDataObject, anObject);
		}
	}

	public static FIBController instanciateController(FIBComponent fibComponent, LocalizedDelegate parentLocalizer) {
		FIBController returned = null;
		// System.out.println("Instanciate controller for component: " + fibComponent);
		/*if (fibComponent != null) {
			fibComponent.getFactory().stringRepresentation(fibComponent);
		}*/
		if (fibComponent.getControllerClass() != null) {
			try {
				// System.out.println("Class=" + fibComponent.getControllerClass());
				Constructor<? extends FIBController> c = fibComponent.getControllerClass().getConstructor(FIBComponent.class);
				// System.out.println("Constructor=" + c);
				returned = c.newInstance(fibComponent);
				// System.out.println("returned=" + returned);
			} catch (SecurityException e) {
				LOGGER.warning("SecurityException: Could not instanciate " + fibComponent.getControllerClass());
			} catch (NoSuchMethodException e) {
				// Thread.dumpStack();
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
			returned = new FIBController(fibComponent);
		}
		returned.setParentLocalizer(parentLocalizer);
		return returned;
	}

	public static FIBView makeView(FIBComponent fibComponent, LocalizedDelegate parentLocalizer) {
		return makeView(fibComponent, instanciateController(fibComponent, parentLocalizer));
	}

	public static FIBView makeView(FIBComponent fibComponent, FIBController controller) {
		return controller.buildView(fibComponent);
	}

	protected static void recursivelyAddEditorLauncher(EditorLauncher editorLauncher,
			FIBContainerView<? extends FIBContainer, JComponent, ?> container) {
		container.getJComponent().addMouseListener(editorLauncher);
		for (FIBComponent c : container.getComponent().getSubComponents()) {
			FIBView<?, ?, ?> subView = container.getController().viewForComponent(c);
			if (subView instanceof FIBContainerView) {
				recursivelyAddEditorLauncher(editorLauncher, (FIBContainerView) subView);
			}
		}
	}

	public final <M extends FIBComponent> FIBView<M, ?, ?> buildView(M fibComponent) {
		if (fibComponent instanceof FIBContainer) {
			return buildContainer((FIBContainer) fibComponent);
		} else if (fibComponent instanceof FIBWidget) {
			FIBWidgetView widgetView = buildWidget((FIBWidget) fibComponent);
			if (widgetView != null) {
				return widgetView;
			}
		}
		return null;
	}

	protected final FIBView buildContainer(FIBContainer fibContainer) {
		FIBView returned = makeContainer(fibContainer);
		if (returned != null && fibContainer.isRootComponent()) {
			if (returned instanceof FIBContainerView && allowsFIBEdition()) {
				EditorLauncher editorLauncher = new EditorLauncher(this, fibContainer);
				recursivelyAddEditorLauncher(editorLauncher, (FIBContainerView) returned);
			}
			return returned;
		}
		if (returned != null) {
			returned.updateGraphicalProperties();
		}
		return returned;
	}

	protected final FIBView makeContainer(FIBContainer fibContainer) {
		try {
			return getViewFactory().makeContainer(fibContainer);
		} catch (RuntimeException e) {
			LOGGER.warning("Unexpected exception " + e.getMessage() + ". See logs for details.");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Implementation of a MouseAdapter which ignore SingleClick events when DoubleClick was performed<br>
	 * Also perform mouse binding execution
	 * 
	 * @author sylvain
	 * 
	 */
	protected class FIBMouseAdapter extends MouseAdapter {

		// private boolean wasDoubleClick = false;
		// private Timer timer;
		private final FIBWidgetView widgetView;
		private final FIBWidget fibWidget;
		private boolean isPopupTrigger = false;

		public FIBMouseAdapter(FIBWidgetView widgetView, FIBWidget fibWidget) {
			this.widgetView = widgetView;
			this.fibWidget = fibWidget;
		}

		protected void fireSingleClick(MouseEvent e) {
			mouseEvent = e;
			fireMouseClicked(widgetView, 1);
			if (fibWidget.hasRightClickAction()
					&& (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3 || ToolBox.isMacOS() && isPopupTrigger)) {
				// Detected right-click associated with action
				widgetView.applyRightClickAction(e);
			} else if (fibWidget.hasClickAction()) {
				// Detected click associated with action
				widgetView.applySingleClickAction(e);
			}
		}

		protected void fireDoubleClick(MouseEvent e) {
			mouseEvent = e;
			fireMouseClicked(widgetView, 2);
			if (fibWidget.hasDoubleClickAction()) {
				// Detected double-click associated with action
				widgetView.applyDoubleClickAction(e);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			mouseEvent = e;
			if (e.getClickCount() == 2) {
				// wasDoubleClick = true;
				fireDoubleClick(mouseEvent);
			} else if (e.getClickCount() == 1) {
				// wasDoubleClick = true;
				fireSingleClick(mouseEvent);
			}/* {
				Integer timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
				timer = new Timer(timerinterval.intValue(), new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						if (wasDoubleClick) {
							wasDoubleClick = false; // reset flag
						} else {
							fireSingleClick(mouseEvent);
						}
					}
				});
				timer.setRepeats(false);
				timer.start();

				}*/
			isPopupTrigger = false;
		}

		/*@Override
		public void mouseReleased(MouseEvent e) {
			isPopupTrigger = true;
		}*/

	}

	/**
	 * Build FIBWidgetView given supplied {@link FIBWidget}
	 * 
	 * Also add MouseListenener and KeyListener
	 * 
	 * @param fibWidget
	 * @return
	 */
	protected final FIBWidgetView buildWidget(final FIBWidget fibWidget) {
		final FIBWidgetView returned = makeWidget(fibWidget);
		returned.getDynamicJComponent().addMouseListener(new FIBMouseAdapter(returned, fibWidget));

		returned.getDynamicJComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (fibWidget.hasEnterPressedAction() && e.getKeyCode() == KeyEvent.VK_ENTER) {
					// Detected double-click associated with action
					try {
						fibWidget.getEnterPressedAction().execute(returned.getBindingEvaluationContext());
					} catch (TypeMismatchException e1) {
						e1.printStackTrace();
					} catch (NullReferenceException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		if (StringUtils.isNotEmpty(fibWidget.getTooltipText())) {
			returned.getDynamicJComponent().setToolTipText(fibWidget.getTooltipText());
		}
		returned.updateGraphicalProperties();
		return returned;
	}

	protected final FIBWidgetView makeWidget(FIBWidget fibWidget) {
		try {
			return getViewFactory().makeWidget(fibWidget);
		} catch (RuntimeException e) {
			LOGGER.warning("Unexpected exception " + e.getMessage() + ". See logs for details.");
			e.printStackTrace();
			return null;
		}
	}

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
		Window w = retrieveWindow();
		if (w != null) {
			w.setVisible(true);
		}
	}

	public void hide() {
		Window w = retrieveWindow();
		if (w != null) {
			w.setVisible(false);
		}
	}

	private Window retrieveWindow() {
		Component c = SwingUtilities.getRoot(getRootView().getJComponent());
		if (c instanceof Window) {
			return (Window) c;
		}
		return null;
	}

	public void validateAndDispose() {
		status = Status.VALIDATED;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void nextAndDispose() {
		status = Status.NEXT;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void backAndDispose() {
		status = Status.BACK;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void cancelAndDispose() {
		status = Status.CANCELED;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void abortAndDispose() {
		status = Status.ABORTED;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void resetAndDispose() {
		status = Status.RESET;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void chooseYesAndDispose() {
		status = Status.YES;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void chooseNoAndDispose() {
		status = Status.NO;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
	}

	public void chooseQuitAndDispose() {
		status = Status.QUIT;
		Window w = retrieveWindow();
		if (w != null) {
			w.dispose();
		}
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
		} else {
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

	/*public void searchNewLocalizationEntries() {
		logger.fine("Search new localization entries");
		Language currentLanguage = FlexoLocalization.getCurrentLanguage();
		getRootComponent().retrieveFIBLocalizedDictionary().beginSearchNewLocalizationEntries();
		for (Language language : Language.availableValues()) {
			switchToLanguage(language);
		}
		getRootComponent().retrieveFIBLocalizedDictionary().endSearchNewLocalizationEntries();
		getRootComponent().retrieveFIBLocalizedDictionary().refresh();
		switchToLanguage(currentLanguage);
		// setChanged();
		// notifyObservers();
	}*/

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
				oldFocusedWidget.getJComponent().repaint();
			}
			if (newFocusedWidget != null) {
				newFocusedWidget.getJComponent().repaint();
				if (newFocusedWidget.isSelectableComponent()) {
					setLastFocusedSelectable(newFocusedWidget.getSelectableComponent());
					if (getLastFocusedSelectable().synchronizedWithSelection()) {
						setSelectionLeader(newFocusedWidget.getSelectableComponent());
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

		// LOGGER.info("updateSelection() dans FIBController with " + newSelection);
		// LOGGER.info("widget=" + widget);
		// LOGGER.info("selectionLeader=" + getSelectionLeader());

		if (isEmbedded()) {
			// When component is embedded, forward this to the parent
			getEmbeddingController().updateSelection(widget, oldSelection, newSelection);
			return;
		}

		if (widget == getSelectionLeader()) {

			// LOGGER.info("*************** I'm the SELECTION LEADER: " + getSelectionLeader());

			// The caller widget is the selection leader, and should fire selection change event all over the world !
			fireSelectionChanged(widget);
			List<Object> objectsToRemoveFromSelection = new Vector<Object>();
			List<Object> objectsToAddToSelection = new Vector<Object>();
			if (oldSelection != null) {
				objectsToRemoveFromSelection.addAll(oldSelection);
			}
			for (Object o : newSelection) {
				if (oldSelection != null && oldSelection.contains(o)) {
					objectsToRemoveFromSelection.remove(o);
				} else {
					objectsToAddToSelection.add(o);
				}
			}

			for (FIBView<?, ?, ?> v : getAllViews()) {
				if (v.isSelectableComponent() && v.getSelectableComponent() != getSelectionLeader()
						&& v.getSelectableComponent().synchronizedWithSelection()) {
					for (Object o : objectsToAddToSelection) {
						if (v.getSelectableComponent().mayRepresent(o)) {
							v.getSelectableComponent().objectAddedToSelection(o);
						}
					}
					for (Object o : objectsToRemoveFromSelection) {
						// Don't do this for FIBSelectable which are not SelectionLeader !!!
						// Otherwise, if this selectable'selection is the cause of displaying of selection leader
						// the selection leader might disapppear
						if (v.getSelectableComponent().mayRepresent(o) && v.getSelectableComponent() == getSelectionLeader()) {
							v.getSelectableComponent().objectRemovedFromSelection(o);
						}
					}
				}
			}
		}
	}

	public void objectAddedToSelection(Object o) {

		// LOGGER.info("************** objectAddedToSelection() dans FIBController with " + o);

		LOGGER.fine("FIBController: objectAddedToSelection(): " + o);

		for (FIBView<?, ?, ?> v : getViews()) {
			if (v.isSelectableComponent() && v.getSelectableComponent().synchronizedWithSelection()) {
				if (v.getSelectableComponent().mayRepresent(o)) {
					v.getSelectableComponent().objectAddedToSelection(o);
					if (getSelectionLeader() == null) {
						setSelectionLeader(v.getSelectableComponent());
					}
					if (getLastFocusedSelectable() == null) {
						setLastFocusedSelectable(getSelectionLeader());
					}
				}
			}
		}

	}

	public void objectRemovedFromSelection(Object o) {

		// LOGGER.info("************** objectRemovedFromSelection() dans FIBController with " + o);

		LOGGER.fine("FIBController: objectRemovedFromSelection(): " + o);
		for (FIBView<?, ?, ?> v : getViews()) {
			if (v.isSelectableComponent() && v.getSelectableComponent().synchronizedWithSelection()) {
				if (v.getSelectableComponent().mayRepresent(o)) {
					v.getSelectableComponent().objectRemovedFromSelection(o);
				}
			}
		}
	}

	public void selectionCleared() {
		LOGGER.fine("FIBController: selectionCleared()");
		for (FIBView<?, ?, ?> v : getViews()) {
			if (v.isSelectableComponent() && v.getSelectableComponent().synchronizedWithSelection()) {
				v.getSelectableComponent().selectionResetted();
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

	public void fireMouseClicked(FIBView<?, ?, ?> view, int clickCount) {
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

	protected class DefaultFIBViewFactory implements FIBViewFactory {
		@Override
		public FIBView makeContainer(FIBContainer fibContainer) {
			if (fibContainer instanceof FIBTab) {
				return new FIBTabView((FIBTab) fibContainer, FIBController.this);
			} else if (fibContainer instanceof FIBPanel) {
				return new FIBPanelView((FIBPanel) fibContainer, FIBController.this);
			} else if (fibContainer instanceof FIBTabPanel) {
				return new FIBTabPanelView((FIBTabPanel) fibContainer, FIBController.this);
			} else if (fibContainer instanceof FIBSplitPanel) {
				return new FIBSplitPanelView((FIBSplitPanel) fibContainer, FIBController.this);
			}
			return null;
		}

		@Override
		public FIBWidgetView makeWidget(FIBWidget fibWidget) {
			if (fibWidget instanceof FIBTextField) {
				return new FIBTextFieldWidget((FIBTextField) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBEditor) {
				return new FIBEditorWidget((FIBEditor) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBTextPane) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.warning("Can't handle TextPane yet");
				}
				return new FIBEditorPaneWidget((FIBEditorPane) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBEditorPane) {
				return new FIBEditorPaneWidget((FIBEditorPane) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBTextArea) {
				return new FIBTextAreaWidget((FIBTextArea) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBHtmlEditor) {
				return new FIBHtmlEditorWidget((FIBHtmlEditor) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBLabel) {
				return new FIBLabelWidget((FIBLabel) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBImage) {
				return new FIBImageWidget((FIBImage) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBCheckBox) {
				return new FIBCheckBoxWidget((FIBCheckBox) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBTable) {
				return new FIBTableWidget((FIBTable) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBBrowser) {
				return new FIBBrowserWidget((FIBBrowser) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBDropDown) {
				return new FIBDropDownWidget((FIBDropDown) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBList) {
				return new FIBListWidget((FIBList) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBNumber) {
				FIBNumber w = (FIBNumber) fibWidget;
				switch (w.getNumberType()) {
				case ByteType:
					return new FIBNumberWidget.FIBByteWidget(w, FIBController.this);
				case ShortType:
					return new FIBNumberWidget.FIBShortWidget(w, FIBController.this);
				case IntegerType:
					return new FIBNumberWidget.FIBIntegerWidget(w, FIBController.this);
				case LongType:
					return new FIBNumberWidget.FIBLongWidget(w, FIBController.this);
				case FloatType:
					return new FIBNumberWidget.FIBFloatWidget(w, FIBController.this);
				case DoubleType:
					return new FIBNumberWidget.FIBDoubleWidget(w, FIBController.this);
				default:
					break;
				}
			}
			if (fibWidget instanceof FIBColor) {
				return new FIBColorWidget((FIBColor) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBFont) {
				return new FIBFontWidget((FIBFont) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBFile) {
				return new FIBFileWidget((FIBFile) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBButton) {
				return new FIBButtonWidget((FIBButton) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBRadioButtonList) {
				return new FIBRadioButtonListWidget((FIBRadioButtonList) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBCheckboxList) {
				return new FIBCheckboxListWidget((FIBCheckboxList) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBCustom) {
				return new FIBCustomWidget((FIBCustom) fibWidget, FIBController.this);
			}
			if (fibWidget instanceof FIBReferencedComponent) {
				return new FIBReferencedComponentWidget((FIBReferencedComponent) fibWidget, FIBController.this, this);
			}
			return null;
		}

	}

	private static class EditorLauncher extends MouseAdapter {
		private final FIBComponent component;
		private final FIBController controller;

		public EditorLauncher(FIBController controller, FIBComponent component) {
			LOGGER.fine("make EditorLauncher for component: " + component.getResource());
			this.component = component;
			this.controller = controller;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if (e.isAltDown()) {
				controller.openFIBEditor(component, e);
			}
		}
	}

	protected void openFIBEditor(final FIBComponent component, MouseEvent event) {
		if (component.getResource() == null) {
			try {
				File fibFile = File.createTempFile("FIBComponent", ".fib");
				FileResourceImpl fibLocation = null;
				try {
					fibLocation = new FileResourceImpl(fibFile.getCanonicalPath(), fibFile.toURI().toURL(), fibFile);
				} catch (LocatorNotFoundException e) {
					LOGGER.severe("No Locator found for managing FileResources!! ");
					e.printStackTrace();
				}
				component.setResource(fibLocation);
				FIBLibrary.save(component, fibFile);
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.warning("Cannot create FIB temp file definition for component, aborting FIB edition");
				return;
			}
		}
		Class embeddedEditor = null;
		Constructor c = null;
		try {
			embeddedEditor = Class.forName("org.openflexo.fib.editor.FIBEmbeddedEditor");
			c = embeddedEditor.getConstructors()[0];
			/*File fibFile = ((FileResourceImpl) component.getResource()).getFile();
			if (!fibFile.exists()) {
				logger.warning("Cannot find FIB file definition for component, aborting FIB edition");
				return;
			}*/
			Object[] args = new Object[2];
			args[0] = component.getResource();
			args[1] = getDataObject();
			LOGGER.info("Opening FIB editor for " + component.getResource());
			c.newInstance(args);
		} catch (ClassNotFoundException e) {
			LOGGER.warning("Cannot open FIB Editor, please add org.openflexo.fib.editor.FIBEmbeddedEditor in the class path");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			LOGGER.warning("Cannot instanciate " + embeddedEditor + " with constructor " + c + " because of unexpected exception ");
			e.getTargetException().printStackTrace();
		}
	}

	protected boolean allowsFIBEdition() {
		return true;
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
}
