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

import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBHtmlEditorOption;
import org.openflexo.fib.swing.view.SwingRenderingAdapter;
import org.openflexo.fib.view.widget.impl.FIBHtmlEditorWidgetImpl;

import com.metaphaseeditor.MetaphaseEditorConfiguration;
import com.metaphaseeditor.MetaphaseEditorPanel;

/**
 * Represents a widget able to edit an HTML fragment
 * 
 * @author sylvain
 */
public class JFIBHtmlEditorWidget extends FIBHtmlEditorWidgetImpl<MetaphaseEditorPanel> {

	private static final Logger LOGGER = Logger.getLogger(JFIBHtmlEditorWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JTextField<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingHtmlEditorWidgetRenderingAdapter extends SwingRenderingAdapter<MetaphaseEditorPanel>
			implements HtmlEditorWidgetRenderingAdapter<MetaphaseEditorPanel> {

		@Override
		public String getText(MetaphaseEditorPanel component) {
			return component.getDocument();
		}

		@Override
		public void setText(MetaphaseEditorPanel component, String aText) {
			component.setDocument(aText);
		}

		@Override
		public boolean isEditable(MetaphaseEditorPanel component) {
			return true;
		}

		@Override
		public void setEditable(MetaphaseEditorPanel component, boolean editable) {
		}

		@Override
		public int getCaretPosition(MetaphaseEditorPanel component) {
			return component.getHtmlTextPane().getCaretPosition();
		}

		@Override
		public void setCaretPosition(MetaphaseEditorPanel component, int caretPosition) {
			component.getHtmlTextPane().setCaretPosition(caretPosition);
		}

	}

	public static SwingHtmlEditorWidgetRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingHtmlEditorWidgetRenderingAdapter();

	public JFIBHtmlEditorWidget(FIBHtmlEditor model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	@Override
	protected MetaphaseEditorPanel makeTechnologyComponent() {
		return new MetaphaseEditorPanel(buildConfiguration()) {
			@Override
			public void documentWasEdited() {
				super.documentWasEdited();
				updateModelFromWidget();
			}
		};

	}

	@Override
	protected void updateHtmlEditorConfiguration() {
		getTechnologyComponent().updateComponents(buildConfiguration());
	}

	private MetaphaseEditorConfiguration buildConfiguration() {
		MetaphaseEditorConfiguration returned = new MetaphaseEditorConfiguration();
		for (FIBHtmlEditorOption o : getComponent().getOptionsInLine1()) {
			returned.addToOptions(o.makeMetaphaseEditorOption(1));
		}
		for (FIBHtmlEditorOption o : getComponent().getOptionsInLine2()) {
			returned.addToOptions(o.makeMetaphaseEditorOption(2));
		}
		for (FIBHtmlEditorOption o : getComponent().getOptionsInLine3()) {
			returned.addToOptions(o.makeMetaphaseEditorOption(3));
		}
		return returned;
	}

	@Override
	public MetaphaseEditorPanel getJComponent() {
		return getTechnologyComponent();
	}

}
