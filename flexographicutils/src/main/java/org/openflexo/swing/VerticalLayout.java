/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexographicutils, a component of the software infrastructure 
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

/* Copyright (c) 1997 by Groupe Bull.  All Rights Reserved */
/* $Id: VerticalLayout.java,v 1.2 2011/09/12 11:47:24 gpolet Exp $ */
/* Author: Jean-Michel.Leon@sophia.inria.fr */

package org.openflexo.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * A simple layout that arranges its components vertically all the components are given the same width and keep their own height.
 * 
 */

public class VerticalLayout implements LayoutManager {

	final static private int VGAP = 0;

	final static private int HMARGIN = 0;

	final static private int VMARGIN = 0;

	protected int vgap;

	protected int hmargin;

	protected int vmargin;

	/**
	 * Constructs a new VerticalLayout.
	 */
	public VerticalLayout() {
		this(VGAP, HMARGIN, VMARGIN);
	}

	/**
	 * Constructs a new VerticalLayout with specific gap and margin.
	 * 
	 * @param gap
	 * @param h
	 * @param v
	 */
	public VerticalLayout(int gap, int h, int v) {
		vgap = gap;
		hmargin = h;
		vmargin = v;
	}

	/**
	 * Adds the specified named component to the layout.
	 * 
	 * @param name
	 *            the String name
	 * @param comp
	 *            the component to be added
	 */
	@Override
	public void addLayoutComponent(String name, Component comp) {
		// interface method
	}

	/**
	 * Removes the specified component from the layout.
	 * 
	 * @param comp
	 *            the component to be removed
	 */
	@Override
	public void removeLayoutComponent(Component comp) {
		// interface method
	}

	@Override
	public Dimension minimumLayoutSize(Container target) {
		return preferredLayoutSize(target);
	}

	@Override
	public Dimension preferredLayoutSize(Container target) {
		Dimension dim = new Dimension(0, 0);
		int width = 0;
		int height = 0;
		Component children[] = target.getComponents();
		int length = 0;
		for (int i = 0; i < children.length; i++) {
			if (!children[i].isVisible()) {
				continue;
			}
			Dimension d = children[i].getPreferredSize();
			width = Math.max(width, d.width);
			height += d.height;
			length++;
		}
		height += vgap * (length + 1) + vmargin * 2 * length;
		Insets insets = target.getInsets();
		dim.width = width + insets.left + insets.right + hmargin * 2;
		dim.height = height + insets.top + insets.bottom;
		return dim;
	}

	/**
	 * Lays out the specified container.
	 * 
	 * @param target
	 *            the component being laid out
	 * @see Container
	 */
	@Override
	public void layoutContainer(Container target) {
		Insets insets = target.getInsets();
		int top = insets.top, left = insets.left + hmargin;
		int width = target.getSize().width - left - insets.right - hmargin;
		Component children[] = target.getComponents();
		// available vertical space
		// Unused int vroom = target.getSize().height - top - insets.bottom - vmargin * 2;

		top += vgap;
		for (int i = 0; i < children.length; i++) {
			if (!children[i].isVisible()) {
				continue;
			}
			int h = children[i].getPreferredSize().height + vmargin * 2;
			children[i].setBounds(left, top, width, h);
			top += h + vgap;
			// Unused vroom -= h + vgap;
		}
	}

	/**
	 * Returns the String representation of this BorderLayout's values.
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return getClass().getName() + ",vgap=" + vgap + "]";
	}

}
