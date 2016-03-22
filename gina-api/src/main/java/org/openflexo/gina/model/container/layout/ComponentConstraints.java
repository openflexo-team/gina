/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.gina.model.container.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBProperty;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.container.FIBPanel.Layout;

/**
 * This abstraction represent the constraints configuration of a component inside a container declaring a particular layout.<br>
 * Stores a dictionary of String values associated to String keys.
 * 
 * @author sylvain
 *
 */
@SuppressWarnings("serial")
public abstract class ComponentConstraints extends Hashtable<String, String> {

	static final Logger LOGGER = Logger.getLogger(FIBComponent.class.getPackage().getName());

	private static final String INDEX = "index";

	public boolean ignoreNotif = false;

	public String getStringRepresentation() {
		StringBuilder returned = new StringBuilder();
		returned.append(getType().name()).append("(");
		boolean isFirst = true;
		List<String> keys = new ArrayList<String>(keySet());
		Collections.sort(keys);
		for (String key : keys) {
			String v = get(key);
			returned.append(isFirst ? "" : ";").append(key).append("=").append(v);
			isFirst = false;
		}
		returned.append(")");
		return returned.toString();
	}

	private FIBComponent component;

	public ComponentConstraints() {
		super();
	}

	protected ComponentConstraints(String someConstraints) {
		this();
		StringTokenizer st = new StringTokenizer(someConstraints, ";");
		while (st.hasMoreTokens()) {
			String next = st.nextToken();
			StringTokenizer st2 = new StringTokenizer(next, "=");
			String key = null;
			String value = null;
			if (st2.hasMoreTokens()) {
				key = st2.nextToken();
			}
			if (st2.hasMoreTokens()) {
				value = st2.nextToken();
			}
			if (key != null && value != null) {
				put(key, value);
			}
		}
	}

	ComponentConstraints(ComponentConstraints someConstraints) {
		this();
		ignoreNotif = true;
		for (String key : someConstraints.keySet()) {
			put(key, someConstraints.get(key));
		}
		ignoreNotif = false;
		component = someConstraints.component;
	}

	@Override
	public synchronized String put(String key, String value) {
		// String oldValue = get(key);
		String returned = super.put(key, value);

		if (component != null && !ignoreNotif) {
			FIBPropertyNotification<ComponentConstraints> notification = new FIBPropertyNotification<ComponentConstraints>(
					(FIBProperty<ComponentConstraints>) FIBProperty.getFIBProperty(FIBComponent.class, FIBComponent.CONSTRAINTS_KEY), null,
					this);
			component.notify(notification);
		}

		return returned;
	}

	protected abstract Layout getType();

	public String getStringValue(String key, String defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			// LOGGER.info("Ben je trouve pas....... pourtant=" + this);
			ignoreNotif = true;
			setStringValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		return stringValue;
	}

	public void setStringValue(String key, String value) {
		put(key, value);
	}

	public <E extends Enum> E getEnumValue(String key, Class<E> enumType, E defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			ignoreNotif = true;
			setEnumValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		for (E en : enumType.getEnumConstants()) {
			if (en.name().equals(stringValue)) {
				return en;
			}
		}
		LOGGER.warning("Found inconsistent value '" + stringValue + "' as " + enumType);
		return defaultValue;
	}

	public void setEnumValue(String key, Enum value) {
		put(key, value.name());
	}

	public int getIntValue(String key, int defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			ignoreNotif = true;
			setIntValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		return Integer.parseInt(stringValue);
	}

	public void setIntValue(String key, int value) {
		put(key, ((Integer) value).toString());
	}

	public float getFloatValue(String key, float defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			ignoreNotif = true;
			setFloatValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		return Float.parseFloat(stringValue);
	}

	public void setFloatValue(String key, float value) {
		put(key, ((Float) value).toString());
	}

	public double getDoubleValue(String key, double defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			ignoreNotif = true;
			setDoubleValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		return Double.parseDouble(stringValue);
	}

	public void setDoubleValue(String key, double value) {
		put(key, ((Double) value).toString());
	}

	public boolean getBooleanValue(String key, boolean defaultValue) {
		String stringValue = get(key);
		if (stringValue == null) {
			ignoreNotif = true;
			setBooleanValue(key, defaultValue);
			ignoreNotif = false;
			return defaultValue;
		}
		return stringValue.equalsIgnoreCase("true");
	}

	public void setBooleanValue(String key, boolean value) {
		put(key, value ? "true" : "false");
	}

	public FIBComponent getComponent() {
		return component;
	}

	public void setComponent(FIBComponent component) {
		this.component = component;
	}

	public final int getIndex() {
		if (hasIndex()) {
			return getIntValue(INDEX, 0);
		}
		return -1;
	}

	public final void setIndex(int x) {
		setIntValue(INDEX, x);
	}

	public final boolean hasIndex() {
		return get(INDEX) != null;
	}

	@Override
	public synchronized String toString() {
		return getClass().getSimpleName() + " " + super.toString();
	}

}
