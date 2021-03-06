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

package org.openflexo.gina.swing.editor.palette;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
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
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBOperator;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.model.container.layout.ComponentConstraints;
import org.openflexo.gina.swing.editor.FIBEditor;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerView;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableViewDelegate.FIBDropTarget;
import org.openflexo.gina.swing.editor.view.OperatorDecorator;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBOperatorView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.logging.FlexoLogger;

public class PaletteElement implements FIBDraggable /* implements Transferable */ {
	static final Logger logger = FlexoLogger.getLogger(PaletteElement.class.getPackage().getName());

	private final FIBEditorPalette palette;
	private final FIBComponent modelComponent;
	private final FIBComponent representationComponent;
	private final FIBView<FIBComponent, ? extends JComponent> view;

	private final DragSource dragSource;
	private final DragGestureListener dgListener;
	private final DragSourceListener dsListener;
	private final int dragAction = DnDConstants.ACTION_COPY;
	private final Hashtable<JComponent, DragGestureRecognizer> dgr;

	// private static final DataFlavor DATA_FLAVOR = new
	// DataFlavor(PaletteElement.class, "PaletteElement");

	public PaletteElement(FIBComponent modelComponent, FIBComponent representationComponent, FIBEditorPalette palette, Object dataObject) {
		this.modelComponent = modelComponent;
		this.representationComponent = representationComponent;
		this.palette = palette;

		int x = Integer.parseInt(representationComponent.getParameter("x"));
		int y = Integer.parseInt(representationComponent.getParameter("y"));

		view = FIBController.makeView(representationComponent, SwingViewFactory.INSTANCE, FIBEditor.EDITOR_LOCALIZATION, null, true);
		view.getController().setVariableValue("paletteData", dataObject);

		if (view.getTechnologyComponent() != null) {
			Dimension size = view.getTechnologyComponent().getPreferredSize();
			view.getTechnologyComponent().setBounds(x, y, size.width, size.height);
		}

		this.dgListener = new DGListener();
		this.dragSource = DragSource.getDefaultDragSource();
		this.dsListener = new DSListener();

		dgr = new Hashtable<>();

		if (view.getTechnologyComponent() != null) {
			recursivelyRemoveFocusableProperty(view.getTechnologyComponent());
			recursivelyAddDGR(view.getTechnologyComponent());
		}

		enableDragging();

	}

	private void recursivelyRemoveFocusableProperty(JComponent c) {
		c.setRequestFocusEnabled(false);
		c.setFocusable(false);

		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				if (c2 instanceof JComponent) {
					recursivelyRemoveFocusableProperty((JComponent) c2);
				}
			}
		}
	}

	private void recursivelyAddDGR(JComponent c) {
		for (MouseListener ml : c.getMouseListeners()) {
			c.removeMouseListener(ml);
		}
		DragGestureRecognizer newDGR = dragSource.createDefaultDragGestureRecognizer(c, dragAction, dgListener);
		dgr.put(c, newDGR);

		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				if (c2 instanceof JComponent) {
					recursivelyAddDGR((JComponent) c2);
				}
			}
		}
	}

	public FIBView<FIBComponent, ? extends JComponent> getView() {
		return view;
	}

	@Override
	final public void enableDragging() {
		for (JComponent j : dgr.keySet()) {
			dgr.get(j).setComponent(j);
		}
	}

	@Override
	public void disableDragging() {
		for (JComponent j : dgr.keySet()) {
			dgr.get(j).setComponent(null);
		}
	}

	@Override
	public boolean acceptDragging(FIBDropTarget target) {
		// logger.fine("acceptDragging ? for component: " +
		// target.getFIBComponent() + " place holder: " +
		// target.getPlaceHolder());
		return true;
	}

	/*
	 * @Override public DataFlavor[] getTransferDataFlavors() { return new
	 * DataFlavor[] { DATA_FLAVOR }; }
	 * 
	 * @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
	 * return true; }
	 * 
	 * @Override public Object getTransferData(DataFlavor flavor) throws
	 * UnsupportedFlavorException, IOException { return this; }
	 */

	@Override
	public boolean elementDragged(FIBDropTarget target, DropListener dropListener, Point pt) {

		// System.out.println("elementDragged(), dl=" + dropListener.getEditableView());
		// System.out.println("target=" + target);

		PlaceHolder ph = target.getPlaceHolder(dropListener, pt);

		/*System.out.println("ph=" + ph);
		System.out.println("modelComponent=" + modelComponent);
		System.out.println("target.getFIBComponent()=" + target.getFIBComponent());*/

		FIBOperator targetOperator = null;
		FIBOperatorView<?, ?, ?> operatorView = null;

		boolean isTabInsertion = modelComponent instanceof FIBPanel && target.getFIBComponent() instanceof FIBTabPanel;
		boolean componentInsertionInOperator = target.getFIBComponent() instanceof FIBOperator;
		if (componentInsertionInOperator) {
			targetOperator = (FIBOperator) target.getFIBComponent();
			operatorView = (FIBOperatorView<?, ?, ?>) target.getEditableView();
		}

		// System.out.println("editable view = " + target.getEditableView());
		// System.out.println("delegate = " + target.getEditableView().getDelegate());
		// System.out.println("les operators = " + target.getEditableView().getDelegate());

		if (!isTabInsertion && !componentInsertionInOperator && ph == null) {
			// May be this is is an operator area ???

			// System.out.println("target.getEditableView().getParentView()=" + target.getEditableView().getParentView());
			FIBContainerView<?, ?, ?> parentView = target.getEditableView().getParentView();
			if (parentView instanceof FIBOperatorView) {
				operatorView = (FIBOperatorView<?, ?, ?>) parentView;
				parentView = operatorView.getConcreteContainerView();
			}
			if (parentView instanceof FIBSwingEditableContainerView) {
				Point ptInParent = SwingUtilities.convertPoint(target.getEditableView().getResultingJComponent(), pt,
						((JFIBView) parentView).getResultingJComponent());
				// Point ptInParent = new Point(pt.x + target.getEditableView().getResultingJComponent().getBounds().x,
				// pt.y + target.getEditableView().getResultingJComponent().getBounds().y);
				OperatorDecorator d = ((FIBSwingEditableContainerView<?, ?>) parentView).getDelegate().getOperatorDecorator(ptInParent);
				if (d != null) {
					componentInsertionInOperator = true;
					targetOperator = d.getOperator();
					operatorView = d.getOperatorView();
				}
			}
			/*boolean deleteIt = JOptionPane.showConfirmDialog(target.getFrame(),
					target.getFIBComponent() + ": really delete this component (undoable operation) ?", "information",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
			if (!deleteIt) {
				return false;
			}*/
		}

		FIBComponent newComponent = (FIBComponent) modelComponent.cloneObject();

		if (newComponent instanceof FIBPanel) {
			newComponent.setTemporarySize(representationComponent.getWidth(), representationComponent.getHeight());
		}

		newComponent.setLocalizedDictionary(null);
		newComponent.clearParameters();

		/*
		 * logger.info("Element dragged with component: " + newComponent +
		 * " place holder: " + ph);
		 * System.out.println(newComponent.getFactory().
		 * stringRepresentation(newComponent)); Thread.dumpStack();
		 */

		try {
			FIBComponent targetComponent = target.getFIBComponent();
			FIBContainer containerComponent = targetComponent.getParent();

			if (componentInsertionInOperator) {
				// Handle component insertion in operator
				System.out.println("Insert " + newComponent + " in operator " + targetOperator);
				ComponentConstraints constraints = operatorView.getConcreteContainerView().getLayoutManager().makeDefaultConstraints();
				// System.out.println("using constraints=" + targetOperator.getConstraints());
				targetOperator.addToSubComponents(newComponent, constraints);
				newComponent.translateNameWhenRequired();
				return true;
			}
			else if (isTabInsertion) {
				// Handle tab insertion
				if (targetComponent instanceof FIBTab && !(newComponent instanceof FIBPanel)) {
					return false;
				}
				if (containerComponent == null) {
					return false;
				}
				FIBTab newTabComponent = containerComponent.getModelFactory().newFIBTab();
				newTabComponent.setLayout(Layout.border);
				newTabComponent.setTitle("NewTab");
				newTabComponent.finalizeDeserialization();
				((FIBTabPanel) targetComponent).addToSubComponents(newTabComponent);
				newTabComponent.translateNameWhenRequired();
				return true;
			}
			else if (ph != null) {
				// Component inserted in placeholder
				System.out.println("Insert in placeholder " + ph);
				ph.willDelete();
				ph.insertComponent(newComponent, -1);
				newComponent.translateNameWhenRequired();
				ph.hasDeleted();
				return true;
			}
			else {
				// Normal case, we replace targetComponent by newComponent
				if (containerComponent == null) {
					return false;
				}
				boolean deleteIt = JOptionPane.showConfirmDialog(target.getFrame(),
						target.getFIBComponent() + ": really delete this component (undoable operation) ?", "information",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
				if (!deleteIt) {
					return false;
				}
				ComponentConstraints constraints = targetComponent.getConstraints();
				containerComponent.removeFromSubComponents(targetComponent);
				// WAS:
				// containerComponent.removeFromSubComponentsNoNotification(targetComponent);
				// WAS: No notification, we will do it later, to avoid
				// reindexing
				targetComponent.delete();
				containerComponent.addToSubComponents(newComponent, constraints);
				newComponent.translateNameWhenRequired();
				return true;
			}
		} finally {
			dropListener.getEditableView().getEditorController().setSelectedObject(newComponent);
		}

	}

	/**
	 * DGListener a listener that will start the drag. has access to top level's dsListener and dragSource
	 * 
	 * @see java.awt.dnd.DragGestureListener
	 * @see java.awt.dnd.DragSource
	 * @see java.awt.datatransfer.StringSelection
	 */
	class DGListener implements DragGestureListener {
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

			PaletteElementDrag transferable = new PaletteElementDrag(PaletteElement.this, e.getDragOrigin());

			try {
				// initial cursor, transferrable, dsource listener
				e.startDrag(FIBEditorPalette.dropKO, transferable, dsListener);

				FIBEditorPalettes.logger.info("Starting drag for " + palette);
				// getDrawingView().captureDraggedNode(PaletteElementView.this,
				// e);
			} catch (Exception idoe) {
				idoe.printStackTrace();
				FIBEditorPalettes.logger.warning("Unexpected exception " + idoe);
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
	public class DSListener implements DragSourceListener {

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragDropEnd(DragSourceDropEvent e) {

			// getDrawingView().resetCapturedNode();
			if (e.getDropSuccess() == false) {
				if (FIBEditorPalettes.logger.isLoggable(Level.INFO)) {
					FIBEditorPalettes.logger.info("Dropping was not successful");
				}
				return;
			}
			/*
			 * the dropAction should be what the drop target specified in
			 * acceptDrop
			 */
			// this is the action selected by the drop target
			if (e.getDropAction() == DnDConstants.ACTION_MOVE) {
				System.out.println("Coucou, que se passe-t-il par ici ?");
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
			// interface
			// System.out.println("dragOver() with "+context+" component="+e.getSource());
		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragExit(DragSourceEvent e) {
			// System.out.println("dragExit() with "+context+" component="+e.getSource());
			// interface
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
