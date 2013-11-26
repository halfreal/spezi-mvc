package de.halfreal.spezi.mvc;

public abstract class UpdateListener<T> implements ChangeListener<T> {

	@Override
	public void onChange(T oldValue, T newValue) {
		onUpdate(newValue);
	}

	public abstract void onUpdate(T newValue);

}
