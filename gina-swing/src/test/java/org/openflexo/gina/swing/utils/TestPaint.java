package org.openflexo.gina.swing.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TestPaint {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel(new BorderLayout()) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.setColor(Color.RED);
				g.drawLine(0, 0, 200, 200);
			}
		};
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(new JLabel("test") {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.RED);
				g.drawRect(3, 3, 20, 10);
			}
		}, BorderLayout.WEST);
		topPanel.add(new JTextField() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.setColor(Color.RED);
				g.fillRect(3, 3, 20, 10);
			}

		}, BorderLayout.CENTER);
		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(new JScrollPane(new JTextArea(5, 40)), BorderLayout.CENTER);
		frame.getContentPane().add(panel);
		frame.validate();
		frame.pack();
		frame.setVisible(true);
	}
}
