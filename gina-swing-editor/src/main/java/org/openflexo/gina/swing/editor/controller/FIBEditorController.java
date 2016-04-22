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

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.swing.editor.EditedFIBComponent;
import org.openflexo.gina.swing.editor.FIBAbstractEditor;
import org.openflexo.gina.swing.editor.FIBGenericEditor;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableView;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableViewDelegate;
import org.openflexo.gina.swing.editor.widget.FIBEditorBrowser;
import org.openflexo.gina.swing.utils.FocusedObjectChange;
import org.openflexo.gina.swing.utils.SelectedObjectChange;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.NoInsetsBorder;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This is a controller managing the edition of an {@link EditedFIBComponent}<br>
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

	private final FIBModelFactory factory;

	// private final JPanel editorPanel2;
	// private final JFIBView<?, ?> fibPanel;

	private final FIBGenericEditor editor;

	private FIBEditorPanel editorPanel;
	private FIBEditorBrowser editorBrowser;

	private FIBComponent fibComponent = null;
	private FIBComponent selectedObject = null;
	private FIBComponent focusedObject = null;

	private final ContextualMenu contextualMenu;

	private static final Border FOCUSED_BORDER = new NoInsetsBorder(BorderFactory.createLineBorder(Color.RED));
	private static final Border SELECTED_BORDER = new NoInsetsBorder(BorderFactory.createLineBorder(Color.BLUE));

	private final Map<FIBComponent, FIBSwingEditableViewDelegate<?, ?>> viewDelegates;

	private final PropertyChangeSupport pcSupport;

	@SuppressWarnings("serial")
	public class FIBEditorPanel extends JPanel {

		private JFIBView<?, ?> fibPanel;

		public FIBEditorPanel(FIBController controller) {
			super(new BorderLayout());

			fibPanel = (JFIBView<?, ?>) controller.buildView();
			add(fibPanel.getJComponent());
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
			if (focused != null && focused != selected) {
				Point origin = SwingUtilities.convertPoint(focused.getResultingJComponent(), new Point(), this);
				// Graphics componentGraphics = g.create(origin.x, origin.y,
				// focused.getJComponent().getWidth(), focused
				// .getJComponent().getHeight());
				Rectangle bounds = new Rectangle(origin.x, origin.y, focused.getResultingJComponent().getWidth() - 1,
						focused.getJComponent().getHeight() - 1);
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
			if (selected != null) {
				Point origin = SwingUtilities.convertPoint(selected.getResultingJComponent(), new Point(), this);
				// Graphics componentGraphics = g.create(origin.x, origin.y,
				// selected.getJComponent().getWidth(), selected
				// .getJComponent().getHeight());

				Rectangle bounds = new Rectangle(origin.x, origin.y, selected.getResultingJComponent().getWidth() - 1,
						selected.getJComponent().getHeight() - 1);
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

	public FIBEditorController(FIBModelFactory factory, FIBComponent fibComponent, FIBGenericEditor editor, JFrame frame) {
		this(factory, fibComponent, editor, null, frame);
		// Class testClass = null;

		if (fibComponent instanceof FIBContainer) {
			FIBContainer fibContainer = (FIBContainer) fibComponent;
			boolean instantiable = fibContainer.getDataClass() != null && !Modifier.isAbstract(fibContainer.getDataClass().getModifiers());
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
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

	public FIBEditorController(FIBModelFactory factory, FIBComponent fibComponent, FIBGenericEditor editor, Object dataObject,
			JFrame frame) {
		this(factory, fibComponent, editor, dataObject,
				FIBController.instanciateController(fibComponent, SwingEditorViewFactory.INSTANCE, FIBAbstractEditor.LOCALIZATION), frame);
	}

	public FIBEditorController(FIBModelFactory factory, FIBComponent fibComponent, FIBGenericEditor editor, Object dataObject,
			FIBController controller, JFrame frame) {

		pcSupport = new PropertyChangeSupport(this);

		this.controller = controller;
		this.factory = factory;
		viewDelegates = new HashMap<FIBComponent, FIBSwingEditableViewDelegate<?, ?>>();
		controller.setViewFactory(new SwingEditorViewFactory(this));

		this.editor = editor;
		this.fibComponent = fibComponent;

		contextualMenu = new ContextualMenu(this, frame);

		addObserver(editor.getInspector());

		editorPanel = new FIBEditorPanel(controller);

		editorBrowser = new FIBEditorBrowser(fibComponent, this);

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

	public FIBEditorBrowser getEditorBrowser() {
		return editorBrowser;
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
			logger.fine("Switch from " + selectedObject + " to " + aComponent);
			FIBComponent oldValue = selectedObject;
			SelectedObjectChange change = new SelectedObjectChange(oldValue, aComponent);
			selectedObject = aComponent;
			setChanged();
			notifyObservers(change);
			editorPanel.repaint();
			getPropertyChangeSupport().firePropertyChange("selectedObject", oldValue, selectedObject);
		}

		// System.out.println("set selected: "+selectedObject);

		/*
		 * if (selectedObject != null) {
		 * fibPanel.getController().viewForComponent
		 * (selectedObject).getJComponent().setBorder(oldBorder); }
		 * 
		 * if (aComponent != null) { selectedObject = aComponent; oldBorder =
		 * fibPanel
		 * .getController().viewForComponent(aComponent).getJComponent().
		 * getBorder();
		 * fibPanel.getController().viewForComponent(aComponent).getJComponent
		 * ().setBorder(BorderFactory.createLineBorder(Color.BLUE));
		 * editor.getInspector().inspectObject(aComponent); } else {
		 * editor.getInspector().inspectObject(null); }
		 */
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

	private final Map<FIBSwingEditableView<?, ?>, DropListener> dropListeners = new HashMap<FIBSwingEditableView<?, ?>, DropListener>();

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

}
