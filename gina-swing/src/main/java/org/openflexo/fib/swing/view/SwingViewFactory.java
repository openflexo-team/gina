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

package org.openflexo.fib.swing.view;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
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
import org.openflexo.fib.model.FIBFile;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBImage;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.model.FIBMouseEvent;
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
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.swing.view.container.JFIBPanelView;
import org.openflexo.fib.swing.view.container.JFIBSplitPanelView;
import org.openflexo.fib.swing.view.container.JFIBTabPanelView;
import org.openflexo.fib.swing.view.container.JFIBTabView;
import org.openflexo.fib.swing.view.widget.JFIBBrowserWidget;
import org.openflexo.fib.swing.view.widget.JFIBButtonWidget;
import org.openflexo.fib.swing.view.widget.JFIBCheckBoxWidget;
import org.openflexo.fib.swing.view.widget.JFIBCheckboxListWidget;
import org.openflexo.fib.swing.view.widget.JFIBColorWidget;
import org.openflexo.fib.swing.view.widget.JFIBCustomWidget;
import org.openflexo.fib.swing.view.widget.JFIBDropDownWidget;
import org.openflexo.fib.swing.view.widget.JFIBEditorWidget;
import org.openflexo.fib.swing.view.widget.JFIBFileWidget;
import org.openflexo.fib.swing.view.widget.JFIBFontWidget;
import org.openflexo.fib.swing.view.widget.JFIBHtmlEditorWidget;
import org.openflexo.fib.swing.view.widget.JFIBImageWidget;
import org.openflexo.fib.swing.view.widget.JFIBLabelWidget;
import org.openflexo.fib.swing.view.widget.JFIBListWidget;
import org.openflexo.fib.swing.view.widget.JFIBNumberWidget;
import org.openflexo.fib.swing.view.widget.JFIBRadioButtonListWidget;
import org.openflexo.fib.swing.view.widget.JFIBReferencedComponentWidget;
import org.openflexo.fib.swing.view.widget.JFIBTableWidget;
import org.openflexo.fib.swing.view.widget.JFIBTextAreaWidget;
import org.openflexo.fib.swing.view.widget.JFIBTextFieldWidget;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.GinaViewFactory;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.fib.view.container.FIBSplitPanelView;
import org.openflexo.fib.view.container.FIBTabPanelView;
import org.openflexo.fib.view.container.FIBTabView;
import org.openflexo.fib.view.impl.GinaViewFactoryImpl;
import org.openflexo.fib.view.widget.FIBLabelWidget;
import org.openflexo.fib.view.widget.FIBTextFieldWidget;
import org.openflexo.fib.view.widget.impl.FIBBrowserWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBButtonWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBCheckBoxWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBCheckboxListWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBColorWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBCustomWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBDropDownWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBEditorWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBFileWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBFontWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBHtmlEditorWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBImageWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBListWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBNumberWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBRadioButtonListWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBReferencedComponentWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBTableWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBTextAreaWidgetImpl;
import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.toolbox.StringUtils;

/**
 * A {@link GinaViewFactory} implementation dedicated for Swing
 * 
 * @author sylvain
 * 
 */
public class SwingViewFactory extends GinaViewFactoryImpl<JComponent> {

	private static final Logger LOGGER = Logger.getLogger(SwingViewFactory.class.getPackage().getName());

	public static final SwingViewFactory INSTANCE = new SwingViewFactory();

	@Override
	public boolean allowsFIBEdition() {
		return true;
	}

	@Override
	public final <F extends FIBContainer> FIBContainerView<F, ? extends JComponent, ? extends JComponent> makeContainer(F fibContainer,
			FIBController controller) {
		FIBContainerView<F, ? extends JComponent, ? extends JComponent> returned = super.makeContainer(fibContainer, controller);
		if (returned != null && fibContainer.isRootComponent()) {
			if (returned instanceof FIBContainerView && allowsFIBEdition()) {
				EditorLauncher editorLauncher = new EditorLauncher(controller, fibContainer);
				recursivelyAddEditorLauncher(editorLauncher, (FIBContainerView) returned);
			}
			return returned;
		}
		if (returned != null) {
			returned.updateGraphicalProperties();
		}
		return returned;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends FIBWidget> FIBWidgetView<F, ? extends JComponent, ?> makeWidget(final F fibWidget, FIBController controller) {
		final FIBWidgetView<F, ? extends JComponent, ?> returned = super.makeWidget(fibWidget, controller);

		returned.getTechnologyComponent().addMouseListener(new FIBMouseAdapter(returned, fibWidget));

		returned.getTechnologyComponent().addKeyListener(new KeyAdapter() {
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
			returned.getTechnologyComponent().setToolTipText(fibWidget.getTooltipText());
		}
		returned.updateGraphicalProperties();
		return returned;
	}

	@Override
	public FIBTabView<? extends JComponent, ? extends JComponent> makeTabView(FIBTab container, FIBController controller) {
		return new JFIBTabView(container, controller);
	}

	@Override
	public FIBPanelView<? extends JComponent, ? extends JComponent> makePanelView(FIBPanel container, FIBController controller) {
		return new JFIBPanelView(container, controller);
	}

	@Override
	public FIBTabPanelView<? extends JComponent, ? extends JComponent> makeTabPanelView(FIBTabPanel container, FIBController controller) {
		return new JFIBTabPanelView(container, controller);
	}

	@Override
	public FIBSplitPanelView<? extends JComponent, ? extends JComponent> makeSplitPanelView(FIBSplitPanel container,
			FIBController controller) {
		return new JFIBSplitPanelView(container, controller);
	}

	@Override
	public FIBLabelWidget<? extends JComponent> makeLabel(FIBLabel widget, FIBController controller) {
		return new JFIBLabelWidget(widget, controller);
	}

	@Override
	public FIBTextFieldWidget<? extends JComponent> makeTextField(FIBTextField widget, FIBController controller) {
		return new JFIBTextFieldWidget(widget, controller);
	}

	@Override
	public FIBTextAreaWidgetImpl<? extends JComponent> makeTextArea(FIBTextArea widget, FIBController controller) {
		return new JFIBTextAreaWidget(widget, controller);
	}

	@Override
	public FIBImageWidgetImpl<? extends JComponent> makeImage(FIBImage widget, FIBController controller) {
		return new JFIBImageWidget(widget, controller);
	}

	@Override
	public FIBNumberWidgetImpl<? extends JComponent, ?> makeNumber(FIBNumber widget, FIBController controller) {
		return new JFIBNumberWidget(widget, controller);
	}

	@Override
	public FIBCheckBoxWidgetImpl<? extends JComponent> makeCheckbox(FIBCheckBox widget, FIBController controller) {
		return new JFIBCheckBoxWidget(widget, controller);
	}

	@Override
	public FIBCheckboxListWidgetImpl<? extends JComponent, ?> makeCheckboxList(FIBCheckboxList widget, FIBController controller) {
		return new JFIBCheckboxListWidget(widget, controller);
	}

	@Override
	public FIBRadioButtonListWidgetImpl<? extends JComponent, ?> makeRadioButtonList(FIBRadioButtonList widget, FIBController controller) {
		return new JFIBRadioButtonListWidget(widget, controller);
	}

	@Override
	public FIBDropDownWidgetImpl<? extends JComponent, ?> makeDropDown(FIBDropDown widget, FIBController controller) {
		return new JFIBDropDownWidget(widget, controller);
	}

	@Override
	public FIBListWidgetImpl<? extends JComponent, ?> makeList(FIBList widget, FIBController controller) {
		return new JFIBListWidget(widget, controller);
	}

	@Override
	public FIBEditorWidgetImpl<? extends JComponent> makeEditor(FIBEditor widget, FIBController controller) {
		return new JFIBEditorWidget(widget, controller);
	}

	@Override
	public FIBHtmlEditorWidgetImpl<? extends JComponent> makeHtmlEditor(FIBHtmlEditor widget, FIBController controller) {
		return new JFIBHtmlEditorWidget(widget, controller);
	}

	@Override
	public FIBTableWidgetImpl<? extends JComponent, ?> makeTable(FIBTable widget, FIBController controller) {
		return new JFIBTableWidget(widget, controller);
	}

	@Override
	public FIBBrowserWidgetImpl<? extends JComponent, ?> makeBrowser(FIBBrowser widget, FIBController controller) {
		return new JFIBBrowserWidget(widget, controller);
	}

	@Override
	public FIBColorWidgetImpl<? extends JComponent> makeColor(FIBColor widget, FIBController controller) {
		return new JFIBColorWidget(widget, controller);
	}

	@Override
	public FIBFontWidgetImpl<? extends JComponent> makeFont(FIBFont widget, FIBController controller) {
		return new JFIBFontWidget(widget, controller);
	}

	@Override
	public FIBFileWidgetImpl<? extends JComponent> makeFile(FIBFile widget, FIBController controller) {
		return new JFIBFileWidget(widget, controller);
	}

	@Override
	public FIBButtonWidgetImpl<? extends JComponent> makeButton(FIBButton widget, FIBController controller) {
		return new JFIBButtonWidget(widget, controller);
	}

	@Override
	public FIBCustomWidgetImpl<? extends JComponent, ?> makeCustomWidget(FIBCustom widget, FIBController controller) {
		return new JFIBCustomWidget(widget, controller);
	}

	@Override
	public FIBReferencedComponentWidgetImpl<? extends JComponent> makeReferencedComponentWidget(FIBReferencedComponent widget,
			FIBController controller) {
		return new JFIBReferencedComponentWidget(widget, controller);
	}

	protected void recursivelyAddEditorLauncher(EditorLauncher editorLauncher,
			FIBContainerView<? extends FIBContainer, JComponent, ?> container) {
		container.getJComponent().addMouseListener(editorLauncher);
		for (FIBComponent c : container.getComponent().getSubComponents()) {
			FIBView<?, ?> subView = container.getController().viewForComponent(c);
			if (subView instanceof FIBContainerView) {
				recursivelyAddEditorLauncher(editorLauncher, (FIBContainerView) subView);
			}
		}
	}

	/**
	 * A MouseAdapter which handle click actions<br>
	 * Also perform mouse binding execution
	 * 
	 * @author sylvain
	 * 
	 */
	protected class FIBMouseAdapter extends MouseAdapter {

		private final FIBWidgetView<?, ? extends JComponent, ?> widgetView;

		public FIBMouseAdapter(FIBWidgetView<?, ? extends JComponent, ?> widgetView, FIBWidget fibWidget) {
			this.widgetView = widgetView;
		}

		private FIBMouseEvent makeMouseEvent(final MouseEvent e) {
			return new FIBMouseEvent() {

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
			};
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
				openFIBEditor(component, e);
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
				args[1] = controller.getDataObject();
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

	}

}
