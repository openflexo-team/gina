/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.gina.sampleData;

import java.util.Vector;

import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class Family extends SampleData {

	private Person father;
	private Person mother;

	private Person biggestChild;
	private Person biggestParent;

	private Person[] parents;

	private Vector<Person> children;
	private Vector<Person> jackies;

	public enum Gender {
		Male, Female
	}

	public Family() {

		super();

		father = new Person("Robert", "Smith", 39, Gender.Male);
		mother = new Person("Mary", "Smith", 37, Gender.Female);
		parents = new Person[2];
		parents[0] = father;
		parents[1] = mother;

		biggestParent = father;

		Person jacky1, jacky2, jacky3;

		children = new Vector<Person>();
		children.add(new Person("John", "Smith", 9, Gender.Male));
		children.add(new Person("Suzy", "Smith", 3, Gender.Female));
		children.add(jacky1 = new Person("Jacky1", "Smith", 2, Gender.Male));
		children.add(jacky2 = new Person("Jacky2", "Smith", 3, Gender.Male));
		children.add(jacky3 = new Person("Jacky3", "Smith", 4, Gender.Male));

		jackies = new Vector<Person>();
		jackies.add(jacky1);
		jackies.add(jacky2);
		jackies.add(jacky3);

		biggestChild = jacky3;

	}

	public Vector<Person> getChildren() {
		return children;
	}

	public void setChildren(Vector<Person> children) {
		System.out.println("??????????? setChildren with " + children);
		this.children = children;
	}

	public Person createChild() {
		Person newChild = new Person("John Jr", "Smith", 0, Gender.Male);
		children.add(newChild);
		getPropertyChangeSupport().firePropertyChange("children", null, newChild);
		return newChild;
	}

	public Person deleteChild(Person childToDelete) {
		children.remove(childToDelete);
		return childToDelete;
	}

	@Override
	public String toString() {
		return super.toString() + " children=" + Integer.toHexString(children.hashCode()) + " : " + children;
	}

	public Resource fibForPerson(Person person) {

		if (person == null) {
			return null;
		}
		if (person.getGender() == Gender.Male) {
			return ResourceLocator.locateResource("TestFIB/TestMalePerson.fib");
		}
		else if (person.getGender() == Gender.Female) {
			return ResourceLocator.locateResource("TestFIB/TestFemalePerson.fib");
		}
		return null;
	}

	public Person getFather() {
		return father;
	}

	public void setFather(Person father) {
		Person oldFather = this.father;
		this.father = father;
		getPropertyChangeSupport().firePropertyChange("father", oldFather, father);
	}

	public Person getMother() {
		return mother;
	}

	public void setMother(Person mother) {
		Person oldMother = this.mother;
		this.mother = mother;
		getPropertyChangeSupport().firePropertyChange("mother", oldMother, mother);
	}

	public Person getBiggestChild() {
		return biggestChild;
	}

	public void setBiggestChild(Person biggestChild) {
		Person oldBiggestChild = this.biggestChild;
		this.biggestChild = biggestChild;
		getPropertyChangeSupport().firePropertyChange("biggestChild", oldBiggestChild, biggestChild);
	}

	public Person getBiggestParent() {
		return biggestParent;
	}

	public void setBiggestParent(Person biggestParent) {
		Person oldBiggestParent = this.biggestParent;
		this.biggestParent = biggestParent;
		getPropertyChangeSupport().firePropertyChange("biggestParent", oldBiggestParent, biggestParent);
	}

	public Person[] getParents() {
		return parents;
	}

	public void setParents(Person[] parents) {
		this.parents = parents;
	}

	public Vector<Person> getJackies() {
		return jackies;
	}

	public void addToJackies(Person aNewJacky) {
		jackies.add(aNewJacky);
		getPropertyChangeSupport().firePropertyChange("jackies", null, aNewJacky);
	}

	public void setJackies(Vector<Person> jackies) {
		this.jackies = jackies;
	}

}
