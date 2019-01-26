package org.yamalab.uchiwade3d_ex1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class BitmapMemoryView extends android.view.View
{
    public BitmapMemoryView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public BitmapMemoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public BitmapMemoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

    }

    int[][] bitmap;
    int xMax, yMax;
    public void setBitmap(int [][] x){
        bitmap=x;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmap(canvas);
    }

    private void drawBitmap(Canvas canvas){
        if(bitmap==null) return;
        int ym=bitmap.length;
        int xm=bitmap[0].length;
        xMax=xm; yMax=ym;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        for(int i=0;i<yMax;i++){
            for(int j=0;j<xMax;j++){
                paint.setColor(bitmap[i][j]);
                // 以下の値が小さいと点が見えない可能性があります
                canvas.drawPoint(0+j*7,0+i*7,paint);
            }
        }
        invalidate();
    }

}
