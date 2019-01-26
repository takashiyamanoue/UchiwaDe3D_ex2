package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language;

public class Atom extends LispObject
{
	private static String kind="atom";
	public boolean isAtom(){
		return true;
	}
	public boolean isKind(String x){
		return x.equals(kind);
	}
}


