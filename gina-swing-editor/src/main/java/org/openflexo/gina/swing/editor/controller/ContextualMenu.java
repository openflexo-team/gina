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

package org.openflexo.gina.swing.editor.controller;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

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
import org.openflexo.gina.model.container.FIBPanel.Border;
import org.openflexo.gina.model.container.FIBPanel.FlowLayoutAlignment;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.layout.BorderLayoutConstraints;
import org.openflexo.gina.model.container.layout.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.gina.model.container.layout.SplitLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBFile;
import org.openflexo.gina.model.widget.FIBFile.FileMode;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBLabel.Align;
import org.openflexo.gina.model.widget.FIBReferencedComponent;
import org.openflexo.gina.swing.editor.controller.EditorAction.ActionAvailability;
import org.openflexo.gina.swing.utils.BindingSelector;
import org.openflexo.gina.swing.utils.JFIBDialog;
import org.openflexo.gina.swing.utils.JFIBDialogInspectorController;
import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.gina.swing.view.widget.JFIBReferencedComponentWidget;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.StringUtils;

public class ContextualMenu {
	private static final Logger logger = FlexoLogger.getLogger(ContextualMenu.class.getPackage().getName());
	private static final ResourceLocator rl = ResourceLocator.getResourceLocator();

	private FIBEditorController editorController;
	private Hashtable<EditorAction, PopupMenuItem> actions;
	private JPopupMenu menu;

	public ContextualMenu(FIBEditorController anEditorController, final JFrame frame) {
		this.editorController = anEditorController;
		actions = new Hashtable<>();
		menu = new JPopupMenu();

		addToActions(new EditorAction("Inspect", null, object -> {
			JFIBDialogInspectorController inspector = editorController.getEditor().getInspector();
			if (inspector != null)
				inspector.setVisible(true);
			return object;
		}, object -> object != null));
		addToActions(new EditorAction("Delete", FIBEditorIconLibrary.DELETE_ICON, object -> {
			if (object instanceof FIBComponent) {
				FIBContainer parent = ((FIBComponent) object).getParent();
				boolean deleteIt = JOptionPane.showConfirmDialog(frame, object + ": really delete this component (undoable operation) ?",
						"information", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
				if (deleteIt) {
					logger.info("Removing object " + object + " from " + parent);
					parent.removeFromSubComponents((FIBComponent) object);
					object.delete();
				}
				return parent;
			}
			return null;
		}, object -> object instanceof FIBComponent));

		ActionAvailability componentWithParent = object -> object instanceof FIBComponent && ((FIBComponent) object).getParent() != null;
		addToActions(new EditorAction("Wrap with panel", null, object -> {
			FIBComponent component = (FIBComponent) object;
			FIBContainer parent = component.getParent();
			parent.removeFromSubComponents(component);
			FIBPanel newPanel = editorController.getFactory().newFIBPanel();
			newPanel.setLayout(Layout.border);
			newPanel.setBorder(Border.titled);
			newPanel.finalizeDeserialization();
			parent.addToSubComponents(newPanel, component.getConstraints());
			newPanel.addToSubComponents(component, new BorderLayoutConstraints(BorderLayoutLocation.center));
			return parent;
		}, componentWithParent));

		addToActions(new EditorAction("Wrap with split panel", null, object -> {
			FIBComponent component = (FIBComponent) object;
			FIBContainer parent = component.getParent();
			parent.removeFromSubComponents(component);
			FIBSplitPanel newPanel = editorController.getFactory().newFIBSplitPanel();
			newPanel.makeDefaultHorizontalLayout();
			parent.addToSubComponents(newPanel, component.getConstraints());
			newPanel.addToSubComponents(component, SplitLayoutConstraints.makeSplitLayoutConstraints(newPanel.getFirstEmptyPlaceHolder()));
			return parent;
		}, componentWithParent));

		addToActions(new EditorAction("Make reusable component", null, object -> {
			FIBContainer component = (FIBContainer) object;
			FIBContainer parent = component.getParent();
			return makeReusableComponent(component, parent, frame);
		}, componentWithParent));

		addToActions(new EditorAction("Open component", null, object -> {
			FIBReferencedComponent referencedComponent = (FIBReferencedComponent) object;

			JFIBReferencedComponentWidget widgetView = (JFIBReferencedComponentWidget) editorController.getController()
					.viewForComponent(referencedComponent);

			Object dataObject = widgetView.getValue();

			editorController.getEditor().loadFIB(widgetView.getComponentFile(), dataObject, frame);

			return referencedComponent;
		}, object -> object instanceof FIBReferencedComponent));
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

	public void addToActions(EditorAction action) {
		PopupMenuItem newMenuItem = new PopupMenuItem(action);
		menu.add(newMenuItem);
		actions.put(action, newMenuItem);
	}

	public void displayPopupMenu(FIBModelObject object, Component invoker, Point p) {
		if (menu == null) {
			return;
		}
		for (EditorAction action : actions.keySet()) {
			PopupMenuItem menuItem = actions.get(action);
			menuItem.setObject(object);
		}
		if (p != null) {
			menu.show(invoker, p.x, p.y);
		}
	}

	class PopupMenuItem extends JMenuItem {
		private FIBModelObject object;
		private final EditorAction action;

		public PopupMenuItem(EditorAction anAction) {
			super(anAction.getActionName(), anAction.getActionIcon());
			this.action = anAction;
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FIBModelObject selectThis = action.getPerformer().performAction(object);
					if (selectThis instanceof FIBComponent) {
						editorController.setSelectedObject(selectThis);
					}
				}
			});
		}

		public FIBModelObject getObject() {
			return object;
		}

		public void setObject(FIBModelObject object) {
			this.object = object;
			setVisible(action.getAvailability().isAvailableFor(object));
		}

	}

}
