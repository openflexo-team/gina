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

package org.openflexo.gina.swing.view;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.controller.FIBSelectable;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBMouseEvent;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.bindings.RuntimeContext;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.model.operator.FIBIteration;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBColor;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBDate;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.model.widget.FIBEditor;
import org.openflexo.gina.model.widget.FIBEditorPane;
import org.openflexo.gina.model.widget.FIBFile;
import org.openflexo.gina.model.widget.FIBFont;
import org.openflexo.gina.model.widget.FIBHtmlEditor;
import org.openflexo.gina.model.widget.FIBImage;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBList;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBRadioButtonList;
import org.openflexo.gina.model.widget.FIBReferencedComponent;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.swing.view.container.JFIBIterationView;
import org.openflexo.gina.swing.view.container.JFIBPanelView;
import org.openflexo.gina.swing.view.container.JFIBSplitPanelView;
import org.openflexo.gina.swing.view.container.JFIBTabPanelView;
import org.openflexo.gina.swing.view.container.JFIBTabView;
import org.openflexo.gina.swing.view.widget.JFDFIBTableWidget;
import org.openflexo.gina.swing.view.widget.JFIBBrowserWidget;
import org.openflexo.gina.swing.view.widget.JFIBButtonWidget;
import org.openflexo.gina.swing.view.widget.JFIBCheckBoxWidget;
import org.openflexo.gina.swing.view.widget.JFIBCheckboxListWidget;
import org.openflexo.gina.swing.view.widget.JFIBColorWidget;
import org.openflexo.gina.swing.view.widget.JFIBCustomWidget;
import org.openflexo.gina.swing.view.widget.JFIBDateWidget;
import org.openflexo.gina.swing.view.widget.JFIBDropDownWidget;
import org.openflexo.gina.swing.view.widget.JFIBEditorPaneWidget;
import org.openflexo.gina.swing.view.widget.JFIBEditorWidget;
import org.openflexo.gina.swing.view.widget.JFIBFileWidget;
import org.openflexo.gina.swing.view.widget.JFIBFontWidget;
import org.openflexo.gina.swing.view.widget.JFIBHtmlEditorWidget;
import org.openflexo.gina.swing.view.widget.JFIBImageWidget;
import org.openflexo.gina.swing.view.widget.JFIBLabelWidget;
import org.openflexo.gina.swing.view.widget.JFIBListWidget;
import org.openflexo.gina.swing.view.widget.JFIBNumberWidget;
import org.openflexo.gina.swing.view.widget.JFIBRadioButtonListWidget;
import org.openflexo.gina.swing.view.widget.JFIBReferencedComponentWidget;
import org.openflexo.gina.swing.view.widget.JFIBTableWidget;
import org.openflexo.gina.swing.view.widget.JFIBTextAreaWidget;
import org.openflexo.gina.swing.view.widget.JFIBTextFieldWidget;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.FIBWidgetView;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.gina.view.container.FIBPanelView;
import org.openflexo.gina.view.container.FIBSplitPanelView;
import org.openflexo.gina.view.container.FIBTabPanelView;
import org.openflexo.gina.view.container.FIBTabView;
import org.openflexo.gina.view.impl.GinaViewFactoryImpl;
import org.openflexo.gina.view.operator.FIBIterationView;
import org.openflexo.gina.view.widget.FIBLabelWidget;
import org.openflexo.gina.view.widget.FIBTextFieldWidget;
import org.openflexo.gina.view.widget.impl.FIBBrowserWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBButtonWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBCheckBoxWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBCheckboxListWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBColorWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBCustomWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBDateWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBDropDownWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBEditorPaneWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBEditorWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBFileWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBFontWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBHtmlEditorWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBImageWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBListWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBNumberWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBRadioButtonListWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBReferencedComponentWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBTableWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBTextAreaWidgetImpl;
import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.toolbox.StringUtils;

/**
 * A {@link GinaViewFactory} implementation dedicated for Swing<br>
 * Developers note: We want this class to be stateless: please do not reference any data inside this class
 * 
 * @author sylvain
 * 
 */
public class SwingViewFactory extends GinaViewFactoryImpl<JComponent> {

	private static final Logger LOGGER = Logger.getLogger(SwingViewFactory.class.getPackage().getName());

	public static final SwingViewFactory INSTANCE = new SwingViewFactory();

	// Prevent external instanciation
	protected SwingViewFactory() {
	}

	@Override
	public boolean allowsFIBEdition() {
		return true;
	}

	@Override
	public final <F extends FIBContainer> FIBContainerView<F, ? extends JComponent, ? extends JComponent> makeContainer(F fibContainer,
			FIBController controller, RuntimeContext context, boolean updateNow) {
		// System.out.println("Make container view for " + fibContainer);
		FIBContainerView<F, ? extends JComponent, ? extends JComponent> returned = super.makeContainer(fibContainer, controller, context,
				updateNow);
		if (returned != null && fibContainer.isRootComponent()) {
			if (returned instanceof FIBContainerView && allowsFIBEdition()) {
				EditorLauncher editorLauncher = new EditorLauncher(controller, fibContainer);
				recursivelyAddEditorLauncher(editorLauncher, (FIBContainerView) returned);
			}
			return returned;
		}
		/*if (returned != null) {
			// returned.updateGraphicalProperties();
			returned.update();
		}*/
		// ((JFIBView) returned).getJComponent().revalidate();
		return returned;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends FIBWidget> FIBWidgetView<F, ? extends JComponent, ?> makeWidget(final F fibWidget, FIBController controller,
			RuntimeContext context, boolean updateNow) {
		final FIBWidgetView<F, ? extends JComponent, ?> returned = super.makeWidget(fibWidget, controller, context, updateNow);

		if (returned == null) {
			System.out.println("For fibWidget " + fibWidget + " returned=" + returned);
		}

		else if (returned.getTechnologyComponent() == null) {
			System.out.println(
					"For fibWidget " + fibWidget + " returned=" + returned + " technologyComponent=" + returned.getTechnologyComponent());
		}

		else {

			// We retrieve the JComponent on which controls apply
			JComponent dynamicJComponent = ((JFIBView) returned).getRenderingAdapter()
					.getDynamicJComponent(returned.getTechnologyComponent());

			dynamicJComponent.addMouseListener(new SwingMouseAdapter(returned, fibWidget));

			dynamicJComponent.addKeyListener(new KeyAdapter() {
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
				dynamicJComponent.setToolTipText(fibWidget.getTooltipText());
			}
		}
		// returned.updateGraphicalProperties();
		// returned.update();
		return returned;
	}

	@Override
	public FIBTabView<? extends JComponent, ? extends JComponent> makeTabView(FIBTab container, FIBController controller,
			RuntimeContext context) {
		return new JFIBTabView(container, controller);
	}

	@Override
	public FIBPanelView<? extends JComponent, ? extends JComponent> makePanelView(FIBPanel container, FIBController controller,
			RuntimeContext context) {
		return new JFIBPanelView(container, controller);
	}

	@Override
	public FIBTabPanelView<? extends JComponent, ? extends JComponent> makeTabPanelView(FIBTabPanel container, FIBController controller,
			RuntimeContext context) {
		return new JFIBTabPanelView(container, controller);
	}

	@Override
	public FIBSplitPanelView<? extends JComponent, ? extends JComponent> makeSplitPanelView(FIBSplitPanel container,
			FIBController controller, RuntimeContext context) {
		return new JFIBSplitPanelView(container, controller);
	}

	@Override
	public FIBIterationView<? extends JComponent, ? extends JComponent> makeIterationView(FIBIteration iteration, FIBController controller,
			RuntimeContext context) {
		return new JFIBIterationView(iteration, controller, context);
	}

	@Override
	public FIBLabelWidget<? extends JComponent> makeLabel(FIBLabel widget, FIBController controller, RuntimeContext context) {
		return new JFIBLabelWidget(widget, controller);
	}

	@Override
	public FIBTextFieldWidget<? extends JComponent> makeTextField(FIBTextField widget, FIBController controller, RuntimeContext context) {
		return new JFIBTextFieldWidget(widget, controller);
	}

	@Override
	public FIBTextAreaWidgetImpl<? extends JComponent> makeTextArea(FIBTextArea widget, FIBController controller, RuntimeContext context) {
		return new JFIBTextAreaWidget(widget, controller);
	}

	@Override
	public FIBEditorPaneWidgetImpl<? extends JComponent> makeEditorPane(FIBEditorPane widget, FIBController controller,
			RuntimeContext context) {
		return new JFIBEditorPaneWidget(widget, controller);
	}

	@Override
	public FIBImageWidgetImpl<? extends JComponent> makeImage(FIBImage widget, FIBController controller, RuntimeContext context) {
		return new JFIBImageWidget(widget, controller);
	}

	@Override
	public FIBNumberWidgetImpl<? extends JComponent, ?> makeNumber(FIBNumber widget, FIBController controller, RuntimeContext context) {
		return new JFIBNumberWidget<>(widget, controller);
	}

	@Override
	public FIBCheckBoxWidgetImpl<? extends JComponent> makeCheckbox(FIBCheckBox widget, FIBController controller, RuntimeContext context) {
		return new JFIBCheckBoxWidget(widget, controller);
	}

	@Override
	public FIBCheckboxListWidgetImpl<? extends JComponent, ?> makeCheckboxList(FIBCheckboxList widget, FIBController controller,
			RuntimeContext context) {
		return new JFIBCheckboxListWidget<>(widget, controller);
	}

	@Override
	public FIBRadioButtonListWidgetImpl<? extends JComponent, ?> makeRadioButtonList(FIBRadioButtonList widget, FIBController controller,
			RuntimeContext context) {
		return new JFIBRadioButtonListWidget<>(widget, controller);
	}

	@Override
	public FIBDropDownWidgetImpl<? extends JComponent, ?> makeDropDown(FIBDropDown widget, FIBController controller,
			RuntimeContext context) {
		return new JFIBDropDownWidget<>(widget, controller);
	}

	@Override
	public FIBListWidgetImpl<? extends JComponent, ?> makeList(FIBList widget, FIBController controller, RuntimeContext context) {
		return new JFIBListWidget<>(widget, controller);
	}

	@Override
	public FIBEditorWidgetImpl<? extends JComponent> makeEditor(FIBEditor widget, FIBController controller, RuntimeContext context) {
		return new JFIBEditorWidget(widget, controller);
	}

	@Override
	public FIBHtmlEditorWidgetImpl<? extends JComponent> makeHtmlEditor(FIBHtmlEditor widget, FIBController controller,
			RuntimeContext context) {
		return new JFIBHtmlEditorWidget(widget, controller);
	}

	@Override
	public FIBTableWidgetImpl<? extends JComponent, ?> makeTable(FIBTable widget, FIBController controller, RuntimeContext context) {
		if (widget.getLookAndFeel() != null) {
			// System.out.println(">>>>>>>>>>>>>> Nouvelle table avec " + widget.getLookAndFeel());
			switch (widget.getLookAndFeel()) {
				case Classic:
					return new JFIBTableWidget<>(widget, controller);
				case FlatDesign:
					return new JFDFIBTableWidget<>(widget, controller);
			}
		}
		return new JFIBTableWidget<>(widget, controller);
	}

	@Override
	public FIBBrowserWidgetImpl<? extends JComponent, ?> makeBrowser(FIBBrowser widget, FIBController controller, RuntimeContext context) {
		return new JFIBBrowserWidget<>(widget, controller);
	}

	@Override
	public FIBColorWidgetImpl<? extends JComponent> makeColor(FIBColor widget, FIBController controller, RuntimeContext context) {
		return new JFIBColorWidget(widget, controller);
	}

	@Override
	public FIBFontWidgetImpl<? extends JComponent> makeFont(FIBFont widget, FIBController controller, RuntimeContext context) {
		return new JFIBFontWidget(widget, controller);
	}

	@Override
	public FIBDateWidgetImpl<? extends JComponent> makeDate(FIBDate widget, FIBController controller, RuntimeContext context) {
		return new JFIBDateWidget(widget, controller);
	}

	@Override
	public FIBFileWidgetImpl<? extends JComponent> makeFile(FIBFile widget, FIBController controller, RuntimeContext context) {
		return new JFIBFileWidget(widget, controller);
	}

	@Override
	public FIBButtonWidgetImpl<? extends JComponent> makeButton(FIBButton widget, FIBController controller, RuntimeContext context) {
		return new JFIBButtonWidget(widget, controller);
	}

	@Override
	public FIBCustomWidgetImpl<? extends JComponent, ?, ?> makeCustomWidget(FIBCustom widget, FIBController controller,
			RuntimeContext context) {
		return new JFIBCustomWidget<>(widget, controller);
	}

	@Override
	public FIBReferencedComponentWidgetImpl<? extends JComponent> makeReferencedComponentWidget(FIBReferencedComponent widget,
			FIBController controller, RuntimeContext context) {
		return new JFIBReferencedComponentWidget(widget, controller);
	}

	@Override
	public void show(FIBController controller) {
		Window w = retrieveWindow(controller);
		if (w != null) {
			w.setVisible(true);
		}
	}

	@Override
	public void hide(FIBController controller) {
		Window w = retrieveWindow(controller);
		if (w != null) {
			w.setVisible(false);
		}
	}

	@Override
	public void disposeWindow(FIBController controller) {
		Window w = retrieveWindow(controller);
		if (w != null) {
			w.dispose();
		}
	}

	private static Window retrieveWindow(FIBController controller) {
		Component c = SwingUtilities.getRoot(((JFIBView<?, ?>) controller.getRootView()).getJComponent());
		if (c instanceof Window) {
			return (Window) c;
		}
		return null;
	}

	protected void recursivelyAddEditorLauncher(EditorLauncher editorLauncher,
			FIBContainerView<? extends FIBContainer, JComponent, ?> container) {

		if (((JFIBView<?, ?>) container).getJComponent() == null) {
			return;
		}

		((JFIBView<?, ?>) container).getJComponent().addMouseListener(editorLauncher);

		for (FIBComponent c : container.getComponent().getSubComponents()) {
			FIBView<?, ?> subView = container.getController().viewForComponent(c);
			if (subView instanceof FIBContainerView) {
				recursivelyAddEditorLauncher(editorLauncher, (FIBContainerView) subView);
			}
		}
	}

	public static class SwingFIBMouseEvent implements FIBMouseEvent {

		private final MouseEvent e;

		public SwingFIBMouseEvent(MouseEvent e) {
			this.e = e;
		}

		@Override
		public int getY() {
			return e.getY();
		}

		@Override
		public int getX() {
			return e.getX();
		}

		@Override
		public Object getSource() {
			return e.getSource();
		}

		@Override
		public Point getPoint() {
			return e.getPoint();
		}

		@Override
		public Point getLocationOnScreen() {
			return e.getLocationOnScreen();
		}

		@Override
		public int getClickCount() {
			return e.getClickCount();
		}

		@Override
		public int getButton() {
			return e.getButton();
		}

	}

	/**
	 * A MouseAdapter which handle click actions<br>
	 * Also perform mouse binding execution
	 * 
	 * @author sylvain
	 * 
	 */
	protected class SwingMouseAdapter extends MouseAdapter {

		private final FIBWidgetView<?, ? extends JComponent, ?> widgetView;

		public SwingMouseAdapter(FIBWidgetView<?, ? extends JComponent, ?> widgetView, FIBWidget fibWidget) {
			this.widgetView = widgetView;
		}

		private FIBMouseEvent makeMouseEvent(MouseEvent e) {
			return new SwingFIBMouseEvent(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);

			// If we press in a component which is selectable and declared to be synchronized with selection
			// We should handle the case where another component (which is not focusable) represent the current selection
			// If this is not the same component, trigger the selection modification
			// See GINA-32
			if (widgetView instanceof FIBSelectable && ((FIBSelectable) widgetView).synchronizedWithSelection()) {
				if (widgetView.getController().getSelectionLeader() != widgetView) {
					List oldSelection = widgetView.getController().getSelectionLeader().getSelection();
					widgetView.getController().setSelectionLeader((FIBSelectable) widgetView);
					widgetView.getController().updateSelection((FIBSelectable) widgetView, oldSelection,
							widgetView.getController().getSelectionLeader().getSelection());
				}
			}

		}

		@Override
		public void mouseClicked(MouseEvent e) {

			widgetView.getController().fireMouseClicked(widgetView, e.getClickCount());
			if (e.getClickCount() == 1) {
				if (widgetView.getWidget().hasRightClickAction() && (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3)) {
					// Detected right-click associated with action
					widgetView.applyRightClickAction(makeMouseEvent(e));
				}
				else if (widgetView.getWidget().hasClickAction()) {
					// Detected click associated with action
					widgetView.applySingleClickAction(makeMouseEvent(e));
				}
			}
			else if (e.getClickCount() == 2) {
				if (widgetView.getWidget().hasDoubleClickAction()) {
					// Detected double-click associated with action
					widgetView.applyDoubleClickAction(makeMouseEvent(e));
				}
			}
		}
	}

	private class EditorLauncher extends MouseAdapter {
		private final FIBComponent component;
		private final FIBController controller;

		public EditorLauncher(FIBController controller, FIBComponent component) {
			// System.out.println("make EditorLauncher for component: " + component.getResource());
			this.component = component;
			this.controller = controller;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if (e.isAltDown()) {
				openFIBEditor(component, e);
			}
		}

		protected void openFIBEditor(final FIBComponent component, MouseEvent event) {

			if (getInteractiveFIBEditor() == null) {
				return;
			}

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
					if (component.getFIBLibrary() != null) {
						component.getFIBLibrary().save(component, fibLocation);
					}
				} catch (IOException e) {
					e.printStackTrace();
					LOGGER.warning("Cannot create FIB temp file definition for component, aborting FIB edition");
					return;
				}
			}

			getInteractiveFIBEditor().openResource(component.getResource(), component.getComponent(), controller.getDataObject());

		}

	}

}
