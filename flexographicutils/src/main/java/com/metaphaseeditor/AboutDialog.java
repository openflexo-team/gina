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

import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.toolbox.ResourceLocator;

/**
 * 
 * @author Rudolf Visagie
 */
public class AboutDialog extends javax.swing.JDialog {
	
	private static ResourceLocator rl = ResourceLocator.getResourceLocator();

	/** Creates new form AboutDialog */
	public AboutDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		setIconImage(new ImageIconResource(rl.locateResource("Icons/MetaphaseEditor/icons/metaphase16x16.png")).getImage());
		setLocationRelativeTo(null);
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
	 * method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		javax.swing.JLabel homepageLabel = new javax.swing.JLabel();
		javax.swing.JLabel appHomepageLabel = new javax.swing.JLabel();
		javax.swing.JLabel appDescLabel = new javax.swing.JLabel();
		javax.swing.JLabel imageLabel = new javax.swing.JLabel();
		javax.swing.JLabel versionLabel = new javax.swing.JLabel();
		javax.swing.JLabel appVersionLabel = new javax.swing.JLabel();
		javax.swing.JLabel authorLabel = new javax.swing.JLabel();
		javax.swing.JLabel appVendorLabel = new javax.swing.JLabel();
		javax.swing.JLabel appTitleLabel = new javax.swing.JLabel();
		closeButton = new javax.swing.JButton();
		javax.swing.JLabel acknowledgementsLabel = new javax.swing.JLabel();
		javax.swing.JLabel appLicenseLabel = new javax.swing.JLabel();
		javax.swing.JLabel licenseLabel = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("About");
		setIconImage(null);

		homepageLabel.setFont(homepageLabel.getFont().deriveFont(homepageLabel.getFont().getStyle() | java.awt.Font.BOLD));
		homepageLabel.setText("Homepage:");

		appHomepageLabel.setText("http://www.metaphaseeditor.com/");

		appDescLabel.setText("<html>Metaphase is an open-source WYSIWYG HTML editor.");

		imageLabel.setIcon(new ImageIconResource(rl.locateResource("MetaphaseEditor/images/Metaphase-flourescent.JPG"))); // NOI18N

		versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() | java.awt.Font.BOLD));
		versionLabel.setText("Product Version:");

		appVersionLabel.setText("1.0");

		authorLabel.setFont(authorLabel.getFont().deriveFont(authorLabel.getFont().getStyle() | java.awt.Font.BOLD));
		authorLabel.setText("Author:");

		appVendorLabel.setText("Rudolf Visagie");

		appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | java.awt.Font.BOLD,
				appTitleLabel.getFont().getSize() + 4));
		appTitleLabel.setText("Metaphase HTML Editor");

		closeButton.setText("Close");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeButtonActionPerformed(evt);
			}
		});

		acknowledgementsLabel.setFont(acknowledgementsLabel.getFont().deriveFont(
				acknowledgementsLabel.getFont().getStyle() | java.awt.Font.BOLD));
		acknowledgementsLabel.setText("Acknowledgements:");

		appLicenseLabel.setText("GNU LESSER GENERAL PUBLIC LICENSE, Version 3");

		licenseLabel.setFont(licenseLabel.getFont().deriveFont(licenseLabel.getFont().getStyle() | java.awt.Font.BOLD));
		licenseLabel.setText("License:");

		jLabel1.setText("<html>Metaphase HTML Editor is based on the excellent web-based CKEditor. All the toolbar icons are also taken from CKEditor. (<a href=\"http://ckeditor.com\">http://ckeditor.com</a>).<br><br>\nCharles Bell for his HTMLDocumentEditor (<a href=\"http://www.artima.com/forums/flat.jsp?forum=1&thread=1276\">http://www.artima.com/forums/flat.jsp?forum=1&thread=1276</a>) that served as a good starting point.<br><br>Modified portions of code from Kafenio.editor has also been included. (<a href=\"http://editor.kafenio.org/\">http://editor.kafenio.org/</a>)<br><br>The spell checker functionality is provided by Jazzy - The Java Open Source Spell Checker. (<a href=\"http://jazzy.sourceforge.net/\">http://jazzy.sourceforge.net/</a>)<br><br>The image on the left representing cell metaphase is from WikiVisual. (<a href=\"http://en.wikivisual.com/index.php/Metaphase\">http://en.wikivisual.com/index.php/Metaphase</a>) ");

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout
						.createSequentialGroup()
						.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(66, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout
						.createSequentialGroup()
						.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(86, Short.MAX_VALUE)));

		jScrollPane2.setViewportView(jPanel1);

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
																.addComponent(imageLabel)
																.addGap(10, 10, 10)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(appTitleLabel)
																				.addComponent(acknowledgementsLabel)
																				.addComponent(appDescLabel,
																						javax.swing.GroupLayout.DEFAULT_SIZE, 432,
																						Short.MAX_VALUE)
																				.addGroup(
																						layout.createSequentialGroup()
																								.addGroup(
																										layout.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.LEADING)
																												.addComponent(versionLabel)
																												.addComponent(authorLabel)
																												.addComponent(homepageLabel)
																												.addComponent(licenseLabel))
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addGroup(
																										layout.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.LEADING)
																												.addComponent(
																														appVersionLabel)
																												.addComponent(
																														appVendorLabel)
																												.addComponent(
																														appHomepageLabel)
																												.addComponent(
																														appLicenseLabel)))
																				.addComponent(jScrollPane2,
																						javax.swing.GroupLayout.DEFAULT_SIZE, 432,
																						Short.MAX_VALUE)))
												.addComponent(closeButton, javax.swing.GroupLayout.Alignment.TRAILING)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(imageLabel)
										.addGroup(
												layout.createSequentialGroup()
														.addComponent(appTitleLabel)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(appDescLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(versionLabel).addComponent(appVersionLabel))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(authorLabel).addComponent(appVendorLabel))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(homepageLabel).addComponent(appHomepageLabel))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(licenseLabel).addComponent(appLicenseLabel))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(acknowledgementsLabel)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(closeButton).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_closeButtonActionPerformed
		setVisible(false);
	}// GEN-LAST:event_closeButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton closeButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane2;
	// End of variables declaration//GEN-END:variables

}
