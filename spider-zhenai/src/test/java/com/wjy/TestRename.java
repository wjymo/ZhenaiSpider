package com.wjy;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class TestRename {
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private RequestConfig requestConfig;
    @Autowired
    private HttpClientBuilder httpClientBuilder;
    private final static String USER_AGENT="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    @Test
    public  void testActiviti7(){
        File file=new File("G:\\BaiduNetdiskDownload\\ES资料\\3-1 Activiti7工作流引擎");
        File[] files = file.listFiles((dir, name) -> isNumeric(name));
        Integer total=0;
        for (File fileItem : files) {
            total+=fileItem.listFiles().length;
        }
        System.out.println(total);
    }

    public static boolean isNumeric(String str){
        return str.matches("-?[0-9]+.*[0-9]*");
    }

    @Test
    public  void testGetActiviti7() throws IOException {
//        File target=new File("G:/BaiduNetdiskDownload/ES资料/saas-ihrm标题.txt");
        List<String> titles=new ArrayList<>();
        // 创建http GET请求
        HttpGet httpGet = new HttpGet("https://www.boxuegu.com/promote/outline-1232.html");
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("User-Agent",USER_AGENT);
        CloseableHttpResponse response = null;
        try {
            response =  beanFactory.getBean(CloseableHttpClient.class).execute(httpGet);
            System.out.println(response.getStatusLine().getStatusCode());
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            Document document = Jsoup.parse(result);
            Elements elementsByClass = document.getElementsByClass("summary-cont");
            for (Element element : elementsByClass) {
                List<Node> nodes = element.childNodes();
                for (Node node : nodes) {
                    List<Node> childNodes = node.childNodes();
                    if(!CollectionUtils.isEmpty(childNodes)){
                        String title = childNodes.get(0).toString();
//                        FileUtils.write(target,title.concat("\n"),"utf-8",true);
                        titles.add(title);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String path="G:/BaiduNetdiskDownload/ES资料/";
        String targetDir="3-1 Activiti7工作流引擎/";

        File renameDir=new File(path.concat(targetDir));
        File[] files = renameDir.listFiles((dir, name) -> isNumeric(name));
        Iterator<String> iterator = titles.iterator();
        for (File fileDir : files) {
            String dirName = fileDir.getName();
            File[] fileMp4s = fileDir.listFiles();
            for (File fileMp4 : fileMp4s) {
                if(fileMp4.isFile()){
                    if(iterator.hasNext()){
                        String name = fileMp4.getName();
                        String suffix = name.substring(name.lastIndexOf(".") );
                        String nextTitle = iterator.next();
                        String newName = "G:/".concat("新的3-1 Activiti7工作流引擎/")
                                .concat(dirName).concat("/").concat(nextTitle).concat(suffix);
                        File newFile=new File(newName);
//                        boolean b = fileMp4.renameTo(newFile);
//                        System.out.println("newName: "+newName+",rename结果："+b);
                        System.out.println("newName: "+newName);
                        FileUtils.copyFile(fileMp4,newFile);
                    }
                }
            }


        }
        System.out.println(1);
    }

    @Test
    public void rename2(){
        File file=new File("G:/BaiduNetdiskDownload/ES资料\\as.txt");
        file.renameTo(new File("G:\\BaiduNetdiskDownload\\ES资料\\www.txt"));
    }
}
