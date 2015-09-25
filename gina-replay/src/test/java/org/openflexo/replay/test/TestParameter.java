package org.openflexo.replay.test;

import org.openflexo.replay.ScenarioNode;

public class TestParameter {
	private int testIndex;
	private ScenarioNode node;

	public TestParameter(int testIndex, ScenarioNode scenarioNode) {
		super();
		this.testIndex = testIndex;
		this.node = scenarioNode;
	}

	public int getTestIndex() {
		return testIndex;
	}

	public void setTestIndex(int testIndex) {
		this.testIndex = testIndex;
	}
	
	public String toString() {
		return this.node.toString();
	}
}
