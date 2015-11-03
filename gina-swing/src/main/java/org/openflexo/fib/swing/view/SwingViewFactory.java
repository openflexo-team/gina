/**
 * 
 * Copyright (c) 2013-2015, Openflexo
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

package org.openflexo.fib.swing.view;

import javax.swing.JComponent;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBCheckboxList;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBEditor;
import org.openflexo.fib.model.FIBFile;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBImage;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.model.FIBReferencedComponent;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.swing.view.widget.JFIBImageWidget;
import org.openflexo.fib.swing.view.widget.JFIBLabelWidget;
import org.openflexo.fib.swing.view.widget.JFIBNumberWidget;
import org.openflexo.fib.swing.view.widget.JFIBTextAreaWidget;
import org.openflexo.fib.swing.view.widget.JFIBTextFieldWidget;
import org.openflexo.fib.view.GinaViewFactory;
import org.openflexo.fib.view.impl.GinaViewFactoryImpl;
import org.openflexo.fib.view.widget.FIBLabelWidget;
import org.openflexo.fib.view.widget.FIBTextFieldWidget;
import org.openflexo.fib.view.widget.impl.FIBBrowserWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBButtonWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBCheckBoxWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBCheckboxListWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBColorWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBCustomWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBDropDownWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBEditorWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBFileWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBFontWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBHtmlEditorWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBImageWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBListWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBNumberWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBRadioButtonListWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBReferencedComponentWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBTableWidgetImpl;
import org.openflexo.fib.view.widget.impl.FIBTextAreaWidgetImpl;

/**
 * A {@link GinaViewFactory} implementation dedicated for Swing
 * 
 * @author sylvain
 * 
 */
public class SwingViewFactory extends GinaViewFactoryImpl<JComponent> {

	public SwingViewFactory() {
	}

	@Override
	public FIBLabelWidget<? extends JComponent> makeLabel(FIBLabel widget, FIBController controller) {
		return new JFIBLabelWidget(widget, controller);
	}

	@Override
	public FIBTextFieldWidget<? extends JComponent> makeTextField(FIBTextField widget, FIBController controller) {
		return new JFIBTextFieldWidget(widget, controller);
	}

	@Override
	public FIBTextAreaWidgetImpl<? extends JComponent> makeTextArea(FIBTextArea widget, FIBController controller) {
		return new JFIBTextAreaWidget(widget, controller);
	}

	@Override
	public FIBImageWidgetImpl<? extends JComponent> makeImage(FIBImage widget, FIBController controller) {
		return new JFIBImageWidget(widget, controller);
	}

	@Override
	public FIBNumberWidgetImpl<? extends JComponent, ?> makeNumber(FIBNumber widget, FIBController controller) {
		return new JFIBNumberWidget(widget, controller);
	}

	@Override
	public FIBCheckBoxWidgetImpl<? extends JComponent> makeCheckbox(FIBCheckBox widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBCheckboxListWidgetImpl<? extends JComponent, ?> makeCheckboxList(FIBCheckboxList widget,
			FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBRadioButtonListWidgetImpl<? extends JComponent, ?> makeRadioButtonList(FIBRadioButtonList widget,
			FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBDropDownWidgetImpl<? extends JComponent, ?> makeDropDown(FIBDropDown widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBListWidgetImpl<? extends JComponent, ?> makeList(FIBList widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBEditorWidgetImpl<? extends JComponent> makeEditor(FIBEditor widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBHtmlEditorWidgetImpl<? extends JComponent> makeHtmlEditor(FIBHtmlEditor widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBTableWidgetImpl<? extends JComponent, ?> makeTable(FIBTable widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBBrowserWidgetImpl<? extends JComponent, ?> makeBrowser(FIBBrowser widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBColorWidgetImpl<? extends JComponent> makeColor(FIBColor widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBFontWidgetImpl<? extends JComponent> makeFont(FIBFont widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBFileWidgetImpl<? extends JComponent> makeFile(FIBFile widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBButtonWidgetImpl<? extends JComponent> makeButton(FIBButton widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBCustomWidgetImpl<? extends JComponent, ?> makeCustomWidget(FIBCustom widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FIBReferencedComponentWidgetImpl<? extends JComponent> makeReferencedComponentWidget(
			FIBReferencedComponent widget, FIBController controller) {
		// TODO Auto-generated method stub
		return null;
	}

}
