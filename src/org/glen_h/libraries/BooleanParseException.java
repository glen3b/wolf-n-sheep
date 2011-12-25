package org.glen_h.libraries;

/**
 * Thrown when an attempt to parse a {@code boolean} in a strict environment from a {@code String} fails (the string is invalid).
 * @see java.lang.Boolean boolean
 * @author Glen Husman
 */

public class BooleanParseException extends Exception {
	
	private static final long serialVersionUID = -995845773606727044L;

	public BooleanParseException() {
        super();
    }
	
    public BooleanParseException(String detailMessage) {
        super(detailMessage);
    }
	
}
