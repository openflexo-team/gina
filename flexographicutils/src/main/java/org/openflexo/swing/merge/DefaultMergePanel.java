/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.swing.merge;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.diff.merge.IMerge;
import org.openflexo.diff.merge.Merge;
import org.openflexo.swing.merge.MergePanelElements.ComparePanel;
import org.openflexo.toolbox.TokenMarkerStyle;

public class DefaultMergePanel extends JLayeredPane {

	private MergePanelElements mergePanelElements;
	private Merge _merge;
	JList<?> changesList;
	ComparePanel comparePanel;

	public DefaultMergePanel(Merge merge, TokenMarkerStyle style) {
		super();
		_merge = merge;

		setLayout(new BorderLayout());

		mergePanelElements = new MergePanelElements(merge, style);
		MergePanelElements.FilterChangeList changesList = mergePanelElements.getChangesList();
		changesList.setVisibleRowCount(10);
		ComparePanel comparePanel = mergePanelElements.getComparePanel();

		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("Merge panel", SwingConstants.CENTER);

		topPanel.add(title, BorderLayout.NORTH);
		topPanel.add(changesList, BorderLayout.CENTER);

		add(topPanel, BorderLayout.NORTH);
		add(comparePanel, BorderLayout.CENTER);

		if (_merge.getChanges().size() > 0) {
			mergePanelElements.selectChange(_merge.getChanges().firstElement());
		}
	}

	public IMerge getMerge() {
		return mergePanelElements.getMerge();
	}

}
