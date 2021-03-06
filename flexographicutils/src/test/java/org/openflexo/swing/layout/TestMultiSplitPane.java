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

package org.openflexo.swing.layout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.swing.layout.MultiSplitLayout.Split;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class TestMultiSplitPane {

	public static final String LEFT = "left";
	public static final String CENTER = "center";
	public static final String RIGHT = "right";

	public static final String TOP = "top";
	public static final String MIDDLE = "middle";
	public static final String BOTTOM = "bottom";

	protected void initUI() {

		MultiSplitLayoutFactory factory = new MultiSplitLayoutFactory.DefaultMultiSplitLayoutFactory();
		Split<?> root = getDefaultLayout(factory);
		final MultiSplitLayout layout = new MultiSplitLayout(factory);
		layout.setLayoutByWeight(false);
		layout.setFloatingDividers(false);
		JXMultiSplitPane splitPane = new JXMultiSplitPane(layout);
		splitPane.setDividerPainter(new KnobDividerPainter());
		addButton(LEFT + TOP, splitPane);
		addButton(CENTER + TOP, splitPane);
		addButton(RIGHT + TOP, splitPane);
		addButton(LEFT + BOTTOM, splitPane);
		addButton(CENTER + BOTTOM, splitPane);
		addButton(RIGHT + BOTTOM, splitPane);

		System.out.println("root=" + root);
		System.out.println("layout=" + layout);
		MultiSplitLayout.printModel(root);

		layout.setModel(root);
		// restoreLayout(layout, root);
		splitPane.setPreferredSize(layout.getModel().getBounds().getSize());
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveLayout(layout);
				System.exit(0);
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.add(splitPane);
		frame.pack();
		frame.setVisible(true);
	}

	protected Split<?> getDefaultLayout(MultiSplitLayoutFactory factory) {
		Split root = factory.makeSplit();
		root.setName("ROOT");
		Split<?> left = getVerticalSplit(LEFT, 0.5, 0.5, factory);
		left.setWeight(0);
		left.setName(LEFT);
		Split<?> center = getVerticalSplit(CENTER, 0.8, 0.2, factory);
		center.setWeight(1.0);
		center.setName(CENTER);
		Split<?> right = getVerticalSplit(RIGHT, 0.5, 0.5, factory);
		right.setWeight(0);
		right.setName(RIGHT);
		root.setChildren(left, factory.makeDivider(), center, factory.makeDivider(), right);
		return root;
	}

	protected void addButton(final String buttonName, final JXMultiSplitPane splitPane) {
		final JButton button = new JButton(buttonName);
		splitPane.add(buttonName, button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MultiSplitLayout layout = splitPane.getMultiSplitLayout();
				restoreLayout(layout, getDefaultLayout(splitPane.getMultiSplitLayout().getFactory()));
				splitPane.revalidate();
			}
		});
	}

	public Split<?> getVerticalSplit(String name, double topWeight, double bottomWeight, MultiSplitLayoutFactory factory) {
		Split split = factory.makeSplit();
		split.setRowLayout(false);
		Leaf<?> top = factory.makeLeaf(name + TOP);
		top.setWeight(topWeight);
		Leaf<?> bottom = factory.makeLeaf(name + BOTTOM);
		bottom.setWeight(bottomWeight);
		split.setChildren(top, factory.makeDivider(), bottom);
		return split;
	}

	protected void restoreLayout(MultiSplitLayout layout, Node defaultModel) {
		Node<?> model = defaultModel;
		try {
			model = getGson().fromJson(new InputStreamReader(new FileInputStream(getLayoutFile()), "UTF-8"), Split.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		layout.setModel(model);
	}

	protected void saveLayout(MultiSplitLayout layout) {
		Gson gson = getGson();
		String json = gson.toJson(layout.getModel());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(getLayoutFile());
			fos.write(json.getBytes("UTF-8"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected Gson getGson() {
		GsonBuilder builder = new GsonBuilder().registerTypeAdapterFactory(new MultiSplitLayoutTypeAdapterFactory());
		Gson gson = builder.create();
		return gson;
	}

	protected File getLayoutFile() {
		return ResourceLocator.retrieveResourceAsFile(ResourceLocator.locateResource("testlayout"));
	}

	public static void main(String[] args) {
		/*BeanInfo info = Introspector.getBeanInfo(JTextField.class);
		PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; ++i) {
			PropertyDescriptor pd = propertyDescriptors[i];
			if (pd.getName().equals("text")) {
				pd.setValue("transient", Boolean.TRUE);
			}
		}*/

		SwingUtilities.invokeLater(() -> new TestMultiSplitPane().initUI());
	}
}
