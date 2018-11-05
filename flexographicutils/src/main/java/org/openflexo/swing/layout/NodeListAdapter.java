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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.swing.layout.MultiSplitLayout.Node;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class NodeListAdapter extends TypeAdapter<List<Node>> {

	private final TypeAdapter<List<?>> collectionAdapter;
	private final TypeAdapter<Node> nodeAdapter;

	public NodeListAdapter(TypeAdapter<List<?>> collectionAdapter, TypeAdapter<Node> nodeAdapter) {
		this.collectionAdapter = collectionAdapter;
		this.nodeAdapter = nodeAdapter;
	}

	@Override
	public void write(JsonWriter out, List<Node> value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		collectionAdapter.write(out, value);
	}

	@Override
	public List<Node> read(JsonReader in) throws IOException {
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}

		List<Node> collection = new ArrayList<>();
		in.beginArray();
		while (in.hasNext()) {
			Node<?> instance = nodeAdapter.read(in);
			collection.add(instance);
		}
		in.endArray();
		return collection;
	}

}
