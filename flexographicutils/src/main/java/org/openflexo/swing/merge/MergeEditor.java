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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.diff.merge.Merge;
import org.openflexo.swing.merge.MergePanelElements.ComparePanel;

public abstract class MergeEditor extends JLayeredPane {

	MergePanelElements mergePanelElements;
	Merge _report;
	MergePanelElements.FilterChangeList changesList;
	ComparePanel comparePanel;
	JPanel controlPanel;

	public MergeEditor(Merge merge) {
		super();
		_report = merge;

		setLayout(new BorderLayout());

		mergePanelElements = new MergePanelElements(merge);
		changesList = mergePanelElements.getChangesList();
		changesList.setVisibleRowCount(5);
		ComparePanel comparePanel = mergePanelElements.getComparePanel();
		JScrollPane mergeTextArea = mergePanelElements.getMergePanel();

		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("Merge panel", SwingConstants.CENTER);

		topPanel.add(title, BorderLayout.NORTH);
		topPanel.add(changesList, BorderLayout.CENTER);

		add(topPanel, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mergeTextArea, comparePanel);
		Dimension dim = mergeTextArea.getPreferredSize();
		dim.height = 280;
		mergeTextArea.setPreferredSize(dim);
		Dimension dim2 = comparePanel.getPreferredSize();
		dim2.height = 300;
		comparePanel.setPreferredSize(dim2);
		splitPane.setDividerLocation(0.5);

		add(splitPane, BorderLayout.CENTER);

		controlPanel = mergePanelElements.getControlPanel();
		add(controlPanel, BorderLayout.SOUTH);

		JButton doneButton = new JButton();
		doneButton.setText("Done");
		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				done();
			}
		});

		controlPanel.add(doneButton);
		validate();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (_report.getChanges().size() > 0) {
					mergePanelElements.selectChange(_report.getChanges().firstElement());
				}
			}
		});
	}

	public abstract void done();

}
