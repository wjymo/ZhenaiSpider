package com.wjy;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.text.ParseException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class TestSplitVideo {
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private RequestConfig requestConfig;
    @Autowired
    private HttpClientBuilder httpClientBuilder;
    private final static String USER_AGENT="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    /**
     * 原生切割视频方式，不可用！！切割后视频无法观看，要使用下面的ffmpeg来处理
     * @throws IOException
     */
    @Test
    public void simpleSplitVideo() throws IOException {
        FileInputStream fis =
                new FileInputStream(
                        "G:\\BaiduNetdiskDownload\\73_Java API初步使用_员工管理案例：基于Java实现员工信息的增删改查\\视频\\课程视频 - 副本.avi");
        FileOutputStream fos=null;
        byte buf[]=new byte[1024*1024*5];//定义一次写入 5M 数据
        int len=0;
        int count=1;
        fos=new FileOutputStream("G:\\BaiduNetdiskDownload\\copy\\"+(count++)+".part");
        int num = 1;//用来记录程序运行中实时的每个 output 写入次数
        while ((len=fis.read(buf))!=-1){
            fos.write(buf,0,len);
            if(num>10) {
                //如果写入次数大于 20(单个文件超过 100M),则此流停止,创建新的输出流写入新的片段
                fos.close();
                fos=new FileOutputStream("G:\\BaiduNetdiskDownload\\copy\\"+(count++)+".part");
                num = 1;//重新计数
            }else{
                num++;
            }
        }
        fis.close();
        fos.close();
        System.out.println("切割完毕!");
    }


    /**
     * 通过json配置文件中分隔好的视频信息，使用ffmpeg来切割原来很大的视频！！
     * @throws IOException
     */
    @Test
    public void testFfmpegSplitVideo() throws IOException {
        String s = FileUtils.readFileToString(new File("F:\\workspace\\spider\\spider-zhenai\\src\\main\\resources\\split.json"), "utf-8");
        JSONArray jsonArray =JSON.parseArray(s);
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            JSONArray split = jsonObject.getJSONArray("split");
            String source = jsonObject.getString("source");
            for (Object o1 : split) {
                JSONObject splitJsonObject = (JSONObject) o1;
                String target = splitJsonObject.getString("target");
                String startTime = splitJsonObject.getString("startTime");
                String length = splitJsonObject.getString("length");
                ffmpegSplitVideo(source,target,startTime,length);
            }
        }
        System.out.println(1);

    }

    public void ffmpegSplitVideo(String inputVideoPath,String outputVideoPath,String startTime,String length){
        List<String> commands=new ArrayList<>();
        commands.add("D:\\devtools2\\ffmpeg\\ffmpeg-20191215-ed9279a-win64-static\\bin\\ffmpeg.exe");
        commands.add("-i");
//        commands.add("G:\\BaiduNetdiskDownload\\netty-srpingboot\\第10章+第11章.flv");
        commands.add(inputVideoPath);
        commands.add("-ss");
//        commands.add("00:01:30");
        commands.add(startTime);
        commands.add("-t");
//        commands.add("20");
        commands.add(length);
        commands.add("-vcodec");
        commands.add("copy");
        commands.add("-acodec");
        commands.add("copy");
//        commands.add("G:\\BaiduNetdiskDownload\\copy\\新11章2.flv");
        commands.add(outputVideoPath);

        ProcessBuilder builder=new ProcessBuilder(commands);
        BufferedReader bufferedReader =null;
        try {
            Process process = builder.start();
            InputStream errorStream = process.getErrorStream();
            bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
            String line=null;
            while ((line=bufferedReader.readLine())!=null){
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getVideoInfo(){
        // 创建http GET请求
        HttpGet httpGet = new HttpGet("https://coding.imooc.com/class/chapter/261.html#Anchor");
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("User-Agent",USER_AGENT);
        CloseableHttpResponse response = null;
        try {
            response =  beanFactory.getBean(CloseableHttpClient.class).execute(httpGet);
            System.out.println(response.getStatusLine().getStatusCode());
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            Document document = Jsoup.parse(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getVideoLength() throws ParseException {
        File dir=new File("G:\\BaiduNetdiskDownload\\netty-srpingboot");
        File[] files = dir.listFiles();
        Map<String,String> map=new HashMap<>();
        for (File file : files) {
            String absolutePath = file.getAbsolutePath();
            String name = file.getName();
            List<String> commands=new ArrayList<>();
            commands.add("D:\\devtools2\\ffmpeg\\ffmpeg-20191215-ed9279a-win64-static\\bin\\ffmpeg.exe");
            commands.add("-i");
            commands.add(absolutePath);
            ProcessBuilder builder=new ProcessBuilder(commands);
            BufferedReader bufferedReader =null;
            try {
                Process process = builder.start();
                InputStream errorStream = process.getErrorStream();
                bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
                String line=null;
                while ((line=bufferedReader.readLine())!=null){
                    System.out.println(line);
                    if(line.contains("Duration")){
                        String[] split = line.split(",");
                        String[] split1 = split[0].split(":");
                        String videoLength= split1[1] + ":" + split1[2] + ":" + split1[3];
                        map.put(name,videoLength);
                        map.put(absolutePath,videoLength);
                        System.out.println(1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(bufferedReader!=null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        File target=new File("G:\\BaiduNetdiskDownload\\对照表.txt");
        map.entrySet().forEach(entry->{
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                FileUtils.write(target,key.concat("   @@   ").concat(value).concat("\n"),"utf-8",true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println(1);


//        List<String> commands=new ArrayList<>();
//        commands.add("D:\\devtools2\\ffmpeg\\ffmpeg-20191215-ed9279a-win64-static\\bin\\ffmpeg.exe");
//        commands.add("-i");
//        commands.add("G:\\BaiduNetdiskDownload\\netty-srpingboot\\第10章+第11章.flv");
//        ProcessBuilder builder=new ProcessBuilder(commands);
//        BufferedReader bufferedReader =null;
//        StringBuilder stringBuilder=new StringBuilder();
//        String videoLength=null;
//        TimeBean timeBean=new TimeBean();
//        try {
//            Process process = builder.start();
//            InputStream inputStream = process.getInputStream();
//            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            String line=null;
//            while ((line=bufferedReader.readLine())!=null){
//                System.out.println(line);
//                stringBuilder.append(line);
//            }
//            System.out.println(1);
//            InputStream errorStream = process.getErrorStream();
//            bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
//            line=null;
//            while ((line=bufferedReader.readLine())!=null){
//                System.out.println(line);
//                if(line.contains("Duration")){
//                    String[] split = line.split(",");
//                    String[] split1 = split[0].split(":");
//                    videoLength= split1[1] + ":" + split1[2] + ":" + split1[3];
//                    timeBean=new TimeBean();
//                    timeBean.setHour(Integer.parseInt(split1[1].trim())).setMinute(Integer.parseInt(split1[2].trim()))
//                    .setSecond(Double.parseDouble(split1[3].trim()));
//                    System.out.println(1);
//                }
//                stringBuilder.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if(bufferedReader!=null){
//                try {
//                    bufferedReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        System.out.println(videoLength);
//        System.out.println(timeBean);
    }
}
