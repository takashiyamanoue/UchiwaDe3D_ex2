package org.yamalab.uchiwade3d_ex1;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bitmap3d_ex1.db";
    private static final int DATABASE_VERSION =1;

    public DatabaseHelper( Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate( SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE bitmap3d_ex1(");
        sb.append("_id INTEGER PRIMARY KEY,");
        sb.append("name TEXT,");
        sb.append("comment TEXT,");
        sb.append("bitimage TEXT");
        sb.append(");");
        String sql = sb.toString(); // SQL の 実行。
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
