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

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.logging.FlexoLogger;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public abstract class PlaceHolder {

	static final Logger logger = FlexoLogger.getLogger(PlaceHolder.class.getPackage().getName());

	private final FIBSwingEditableContainerView<?, ?> view;
	private final String text;

	private final Rectangle bounds;

	public PlaceHolder(FIBSwingEditableContainerView<?, ?> view, String text, Rectangle bounds) {
		this.view = view;
		this.text = text;
		JLabel label = new JLabel(text);
		label.setForeground(Color.DARK_GRAY);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		this.bounds = new Rectangle(bounds);
		visible = false;
	}

	public PlaceHolder(FIBSwingEditableContainerView<?, ?> view, String text) {
		this(view, text, new Rectangle(200, 200));
	}

	public Rectangle getBounds() {
		return bounds;
	}

	private boolean visible;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public FIBSwingEditableContainerView<?, ?> getView() {
		return view;
	}

	@Override
	public String toString() {
		return "PlaceHolder:[" + text + "]";
	}

	/**
	 * Insert supplied component in placeholder<br>
	 * If this is a move, originalIndex stores the index of component when move was initialized
	 * 
	 * @param newComponent
	 * @param originalIndex
	 */
	public abstract void insertComponent(FIBComponent newComponent, int originalIndex);

	public void willDelete() {
	}

	public void hasDeleted() {
	}

	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		float alpha = isVisible() ? 0.2f : 0.1f;
		int type = AlphaComposite.SRC_OVER;
		AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
		g2.setComposite(composite);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


		g2.setColor(isVisible() ? Color.GREEN : Color.YELLOW);
		g2.fillRoundRect(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2, 15, 15);
		g2.setStroke(new BasicStroke(2.0f));
		g2.setColor(Color.GRAY);
		g2.drawRoundRect(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2, 15, 15);

	}

}
