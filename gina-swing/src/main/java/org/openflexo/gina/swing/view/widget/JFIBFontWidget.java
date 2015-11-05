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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.model.widget.FIBFont;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBFontWidget.FontSelectorPanel;
import org.openflexo.gina.view.widget.impl.FIBFontWidgetImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FontSelector;

/**
 * Swing implementation for a widget able to select a Font
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public class JFIBFontWidget extends FIBFontWidgetImpl<FontSelectorPanel>implements FocusListener {

	private static final Logger LOGGER = Logger.getLogger(JFIBFontWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing Font Widget<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingFontWidgetRenderingAdapter extends SwingRenderingAdapter<FontSelectorPanel>
			implements FontWidgetRenderingAdapter<FontSelectorPanel> {

		@Override
		public Font getSelectedFont(FontSelectorPanel component) {
			return component.selector.getEditedObject();
		}

		@Override
		public void setSelectedFont(FontSelectorPanel component, Font aFont) {
			component.selector.setEditedObject(aFont);
		}

		@Override
		public boolean isCheckboxVisible(FontSelectorPanel component) {
			return component.checkBox.isVisible();
		}

		@Override
		public void setCheckboxVisible(FontSelectorPanel component, boolean visible) {
			component.checkBox.setVisible(visible);
		}

		@Override
		public boolean isCheckboxSelected(FontSelectorPanel component) {
			return component.checkBox.isSelected();
		}

		@Override
		public void setCheckboxSelected(FontSelectorPanel component, boolean selected) {
			component.checkBox.setSelected(selected);
		}

		@Override
		public boolean isCheckboxEnabled(FontSelectorPanel component) {
			return component.checkBox.isEnabled();
		}

		@Override
		public void setCheckboxEnabled(FontSelectorPanel component, boolean enabled) {
			component.checkBox.setEnabled(enabled);
		}

	}

	public static SwingFontWidgetRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingFontWidgetRenderingAdapter();

	public static class FontSelectorPanel extends JPanel {
		private final JCheckBox checkBox;
		protected FontSelector selector;
		private final JFIBFontWidget widget;

		public FontSelectorPanel(JFIBFontWidget widget) {
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
					FontSelectorPanel.this.widget.updateModelFromWidget();
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
			checkBox.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "undefined_value", checkBox));
			checkBox.setToolTipText(FlexoLocalization.localizedTooltipForKey(FIBModelObjectImpl.LOCALIZATION, "undefined_value", checkBox));
		}

	}

	public JFIBFontWidget(FIBFont model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	@Override
	protected FontSelectorPanel makeTechnologyComponent() {
		return new FontSelectorPanel(this);
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		getTechnologyComponent().updateLanguage();
	}

	@Override
	public JComponent getJComponent() {
		return getTechnologyComponent();
	}

}
