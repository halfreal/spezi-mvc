package de.halfreal.spezi.mvc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class AbstractModel {

	private Map<Key, List<ChangeListener>> listeners;

	protected AbstractModel() {
		listeners = new HashMap<Key, List<ChangeListener>>();
	}

	public <T> void addChangeListener(Key<T> key, ChangeListener<T> listener) {
		List<ChangeListener> keyListeners = findOrCreateKeyListeners(key);
		keyListeners.add(listener);
	}

	private synchronized List<ChangeListener> findOrCreateKeyListeners(Key key) {
		List<ChangeListener> keyListeners = listeners.get(key);
		if (keyListeners == null) {
			keyListeners = new ArrayList<ChangeListener>();
			listeners.put(key, keyListeners);
		}
		return keyListeners;
	}

	@SuppressWarnings("unchecked")
	protected <T> void fireChange(Key<T> key, T oldValue, T newValue) {
		List<ChangeListener> keyListeners = listeners.get(key);
		if (keyListeners != null) {
			for (ChangeListener listener : keyListeners) {
				listener.onChange(oldValue, newValue);
			}
		}
	}

	public <T> void fireUpdate(Key<T> key) {
		T value = get(key);
		fireChange(key, value, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Key<T> key) {
		try {
			Field field = getClass().getDeclaredField(key.getName());
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			Object value = field.get(this);
			return (T) value;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> void removeChangeListener(Key<T> key, ChangeListener<T> listener) {
		List<ChangeListener> keyListeners = listeners.get(key);
		if (keyListeners != null) {
			keyListeners.remove(listener);
		}
	}

	public <T> void set(Key<T> key, T value) {
		try {
			Field field = getClass().getDeclaredField(key.getName());
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			field.set(this, value);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
