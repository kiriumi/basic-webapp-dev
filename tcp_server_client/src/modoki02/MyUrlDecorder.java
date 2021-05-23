package modoki02;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 自作のURLデコーダー
 *
 * ※ Java標準のURODecorder.decode() は、SJISの2バイト目がエンコードされてない場合、誤動作するため
 *
 *
 * @author kengo
 *
 */
public class MyUrlDecorder {

    public static String decode(String src, String charsetName) throws UnsupportedEncodingException {

        byte[] srcBytes = src.getBytes("ISO_8859_1");

        // 配列の長さが、変換後の方が長くなることはないので、srcByteの長さの配列をいったん確保する
        byte[] destBytes = new byte[srcBytes.length];

        int destIdx = 0;
        for (int srcIdx = 0; srcIdx < srcBytes.length; srcIdx++) {

            if (srcBytes[srcIdx] == (byte) '%') {
                // 「%」の後ろ2文字（2byte）分を16進数として解釈し、数値に変換
                destBytes[destIdx] = (byte) hex2int(srcBytes[srcIdx + 1], srcBytes[srcIdx + 2]);
                srcIdx += 2;

            } else {
                destBytes[destIdx] = srcBytes[srcIdx];
            }
            destIdx++;
        }

        byte[] destBytesArrangedLength = Arrays.copyOf(destBytes, destIdx);

        return new String(destBytesArrangedLength, charsetName);
    }

    /**
     * 「%」の後ろ２つの文字（16進数2桁）のASCIIコード（byte）を、数値（int）に変換する
     *
     * @param b1
     * @param b2
     * @return
     */
    private static int hex2int(byte b1, byte b2) {

        int digit;

        if (b1 >= 'A') {
            // 0xDFとの&で小文字を大文字に変換する
            digit = (b1 & 0xDF) - 'A' + 10;
        } else {
            digit = (b1 - '0');
        }

        digit *= 16;

        if (b2 >= 'A') {
            // 0xDFとの&で小文字を大文字に変換する
            digit += (b2 & 0xDF) - 'A' + 10;
        } else {
            digit += b2 - '0';
        }

        return digit;
    }

}
