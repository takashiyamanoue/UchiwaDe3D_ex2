package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language;

public class Fun_reverse implements PrimitiveFunction
{
    public ALisp lisp;
    public Fun_reverse(ALisp l)
    {
        lisp=l;
    }
    public LispObject fun(LispObject proc, LispObject argl)
    {
        return lisp.reverse(lisp.car(argl));
    }
}


