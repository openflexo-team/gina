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

package org.openflexo.replay.sampleData;

import javax.swing.Icon;

import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.replay.sampleData.Family.Gender;

public class Person extends SampleData {

	private String firstName;
	private String lastName;
	private int age;
	private Gender gender;

	public Person(String firstName, String lastName, int age, Gender gender) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.gender = gender;
	}

	@Override
	public String toString() {
		return firstName + " " + lastName + " aged " + age + " (" + gender + ")";
	}

	public Icon getIcon() {
		return UtilsIconLibrary.OK_ICON;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		String oldFirstName = this.firstName;
		this.firstName = firstName;
		//System.out.println("Notify firstName changed from " + oldFirstName + " to " + firstName);
		getPropertyChangeSupport().firePropertyChange("firstName", oldFirstName, firstName);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		String oldLastName = this.lastName;
		this.lastName = lastName;
		getPropertyChangeSupport().firePropertyChange("lastName", oldLastName, lastName);
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		Gender oldGender = this.gender;
		this.gender = gender;
		getPropertyChangeSupport().firePropertyChange("gender", oldGender, gender);
	}
}
