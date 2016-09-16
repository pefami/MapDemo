package com.example.pefami.mapdemo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该类负责向服务端发起异步请求（https、http请求）
 *
 *
 */
public class AsynRequestUtils {

    private static final Pattern REG_UNICODE = Pattern.compile("[0-9A-Fa-f]{4}");

    public static void main(String[] args) {

        String urlPath = "http://api.map.baidu.com/trace/v2/entity/add";
        StringBuffer parameter = new StringBuffer();
        parameter.append("ak=").append("TRt0EE2XUYfzqMNGx8s5BisOoRrjMZUZ");
        parameter.append("&mcode=").append("申请android端ak时，填写的安全码");
        parameter.append("&service_id=").append("创建的鹰眼服务ID");
        parameter.append("&entity_name=").append("entity标识");
        handleHttpAsyn(urlPath, parameter.toString(), "HTTP", "POST");

    }

    protected static void handleHttpAsyn(String urlPath, String parameter, String requestType, String requestMethod) {

        TaskThread taskThread = new TaskThread();
        taskThread.urlPath = urlPath;
        taskThread.parameter = parameter;
        taskThread.requestType = requestType;
        taskThread.requestMethod = requestMethod;
        taskThread.start();
    }

    static class TaskThread extends Thread {

        // 请求路径
        private String urlPath;

        // 请求参数
        private String parameter;

        // 请求类型（HTTPS/HTTP）
        private String requestType;

        // 请求方法（GET/POST）
        private String requestMethod;

        @Override
        public void run() {
            // TODO Auto-generated method stub

            try {
                if ("HTTP".equals(requestType)) {
                    handlerHttpRequest();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("请求失败");
                e.printStackTrace();
            }

        }

        // 处理http类型请求
        private void handlerHttpRequest() throws Exception {
            if ("GET".equals(requestMethod)) {
                doHttpGet();
            } else if ("POST".equals(requestMethod)) {
                doHttpPost();
            }
        }


        // http get请求
        private void doHttpGet() throws Exception {
            URL urlGet = new URL(urlPath + "?" + parameter);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET");
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);
            int responsecode = http.getResponseCode();
            if (responsecode == 200) {
                InputStream inStream = http.getInputStream();
                String result = unicode2String(handlerInputStream(inStream));
                System.out.println("请求结果:" + result);
            }
        }

        // http post请求
        private void doHttpPost() throws Exception {
            URL urlPost = new URL(urlPath);
            HttpURLConnection http = (HttpURLConnection) urlPost.openConnection();
            http.setRequestMethod("POST");
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);
            // 是否输入参数
            http.setDoOutput(true);
            byte[] data = parameter.getBytes();
            // 把封装好的数据发送到输出流
            OutputStream outStream = http.getOutputStream();
            outStream.write(data);
            outStream.flush();
            outStream.close();
            InputStream inStream = http.getInputStream();
            String result = unicode2String(handlerInputStream(inStream));
            System.out.println("请求结果:" + result);
        }
    }

    /**
     * 解析输入流
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String handlerInputStream(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String result = "";
        String line = "";
        while (null != (line = bufferedReader.readLine())) {
            result += line;
        }
        return result;
    }

    /**
     * unicode编码解析输入流
     *
     * @param str
     * @return
     */
    private static String unicode2String(String str) {
        StringBuilder sb = new StringBuilder();
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c1 = str.charAt(i);
            if (c1 == '\\' && i < len - 1) {
                char c2 = str.charAt(++i);
                if (c2 == 'u' && i <= len - 5) {
                    String tmp = str.substring(i + 1, i + 5);
                    Matcher matcher = REG_UNICODE.matcher(tmp);
                    if (matcher.find()) {
                        sb.append((char) Integer.parseInt(tmp, 16));
                        i = i + 4;
                    } else {
                        sb.append(c1).append(c2);
                    }
                } else {
                    sb.append(c1).append(c2);
                }
            } else {
                sb.append(c1);
            }
        }
        return sb.toString();
    }


}