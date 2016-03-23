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
package org.openflexo.gina.swing.editor.controller;

import javax.swing.JComponent;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBColor;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.model.widget.FIBEditor;
import org.openflexo.gina.model.widget.FIBFile;
import org.openflexo.gina.model.widget.FIBFont;
import org.openflexo.gina.model.widget.FIBHtmlEditor;
import org.openflexo.gina.model.widget.FIBImage;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBList;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBRadioButtonList;
import org.openflexo.gina.model.widget.FIBReferencedComponent;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.swing.editor.view.container.JFIBEditablePanelView;
import org.openflexo.gina.swing.editor.view.container.JFIBEditableSplitPanelView;
import org.openflexo.gina.swing.editor.view.container.JFIBEditableTabPanelView;
import org.openflexo.gina.swing.editor.view.container.JFIBEditableTabView;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableBrowserWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableButtonWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableCheckboxListWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableCheckboxWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableColorWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableCustomWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableDropDownWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableEditorWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableFileWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableFontWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableHtmlEditorWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableImageWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableLabelWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableListWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableNumberWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableRadioButtonListWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableReferencedComponentWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableTableWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableTextAreaWidget;
import org.openflexo.gina.swing.editor.view.widget.JFIBEditableTextFieldWidget;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.container.FIBPanelView;
import org.openflexo.gina.view.container.FIBSplitPanelView;
import org.openflexo.gina.view.container.FIBTabPanelView;
import org.openflexo.gina.view.container.FIBTabView;
import org.openflexo.gina.view.widget.FIBLabelWidget;
import org.openflexo.gina.view.widget.FIBTextFieldWidget;
import org.openflexo.gina.view.widget.impl.FIBBrowserWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBButtonWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBCheckBoxWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBCheckboxListWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBColorWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBCustomWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBDropDownWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBEditorWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBFileWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBFontWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBHtmlEditorWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBImageWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBListWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBNumberWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBRadioButtonListWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBReferencedComponentWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBTableWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBTextAreaWidgetImpl;

/**
 * Extends {@link SwingViewFactory} by providing editable views in the context of Swing editor
 * 
 * @author sylvain
 *
 */
public class SwingEditorViewFactory extends SwingViewFactory {
	/**
	 * 
	 */
	private final FIBEditorController fibEditorController;

	/**
	 * @param fibEditorController
	 */
	SwingEditorViewFactory(FIBEditorController fibEditorController) {
		this.fibEditorController = fibEditorController;
	}

	@Override
	public FIBTabView<? extends JComponent, ? extends JComponent> makeTabView(FIBTab container, FIBController controller) {
		return new JFIBEditableTabView(container, fibEditorController);
	}

	@Override
	public FIBPanelView<? extends JComponent, ? extends JComponent> makePanelView(FIBPanel container, FIBController controller) {
		return new JFIBEditablePanelView(container, fibEditorController);
	}

	@Override
	public FIBTabPanelView<? extends JComponent, ? extends JComponent> makeTabPanelView(FIBTabPanel container, FIBController controller) {
		return new JFIBEditableTabPanelView(container, fibEditorController);
	}

	@Override
	public FIBSplitPanelView<? extends JComponent, ? extends JComponent> makeSplitPanelView(FIBSplitPanel container,
			FIBController controller) {
		return new JFIBEditableSplitPanelView(container, fibEditorController);
	}

	@Override
	public FIBLabelWidget<? extends JComponent> makeLabel(FIBLabel widget, FIBController controller) {
		return new JFIBEditableLabelWidget(widget, fibEditorController);
	}

	@Override
	public FIBTextFieldWidget<? extends JComponent> makeTextField(FIBTextField widget, FIBController controller) {
		return new JFIBEditableTextFieldWidget(widget, fibEditorController);
	}

	@Override
	public FIBTextAreaWidgetImpl<? extends JComponent> makeTextArea(FIBTextArea widget, FIBController controller) {
		return new JFIBEditableTextAreaWidget(widget, fibEditorController);
	}

	@Override
	public FIBImageWidgetImpl<? extends JComponent> makeImage(FIBImage widget, FIBController controller) {
		return new JFIBEditableImageWidget(widget, fibEditorController);
	}

	@Override
	public FIBNumberWidgetImpl<? extends JComponent, ?> makeNumber(FIBNumber widget, FIBController controller) {
		return new JFIBEditableNumberWidget(widget, fibEditorController);
	}

	@Override
	public FIBCheckBoxWidgetImpl<? extends JComponent> makeCheckbox(FIBCheckBox widget, FIBController controller) {
		return new JFIBEditableCheckboxWidget(widget, fibEditorController);
	}

	@Override
	public FIBCheckboxListWidgetImpl<? extends JComponent, ?> makeCheckboxList(FIBCheckboxList widget, FIBController controller) {
		return new JFIBEditableCheckboxListWidget(widget, fibEditorController);
	}

	@Override
	public FIBRadioButtonListWidgetImpl<? extends JComponent, ?> makeRadioButtonList(FIBRadioButtonList widget, FIBController controller) {
		return new JFIBEditableRadioButtonListWidget(widget, fibEditorController);
	}

	@Override
	public FIBDropDownWidgetImpl<? extends JComponent, ?> makeDropDown(FIBDropDown widget, FIBController controller) {
		return new JFIBEditableDropDownWidget(widget, fibEditorController);
	}

	@Override
	public FIBListWidgetImpl<? extends JComponent, ?> makeList(FIBList widget, FIBController controller) {
		return new JFIBEditableListWidget(widget, fibEditorController);
	}

	@Override
	public FIBEditorWidgetImpl<? extends JComponent> makeEditor(FIBEditor widget, FIBController controller) {
		return new JFIBEditableEditorWidget(widget, fibEditorController);
	}

	@Override
	public FIBHtmlEditorWidgetImpl<? extends JComponent> makeHtmlEditor(FIBHtmlEditor widget, FIBController controller) {
		return new JFIBEditableHtmlEditorWidget(widget, fibEditorController);
	}

	@Override
	public FIBTableWidgetImpl<? extends JComponent, ?> makeTable(FIBTable widget, FIBController controller) {
		return new JFIBEditableTableWidget(widget, fibEditorController);
	}

	@Override
	public FIBBrowserWidgetImpl<? extends JComponent, ?> makeBrowser(FIBBrowser widget, FIBController controller) {
		return new JFIBEditableBrowserWidget(widget, fibEditorController);
	}

	@Override
	public FIBColorWidgetImpl<? extends JComponent> makeColor(FIBColor widget, FIBController controller) {
		return new JFIBEditableColorWidget(widget, fibEditorController);
	}

	@Override
	public FIBFontWidgetImpl<? extends JComponent> makeFont(FIBFont widget, FIBController controller) {
		return new JFIBEditableFontWidget(widget, fibEditorController);
	}

	@Override
	public FIBFileWidgetImpl<? extends JComponent> makeFile(FIBFile widget, FIBController controller) {
		return new JFIBEditableFileWidget(widget, fibEditorController);
	}

	@Override
	public FIBButtonWidgetImpl<? extends JComponent> makeButton(FIBButton widget, FIBController controller) {
		return new JFIBEditableButtonWidget(widget, fibEditorController);
	}

	@Override
	public FIBCustomWidgetImpl<? extends JComponent, ?, ?> makeCustomWidget(FIBCustom widget, FIBController controller) {
		return new JFIBEditableCustomWidget(widget, fibEditorController);
	}

	@Override
	public FIBReferencedComponentWidgetImpl<? extends JComponent> makeReferencedComponentWidget(FIBReferencedComponent widget,
			FIBController controller) {
		return new JFIBEditableReferencedComponentWidget(widget, fibEditorController);
	}

}
