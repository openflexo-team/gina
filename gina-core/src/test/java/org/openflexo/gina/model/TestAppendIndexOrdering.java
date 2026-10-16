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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.test.FIBTestCase;

/**
 * Order produced by {@link FIBContainer#append(FIBContainer)} when widgets carry an index.
 * 
 * <p>
 * This is the mechanism that composes the inspector of a concept with those of its ancestors: the ancestors are appended first, then the
 * concept's own, and the <code>index</code> of the widgets decides the order. The cases below are the conventions authors rely on:
 * <ul>
 * <li>a widget WITHOUT index goes after every negative index, in append order (ancestors first);</li>
 * <li>a negative index goes before everything else;</li>
 * <li>on EQUAL positive indexes, the one appended first (the ancestor's) stays first;</li>
 * <li>widgets sharing an index (a label and its widget) stay adjacent.</li>
 * </ul>
 * 
 * @author sylvain
 */
public class TestAppendIndexOrdering extends FIBTestCase {

	/** Name followed by an optional index: <code>layer("a", 1, "b", null)</code> */
	private FIBPanel layer(Object... nameAndIndex) {
		FIBPanel returned = newFIBPanel();
		for (int i = 0; i < nameAndIndex.length; i += 2) {
			FIBLabel label = newFIBLabel((String) nameAndIndex[i]);
			label.setName((String) nameAndIndex[i]);
			returned.addToSubComponents(label);
			label.setIndex((Integer) nameAndIndex[i + 1]);
		}
		return returned;
	}

	private List<String> compose(FIBPanel... layersAncestorsFirst) {
		FIBPanel target = newFIBPanel();
		for (FIBPanel layer : layersAncestorsFirst) {
			target.append(layer);
		}
		List<String> returned = new ArrayList<>();
		for (FIBComponent c : target.getSubComponents()) {
			returned.add(c.getName());
		}
		return returned;
	}

	@Test
	public void childContinuesAfterParent() {
		assertEquals(Arrays.asList("p1", "p2", "c1"), compose(layer("p1", 1, "p2", 2), layer("c1", 3)));
	}

	@Test
	public void childInsertsBetweenParentWidgets() {
		assertEquals(Arrays.asList("p1", "c1", "p2"), compose(layer("p1", 100, "p2", 200), layer("c1", 150)));
	}

	@Test
	public void childInsertsBeforeParent() {
		assertEquals(Arrays.asList("c1", "p1"), compose(layer("p1", 5), layer("c1", 2)));
	}

	@Test
	public void equalPositiveIndexesKeepAncestorFirst() {
		assertEquals(Arrays.asList("p1", "c1"), compose(layer("p1", 1), layer("c1", 1)));
	}

	@Test
	public void equalPositiveIndexesAcrossThreeGenerations() {
		assertEquals(Arrays.asList("g", "p", "c"), compose(layer("g", 1), layer("p", 1), layer("c", 1)));
	}

	@Test
	public void widgetsWithoutIndexKeepAppendOrder() {
		assertEquals(Arrays.asList("p1", "p2", "c1", "c2"),
				compose(layer("p1", null, "p2", null), layer("c1", null, "c2", null)));
	}

	@Test
	public void widgetsWithoutIndexGoAfterIndexedOnes() {
		// Policy documented on append(): no index -> after every negative index. Indexed positive widgets are not "before" it.
		assertEquals(Arrays.asList("n", "p1", "c1"), compose(layer("n", -1, "p1", null), layer("c1", null)));
	}

	@Test
	public void negativeIndexGoesFirst() {
		assertEquals(Arrays.asList("c1", "p1", "p2"), compose(layer("p1", 1, "p2", 2), layer("c1", -1)));
	}

	@Test
	public void negativeIndexesAreOrdered() {
		assertEquals(Arrays.asList("c2", "c1", "p1"), compose(layer("p1", 1), layer("c1", -1, "c2", -2)));
	}

	@Test
	public void sameIndexWidgetsStayAdjacent() {
		// A label and its widget carry the same index: a widget of the child inserted between would split them
		assertEquals(Arrays.asList("p1l", "p1w", "c1l", "c1w", "p2l", "p2w"),
				compose(layer("p1l", 100, "p1w", 100, "p2l", 300, "p2w", 300), layer("c1l", 200, "c1w", 200)));
	}

	@Test
	public void equalIndexGroupsStayAdjacentAndAncestorFirst() {
		assertEquals(Arrays.asList("pl", "pw", "cl", "cw"), compose(layer("pl", 1, "pw", 1), layer("cl", 1, "cw", 1)));
	}

	@Test
	public void unindexedWidgetGoesBeforePositivesOfTheAncestor() {
		assertEquals(Arrays.asList("c", "p1", "p2"), compose(layer("p1", 1, "p2", 2), layer("c", null)));
	}

	@Test
	public void layerMixingIndexedAndUnindexedWidgets() {
		// grandName (100) equal to the parent's, then grandNote with no index: the latter goes first, before every positive
		assertEquals(Arrays.asList("gn", "p1", "g1"), compose(layer("p1", 100), layer("g1", 100, "gn", null)));
	}
}
