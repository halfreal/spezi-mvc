package de.halfreal.spezi.mvc;

public interface ChangeListener<T> {

	public abstract void onChange(T oldValue, T newValue);

}
