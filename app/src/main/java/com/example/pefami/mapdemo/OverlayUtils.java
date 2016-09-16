package com.example.pefami.mapdemo;

import android.graphics.Color;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pefami on 2016/9/16.
 */
public class OverlayUtils {
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
                    Log.e("Location","=====");
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
}
