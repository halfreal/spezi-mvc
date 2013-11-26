package de.halfreal.spezi.mvc;

public class TestModel extends AbstractModel {

	public static final Key<String> testKey = new Key<String>("testValue");

	private String testValue;

	public String getTestValue() {
		return testValue;
	}

	public void setTestValue(String testValue) {
		String oldValue = this.testValue;
		this.testValue = testValue;
		fireChange(testKey, oldValue, testValue);
	}

}
