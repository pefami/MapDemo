package com.example.pefami.mapdemo;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.pefami.mapdemo.bean.TrackPoint;
import com.example.pefami.mapdemo.dao.TrackDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pefami on 2016/9/16.
 */
public class OverlayUtils {
    private Context mContext;
    public OverlayUtils(Context context){
        mContext=context;
    }
    private  List<Overlay> boundaryLine = new ArrayList<>();
    //显示区域边界
    public  void showBoundary(final BaiduMap baiduMap, String city, String district) {
        DistrictSearch districtSearch = DistrictSearch.newInstance();
        districtSearch.searchDistrict(new DistrictSearchOption().cityName(city).districtName(district));
        //反回结果监听
        districtSearch.setOnDistrictSearchListener(new OnGetDistricSearchResultListener() {
            @Override
            public void onGetDistrictResult(DistrictResult districtResult) {
                //获取边界坐标
                List<List<LatLng>> polylines = districtResult.getPolylines();
                for (List<LatLng> polyline : polylines) {
                    OverlayOptions polylineOptions = new PolylineOptions().points(polyline).width(10).color(Color.BLUE);
                    Overlay overlay = baiduMap.addOverlay(polylineOptions);
                    boundaryLine.add(overlay);
                }
            }
        });
    }
    //隐藏边界
    public  void hideBoundary(){
        for (Overlay overlay:boundaryLine){
            overlay.remove();
        }
        boundaryLine.clear();
    }
    private List<Overlay> historyTrack=new ArrayList<>();
    //有效路径
    private List<Overlay> validTrack=new ArrayList<>();
    private double minSpeed=2;
    private double maxSpeed=10;
    private void showValidTrack(List<TrackPoint> trackPoints,BaiduMap baiduMap){
        //有效定位点集合
        Map<Integer,List<LatLng>> validMap=new HashMap<>();
        int trackID=0;
        boolean isVaild=false;
        for(TrackPoint point:trackPoints){
            double speed=point.getSpeed();
            if(speed>=minSpeed&&speed<=maxSpeed){
                //是有效路径的定位点
                if(isVaild){
                    //说明是该路径的中间点
                    List<LatLng> latLngs = validMap.get(trackID);
                    LatLng newLatlng=new LatLng(point.getLantitude(),point.getLongitude());
                    LatLng oldLatLng = latLngs.get(latLngs.size() - 1);
                    //如果和上一点距离很小，丢弃该点
                    double distance = DistanceUtil.getDistance(oldLatLng, newLatlng);
                    if(distance>10){
                        latLngs.add(newLatlng);
                    }
                }else{
                    //说明是该路径的起点
                    isVaild=true;
                    List<LatLng> latLngs=new ArrayList<>();
                    latLngs.add(new LatLng(point.getLantitude(),point.getLongitude()));
                    validMap.put(trackID,latLngs);
                }

            }else{
                //不是有效路径的定位点
                isVaild=false;
                trackID++;
            }
        }
        //显示轨迹
        Collection<List<LatLng>> values = validMap.values();
        for(List<LatLng> polyline:values){
            if(polyline.size()>1&&baiduMap!=null){
                Log.e("Line",polyline.toString());
                OverlayOptions polylineOptions = new PolylineOptions().points(polyline).width(10).color(Color.GREEN);
                Overlay overlay = baiduMap.addOverlay(polylineOptions);
                validTrack.add(overlay);
            }
        }
    }
    //显示历史轨迹
    public void showHistoryTrack(TrackDao trackDao, BaiduMap baiduMap){
        List<TrackPoint> trackPoints = trackDao.queryTrack();
        Map<String,List<LatLng>> polylines=new HashMap<>();
        //数据处理
        for(TrackPoint point:trackPoints){
            //该轨迹已存在
            if(polylines.containsKey(point.getTrackid())){
                List<LatLng> latLngs = polylines.get(point.getTrackid());
                LatLng newLatlng=new LatLng(point.getLantitude(),point.getLongitude());
                LatLng oldLatLng = latLngs.get(latLngs.size() - 1);
                //如果和上一点距离很小，丢弃该点
                double distance = DistanceUtil.getDistance(oldLatLng, newLatlng);
                if(distance>10){
                    latLngs.add(newLatlng);
                }
            }else{
                //该轨迹不存在
                List<LatLng> latLngs=new ArrayList<>();
                LatLng newLatlng=new LatLng(point.getLantitude(),point.getLongitude());
                latLngs.add(newLatlng);
                polylines.put(point.getTrackid(),latLngs);
            }
        }
        //显示轨迹
        Collection<List<LatLng>> values = polylines.values();
        for(List<LatLng> polyline:values){
            if(polyline.size()>1&&baiduMap!=null){
                OverlayOptions polylineOptions = new PolylineOptions().points(polyline).width(10).color(Color.BLUE);
                Overlay overlay = baiduMap.addOverlay(polylineOptions);
                historyTrack.add(overlay);
            }
        }
        //显示有效轨迹
        showValidTrack(trackPoints,baiduMap);
        Toast.makeText(mContext,"共有"+historyTrack.size()+"条轨迹",Toast.LENGTH_SHORT).show();

    }
    //隐藏历史轨迹
    public  void hideHistoryTrack(){
        for (Overlay overlay:historyTrack){
            overlay.remove();
        }
        historyTrack.clear();
        for (Overlay overlay:validTrack){
            overlay.remove();
        }
        validTrack.clear();
    }

}
