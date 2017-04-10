/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.gina.swing.editor.test;

import java.util.Observable;
import java.util.Vector;

public class Coucou extends Observable {
	private String name;
	public String description;
	private Vector<Prout> prouts;
	public boolean showProuts = false;

	public byte testByte = (byte) 127;
	public short testShort = 10;
	public int testInteger = 1089;
	public long testLong = 1099886657;
	public float testFloat = 98873.7812873f;
	public double testDouble = 1.9823983276276;

	public Coucou() {
		name = "coucou";
		description = "une description qui decrit ce que c'est";
		prouts = new Vector<>();
		createNewProut();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		System.out.println("Name: " + name);
		this.name = name;
		setChanged();
		notifyObservers();
	}

	public Vector<Prout> getProuts() {
		return prouts;
	}

	public void setProuts(Vector<Prout> prouts) {
		this.prouts = prouts;
	}

	public void addToProuts(Prout aProut) {
		prouts.add(aProut);
	}

	public void removeFromProuts(Prout aProut) {
		prouts.remove(aProut);
	}

	public Prout createNewProut() {
		Prout returned = new Prout("hop");
		addToProuts(returned);
		return returned;
	}

	public void deleteProut(Prout prout) {
		removeFromProuts(prout);
	}

	public boolean isProutAddable() {
		return true;
	}

	public boolean isProutDeletable(Prout aProut) {
		return true;
	}

}
