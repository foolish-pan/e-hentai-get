package com.foolish.conn;

import java.net.Proxy;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class ConnectionFactory {

    public static Connection conn(String url, Proxy proxy, Map<String, String> cookies) {
        return Jsoup.connect(url).proxy(proxy).cookies(cookies);
    }

}
