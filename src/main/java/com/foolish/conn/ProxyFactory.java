package com.foolish.conn;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class ProxyFactory {

    private ProxyFactory() {
    }

    public static Proxy defaultProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 2081));
    }

}
