package org.openflexo.gina.swing.view.widget;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTargetListener;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.BrowserCell;
import org.openflexo.toolbox.ToolBox;

/**
 * Swing implementation of a {@link JTree} supporting drag&drop<br>
 * It suppose that some elements of the tree may be dragged somewhere
 * 
 * @see DropTargetListener
 * 
 * @author sylvain
 */
@SuppressWarnings("serial")
public abstract class DnDJTree extends JTree implements DragGestureListener, TreeSelectionListener, BindingEvaluationContext {

	/** Variables needed for DnD */
	protected DragSource dragSource = null;

	/** Stores the selected node info */
	protected TreePath selectedTreePath = null;
	private BrowserCell selectedBrowserCell = null;

	/** The browser cell beeing currently dragged */
	protected BrowserCell draggedBrowserCell;

	/** The browser cell destination of the drop: the one on we target */
	protected BrowserCell targetBrowserCell;

	private static final Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static final Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static Cursor dropOK = ToolBox.isMacOS()
			? Toolkit.getDefaultToolkit().createCustomCursor(DROP_OK_IMAGE, new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;
	public static Cursor dropKO = ToolBox.isMacOS()
			? Toolkit.getDefaultToolkit().createCustomCursor(DROP_KO_IMAGE, new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

	private JFIBBrowserWidget<?> widget;

	public DnDJTree(JFIBBrowserWidget<?> widget) {
		super(widget.getBrowserModel());
		this.widget = widget;

		addTreeSelectionListener(this);

	}

	/** Returns The selected node */
	public BrowserCell getSelectedBrowserCell() {
		return selectedBrowserCell;
	}

	/** TreeSelectionListener - sets selected node */
	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		selectedTreePath = evt.getNewLeadSelectionPath();
		if (selectedTreePath == null) {
			selectedBrowserCell = null;
			return;
		}
		Object lastPathComponent = selectedTreePath.getLastPathComponent();
		if (lastPathComponent instanceof BrowserCell) {
			selectedBrowserCell = (BrowserCell) lastPathComponent;
		}
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("dragged")) {
			// System.out.println("dragged is" + draggedBrowserCell.getRepresentedObject());
			return draggedBrowserCell.getRepresentedObject();
		}
		else if (variable.getVariableName().equals("target")) {
			// System.out.println(" target is " + draggedBrowserCell.getRepresentedObject());
			return targetBrowserCell.getRepresentedObject();
		}
		else {
			return widget.getBindingEvaluationContext().getValue(variable);
		}
	}

	public DragSource getDragSource() {
		return dragSource;
	}

	public JFIBBrowserWidget<?> getWidget() {
		return widget;
	}
}
