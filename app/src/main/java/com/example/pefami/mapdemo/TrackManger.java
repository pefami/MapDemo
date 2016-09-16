package com.example.pefami.mapdemo;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.example.pefami.mapdemo.bean.HistoryTrackData;
import com.example.pefami.mapdemo.utils.GsonService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pefami on 2016/9/15.
 */
public class TrackManger {
    private Context mContext;
    private LBSTraceClient client;
    private Trace trace;
    private boolean isStartTrack;
    private OnTrackListener trackListener;
    private int  startTime;
    private int endTime;

    public TrackManger(Context context) {
        mContext = context;
    }

    //鹰眼服务ID
    private long serviceId =  125370;
    //entity标识
    private String entityName = "mycar";
    public void startTrack() {
        if(isStartTrack){
            return;
        }
        isStartTrack=true;
        initOnTrackListener();
        //实例化轨迹服务客户端
        client = new LBSTraceClient(mContext);
        // 采集周期
        int gatherInterval = 5;
        // 打包周期
        int packInterval = 30;
        // http协议类型
        int protocolType = 1;
        // 设置采集和打包周期
        client. setInterval(gatherInterval, packInterval);
        // 设置定位模式
        client. setLocationMode(LocationMode.High_Accuracy);
        // 设置http协议类型
        client. setProtocolType (protocolType);

        //轨迹服务类型（0 : 不上传位置数据，也不接收报警信息； 1 : 不上传位置数据，但不接收报警信息；2 : 上传位置数据，且接收报警信息）
        int traceType = 2;
        //实例化轨迹服务
        trace = new Trace(mContext, serviceId, entityName, traceType);

        //实例化开启轨迹服务回调接口
        OnStartTraceListener startTraceListener = new OnStartTraceListener() {
            //开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTraceCallback(int arg0, String arg1) {
            }

            //轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTracePushCallback(byte arg0, String arg1) {
            }
        };
        //开启轨迹服务
        client.startTrace(trace, startTraceListener);
    }
    public void stopTrack(){
        if(!isStartTrack){
            return;
        }
        isStartTrack=false;
        //实例化停止轨迹服务回调接口
        OnStopTraceListener stopTraceListener = new OnStopTraceListener(){
            // 轨迹服务停止成功
            @Override
            public void onStopTraceSuccess() {
            }
            // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onStopTraceFailed(int arg0, String arg1) {
            }
        };
        //停止轨迹服务
        client.stopTrace(trace,stopTraceListener);
    }
    /**
     * 初始化OnTrackListener
     */
    private void initOnTrackListener() {

        trackListener = new OnTrackListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                Looper.prepare();
                Toast.makeText(mContext, "track请求失败: " + arg0, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            // 查询历史轨迹回调接口
            @Override
            public void onQueryHistoryTrackCallback(String arg0) {
                super.onQueryHistoryTrackCallback(arg0);
                showHistoryTrack(arg0);
            }
        };
    }
    /**
     * 查询历史轨迹
     */
    public void queryHistoryTrack() {

        // 是否返回精简的结果（0 : 否，1 : 是）
        int simpleReturn = 0;
        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // 分页大小
        int pageSize = 1000;
        // 分页索引
        int pageIndex = 1;

        client.queryHistoryTrack(serviceId, entityName, simpleReturn, startTime, endTime, pageSize, pageIndex, trackListener);
    }

    /**
     * 历史轨迹点
     *
     */
    public void showHistoryTrack(String historyTrack) {

        HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack,
                HistoryTrackData.class);
        List<LatLng> latLngList = new ArrayList<LatLng>();
        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
            if (historyTrackData.getListPoints() != null) {
                latLngList.addAll(historyTrackData.getListPoints());
            }
        }
    }
}
