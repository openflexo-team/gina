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

package org.openflexo.gina.swing.editor.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.editor.controller.DraggedFIBComponent;
import org.openflexo.gina.swing.editor.controller.ExistingElementDrag;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorPalette;
import org.openflexo.gina.swing.editor.view.container.JFIBEditableSplitPanelView;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.Focusable;

public class FIBSwingEditableViewDelegate<M extends FIBComponent, J extends JComponent> implements MouseListener, FocusListener, Focusable {

	static final Logger logger = FlexoLogger.getLogger(FIBSwingEditableViewDelegate.class.getPackage().getName());

	private FIBSwingEditableView<M, J> view;

	private final DragSource dragSource;
	private final MoveDGListener dgListener;
	private final MoveDSListener dsListener;
	private final int dragAction = DnDConstants.ACTION_MOVE;

	public FIBSwingEditableViewDelegate(FIBSwingEditableView<M, J> view) {
		this.view = view;

		JComponent jComponent = view.getJComponent();

		jComponent.setFocusable(true);
		jComponent.setRequestFocusEnabled(true);

		recursivelyAddListenersTo(jComponent);

		// view.getJComponent().addMouseListener(this);
		// view.getJComponent().addFocusListener(this);

		view.getEditorController().registerViewDelegate(this);

		dragSource = DragSource.getDefaultDragSource();
		dsListener = new MoveDSListener();
		dgListener = new MoveDGListener();

		if (!(view instanceof JFIBEditableSplitPanelView)) {
			DragGestureRecognizer newDGR = dragSource.createDefaultDragGestureRecognizer(view.getTechnologyComponent(), dragAction,
					dgListener);
		}

	}

	public FIBSwingEditableView<M, J> getView() {
		return view;
	}

	public void delete() {
		logger.fine("Delete delegate view=" + view);
		JComponent jComponent = view.getJComponent();
		recursivelyDeleteListenersFrom(jComponent);
		view.getEditorController().unregisterViewDelegate(this);
		view = null;
	}

	private void recursivelyAddListenersTo(Component c) {
		/*
		 * for (MouseListener ml : c.getMouseListeners()) {
		 * c.removeMouseListener(ml); }
		 */
		c.addMouseListener(this);
		c.addFocusListener(this);
		// Listen to drag'n'drop events
		new FIBDropTarget(view);

		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				if (!isComponentRootComponentForAnyFIBView(c2)) {
					recursivelyAddListenersTo(c2);
				}
			}
		}
	}

	private void recursivelyDeleteListenersFrom(Component c) {
		c.removeMouseListener(this);
		c.removeFocusListener(this);

		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				if (!isComponentRootComponentForAnyFIBView(c2)) {
					recursivelyDeleteListenersFrom(c2);
				}
			}
		}
	}

	private boolean isComponentRootComponentForAnyFIBView(Component c) {
		for (FIBView<?, ?> v : getController().getViews()) {
			JComponent resultingJComponent = ((JFIBView<?, ?>) v).getResultingJComponent();
			if (resultingJComponent == c) {
				return true;
			}
		}
		return false;
	}

	public FIBEditorController getEditorController() {
		return view.getEditorController();
	}

	public FIBController getController() {
		return view.getEditorController().getController();
	}

	final public M getFIBComponent() {
		return view.getComponent();
	}

	public JComponent getJComponent() {
		return view.getJComponent();
	}

	@Override
	public boolean isFocused() {
		return getEditorController().getFocusedObject() == getFIBComponent();
	}

	@Override
	public void setFocused(boolean focused) {
		getEditorController().setFocusedObject(getFIBComponent());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		getEditorController().setSelectedObject(getFIBComponent());
		getJComponent().requestFocusInWindow();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
			getEditorController().getContextualMenu().displayPopupMenu(getFIBComponent(), getJComponent(), e);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// getController().setSelectedObject(null);
	}

	@Override
	public void focusGained(FocusEvent e) {
		getEditorController().setSelectedObject(getFIBComponent());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		getEditorController().setFocusedObject(getFIBComponent());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		getEditorController().setFocusedObject(null);
	}

	public static class FIBDropTarget extends DropTarget {
		private final FIBSwingEditableView editableView;
		private final PlaceHolder placeHolder = null;

		public FIBDropTarget(FIBSwingEditableView<?, ?> editableView) {
			super(editableView.getJComponent(), DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE,
					editableView.getEditorController().buildPaletteDropListener(editableView), true);
			this.editableView = editableView;
			logger.fine("Made FIBDropTarget for " + getFIBComponent());
		}

		/*public FIBDropTarget(PlaceHolder placeHolder) {
			super(placeHolder, DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE, placeHolder.getView()
					.getEditorController().buildPaletteDropListener(placeHolder.getView(), placeHolder), true);
			this.placeHolder = placeHolder;
			this.editableView = placeHolder.getView();
			logger.fine("Made FIBDropTarget for " + getFIBComponent());
		}*/

		public PlaceHolder getPlaceHolder() {
			return placeHolder;
		}

		public boolean isPlaceHolder() {
			return placeHolder != null;
		}

		final public FIBComponent getFIBComponent() {
			return editableView.getComponent();
		}

		public FIBEditorController getFIBEditorController() {
			return editableView.getEditorController();
		}

	}

	/**
	 * DGListener a listener that will start the drag. has access to top level's dsListener and dragSource
	 * 
	 * @see java.awt.dnd.DragGestureListener
	 * @see java.awt.dnd.DragSource
	 * @see java.awt.datatransfer.StringSelection
	 */
	class MoveDGListener implements DragGestureListener {
		/**
		 * Start the drag if the operation is ok. uses java.awt.datatransfer.StringSelection to transfer the label's data
		 * 
		 * @param e
		 *            the event object
		 */
		@Override
		public void dragGestureRecognized(DragGestureEvent e) {
			logger.fine("dragGestureRecognized");

			// if the action is ok we go ahead
			// otherwise we punt
			if ((e.getDragAction() & dragAction) == 0) {
				return;
				// get the label's text and put it inside a Transferable
				// Transferable transferable = new StringSelection(
				// DragLabel.this.getText() );
			}

			ExistingElementDrag transferable = new ExistingElementDrag(new DraggedFIBComponent(view.getComponent()), e.getDragOrigin());

			try {
				// initial cursor, transferrable, dsource listener
				e.startDrag(FIBEditorPalette.dropKO, transferable, dsListener);
				logger.info("Starting existing element drag for " + view.getComponent());
				// getDrawingView().captureDraggedNode(PaletteElementView.this,
				// e);
			} catch (Exception idoe) {
				idoe.printStackTrace();
				logger.warning("Unexpected exception " + idoe);
			}
		}

	}

	/**
	 * DSListener a listener that will track the state of the DnD operation
	 * 
	 * @see java.awt.dnd.DragSourceListener
	 * @see java.awt.dnd.DragSource
	 * @see java.awt.datatransfer.StringSelection
	 */
	public class MoveDSListener implements DragSourceListener {

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragDropEnd(DragSourceDropEvent e) {

			// getDrawingView().resetCapturedNode();
			if (!e.getDropSuccess()) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Dropping was not successful");
				}
				return;
			}
			/*
			 * the dropAction should be what the drop target specified in
			 * acceptDrop
			 */
			// this is the action selected by the drop target
			if (e.getDropAction() == DnDConstants.ACTION_MOVE) {
				System.out.println("Tiens, que se passe-t-il donc ?");
			}

		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragEnter(DragSourceDragEvent e) {
			DragSourceContext context = e.getDragSourceContext();
			// System.out.println("dragEnter() with "+context+" component="+e.getSource());
			// intersection of the users selected action, and the source and
			// target actions
			int myaction = e.getDropAction();
			if ((myaction & dragAction) != 0) {
				context.setCursor(DragSource.DefaultCopyDrop);
			}
			else {
				context.setCursor(DragSource.DefaultCopyNoDrop);
			}
		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragOver(DragSourceDragEvent e) {

		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragExit(DragSourceEvent e) {

		}

		/**
		 * for example, press shift during drag to change to a link action
		 * 
		 * @param e
		 *            the event
		 */
		@Override
		public void dropActionChanged(DragSourceDragEvent e) {
			DragSourceContext context = e.getDragSourceContext();
			context.setCursor(DragSource.DefaultCopyNoDrop);
		}
	}

}
