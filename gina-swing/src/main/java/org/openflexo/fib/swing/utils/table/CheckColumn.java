/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

package org.openflexo.fib.swing.utils.table;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class CheckColumn<D> extends AbstractColumn<D, Boolean> implements EditableColumn<D, Boolean> {

	public CheckColumn(String title, int defaultWidth) {
		super(title, defaultWidth, false);
	}

	@Override
	public String getLocalizedTitle() {
		return " ";
	}

	@Override
	public Class getValueClass() {
		return Boolean.class;
	}

	@Override
	public Boolean getValueFor(D object) {
		return getBooleanValue(object);
	}

	public abstract Boolean getBooleanValue(D object);

	public abstract void setBooleanValue(D object, Boolean aBoolean);

	@Override
	public String toString() {
		return "CheckColumn " + "[" + getTitle() + "]" + Integer.toHexString(hashCode());
	}

	/**
	 * No custom cell renderer: use JTable default one
	 * 
	 * @return
	 */
	@Override
	public boolean requireCellRenderer() {
		return false;
	}

	@Override
	public boolean isCellEditableFor(D object) {
		return true;
	}

	@Override
	public void setValueFor(D object, Boolean value) {
		setBooleanValue(object, value);
		valueChanged(object, value);
	}

}
