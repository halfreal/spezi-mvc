package de.halfreal.spezi.mvc;

import static org.junit.Assert.*;

public class TestExpectedListener<T> extends TestCalledListener<T> {

	private final T expectedNewValue;

	public TestExpectedListener(T expectedNewValue) {
		super();
		this.expectedNewValue = expectedNewValue;
	}

	@Override
	public void onChange(T oldValue, T newValue) {
		super.onChange(oldValue, newValue);
		assertEquals(expectedNewValue, newValue);
	}

}
