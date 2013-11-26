package de.halfreal.spezi.mvc.sample.java;

import static org.junit.Assert.*;

import org.junit.Test;

public class ModelTest {

	@Test
	public void generatedClassExists() {
		try {
			Class.forName("de.halfreal.spezi.mvc.sample.java.Model");
		} catch (ClassNotFoundException e) {
			assertTrue(
					"Class de.halfreal.spezi.mvc.sample.java.Model not found",
					false);
		}
	}

}
