package org.openflexo.gina.swing.view.widget;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.ExpressionEvaluator;
import org.openflexo.connie.java.expr.JavaExpressionEvaluator;
import org.openflexo.gina.model.widget.FIBBrowserDragOperation;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.BrowserCell;

/**
 * Swing implementation of a {@link JTree} supporting internal drag&drop<<br>
 * (Implements drag and drop inside JTree, between elements)
 * 
 * @author sylvain
 */
@SuppressWarnings("serial")
public class InternalDnDJTree extends DnDJTree implements DropTargetListener, DragSourceListener {

	public InternalDnDJTree(JFIBBrowserWidget<?> widget) {
		super(widget);

		if (widget.getComponent().getAllowsDragAndDrop()) {

			dragSource = DragSource.getDefaultDragSource();

			DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this, // DragSource
					DnDConstants.ACTION_MOVE, // specifies valid actions
					this // DragGestureListener
			);

			/* Eliminates right mouse clicks as valid actions - useful especially
			 * if you implement a JPopupMenu for the JTree
			 */
			dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);

			/* First argument:  Component to associate the target with
			 * Second argument: DropTargetListener 
			 */
			// Unused DropTarget dropTarget =
			new DropTarget(this, this);
		}

	}

	@Override
	public ExpressionEvaluator getEvaluator() {
		return new JavaExpressionEvaluator(this);
	}

	/** DropTargetListener interface method - What we do when drag is released */
	@Override
	public void drop(DropTargetDropEvent e) {
		// try {
		Transferable tr = e.getTransferable();

		// flavor not supported, reject drop
		if (!tr.isDataFlavorSupported(FIBBrowserModel.BROWSER_CELL_FLAVOR))
			e.rejectDrop();

		// cast into appropriate data type
		// BrowserCell childInfo = (BrowserCell) tr.getTransferData(FIBBrowserModel.BROWSER_CELL_FLAVOR);

		// get new parent node
		Point loc = e.getLocation();
		TreePath destinationPath = getPathForLocation(loc.x, loc.y);

		if (!isDroppable(selectedTreePath, destinationPath)) {
			e.rejectDrop();
			return;
		}

		// Unused BrowserCell newParent = (BrowserCell)
		destinationPath.getLastPathComponent();

		// get old parent node
		// Unused BrowserCell oldParent =
		getSelectedBrowserCell().getParent();

		int action = e.getDropAction();
		boolean copyAction = (action == DnDConstants.ACTION_COPY);

		/*System.out.println("Perform DND");
		System.out.println("from: " + oldParent);
		System.out.println("to: " + newParent);
		System.out.println("action: " + action);*/

		performDrop(selectedTreePath, destinationPath);

		if (copyAction)
			e.acceptDrop(DnDConstants.ACTION_COPY);
		else
			e.acceptDrop(DnDConstants.ACTION_MOVE);

		e.getDropTargetContext().dropComplete(true);

		// expand nodes appropriately - this probably isnt the best way...
		// DefaultTreeModel model = (DefaultTreeModel) getModel();
		// getModel().reload(oldParent);
		// getModel().reload(newParent);
		// TreePath parentPath = new TreePath(newParent.getPath());
		// expandPath(parentPath);

		/*} catch (IOException io) {
		e.rejectDrop();
		} catch (UnsupportedFlavorException ufe) {
		e.rejectDrop();
		}*/
	} // end of method

	/** DropTaregetListener interface method */
	@Override
	public void dragEnter(DropTargetDragEvent e) {
	}

	/** DropTaregetListener interface method */
	@Override
	public void dragExit(DropTargetEvent e) {
	}

	/** DropTaregetListener interface method */
	@Override
	public void dragOver(DropTargetDragEvent e) {
		// set cursor location. Needed in setCursor method
		Point cursorLocationBis = e.getLocation();
		TreePath destinationPath = getPathForLocation(cursorLocationBis.x, cursorLocationBis.y);

		// if destination path is okay accept drop...
		if (isDroppable(selectedTreePath, destinationPath)) {
			// System.out.println("C'est bien ca");
			e.acceptDrag(DnDConstants.ACTION_MOVE);
			setCursor(/*Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)*/dropOK);
			// setCursor(DragSource.DefaultMoveNoDrop);
		}
		// ...otherwise reject drop
		else {
			// System.out.println("Pas bon");
			e.rejectDrag();
			// setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			setCursor(/*DragSource.DefaultMoveNoDrop*/dropKO);
		}
	}

	/** DropTaregetListener interface method */
	@Override
	public void dropActionChanged(DropTargetDragEvent e) {
	}

	/**
	 * Convenience method to test whether drop location is valid
	 * 
	 * @param destination
	 *            The destination path
	 * @param dropper
	 *            The path for the node to be dropped
	 * @return null if no problems, otherwise an explanation
	 */
	private boolean isDroppable(TreePath dropper, TreePath destination) {

		if (destination == null || dropper == null) {
			return false;
		}

		// System.out.println("isDroppable() ?");

		draggedBrowserCell = (BrowserCell) dropper.getLastPathComponent();
		targetBrowserCell = (BrowserCell) destination.getLastPathComponent();

		// System.out.println("dragged=" + draggedBrowserCell.getRepresentedObject());
		// System.out.println("target=" + targetBrowserCell.getRepresentedObject());
		// System.out.println("browser el = " + draggedBrowserCell.getBrowserElement());
		// System.out.println("drag ops = " + draggedBrowserCell.getBrowserElement().getDragOperations());

		for (FIBBrowserDragOperation op : draggedBrowserCell.getBrowserElement().getDragOperations()) {
			// System.out.println("op=" + op);
			// System.out.println("targetBrowserCell.getBrowserElement()=" + targetBrowserCell.getBrowserElement());
			// System.out.println("op.getTargetElement()=" + op.getTargetElement());
			Boolean applicable = false;
			if (targetBrowserCell.getBrowserElement() == op.getTargetElement()) {
				try {
					// System.out.println("****** On se demande: " + op.getIsAvailable());
					// System.out.println("valide ? " + op.getIsAvailable().isValid());
					// System.out.println("reason ? " + op.getIsAvailable().invalidBindingReason());
					applicable = op.getIsAvailable().getBindingValue(this);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
				if (applicable != null && applicable) {
					// Yes! looked up move gesture
					return true;
				}
			}
		}

		return false;
	}

	private boolean performDrop(TreePath dropper, TreePath destination) {

		if (destination == null || dropper == null) {
			return false;
		}

		draggedBrowserCell = (BrowserCell) dropper.getLastPathComponent();
		targetBrowserCell = (BrowserCell) destination.getLastPathComponent();

		for (FIBBrowserDragOperation op : draggedBrowserCell.getBrowserElement().getDragOperations()) {
			boolean applicable = false;
			if (targetBrowserCell.getBrowserElement() == op.getTargetElement()) {
				try {
					applicable = op.getIsAvailable().getBindingValue(this);
					if (applicable) {
						op.getAction().execute(this);
						return true;
					}
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	/** DragGestureListener interface method */
	@Override
	public void dragGestureRecognized(DragGestureEvent e) {
		// Get the selected node

		System.out.println("dragGestureRecognized with " + e);

		BrowserCell dragNode = getSelectedBrowserCell();
		if (dragNode != null) {

			// Get the Transferable Object
			// Transferable transferable = (Transferable) dragNode.getUserObject();
			Transferable transferable = dragNode;

			/* ********************** CHANGED ********************** */

			// Select the appropriate cursor;
			Cursor cursor = DragSource.DefaultCopyNoDrop;
			int action = e.getDragAction();
			if (action == DnDConstants.ACTION_MOVE) {
				// cursor = DragSource.DefaultMoveNoDrop;
				cursor = dropKO;
			}

			// In fact the cursor is set to NoDrop because once an action is rejected
			// by a dropTarget, the dragSourceListener are no more invoked.
			// Setting the cursor to no drop by default is so more logical, because
			// when the drop is accepted by a component, then the cursor is changed by the
			// dropActionChanged of the default DragSource.

			// begin the drag
			dragSource.startDrag(e, cursor, transferable, this);
		}
	}

	/** DragSourceListener interface method */
	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		setCursor(Cursor.getDefaultCursor());
	}

	/** DragSourceListener interface method */
	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
		/* ********************** CHANGED ********************** */
		/* ****************** END OF CHANGE ******************** */
	}

	/** DragSourceListener interface method */
	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		/* ********************** CHANGED ********************** */
		/* ****************** END OF CHANGE ******************** */
		// setCursor(Cursor.getDefaultCursor());
	}

	/** DragSourceListener interface method */
	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	/** DragSourceListener interface method */
	@Override
	public void dragExit(DragSourceEvent dsde) {
		// setCursor(Cursor.getDefaultCursor());
	}

}
