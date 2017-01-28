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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.swing.layout.MultiSplitLayout.Node;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

public class MultiSplitLayoutTypeAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		Class<? super T> rawType = type.getRawType();
		if (Node.class.isAssignableFrom(rawType)) {
			TypeAdapter<List<Node>> collectionAdapter = gson.getAdapter(new TypeToken<List<Node>>() {
			});
			return (TypeAdapter<T>) new NodeAdapter(collectionAdapter);
		}
		else if (List.class.isAssignableFrom(rawType)) {
			Type type2 = type.getType();
			if (type2 instanceof ParameterizedType && ((ParameterizedType) type2).getActualTypeArguments().length == 1) {
				Type type3 = ((ParameterizedType) type2).getActualTypeArguments()[0];
				if (Node.class.isAssignableFrom($Gson$Types.getRawType(type3))) {
					TypeAdapter<Node> nodeAdapter = gson.getAdapter(Node.class);
					TypeAdapter<List<?>> collectionAdapter = gson.getAdapter(new TypeToken<List<?>>() {
					});
					return (TypeAdapter<T>) new NodeListAdapter(collectionAdapter, nodeAdapter);
				}
			}
		}
		return null;
	}

}
