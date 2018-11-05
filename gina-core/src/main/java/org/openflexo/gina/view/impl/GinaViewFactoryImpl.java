package org.openflexo.gina.view.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.bindings.RuntimeContext;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.model.operator.FIBIteration;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBColor;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBDate;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.model.widget.FIBEditor;
import org.openflexo.gina.model.widget.FIBEditorPane;
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
import org.openflexo.gina.utils.InteractiveFIBEditor;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBWidgetView;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.gina.view.GinaViewFactoryExtension;
import org.openflexo.gina.view.container.FIBPanelView;
import org.openflexo.gina.view.container.FIBSplitPanelView;
import org.openflexo.gina.view.container.FIBTabPanelView;
import org.openflexo.gina.view.container.FIBTabView;
import org.openflexo.gina.view.operator.FIBIterationView;
import org.openflexo.gina.view.widget.FIBLabelWidget;
import org.openflexo.gina.view.widget.FIBTextFieldWidget;
import org.openflexo.gina.view.widget.impl.FIBBrowserWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBButtonWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBCheckBoxWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBCheckboxListWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBColorWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBCustomWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBDateWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBDropDownWidgetImpl;
import org.openflexo.gina.view.widget.impl.FIBEditorPaneWidgetImpl;
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
 * Default base partial implementation of a {@link GinaViewFactory}
 * 
 * @author sylvain
 * 
 * @param <C>
 *            base minimal class of components build by this tool factory (eg JComponent for Swing)
 */
public abstract class GinaViewFactoryImpl<C> implements GinaViewFactory<C> {

	private static final Logger LOGGER = Logger.getLogger(GinaViewFactoryImpl.class.getPackage().getName());

	private InteractiveFIBEditor interactiveFIBEditor = null;

	private List<GinaViewFactoryExtension> extensions = new ArrayList<>();

	public GinaViewFactoryImpl() {
		LOGGER.info("Loading available GinaViewFactory extensions...");
		ServiceLoader<GinaViewFactoryExtension> loader = ServiceLoader.load(GinaViewFactoryExtension.class);
		Iterator<GinaViewFactoryExtension> iterator = loader.iterator();
		while (iterator.hasNext()) {
			GinaViewFactoryExtension extension = iterator.next();
			extension.install(this);
			extensions.add(extension);
		}
		LOGGER.info("Loading available GinaViewFactory extensions. Done.");
	}

	public InteractiveFIBEditor getInteractiveFIBEditor() {
		return interactiveFIBEditor;
	}

	public void installInteractiveFIBEditor(InteractiveFIBEditor interactiveFIBEditor) {
		this.interactiveFIBEditor = interactiveFIBEditor;
	}

	@Override
	public boolean allowsFIBEdition() {
		return interactiveFIBEditor != null;
	}

	@Override
	public <F extends FIBContainer> FIBContainerView<F, ? extends C, ? extends C> makeContainer(F fibContainer, FIBController controller,
			RuntimeContext context, boolean updateNow) {
		FIBContainerView<F, ? extends C, ? extends C> returned = null;
		for (GinaViewFactoryExtension extension : extensions) {
			if (extension.handleContainer(fibContainer)) {
				returned = (FIBContainerView<F, ? extends C, ? extends C>) extension.makeContainer(fibContainer, controller, context,
						updateNow);
				if (returned != null) {
					returned.setRuntimeContext(context);
					break;
				}
			}
		}
		if (returned == null) {
			returned = buildContainer(fibContainer, controller, context);
			returned.setRuntimeContext(context);
		}
		if (updateNow) {
			// returned.updateGraphicalProperties();
			returned.update();
		}
		return returned;
	}

	private <F extends FIBContainer> FIBContainerView<F, ? extends C, ? extends C> buildContainer(F fibContainer, FIBController controller,
			RuntimeContext context) {

		if (fibContainer instanceof FIBTab) {
			return (FIBContainerView<F, ? extends C, ? extends C>) makeTabView((FIBTab) fibContainer, controller, context);
		}
		else if (fibContainer instanceof FIBIteration) {
			return (FIBContainerView<F, ? extends C, ? extends C>) makeIterationView((FIBIteration) fibContainer, controller, context);
		}
		else if (fibContainer instanceof FIBPanel) {
			return (FIBContainerView<F, ? extends C, ? extends C>) makePanelView((FIBPanel) fibContainer, controller, context);
		}
		else if (fibContainer instanceof FIBTabPanel) {
			return (FIBContainerView<F, ? extends C, ? extends C>) makeTabPanelView((FIBTabPanel) fibContainer, controller, context);
		}
		else if (fibContainer instanceof FIBSplitPanel) {
			return (FIBContainerView<F, ? extends C, ? extends C>) makeSplitPanelView((FIBSplitPanel) fibContainer, controller, context);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends FIBWidget> FIBWidgetView<F, ? extends C, ?> makeWidget(F fibWidget, FIBController controller, RuntimeContext context,
			boolean updateNow) {
		FIBWidgetView<F, ? extends C, ?> returned = null;
		for (GinaViewFactoryExtension extension : extensions) {
			if (extension.handleWidget(fibWidget)) {
				returned = (FIBWidgetView<F, ? extends C, ?>) extension.makeWidget(fibWidget, controller, context);
				if (returned != null) {
					returned.setRuntimeContext(context);
					break;
				}
			}
		}
		if (returned == null) {
			returned = buildWidget(fibWidget, controller, context);
			returned.setRuntimeContext(context);
		}
		if (updateNow) {
			returned.update();
		}
		return returned;
	}

	@SuppressWarnings("unchecked")
	private <F extends FIBWidget> FIBWidgetView<F, ? extends C, ?> buildWidget(F fibWidget, FIBController controller,
			RuntimeContext context) {
		if (fibWidget instanceof FIBLabel) {
			return (FIBWidgetView<F, ? extends C, ?>) makeLabel((FIBLabel) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBTextField) {
			return (FIBWidgetView<F, ? extends C, ?>) makeTextField((FIBTextField) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBTextArea) {
			return (FIBWidgetView<F, ? extends C, ?>) makeTextArea((FIBTextArea) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBEditorPane) {
			return (FIBWidgetView<F, ? extends C, ?>) makeEditorPane((FIBEditorPane) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBImage) {
			return (FIBWidgetView<F, ? extends C, ?>) makeImage((FIBImage) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBNumber) {
			return (FIBWidgetView<F, ? extends C, ?>) makeNumber((FIBNumber) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBCheckBox) {
			return (FIBWidgetView<F, ? extends C, ?>) makeCheckbox((FIBCheckBox) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBDropDown) {
			return (FIBWidgetView<F, ? extends C, ?>) makeDropDown((FIBDropDown) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBList) {
			return (FIBWidgetView<F, ? extends C, ?>) makeList((FIBList) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBEditor) {
			return (FIBWidgetView<F, ? extends C, ?>) makeEditor((FIBEditor) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBHtmlEditor) {
			return (FIBWidgetView<F, ? extends C, ?>) makeHtmlEditor((FIBHtmlEditor) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBTable) {
			return (FIBWidgetView<F, ? extends C, ?>) makeTable((FIBTable) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBBrowser) {
			return (FIBWidgetView<F, ? extends C, ?>) makeBrowser((FIBBrowser) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBColor) {
			return (FIBWidgetView<F, ? extends C, ?>) makeColor((FIBColor) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBFont) {
			return (FIBWidgetView<F, ? extends C, ?>) makeFont((FIBFont) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBDate) {
			return (FIBWidgetView<F, ? extends C, ?>) makeDate((FIBDate) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBFile) {
			return (FIBWidgetView<F, ? extends C, ?>) makeFile((FIBFile) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBButton) {
			return (FIBWidgetView<F, ? extends C, ?>) makeButton((FIBButton) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBRadioButtonList) {
			return (FIBWidgetView<F, ? extends C, ?>) makeRadioButtonList((FIBRadioButtonList) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBCheckboxList) {
			return (FIBWidgetView<F, ? extends C, ?>) makeCheckboxList((FIBCheckboxList) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBCustom) {
			return (FIBWidgetView<F, ? extends C, ?>) makeCustomWidget((FIBCustom) fibWidget, controller, context);
		}
		if (fibWidget instanceof FIBReferencedComponent) {
			return (FIBWidgetView<F, ? extends C, ?>) makeReferencedComponentWidget((FIBReferencedComponent) fibWidget, controller,
					context);
		}
		return null;
	}

	public abstract FIBTabView<? extends C, ? extends C> makeTabView(FIBTab container, FIBController controller, RuntimeContext context);

	public abstract FIBPanelView<? extends C, ? extends C> makePanelView(FIBPanel container, FIBController controller,
			RuntimeContext context);

	public abstract FIBTabPanelView<? extends C, ? extends C> makeTabPanelView(FIBTabPanel container, FIBController controller,
			RuntimeContext context);

	public abstract FIBSplitPanelView<? extends C, ? extends C> makeSplitPanelView(FIBSplitPanel container, FIBController controller,
			RuntimeContext context);

	public abstract FIBIterationView<? extends C, ? extends C> makeIterationView(FIBIteration iteration, FIBController controller,
			RuntimeContext context);

	public abstract FIBLabelWidget<? extends C> makeLabel(FIBLabel widget, FIBController controller, RuntimeContext context);

	public abstract FIBTextFieldWidget<? extends C> makeTextField(FIBTextField widget, FIBController controller, RuntimeContext context);

	public abstract FIBTextAreaWidgetImpl<? extends C> makeTextArea(FIBTextArea widget, FIBController controller, RuntimeContext context);

	public abstract FIBEditorPaneWidgetImpl<? extends C> makeEditorPane(FIBEditorPane widget, FIBController controller,
			RuntimeContext context);

	public abstract FIBImageWidgetImpl<? extends C> makeImage(FIBImage widget, FIBController controller, RuntimeContext context);

	public abstract FIBNumberWidgetImpl<? extends C, ?> makeNumber(FIBNumber widget, FIBController controller, RuntimeContext context);

	public abstract FIBCheckBoxWidgetImpl<? extends C> makeCheckbox(FIBCheckBox widget, FIBController controller, RuntimeContext context);

	public abstract FIBCheckboxListWidgetImpl<? extends C, ?> makeCheckboxList(FIBCheckboxList widget, FIBController controller,
			RuntimeContext context);

	public abstract FIBRadioButtonListWidgetImpl<? extends C, ?> makeRadioButtonList(FIBRadioButtonList widget, FIBController controller,
			RuntimeContext context);

	public abstract FIBDropDownWidgetImpl<? extends C, ?> makeDropDown(FIBDropDown widget, FIBController controller,
			RuntimeContext context);

	public abstract FIBListWidgetImpl<? extends C, ?> makeList(FIBList widget, FIBController controller, RuntimeContext context);

	public abstract FIBEditorWidgetImpl<? extends C> makeEditor(FIBEditor widget, FIBController controller, RuntimeContext context);

	public abstract FIBHtmlEditorWidgetImpl<? extends C> makeHtmlEditor(FIBHtmlEditor widget, FIBController controller,
			RuntimeContext context);

	public abstract FIBTableWidgetImpl<? extends C, ?> makeTable(FIBTable widget, FIBController controller, RuntimeContext context);

	public abstract FIBBrowserWidgetImpl<? extends C, ?> makeBrowser(FIBBrowser widget, FIBController controller, RuntimeContext context);

	public abstract FIBColorWidgetImpl<? extends C> makeColor(FIBColor widget, FIBController controller, RuntimeContext context);

	public abstract FIBFontWidgetImpl<? extends C> makeFont(FIBFont widget, FIBController controller, RuntimeContext context);

	public abstract FIBDateWidgetImpl<? extends C> makeDate(FIBDate widget, FIBController controller, RuntimeContext context);

	public abstract FIBFileWidgetImpl<? extends C> makeFile(FIBFile widget, FIBController controller, RuntimeContext context);

	public abstract FIBButtonWidgetImpl<? extends C> makeButton(FIBButton widget, FIBController controller, RuntimeContext context);

	public abstract FIBCustomWidgetImpl<? extends C, ?, ?> makeCustomWidget(FIBCustom widget, FIBController controller,
			RuntimeContext context);

	public abstract FIBReferencedComponentWidgetImpl<? extends C> makeReferencedComponentWidget(FIBReferencedComponent widget,
			FIBController controller, RuntimeContext context);

}
