package org.yamalab.uchiwade3d_ex1.twitterconnector;

public interface TwitterApplication {
	public String getOutput();
	public boolean parseCommand(String cmd, String v);
}
