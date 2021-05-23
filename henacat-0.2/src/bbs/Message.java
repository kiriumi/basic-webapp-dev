package bbs;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 投稿を保持するクラス
 * ※投稿をメモリ上に保持する
 *
 * @author kengo
 *
 */
public class Message {

    static List<Message> messageList = new ArrayList<>();

    String title;
    String handle;
    String message;
    ZonedDateTime date;

    Message(String title, String handle, String message) {
        this.title = title;
        this.handle = handle;
        this.message = message;
        this.date = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
    }

}
