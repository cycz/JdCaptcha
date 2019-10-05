import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import javax.script.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import util.*;

/**
 * @Author: cyz
 * @Date: 2019/2/25 0025 下午 22:23
 * @Version 1.0
 */
public class SlideMain {
    private Map<String, String> tempMap = new HashMap<>();
    private Map<String, String> cookieMap = new HashMap<>();

    public int getPic() {
        try {
            //主页
            String url = "https://passport.jd.com/new/login.aspx?ReturnUrl=https%3A%2F%2Fwww.jd.com%2F";
            Connection.Response response = Jsoup.connect(url).method(Connection.Method.GET).execute();
            String indexBody = response.body();
            Document document = Jsoup.parse(indexBody);
            Element formlogin = document.getElementById("formlogin");
            //获得参数
            Map<String, String> baseMap = HtmlUtil.parseHtmlForm(formlogin);
            String slideAppId = baseMap.get("slideAppId");
            cookieMap = response.cookies();
            int distance = downloadCaptcha();

            return distance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int downloadCaptcha() {
        try {
            InputStream in = null;
            Connection.Response response = null;
            String callback = "jsonp_01677197385162" + (int) Math.floor(Math.random() * 10000);
            //暂时写死
            String eid = "SALSQTDTF7G3SFVX2GVRMRTPZNHPITUM23YFRZG7V75UCOHC5CH7PJOVO7IOVESKTLNE47IGAXADMDA4DPLDSQJDZI";
            tempMap.put("eid", eid);
            String url = "https://iv.jd.com/slide/g.html?" +
                    "appId=" + tempMap.get("slideAppId") +
                    "&scene=login" +
                    "&product=click-bind-suspend" +
                    "&e=" + eid +
                    "&callback=" + callback;

            response = Jsoup.connect(url).method(Connection.Method.GET)
                    .cookies(cookieMap)
                    .header("Host", "iv.jd.com")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .header("Accept", "*/*")
                    .header("Referer", "https://passport.jd.com/uc/login")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
                    .execute();
            CookiesUtil.update(cookieMap, response.cookies());
            String body = response.body();
            body = StringUtils.substringBetween(body, callback + "(", ")");
            if (body == null) {
                System.err.print("下载图片验证码失败");
            }
            JSONObject jsonObject = JSON.parseObject(body);
            String message = jsonObject.getString("message");
            String y = jsonObject.getString("y");
            String bg = jsonObject.getString("bg");
            String challenge = jsonObject.getString("challenge");
            tempMap.put("challenge", challenge);
            byte[] bgBytes = Base64Util.Base642byte(bg);
            Base64Util.GenerateImage(bg, "test.jpg");
            in = new ByteArrayInputStream(bgBytes);
            BufferedImage bi = ImageIO.read(in);
            Map<String, BufferedImage> bufferedImageMap = JdPicDateUtils.getBufferedImageMap();
            int x = JdPicDateUtils.getDistance(bi, bufferedImageMap, y);
            System.out.println("获得距离x:" + x);
            int xx = (int) Math.floor(x * 278 / 360);
            return xx;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int checkCaptcha(int distance) {
        try {
            Connection.Response response = null;
            String callback = "jsonp_01677197385162" + (int) Math.floor(Math.random() * 10000);
            String eid = tempMap.get("eid");
            String d = JdSlideEncrypt.encrypt(distance + "");
            String challenge = tempMap.get("challenge");
            //写死
            String sessionid = "6983130332842014834";
            String testUsername = "13166666666";
            String url = "https://iv.jd.com/slide/s.html?" +
                    "d=" + d +
                    "&c=" + challenge +
                    "&w=278" +
                    "&appId=1604ebb2287" +
                    "&scene=login" +
                    "&product=click-bind-suspend" +
                    "&e=" + eid +
                    "&s=" + sessionid +
                    "&o=" + testUsername +
                    "&lang=zh_CN" +
                    "&callback=" + callback;

            response = Jsoup.connect(url).method(Connection.Method.GET)
                    .cookies(cookieMap)
                    .header("Host", "iv.jd.com")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .header("Accept", "*/*")
                    .header("Referer", "https://passport.jd.com/uc/login")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
                    .execute();
            CookiesUtil.update(cookieMap, response.cookies());
            String body = response.body();
            body = StringUtils.substringBetween(body, callback + "(", ")");
            System.out.println(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void main(String[] args) {
        try {

            SlideMain slideMain = new SlideMain();
            for (int i = 5; i > 0; i--) {
                int distance = slideMain.getPic();
                Thread.sleep(1000 * 3);
                slideMain.checkCaptcha(distance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
