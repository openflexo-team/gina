/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.model;

import java.io.File;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.fib.model.FIBCustom.FIBCustomAssignment;
import org.openflexo.fib.model.converter.ComponentConstraintsConverter;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.converter.DataBindingConverter;
import org.openflexo.model.converter.RelativePathFileConverter;
import org.openflexo.model.converter.ResourceLocationConverter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * {@link ModelFactory} used to handle FIB models<br>
 * One instance is declared for the {@link FIBLibrary}
 * 
 * @author sylvain
 * 
 */
public class FIBModelFactory extends ModelFactory {

	private final FIBValidationModel validationModel;

	public FIBModelFactory() throws ModelDefinitionException {
		super(ModelContextLibrary.getModelContext(FIBComponent.class));
		addConverter(new DataBindingConverter());
		addConverter(new ComponentConstraintsConverter());
		addConverter(new ResourceLocationConverter());
		validationModel = new FIBValidationModel(this);
	}

	public FIBModelFactory(Class<?>... additionalClasses) throws ModelDefinitionException {
		super(ModelContextLibrary.getCompoundModelContext(FIBComponent.class, additionalClasses));
		addConverter(new DataBindingConverter());
		addConverter(new ComponentConstraintsConverter());
		validationModel = new FIBValidationModel(this);
	}

	public FIBModelFactory(File relativePath) throws ModelDefinitionException {
		this();
		addConverter(new RelativePathFileConverter(relativePath));
	}

	public FIBValidationModel getValidationModel() {
		return validationModel;
	}

	public FIBPanel newFIBPanel() {
		return newInstance(FIBPanel.class);
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

}
