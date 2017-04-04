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

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.openflexo.swing.layout.MultiSplitLayout.DefaultDivider;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultLeaf;
import org.openflexo.swing.layout.MultiSplitLayout.DefaultSplit;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.swing.layout.MultiSplitLayout.Split;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class NodeAdapter extends TypeAdapter<Node> {

	private static enum Types {
		LEAF(DefaultLeaf.class), SPLIT(DefaultSplit.class), DIVIDER(DefaultDivider.class);

		private Class<? extends Node> type;

		private Types(Class<? extends Node> type) {
			this.type = type;
		}

		public Class<? extends Node> getType() {
			return type;
		}

		public static NodeAdapter.Types getTypeForClass(Class<? extends Node> type) {
			for (NodeAdapter.Types t : values()) {
				if (t.getType().isAssignableFrom(type)) {
					return t;
				}
			}
			return null;
		}
	}

	private final TypeAdapter<List<Node>> listAdapter;

	public NodeAdapter(TypeAdapter<List<Node>> collectionAdapter) {
		super();
		this.listAdapter = collectionAdapter;
	}

	private static String toString(Rectangle rect) {
		return "[" + rect.x + "," + rect.y + "," + rect.width + "," + rect.height + "]";
	}

	private static Rectangle fromString(String string) {
		StringTokenizer tokenizer = new StringTokenizer(string, "[],");
		Rectangle rect = new Rectangle();
		rect.x = Integer.valueOf(tokenizer.nextToken());
		rect.y = Integer.valueOf(tokenizer.nextToken());
		rect.width = Integer.valueOf(tokenizer.nextToken());
		rect.height = Integer.valueOf(tokenizer.nextToken());
		return rect;
	}

	@Override
	public void write(JsonWriter out, Node value) throws IOException {
		out.beginObject();
		out.name("type");
		out.value(Types.getTypeForClass(value.getClass()).name());
		if (!(value instanceof Divider)) {
			out.name("weight");
			out.value(value.getWeight());
		}
		out.name("visible");
		out.value(value.isVisible());
		out.name("bounds");
		out.value(toString(value.getBounds()));
		String name = null;
		if (value instanceof Split) {
			Split split = (Split) value;
			name = split.getName();
			out.name("rowLayout");
			out.value(split.isRowLayout());
			out.name("children");
			listAdapter.write(out, split.getChildren());
		}
		else if (value instanceof Leaf) {
			name = ((Leaf<?>) value).getName();
		}
		if (name != null) {
			out.name("name");
			out.value(name);
		}
		out.endObject();
	}

	@Override
	public Node read(JsonReader in) throws IOException {
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}
		in.beginObject();
		String name = in.nextName();
		NodeAdapter.Types t = Types.valueOf(in.nextString());
		Node<?> node;
		try {
			node = t.getType().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		while (in.hasNext()) {
			name = in.nextName();
			if (name.equals("weight")) {
				double weight = in.nextDouble();
				node.setWeight(weight);
			}
			else if (name.equals("visible")) {
				boolean visible = in.nextBoolean();
				node.setVisible(visible);
			}
			else if (name.equals("bounds")) {
				Rectangle bounds = fromString(in.nextString());
				node.setBounds(bounds);
			}
			else if (name.equals("rowLayout")) {
				Split<?> split = (Split<?>) node;
				boolean rowLayout = in.nextBoolean();
				split.setRowLayout(rowLayout);
			}
			else if (name.equals("children")) {
				Split split = (Split) node;
				List<Node> children = listAdapter.read(in);
				split.setChildren(children);
			}
			else if (name.equals("name")) {
				String nameValue = in.nextString();
				if (node instanceof Leaf) {
					((Leaf<?>) node).setName(nameValue);
				}
				else if (node instanceof Split) {
					((Split<?>) node).setName(nameValue);
				}
			}
		}
		in.endObject();
		return node;
	}
}
