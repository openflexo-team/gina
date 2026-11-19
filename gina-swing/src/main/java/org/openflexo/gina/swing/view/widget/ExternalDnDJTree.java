package org.openflexo.gina.swing.view.widget;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;
import java.util.logging.Logger;

import javax.swing.JTree;

import org.openflexo.gina.utils.GinaMouseDiagnostics;

/**
 * Swing implementation of a {@link JTree} supporting external drag&drop<br>
 * It suppose that some elements of the tree may be dragged somewhere.<br>
 * It extends {@link InternalDnDJTree}: the drops between elements of the tree (the {@code DragOperation}s of the browser) keep working.
 * While an external {@link DragGestureListener} is registered, it receives the drag gestures; otherwise the gestures are handled by the
 * internal drag and drop (when the browser allows it)
 * 
 * @see DropTargetListener
 * 
 * @author sylvain
 */
@SuppressWarnings("serial")
public class ExternalDnDJTree extends InternalDnDJTree {

	private static final Logger logger = Logger.getLogger(ExternalDnDJTree.class.getPackage().getName());

	private DragGestureListener dgListener;

	public ExternalDnDJTree(JFIBBrowserWidget<?> widget) {
		super(widget);

	}

	/**
	 * Register {@link DragGestureListener} for this JTree
	 * 
	 * @param dgListener
	 */
	public void registerDragGestureListener(DragGestureListener dgListener) {

		if (!getWidget().getComponent().getAllowsExternalDragAndDrop()) {
			logger.warning("External drag and drop not supported");
			return;
		}

		this.dgListener = dgListener;

		if (dragSource != null) {
			// The internal drag and drop already listens to the drag gestures of this tree (and routes them here): a second recognizer
			// would start every drag twice
			return;
		}

		dragSource = DragSource.getDefaultDragSource();

		DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this, // DragSource
				DnDConstants.ACTION_MOVE, // specifies valid actions
				this // DragGestureListener
		);

		dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);

		if (GinaMouseDiagnostics.MOUSE_DEBUG) {
			// createDefaultDragGestureRecognizer registers its own MouseListener/MouseMotionListener
			// directly on this JTree (see java.awt.dnd.DragGestureRecognizer.registerListeners()),
			// on top of the click/right-click dispatcher installed by SwingViewFactory. Both sets
			// of listeners see every raw mouse event on this component; sourceActions excludes
			// BUTTON3 from actually starting a drag, but logging registration + every recognized
			// gesture confirms this empirically instead of trusting the mask alone.
			GinaMouseDiagnostics.logDragGesture(this, "registered",
					"sourceActions=" + dgr.getSourceActions() + " (BUTTON3_MASK excluded=" + ((dgr.getSourceActions()
							& InputEvent.BUTTON3_MASK) == 0) + ")");
		}
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		if (GinaMouseDiagnostics.MOUSE_DEBUG) {
			GinaMouseDiagnostics.logDragGesture(this, "recognized",
					"triggerButton=" + (dge.getTriggerEvent() instanceof java.awt.event.MouseEvent
							? ((java.awt.event.MouseEvent) dge.getTriggerEvent()).getButton() : "?") + " dragAction=" + dge
									.getDragAction() + " origin=" + dge.getDragOrigin());
		}
		if (dgListener != null) {
			dgListener.dragGestureRecognized(dge);
		}
		else if (dragSource != null && getWidget().getComponent().getAllowsDragAndDrop()) {
			// No external listener (any more): drag between elements of this tree
			super.dragGestureRecognized(dge);
		}
	}

	/**
	 * Unregister the external {@link DragGestureListener}: the drag gestures are then handled by the internal drag and drop
	 */
	public void unregisterDragGestureListener() {
		this.dgListener = null;
	}

}
