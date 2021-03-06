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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.model.FIBValidationModel;
import org.openflexo.gina.model.FIBValidationReport;
import org.openflexo.gina.swing.editor.EditedFIBComponent;
import org.openflexo.gina.swing.editor.FIBEditor;
import org.openflexo.gina.swing.editor.palette.DropListener;
import org.openflexo.gina.swing.editor.palette.FIBEditorPalettes;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableView;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableViewDelegate;
import org.openflexo.gina.swing.editor.widget.FIBEditorBrowser;
import org.openflexo.gina.swing.utils.FocusedObjectChange;
import org.openflexo.gina.swing.utils.SelectedObjectChange;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.undo.UndoManager;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This is a controller managing the edition of a {@link EditedFIBComponent}<br>
 * 
 * Swing view to be used is given by {@link #getEditorPanel()} method.
 * 
 * Such controller manages:
 * <ul>
 * <li>an single instance of {@link EditedFIBComponent}</li>
 * <li>a</li>
 * </ul>
 * 
 * @author sylvain
 *
 */
public class FIBEditorController extends Observable implements HasPropertyChangeSupport {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditorController.class.getPackage().getName());

	private final FIBController controller;

	private final EditedFIBComponent editedComponent;

	private final FIBEditor editor;

	private FIBEditorPanel editorPanel;
	private FIBEditorBrowser editorBrowser;

	private FIBModelObject selectedObject = null;
	private FIBModelObject focusedObject = null;

	private final ContextualMenu contextualMenu;

	private final Map<FIBComponent, FIBSwingEditableViewDelegate<?, ?>> viewDelegates;

	private final PropertyChangeSupport pcSupport;

	private FIBValidationReport validationReport;

	public FIBEditorController(EditedFIBComponent editedComponent, FIBEditor editor, JFrame frame) {
		this(editedComponent, null, editor, frame);
		// Class testClass = null;

		if (editedComponent.getDataObject() != null) {
			editorPanel.updateWithDataObject(editedComponent.getDataObject());
		}
		else {

			if (editedComponent.getFIBComponent() instanceof FIBContainer) {
				FIBContainer fibContainer = (FIBContainer) editedComponent.getFIBComponent();
				boolean instantiable = fibContainer.getDataClass() != null
						&& !Modifier.isAbstract(fibContainer.getDataClass().getModifiers());
				if (instantiable) {
					try {
						instantiable = fibContainer.getDataClass().getConstructor(new Class[0]) != null;
					} catch (SecurityException e) {
						instantiable = false;
					} catch (NoSuchMethodException e) {
						instantiable = false;
					}
				}
				if (instantiable) {
					try {
						// testClass =
						// Class.forName(fibComponent.getDataClassName());
						Object testData = fibContainer.getDataClass().newInstance();
						editorPanel.updateWithDataObject(testData);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				else {
					editorPanel.updateWithoutDataObject();
				}
			}
			else {
				editorPanel.updateWithoutDataObject();
			}
		}
	}

	public FIBEditorController(EditedFIBComponent editedComponent, Object dataObject, FIBEditor editor, JFrame frame) {
		this(editedComponent, dataObject, editor, FIBController.instanciateController(editedComponent.getFIBComponent(),
				SwingEditorViewFactory.INSTANCE, FIBModelObjectImpl.READ_ONLY_GINA_LOCALIZATION), frame);
	}

	public FIBEditorController(EditedFIBComponent editedComponent, Object dataObject, FIBEditor editor, FIBController controller,
			JFrame frame) {

		pcSupport = new PropertyChangeSupport(this);
		this.editedComponent = editedComponent;

		this.controller = controller;
		viewDelegates = new HashMap<>();
		controller.setViewFactory(new SwingEditorViewFactory(this));

		this.editor = editor;

		contextualMenu = new ContextualMenu(this, frame);

		if (editor.getInspector() != null) {
			addObserver(editor.getInspector());
		}

		if (editor.getInspectors() != null) {
			addObserver(editor.getInspectors());
		}

		editorPanel = new FIBEditorPanel(controller);

		editorBrowser = new FIBEditorBrowser(editedComponent.getFIBComponent(), this);

		if (dataObject != null) {
			editorPanel.updateWithDataObject(dataObject);
		}
		else {
			editorPanel.updateWithoutDataObject();
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

	public EditedFIBComponent getEditedComponent() {
		return editedComponent;
	}

	public FIBComponent getFIBComponent() {
		return editedComponent.getFIBComponent();
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

	public FIBEditor getEditor() {
		return editor;
	}

	public FIBModelFactory getFactory() {
		return editedComponent.getFactory();
	}

	public FIBEditorPanel getEditorPanel() {
		return editorPanel;
	}

	public ContextualMenu getContextualMenu() {
		return contextualMenu;
	}

	public FIBEditorPalettes getPalette() {
		return editor.getPalettes();
	}

	public FIBEditorBrowser getEditorBrowser() {
		return editorBrowser;
	}

	public FIBModelObject getFocusedObject() {
		return focusedObject;
	}

	public void setFocusedObject(FIBModelObject aComponent) {

		if (aComponent != focusedObject) {
			// System.out.println("setFocusedObject with "+aComponent);
			FocusedObjectChange change = new FocusedObjectChange(focusedObject, aComponent);
			focusedObject = aComponent;
			setChanged();
			notifyObservers(change);
			editorPanel.repaint();
		}
	}

	public FIBModelObject getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(FIBModelObject aComponent) {
		if (aComponent != selectedObject) {
			// System.out.println("setSelectedObject from " + selectedObject + " to " + aComponent);
			FIBModelObject oldValue = selectedObject;
			SelectedObjectChange change = new SelectedObjectChange(oldValue, aComponent);
			selectedObject = aComponent;
			setChanged();
			notifyObservers(change);
			editorPanel.repaint();
			getPropertyChangeSupport().firePropertyChange("selectedObject", oldValue, selectedObject);
		}

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

	/*
	 * public void keyTyped(KeyEvent e) { logger.fine("keyTyped() "+e); if
	 * (e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() ==
	 * KeyEvent.VK_BACK_SPACE) { if (getSelectedObject() != null) { boolean
	 * deleteIt = JOptionPane.showConfirmDialog(editor.getFrame(),
	 * getSelectedObject
	 * ()+": really delete this component (undoable operation) ?",
	 * "information", JOptionPane.YES_NO_CANCEL_OPTION,
	 * JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION; if (deleteIt)
	 * { logger.info("Removing object "+getSelectedObject());
	 * getSelectedObject().delete(); } } } }
	 */

	public void switchToLanguage(Language language) {
		controller.switchToLanguage(language);
	}

	public void registerViewDelegate(FIBSwingEditableViewDelegate<?, ?> delegate) {
		viewDelegates.put(delegate.getFIBComponent(), delegate);
	}

	public void unregisterViewDelegate(FIBSwingEditableViewDelegate<?, ?> delegate) {
		if (viewDelegates.get(delegate.getFIBComponent()) == delegate) {
			viewDelegates.remove(delegate.getFIBComponent());
		}
	}

	public DropListener buildPaletteDropListener(FIBSwingEditableView<?, ?> editableView) {
		DropListener returned = new DropListener(editableView);
		dropListeners.put(editableView, returned);
		return returned;
	}

	private final Map<FIBSwingEditableView<?, ?>, DropListener> dropListeners = new HashMap<>();

	private boolean isDragging = false;

	public boolean isDragging() {
		return isDragging;
	}

	public void dragEnter(DropListener dl) {

		isDragging = true;

		if (getSelectedObject() != null) {
			setSelectedObject(null);
		}
		// System.out.println("Drag ENTER in " + dl.getEditableView());
		dropListeners.put(dl.getEditableView(), dl);
		if (dl.getEditableView().getParentView() != null) {
			DropListener parentDL = dropListeners.get(dl.getEditableView().getParentView());
			dl.setParentDropListener(parentDL);
		}
	}

	public void dragExit(DropListener dl) {
		// System.out.println("Drag EXIT in " + dl.getEditableView());
		isDragging = false;
	}

	public void dragEnd(DropListener dl) {

		isDragging = false;

		// System.out.println("Drag END in " + dl.getEditableView());
		// dropListeners.clear();
	}

	public UndoManager getUndoManager() {
		return null;
	}

	public FIBValidationReport getValidationReport() {
		if (validationReport == null) {
			try {
				validationReport = getValidationModel().validate(getFIBComponent());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return validationReport;
	}

	public FIBValidationModel getValidationModel() {
		return getFIBComponent().getModelFactory().getValidationModel();
	}

	@SuppressWarnings("serial")
	public class FIBEditorPanel extends JPanel {

		private JFIBView<?, ?> fibPanel;

		public FIBEditorPanel(FIBController controller) {
			super(new BorderLayout());

			fibPanel = (JFIBView<?, ?>) controller.buildView();
			add(fibPanel.getJComponent(), BorderLayout.CENTER);
		}

		public FIBEditorController getEditorController() {
			return FIBEditorController.this;
		}

		public void updateWithDataObject(Object data) {
			fibPanel.getController().setDataObject(data, true);
		}

		public void updateWithoutDataObject() {
			fibPanel.getController().setDataObject(null, true);
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			paintFocusedAndSelected(g);
		}

		private void paintFocusedAndSelected(Graphics g) {
			FIBSwingEditableViewDelegate<?, ?> focused = viewDelegates.get(getFocusedObject());
			FIBSwingEditableViewDelegate<?, ?> selected = viewDelegates.get(getSelectedObject());
			if (focused != null && focused != selected && focused.getResultingJComponent() != null) {
				Point origin = SwingUtilities.convertPoint(focused.getResultingJComponent(), new Point(), this);
				// Graphics componentGraphics = g.create(origin.x, origin.y,
				// focused.getJComponent().getWidth(), focused
				// .getJComponent().getHeight());
				Rectangle bounds = null;
				if (focused.getView().isOperatorContentsStart()) {
					bounds = new Rectangle(origin.x + FIBSwingEditableView.OPERATOR_ICON_SPACE, origin.y,
							focused.getResultingJComponent().getWidth() - FIBSwingEditableView.OPERATOR_ICON_SPACE - 1,
							focused.getJComponent().getHeight() - 1);
				}
				else {
					bounds = new Rectangle(origin.x, origin.y, focused.getResultingJComponent().getWidth() - 1,
							focused.getJComponent().getHeight() - 1);
				}

				Graphics2D g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				g2.setColor(Color.WHITE);
				g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				g2.setStroke(new BasicStroke(0.5f));
				g2.setColor(Color.LIGHT_GRAY);
				g2.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);

				// FOCUSED_BORDER.paintBorder(focused.getJComponent(),
				// componentGraphics, 0, 0, focused.getJComponent()
				// .getWidth(), focused.getJComponent().getHeight());
				// componentGraphics.dispose();
			}
			if (selected != null && selected.getResultingJComponent() != null) {
				Point origin = SwingUtilities.convertPoint(selected.getResultingJComponent(), new Point(), this);
				// Graphics componentGraphics = g.create(origin.x, origin.y,
				// selected.getJComponent().getWidth(), selected
				// .getJComponent().getHeight());

				Rectangle bounds;
				if (selected.getView().isOperatorContentsStart()) {
					bounds = new Rectangle(origin.x + FIBSwingEditableView.OPERATOR_ICON_SPACE, origin.y,
							selected.getResultingJComponent().getWidth() - FIBSwingEditableView.OPERATOR_ICON_SPACE - 1,
							selected.getJComponent().getHeight() - 1);
				}
				else {
					bounds = new Rectangle(origin.x, origin.y, selected.getResultingJComponent().getWidth() - 1,
							selected.getJComponent().getHeight() - 1);
				}
				Graphics2D g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
				g2.setColor(Color.BLUE);
				g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				g2.setStroke(new BasicStroke(0.5f));
				g2.setColor(Color.BLUE.brighter());
				g2.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);

				// SELECTED_BORDER.paintBorder(selected.getJComponent(),
				// componentGraphics, 0, 0, selected.getJComponent()
				// .getWidth(), selected.getJComponent().getHeight());
				// componentGraphics.dispose();
			}
		}
	}

}
