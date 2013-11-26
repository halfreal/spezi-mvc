package de.halfreal.spezi.mvc;

public class TestCalledListener<T> implements ChangeListener<T> {

	private boolean called = false;

	public boolean isCalled() {
		return called;
	}

	@Override
	public void onChange(T oldValue, T newValue) {
		called = true;
	}

	public void reset() {
		called = false;
	}

}
