package com.example.pefami.mapdemo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pefami.mapdemo.bean.TrackPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pefami on 2016/9/17.
 */
public class TrackDao {
    private TrackDBHelper trackDBHelper;
    public static final String TRACKID = "trackid";
    public static final String LANTITUDE = "lantitude";
    public static final String LONGITUDE = "longitude";
    public static final String TIME = "time";
    public static final String SPEED = "speed";
    public static final String TABLE = "t_point";

    public TrackDao(Context context) {
        trackDBHelper = new TrackDBHelper(context);
    }

    //添加点坐标
    public void addTrackPoint(ContentValues contentValues) {
        SQLiteDatabase writableDatabase = trackDBHelper.getWritableDatabase();
        writableDatabase.insert(TABLE, null, contentValues);
        writableDatabase.close();
    }

    //获取坐标
    public List<TrackPoint> queryTrack() {
        List<TrackPoint> list = new ArrayList<>();
        SQLiteDatabase readableDatabase = trackDBHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.query(TABLE, null, null, null, null, null, TIME);
        while (cursor.moveToNext()) {
            String trackid = cursor.getString(cursor.getColumnIndex(TRACKID));
            double lantitude = cursor.getDouble(cursor.getColumnIndex(LANTITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
            long time = Long.parseLong(cursor.getString(cursor.getColumnIndex(TIME)));
            double speed = cursor.getDouble(cursor.getColumnIndex(SPEED));
            list.add(new TrackPoint(trackid, lantitude, longitude, time, speed));
        }
//        for(TrackPoint t:list){
//            Log.e("Locationg",t.toString());
//        }
        cursor.close();
        readableDatabase.close();
        return list;
    }
}
