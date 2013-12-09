package de.halfreal.spezi.mvc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("rawtypes")
public abstract class AbstractModel {

	public static Field findField(String name, Class<?> type)
			throws NoSuchFieldException {
		Field declaredField = null;
		try {
			declaredField = type.getDeclaredField(name);
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
			if (type.getSuperclass() == null) {
				throw e;
			}
		}
		if (declaredField != null) {
			return declaredField;
		} else if (type.getSuperclass() != null) {
			return findField(name, type.getSuperclass());
		} else {
			return null;
		}
	}

	private Map<Key, List<ChangeListener>> listeners;

	protected AbstractModel() {
		listeners = new HashMap<Key, List<ChangeListener>>();
	}

	public <T> void addChangeListener(Key<T> key, ChangeListener<T> listener) {
		List<ChangeListener> keyListeners = findOrCreateKeyListeners(key);
		keyListeners.add(listener);
	}

	public <T> Field findField(Key<T> key) throws NoSuchFieldException {
		return findField(key.getName(), getClass());
	}

	private synchronized List<ChangeListener> findOrCreateKeyListeners(Key key) {
		List<ChangeListener> keyListeners = listeners.get(key);
		if (keyListeners == null) {
			keyListeners = new CopyOnWriteArrayList<ChangeListener>();
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
			Field field = findField(key);
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
			Field field = findField(key);
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			@SuppressWarnings("unchecked")
			T oldValue = (T) field.get(this);
			field.set(this, value);
			fireChange(key, oldValue, value);
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
