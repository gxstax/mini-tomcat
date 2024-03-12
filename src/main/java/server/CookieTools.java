package server;

import javax.servlet.http.Cookie;

/**
 * <p>
 * Cookie处理工具类
 * </p>
 *
 * @author Ant
 * @since 2024/3/8 18:41
 */
public class CookieTools {

    private static final String tspecials = "()<>@,;:\\\"/[]?={} \t";

    public static String getCookieHeaderName(Cookie cookie) {
        return "Set-Cookie";
    }

    public static void getCookieHeaderValue(Cookie cookie, StringBuffer buffer) {
        String name = cookie.getName();
        if (name == null) {
            name = "";
        }

        String value = cookie.getValue();
        if (null == value) {
            value = "";
        }
        buffer.append(name);
        buffer.append("=");
        buffer.append(value);
    }

    public void maybeQuote (int version, StringBuffer buffer,  String value) {
        if (version == 0 || isToken(value)) {
            buffer.append(value);
        } else {
            buffer.append('"');
            buffer.append(value);
            buffer.append('"');
        }
    }

    private static boolean isToken(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (c < 0x20 || c >= 0x7f || tspecials.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }

}
