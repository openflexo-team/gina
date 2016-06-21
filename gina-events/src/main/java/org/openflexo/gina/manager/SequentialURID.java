package org.openflexo.gina.manager;

/**
 * A sequentialURID is just an URID composed of a basename and a sequence number.
 * 
 * @author Alexandre
 *
 */
public class SequentialURID implements URID {

	private String baseIdentifier;
	private int sequence;
	
	public SequentialURID(String baseIdentifier) {
		this(baseIdentifier, 0);
	}

	public SequentialURID(String baseIdentifier, int sequence) {
		this.baseIdentifier = baseIdentifier;
		this.sequence = sequence;
	}

	public String getBaseIdentifier() {
		return baseIdentifier;
	}

	public void setBaseIdentifier(String baseIdentifier) {
		this.baseIdentifier = baseIdentifier;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@Override
	public String getIdentifier() {
		return baseIdentifier + "*" + String.valueOf(sequence);
	}
	
	@Override
	public String toString() {
		return getIdentifier();
	}
}
