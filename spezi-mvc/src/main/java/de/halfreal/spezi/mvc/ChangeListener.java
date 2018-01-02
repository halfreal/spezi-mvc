package de.halfreal.spezi.mvc;

public interface ChangeListener<T> {
    void onChange(T oldValue, T newValue);
}
