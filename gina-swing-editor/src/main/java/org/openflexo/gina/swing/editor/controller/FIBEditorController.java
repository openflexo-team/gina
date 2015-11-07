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

package org.openflexo.gina.swing.editor.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBColor;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.model.widget.FIBEditor;
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
import org.openflexo.gina.swing.editor.FIBAbstractEditor;
import org.openflexo.gina.swing.editor.FIBGenericEditor;
import org.openflexo.gina.swing.editor.view.FIBEditableView;
import org.openflexo.gina.swing.editor.view.FIBEditableViewDelegate;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.editor.view.container.FIBEditablePanelView;
import org.openflexo.gina.swing.editor.view.container.FIBEditableSplitPanelView;
import org.openflexo.gina.swing.editor.view.container.FIBEditableTabPanelView;
import org.openflexo.gina.swing.editor.view.container.FIBEditableTabView;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableBrowserWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableButtonWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableCheckboxListWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableCheckboxWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableColorWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableCustomWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableDropDownWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableEditorPaneWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableEditorWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableFileWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableFontWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableHtmlEditorWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableImageWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableLabelWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableListWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableNumberWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableRadioButtonListWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableReferencedComponentWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableTableWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableTextAreaWidget;
import org.openflexo.gina.swing.editor.view.widget.FIBEditableTextFieldWidget;
import org.openflexo.gina.swing.editor.widget.FIBEditorBrowser;
import org.openflexo.gina.swing.utils.FocusedObjectChange;
import org.openflexo.gina.swing.utils.SelectedObjectChange;
import org.openflexo.gina.swing.utils.controller.FIBViewFactory;
import org.openflexo.gina.swing.utils.model.FIBEditorPane;
import org.openflexo.gina.swing.utils.model.FIBTextPane;
import org.openflexo.gina.swing.utils.swing.view.FIBView;
import org.openflexo.gina.swing.utils.swing.view.FIBWidgetView;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.NoInsetsBorder;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public class FIBEditorController /*extends FIBController*/extends Observable implements HasPropertyChangeSupport {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditorController.class.getPackage().getName());

	private final FIBController controller;

	private final FIBModelFactory factory;

	private final JPanel editorPanel;
	private final FIBView<?, ?> fibPanel;
	private final FIBGenericEditor editor;

	private FIBComponent fibComponent = null;
	private FIBComponent selectedObject = null;
	private FIBComponent focusedObject = null;

	private final ContextualMenu contextualMenu;

	private static final Border FOCUSED_BORDER = new NoInsetsBorder(BorderFactory.createLineBorder(Color.RED));
	private static final Border SELECTED_BORDER = new NoInsetsBorder(BorderFactory.createLineBorder(Color.BLUE));

	private final Map<FIBComponent, FIBEditableViewDelegate<?, ?>> viewDelegates;

	private final PropertyChangeSupport pcSupport;

	private class FibWrappingPanel extends JPanel {
		public FibWrappingPanel(JComponent wrappedFib) {
			super(new BorderLayout());
			add(wrappedFib);
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			paintFocusedAndSelected(g);
		}

		private void paintFocusedAndSelected(Graphics g) {
			FIBEditableViewDelegate<?, ?> focused = viewDelegates.get(getFocusedObject());
			FIBEditableViewDelegate<?, ?> selected = viewDelegates.get(getSelectedObject());
			if (focused != null && focused != selected) {
				Point origin = SwingUtilities.convertPoint(focused.getJComponent(), new Point(), this);
				Graphics componentGraphics = g.create(origin.x, origin.y, focused.getJComponent().getWidth(), focused.getJComponent()
						.getHeight());
				FOCUSED_BORDER.paintBorder(focused.getJComponent(), componentGraphics, 0, 0, focused.getJComponent().getWidth(), focused
						.getJComponent().getHeight());
				componentGraphics.dispose();
			}
			if (selected != null) {
				Point origin = SwingUtilities.convertPoint(selected.getJComponent(), new Point(), this);
				Graphics componentGraphics = g.create(origin.x, origin.y, selected.getJComponent().getWidth(), selected.getJComponent()
						.getHeight());
				SELECTED_BORDER.paintBorder(selected.getJComponent(), componentGraphics, 0, 0, selected.getJComponent().getWidth(),
						selected.getJComponent().getHeight());
				componentGraphics.dispose();
			}
		}
	}

	public FIBEditorController(FIBModelFactory factory, FIBComponent fibComponent, FIBGenericEditor editor) {
		this(factory, fibComponent, editor, null);
		// Class testClass = null;
		boolean instantiable = fibComponent.getDataClass() != null && !Modifier.isAbstract(fibComponent.getDataClass().getModifiers());
		if (instantiable) {
			try {
				instantiable = fibComponent.getDataClass().getConstructor(new Class[0]) != null;
			} catch (SecurityException e) {
				instantiable = false;
			} catch (NoSuchMethodException e) {
				instantiable = false;
			}
		}
		if (instantiable) {
			try {
				// testClass = Class.forName(fibComponent.getDataClassName());
				Object testData = fibComponent.getDataClass().newInstance();
				fibPanel.getController().setDataObject(testData);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			fibPanel.getController().updateWithoutDataObject();
		}

	}

	public FIBEditorController(FIBModelFactory factory, FIBComponent fibComponent, FIBGenericEditor editor, Object dataObject) {
		this(factory, fibComponent, editor, dataObject, FIBController.instanciateController(fibComponent, FIBAbstractEditor.LOCALIZATION));
	}

	public FIBEditorController(FIBModelFactory factory, FIBComponent fibComponent, FIBGenericEditor editor, Object dataObject,
			FIBController controller) {

		pcSupport = new PropertyChangeSupport(this);

		this.controller = controller;
		this.factory = factory;
		viewDelegates = new HashMap<FIBComponent, FIBEditableViewDelegate<?, ?>>();
		controller.setViewFactory(new EditorFIBViewFactory());

		this.editor = editor;
		this.fibComponent = fibComponent;

		contextualMenu = new ContextualMenu(this);

		addObserver(editor.getInspector());

		editorPanel = new JPanel(new BorderLayout());

		/*FIBComponent browserComponent = FIBLibrary.instance().retrieveFIBComponent(BROWSER_FIB, false, factory);
		browserController = new FIBBrowserController(browserComponent, this);
		FIBViewImpl<?, ?, ?> view = FIBController.makeView(browserComponent, browserController);
		view.getController().setDataObject(fibComponent);*/

		fibPanel = controller.buildView();

		FIBEditorBrowser editorBrowser = new FIBEditorBrowser(fibComponent, this);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorBrowser, new FibWrappingPanel(
				fibPanel.getResultingJComponent())/*new JScrollPane(fibPanel.getJComponent())*/);

		editorPanel.add(splitPane, BorderLayout.CENTER);

		if (dataObject != null) {
			fibPanel.getController().setDataObject(dataObject, true);
		} else {
			fibPanel.getController().updateWithoutDataObject();
		}

	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public FIBComponent getFIBComponent() {
		return fibComponent;
	}

	public Object getDataObject() {
		return controller.getDataObject();
	}

	public void setDataObject(Object anObject) {
		controller.setDataObject(anObject);
	}

	public FIBController getController() {
		return controller;
	}

	public FIBGenericEditor getEditor() {
		return editor;
	}

	public FIBModelFactory getFactory() {
		return factory;
	}

	public JPanel getEditorPanel() {
		return editorPanel;
	}

	public ContextualMenu getContextualMenu() {
		return contextualMenu;
	}

	public FIBEditorPalette getPalette() {
		return editor.getPalette();
	}

	public FIBView<?, ?> getFibPanel() {
		return fibPanel;
	}

	public FIBComponent getFocusedObject() {
		return focusedObject;
	}

	public void setFocusedObject(FIBComponent aComponent) {
		if (aComponent != focusedObject) {
			// System.out.println("setFocusedObject with "+aComponent);
			FocusedObjectChange change = new FocusedObjectChange(focusedObject, aComponent);
			focusedObject = aComponent;
			setChanged();
			notifyObservers(change);
			editorPanel.repaint();
		}
	}

	public FIBComponent getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(FIBComponent aComponent) {
		if (aComponent != selectedObject) {
			logger.info("Switch from " + selectedObject + " to " + aComponent);
			FIBComponent oldValue = selectedObject;
			SelectedObjectChange change = new SelectedObjectChange(oldValue, aComponent);
			selectedObject = aComponent;
			setChanged();
			notifyObservers(change);
			editorPanel.repaint();
			getPropertyChangeSupport().firePropertyChange("selectedObject", oldValue, selectedObject);
		}

		// System.out.println("set selected: "+selectedObject);

		/*if (selectedObject != null) {
			fibPanel.getController().viewForComponent(selectedObject).getJComponent().setBorder(oldBorder);
		}

		if (aComponent != null) {
			selectedObject = aComponent;
			oldBorder = fibPanel.getController().viewForComponent(aComponent).getJComponent().getBorder();
			fibPanel.getController().viewForComponent(aComponent).getJComponent().setBorder(BorderFactory.createLineBorder(Color.BLUE));
			editor.getInspector().inspectObject(aComponent);
		}
		else {
			editor.getInspector().inspectObject(null);
		}*/
	}

	public void notifyFocusedAndSelectedObject() {
		FocusedObjectChange change1 = new FocusedObjectChange(focusedObject, focusedObject);
		setChanged();
		notifyObservers(change1);
		SelectedObjectChange change2 = new SelectedObjectChange(selectedObject, selectedObject);
		setChanged();
		notifyObservers(change2);
	}

	public FIBView<?, ?> viewForComponent(FIBComponent component) {
		return controller.viewForComponent(component);
	}

	/*public void keyTyped(KeyEvent e)
	{
		logger.fine("keyTyped() "+e);
		if (e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
			if (getSelectedObject() != null) {
				boolean deleteIt = JOptionPane.showConfirmDialog(editor.getFrame(),
						getSelectedObject()+": really delete this component (undoable operation) ?", "information",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
				if (deleteIt) {
					logger.info("Removing object "+getSelectedObject());
					getSelectedObject().delete();
				}
			}
		}
	}*/

	public void switchToLanguage(Language language) {
		controller.switchToLanguage(language);
	}

	public void registerViewDelegate(FIBEditableViewDelegate<?, ?> delegate) {
		viewDelegates.put(delegate.getFIBComponent(), delegate);
	}

	public void unregisterViewDelegate(FIBEditableViewDelegate<?, ?> delegate) {
		if (viewDelegates.get(delegate.getFIBComponent()) == delegate) {
			viewDelegates.remove(delegate.getFIBComponent());
		}
	}

	protected class EditorFIBViewFactory implements FIBViewFactory {
		@Override
		public FIBView<?, ?> makeContainer(FIBContainer fibContainer) {
			if (fibContainer instanceof FIBTab) {
				return new FIBEditableTabView((FIBTab) fibContainer, FIBEditorController.this);
			} else if (fibContainer instanceof FIBPanel) {
				return new FIBEditablePanelView((FIBPanel) fibContainer, FIBEditorController.this);
			} else if (fibContainer instanceof FIBTabPanel) {
				return new FIBEditableTabPanelView((FIBTabPanel) fibContainer, FIBEditorController.this);
			} else if (fibContainer instanceof FIBSplitPanel) {
				return new FIBEditableSplitPanelView((FIBSplitPanel) fibContainer, FIBEditorController.this);
			}
			return null;
		}

		@Override
		public FIBWidgetView<?, ?, ?> makeWidget(FIBWidget fibWidget) {
			if (fibWidget instanceof FIBTextField) {
				return new FIBEditableTextFieldWidget((FIBTextField) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBTextArea) {
				return new FIBEditableTextAreaWidget((FIBTextArea) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBEditor) {
				return new FIBEditableEditorWidget((FIBEditor) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBHtmlEditor) {
				return new FIBEditableHtmlEditorWidget((FIBHtmlEditor) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBLabel) {
				return new FIBEditableLabelWidget((FIBLabel) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBImage) {
				return new FIBEditableImageWidget((FIBImage) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBCheckBox) {
				return new FIBEditableCheckboxWidget((FIBCheckBox) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBTable) {
				return new FIBEditableTableWidget((FIBTable) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBBrowser) {
				return new FIBEditableBrowserWidget((FIBBrowser) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBDropDown) {
				return new FIBEditableDropDownWidget((FIBDropDown) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBRadioButtonList) {
				return new FIBEditableRadioButtonListWidget((FIBRadioButtonList) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBCheckboxList) {
				return new FIBEditableCheckboxListWidget((FIBCheckboxList) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBList) {
				return new FIBEditableListWidget((FIBList) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBNumber) {
				FIBNumber w = (FIBNumber) fibWidget;
				switch (w.getNumberType()) {
				case ByteType:
					return new FIBEditableNumberWidget.FIBEditableByteWidget(w, FIBEditorController.this);
				case ShortType:
					return new FIBEditableNumberWidget.FIBEditableShortWidget(w, FIBEditorController.this);
				case IntegerType:
					return new FIBEditableNumberWidget.FIBEditableIntegerWidget(w, FIBEditorController.this);
				case LongType:
					return new FIBEditableNumberWidget.FIBEditableLongWidget(w, FIBEditorController.this);
				case FloatType:
					return new FIBEditableNumberWidget.FIBEditableFloatWidget(w, FIBEditorController.this);
				case DoubleType:
					return new FIBEditableNumberWidget.FIBEditableDoubleWidget(w, FIBEditorController.this);
				default:
					break;
				}
			}
			if (fibWidget instanceof FIBColor) {
				return new FIBEditableColorWidget((FIBColor) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBFont) {
				return new FIBEditableFontWidget((FIBFont) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBFile) {
				return new FIBEditableFileWidget((FIBFile) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBButton) {
				return new FIBEditableButtonWidget((FIBButton) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBCustom) {
				return new FIBEditableCustomWidget((FIBCustom) fibWidget, FIBEditorController.this);
			}
			if (fibWidget instanceof FIBReferencedComponent) {
				return new FIBEditableReferencedComponentWidget((FIBReferencedComponent) fibWidget, FIBEditorController.this, this);
			}
			if (fibWidget instanceof FIBTextPane) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Can't handle editable FIBTextPane!");
				}
			}
			if (fibWidget instanceof FIBEditorPane) {
				return new FIBEditableEditorPaneWidget((FIBEditorPane) fibWidget, FIBEditorController.this);
			}
			return null;
		}
	}

	public DropListener buildPaletteDropListener(FIBEditableView<?, ?> editableView, PlaceHolder placeHolder) {
		return new DropListener(editableView, placeHolder);
	}

}