/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.gina.swing.editor.controller.action;

import java.io.File;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.controller.FIBController.Status;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.FlowLayoutAlignment;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBFile;
import org.openflexo.gina.model.widget.FIBFile.FileMode;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBLabel.Align;
import org.openflexo.gina.model.widget.FIBReferencedComponent;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorIconLibrary;
import org.openflexo.gina.swing.utils.BindingSelector;
import org.openflexo.gina.swing.utils.JFIBDialog;
import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.icon.IconFactory;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.toolbox.StringUtils;

public class MakeReusableComponent extends AbstractEditorActionImpl {

	private static final Logger logger = FlexoLogger.getLogger(MakeReusableComponent.class.getPackage().getName());

	public MakeReusableComponent(FIBEditorController anEditorController, JFrame frame) {
		super("make_reusable_component", IconFactory.getImageIcon(FIBEditorIconLibrary.PANEL_ICON, FIBEditorIconLibrary.DUPLICATE),
				anEditorController, frame);
	}

	@Override
	public boolean isEnabledFor(FIBModelObject object) {
		return object instanceof FIBContainer;
	}

	@Override
	public boolean isVisibleFor(FIBModelObject object) {
		return true;
	}

	@Override
	public FIBModelObject performAction(FIBModelObject object) {
		if (object instanceof FIBContainer) {
			FIBContainer component = (FIBContainer) object;
			FIBContainer parent = component.getParent();
			return makeReusableComponent(component, parent, getFrame());
		}
		return null;
	}

	public FIBModelObject makeReusableComponent(FIBContainer component, FIBContainer parent, JFrame frame) {
		FIBModelFactory dialogFactory = null;
		try {
			dialogFactory = new FIBModelFactory(null);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			return null;
		}
		MakeReusableComponentParameters params = new MakeReusableComponentParameters(component, parent);
		FIBPanel panel = dialogFactory.newFIBPanel();
		panel.setDataClass(params.getClass());
		panel.setLayout(Layout.twocols);
		FIBLabel title = dialogFactory.newFIBLabel("Make reusable component");
		title.setAlign(Align.center);
		panel.addToSubComponents(title, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false));
		panel.addToSubComponents(dialogFactory.newFIBLabel("file"), new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		FIBFile fileWidget = dialogFactory.newFIBFile();
		fileWidget.setMode(FileMode.SaveMode);
		fileWidget.setData(new DataBinding<>("data.reusableComponentFile"));
		panel.addToSubComponents(fileWidget, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		panel.addToSubComponents(dialogFactory.newFIBLabel("data"), new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		FIBCustom dataWidget = dialogFactory.newFIBCustom();
		dataWidget.setComponentClass(BindingSelector.class);
		dataWidget.setData(new DataBinding<>("data.data"));
		FIBCustom.FIBCustomAssignment assignment = dialogFactory.newInstance(FIBCustom.FIBCustomAssignment.class);
		assignment.setOwner(dataWidget);
		assignment.setVariable(new DataBinding<>("component.bindable"));
		assignment.setValue(new DataBinding<>("data"));
		assignment.setMandatory(true);
		;
		dataWidget.addToAssignments(assignment);
		panel.addToSubComponents(dataWidget, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		FIBPanel controlPanel = dialogFactory.newFIBPanel();
		controlPanel.setLayout(Layout.flow);
		controlPanel.setFlowAlignment(FlowLayoutAlignment.CENTER);
		FIBButton validateButton = dialogFactory.newFIBButton();
		validateButton.setLabel("validate");
		validateButton.setAction(new DataBinding<>("controller.validateAndDispose()"));
		controlPanel.addToSubComponents(validateButton);
		FIBButton cancelButton = dialogFactory.newFIBButton();
		cancelButton.setLabel("cancel");
		cancelButton.setAction(new DataBinding<>("controller.cancelAndDispose()"));
		controlPanel.addToSubComponents(cancelButton);
		panel.addToSubComponents(controlPanel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false));

		JFIBDialog<?> dialog = JFIBDialog.instanciateAndShowDialog(panel, params, frame, true);

		if (dialog.getStatus() == Status.VALIDATED) {
			if (params.reusableComponentFile != null) {
				logger.info("Saving new component to " + params.reusableComponentFile);
				FIBContainer reusableComponent = component;
				parent.removeFromSubComponents(reusableComponent);
				reusableComponent.setControllerClass(parent.getRootComponent().getControllerClass());
				reusableComponent.setDataClass(TypeUtils.getBaseClass(params.data.getAnalyzedType()));
				for (FIBComponent child : reusableComponent.getAllSubComponents()) {
					for (DataBinding<?> binding : child.getDeclaredBindings()) {
						if (binding.isSet()) {
							if (binding.toString().startsWith(params.data.toString())) {
								binding.setUnparsedBinding(binding.toString().replace(params.data.toString(), "data"));
							}
							if (StringUtils.isNotEmpty(reusableComponent.getName())) {
								if (binding.toString().startsWith(reusableComponent.getName() + ".")) {
									binding.setUnparsedBinding(binding.toString().substring(reusableComponent.getName().length() + 1));
								}
							}
						}
					}
				}
				DataBinding<Boolean> visible = reusableComponent.getVisible();
				reusableComponent.setData(null);
				reusableComponent.setVisible(null);

				try {
					FileResourceImpl reusableComponentResource = new FileResourceImpl(params.reusableComponentFile);
					logger.info("Save to " + reusableComponentResource);
					reusableComponent.getFIBLibrary().save(reusableComponent, reusableComponentResource);
					// logger.info("Current directory = " + editorController.getEditor().getEditedComponentFile().getParentFile());
					// RelativePathFileConverter relativePathFileConverter = new RelativePathFileConverter(editorController.getEditor()
					// .getEditedComponentFile().getParentFile());
					// String relativeFilePath = relativePathFileConverter.convertToString(params.reusableComponentFile);
					// logger.info("Relative file path: " + relativeFilePath);
					FIBReferencedComponent widget = dialogFactory.newFIBReferencedComponent();
					widget.setComponentFile(reusableComponentResource);
					widget.setData(params.data);
					widget.setVisible(visible);
					parent.addToSubComponents(widget, reusableComponent.getConstraints());
					return widget;
				} catch (Exception e) {
					logger.severe("Unable to create FileResourceLocation from File: " + params.reusableComponentFile.getName());
					e.printStackTrace();
				}

			}
		}

		return null;
	}

	public static class MakeReusableComponentParameters extends DefaultBindable {
		public String componentName;
		public File reusableComponentFile;
		public Class<?> reusableComponentClass;
		public DataBinding<Object> data;

		private final FIBComponent contextComponent;

		public MakeReusableComponentParameters(FIBContainer component, FIBContainer parent) {
			this.contextComponent = parent;
			if (StringUtils.isNotEmpty(component.getName())) {
				componentName = component.getName();
				reusableComponentFile = new File(JFIBPreferences.getLastDirectory(), componentName + ".fib");
			}
			else {
				reusableComponentFile = new File(JFIBPreferences.getLastDirectory(), "ReusableComponent.fib");
			}
			if (component.getData().isSet()) {
				data = new DataBinding<>(component.getData().toString(), this, Object.class, BindingDefinitionType.GET);
			}
			else {
				data = new DataBinding<>(this, Object.class, BindingDefinitionType.GET);
			}
		}

		@Override
		public BindingModel getBindingModel() {
			return contextComponent.getBindingModel();
		}

		@Override
		public BindingFactory getBindingFactory() {
			return contextComponent.getBindingFactory();
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			reusableComponentClass = TypeUtils.getBaseClass(data.getAnalyzedType());
			System.out.println("reusableComponentClass=" + reusableComponentClass);
		}

		@Override
		public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
			reusableComponentClass = TypeUtils.getBaseClass(data.getAnalyzedType());
			System.out.println("reusableComponentClass=" + reusableComponentClass);
		}
	}

}
