/**
 * Metaphase Editor - WYSIWYG HTML Editor Component
 * Copyright (C) 2010  Rudolf Visagie
 * Full text of license can be found in com/metaphaseeditor/LICENSE.txt
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author can be contacted at metaphase.editor@gmail.com.
 */

package com.metaphaseeditor;

import org.openflexo.rm.CompositeResourceLocatorImpl;
import org.openflexo.toolbox.ImageIconResource;

/**
 * 
 * @author Rudolf Visagie
 */
public class TableDialog extends javax.swing.JDialog {

	private String tableHtml;

	private static CompositeResourceLocatorImpl rl = CompositeResourceLocatorImpl.getResourceLocator();

	private enum Alignment {
		NONE("None", null), LEFT("Left", "left"), CENTER("Center", "center"), RIGHT("Right", "right");

		private String text;
		private String attrValue;

		Alignment(String text, String attrValue) {
			this.text = text;
			this.attrValue = attrValue;
		}

		public String getAttrValue() {
			return attrValue;
		}

		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return text;
		}
	};

	private enum WidthType {
		PIXELS("Pixels"), PERCENTAGE("Percentage");

		private String text;

		WidthType(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	private enum HeaderType {
		NONE("None"), FIRST_ROW("First Row"), FIRST_COLUMN("First Column"), BOTH("Both");

		private String text;

		HeaderType(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	/** Creates new form TableDialog */
	public TableDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		setIconImage(new ImageIconResource(rl.locateResource("Icons/MetaphaseEditor/icons/metaphase16x16.png")).getImage());

		setLocationRelativeTo(null);

		alignmentComboBox.removeAllItems();
		Alignment[] alignments = Alignment.values();
		for (int i = 0; i < alignments.length; i++) {
			alignmentComboBox.addItem(alignments[i]);
		}

		headersComboBox.removeAllItems();
		HeaderType[] headerTypes = HeaderType.values();
		for (int i = 0; i < headerTypes.length; i++) {
			headersComboBox.addItem(headerTypes[i]);
		}

		widthTypeComboBox.removeAllItems();
		WidthType[] widthTypes = WidthType.values();
		for (int i = 0; i < widthTypes.length; i++) {
			widthTypeComboBox.addItem(widthTypes[i]);
		}

		pack();
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
	 * method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		rowsLabel = new javax.swing.JLabel();
		rowsSpinner = new javax.swing.JSpinner();
		columnsLabel = new javax.swing.JLabel();
		columnsSpinner = new javax.swing.JSpinner();
		widthLabel = new javax.swing.JLabel();
		heightLabel = new javax.swing.JLabel();
		heightSpinner = new javax.swing.JSpinner();
		widthSpinner = new javax.swing.JSpinner();
		widthTypeComboBox = new javax.swing.JComboBox();
		headersLabel = new javax.swing.JLabel();
		headersComboBox = new javax.swing.JComboBox();
		cellPaddingLabel = new javax.swing.JLabel();
		cellPaddingSpinner = new javax.swing.JSpinner();
		cellSpacingLabel = new javax.swing.JLabel();
		cellSpacingSpinner = new javax.swing.JSpinner();
		borderSizeLabel = new javax.swing.JLabel();
		borderSizeSpinner = new javax.swing.JSpinner();
		alignmentLabel = new javax.swing.JLabel();
		alignmentComboBox = new javax.swing.JComboBox();
		captionLabel = new javax.swing.JLabel();
		captionTextField = new javax.swing.JTextField();
		summaryLabel = new javax.swing.JLabel();
		summaryTextField = new javax.swing.JTextField();
		cancelButton = new javax.swing.JButton();
		okButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Insert Table");

		rowsLabel.setText("Rows");

		rowsSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 50, 1));

		columnsLabel.setText("Columns");

		columnsSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 50, 1));

		widthLabel.setText("Width");

		heightLabel.setText("Height");

		heightSpinner.setModel(new javax.swing.SpinnerNumberModel(100, 1, 2000, 1));

		widthSpinner.setModel(new javax.swing.SpinnerNumberModel(200, 1, 2000, 1));

		widthTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pixels", "Percent" }));

		headersLabel.setText("Headers");

		headersComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "First Row", "First Column", "Both" }));

		cellPaddingLabel.setText("Cell Padding");

		cellPaddingSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(0), null, Integer.valueOf(1)));

		cellSpacingLabel.setText("Cell Spacing");

		cellSpacingSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(0), null, Integer.valueOf(1)));

		borderSizeLabel.setText("Border Size");

		borderSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(0), null, Integer.valueOf(1)));

		alignmentLabel.setText("Alignment");

		alignmentComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Left", "Center", "Right" }));

		captionLabel.setText("Caption");

		summaryLabel.setText("Summary");

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		okButton.setText("OK");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(headersLabel).addComponent(rowsLabel)
																				.addComponent(borderSizeLabel).addComponent(columnsLabel))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(borderSizeSpinner,
																						javax.swing.GroupLayout.PREFERRED_SIZE, 66,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addGroup(
																						layout.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								false)
																								.addComponent(
																										rowsSpinner,
																										javax.swing.GroupLayout.Alignment.LEADING)
																								.addComponent(
																										columnsSpinner,
																										javax.swing.GroupLayout.Alignment.LEADING,
																										javax.swing.GroupLayout.DEFAULT_SIZE,
																										64, Short.MAX_VALUE))
																				.addComponent(headersComboBox,
																						javax.swing.GroupLayout.PREFERRED_SIZE, 105,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																.addGap(18, 18, 18)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(widthLabel)
																				.addGroup(
																						layout.createSequentialGroup()
																								.addGroup(
																										layout.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.LEADING)
																												.addComponent(
																														cellPaddingLabel)
																												.addComponent(heightLabel)
																												.addComponent(
																														cellSpacingLabel))
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																								.addGroup(
																										layout.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.LEADING,
																												false)
																												.addComponent(heightSpinner)
																												.addComponent(widthSpinner)
																												.addComponent(
																														cellPaddingSpinner)
																												.addComponent(
																														cellSpacingSpinner))))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(widthTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(alignmentLabel).addComponent(summaryLabel)
																				.addComponent(captionLabel))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(summaryTextField,
																						javax.swing.GroupLayout.Alignment.TRAILING,
																						javax.swing.GroupLayout.DEFAULT_SIZE, 325,
																						Short.MAX_VALUE)
																				.addComponent(captionTextField,
																						javax.swing.GroupLayout.Alignment.TRAILING,
																						javax.swing.GroupLayout.DEFAULT_SIZE, 325,
																						Short.MAX_VALUE)
																				.addComponent(alignmentComboBox,
																						javax.swing.GroupLayout.PREFERRED_SIZE, 88,
																						javax.swing.GroupLayout.PREFERRED_SIZE)))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup().addComponent(okButton)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(cancelButton))).addContainerGap()));

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { cancelButton, okButton });

		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												layout.createSequentialGroup()
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(rowsLabel)
																		.addComponent(rowsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(columnsSpinner,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(columnsLabel))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(headersLabel)
																		.addComponent(headersComboBox,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(borderSizeLabel)
																		.addComponent(borderSizeSpinner,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(alignmentLabel)
																		.addComponent(alignmentComboBox,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addGroup(
												layout.createSequentialGroup()
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(widthLabel)
																		.addComponent(widthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(widthTypeComboBox,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(heightLabel)
																		.addComponent(heightSpinner,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(cellPaddingLabel)
																		.addComponent(cellPaddingSpinner,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(cellSpacingLabel)
																		.addComponent(cellSpacingSpinner,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(captionLabel)
										.addComponent(captionTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(summaryLabel)
										.addComponent(summaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(cancelButton)
										.addComponent(okButton)).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
		setVisible(false);
	}// GEN-LAST:event_cancelButtonActionPerformed

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okButtonActionPerformed
		int rows = ((Integer) rowsSpinner.getValue()).intValue();
		int columns = ((Integer) columnsSpinner.getValue()).intValue();
		int borderSize = ((Integer) borderSizeSpinner.getValue()).intValue();
		int width = ((Integer) widthSpinner.getValue()).intValue();
		int height = ((Integer) heightSpinner.getValue()).intValue();
		int cellPadding = ((Integer) cellPaddingSpinner.getValue()).intValue();
		int cellSpacing = ((Integer) cellSpacingSpinner.getValue()).intValue();
		Alignment alignment = (Alignment) alignmentComboBox.getSelectedItem();
		WidthType widthType = (WidthType) widthTypeComboBox.getSelectedItem();
		HeaderType headerType = (HeaderType) headersComboBox.getSelectedItem();
		boolean rowHeaders = headerType == HeaderType.FIRST_ROW || headerType == HeaderType.BOTH;
		boolean columnHeaders = headerType == HeaderType.FIRST_COLUMN || headerType == HeaderType.BOTH;
		String caption = captionTextField.getText();
		String summary = summaryTextField.getText();

		StringBuffer buffer = new StringBuffer();
		buffer.append("<table ");
		if (alignment != Alignment.NONE) {
			buffer.append("align='");
			buffer.append(alignment.attrValue);
			buffer.append("' ");
		}
		if (summary.length() > 0) {
			buffer.append("summary='");
			buffer.append(summary);
			buffer.append("' ");
		}
		buffer.append("border='");
		buffer.append(borderSize);
		buffer.append("' width='");
		buffer.append(width);
		if (widthType == WidthType.PERCENTAGE) {
			buffer.append("%");
		}
		buffer.append("' height='");
		buffer.append(height);
		buffer.append("' cellpadding='");
		buffer.append(cellPadding);
		buffer.append("' cellspacing='");
		buffer.append(cellSpacing);
		buffer.append("'>");
		if (caption.length() > 0) {
			buffer.append("<caption>");
			buffer.append(caption);
			buffer.append("</caption>");
		}
		for (int i = 0; i < rows; i++) {
			buffer.append("<tr>");
			for (int j = 0; j < columns; j++) {
				if (i == 0 && rowHeaders || j == 0 && columnHeaders) {
					buffer.append("<th>&nbsp;</th>");
				} else {
					buffer.append("<td>&nbsp;</td>");
				}
			}
			buffer.append("</tr>");
		}
		buffer.append("</table>");
		tableHtml = buffer.toString();
		setVisible(false);
	}// GEN-LAST:event_okButtonActionPerformed

	public String showDialog() {
		setVisible(true);
		return tableHtml;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JComboBox alignmentComboBox;
	private javax.swing.JLabel alignmentLabel;
	private javax.swing.JLabel borderSizeLabel;
	private javax.swing.JSpinner borderSizeSpinner;
	private javax.swing.JButton cancelButton;
	private javax.swing.JLabel captionLabel;
	private javax.swing.JTextField captionTextField;
	private javax.swing.JLabel cellPaddingLabel;
	private javax.swing.JSpinner cellPaddingSpinner;
	private javax.swing.JLabel cellSpacingLabel;
	private javax.swing.JSpinner cellSpacingSpinner;
	private javax.swing.JLabel columnsLabel;
	private javax.swing.JSpinner columnsSpinner;
	private javax.swing.JComboBox headersComboBox;
	private javax.swing.JLabel headersLabel;
	private javax.swing.JLabel heightLabel;
	private javax.swing.JSpinner heightSpinner;
	private javax.swing.JButton okButton;
	private javax.swing.JLabel rowsLabel;
	private javax.swing.JSpinner rowsSpinner;
	private javax.swing.JLabel summaryLabel;
	private javax.swing.JTextField summaryTextField;
	private javax.swing.JLabel widthLabel;
	private javax.swing.JSpinner widthSpinner;
	private javax.swing.JComboBox widthTypeComboBox;
	// End of variables declaration//GEN-END:variables

}
