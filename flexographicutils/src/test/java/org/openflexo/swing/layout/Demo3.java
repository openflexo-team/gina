package org.openflexo.swing.layout;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;

public class Demo3 {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame("Test Oriented Collapsible Pane");

				f.add(new JLabel("Press Ctrl+F or Ctrl+G to collapse panes."), BorderLayout.NORTH);

				JTree tree1 = new JTree();
				tree1.setBorder(BorderFactory.createEtchedBorder());
				f.add(tree1);

				JXCollapsiblePane pane = new JXCollapsiblePane(Direction.DOWN);
				JTree tree2 = new JTree();
				tree2.setBorder(BorderFactory.createEtchedBorder());
				pane.add(tree2);
				f.add(pane, BorderLayout.SOUTH);

				pane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl F"), JXCollapsiblePane.TOGGLE_ACTION);

				pane = new JXCollapsiblePane(Direction.RIGHT);
				JTree tree3 = new JTree();
				pane.add(tree3);
				tree3.setBorder(BorderFactory.createEtchedBorder());
				f.add(pane, BorderLayout.WEST);

				pane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl G"), JXCollapsiblePane.TOGGLE_ACTION);

				f.setSize(640, 480);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}
}
