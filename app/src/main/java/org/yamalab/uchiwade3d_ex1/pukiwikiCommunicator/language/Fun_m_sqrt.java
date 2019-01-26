package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language;
public class Fun_m_sqrt implements PrimitiveFunction
{
    public ALisp lisp;
    public Fun_m_sqrt(ALisp l)
    {
        lisp=l;
    }
    public LispObject fun(LispObject proc, LispObject argl)
    {
                MyNumber x=(MyNumber)(lisp.car(argl));
                return x.sqrt();
    }
}
