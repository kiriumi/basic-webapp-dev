package etc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceInitializer {

    private static final List<String> list = new ArrayList<>() {
        {
            add("ほげ");
            add("ふー");
        }
    };

    /**
     * 拡張子とContent-Typeの対応表
     * ※インスタンス初期化子を使う場合
     */
    private static final Map<String, String> contentTypeMap = new HashMap<>() {
        {
            put("html", "text/html");
            put("htm", "text/html");
            put("txt", "text/plain");
            put("css", "text/css");
            put("png", "image/png");
            put("jpg", "image/jpeg");
            put("jpeg", "image/jpeg");
            put("gif", "image/gif");
        }
    };

    public static void main(String[] args) {

        System.out.println(list.get(1));
        System.out.println(contentTypeMap.get("txt"));
    }

}
