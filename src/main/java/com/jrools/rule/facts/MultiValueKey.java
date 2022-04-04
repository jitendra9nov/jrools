/* (C) 2022 */
package com.jrools.rule.facts;

import static java.util.Arrays.deepEquals;
import static java.util.Arrays.deepHashCode;
import static org.apache.commons.lang3.builder.CompareToBuilder.reflectionCompare;

public class MultiValueKey  implements Comparable<MultiValueKey>{
	
	private Object [] values;
	
	public MultiValueKey(Object [] values) {
		this.values=values;
	}

	
	/**
	 * @return the values
	 */
	public Object[] getValues() {
		return values;
	}


	/**
	 * @param values the values to set
	 */
	public void setValues(Object[] values) {
		this.values = values;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + deepHashCode(values);
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
		MultiValueKey other = (MultiValueKey) obj;
		if (!deepEquals(values, other.values))
			return false;
		return true;
	}


	@Override
	public int compareTo(MultiValueKey o) {
		return reflectionCompare(this, o);
	}
	
	

}
