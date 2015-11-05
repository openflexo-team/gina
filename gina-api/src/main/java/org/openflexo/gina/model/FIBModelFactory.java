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

import java.io.File;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.converter.ComponentConstraintsConverter;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBCustom;
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
import org.openflexo.gina.model.widget.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomAssignment;
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
