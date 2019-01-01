package org.yamalab.uchiwade3d_ex1;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ThreeDimensionInputController extends AccessoryController
        implements OnClickListener,
                android.widget.SeekBar.OnSeekBarChangeListener
{

    final String TAG = "3DInputController";
    private AdkTwitterActivity mHostActivity;
    private android.widget.SeekBar mPBar_red, mPBar_green, mPBar_blue;
    private TextViewWithColor inputSliceImage[][];
    private TextViewWithColor presetColors[];
    int tDImage[][][];

    private int[][] mBitMap_1;
    private int[][] mBitMap_2;

    private Button m3DViewButton;
    private Button mReturnButton;
    private EditText mDepthField;
    private Button mDepthSetButton;
    private Button mUpButton;
    private Button mDownButton;
    private TextViewWithColor mColorIndicator;
    private ImageView mDownArrowView_1;
    private ImageView mUpArrowView_1;
    private ImageView mDownArrowView_2;
    private ImageView mUpArrowView_2;
    private View mBitMapMemory_1;
    private View mBitMapMemory_2;
    private Button mClearButton;

    private int imageDepth;

    ThreeDimensionInputController(AdkTwitterActivity hostActivity){
        super(hostActivity);
        mHostActivity=hostActivity;
        Log.d(TAG,"3DInputController");

    /* */
//		mAdk2WikiView = (EditText) findViewById(R.id.input_adk_to_wiki_area);
		Log.d(TAG,"3DInputController-1");
    /* */
		Log.d(TAG,"3DInputController-8");
    }

    public void init(){

        if(tDImage!=null) return;
        tDImage=new int[16][16][16];

        mBitMap_1=new int[16][16];
        mBitMap_2=new int[16][16];

        inputSliceImage=new TextViewWithColor[16][16];
        inputSliceImage[0][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_00),0);
        inputSliceImage[0][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_01),0);
        inputSliceImage[0][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_02),0);
        inputSliceImage[0][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_03),0);
        inputSliceImage[0][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_04),0);
        inputSliceImage[0][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_05),0);
        inputSliceImage[0][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_06),0);
        inputSliceImage[0][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_07),0);
        inputSliceImage[0][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_08),0);
        inputSliceImage[0][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_09),0);
        inputSliceImage[0][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_10),0);
        inputSliceImage[0][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_11),0);
        inputSliceImage[0][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_12),0);
        inputSliceImage[0][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_13),0);
        inputSliceImage[0][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_14),0);
        inputSliceImage[0][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_00_15),0);

        inputSliceImage[1][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_00),0);
        inputSliceImage[1][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_01),0);
        inputSliceImage[1][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_02),0);
        inputSliceImage[1][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_03),0);
        inputSliceImage[1][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_04),0);
        inputSliceImage[1][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_05),0);
        inputSliceImage[1][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_06),0);
        inputSliceImage[1][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_07),0);
        inputSliceImage[1][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_08),0);
        inputSliceImage[1][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_09),0);
        inputSliceImage[1][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_10),0);
        inputSliceImage[1][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_11),0);
        inputSliceImage[1][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_12),0);
        inputSliceImage[1][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_13),0);
        inputSliceImage[1][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_14),0);
        inputSliceImage[1][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_01_15),0);

        inputSliceImage[2][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_00),0);
        inputSliceImage[2][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_01),0);
        inputSliceImage[2][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_02),0);
        inputSliceImage[2][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_03),0);
        inputSliceImage[2][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_04),0);
        inputSliceImage[2][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_05),0);
        inputSliceImage[2][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_06),0);
        inputSliceImage[2][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_07),0);
        inputSliceImage[2][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_08),0);
        inputSliceImage[2][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_09),0);
        inputSliceImage[2][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_10),0);
        inputSliceImage[2][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_11),0);
        inputSliceImage[2][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_12),0);
        inputSliceImage[2][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_13),0);
        inputSliceImage[2][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_14),0);
        inputSliceImage[2][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_02_15),0);

        inputSliceImage[3][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_00),0);
        inputSliceImage[3][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_01),0);
        inputSliceImage[3][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_02),0);
        inputSliceImage[3][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_03),0);
        inputSliceImage[3][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_04),0);
        inputSliceImage[3][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_05),0);
        inputSliceImage[3][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_06),0);
        inputSliceImage[3][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_07),0);
        inputSliceImage[3][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_08),0);
        inputSliceImage[3][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_09),0);
        inputSliceImage[3][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_10),0);
        inputSliceImage[3][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_11),0);
        inputSliceImage[3][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_12),0);
        inputSliceImage[3][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_13),0);
        inputSliceImage[3][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_14),0);
        inputSliceImage[3][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_03_15),0);

        inputSliceImage[4][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_00),0);
        inputSliceImage[4][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_01),0);
        inputSliceImage[4][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_02),0);
        inputSliceImage[4][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_03),0);
        inputSliceImage[4][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_04),0);
        inputSliceImage[4][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_05),0);
        inputSliceImage[4][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_06),0);
        inputSliceImage[4][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_07),0);
        inputSliceImage[4][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_08),0);
        inputSliceImage[4][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_09),0);
        inputSliceImage[4][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_10),0);
        inputSliceImage[4][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_11),0);
        inputSliceImage[4][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_12),0);
        inputSliceImage[4][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_13),0);
        inputSliceImage[4][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_14),0);
        inputSliceImage[4][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_04_15),0);

        inputSliceImage[5][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_00),0);
        inputSliceImage[5][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_01),0);
        inputSliceImage[5][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_02),0);
        inputSliceImage[5][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_03),0);
        inputSliceImage[5][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_04),0);
        inputSliceImage[5][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_05),0);
        inputSliceImage[5][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_06),0);
        inputSliceImage[5][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_07),0);
        inputSliceImage[5][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_08),0);
        inputSliceImage[5][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_09),0);
        inputSliceImage[5][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_10),0);
        inputSliceImage[5][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_11),0);
        inputSliceImage[5][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_12),0);
        inputSliceImage[5][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_13),0);
        inputSliceImage[5][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_14),0);
        inputSliceImage[5][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_05_15),0);

        inputSliceImage[6][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_00),0);
        inputSliceImage[6][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_01),0);
        inputSliceImage[6][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_02),0);
        inputSliceImage[6][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_03),0);
        inputSliceImage[6][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_04),0);
        inputSliceImage[6][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_05),0);
        inputSliceImage[6][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_06),0);
        inputSliceImage[6][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_07),0);
        inputSliceImage[6][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_08),0);
        inputSliceImage[6][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_09),0);
        inputSliceImage[6][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_10),0);
        inputSliceImage[6][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_11),0);
        inputSliceImage[6][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_12),0);
        inputSliceImage[6][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_13),0);
        inputSliceImage[6][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_14),0);
        inputSliceImage[6][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_06_15),0);

        inputSliceImage[7][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_00),0);
        inputSliceImage[7][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_01),0);
        inputSliceImage[7][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_02),0);
        inputSliceImage[7][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_03),0);
        inputSliceImage[7][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_04),0);
        inputSliceImage[7][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_05),0);
        inputSliceImage[7][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_06),0);
        inputSliceImage[7][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_07),0);
        inputSliceImage[7][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_08),0);
        inputSliceImage[7][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_09),0);
        inputSliceImage[7][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_10),0);
        inputSliceImage[7][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_11),0);
        inputSliceImage[7][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_12),0);
        inputSliceImage[7][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_13),0);
        inputSliceImage[7][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_14),0);
        inputSliceImage[7][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_07_15),0);

        inputSliceImage[8][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_00),0);
        inputSliceImage[8][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_01),0);
        inputSliceImage[8][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_02),0);
        inputSliceImage[8][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_03),0);
        inputSliceImage[8][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_04),0);
        inputSliceImage[8][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_05),0);
        inputSliceImage[8][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_06),0);
        inputSliceImage[8][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_07),0);
        inputSliceImage[8][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_08),0);
        inputSliceImage[8][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_09),0);
        inputSliceImage[8][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_10),0);
        inputSliceImage[8][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_11),0);
        inputSliceImage[8][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_12),0);
        inputSliceImage[8][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_13),0);
        inputSliceImage[8][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_14),0);
        inputSliceImage[8][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_08_15),0);

        inputSliceImage[9][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_00),0);
        inputSliceImage[9][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_01),0);
        inputSliceImage[9][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_02),0);
        inputSliceImage[9][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_03),0);
        inputSliceImage[9][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_04),0);
        inputSliceImage[9][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_05),0);
        inputSliceImage[9][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_06),0);
        inputSliceImage[9][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_07),0);
        inputSliceImage[9][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_08),0);
        inputSliceImage[9][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_09),0);
        inputSliceImage[9][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_10),0);
        inputSliceImage[9][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_11),0);
        inputSliceImage[9][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_12),0);
        inputSliceImage[9][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_13),0);
        inputSliceImage[9][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_14),0);
        inputSliceImage[9][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_09_15),0);

        inputSliceImage[10][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_00),0);
        inputSliceImage[10][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_01),0);
        inputSliceImage[10][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_02),0);
        inputSliceImage[10][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_03),0);
        inputSliceImage[10][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_04),0);
        inputSliceImage[10][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_05),0);
        inputSliceImage[10][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_06),0);
        inputSliceImage[10][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_07),0);
        inputSliceImage[10][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_08),0);
        inputSliceImage[10][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_09),0);
        inputSliceImage[10][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_10),0);
        inputSliceImage[10][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_11),0);
        inputSliceImage[10][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_12),0);
        inputSliceImage[10][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_13),0);
        inputSliceImage[10][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_14),0);
        inputSliceImage[10][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_10_15),0);

        inputSliceImage[11][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_00),0);
        inputSliceImage[11][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_01),0);
        inputSliceImage[11][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_02),0);
        inputSliceImage[11][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_03),0);
        inputSliceImage[11][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_04),0);
        inputSliceImage[11][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_05),0);
        inputSliceImage[11][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_06),0);
        inputSliceImage[11][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_07),0);
        inputSliceImage[11][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_08),0);
        inputSliceImage[11][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_09),0);
        inputSliceImage[11][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_10),0);
        inputSliceImage[11][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_11),0);
        inputSliceImage[11][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_12),0);
        inputSliceImage[11][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_13),0);
        inputSliceImage[11][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_14),0);
        inputSliceImage[11][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_11_15),0);

        inputSliceImage[12][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_00),0);
        inputSliceImage[12][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_01),0);
        inputSliceImage[12][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_02),0);
        inputSliceImage[12][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_03),0);
        inputSliceImage[12][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_04),0);
        inputSliceImage[12][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_05),0);
        inputSliceImage[12][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_06),0);
        inputSliceImage[12][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_07),0);
        inputSliceImage[12][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_08),0);
        inputSliceImage[12][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_09),0);
        inputSliceImage[12][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_10),0);
        inputSliceImage[12][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_11),0);
        inputSliceImage[12][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_12),0);
        inputSliceImage[12][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_13),0);
        inputSliceImage[12][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_14),0);
        inputSliceImage[12][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_12_15),0);

        inputSliceImage[13][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_00),0);
        inputSliceImage[13][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_01),0);
        inputSliceImage[13][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_02),0);
        inputSliceImage[13][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_03),0);
        inputSliceImage[13][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_04),0);
        inputSliceImage[13][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_05),0);
        inputSliceImage[13][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_06),0);
        inputSliceImage[13][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_07),0);
        inputSliceImage[13][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_08),0);
        inputSliceImage[13][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_09),0);
        inputSliceImage[13][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_10),0);
        inputSliceImage[13][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_11),0);
        inputSliceImage[13][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_12),0);
        inputSliceImage[13][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_13),0);
        inputSliceImage[13][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_14),0);
        inputSliceImage[13][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_13_15),0);

        inputSliceImage[14][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_00),0);
        inputSliceImage[14][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_01),0);
        inputSliceImage[14][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_02),0);
        inputSliceImage[14][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_03),0);
        inputSliceImage[14][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_04),0);
        inputSliceImage[14][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_05),0);
        inputSliceImage[14][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_06),0);
        inputSliceImage[14][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_07),0);
        inputSliceImage[14][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_08),0);
        inputSliceImage[14][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_09),0);
        inputSliceImage[14][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_10),0);
        inputSliceImage[14][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_11),0);
        inputSliceImage[14][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_12),0);
        inputSliceImage[14][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_13),0);
        inputSliceImage[14][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_14),0);
        inputSliceImage[14][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_14_15),0);

        inputSliceImage[15][0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_00),0);
        inputSliceImage[15][1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_01),0);
        inputSliceImage[15][2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_02),0);
        inputSliceImage[15][3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_03),0);
        inputSliceImage[15][4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_04),0);
        inputSliceImage[15][5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_05),0);
        inputSliceImage[15][6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_06),0);
        inputSliceImage[15][7]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_07),0);
        inputSliceImage[15][8]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_08),0);
        inputSliceImage[15][9]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_09),0);
        inputSliceImage[15][10]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_10),0);
        inputSliceImage[15][11]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_11),0);
        inputSliceImage[15][12]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_12),0);
        inputSliceImage[15][13]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_13),0);
        inputSliceImage[15][14]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_14),0);
        inputSliceImage[15][15]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.button_15_15),0);

        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                TextView x=(inputSliceImage[i][j]).getTextView();
				x.setOnClickListener(this);
            }
        }

        presetColors=new TextViewWithColor[7];
        presetColors[0]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.color_white),0xffffffff);
        presetColors[1]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.color_red),0xffff0000);
        presetColors[2]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.color_green),0xff00ff00);
        presetColors[3]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.color_blue),0xff0000ff);
        presetColors[4]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.color_yellow),0xffffff00);
        presetColors[5]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.color_magenta),0xff00ffff);
        presetColors[6]=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.color_cyan),0xffff00ff);
        for(int i=0;i<7;i++){
            (presetColors[i].getTextView()).setOnClickListener(this);
        }

        imageDepth=0;

        Log.d(TAG,"3DInputController-5");

        mDepthField = (EditText) findViewById(R.id.depthIndicator);

        mReturnButton=(Button)findViewById(R.id.return_to_main);
        m3DViewButton=(Button)findViewById(R.id.three_d_view_button);
        mDepthSetButton=(Button)findViewById(R.id.set_button);
        mUpButton=(Button)findViewById(R.id.up_button);
        mDownButton=(Button)findViewById(R.id.down_button);
        mColorIndicator=new TextViewWithColor(mHostActivity,(TextView)findViewById(R.id.color_indicator),0);
        mClearButton=(Button)findViewById(R.id.clearButton);

        mDepthField.setOnClickListener(this);
        mReturnButton.setOnClickListener(this);
        mDepthSetButton.setOnClickListener(this);
        mUpButton.setOnClickListener(this);
        mDownButton.setOnClickListener(this);
        (mColorIndicator.getTextView()).setOnClickListener(this);
        mClearButton.setOnClickListener(this);

        EditText depthString=(EditText)findViewById(R.id.depthIndicator);
        depthString.setText("0");

        Log.d(TAG,"3DInputController-6");
        mPBar_red=(SeekBar)findViewById(R.id.seekBar_red);
        mPBar_green=(SeekBar)findViewById(R.id.seekBar_green);
        mPBar_blue=(SeekBar)findViewById(R.id.seekBar_blue);
        mPBar_red.setOnSeekBarChangeListener(this);
        mPBar_green.setOnSeekBarChangeListener(this);
        mPBar_blue.setOnSeekBarChangeListener(this);
        mDownArrowView_1=(ImageView)findViewById(R.id.downArrow_1);
        mDownArrowView_2=(ImageView)findViewById(R.id.downArrow_2);
        mUpArrowView_1=(ImageView)findViewById(R.id.upArrow_1);
        mUpArrowView_2=(ImageView)findViewById(R.id.upArrow_2);

        mBitMapMemory_1=(View)findViewById(R.id.bitmap_memory_1);
        mBitMapMemory_2=(View)findViewById(R.id.bitmap_memory_2);

        mDownArrowView_1.setOnClickListener(this);
        mDownArrowView_2.setOnClickListener(this);
        mUpArrowView_1.setOnClickListener(this);
        mUpArrowView_2.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if(this.mHostActivity==null) return;
        if(v==mReturnButton){
//            setContentView(R.layout.main);
            mHostActivity.showControls();
            return;
        }
        else
        if(v==m3DViewButton){
            updateSliceImage(imageDepth);
//            setContentView(R.layout.x);
            return;
        }
        else
        if(v==mUpButton){
            String x=mDepthField.getText().toString();
            int d=0;
            try{
                d=(new Integer(x)).intValue();
            }
            catch(Exception e){

            }
            imageDepth=d;
            updateSliceImage(imageDepth);
            if(imageDepth<=0) {
                imageDepth=0;
            }
            else
            if(imageDepth>=15){
                imageDepth=14;
            }
            else {
                imageDepth--;
            }
            setSliceImage(imageDepth);
            mDepthField.setText(""+imageDepth);
            return;
        }
        else
        if(v==mDownButton){
            String x=mDepthField.getText().toString();
            int d=0;
            try{
                d=(new Integer(x)).intValue();
            }
            catch(Exception e){

            }
            imageDepth=d;
            updateSliceImage(imageDepth);
            if(imageDepth<=0){
                imageDepth=1;
            }
            else
            if(imageDepth>=15){
                imageDepth=15;
            }
            else {
                imageDepth++;
            }
            setSliceImage(imageDepth);
            mDepthField.setText(""+imageDepth);
            return;
        }
        else
        if(v==mDownArrowView_1){
            copy2Memory(mBitMap_1,(BitmapMemoryView)mBitMapMemory_1);
            return;
        }
        else
        if(v==mDownArrowView_2){
            copy2Memory(mBitMap_2,(BitmapMemoryView)mBitMapMemory_2);
            return;
        }
        else
        if(v==mUpArrowView_1){
            copyFromMemory(mBitMap_1);
            return;
        }
        else
        if(v==mUpArrowView_2){
            copyFromMemory(mBitMap_2);
            return;
        }
        else
        if(v==mClearButton){
            for(int i=0;i<16;i++){
                for(int j=0;j<16;j++){
                    inputSliceImage[i][j].setColor(0);
                }
            }
            return;
        }
        else
        {
            for(int i=0;i<16;i++){
                for(int j=0;j<16;j++){
                    /* */
                    TextView bx=(inputSliceImage[i][j]).getTextView();
                    int cx=(inputSliceImage[i][j]).getColor();
                    if(v==bx) {
                        try {
                            if (cx == 0) {
                                int c2x=mColorIndicator.getColor();
                                (inputSliceImage[i][j]).setColor(c2x);
                                bx.setBackgroundColor(c2x);
                                updateSliceImage(imageDepth);
                                return;
                            } else {
                                bx.setBackgroundColor(0);
                                (inputSliceImage[i][j]).setColor(0);
                                updateSliceImage(imageDepth);
                                return;
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        return;
                    }
                    /* */
                }
            }
            for(int i=0;i<7;i++){
                TextView tx=presetColors[i].getTextView();
                if(v==tx){
                    int cx=presetColors[i].getColor();
                    int rx=(cx & 0x00ff0000)>>16;
                    int px=rx*100/(0xff);
                    mPBar_red.setProgress(px);
                    int gx=(cx & 0x0000ff00)>>8;
                    px=gx*100/(0xff);
                    mPBar_green.setProgress(px);
                    int bx=(cx & 0x000000ff);
                    px=bx*100/(0xff);
                    mPBar_blue.setProgress(px);
                }
            }

        }
//		System.out.println("onClick...");
    }

    void setSliceImage(int d){
        /* */
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                int x=tDImage[i][j][d];
                TextView bx=(inputSliceImage[i][j]).getTextView();
                (inputSliceImage[i][j]).setColor(x);
            }
        }
        /* */
    }
    void updateSliceImage(int d){
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                /* */
                int cc=(inputSliceImage[i][j]).getColor();
//                ColorDrawable colorDrawable = (ColorDrawable) bx.getBackground();
                tDImage[i][j][d]=cc;
                /* */
            }
        }
    }

    void copy2Memory(int[][] mem, BitmapMemoryView bmv){
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                /* */
                int cc=(inputSliceImage[i][j]).getColor();
//                ColorDrawable colorDrawable = (ColorDrawable) bx.getBackground();
                mem[i][j]=cc;
                /* */
            }
        }
        bmv.setBitmap(mem);
    }

    void copyFromMemory(int[][] mem){
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                /* */
                int cc=mem[i][j];
                (inputSliceImage[i][j]).setColor(cc);
            }
        }

    }

    void drawBitMap(int[][] mem, View v){

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        i=i*255/100;
        if(seekBar==mPBar_red){
            int cx=mColorIndicator.getColor();
            int nc=(cx & 0x0000ffff) | ((i & 0x000000ff)<<16)|0xff000000;
            mColorIndicator.setColor(nc);
        }
        else
        if(seekBar==mPBar_green){
            int cx=mColorIndicator.getColor();
            int nc=(cx & 0x00ff00ff) | ((i & 0x000000ff)<<8)|0xff000000;
            mColorIndicator.setColor(nc);
        }
        else
        if(seekBar==mPBar_blue){
            int cx=mColorIndicator.getColor();
            int nc=(cx & 0x00ffff00) | (i & 0x000000ff)|0xff000000;
            mColorIndicator.setColor(nc);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG,"onStartTrackingTauch");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG,"onStopTrackingTouch");
    }

    protected void onAccesssoryAttached() {
        Log.d(TAG,"onAccessoryAttached");
    }

}
