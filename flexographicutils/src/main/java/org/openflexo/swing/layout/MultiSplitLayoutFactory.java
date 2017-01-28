/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.swing.layout;

import org.openflexo.swing.layout.MultiSplitLayout.ColSplit;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultColSplit;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultDivider;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultLeaf;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultRowSplit;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.RowSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Split;

public interface MultiSplitLayoutFactory {

	public <N extends Divider<N>> Divider<N> makeDivider();

	public <N extends Leaf<N>> Leaf<N> makeLeaf();

	public <N extends Leaf<N>> Leaf<N> makeLeaf(String name);

	public <N extends Split<N>> Split<N> makeSplit();

	public <N extends ColSplit<N>> ColSplit<?> makeColSplit();

	public <N extends ColSplit<N>> ColSplit<?> makeColSplit(Leaf<?> top, Divider<?> divider, Leaf<?> bottom);

	public <N extends RowSplit<N>> RowSplit<?> makeRowSplit();

	public <N extends RowSplit<N>> RowSplit<?> makeRowSplit(Leaf<?> left, Divider<?> divider, Leaf<?> right);

	public static class DefaultMultiSplitLayoutFactory implements MultiSplitLayoutFactory {

		@Override
		public <N extends Divider<N>> Divider<N> makeDivider() {
			return new DefaultDivider<>();
		}

		@Override
		public <N extends Leaf<N>> Leaf<N> makeLeaf() {
			return new DefaultLeaf<>();
		}

		@Override
		public <N extends Leaf<N>> Leaf<N> makeLeaf(String name) {
			return new DefaultLeaf<>(name);
		}

		@Override
		public <N extends Split<N>> Split<N> makeSplit() {
			return new DefaultSplit<>();
		}

		@Override
		public <N extends ColSplit<N>> ColSplit<N> makeColSplit() {
			return new DefaultColSplit<>();
		}

		@Override
		public <N extends ColSplit<N>> ColSplit<N> makeColSplit(Leaf<?> top, Divider<?> divider, Leaf<?> bottom) {
			return new DefaultColSplit(top, divider, bottom);
		}

		@Override
		public <N extends RowSplit<N>> RowSplit<N> makeRowSplit() {
			return new DefaultRowSplit<>();
		}

		@Override
		public RowSplit<?> makeRowSplit(Leaf left, Divider divider, Leaf right) {
			return new DefaultRowSplit(left, divider, right);
		}

	}

}
