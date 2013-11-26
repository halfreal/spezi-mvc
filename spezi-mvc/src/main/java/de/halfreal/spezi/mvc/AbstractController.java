package de.halfreal.spezi.mvc;

public abstract class AbstractController<M extends AbstractModel> {

	protected M model;

	protected AbstractController(M model) {
		if (model == null) {
			throw new IllegalArgumentException("Model must not be null");
		}
		this.model = model;
	}

	public M getModel() {
		return model;
	}

}
