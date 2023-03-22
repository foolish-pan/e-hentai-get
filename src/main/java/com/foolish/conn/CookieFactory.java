package com.foolish.conn;

import java.util.HashMap;
import java.util.Map;

public class CookieFactory {

    private CookieFactory() {
    }

    public static Map<String, String> defaultCookie() {
        Map<String, String> cookies = new HashMap<String, String>();
        cookies.put("ipb_member_id", "4577796");
        cookies.put("ipb_pass_hash", "be15fb7b25a7f8ccd28cd98d7a70078b");
        cookies.put("sk", "c8l2qs2rag60lzbs5mjbf1la3vso");
        cookies.put("nw", "1");
        cookies.put("event", "1637316771");
        return cookies;
    }

}
