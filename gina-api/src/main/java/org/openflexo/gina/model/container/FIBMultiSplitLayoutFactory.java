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

package org.openflexo.gina.model.container;

import java.util.List;

import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.swing.layout.MultiSplitLayout;
import org.openflexo.swing.layout.MultiSplitLayout.ColSplit;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultColSplit;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultRowSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.swing.layout.MultiSplitLayout.RowSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Split;
import org.openflexo.swing.layout.MultiSplitLayoutFactory;

public class FIBMultiSplitLayoutFactory implements MultiSplitLayoutFactory {

	private final FIBModelFactory factory;

	public FIBMultiSplitLayoutFactory(FIBModelFactory factory) {
		this.factory = factory;
	}

	@Override
	public FIBDivider makeDivider() {
		return factory.newInstance(FIBDivider.class);
	}

	@Override
	public FIBLeaf makeLeaf() {
		return factory.newInstance(FIBLeaf.class);
	}

	@Override
	public FIBLeaf makeLeaf(String name) {
		return factory.newInstance(FIBLeaf.class);
	}

	@Override
	public FIBSplit makeSplit() {
		return factory.newInstance(FIBSplit.class);
	}

	@Override
	public FIBColSplit makeColSplit() {
		return factory.newInstance(FIBColSplit.class);
	}

	@Override
	public FIBRowSplit makeRowSplit() {
		return factory.newInstance(FIBRowSplit.class);
	}

	@Override
	public FIBRowSplit makeRowSplit(Leaf left, Divider divider, Leaf right) {
		return factory.newInstance(FIBRowSplit.class);
	}

	@Override
	public FIBColSplit makeColSplit(Leaf top, Divider divider, Leaf bottom) {
		return factory.newInstance(FIBColSplit.class);
	}

	/**
	 * API for the nodes that model a MultiSplitLayout.
	 */
	@ModelEntity(isAbstract = true)
	@ImplementationClass(FIBNode.FIBNodeImpl.class)
	public static interface FIBNode<N extends FIBNode<N>> extends Node<N> {

		@SuppressWarnings("serial")
		public static abstract class FIBNodeImpl<N extends FIBNode<N>> extends MultiSplitLayout.DefaultNode<N> implements FIBNode<N> {

		}
	}

	/**
	 * Models a single vertical/horizontal divider.
	 */
	@ModelEntity
	@ImplementationClass(FIBDivider.FIBDividerImpl.class)
	@XMLElement(xmlTag = "Divider")
	public static interface FIBDivider<N extends FIBDivider<N>> extends FIBNode<N>, Divider<N> {

		@SuppressWarnings("serial")
		public static abstract class FIBDividerImpl<N extends FIBDivider<N>> extends MultiSplitLayout.DefaultDivider<N>
				implements FIBDivider<N> {

		}
	}

	/**
	 * Models a java.awt Component child.
	 */
	@ModelEntity
	@ImplementationClass(FIBLeaf.FIBLeafImpl.class)
	@XMLElement(xmlTag = "Leaf")
	public static interface FIBLeaf extends FIBNode<FIBLeaf>, Leaf<FIBLeaf> {

		@PropertyIdentifier(type = String.class)
		public static final String NAME_KEY = "name";

		@PropertyIdentifier(type = double.class)
		public static final String WEIGHT_KEY = "weight";

		@Override
		@Getter(NAME_KEY)
		@XMLAttribute
		public String getName();

		@Override
		@Setter(NAME_KEY)
		public void setName(String name);

		@Override
		@Getter(value = WEIGHT_KEY, defaultValue = "0.5")
		@XMLAttribute
		public double getWeight();

		@Override
		@Setter(WEIGHT_KEY)
		public void setWeight(double weight);

		@SuppressWarnings("serial")
		public static abstract class FIBLeafImpl extends MultiSplitLayout.DefaultLeaf<FIBLeaf> implements FIBLeaf {
		}
	}

	/**
	 * Defines a vertical or horizontal subdivision into two or more tiles.
	 */
	@ModelEntity(isAbstract = true)
	@ImplementationClass(FIBSplit.FIBSplitImpl.class)
	public static interface FIBSplit<N extends FIBNode<N>> extends FIBNode<N>, Split<N> {

		@PropertyIdentifier(type = String.class)
		public static final String NAME_KEY = "name";

		@PropertyIdentifier(type = double.class)
		public static final String WEIGHT_KEY = "weight";

		@PropertyIdentifier(type = List.class)
		public static final String CHILDREN_KEY = "children";

		@Override
		@Getter(NAME_KEY)
		@XMLAttribute
		public String getName();

		@Override
		@Setter(NAME_KEY)
		public void setName(String name);

		@Override
		@Getter(value = WEIGHT_KEY, defaultValue = "0.5")
		@XMLAttribute
		public double getWeight();

		@Override
		@Setter(WEIGHT_KEY)
		public void setWeight(double weight);

		@Override
		@Getter(value = CHILDREN_KEY, cardinality = Cardinality.LIST)
		@XMLElement
		@CloningStrategy(StrategyType.CLONE)
		public List<N> getChildren();

		@Override
		@Setter(CHILDREN_KEY)
		public void setChildren(List<N> children);

		@Override
		@Adder(CHILDREN_KEY)
		public void addToChildren(N child);

		@Override
		@Remover(CHILDREN_KEY)
		public void removeFromChildren(N child);

		@SuppressWarnings("serial")
		public static abstract class FIBSplitImpl<N extends FIBNode<N>> extends MultiSplitLayout.DefaultSplit<N> implements FIBSplit<N> {

			@Override
			public String toString() {
				return getClass().getSimpleName() + ":" + super.toString();
			}
		}

	}

	/**
	 * Defines a horizontal subdivision into two or more tiles.
	 */
	@ModelEntity
	@ImplementationClass(FIBRowSplit.FIBRowSplitImpl.class)
	@XMLElement(xmlTag = "RowSplit")
	public static interface FIBRowSplit<N extends FIBNode<N>> extends FIBSplit<N>, RowSplit<N> {

		public static abstract class FIBRowSplitImpl<N extends FIBNode<N>> extends DefaultRowSplit<N> implements FIBRowSplit<N> {

		}
	}

	/**
	 * Defines a vertical subdivision into two or more tiles.
	 */
	@ModelEntity
	@ImplementationClass(FIBColSplit.FIBColSplitImpl.class)
	@XMLElement(xmlTag = "ColSplit")
	public static interface FIBColSplit<N extends FIBNode<N>> extends FIBSplit<N>, ColSplit<N> {

		public static abstract class FIBColSplitImpl<N extends FIBNode<N>> extends DefaultColSplit<N> implements FIBColSplit<N> {

		}
	}

}
