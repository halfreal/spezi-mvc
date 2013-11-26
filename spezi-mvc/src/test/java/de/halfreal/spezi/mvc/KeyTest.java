package de.halfreal.spezi.mvc;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeyTest {

	@Test
	public void testKey() {
		String name = "name";
		Key<String> key = new Key<String>(name);

		assertEquals(name, key.getName());
	}

}
