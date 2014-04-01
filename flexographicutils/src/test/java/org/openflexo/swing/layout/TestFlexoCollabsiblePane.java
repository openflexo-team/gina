package org.openflexo.swing.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.swing.FlexoCollabsiblePanelGroup;

public class TestFlexoCollabsiblePane {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame("Test Flexo Collapsible Pane");

				FlexoCollabsiblePanelGroup contentsPane = new FlexoCollabsiblePanelGroup();
				contentsPane.addContents("panel1", makeContents("prout", Color.red));
				contentsPane.addContents("panel2", makeContents("yoplaboum", Color.yellow));
				contentsPane.addContents("panel3", makeContents("zoubi", Color.blue));

				f.add(contentsPane, BorderLayout.CENTER);

				f.setSize(640, 480);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}

	private static JPanel makeContents(String s, Color c) {
		JPanel returned = new JPanel(new BorderLayout());
		returned.setBackground(c);
		returned.setPreferredSize(new Dimension(300, 200));
		returned.add(new JLabel(s), BorderLayout.CENTER);
		return returned;
	}
}
