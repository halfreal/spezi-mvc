package de.halfreal.spezi.mvc.android;

public interface ValidationLifecycle {

	void initialNetworkCall();

	void invalidate();

	boolean isValid();

	void notifyDataChanged();

	void validate();

}
