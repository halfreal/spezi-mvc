package de.halfreal.spezi.mvc.android;

public interface ValidateLifecycle {

	void initialNetworkCall();

	void invalidate();

	boolean isValid();

	void notifyDataChanged();

	void validate();

}
