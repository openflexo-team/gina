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

	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		dgListener.dragGestureRecognized(dge);
	}

}
