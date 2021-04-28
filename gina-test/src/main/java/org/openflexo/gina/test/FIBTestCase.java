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

package org.openflexo.gina.test;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.BeforeClass;
import org.openflexo.connie.binding.javareflect.KeyValueLibrary;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElementChildren;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.model.widget.FIBDropDownColumn;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBLabelColumn;
import org.openflexo.gina.model.widget.FIBList;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBNumberColumn;
import org.openflexo.gina.model.widget.FIBRadioButtonList;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.model.widget.FIBTextFieldColumn;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.exceptions.ModelDefinitionException;

/**
 * Provides a JUnit 4 generic environment for FIB testing purposes
 * 
 */
public abstract class FIBTestCase {

	private static final Logger logger = FlexoLogger.getLogger(FIBTestCase.class.getPackage().getName());

	protected static FIBModelFactory factory;

	@BeforeClass
	public static void setUpClass() {
		try {
			factory = new FIBModelFactory(null);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
	}

	public FIBPanel newFIBPanel() {
		return factory.newInstance(FIBPanel.class);
	}

	public FIBTabPanel newFIBTabPanel() {
		return factory.newInstance(FIBTabPanel.class);
	}

	public FIBTab newFIBTab() {
		return factory.newInstance(FIBTab.class);
	}

	public FIBLabel newFIBLabel() {
		return factory.newInstance(FIBLabel.class);
	}

	public FIBLabel newFIBLabel(String label) {
		FIBLabel returned = factory.newInstance(FIBLabel.class);
		returned.setLabel(label);
		return returned;
	}

	public FIBTextField newFIBTextField() {
		return factory.newInstance(FIBTextField.class);
	}

	public FIBBrowser newFIBBrowser() {
		return factory.newInstance(FIBBrowser.class);
	}

	public FIBBrowserElement newFIBBrowserElement() {
		return factory.newInstance(FIBBrowserElement.class);
	}

	public FIBBrowserElementChildren newFIBBrowserElementChildren() {
		return factory.newInstance(FIBBrowserElementChildren.class);
	}

	public FIBCheckBox newFIBCheckBox() {
		return factory.newInstance(FIBCheckBox.class);
	}

	public FIBCheckboxList newFIBCheckboxList() {
		return factory.newInstance(FIBCheckboxList.class);
	}

	public FIBDropDown newFIBDropDown() {
		return factory.newInstance(FIBDropDown.class);
	}

	public FIBList newFIBList() {
		return factory.newInstance(FIBList.class);
	}

	public FIBNumber newFIBNumber() {
		return factory.newInstance(FIBNumber.class);
	}

	public FIBRadioButtonList newFIBRadioButtonList() {
		return factory.newInstance(FIBRadioButtonList.class);
	}

	public FIBNumberColumn newFIBNumberColumn() {
		return factory.newInstance(FIBNumberColumn.class);
	}

	public FIBLabelColumn newFIBLabelColumn() {
		return factory.newInstance(FIBLabelColumn.class);
	}

	public FIBDropDownColumn newFIBDropDownColumn() {
		return factory.newInstance(FIBDropDownColumn.class);
	}

	public FIBTable newFIBTable() {
		return factory.newInstance(FIBTable.class);
	}

	public FIBTextFieldColumn newFIBTextFieldColumn() {
		return factory.newInstance(FIBTextFieldColumn.class);
	}

	public FIBTextArea newFIBTextArea() {
		return factory.newInstance(FIBTextArea.class);
	}

	protected void log(String step) {
		logger.info("\n******************************************************************************\n" + step
				+ "\n******************************************************************************\n");
	}

	@After
	public void tearDown() throws Exception {
		KeyValueLibrary.clearCache();
	}
}
