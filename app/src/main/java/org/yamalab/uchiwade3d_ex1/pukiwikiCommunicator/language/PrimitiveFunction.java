package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language;


import java.lang.*;
public interface PrimitiveFunction
{
    abstract LispObject fun(LispObject proc, LispObject arg);
}
