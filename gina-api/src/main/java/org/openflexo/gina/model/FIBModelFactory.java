/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.gina.model;

import java.lang.reflect.Type;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.type.CustomTypeManager;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.converter.ComponentConstraintsConverter;
import org.openflexo.gina.model.graph.FIBContinuousSimpleFunctionGraph;
import org.openflexo.gina.model.graph.FIBDiscreteFunction;
import org.openflexo.gina.model.graph.FIBGraph;
import org.openflexo.gina.model.graph.FIBNumericFunction;
import org.openflexo.gina.model.operator.FIBIteration;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElementChildren;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomAssignment;
import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.converter.DataBindingConverter;
import org.openflexo.pamela.converter.RelativePathResourceConverter;
import org.openflexo.pamela.converter.TypeConverter;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;
import org.openflexo.gina.model.widget.FIBDate;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.model.widget.FIBDropDownColumn;
import org.openflexo.gina.model.widget.FIBFile;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBLabelColumn;
import org.openflexo.gina.model.widget.FIBList;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBNumberColumn;
import org.openflexo.gina.model.widget.FIBRadioButtonList;
import org.openflexo.gina.model.widget.FIBReferencedComponent;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.model.widget.FIBTextFieldColumn;
import org.openflexo.rm.Resource;

/**
 * {@link ModelFactory} used to handle FIB models<br>
 * One instance is declared for the {@link FIBLibrary}
 * 
 * @author sylvain
 * 
 */
public class FIBModelFactory extends ModelFactory {

	private final FIBValidationModel validationModel;

	private final RelativePathResourceConverter relativePathResourceConverter;

	public FIBModelFactory(CustomTypeManager customTypeManager, Class<?>... additionalClasses) throws ModelDefinitionException {
		this(null, customTypeManager, additionalClasses);
	}

	public FIBModelFactory(Resource containerResource, CustomTypeManager customTypeManager, Class<?>... additionalClasses)
			throws ModelDefinitionException {
		super(ModelContextLibrary.getCompoundModelContext(FIBComponent.class, additionalClasses));
		relativePathResourceConverter = new RelativePathResourceConverter(null);
		relativePathResourceConverter.setContainerResource(containerResource);

		addConverter(new DataBindingConverter());
		addConverter(new ComponentConstraintsConverter());
		addConverter(new TypeConverter(customTypeManager != null ? customTypeManager.getCustomTypeFactories() : null));
		addConverter(relativePathResourceConverter);
		validationModel = new FIBValidationModel(this);
	}

	public FIBValidationModel getValidationModel() {
		return validationModel;
	}

	public FIBVariable<?> newFIBVariable(FIBComponent component, String name) {
		return newFIBVariable(component, name, (Type) null);
	}

	public FIBVariable<?> newFIBVariable(FIBComponent component, String name, Type type) {
		FIBVariable<?> returned = newInstance(FIBVariable.class);
		returned.setName(name);
		if (type != null) {
			returned.setType(type);
		}
		component.addToVariables(returned);
		return returned;
	}

	public <T> FIBVariable<T> newFIBVariable(FIBComponent component, String name, DataBinding<T> value) {
		@SuppressWarnings("unchecked")
		FIBVariable<T> returned = newInstance(FIBVariable.class);
		returned.setName(name);
		returned.setValue(value);
		component.addToVariables(returned);
		return returned;
	}

	public FIBPanel newFIBPanel() {
		FIBPanel returned = newInstance(FIBPanel.class);
		returned.setLayout(Layout.border);
		return returned;
	}

	public FIBIteration newFIBIteration() {
		FIBIteration returned = newInstance(FIBIteration.class);
		return returned;
	}

	public FIBLabel newFIBLabel() {
		return newInstance(FIBLabel.class);
	}

	public FIBLabel newFIBLabel(String label) {
		FIBLabel returned = newInstance(FIBLabel.class);
		returned.setLabel(label);
		return returned;
	}

	public FIBTextField newFIBTextField() {
		return newInstance(FIBTextField.class);
	}

	public FIBBrowser newFIBBrowser() {
		return newInstance(FIBBrowser.class);
	}

	public FIBBrowserElement newFIBBrowserElement() {
		return newInstance(FIBBrowserElement.class);
	}

	public FIBBrowserElementChildren newFIBBrowserElementChildren() {
		return newInstance(FIBBrowserElementChildren.class);
	}

	public FIBCheckboxList newFIBCheckboxList() {
		return newInstance(FIBCheckboxList.class);
	}

	public FIBDropDown newFIBDropDown() {
		return newInstance(FIBDropDown.class);
	}

	public FIBList newFIBList() {
		return newInstance(FIBList.class);
	}

	public FIBNumber newFIBNumber() {
		return newInstance(FIBNumber.class);
	}

	public FIBCheckBox newFIBCheckBox() {
		return newInstance(FIBCheckBox.class);
	}

	public FIBRadioButtonList newFIBRadioButtonList() {
		return newInstance(FIBRadioButtonList.class);
	}

	public FIBNumberColumn newFIBNumberColumn() {
		return newInstance(FIBNumberColumn.class);
	}

	public FIBLabelColumn newFIBLabelColumn() {
		return newInstance(FIBLabelColumn.class);
	}

	public FIBDropDownColumn newFIBDropDownColumn() {
		return newInstance(FIBDropDownColumn.class);
	}

	public FIBTable newFIBTable() {
		return newInstance(FIBTable.class);
	}

	public FIBTextFieldColumn newFIBTextFieldColumn() {
		return newInstance(FIBTextFieldColumn.class);
	}

	public FIBTextArea newFIBTextArea() {
		return newInstance(FIBTextArea.class);
	}

	public FIBDate newFIBDate() {
		return newInstance(FIBDate.class);
	}

	public FIBSplitPanel newFIBSplitPanel() {
		return newInstance(FIBSplitPanel.class);
	}

	public FIBReferencedComponent newFIBReferencedComponent() {
		return newInstance(FIBReferencedComponent.class);
	}

	public FIBFile newFIBFile() {
		return newInstance(FIBFile.class);
	}

	public FIBCustom newFIBCustom() {
		return newInstance(FIBCustom.class);
	}

	public FIBCustomAssignment newFIBCustomAssignment() {
		return newInstance(FIBCustomAssignment.class);
	}

	public FIBCustomAssignment newFIBCustomAssignment(FIBCustom owner, DataBinding<?> variable, DataBinding<?> value, boolean b) {
		FIBCustomAssignment returned = newFIBCustomAssignment();
		returned.setOwner(owner);
		returned.setVariable(variable);
		returned.setValue(value);
		return returned;
	}

	public FIBButton newFIBButton() {
		return newInstance(FIBButton.class);
	}

	public FIBTab newFIBTab() {
		return newInstance(FIBTab.class);
	}

	public FIBDependancy newFIBDependancy(FIBComponent masterComponent) {
		FIBDependancy returned = newInstance(FIBDependancy.class);
		returned.setMasterComponent(masterComponent);
		return returned;
	}

	public FIBContinuousSimpleFunctionGraph newFIBContinuousSimpleFunctionGraph() {
		FIBContinuousSimpleFunctionGraph returned = newInstance(FIBContinuousSimpleFunctionGraph.class);
		returned.setMinValue(new DataBinding<Double>(FIBContinuousSimpleFunctionGraph.DEFAULT_MIN_VALUE.toString()));
		returned.setMaxValue(new DataBinding<Double>(FIBContinuousSimpleFunctionGraph.DEFAULT_MAX_VALUE.toString()));
		returned.setMajorTickSpacing(new DataBinding<Double>(FIBContinuousSimpleFunctionGraph.DEFAULT_MAJOR_TICK_SPACING.toString()));
		returned.setMinorTickSpacing(new DataBinding<Double>(FIBContinuousSimpleFunctionGraph.DEFAULT_MINOR_TICK_SPACING.toString()));
		returned.setStepsNumber(new DataBinding<Integer>(FIBContinuousSimpleFunctionGraph.DEFAULT_STEPS_NUMBER.toString()));
		return returned;
	}

	public FIBNumericFunction newFIBNumericFunction(FIBGraph graph) {
		FIBNumericFunction returned = newInstance(FIBNumericFunction.class);
		returned.setMinValue(new DataBinding<Double>(FIBNumericFunction.DEFAULT_MIN_VALUE.toString()));
		returned.setMaxValue(new DataBinding<Double>(FIBNumericFunction.DEFAULT_MAX_VALUE.toString()));
		returned.setMajorTickSpacing(new DataBinding<Double>(FIBNumericFunction.DEFAULT_MAJOR_TICK_SPACING.toString()));
		returned.setMinorTickSpacing(new DataBinding<Double>(FIBNumericFunction.DEFAULT_MINOR_TICK_SPACING.toString()));
		returned.setStepsNumber(new DataBinding<Integer>(FIBNumericFunction.DEFAULT_STEPS_NUMBER.toString()));
		graph.addToFunctions(returned);
		return returned;
	}

	public FIBDiscreteFunction newFIBDiscreteFunction(FIBGraph graph) {
		FIBDiscreteFunction returned = newInstance(FIBDiscreteFunction.class);
		graph.addToFunctions(returned);
		return returned;
	}

}
