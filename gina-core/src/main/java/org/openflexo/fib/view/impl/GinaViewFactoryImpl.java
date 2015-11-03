package org.openflexo.fib.view.impl;

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
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.GinaViewFactory;
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
 * Default base partial implementation of a {@link GinaViewFactory}
 * 
 * @author sylvain
 * 
 * @param <C>
 *            base minimal class of components build by this tool factory (eg
 *            JComponent for Swing)
 */
public abstract class GinaViewFactoryImpl<C> implements GinaViewFactory<C> {

	@Override
	public <F extends FIBContainer> FIBContainerView<F, ? extends C, ? extends C> makeContainer(F fibContainer,
			FIBController controller) {
		/*
		 * if (fibContainer instanceof FIBTab) { return new FIBTabView((FIBTab)
		 * fibContainer, this.fibController); } else if (fibContainer instanceof
		 * FIBPanel) { return new FIBPanelView((FIBPanel) fibContainer,
		 * this.fibController); } else if (fibContainer instanceof FIBTabPanel)
		 * { return new FIBTabPanelView((FIBTabPanel) fibContainer,
		 * this.fibController); } else if (fibContainer instanceof
		 * FIBSplitPanel) { return new FIBSplitPanelView((FIBSplitPanel)
		 * fibContainer, this.fibController); }
		 */
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends FIBWidget> FIBWidgetView<F, ? extends C, ?> makeWidget(F fibWidget, FIBController controller) {
		if (fibWidget instanceof FIBLabel) {
			return (FIBWidgetView<F, ? extends C, ?>) makeLabel((FIBLabel) fibWidget, controller);
		}
		if (fibWidget instanceof FIBTextField) {
			return (FIBWidgetView<F, ? extends C, ?>) makeTextField((FIBTextField) fibWidget, controller);
		}
		if (fibWidget instanceof FIBTextArea) {
			return (FIBWidgetView<F, ? extends C, ?>) makeTextArea((FIBTextArea) fibWidget, controller);
		}
		if (fibWidget instanceof FIBImage) {
			return (FIBWidgetView<F, ? extends C, ?>) makeImage((FIBImage) fibWidget, controller);
		}
		if (fibWidget instanceof FIBNumber) {
			return (FIBWidgetView<F, ? extends C, ?>) makeNumber((FIBNumber) fibWidget, controller);
		}
		if (fibWidget instanceof FIBCheckBox) {
			return (FIBWidgetView<F, ? extends C, ?>) makeCheckbox((FIBCheckBox) fibWidget, controller);
		}
		if (fibWidget instanceof FIBDropDown) {
			return (FIBWidgetView<F, ? extends C, ?>) makeDropDown((FIBDropDown) fibWidget, controller);
		}
		if (fibWidget instanceof FIBList) {
			return (FIBWidgetView<F, ? extends C, ?>) makeList((FIBList) fibWidget, controller);
		}
		if (fibWidget instanceof FIBEditor) {
			return (FIBWidgetView<F, ? extends C, ?>) makeEditor((FIBEditor) fibWidget, controller);
		}
		if (fibWidget instanceof FIBHtmlEditor) {
			return (FIBWidgetView<F, ? extends C, ?>) makeHtmlEditor((FIBHtmlEditor) fibWidget, controller);
		}
		if (fibWidget instanceof FIBTable) {
			return (FIBWidgetView<F, ? extends C, ?>) makeTable((FIBTable) fibWidget, controller);
		}
		if (fibWidget instanceof FIBBrowser) {
			return (FIBWidgetView<F, ? extends C, ?>) makeBrowser((FIBBrowser) fibWidget, controller);
		}
		if (fibWidget instanceof FIBColor) {
			return (FIBWidgetView<F, ? extends C, ?>) makeColor((FIBColor) fibWidget, controller);
		}
		if (fibWidget instanceof FIBFont) {
			return (FIBWidgetView<F, ? extends C, ?>) makeFont((FIBFont) fibWidget, controller);
		}
		if (fibWidget instanceof FIBFile) {
			return (FIBWidgetView<F, ? extends C, ?>) makeFile((FIBFile) fibWidget, controller);
		}
		if (fibWidget instanceof FIBButton) {
			return (FIBWidgetView<F, ? extends C, ?>) makeButton((FIBButton) fibWidget, controller);
		}
		if (fibWidget instanceof FIBRadioButtonList) {
			return (FIBWidgetView<F, ? extends C, ?>) makeRadioButtonList((FIBRadioButtonList) fibWidget, controller);
		}
		if (fibWidget instanceof FIBCheckboxList) {
			return (FIBWidgetView<F, ? extends C, ?>) makeCheckboxList((FIBCheckboxList) fibWidget, controller);
		}
		if (fibWidget instanceof FIBCustom) {
			return (FIBWidgetView<F, ? extends C, ?>) makeCustomWidget((FIBCustom) fibWidget, controller);
		}
		if (fibWidget instanceof FIBReferencedComponent) {
			return (FIBWidgetView<F, ? extends C, ?>) makeReferencedComponentWidget((FIBReferencedComponent) fibWidget,
					controller);
		}
		return null;
	}

	public abstract FIBLabelWidget<? extends C> makeLabel(FIBLabel widget, FIBController controller);

	public abstract FIBTextFieldWidget<? extends C> makeTextField(FIBTextField widget, FIBController controller);

	public abstract FIBTextAreaWidgetImpl<? extends C> makeTextArea(FIBTextArea widget, FIBController controller);

	public abstract FIBImageWidgetImpl<? extends C> makeImage(FIBImage widget, FIBController controller);

	public abstract FIBNumberWidgetImpl<? extends C, ?> makeNumber(FIBNumber widget, FIBController controller);

	public abstract FIBCheckBoxWidgetImpl<? extends C> makeCheckbox(FIBCheckBox widget, FIBController controller);

	public abstract FIBCheckboxListWidgetImpl<? extends C, ?> makeCheckboxList(FIBCheckboxList widget,
			FIBController controller);

	public abstract FIBRadioButtonListWidgetImpl<? extends C, ?> makeRadioButtonList(FIBRadioButtonList widget,
			FIBController controller);

	public abstract FIBDropDownWidgetImpl<? extends C, ?> makeDropDown(FIBDropDown widget, FIBController controller);

	public abstract FIBListWidgetImpl<? extends C, ?> makeList(FIBList widget, FIBController controller);

	public abstract FIBEditorWidgetImpl<? extends C> makeEditor(FIBEditor widget, FIBController controller);

	public abstract FIBHtmlEditorWidgetImpl<? extends C> makeHtmlEditor(FIBHtmlEditor widget, FIBController controller);

	public abstract FIBTableWidgetImpl<? extends C, ?> makeTable(FIBTable widget, FIBController controller);

	public abstract FIBBrowserWidgetImpl<? extends C, ?> makeBrowser(FIBBrowser widget, FIBController controller);

	public abstract FIBColorWidgetImpl<? extends C> makeColor(FIBColor widget, FIBController controller);

	public abstract FIBFontWidgetImpl<? extends C> makeFont(FIBFont widget, FIBController controller);

	public abstract FIBFileWidgetImpl<? extends C> makeFile(FIBFile widget, FIBController controller);

	public abstract FIBButtonWidgetImpl<? extends C> makeButton(FIBButton widget, FIBController controller);

	public abstract FIBCustomWidgetImpl<? extends C, ?> makeCustomWidget(FIBCustom widget, FIBController controller);

	public abstract FIBReferencedComponentWidgetImpl<? extends C> makeReferencedComponentWidget(
			FIBReferencedComponent widget, FIBController controller);

}
