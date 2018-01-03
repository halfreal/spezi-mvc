package de.halfreal.spezi.mvc;

public class Key<T> {

	private final String name;

	public Key(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Key && name.equals(((Key<?>) obj).name);
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
