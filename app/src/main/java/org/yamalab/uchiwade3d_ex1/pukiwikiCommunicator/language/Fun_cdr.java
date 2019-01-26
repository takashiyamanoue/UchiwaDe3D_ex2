package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language;

public class Fun_cdr implements PrimitiveFunction
{
    public ALisp lisp;
    public Fun_cdr(ALisp l)
    {
        lisp=l;
    }
    public LispObject fun(LispObject proc, LispObject argl)
    {
        return lisp.cdr(lisp.car(argl));
    }
}

