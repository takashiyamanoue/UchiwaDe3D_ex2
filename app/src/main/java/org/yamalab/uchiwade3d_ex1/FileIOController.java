package org.yamalab.uchiwade3d_ex1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.content.Context;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.database.Cursor;

public class FileIOController  extends AccessoryController
        implements OnClickListener
{
    static final String TAG = "FileIOController";
    private EditText mFileNameView;
    private TextView mSelectedNumber;
    private CheckBox[] mCheckBoxs;
    private TextView[] mFileNames;
    private TextView[] mNumbers;
    private TextView[] mComments;
    private Button mLoadButton;
    private Button mSaveButton;
    private RadioGroup mRadioGroup;
    AdkTwitterActivity mHostActivity;
    private int selectedId;

    private int _fio_item_id;

    private ThreeDimensionInputController m3DInputController;

    FileIOController(AdkTwitterActivity hostActivity) {
        super(hostActivity);
        Log.d(TAG,"FileIOController");
        mHostActivity=hostActivity;
        /* */
        mFileNameView = (EditText) findViewById(R.id.fio_name_field);
//		mAdk2WikiView = (EditText) findViewById(R.id.input_adk_to_wiki_area);
        Log.d(TAG,"InputController-1");

        mCheckBoxs=new CheckBox[5];
        mFileNames=new TextView[5];
        mNumbers=new TextView[5];
        mComments=new TextView[5];
        mNumbers[0]=(TextView) findViewById(R.id.fio_tab_r_0_id);
        mFileNames[0]=(TextView) findViewById(R.id.fio_tab_r_0_name);
        mComments[0]=(TextView) findViewById(R.id.fio_tab_r_0_comment);
        mCheckBoxs[0]=(CheckBox)findViewById(R.id.fio_tab_r_0_rb);
        mNumbers[1]=(TextView) findViewById(R.id.fio_tab_r_1_id);
        mFileNames[1]=(TextView) findViewById(R.id.fio_tab_r_1_name);
        mComments[1]=(TextView) findViewById(R.id.fio_tab_r_1_comment);
        mCheckBoxs[1]=(CheckBox)findViewById(R.id.fio_tab_r_1_rb);
        mNumbers[2]=(TextView) findViewById(R.id.fio_tab_r_2_id);
        mFileNames[2]=(TextView) findViewById(R.id.fio_tab_r_2_name);
        mComments[2]=(TextView) findViewById(R.id.fio_tab_r_2_comment);
        mCheckBoxs[2]=(CheckBox)findViewById(R.id.fio_tab_r_2_rb);
        mNumbers[3]=(TextView) findViewById(R.id.fio_tab_r_3_id);
        mFileNames[3]=(TextView) findViewById(R.id.fio_tab_r_3_name);
        mComments[3]=(TextView) findViewById(R.id.fio_tab_r_3_comment);
        mCheckBoxs[3]=(CheckBox)findViewById(R.id.fio_tab_r_3_rb);
        mNumbers[4]=(TextView) findViewById(R.id.fio_tab_r_4_id);
        mFileNames[4]=(TextView) findViewById(R.id.fio_tab_r_4_name);
        mComments[4]=(TextView) findViewById(R.id.fio_tab_r_4_comment);
        mCheckBoxs[4]=(CheckBox)findViewById(R.id.fio_tab_r_4_rb);

        for(int i=0;i<5;i++){
            mNumbers[i].setText(""+i);
            mCheckBoxs[i].setOnClickListener(this);
        }

        mSelectedNumber=(TextView)findViewById(R.id.fio_selected_field);
        mFileNameView=(EditText)findViewById(R.id.fio_name_field);

        mLoadButton=(Button)findViewById(R.id.fio_load_button);
        mSaveButton=(Button)findViewById(R.id.fio_save_button);

        mLoadButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);

        Log.d(TAG,"-8");
        allLoad();
     }
    protected void onAccesssoryAttached() {
    }

    @Override
    public void onClick(View v) {
        //
        if(this.mHostActivity==null) return;
        if(v==mLoadButton){
            String sx=mSelectedNumber.getText().toString();
            int ix=0;
            try{
                ix=(new Integer(sx)).intValue();
            }
            catch(Exception e){
                Log.d(TAG,"onClick -load error:"+e);
                return;
            }
            loadx(ix);
            return;
        }
        else
        if(v==mSaveButton){
            save();
            return;
        }
        else{
            for(int i=0;i<5;i++){
                CheckBox x=mCheckBoxs[i];
                if(v==x){
                    selectedId =i;
                    mSelectedNumber.setText(""+i);
                    String nx=mFileNames[i].getText().toString();
                    mFileNameView.setText(nx);
                    for(int j=0;j<5;j++){
                        if(j!=selectedId){
                            CheckBox x2=mCheckBoxs[j];
                            x2.setChecked(false);
                            x2.invalidate();
                        }
                    }
                    return;
                }
            }
        }
//		System.out.println("onClick...");
    }

    public void allLoad(){
        for(int i=0;i<5;i++){
            boolean bx=load(i,false);
            if(!bx){
                mFileNames[i].setText("");
                mComments[i].setText("");
            }
        }
    }

    public void loadx(int ix){
        String name=mFileNames[ix].getText().toString();
        if(name.equals("")){
            return;
        }
        boolean bx=load(ix,true);
    }

    public boolean load(int ix,boolean update){
        DatabaseHelper helper=new DatabaseHelper(mHostActivity);
        SQLiteDatabase db=helper.getWritableDatabase();
        if(ix<0) return false;
        if(ix>=5) return false;
        String namex="";
        String commentx="";
        String bitmapx="";
        try {
            String sql = "SELECT * FROM bitmap3d_ex1 WHERE _id = " + ix;
            Cursor cursor = db. rawQuery( sql, null);
            if(cursor.moveToNext()){
                int idex=cursor.getColumnIndex("name");
                namex=cursor.getString(idex);
                idex=cursor.getColumnIndex("comment");
                commentx=cursor.getString(idex);
                idex=cursor.getColumnIndex("bitimage");
                bitmapx=cursor.getString(idex);
            }
            else{
                return false;
            }
        }
        catch(Exception e){
            Log.d(TAG,"fio_save error:"+e);
        }
        finally{
            db.close();
        }
        mFileNames[ix].setText(namex);
        mComments[ix].setText(commentx);
        if(update) updateBitmapImage(bitmapx);
        return true;
    }

    public void save(){
        DatabaseHelper helper=new DatabaseHelper(mHostActivity);
        SQLiteDatabase db=helper.getWritableDatabase();
        _fio_item_id= selectedId;
        String name=mFileNameView.getText().toString();
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        String comment= df.format(date);
        String bitimage=getBitImageString();
        try {
            String sqlDelete="DELETE From bitmap3d_ex1 WHERE _id = ?";
            SQLiteStatement stmt = db.compileStatement(sqlDelete);
            stmt.bindLong(1,_fio_item_id);
            stmt.executeUpdateDelete();
            String sqlInsert = "INSERT INTO bitmap3d_ex1 (_id, name, comment, bitimage) VALUES (?, ?, ?, ?) ";
            stmt=db.compileStatement(sqlInsert);
            stmt.bindLong(1,_fio_item_id);
            stmt.bindString(2,name);
            stmt.bindString(3,comment);
            stmt.bindString(4,bitimage);
            stmt.executeInsert();
        }
        catch(Exception e){
            Log.d(TAG,"fio_save error:"+e);
        }
        finally{
            db.close();
        }
        mFileNames[selectedId].setText(name);
        mComments[selectedId].setText(comment);
    }
    public void set3DEditor(ThreeDimensionInputController x){
        m3DInputController=x;
    }

    private void updateBitmapImage(String x){
        if(m3DInputController==null) return;
        m3DInputController.updateBitmapImage(x);
    }
    private String getBitImageString(){
        if(m3DInputController==null) return "";
        return m3DInputController.getBitImageString();
    }

}
