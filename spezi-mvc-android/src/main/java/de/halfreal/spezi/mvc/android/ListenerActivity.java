package de.halfreal.spezi.mvc.android;

import android.app.Activity;
import android.os.Bundle;
import de.halfreal.spezi.mvc.AbstractController;
import de.halfreal.spezi.mvc.AbstractModel;
import de.halfreal.spezi.mvc.ChangeListener;
import de.halfreal.spezi.mvc.Key;
import de.halfreal.spezi.mvc.ListenerRegistry;

public abstract class ListenerActivity<C extends AbstractController<M>, M extends AbstractModel>
		extends Activity implements ValidateLifecycle {

	protected C controller;
	private boolean initialNetworkCall;
	private ListenerRegistry<M> listenerRegistry;
	protected M model;
	private boolean valid;
	private Runnable validationRunning;

	public ListenerActivity(C controller) {
		this.controller = controller;
		this.model = controller.getModel();
		initialNetworkCall = false;
		valid = false;
	}

	public C getController() {
		return controller;
	}

	public M getModel() {
		return model;
	}

	@Override
	public void initialNetworkCall() {
	}

	@Override
	public void invalidate() {
		valid = false;
		if (validationRunning != null) {
			return;
		}
		validationRunning = new Runnable() {

			@Override
			public void run() {
				validate();
				validationRunning = null;
			}
		};
		runOnUiThread(validationRunning);
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public void notifyDataChanged() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listenerRegistry = new ListenerRegistry<M>(model);
		onCreateModelListeners();

	}

	protected abstract void onCreateModelListeners();

	@Override
	public void onPause() {
		super.onPause();
		listenerRegistry.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		listenerRegistry.onResume();

		if (!initialNetworkCall) {
			initialNetworkCall();
			initialNetworkCall = true;
		}
		validate();
	}

	public <T> void registerListener(Key<T> key, ChangeListener<T> listener) {
		listenerRegistry.registerListener(key, listener);
	}

	@Override
	public void validate() {
		if (!valid) {
			notifyDataChanged();
			valid = true;
		}
	}

}
