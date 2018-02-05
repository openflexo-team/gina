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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.openflexo.gina.model.FIBOperator;
import org.openflexo.gina.swing.editor.controller.FIBEditorIconLibrary;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.view.FIBOperatorView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.logging.FlexoLogger;

public class OperatorDecorator {

	static final Logger logger = FlexoLogger.getLogger(OperatorDecorator.class.getPackage().getName());

	private final FIBSwingEditableContainerView<?, ?> containerView;
	private final FIBOperatorView<?, ?, ?> operatorView;
	private final List<FIBView<?, ?>> subViews;
	private final FIBOperator operator;

	public OperatorDecorator(FIBSwingEditableContainerView<?, ?> containerView, FIBOperatorView<?, ?, ?> operatorView,
			List<FIBView<?, ?>> subViews) {
		this.containerView = containerView;
		this.operatorView = operatorView;
		this.operator = operatorView.getComponent();
		JLabel label = new JLabel(operator.getName());
		label.setForeground(Color.DARK_GRAY);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		this.subViews = subViews;

		if (subViews == null || subViews.size() == 0) {
			System.out.println("Pas normal ca");
			Thread.dumpStack();
		}
	}

	public List<FIBView<?, ?>> getSubViews() {
		return subViews;
	}

	public Rectangle getBounds() {
		Rectangle bounds = null;
		for (FIBView<?, ?> jfibView : getSubViews()) {
			Rectangle contentsBounds = ((JFIBView<?, ?>) jfibView).getResultingJComponent().getBounds();
			if (bounds == null) {
				bounds = contentsBounds;
			}
			else {
				bounds = bounds.union(contentsBounds);
			}
		}
		if (bounds != null) {
			return new Rectangle(bounds.x - 4, bounds.y - 4, bounds.width + 8, bounds.height + 8);
		}
		return null;
	}

	public FIBOperator getOperator() {
		return operator;
	}

	public FIBSwingEditableContainerView<?, ?> getContainerView() {
		return containerView;
	}

	public FIBOperatorView<?, ?, ?> getOperatorView() {
		return operatorView;
	}

	@Override
	public String toString() {
		return "OperatorDecorator:[" + getOperator() + "]";
	}

	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		Rectangle bounds = getBounds();
		if (bounds == null) {
			return;
		}

		g2.drawImage(FIBEditorIconLibrary.ITERATION_ICON.getImage(), bounds.x + 4, bounds.y + 4 /*bounds.height / 2 - 8*/, null);

		float alpha = 0.1f;
		int type = AlphaComposite.SRC_OVER;
		AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
		g2.setComposite(composite);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(Color.YELLOW);
		g2.fillRect(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);
		g2.setStroke(new BasicStroke(1.0f));
		g2.setColor(Color.BLACK);
		g2.drawRect(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);

	}

}
