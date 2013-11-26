package de.halfreal.spezi.mvc;

import static org.junit.Assert.*;

import org.junit.Test;

public class AbstractModelTest {

	private static class SimpleModel extends AbstractModel {

		private String string;

		public void setString(String string) {
			String oldValue = this.string;
			this.string = string;
			fireChange(KEY, oldValue, string);
		}

	}

	private static final Key<String> KEY = new Key<String>("string");

	@Test
	public void testGenericGetterAndSetter() {
		SimpleModel model = new SimpleModel();

		String value = "value";
		model.set(KEY, value);

		assertEquals(value, model.get(KEY));
	}

	@Test
	public void testPropertyChangeSupport() {
		testPropertyChangeSupport(null);
		testPropertyChangeSupport("string");
	}

	private void testPropertyChangeSupport(final String newString) {

		SimpleModel model = new SimpleModel();

		TestExpectedListener<String> listener = new TestExpectedListener<String>(
				newString);

		model.addChangeListener(KEY, listener);
		model.setString(newString);

		assertTrue(listener.isCalled());

		model.removeChangeListener(KEY, listener);
		listener.reset();

		model.setString(newString);

		assertFalse(listener.isCalled());
	}

}
