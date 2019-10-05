
import org.apache.commons.codec.digest.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: cyz
 * @Version 1.0
 */
public class JdPicDateUtils {

    private static Map<String, BufferedImage> bufferedImageMap = null;
    private static String[] picStrings = {
            "0cafdec4d059a547d9b515132dfeda9c.png",
            "2e752832dd5775fb9b85ac539c21edb3.png",
            "5d2a20ff27057c1dc919c3546a015773.png",
            "6bc516221904aac0638a49e593961bd7.png",
            "18d2593513d903c71ca604f896b9163b.png",
            "9803b41933ddf82d222462237e4c9bb3.png",
            "67010e410cdaf071b5bf72fa35c1d448.png",
            "c3d0065ead7e43da9c057c81137d419e.png",
            "6c26390570b6062d423bc05e45898cbb.png",
            "c20fdaa0906d123629bade0e436903ce.png"};

    public static Map<String, BufferedImage> getBufferedImageMap() {
        if (bufferedImageMap == null) {
            BufferedImage bi = null;
            InputStream in = null;
            bufferedImageMap = new HashMap<>();
            for (int i = 0; i < picStrings.length; i++) {
                try {
                    in = JdPicDateUtils.class.getClassLoader().getResourceAsStream("pic/" + picStrings[i]);
                    bi = ImageIO.read(in);
                } catch (Exception e) {
//                    LOG.error("bufferedImageMap创建出现异常" + e);
                }
                bufferedImageMap.put(getHash(bi), bi);
//            }
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
//                LOG.error("bufferedImageMap创建出现异常" + e);
            }
        }
        return bufferedImageMap;
    }

    public static String getHash(BufferedImage bi) {
        int[] rgb = new int[3];
        int height = bi.getHeight();
        int miny = bi.getMinY();
        String hash_string = "";
        for (int j = miny; j < height; j++) {
            int pixel = bi.getRGB(0, j); // 下面三行代码将一个数字转换为RGB数字
            rgb[0] = (pixel & 0xff0000) >> 16;
            rgb[1] = (pixel & 0xff00) >> 8;
            rgb[2] = (pixel & 0xff);
            hash_string += rgb[0];
            hash_string += rgb[1];
            hash_string += rgb[2];
        }
        return DigestUtils.md5Hex(hash_string);
    }

    public static int getDistance(BufferedImage currentBi, Map<String, BufferedImage> bufferedImageMap, String y) {
        int x = 0;
        int width = currentBi.getWidth();
        String hashcode = JdPicDateUtils.getHash(currentBi);
        BufferedImage baseBi = bufferedImageMap.get(hashcode);
        if (baseBi == null) {
            return 0;
        }
        int[] currentRgb = new int[3];
        int[] baseRgb = new int[3];
        int currentY = Integer.parseInt(y);
        for (int j = 0; j < width; j++) {
            int currentPixel = currentBi.getRGB(j, currentY + 20); // 下面三行代码将一个数字转换为RGB数字
            int basePixel = baseBi.getRGB(j, currentY + 20); // 下面三行代码将一个数字转换为RGB数字
            currentRgb[0] = (currentPixel & 0xff0000) >> 16;
            currentRgb[1] = (currentPixel & 0xff00) >> 8;
            currentRgb[2] = (currentPixel & 0xff);
            baseRgb[0] = (basePixel & 0xff0000) >> 16;
            baseRgb[1] = (basePixel & 0xff00) >> 8;
            baseRgb[2] = (basePixel & 0xff);
            if (Math.abs(baseRgb[0] - currentRgb[0]) + Math.abs(baseRgb[1] - currentRgb[1]) + Math.abs(baseRgb[2] - currentRgb[2]) > 50) {
                x = j;
                break;
            }
        }
        return x;
    }

    public static void main(String[] args) {
        Map<String, BufferedImage> bufferedImageMap = getBufferedImageMap();
        bufferedImageMap.get("1");
    }

}
