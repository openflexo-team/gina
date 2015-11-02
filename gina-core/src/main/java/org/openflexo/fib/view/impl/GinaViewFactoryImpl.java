package org.openflexo.fib.view.impl;

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
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.model.FIBReferencedComponent;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBTextPane;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.GinaViewFactory;
import org.openflexo.fib.view.widget.FIBReferencedComponentWidget;
import org.openflexo.fib.view.widget.FIBTextFieldWidget;
import org.openflexo.fib.view.widget.impl.FIBTextAreaWidgetImpl;

/**
 * Default base partial implementation of a {@link GinaViewFactory}
 * 
 * @author sylvain
 * 
 * @param <C>
 *            base minimal class of components build by this tool factory (eg JComponent for Swing)
 */
public abstract class GinaViewFactoryImpl<C> implements GinaViewFactory<C> {

	@Override
	public <F extends FIBContainer> FIBContainerView<F, ? extends C, ? extends C> makeContainer(F fibContainer, FIBController controller) {
		/*if (fibContainer instanceof FIBTab) {
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
		}*/
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends FIBWidget> FIBWidgetView<F, ? extends C, ?> makeWidget(F fibWidget, FIBController controller) {
		if (fibWidget instanceof FIBTextField) {
			return (FIBWidgetView<F, ? extends C, ?>) makeTextField((FIBTextField) fibWidget, controller);
		}
		if (fibWidget instanceof FIBEditor) {
			return new FIBEditorWidget((FIBEditor) fibWidget, this.fibController);
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
			return (FIBWidgetView<F, ? extends C, ?>) makeTextArea((FIBTextArea) fibWidget, controller);
		}
		if (fibWidget instanceof FIBHtmlEditor) {
			return new FIBHtmlEditorWidget((FIBHtmlEditor) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBLabel) {
			return new FIBLabelWidgetImpl((FIBLabel) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBImage) {
			return new FIBImageWidgetImpl((FIBImage) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBCheckBox) {
			return new FIBCheckBoxWidgetImpl((FIBCheckBox) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBTable) {
			return new FIBTableWidget((FIBTable) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBBrowser) {
			return new FIBBrowserWidget((FIBBrowser) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBDropDown) {
			return new FIBDropDownWidget((FIBDropDown) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBList) {
			return new FIBListWidget((FIBList) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBNumber) {
			FIBNumber w = (FIBNumber) fibWidget;
			switch (w.getNumberType()) {
				case ByteType:
					return new FIBNumberWidgetImpl.FIBByteWidget(w, this.fibController);
				case ShortType:
					return new FIBNumberWidgetImpl.FIBShortWidget(w, this.fibController);
				case IntegerType:
					return new FIBNumberWidgetImpl.FIBIntegerWidget(w, this.fibController);
				case LongType:
					return new FIBNumberWidgetImpl.FIBLongWidget(w, this.fibController);
				case FloatType:
					return new FIBNumberWidgetImpl.FIBFloatWidget(w, this.fibController);
				case DoubleType:
					return new FIBNumberWidgetImpl.FIBDoubleWidget(w, this.fibController);
				default:
					break;
			}
		}
		if (fibWidget instanceof FIBColor) {
			return new FIBColorWidgetImpl((FIBColor) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBFont) {
			return new FIBFontWidgetImpl((FIBFont) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBFile) {
			return new FIBFileWidgetImpl((FIBFile) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBButton) {
			return new FIBButtonWidgetImpl((FIBButton) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBRadioButtonList) {
			return new FIBRadioButtonListWidget((FIBRadioButtonList) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBCheckboxList) {
			return new FIBCheckboxListWidget((FIBCheckboxList) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBCustom) {
			return new FIBCustomWidget((FIBCustom) fibWidget, this.fibController);
		}
		if (fibWidget instanceof FIBReferencedComponent) {
			return new FIBReferencedComponentWidget((FIBReferencedComponent) fibWidget, this.fibController, this);
		}
		return null;
	}

	public abstract FIBTextFieldWidget<? extends C> makeTextField(FIBTextField widget, FIBController controller);

	public abstract FIBTextAreaWidgetImpl<? extends C> makeTextArea(FIBTextArea widget, FIBController controller);

	public abstract FIBEditorWidget<? extends C> makeEditor(FIBEditor widget, FIBController controller);
}
