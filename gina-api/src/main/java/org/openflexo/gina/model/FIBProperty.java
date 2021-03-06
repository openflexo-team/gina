/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.gina.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.pamela.annotations.PropertyIdentifier;

/**
 * A FIBProperty encodes a typed property access associated to a class of FIB model
 * 
 * @author sylvain
 * 
 * @param <T>
 */
public class FIBProperty<T> {

	private static final Logger LOGGER = Logger.getLogger(FIBProperty.class.getPackage().getName());

	public static void main(String[] args) {
		System.out.println("Hop: " + getFIBProperty(FIBModelObject.class, FIBModelObject.NAME_KEY));
		System.out.println("Hop: " + getFIBProperties(FIBTable.class));
	}

	private static Map<String, FIBProperty<?>> retrieveProperties(Class<?> ownerClass) {
		Map<String, FIBProperty<?>> returned = new HashMap<>();
		for (Field f : ownerClass.getFields()) {
			PropertyIdentifier parameter = f.getAnnotation(PropertyIdentifier.class);
			if (parameter != null) {
				FIBProperty<?> p = new FIBProperty<>(f, parameter);
				// System.out.println("Found " + p);
				returned.put(p.getName(), p);
			}
		}
		return returned;
	}

	private static Map<Class<?>, Map<String, FIBProperty<?>>> cachedProperties = new HashMap<>();

	public static <T> FIBProperty<T> getFIBProperty(Class<?> declaringClass, String name, Class<T> type) {
		FIBProperty<T> returned = (FIBProperty<T>) getFIBProperty(declaringClass, name);
		if (returned != null) {
			returned.type = type;
		}
		return returned;
	}

	public static FIBProperty<?> getFIBProperty(Class<?> declaringClass, String name) {
		Map<String, FIBProperty<?>> cacheForClass = cachedProperties.get(declaringClass);
		if (cacheForClass == null) {
			cacheForClass = retrieveProperties(declaringClass);
			cachedProperties.put(declaringClass, cacheForClass);
		}
		FIBProperty<?> returned = cacheForClass.get(name);
		if (returned == null && declaringClass.getSuperclass() != null) {
			return getFIBProperty(declaringClass.getSuperclass(), name);
		}
		/*if (returned == null) {
			logger.warning("Not found GRParameter " + name + " for " + declaringClass);
		}*/
		return returned;
	}

	private static Map<Class<?>, Collection<FIBProperty<?>>> cache = new HashMap<>();

	public static Collection<FIBProperty<?>> getFIBProperties(Class<?> declaringClass) {
		Collection<FIBProperty<?>> returned = cache.get(declaringClass);
		if (returned == null) {
			Map<String, FIBProperty<?>> cacheForClass = cachedProperties.get(declaringClass);
			if (cacheForClass == null) {
				cacheForClass = retrieveProperties(declaringClass);
				cachedProperties.put(declaringClass, cacheForClass);
			}
			returned = new ArrayList<>();
			returned.addAll(cacheForClass.values());
			if (declaringClass.getSuperclass() != null) {
				returned.addAll(getFIBProperties(declaringClass.getSuperclass()));
			}
			cache.put(declaringClass, returned);
		}
		return returned;
	}

	private final Field field;
	private String name;
	private Class<T> type;

	private FIBProperty(Field field, PropertyIdentifier p) {
		this.field = field;
		try {
			name = (String) field.get(field.getDeclaringClass());
		} catch (IllegalArgumentException e1) {
			name = field.getName();
		} catch (IllegalAccessException e1) {
			name = field.getName();
		}
		type = (Class<T>) p.type();
		if (p.isPrimitive()) {
			if (type.equals(Integer.class)) {
				type = (Class<T>) Integer.TYPE;
			}
			else if (type.equals(Short.class)) {
				type = (Class<T>) Short.TYPE;
			}
			else if (type.equals(Long.class)) {
				type = (Class<T>) Long.TYPE;
			}
			else if (type.equals(Byte.class)) {
				type = (Class<T>) Byte.TYPE;
			}
			else if (type.equals(Double.class)) {
				type = (Class<T>) Double.TYPE;
			}
			else if (type.equals(Float.class)) {
				type = (Class<T>) Float.TYPE;
			}
			else if (type.equals(Character.class)) {
				type = (Class<T>) Character.TYPE;
			}
			else if (type.equals(Boolean.class)) {
				type = (Class<T>) Boolean.TYPE;
			}
		}
	}

	public String getFieldName() {
		return field.getName();
	}

	public String getName() {
		return name;
	}

	public Class<T> getType() {
		return type;
	}

	public Class<?> getDeclaringClass() {
		return field.getDeclaringClass();
	}

	@Override
	public String toString() {
		return "GRParameter: " + getFieldName() + " " + getName() + " " + getType().getSimpleName();
	}

	public T getDefaultValue() {
		if (type.equals(Integer.TYPE)) {
			return (T) Integer.valueOf(0);
		}
		if (type.equals(Short.TYPE)) {
			return (T) Short.valueOf((short) 0);
		}
		if (type.equals(Long.TYPE)) {
			return (T) Long.valueOf(0);
		}
		if (type.equals(Byte.TYPE)) {
			return (T) Byte.valueOf((byte) 0);
		}
		if (type.equals(Double.TYPE)) {
			return (T) Double.valueOf(0);
		}
		if (type.equals(Float.TYPE)) {
			return (T) Float.valueOf(0);
		}
		if (type.equals(Character.TYPE)) {
			return (T) Character.valueOf('a');
		}
		if (type.equals(Boolean.TYPE)) {
			return (T) Boolean.FALSE;
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FIBProperty<?> other = (FIBProperty<?>) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		}
		else if (!field.equals(other.field))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
			return false;
		return true;
	}

}
