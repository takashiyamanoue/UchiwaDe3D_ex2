package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language;

public class Fun_m_atan implements PrimitiveFunction
{
    public ALisp lisp;
    public Fun_m_atan(ALisp l)
    {
        lisp=l;
    }
    public LispObject fun(LispObject proc, LispObject argl)
    {
                MyNumber x=(MyNumber)(lisp.car(argl));
                return x.atan();
    }
}
