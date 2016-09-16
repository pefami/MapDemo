package com.example.pefami.mapdemo.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pefami on 2016/9/17.
 */
public class TrackDBHelper extends SQLiteOpenHelper {
    public static final String NAME = "track";
    public static final int VERSION = 1;
    public TrackDBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表SQL语句
        String track_table = "create table t_point(_id integer primary key autoincrement,trackid varchar(32),lantitude double ,longitude double,time varchar(32),speed double)";
        //执行SQL语句
        db.execSQL(track_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
