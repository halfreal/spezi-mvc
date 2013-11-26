package de.halfreal.spezi.mvc;

import static org.junit.Assert.*;

import org.junit.Test;

public class ListenerRegistryTest {

	@Test
	public void testListenerLifecycle() {

		TestModel model = new TestModel();
		ListenerRegistry<TestModel> registry = new ListenerRegistry<TestModel>(
				model);
		TestCalledListener<String> testCalledListener = new TestCalledListener<String>();

		registry.registerListener(TestModel.testKey, testCalledListener);
		model.setTestValue("foo");
		assertFalse(testCalledListener.isCalled());

		registry.onResume();
		model.setTestValue("foo");
		assertTrue(testCalledListener.isCalled());

		testCalledListener.reset();
		registry.onPause();
		model.setTestValue("foo");
		assertFalse(testCalledListener.isCalled());

	}

}
