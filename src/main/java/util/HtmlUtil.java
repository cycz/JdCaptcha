package util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class HtmlUtil {
    /**
     * 获取表单
     *
     * @param form
     * @return
     */
    public static Map<String, String> parseHtmlForm(Element form) {
        Map<String, String> postMap = new HashMap<>();
        if (form == null) {
            return postMap;
        }
        Elements inputElements = form.getElementsByTag("input");
        if (inputElements != null && inputElements.size() > 0) {
            for (int i = 0; i < inputElements.size(); i++) {
                Element element = inputElements.get(i);
                if (element != null) {
                    String value = element.attr("value");
                    if (value == null) {
                        value = "";
                    }
                    String name = element.attr("name");
                    if (!StringUtils.isEmpty(name)) {
                        postMap.put(name, value);
                    } else {
                        String id = element.attr("id");
                        if (!StringUtils.isEmpty(id)) {
                            postMap.put(id, value);
                        }
                    }
                }
            }
        }
        return postMap;
    }
}
