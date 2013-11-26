package de.halfreal.spezi.mvc;

public class Key<T> {

	private String name;

	public Key(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Key) {
			return name.equals(((Key<?>) obj).name);
		} else {
			return false;
		}
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
