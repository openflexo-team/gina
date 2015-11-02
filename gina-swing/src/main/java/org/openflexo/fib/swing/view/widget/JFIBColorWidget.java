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

package org.openflexo.fib.swing.view.widget;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.fib.swing.view.SwingRenderingTechnologyAdapter;
import org.openflexo.fib.swing.view.widget.JFIBColorWidget.ColorSelectorPanel;
import org.openflexo.fib.view.widget.impl.FIBColorWidgetImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.ColorSelector;

/**
 * Swing implementation for a widget able to select a Color
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public class JFIBColorWidget extends FIBColorWidgetImpl<ColorSelectorPanel>implements FocusListener {

	private static final Logger logger = Logger.getLogger(JFIBColorWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingTechnologyAdapter} implementation dedicated for Swing Color Widget<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingColorWidgetRenderingTechnologyAdapter extends SwingRenderingTechnologyAdapter<ColorSelectorPanel>
			implements ColorWidgetRenderingTechnologyAdapter<ColorSelectorPanel> {

		@Override
		public Color getSelectedColor(ColorSelectorPanel component) {
			return component.selector.getEditedObject();
		}

		@Override
		public void setSelectedColor(ColorSelectorPanel component, Color aColor) {
			component.selector.setEditedObject(aColor);
		}

		@Override
		public boolean isCheckboxVisible(ColorSelectorPanel component) {
			return component.checkBox.isVisible();
		}

		@Override
		public void setCheckboxVisible(ColorSelectorPanel component, boolean visible) {
			component.checkBox.setVisible(visible);
		}

		@Override
		public boolean isCheckboxSelected(ColorSelectorPanel component) {
			return component.checkBox.isSelected();
		}

		@Override
		public void setCheckboxSelected(ColorSelectorPanel component, boolean selected) {
			component.checkBox.setSelected(selected);
		}

		@Override
		public boolean isCheckboxEnabled(ColorSelectorPanel component) {
			return component.checkBox.isEnabled();
		}

		@Override
		public void setCheckboxEnabled(ColorSelectorPanel component, boolean enabled) {
			component.checkBox.setEnabled(enabled);
		}

	}

	public static SwingColorWidgetRenderingTechnologyAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingColorWidgetRenderingTechnologyAdapter();

	public static class ColorSelectorPanel extends JPanel {
		private final JCheckBox checkBox;
		protected ColorSelector selector;
		private final JFIBColorWidget widget;

		public ColorSelectorPanel(JFIBColorWidget widget) {
			super(new GridBagLayout());
			this.widget = widget;
			selector.addApplyCancelListener(widget);
			selector.addFocusListener(widget);
			selector.setEnabled(false);
			checkBox = new JCheckBox();
			checkBox.setHorizontalTextPosition(JCheckBox.LEADING);

			checkBox.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "transparent", checkBox));
			checkBox.setToolTipText(FlexoLocalization.localizedTooltipForKey(FIBModelObjectImpl.LOCALIZATION, "undefined_value", checkBox));

			checkBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					selector.setEnabled(checkBox.isSelected());
					ColorSelectorPanel.this.widget.updateModelFromWidget();
				}
			});
			setOpaque(false);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.anchor = GridBagConstraints.LINE_START;
			add(checkBox, gbc);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			add(selector, gbc);
		}

		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			selector.getDownButton().setEnabled(enabled);
		}

		public void updateLanguage() {
			checkBox.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "transparent", checkBox));
			checkBox.setToolTipText(FlexoLocalization.localizedTooltipForKey(FIBModelObjectImpl.LOCALIZATION, "undefined_value", checkBox));
		}

	}

	public JFIBColorWidget(FIBColor model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	@Override
	protected ColorSelectorPanel makeTechnologyComponent() {
		return new ColorSelectorPanel(this);
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		getDynamicJComponent().updateLanguage();
	}

	@Override
	public JComponent getJComponent() {
		return getDynamicJComponent();
	}

}
