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

import java.util.logging.Level;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBCheckboxList;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBEditor;
import org.openflexo.fib.model.FIBEditorPane;
import org.openflexo.fib.model.FIBFile;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBImage;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.model.FIBReferencedComponent;
import org.openflexo.fib.model.FIBSplitPanel;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBTextPane;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.swing.view.container.FIBPanelView;
import org.openflexo.fib.swing.view.container.FIBSplitPanelView;
import org.openflexo.fib.swing.view.container.FIBTabPanelView;
import org.openflexo.fib.swing.view.container.FIBTabView;
import org.openflexo.fib.swing.view.widget.JFIBBrowserWidget;
import org.openflexo.fib.swing.view.widget.JFIBButtonWidget;
import org.openflexo.fib.swing.view.widget.JFIBCheckBoxWidget;
import org.openflexo.fib.swing.view.widget.JFIBCheckboxListWidget;
import org.openflexo.fib.swing.view.widget.JFIBColorWidget;
import org.openflexo.fib.swing.view.widget.JFIBCustomWidget;
import org.openflexo.fib.swing.view.widget.JFIBDropDownWidget;
import org.openflexo.fib.swing.view.widget.FIBEditorPaneWidget;
import org.openflexo.fib.swing.view.widget.JFIBEditorWidget;
import org.openflexo.fib.swing.view.widget.JFIBFileWidget;
import org.openflexo.fib.swing.view.widget.JFIBFontWidget;
import org.openflexo.fib.swing.view.widget.JFIBHtmlEditorWidget;
import org.openflexo.fib.swing.view.widget.JFIBImageWidget;
import org.openflexo.fib.swing.view.widget.JFIBLabelWidget;
import org.openflexo.fib.swing.view.widget.JFIBListWidget;
import org.openflexo.fib.swing.view.widget.JFIBNumberWidget;
import org.openflexo.fib.swing.view.widget.JFIBRadioButtonListWidget;
import org.openflexo.fib.swing.view.widget.JFIBTableWidget;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.GinaViewFactory;
import org.openflexo.fib.view.impl.GinaViewFactoryImpl;
import org.openflexo.fib.view.widget.FIBReferencedComponentWidget;
import org.openflexo.fib.view.widget.FIBTextFieldWidget;
import org.openflexo.fib.view.widget.impl.FIBTextAreaWidgetImpl;

/**
 * A {@link GinaViewFactory} implementation dedicated for Swing
 * 
 * @author sylvain
 * 
 */
public class SwingViewFactory extends GinaViewFactoryImpl<C> {

	protected SwingViewFactory(FIBController fibController) {
		this.fibController = fibController;
	}

	@Override
	public FIBView makeContainer(FIBContainer fibContainer) {
		if (fibContainer instanceof FIBTab) {
			return new FIBTabView((FIBTab) fibContainer, this.fibController);
		}
		else if (fibContainer instanceof FIBPanel) {
			return new FIBPanelView((FIBPanel) fibContainer, this.fibController);
		}
		else if (fibContainer instanceof FIBTabPanel) {
			return new FIBTabPanelView((FIBTabPanel) fibContainer, this.fibController);
		}
		else if (fibContainer instanceof FIBSplitPanel) {
			return new FIBSplitPanelView((FIBSplitPanel) fibContainer, this.fibController);
		}
		return null;
	}

	@Override
	public FIBWidgetView makeWidget(FIBWidget fibWidget) {
		if (fibWidget instanceof FIBTextField) {
			return makeTextField((FIBTextField) fibWidget, fibController);
		}
		if (fibWidget instanceof FIBEditor) {
			return new JFIBEditorWidget((FIBEditor) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBTextPane) {
			if (FIBController.LOGGER.isLoggable(Level.WARNING)) {
				FIBController.LOGGER.warning("Can't handle TextPane yet");
			}
			return new FIBEditorPaneWidget((FIBEditorPane) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBEditorPane) {
			return new FIBEditorPaneWidget((FIBEditorPane) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBTextArea) {
			return new FIBTextAreaWidgetImpl((FIBTextArea) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBHtmlEditor) {
			return new JFIBHtmlEditorWidget((FIBHtmlEditor) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBLabel) {
			return new JFIBLabelWidget((FIBLabel) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBImage) {
			return new JFIBImageWidget((FIBImage) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBCheckBox) {
			return new JFIBCheckBoxWidget((FIBCheckBox) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBTable) {
			return new JFIBTableWidget((FIBTable) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBBrowser) {
			return new JFIBBrowserWidget((FIBBrowser) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBDropDown) {
			return new JFIBDropDownWidget((FIBDropDown) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBList) {
			return new JFIBListWidget((FIBList) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBNumber) {
			FIBNumber w = (FIBNumber) fibWidget;
			switch (w.getNumberType()) {
				case ByteType:
					return new JFIBNumberWidget.FIBByteWidget(w, this.fibController);
				case ShortType:
					return new JFIBNumberWidget.FIBShortWidget(w, this.fibController);
				case IntegerType:
					return new JFIBNumberWidget.FIBIntegerWidget(w, this.fibController);
				case LongType:
					return new JFIBNumberWidget.FIBLongWidget(w, this.fibController);
				case FloatType:
					return new JFIBNumberWidget.FIBFloatWidget(w, this.fibController);
				case DoubleType:
					return new JFIBNumberWidget.FIBDoubleWidget(w, this.fibController);
				default:
					break;
			}
		}
		if (fibWidget instanceof FIBColor) {
			return new JFIBColorWidget((FIBColor) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBFont) {
			return new JFIBFontWidget((FIBFont) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBFile) {
			return new JFIBFileWidget((FIBFile) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBButton) {
			return new JFIBButtonWidget((FIBButton) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBRadioButtonList) {
			return new JFIBRadioButtonListWidget((FIBRadioButtonList) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBCheckboxList) {
			return new JFIBCheckboxListWidget((FIBCheckboxList) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBCustom) {
			return new JFIBCustomWidget((FIBCustom) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBReferencedComponent) {
			return new FIBReferencedComponentWidget((FIBReferencedComponent) fibWidget, this.fibController, this);
		}
		return null;
	}

	@Override
	public abstract FIBTextFieldWidget<? extends C> makeTextField(FIBTextField widget, FIBController controller);

	@Override
	public abstract FIBTextAreaWidgetImpl<? extends C> makeTextArea(FIBTextArea widget, FIBController controller);

	public abstract JFIBEditorWidget<? extends C> makeEditor(FIBEditor widget, FIBController controller);
}
