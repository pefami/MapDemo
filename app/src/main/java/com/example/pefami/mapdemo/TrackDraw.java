package com.example.pefami.mapdemo;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;

/**
 * Created by pefami on 2016/9/15.
 */
public class TrackDraw {

    private Polyline mVirtureRoad;
    private Marker mStartMarker;
    private Marker mEndMarker;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ArrayList<LatLng> mTrackList = new ArrayList<>();
    private MapView mMapView;
    private BaiduMap baiduMap;
    private double centerLatitude = 22.5750950000;
    private double centerLontitude = 113.8818920000;

    public void initRoadData(MapView mapView, double lat, double lon) {
        mMapView = mapView;
        baiduMap = mapView.getMap();
        centerLatitude = lat;
        centerLontitude = lon;
    }

    //添加位置
    public void addLatLng(LatLng newLatLng) {
        LatLng oldLatLng = mTrackList.get(mTrackList.size() - 1);
        //如果和上一点距离很小，丢弃该点
        double distance = DistanceUtil.getDistance(oldLatLng, newLatLng);
        if(distance>10){
            mTrackList.add(newLatLng);
        }
    }
    //计算距离
    public double distanceTotal(){
        double total=0;
        for(int i=0;i<mTrackList.size()-1;i++){
            double distance=DistanceUtil.getDistance(mTrackList.get(i),mTrackList.get(i+1));
            total+=distance;
        }
        return total;
    }

    private void loopTrackData() {
        baiduMap.clear();
        if (mTrackList.size() == 0) {
            mTrackList.add(new LatLng(centerLatitude, centerLontitude));
        }
        //模拟数据
        LatLng lastLatLng = mTrackList.get(mTrackList.size() - 1);
        mTrackList.add(new LatLng(lastLatLng.latitude + DISTANCE, lastLatLng.longitude + DISTANCE));

        OverlayOptions polylineOptions;
        polylineOptions = new PolylineOptions().points(mTrackList).width(10).color(Color.RED);
        mVirtureRoad = (Polyline) baiduMap.addOverlay(polylineOptions);
        OverlayOptions stMarkerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_st)).position(mTrackList.get(0));
        mStartMarker = (Marker) baiduMap.addOverlay(stMarkerOptions);
        OverlayOptions enMarkerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_en)).position(mTrackList.get(mTrackList.size() - 1)).rotate((float) getAngle(0));
        mEndMarker = (Marker) baiduMap.addOverlay(enMarkerOptions);
    }

    /**
     * 根据点获取图标转的角度
     */
    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
        LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 算取斜率
     */
    private double getSlope(int startIndex) {
        if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
        LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
        return getSlope(startPoint, endPoint);
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;

    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {

        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    /**
     * 计算x方向每次移动的距离
     */
    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    // 通过设置间隔时间和距离可以控制速度和图标移动的距离
    private static final int TIME_INTERVAL = 80;
    private static final double DISTANCE = 0.0001;
    private boolean isNeedLoop = true;

    public void stopMoveLooper() {
        isNeedLoop = false;
    }

    /**
     * 循环进行移动逻辑
     */
    public void moveLooper() {
        isNeedLoop = true;
        new Thread() {
            public void run() {
                while (isNeedLoop) {
                    loopTrackData();//模拟数据
                    mEndMarker.setPosition(mTrackList.get(mTrackList.size() - 1));
                    for (int i = 0; i < mVirtureRoad.getPoints().size() - 1; i++) {

                        final LatLng startPoint = mVirtureRoad.getPoints().get(i);
                        final LatLng endPoint = mVirtureRoad.getPoints().get(i + 1);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // refresh marker's rotate
                                if (mMapView == null) {
                                    return;
                                }
                                mEndMarker.setRotate((float) getAngle(startPoint,
                                        endPoint));
                            }
                        });

                        try {
                            Thread.sleep(TIME_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        }.start();
    }
}
