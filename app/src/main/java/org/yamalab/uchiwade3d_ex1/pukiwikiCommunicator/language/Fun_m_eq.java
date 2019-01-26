package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language;

public class Fun_m_eq implements PrimitiveFunction
{
    public ALisp lisp;
    public Fun_m_eq(ALisp l)
    {
        lisp=l;
    }
    public LispObject fun(LispObject proc, LispObject argl)
    {
                MyNumber x=(MyNumber)(lisp.car(argl));
                MyNumber y=(MyNumber)(lisp.second(argl));
                if(x.eq(y)) return lisp.tSymbol;
                else     return lisp.nilSymbol;

    }
}

