package de.halfreal.spezi.mvc;

import org.junit.Assert;
import org.junit.Test;

public class GenericGetTest {

	private static class SimpleModel extends SimpleModelStub {

	}

	private static class SimpleModelStub extends AbstractModel {

		int number;
		String string;

	}

	@Test
	public void testAccessField() {

		SimpleModel simpleModel = new SimpleModel();
		String testString = "test";
		int testNumber = 42;

		simpleModel.string = testString;
		simpleModel.number = testNumber;

		String outString = simpleModel.get(new Key<String>("string"));
		Integer outNumber = simpleModel.get(new Key<Integer>("number"));
		Assert.assertEquals(testString, outString);
		Assert.assertEquals(testNumber, (int) outNumber);

	}
}
