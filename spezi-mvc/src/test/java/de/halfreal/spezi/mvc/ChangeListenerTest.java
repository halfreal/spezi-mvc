package de.halfreal.spezi.mvc;

import static org.junit.Assert.*;

import org.junit.Test;

public class ChangeListenerTest {

	@Test
	public void testChangeListener() {
		final String newString = "newString";

		TestExpectedListener<String> listener = new TestExpectedListener<String>(
				newString);

		listener.onChange(null, newString);

		assertTrue(listener.isCalled());
	}

}
