package com.example.pefami.mapdemo;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mapView;
    private Button btn_location;
    private Button btn_start;
    private Button btn_heat;
    private Button btn_boundary;
    private TextView tv_totaldis;
    private TextView tv_speed;

    private volatile boolean isFristLocation = true;
    private String provider;
    private BaiduMap baiduMap;
    private LocationManager locationManager;
    private LocationClient mLocationClient;//定位客户端
    public MyLocationListener mMyLocationListener;//定位的监听器
    private TrackManger trackManger;//轨迹管理
    private double mCurrentLantitude;
    private double mCurrentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        btn_location = (Button) findViewById(R.id.btn_location);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_heat = (Button) findViewById(R.id.btn_heat);
        tv_totaldis= (TextView) findViewById(R.id.tv_totaldis);
        tv_speed= (TextView) findViewById(R.id.tv_speed);
        btn_boundary= (Button) findViewById(R.id.btn_boundary);
        btn_location.setOnClickListener(this);
        btn_start.setOnClickListener(this);
        btn_heat.setOnClickListener(this);
        btn_boundary.setOnClickListener(this);
        initMyLocation();
    }

    /**
     * 初始化定位相关代码
     */
    private void initMyLocation() {
        // 定位初始化
        mLocationClient = new LocationClient(getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);
        option.setScanSpan(2000);
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null)
                return;
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            baiduMap.setMyLocationData(locData);
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
//            // 设置自定义图标
//            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//                    .fromResource(R.drawable.navi_map_gps_locked);
//            MyLocationConfigeration config = new MyLocationConfigeration(
//                    mCurrentMode, true, mCurrentMarker);
//            mBaiduMap.setMyLocationConfigeration(config);

            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());

           /* if (trackDraw != null) {
                trackDraw.addLatLng(ll);
            }*/
            //获取当前速度
            float speed = location.getSpeed();
            tv_speed.setText("速度："+speed+" km/h");

            //获取当前计算的行程
            if(trackDraw!=null){
              String distance="当前有效行程：" +(int)trackDraw.distanceTotal()+"米";
                tv_totaldis.setText(distance);
            }
            /*  在此处可以将定位到的坐标点上传到服务器保存*/
            // 第一次定位时，将地图位置移动到当前位置
            if (isFristLocation) {
                Toast.makeText(getApplicationContext(),location.getCity()+":"+location.getAddrStr()+":"+location.getCityCode(),Toast.LENGTH_SHORT).show();
                isFristLocation = false;
                baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(u);
            }
        }

    }

    @Override
    protected void onStart() {
        // 开启图层定位
        baiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private TrackDraw trackDraw;
    private boolean isStartTrack;
    private OverlayUtils overlayUtils;
    private boolean isShowBound;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //定位
            case R.id.btn_location:
                isFristLocation = true;
                break;
            //行程记录
            case R.id.btn_start:
                if (!isStartTrack) {
                    if (trackDraw == null) {
                        trackDraw = new TrackDraw();
                        trackDraw.initRoadData(mapView, mCurrentLantitude, mCurrentLongitude);
                    }
                    trackDraw.moveLooper();
                    btn_start.setText("关闭行程");
                    isStartTrack = true;
                } else {
                    btn_start.setText("开启行程");
                    isStartTrack = false;
                    trackDraw.stopMoveLooper();
                }
                break;
            // 热力图
            case R.id.btn_heat:
                if (baiduMap.isBaiduHeatMapEnabled()) {
                    baiduMap.setBaiduHeatMapEnabled(false);
                } else {
                    baiduMap.setBaiduHeatMapEnabled(true);
                }
                break;
            //区域边界
            case R.id.btn_boundary:
                if(!isShowBound){
                    isShowBound=true;
                    if(overlayUtils==null){
                        overlayUtils=new OverlayUtils();
                    }
//                    overlayUtils.showBoundary(baiduMap,"深圳","南山区");
                    overlayUtils.showBoundary(baiduMap,"深圳",null);
                    btn_boundary.setText("隐藏边界");
                }else{
                    isShowBound=false;
                    if (overlayUtils!=null){
                        overlayUtils.hideBoundary();
                    }
                    btn_boundary.setText("显示边界");
                }
                break;
        }
    }
}
