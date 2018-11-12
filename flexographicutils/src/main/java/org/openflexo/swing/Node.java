/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Flexographicutils, a component of the software infrastructure 
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

package org.openflexo.swing;

import java.util.List;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.Getter.Cardinality;

@ModelEntity
@XMLElement(xmlTag = "Node")
public interface Node {

	public static final String NAME = "name";
	public static final String VISIBLE = "visible";
	public static final String ROW_LAYOUT = "rowLayout";
	public static final String PERCENTAGE = "percentage";
	public static final String CHILDREN = "children";
	public static final String PARENT = "parent";
	public static final String SIZE = "size";

	@Getter(NAME)
	@XMLAttribute(xmlTag = NAME)
	public String getName();

	@Setter(NAME)
	public void setName(String name);

	@Getter(value = VISIBLE, defaultValue = "true")
	@XMLAttribute(xmlTag = VISIBLE)
	public boolean isVisible();

	@Setter(VISIBLE)
	public void setVisible(boolean visible);

	@Getter(value = ROW_LAYOUT, defaultValue = "true")
	@XMLAttribute(xmlTag = ROW_LAYOUT)
	public boolean isRowLayout();

	@Setter(ROW_LAYOUT)
	public void setRowLayout(boolean rowLayout);

	@Getter(value = PERCENTAGE, defaultValue = "-1.0")
	@XMLAttribute(xmlTag = PERCENTAGE)
	public double getPercentage();

	@Setter(PERCENTAGE)
	public void setPercentage(double percentage);

	@Getter(value = SIZE, defaultValue = "-1")
	@XMLAttribute(xmlTag = SIZE)
	public int getSize();

	@Setter(SIZE)
	public void setSize(int size);

	@Getter(value = CHILDREN, cardinality = Cardinality.LIST, inverse = PARENT)
	@XMLElement(xmlTag = "child")
	public List<Node> getChildren();

	@Setter(CHILDREN)
	public void setChildren(List<Node> children);

	@Adder(CHILDREN)
	public void addChild(Node child);

	@Remover(CHILDREN)
	public void removeChild(Node child);

	@Getter(value = PARENT, inverse = CHILDREN)
	public Node getParent();

	@Setter(PARENT)
	public void setParent(Node parent);

}
