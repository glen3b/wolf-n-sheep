package org.glen_h.libraries;

/**
 * Thrown when the current network state is invalid for a particular method (EXAMPLE: Unknown for a current-network-state-determining [will not exit with unknown] method).
 * @see android.net.NetworkInfo NetworkInfo
 * @author Glen Husman
 */

public class InvalidNetworkStateException extends Exception {

	private static final long serialVersionUID = -8896528670452914970L;
	
	public InvalidNetworkStateException() {
        super();
    }
	
    public InvalidNetworkStateException(String detailMessage) {
        super(detailMessage);
    }
	
}
