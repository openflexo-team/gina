package org.openflexo.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class RSyntaxTextAreaDemo extends JFrame {

	public RSyntaxTextAreaDemo() {

		JPanel cp = new JPanel(new BorderLayout());

		RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setCodeFoldingEnabled(true);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		cp.add(sp);

		setContentPane(cp);
		setTitle("Text Editor Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);

	}

	public static void main(String[] args) {
		// Start all Swing applications on the EDT.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new RSyntaxTextAreaDemo().setVisible(true);
			}
		});
	}

}
