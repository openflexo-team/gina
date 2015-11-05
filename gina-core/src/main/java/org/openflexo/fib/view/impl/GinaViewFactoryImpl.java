package org.openflexo.fib.view.impl;

import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.model.container.FIBPanel;
import org.openflexo.fib.model.container.FIBSplitPanel;
import org.openflexo.fib.model.container.FIBTab;
import org.openflexo.fib.model.container.FIBTabPanel;
import org.openflexo.fib.model.widget.FIBBrowser;
import org.openflexo.fib.model.widget.FIBButton;
import org.openflexo.fib.model.widget.FIBCheckBox;
import org.openflexo.fib.model.widget.FIBCheckboxList;
import org.openflexo.fib.model.widget.FIBColor;
import org.openflexo.fib.model.widget.FIBCustom;
import org.openflexo.fib.model.widget.FIBDropDown;
import org.openflexo.fib.model.widget.FIBEditor;
import org.openflexo.fib.model.widget.FIBFile;
import org.openflexo.fib.model.widget.FIBFont;
import org.openflexo.fib.model.widget.FIBHtmlEditor;
import org.openflexo.fib.model.widget.FIBImage;
import org.openflexo.fib.model.widget.FIBLabel;
import org.openflexo.fib.model.widget.FIBList;
import org.openflexo.fib.model.widget.FIBNumber;
import org.openflexo.fib.model.widget.FIBRadioButtonList;
import org.openflexo.fib.model.widget.FIBReferencedComponent;
import org.openflexo.fib.model.widget.FIBTable;
import org.openflexo.fib.model.widget.FIBTextArea;
import org.openflexo.fib.model.widget.FIBTextField;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.GinaViewFactory;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.fib.view.container.FIBSplitPanelView;
import org.openflexo.fib.view.container.FIBTabPanelView;
import org.openflexo.fib.view.container.FIBTabView;
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
 *            base minimal class of components build by this tool factory (eg JComponent for Swing)
 */
public abstract class GinaViewFactoryImpl<C> implements GinaViewFactory<C> {

	private static final Logger LOGGER = Logger.getLogger(GinaViewFactoryImpl.class.getPackage().getName());

	@Override
	public boolean allowsFIBEdition() {
		return false;
	}

	@Override
	public <F extends FIBContainer> FIBContainerView<F, ? extends C, ? extends C> makeContainer(F fibContainer, FIBController controller) {
		FIBContainerView<F, ? extends C, ? extends C> returned = buildContainer(fibContainer, controller);
		if (returned != null) {
			returned.updateGraphicalProperties();
		}
		return returned;
	}

	private <F extends FIBContainer> FIBContainerView<F, ? extends C, ? extends C> buildContainer(F fibContainer,
			FIBController controller) {

		if (fibContainer instanceof FIBTab) {
			return (FIBContainerView<F, ? extends C, ? extends C>) makeTabView((FIBTab) fibContainer, controller);
		}
		else if (fibContainer instanceof FIBPanel) {
			return (FIBContainerView<F, ? extends C, ? extends C>) makePanelView((FIBPanel) fibContainer, controller);
		}
		else if (fibContainer instanceof FIBTabPanel) {
			return (FIBContainerView<F, ? extends C, ? extends C>) makeTabPanelView((FIBTabPanel) fibContainer, controller);
		}
		else if (fibContainer instanceof FIBSplitPanel) {
			return (FIBContainerView<F, ? extends C, ? extends C>) makeSplitPanelView((FIBSplitPanel) fibContainer, controller);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends FIBWidget> FIBWidgetView<F, ? extends C, ?> makeWidget(F fibWidget, FIBController controller) {
		final FIBWidgetView<F, ? extends C, ?> returned = buildWidget(fibWidget, controller);
		returned.updateGraphicalProperties();
		return returned;
	}

	@SuppressWarnings("unchecked")
	private <F extends FIBWidget> FIBWidgetView<F, ? extends C, ?> buildWidget(F fibWidget, FIBController controller) {
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
			return (FIBWidgetView<F, ? extends C, ?>) makeReferencedComponentWidget((FIBReferencedComponent) fibWidget, controller);
		}
		return null;
	}

	public abstract FIBTabView<? extends C, ? extends C> makeTabView(FIBTab container, FIBController controller);

	public abstract FIBPanelView<? extends C, ? extends C> makePanelView(FIBPanel container, FIBController controller);

	public abstract FIBTabPanelView<? extends C, ? extends C> makeTabPanelView(FIBTabPanel container, FIBController controller);

	public abstract FIBSplitPanelView<? extends C, ? extends C> makeSplitPanelView(FIBSplitPanel container, FIBController controller);

	public abstract FIBLabelWidget<? extends C> makeLabel(FIBLabel widget, FIBController controller);

	public abstract FIBTextFieldWidget<? extends C> makeTextField(FIBTextField widget, FIBController controller);

	public abstract FIBTextAreaWidgetImpl<? extends C> makeTextArea(FIBTextArea widget, FIBController controller);

	public abstract FIBImageWidgetImpl<? extends C> makeImage(FIBImage widget, FIBController controller);

	public abstract FIBNumberWidgetImpl<? extends C, ?> makeNumber(FIBNumber widget, FIBController controller);

	public abstract FIBCheckBoxWidgetImpl<? extends C> makeCheckbox(FIBCheckBox widget, FIBController controller);

	public abstract FIBCheckboxListWidgetImpl<? extends C, ?> makeCheckboxList(FIBCheckboxList widget, FIBController controller);

	public abstract FIBRadioButtonListWidgetImpl<? extends C, ?> makeRadioButtonList(FIBRadioButtonList widget, FIBController controller);

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

	public abstract FIBReferencedComponentWidgetImpl<? extends C> makeReferencedComponentWidget(FIBReferencedComponent widget,
			FIBController controller);

}
