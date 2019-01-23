package org.yamalab.uchiwade3d_ex1;
import android.app.Activity;
import android.widget.TextView;

public class TextViewWithColor {
    TextView textView;
    int color;
    Activity hostActivity;
    public TextViewWithColor(Activity a, TextView t, int c){
        textView=t;
        color=c;
        hostActivity=a;
    }
    public void setTextView(TextView x){
        textView=x;
    }
    public void setColor(int c){
        color=c;
        textView.setBackgroundColor(c);
     }
    public TextView getTextView(){
        return textView;
    }
    public int getColor(){
        return color;
    }
    public void setWidth(int w){
        textView.setWidth(w);
    }
    public void setHeight(int h){
        textView.setHeight(h);
    }
}
