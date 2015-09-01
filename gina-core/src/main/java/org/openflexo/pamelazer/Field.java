package org.openflexo.pamelazer;

import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.type.Type;

public class Field {
	private int modifiers;

	private VariableDeclaratorId id;
	private String defaultValue;
	private Type type;
	
	public Field(VariableDeclaratorId id, Type type, int modifiers) {
		this(id, type, modifiers, null);
	}

	public Field(VariableDeclaratorId id, Type type, int modifiers, String defaultValue) {
		this.id = id;
		this.defaultValue = defaultValue;
		this.type = type;
		this.modifiers = modifiers;
	}
	
	public int getModifiers() {
		return modifiers;
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	public VariableDeclaratorId getId() {
		return id;
	}

	public void setId(VariableDeclaratorId id) {
		this.id = id;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public String toString() {
		return getId().getName() + " (" + getType() + ")";
	}
}
