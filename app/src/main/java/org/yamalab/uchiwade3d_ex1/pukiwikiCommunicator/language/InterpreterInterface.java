package org.yamalab.uchiwade3d_ex1.pukiwikiCommunicator.language;

import android.widget.EditText;
import android.widget.CheckBox;

public interface InterpreterInterface
{
	public EditText getOutputText();
	public boolean isTracing();
	public String parseCommand(String x);
	public InterpreterInterface lookUp(String x);
}
