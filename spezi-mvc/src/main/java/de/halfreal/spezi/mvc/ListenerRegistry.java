package de.halfreal.spezi.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ListenerRegistry<M extends AbstractModel> {

	@SuppressWarnings("rawtypes")
	private Map<Key, ChangeListener> listeners;
	private M model;

	@SuppressWarnings("rawtypes")
	public ListenerRegistry(M model) {
		this.model = model;
		listeners = new HashMap<Key, ChangeListener>();
	}

	public void onPause() {
		unregisterListeners();
	};

	public void onResume() {
		registerListeners();
	}

	public <T> void registerListener(Key<T> key, ChangeListener<T> listener) {
		listeners.put(key, listener);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerListeners() {
		for (Entry<Key, ChangeListener> entry : listeners.entrySet()) {
			model.addChangeListener(entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void unregisterListeners() {
		for (Entry<Key, ChangeListener> entry : listeners.entrySet()) {
			model.removeChangeListener(entry.getKey(), entry.getValue());
		}
	}

}
