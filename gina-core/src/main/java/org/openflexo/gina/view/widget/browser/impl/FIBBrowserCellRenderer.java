/**
 * 
 */
/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.gina.view.widget.browser.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.BrowserCell;
import org.openflexo.gina.view.widget.impl.FIBBrowserWidgetImpl;

@SuppressWarnings("serial")
public class FIBBrowserCellRenderer<T> extends DefaultTreeCellRenderer {

	private final FIBBrowserWidgetImpl<?, T> widget;

	public FIBBrowserCellRenderer(FIBBrowserWidgetImpl<?, T> widget) {
		super();
		this.widget = widget;
		if (widget.getWidget().getTextSelectionColor() != null) {
			setTextSelectionColor(widget.getWidget().getTextSelectionColor());
		}
		if (widget.getWidget().getTextNonSelectionColor() != null) {
			setTextNonSelectionColor(widget.getWidget().getTextNonSelectionColor());
		}
		if (widget.getWidget().getBackgroundSelectionColor() != null) {
			setBackgroundSelectionColor(widget.getWidget().getBackgroundSelectionColor());
		}
		if (widget.getWidget().getBackgroundNonSelectionColor() != null) {
			setBackgroundNonSelectionColor(widget.getWidget().getBackgroundNonSelectionColor());
		}
		if (widget.getWidget().getBorderSelectionColor() != null) {
			setBorderSelectionColor(widget.getWidget().getBorderSelectionColor());
		}
		if (widget.getFont() != null) {
			setFont(widget.getFont());
		}
	}

	/**
	 * 
	 * Returns the cell renderer.
	 * 
	 * @param table
	 *            the <code>JTable</code>
	 * @param value
	 *            the value to assign to the cell at <code>[row, column]</code>
	 * @param isSelected
	 *            true if cell is selected
	 * @param hasFocus
	 *            true if cell has focus
	 * @param row
	 *            the row of the cell to render
	 * @param column
	 *            the column of the cell to render
	 * @return the default table cell renderer
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (widget.getWidget() != null && value instanceof BrowserCell) {
			Object representedObject = ((BrowserCell) value).getRepresentedObject();

			if (sel) {
				if (widget.isLastFocusedSelectable()) {
					if (widget.getWidget().getTextSelectionColor() != null) {
						setTextSelectionColor(widget.getWidget().getTextSelectionColor());
					}
					if (widget.getWidget().getBackgroundSelectionColor() != null) {
						setBackgroundSelectionColor(widget.getWidget().getBackgroundSelectionColor());
					}
				}
				else {
					if (widget.getWidget().getTextNonSelectionColor() != null) {
						setTextSelectionColor(widget.getWidget().getTextNonSelectionColor());
					}
					if (widget.getWidget().getBackgroundSecondarySelectionColor() != null) {
						setBackgroundSelectionColor(widget.getWidget().getBackgroundSecondarySelectionColor());
					}
				}
			}

			JLabel returned = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (widget.isEnabled()) {
				if (isEnabled(representedObject)) {
					if (widget.getWidget() != null && widget.getWidget().getTextNonSelectionColor() != null) {
						setTextNonSelectionColor(widget.getWidget().getTextNonSelectionColor());
					}
				}
				else {
					setEnabled(false);
				}
			}

			Font font = getFont(representedObject);
			if (font != null) {
				// System.out.println("on met la fonte a " + font);
				returned.setFont(font);
			}
			if (sel) {
				Color color = getSelectedColor(representedObject);
				if (color != null) {
					returned.setForeground(color);
				}
			}
			else {
				Color color = getNonSelectedColor(representedObject);
				if (color != null) {
					returned.setForeground(color);
				}
			}

			returned.setText(getLabel(representedObject));
			returned.setIcon(getIcon(representedObject));
			returned.setToolTipText(getTooltip(representedObject));
			return returned;

		}

		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

	}

	/*  protected Color getCellBackground(JTree tree, boolean isSelected, boolean hasFocus)
	{
	  	if (isSelected) {
	  		if (widget.isLastFocusedSelectable()) return MAIN_SELECTION_COLOR;
	  		else return SECONDARY_SELECTION_COLOR;
	  	}
	  	return tree.getBackground();
	  }*/

	private FIBBrowserElementType getElementType(Object object) {
		if (widget != null && widget.getBrowserModel() != null && object != null) {
			return widget.getBrowserModel().elementTypeForObject(object);
		}
		return null;
	}

	protected String getLabel(Object object) {
		if (object == null) {
			return null;
		}
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			return elementType.getLabelFor(object);
		}
		return object.toString();
	}

	protected Icon getIcon(Object object) {
		if (object == null) {
			return null;
		}
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			return elementType.getIconFor(object);
		}
		return null;
	}

	protected String getTooltip(Object object) {
		if (object == null) {
			return null;
		}
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			return elementType.getTooltipFor(object);
		}
		return object.toString();
	}

	protected boolean isEnabled(Object object) {
		if (object == null) {
			return false;
		}
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			// System.out.println("Object "+object+" isEnabled="+elementType.isEnabled(object));
			return elementType.isEnabled(object);
		}
		return true;
	}

	protected Font getFont(Object object) {
		if (object == null) {
			return null;
		}
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			return elementType.getFont(object);
		}
		return widget.getFont();
	}

	protected Color getSelectedColor(Object object) {
		if (object == null) {
			return null;
		}
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			return elementType.getSelectedColor(object);
		}
		return null;
	}

	protected Color getNonSelectedColor(Object object) {
		if (object == null) {
			return null;
		}
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			return elementType.getNonSelectedColor(object);
		}
		return null;
	}

	/**
	 * Overrides updateUI
	 * 
	 * @see javax.swing.JLabel#updateUI()
	 */
	/* @Override
	 public void updateUI()
	 {
	     super.updateUI();
	     // Fix for TreeCellRenderer
	     setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
	     setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
	     setOpenIcon(UIManager.getIcon("Tree.openIcon"));
	
	     setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
	     setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
	     setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
	     setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
	     setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
	 }*/

}
