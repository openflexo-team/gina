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

import org.openflexo.connie.expr.ExpressionEvaluator;
import org.openflexo.connie.java.expr.JavaExpressionEvaluator;
import org.openflexo.gina.utils.GinaMouseDiagnostics;

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

	private DragGestureListener dgListener;

	public ExternalDnDJTree(JFIBBrowserWidget<?> widget) {
		super(widget);

	}

	@Override
	public ExpressionEvaluator getEvaluator() {
		return new JavaExpressionEvaluator(this);
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
		dgListener.dragGestureRecognized(dge);
	}

}
