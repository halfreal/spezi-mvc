package de.halfreal.spezi.mvc.android;

import android.app.ListActivity;
import android.os.Bundle;
import de.halfreal.spezi.mvc.AbstractController;
import de.halfreal.spezi.mvc.AbstractModel;
import de.halfreal.spezi.mvc.ChangeListener;
import de.halfreal.spezi.mvc.Key;
import de.halfreal.spezi.mvc.ListenerRegistry;

public abstract class ListenerListActivity<C extends AbstractController<M>, M extends AbstractModel>
		extends ListActivity {

	protected C controller;
	private boolean createdModelListeners;
	private ListenerRegistry<M> listenerRegistry;
	protected M model;

	public ListenerListActivity(C controller) {
		this.controller = controller;
		this.model = controller.getModel();
	}

	public C getController() {
		return controller;
	}

	public M getModel() {
		return model;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listenerRegistry = new ListenerRegistry<M>(model);
	}

	protected abstract void onCreateModelListeners();

	@Override
	protected void onPause() {
		super.onPause();
		listenerRegistry.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!createdModelListeners) {
			onCreateModelListeners();
			createdModelListeners = true;
		}
		listenerRegistry.onResume();
	}

	public <T> void registerListener(Key<T> key, ChangeListener<T> listener) {
		listenerRegistry.registerListener(key, listener);
	}

}
