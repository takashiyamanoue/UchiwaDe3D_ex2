package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language;
public class Fun_atom implements PrimitiveFunction
{
    public ALisp lisp;
    public Fun_atom(ALisp l)
    {
        lisp=l;
    }
    public LispObject fun(LispObject proc, LispObject argl)
    {
        return lisp.atom2(lisp.car(argl));
    }
}
