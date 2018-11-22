package org.openflexo.gina.swing.view.widget;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTree;

/**
 * Swing implementation of a {@link JTree} supporting external drag&drop<br>
 * It suppose that some elements of the tree may be dragged somewhere
 * 
 * @see DropTargetListener
 * 
 * @author sylvain
 */
@SuppressWarnings("serial")
public class ExternalDnDJTree extends DnDJTree {

	private static final Logger logger = Logger.getLogger(ExternalDnDJTree.class.getPackage().getName());

	private Map<JComponent, DropTarget> dropTargetMap = new HashMap<>();
	private DragGestureListener dgListener;

	public ExternalDnDJTree(JFIBBrowserWidget<?> widget) {
		super(widget);

	}

	/**
	 * Instantiate when not instantiated yet, and activate a {@link DropTarget} for supplied dropContainer, given a
	 * {@link DropTargetListener} and a {@link DragGestureListener}
	 * 
	 * @param dropContainer
	 * @param dropTargetListener
	 * @param dgListener
	 * @return the {@link DropTarget} already existing or newly created
	 */
	public DropTarget activateDropTargetListener(JComponent dropContainer, DropTargetListener dropTargetListener,
			DragGestureListener dgListener) {
		System.out.println("activateDropTargetListener() for " + dropContainer);

		if (!getWidget().getComponent().getAllowsExternalDragAndDrop()) {
			logger.warning("External drag and drop not supported");
			return null;
		}

		DropTarget dropTarget = dropTargetMap.get(dropContainer);

		if (dropTarget != null) {
			dropTarget.setActive(true);
		}
		else {

			this.dgListener = dgListener;
			dragSource = DragSource.getDefaultDragSource();

			DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this, // DragSource
					DnDConstants.ACTION_MOVE, // specifies valid actions
					this // DragGestureListener
			);

			dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);

			dropTarget = new DropTarget(dropContainer, dropTargetListener);
			dropTargetMap.put(dropContainer, dropTarget);
		}
		return dropTarget;
	}

	/**
	 * Disactivate eventually existing {@link DropTarget} for supplied dropContainer<br>
	 * When not existing, do nothing
	 * 
	 * @param dropContainer
	 */
	public void disactivateDropTargetListener(JComponent dropContainer) {
		System.out.println("disactivateDropTargetListener() for " + dropContainer);
		DropTarget dropTarget = dropTargetMap.get(dropContainer);
		if (dropTarget != null) {
			dropTarget.setActive(false);
		}
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		dgListener.dragGestureRecognized(dge);
	}

}
