/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

import org.openflexo.icon.UtilsIconLibrary;

public class TabbedPane<J> {

	public static interface TabHeaderRenderer<J> {
		public boolean isTabHeaderVisible(J tab);

		public Icon getTabHeaderIcon(J tab);

		public String getTabHeaderTitle(J tab);

		public String getTabHeaderTooltip(J tab);
	}

	public static interface TabListener<J> {
		public void tabSelected(@Nullable J tab);

		public void tabClosed(@Nonnull J tab);
	}

	private static final Color TRANSPARENT = new Color(1.0f, 1.0f, 1.0f, 0.3f);
	private static final Color LIGHT_BLUE = new Color(150, 183, 255, 255);

	private static final int TAB_SPACING = 2;

	private class TabHeaders extends JPanel implements ActionListener {

		private class TabHeader extends JMenuBar implements ActionListener, MouseListener {
			private static final int ROUNDED_CORNER_SIZE = 8;

			private class TabHeaderBorder implements Border {

				@Override
				public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setColor(Color.LIGHT_GRAY);
					g.drawLine(0, ROUNDED_CORNER_SIZE - 3, 0, height);
					g.drawLine(width - 1, ROUNDED_CORNER_SIZE - 3, width - 1, height);
					g.drawLine(ROUNDED_CORNER_SIZE - 3, 0, width - ROUNDED_CORNER_SIZE + 3, 0);
					g.drawArc(0, 0, ROUNDED_CORNER_SIZE, ROUNDED_CORNER_SIZE, 90, 90);
					g.drawArc(width - ROUNDED_CORNER_SIZE - 1, 0, ROUNDED_CORNER_SIZE, ROUNDED_CORNER_SIZE, 0, 90);
				}

				@Override
				public Insets getBorderInsets(Component c) {
					return new Insets(5, ROUNDED_CORNER_SIZE + 1, 5, ROUNDED_CORNER_SIZE + 1);
				}

				@Override
				public boolean isBorderOpaque() {
					return false;
				}

			}

			private final J tab;

			private JLabel title;
			private JButton close;

			private Color defaultBackground;

			private Color defaultForeground;

			private boolean hovered;

			private java.awt.geom.Path2D.Double clip;

			public TabHeader(J tab) {
				super();
				setLayout(new BorderLayout());
				setBackground(UIManager.getDefaults().getColor("ToolBar.floatingForeground"));
				setOpaque(false);
				this.tab = tab;
				add(title = new JLabel());
				title.setOpaque(false);
				title.setBorder(null);
				addMouseListener(this);
				title.addMouseListener(this);
				add(close = new JButton(UtilsIconLibrary.CLOSE_TAB_ICON), BorderLayout.EAST);
				close.setContentAreaFilled(false);
				close.setOpaque(false);
				close.setBorderPainted(false);
				close.setRolloverIcon(UtilsIconLibrary.CLOSE_TAB_HOVER_ICON);
				close.setPressedIcon(UtilsIconLibrary.CLOSE_TAB_PRESSED_ICON);
				close.addActionListener(this);
				close.setFocusable(false);
				close.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
				close.setPreferredSize(new Dimension(close.getIcon().getIconWidth() + close.getInsets().left + close.getInsets().right,
						close.getIcon().getIconHeight() + close.getInsets().top + close.getInsets().bottom));
				setBorder(new TabHeaderBorder());
				addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						clip = null;
					}
				});
				refresh();
			}

			@Override
			public String toString() {
				return title.getText() + " visible=" + isVisible() + " bounds=" + getBounds();
			}

			@Override
			public boolean isVisible() {
				return (tabHeaderRenderer == null || tabHeaderRenderer.isTabHeaderVisible(tab)) && super.isVisible();
			}

			public void refresh() {
				if (tabHeaderRenderer != null) {
					title.setIcon(tabHeaderRenderer.getTabHeaderIcon(tab));
					title.setText(tabHeaderRenderer.getTabHeaderTitle(tab));
					title.setToolTipText(tabHeaderRenderer.getTabHeaderTooltip(tab));
				}
				else {
					title.setIcon(null);
					if (tab instanceof JComponent) {
						title.setText(((JComponent) tab).getName());
						title.setToolTipText(((JComponent) tab).getToolTipText());
					}
				}
				TabHeaders.this.revalidate();
			}

			@Override
			protected void paintComponent(Graphics g) {
				if (getParent() == TabHeaders.this) {
					Graphics clippedG = g.create();
					((Graphics2D) clippedG).clip(getClip());
					super.paintComponent(clippedG);
					clippedG.dispose();
					if (tab == selectedTab) {
						((Graphics2D) g).setPaint(new GradientPaint(new Point2D.Double(0, 0), TRANSPARENT,
								new Point2D.Double(0, getHeight() / 2), LIGHT_BLUE));
						g.fillRect(0, 0, getWidth(), getHeight());
					}
				}
				else {
					if (isOpaque()) {
						g.setColor(getBackground());
						g.fillRect(0, 0, getWidth(), getHeight());
					}
				}
			}

			public java.awt.geom.Path2D.Double getClip() {
				if (clip == null) {
					clip = new java.awt.geom.Path2D.Double();
					clip.moveTo(ROUNDED_CORNER_SIZE - 3, 0);
					// Top border
					clip.lineTo(getWidth() - ROUNDED_CORNER_SIZE + 3, 0);
					// Top right rounded corner
					clip.quadTo(getWidth(), 0, getWidth(), ROUNDED_CORNER_SIZE - 3);
					// Right border
					clip.lineTo(getWidth(), getHeight() - 1);
					// Bottom border
					clip.lineTo(0, getHeight() - 1);
					// Left border
					clip.lineTo(0, ROUNDED_CORNER_SIZE - 3);
					// Top left rounder border
					clip.quadTo(0, 0, ROUNDED_CORNER_SIZE - 3, 0);
					clip.closePath();
				}
				return clip;
			}

			@Override
			protected void paintBorder(Graphics g) {
				if (getParent() == TabHeaders.this) {
					super.paintBorder(g);
				}
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == close) {
					TabbedPane.this.removeTab(tab);
				}
			}

			public void delete() {
				if (getParent() != null) {
					super.getParent().remove(this);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getSource() != close) {
					dropHoveringEffect();
					TabHeaders.this.hidePopup();
					if (getParent() != TabHeaders.this) {
						// Move the tab at the beginning
						tabs.remove(tab);
						tabs.add(0, tab);
					}
					TabbedPane.this.selectTab(tab);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (getParent() == extraTabsPopup) {
					hovered = true;
					setOpaque(true);
					defaultBackground = getBackground();
					defaultForeground = title.getForeground();
					setBackground(UIManager.getColor("List.selectionBackground"));
					title.setForeground(UIManager.getColor("List.selectionForeground"));
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					repaint();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				dropHoveringEffect();
			}

			public void dropHoveringEffect() {
				if (hovered) {
					setOpaque(false);
					setBackground(defaultBackground);
					title.setForeground(defaultForeground);
					defaultBackground = null;
					defaultForeground = null;
					setCursor(Cursor.getDefaultCursor());
					repaint();
					hovered = false;
				}
			}

		}

		private final Map<J, TabHeader> headerComponents = new HashMap<>();

		private final JButton extraTabsButton;
		private final JPopupMenu extraTabsPopup;

		private int xBorderStart = 0;

		private int xBorderEnd = 0;

		public TabHeaders() {
			setOpaque(false);
			extraTabsButton = new BarButton(UtilsIconLibrary.ARROW_DOWN);
			extraTabsButton.setSize(new Dimension(extraTabsButton.getIcon().getIconWidth(), extraTabsButton.getIcon().getIconHeight()));
			extraTabsButton.addActionListener(this);
			extraTabsPopup = new JPopupMenu();
			extraTabsPopup.setInvoker(extraTabsButton);
			// extraTabsButton.setComponentPopupMenu(extraTabsPopup);
			setBorder(new AbstractBorder() {
				@Override
				public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
					g.setColor(Color.LIGHT_GRAY);
					g.drawLine(0, height - 1, xBorderEnd, height - 1);
					g.drawLine(xBorderStart, height - 1, width, height - 1);
				}
			});
		}

		public void hidePopup() {
			if (extraTabsPopup.isVisible()) {
				extraTabsPopup.setVisible(false);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == extraTabsButton) {
				if (extraTabsPopup != null) {
					if (extraTabsPopup.isVisible()) {
						extraTabsPopup.setVisible(false);
					}
					else {
						extraTabsPopup.setVisible(true);
						Point location = new Point(extraTabsButton.getWidth() - extraTabsPopup.getWidth(), extraTabsButton.getHeight());
						SwingUtilities.convertPointToScreen(location, extraTabsButton);
						extraTabsPopup.setLocation(location);
					}
				}
			}
		}

		@Override
		public void doLayout() {
			extraTabsPopup.removeAll();
			xBorderEnd = 0;
			xBorderStart = getWidth();
			boolean moveToPopup = false;
			if (tabs.size() > 0) {
				TabHeader selectedHeader = selectedTab != null ? headerComponents.get(selectedTab) : null;

				boolean selectedHeaderDone = selectedTab == null;
				int x = 0;
				int availableWidth = getWidth();
				for (int i = 0; i < tabs.size(); i++) {
					J tab = tabs.get(i);
					TabHeader tabHeader = headerComponents.get(tab);
					if (tabHeader != null) {
						if (!tabHeader.isVisible()) {
							tabHeader.setBounds(0, 0, 0, 0);
							selectedHeaderDone |= tabHeader == selectedHeader;
							continue;
						}
						if (!moveToPopup) {
							if (!selectedHeaderDone) {
								if (tab != selectedTab) {
									if (tabHeader != null && selectedHeader != null) {
										if (i + 2 == tabs.size()) { // in this case, we only need to put the current tab and the selected
																	// tab
											moveToPopup = availableWidth
													- (tabHeader.getPreferredSize().width + selectedHeader.getPreferredSize().width) < 0;
										}
										else {
											moveToPopup = availableWidth - (tabHeader.getPreferredSize().width
													+ selectedHeader.getPreferredSize().width + extraTabsButton.getWidth()) < 0;
										}
									}
								}
								if (moveToPopup) {
									// There is not enough room to put the current tab header, the selected header and the extraTabs button
									if (selectedHeader.getParent() != this) {
										add(selectedHeader);
									}
									tabs.remove(selectedTab);
									tabs.add(i, selectedTab);
									i++;
									selectedHeader.setBounds(x, 0, selectedHeader.getPreferredSize().width, getHeight());
									xBorderEnd = x;
									xBorderStart = x + selectedHeader.getWidth();
									selectedHeaderDone = true;
								}
							}
							else {
								if (i + 1 == tabs.size()) {
									moveToPopup = availableWidth - tabHeader.getWidth() < 0;
								}
								else {
									moveToPopup = availableWidth - (tabHeader.getWidth() + extraTabsButton.getWidth()) < 0;
								}
							}
						}
						if (moveToPopup) {
							if (extraTabsButton.getParent() != this) {
								add(extraTabsButton);
							}
							extraTabsButton.setSize(extraTabsButton.getWidth(), getHeight());
							extraTabsButton.setLocation(getWidth() - extraTabsButton.getWidth(), 0);
							extraTabsPopup.add(tabHeader);
							extraTabsPopup.revalidate();
						}
						else {
							if (tabHeader.getParent() != this) {
								add(tabHeader);
							}
							if (x > 0) {
								x += TAB_SPACING;
							}
							tabHeader.setBounds(x, 0, tabHeader.getPreferredSize().width, getHeight());
							x += tabHeader.getWidth();
						}
						availableWidth = getWidth() - x;
						selectedHeaderDone |= tab == selectedTab;
					}

				}
				if (selectedHeader != null) {
					xBorderEnd = selectedHeader.getX();
					xBorderStart = xBorderEnd + selectedHeader.getWidth();
				}
			}
			if (!moveToPopup) {
				if (extraTabsButton.getParent() == this) {
					remove(extraTabsButton);
				}
			}
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension prefSize = new Dimension();
			for (TabHeader header : headerComponents.values()) {
				Dimension headerPrefSize = header.getPreferredSize();
				prefSize.width += headerPrefSize.getWidth();
				prefSize.height = Math.max(prefSize.height, headerPrefSize.height);
			}
			return prefSize;
		}

		@Override
		public Dimension getMinimumSize() {
			if (selectedTab != null) {
				return headerComponents.get(selectedTab).getPreferredSize();
			}
			return super.getMinimumSize();
		}

		public void selectTab(J tab) {
			doLayout();
			repaint();
		}

		public void refresh() {
			for (Entry<J, TabHeader> e : headerComponents.entrySet()) {
				e.getValue().refresh();
			}
		}

		public void hideTab(J tab) {
			if (headerComponents.get(tab) != null) {
				headerComponents.get(tab).setVisible(false);
			}
		}

		public void showTab(J tab) {
			if (headerComponents.get(tab) != null) {
				headerComponents.get(tab).setVisible(true);
			}
		}

		public void addTab(J tab) {
			headerComponents.put(tab, new TabHeader(tab));
			doLayout();
			repaint();
		}

		public void removeTab(J tab) {
			TabHeader tabHeader = headerComponents.remove(tab);
			if (tabHeader != null) {
				Container parent = tabHeader.getParent();
				tabHeader.delete();
				if (parent == this) {
					doLayout();
					repaint();
				}
				else if (parent == extraTabsPopup) {
					extraTabsPopup.revalidate();
					extraTabsPopup.pack();
				}
			}
		}

	}

	protected TabHeaderRenderer<J> tabHeaderRenderer;
	private List<TabListener<J>> tabListeners;

	protected TabHeaders tabHeaders;
	private JPanel tabBody;

	private boolean useTabBody = true;

	protected List<J> tabs;
	protected J selectedTab;

	public TabbedPane() {
		tabs = new ArrayList<>();
		tabListeners = new ArrayList<>();
		tabHeaders = new TabHeaders();
		tabBody = new JPanel(new BorderLayout());
		tabBody.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.LIGHT_GRAY));
	}

	public TabbedPane(TabHeaderRenderer<J> tabHeaderRenderer) {
		this();
		this.tabHeaderRenderer = tabHeaderRenderer;
	}

	public TabHeaders getTabHeaders() {
		return tabHeaders;
	}

	public JPanel getTabBody() {
		return tabBody;
	}

	public boolean isUseTabBody() {
		return useTabBody;
	}

	public void setUseTabBody(boolean useTabBody) {
		this.useTabBody = useTabBody;
		if (useTabBody) {
			for (J tab : tabs) {
				if (tab != null && !JComponent.class.isAssignableFrom(tab.getClass())) {
					throw new IllegalArgumentException("Cannot use tab body because " + tab + " is not a JComponent");
				}
			}
		}
	}

	public TabHeaderRenderer<J> getTabHeaderRenderer() {
		return tabHeaderRenderer;
	}

	public void setTabHeaderRenderer(TabHeaderRenderer<J> tabHeaderRenderer) {
		this.tabHeaderRenderer = tabHeaderRenderer;
	}

	public void addToTabListeners(TabListener<J> listener) {
		tabListeners.add(listener);
	}

	public void removeToTabListeners(TabListener<J> listener) {
		tabListeners.remove(listener);
	}

	public J getSelectedTab() {
		return selectedTab;
	}

	public boolean hasTabs() {
		return tabs.size() > 0;
	}

	public boolean containsTab(J tab) {
		return tabs.contains(tab);
	}

	public List<J> getTabs() {
		return tabs;
	}

	public void addTab(J tab) {

		System.out.println(">>>>>>>>>>>>> On ajoute le tab " + tab);

		if (tab != null && useTabBody && !JComponent.class.isAssignableFrom(tab.getClass())) {
			throw new IllegalArgumentException("Tab must be an instanceof JComponent but received a " + tab.getClass().getName());
		}
		if (!tabs.contains(tab)) {
			tabs.add(tab);
			tabHeaders.addTab(tab);
			if (selectedTab == null && (tabHeaderRenderer == null || tabHeaderRenderer.isTabHeaderVisible(tab))) {
				selectTab(tab);
			}
		}
	}

	public void hideTab(J tab) {
		tabHeaders.hideTab(tab);
	}

	public void showTab(J tab) {
		tabHeaders.showTab(tab);
	}

	public void removeTab(J tab) {
		int indexOf = tabs.indexOf(tab);
		if (tabs.remove(tab)) {
			if (selectedTab == tab) {
				// TODO: Handle removal of selected tab
				if (tabs.size() > 0) {
					if (tabHeaderRenderer == null) {
						if (indexOf >= tabs.size()) {
							selectTab(tabs.get(tabs.size() - 1));
						}
						else {
							selectTab(tabs.get(indexOf));
						}
					}
					else {
						J tabToSelect = null;
						for (int i = indexOf - 1; i > -1; i--) {
							if (tabHeaderRenderer.isTabHeaderVisible(tabs.get(i))) {
								tabToSelect = tabs.get(i);
								break;
							}
						}
						if (tabToSelect == null) {
							for (int i = indexOf + 1; i < tabs.size(); i++) {
								if (tabHeaderRenderer.isTabHeaderVisible(tabs.get(i))) {
									tabToSelect = tabs.get(i);
									break;
								}
							}
						}
						selectTab(tabToSelect);
					}
				}
				else {
					selectTab(null);
				}
			}
			tabHeaders.removeTab(tab);
			fireTabClosed(tab);
		}
	}

	public void selectTab(J tab) {
		if (selectedTab == tab) {
			return;
		}
		if (tab != null && !tabs.contains(tab)) {
			// throw new IllegalArgumentException("Tab must be added to the content pane first.");
			return;
		}
		if (useTabBody && selectedTab != null) {
			tabBody.remove((JComponent) selectedTab);
		}
		selectedTab = tab;
		if (useTabBody) {
			if (tab != null) {
				tabBody.add((JComponent) tab, 0);
			}
			else {
				tabBody.add(new JPanel(), 0);
			}
			tabBody.revalidate();
			tabBody.repaint();
		}
		tabHeaders.selectTab(tab);
		fireTabSelected(tab);
	}

	public void refreshTabHeaders() {
		tabHeaders.refresh();
	}

	protected void fireTabClosed(J tab) {
		for (TabListener<J> tabListener : tabListeners) {
			tabListener.tabClosed(tab);
		}
	}

	protected void fireTabSelected(J tab) {
		for (TabListener<J> tabListener : tabListeners) {
			tabListener.tabSelected(tab);
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		JFrame frame = new JFrame("Test tabbed panes");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TabbedPane<JLabel> tabbedPane = new TabbedPane<>();
		tabbedPane.setTabHeaderRenderer(new TabHeaderRenderer<JLabel>() {

			@Override
			public boolean isTabHeaderVisible(JLabel tab) {
				return true;
			}

			@Override
			public String getTabHeaderTooltip(JLabel tab) {
				return "Some tooltip for " + tab.getText();
			}

			@Override
			public String getTabHeaderTitle(JLabel tab) {
				return tab.getText();
			}

			@Override
			public Icon getTabHeaderIcon(JLabel tab) {
				try {
					return new ImageIcon(new URL("http://cdn1.iconfinder.com/data/icons/CuteMonstersPNG/16/blue_monster.png"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}
			}
		});
		for (int i = 0; i < 20; i++) {
			JLabel label = new JLabel("Some label " + (i + 1));
			label.setHorizontalAlignment(JLabel.CENTER);
			tabbedPane.addTab(label);
		}
		tabbedPane.getTabHeaders().doLayout();
		frame.add(tabbedPane.getTabHeaders(), BorderLayout.NORTH);
		frame.add(tabbedPane.getTabBody());
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

}
