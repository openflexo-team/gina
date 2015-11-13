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

package org.openflexo.gina.swing.view.widget;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBValueEventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBCheckBoxWidgetImpl;

/**
 * Swing implementation for a widget able to edit a boolean (or Boolean) object
 * (a JCheckBox)
 * 
 * @author sylvain
 */
public class JFIBCheckBoxWidget extends FIBCheckBoxWidgetImpl<JCheckBox> implements FocusListener,
		JFIBView<FIBCheckBox, JCheckBox> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JFIBCheckBoxWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JCheckBox<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingCheckBoxRenderingAdapter extends SwingRenderingAdapter<JCheckBox> implements
			CheckBoxRenderingAdapter<JCheckBox> {

		@Override
		public boolean getSelected(JCheckBox component) {
			return component.isSelected();
		}

		@Override
		public void setSelected(JCheckBox component, boolean selected) {
			component.setSelected(selected);
		}

	}

	public static SwingCheckBoxRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingCheckBoxRenderingAdapter();

	/**
	 * @param model
	 */
	public JFIBCheckBoxWidget(FIBCheckBox model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	@Override
	public SwingCheckBoxRenderingAdapter getRenderingAdapter() {
		return (SwingCheckBoxRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	@Override
	protected JCheckBox makeTechnologyComponent() {
		JCheckBox checkbox = new JCheckBox();
		checkbox.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER,
				BOTTOM_COMPENSATING_BORDER, RIGHT_COMPENSATING_BORDER));
		checkbox.setOpaque(false);
		checkbox.setBorderPaintedFlat(true);
		checkbox.setSelected(getWidget().getSelected());
		if (isReadOnly()) {
			checkbox.setEnabled(false);
		} else {
			checkbox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GinaStackEvent stack = JFIBCheckBoxWidget.this.GENotifier.raise(FIBEventFactory.getInstance()
							.createValueEvent(
									getValue() ? FIBValueEventDescription.UNCHECKED : FIBValueEventDescription.CHECKED,
									getTechnologyComponent().isSelected()));

					updateModelFromWidget();

					stack.end();
				}
			});
		}
		checkbox.addFocusListener(this);
		return checkbox;
	}

}
