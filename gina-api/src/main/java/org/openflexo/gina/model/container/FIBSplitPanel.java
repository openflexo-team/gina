/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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
import java.util.Vector;

import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBProperty;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBColSplit;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBDivider;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBLeaf;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBNode;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBRowSplit;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBSplit;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.swing.layout.MultiSplitLayout.ColSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.swing.layout.MultiSplitLayout.RowSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Split;

@ModelEntity
@ImplementationClass(FIBSplitPanel.FIBSplitPanelImpl.class)
@XMLElement(xmlTag = "SplitPanel")
@Imports({ @Import(FIBSplit.class), @Import(FIBLeaf.class), @Import(FIBDivider.class), @Import(FIBColSplit.class),
		@Import(FIBRowSplit.class) })
public interface FIBSplitPanel extends FIBContainer {

	@PropertyIdentifier(type = FIBSplit.class)
	public static final String SPLIT_KEY = "split";

	@Getter(value = SPLIT_KEY)
	@XMLElement
	public FIBSplit getSplit();

	@Setter(SPLIT_KEY)
	public void setSplit(FIBSplit split);

	public String getFirstEmptyPlaceHolder();

	public List<Leaf> getAllLeaves();

	public Leaf getLeafNamed(String aName);

	public void notifySplitLayoutChange();

	public FIBMultiSplitLayoutFactory getSplitLayoutFactory();

	public void makeDefaultHorizontalLayout();

	public void makeDefaultVerticalLayout();

	public Divider addDivider(Split parent);

	public Leaf addLeaf(Split parent);

	public ColSplit addVerticalSplit(Split parent);

	public RowSplit addHorizontalSplit(Split parent);

	public ColSplit addDefaultVerticalSplit(Split parent);

	public RowSplit addDefaultHorizontalSplit(Split parent);

	public Node removeNode(Node node);

	public static abstract class FIBSplitPanelImpl extends FIBContainerImpl implements FIBSplitPanel {

		public static final String LEFT = "left";
		public static final String RIGHT = "right";
		public static final String TOP = "top";
		public static final String BOTTOM = "bottom";

		private FIBSplit split;

		private final FIBMultiSplitLayoutFactory splitLayoutFactory = new FIBMultiSplitLayoutFactory(getFactory());

		@Override
		public FIBMultiSplitLayoutFactory getSplitLayoutFactory() {
			return splitLayoutFactory;
		}

		/*
		 * @Override public Layout getLayout() { return Layout.split; }
		 */

		/*@Override
		public String getIdentifier() {
			return null;
		}*/

		@Override
		public String getFirstEmptyPlaceHolder() {
			if (getAllLeaves().size() > 0) {
				return getAllLeaves().get(0).getName();
			}
			return "leaf";
		}

		@Override
		public FIBSplit getSplit() {
			if (split == null) {
				split = getDefaultHorizontalLayout();
			}
			return split;
		}

		@Override
		public void setSplit(FIBSplit split) {
			FIBPropertyNotification<FIBSplit> notification = requireChange(SPLIT_KEY, split);
			if (notification != null) {
				this.split = split;
				hasChanged(notification);
			}
		}

		protected FIBRowSplit getDefaultHorizontalLayout() {
			Leaf left = splitLayoutFactory.makeLeaf(findNextAvailableLeaf(LEFT));
			left.setWeight(0.5);
			Leaf right = splitLayoutFactory.makeLeaf(findNextAvailableLeaf(RIGHT));
			right.setWeight(0.5);
			return splitLayoutFactory.makeRowSplit(left, splitLayoutFactory.makeDivider(), right);
		}

		protected FIBColSplit getDefaultVerticalLayout() {
			Leaf left = splitLayoutFactory.makeLeaf(findNextAvailableLeaf(TOP));
			left.setWeight(0.5);
			Leaf right = splitLayoutFactory.makeLeaf(findNextAvailableLeaf(BOTTOM));
			right.setWeight(0.5);
			return splitLayoutFactory.makeColSplit(left, splitLayoutFactory.makeDivider(), right);
		}

		@Override
		public void makeDefaultHorizontalLayout() {
			setSplit(getDefaultHorizontalLayout());
		}

		@Override
		public void makeDefaultVerticalLayout() {
			setSplit(getDefaultVerticalLayout());
		}

		@Override
		public Divider addDivider(Split parent) {
			Divider returned = splitLayoutFactory.makeDivider();
			parent.addToChildren(returned);
			notifySplitLayoutChange();
			return returned;
		}

		@Override
		public Leaf addLeaf(Split parent) {
			Leaf returned = splitLayoutFactory.makeLeaf(findNextAvailableLeaf("leaf"));
			parent.addToChildren(returned);
			notifySplitLayoutChange();
			return returned;
		}

		@Override
		public ColSplit addVerticalSplit(Split parent) {
			ColSplit returned = splitLayoutFactory.makeColSplit();
			parent.addToChildren(returned);
			notifySplitLayoutChange();
			return returned;
		}

		@Override
		public RowSplit addHorizontalSplit(Split parent) {
			RowSplit returned = splitLayoutFactory.makeRowSplit();
			parent.addToChildren(returned);
			notifySplitLayoutChange();
			return returned;
		}

		@Override
		public ColSplit addDefaultVerticalSplit(Split parent) {
			ColSplit returned = getDefaultVerticalLayout();
			parent.addToChildren(returned);
			notifySplitLayoutChange();
			return returned;
		}

		@Override
		public RowSplit addDefaultHorizontalSplit(Split parent) {
			RowSplit returned = getDefaultHorizontalLayout();
			parent.addToChildren(returned);
			notifySplitLayoutChange();
			return returned;
		}

		@Override
		public Node removeNode(Node node) {
			if (node != getSplit()) {
				node.getParent().removeFromChildren(node);
				notifySplitLayoutChange();
			}
			return node;
		}

		@Override
		public void notifySplitLayoutChange() {
			FIBPropertyNotification<Split> notification = new FIBPropertyNotification<Split>(
					(FIBProperty<Split>) FIBProperty.getFIBProperty(getClass(), SPLIT_KEY), null, split);
			hasChanged(notification);
		}

		@Override
		public List<Leaf> getAllLeaves() {
			Vector<Leaf> returned = new Vector<Leaf>();
			appendToLeaves(getSplit(), returned);
			return returned;
		}

		private void appendToLeaves(Node n, List<Leaf> returned) {
			if (n instanceof Leaf) {
				returned.add((Leaf) n);
			}
			else if (n instanceof FIBSplit) {
				for (FIBNode n2 : ((FIBSplit<?>) n).getChildren()) {
					appendToLeaves(n2, returned);
				}
			}
		}

		@Override
		public Leaf getLeafNamed(String aName) {
			for (Leaf l : getAllLeaves()) {
				if (l.getName().equals(aName)) {
					return l;
				}
			}
			return null;
		}

		public String findNextAvailableLeaf(String baseName) {
			if (split == null) {
				return baseName;
			}
			int i = 2;
			String tryMe = baseName;
			while (getLeafNamed(tryMe) != null) {
				tryMe = baseName + i;
				i++;
			}
			return tryMe;
		}

	}
}
