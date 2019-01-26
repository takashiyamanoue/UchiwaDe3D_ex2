package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language;

class ListCell extends LispObject
{
    public LispObject d;
    public LispObject a;
    public boolean isAtom(){
    	return false;
    }
}
