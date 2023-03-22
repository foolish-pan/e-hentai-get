package com.foolish.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.foolish.conn.ConnectionFactory;
import com.foolish.conn.CookieFactory;
import com.foolish.conn.ProxyFactory;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Collection {

    private String collectionUrl;

    private Document mainPage;

    private String collectionName;

    private int pageCount;

    private int imageCount;

    public Collection(String collectionUrl) throws IOException {
        this.collectionUrl = collectionUrl;

        System.out.println("Start parse collection info from url: " + this.collectionUrl);

        Connection connection = ConnectionFactory.conn(collectionUrl, ProxyFactory.defaultProxy(),
                CookieFactory.defaultCookie());
        this.mainPage = connection.get();

        Element body = mainPage.body();

        Element titleElement = body.selectFirst("#gd2 #gn");
        this.collectionName = titleElement.text();

        Elements pagesElem = body.select("table.ptt > tbody > tr > td:nth-last-child(2) > a");
        this.pageCount = Integer.parseInt(pagesElem.text());

        Element imgCountElem = body.selectFirst("#gdd > table > tbody > tr:nth-child(6) > td.gdt2");
        this.imageCount = Integer.parseInt(imgCountElem.text().split(" ")[0]);

        System.out.println("Parse success: " + this);
    }

    public List<PageGroup> parsePageGroup() {
        List<PageGroup> pages = new ArrayList<PageGroup>();
        pages.add(new PageGroup(collectionUrl));

        for (int i = 1; i < this.pageCount; i++) {
            pages.add(new PageGroup(collectionUrl + "?p=" + i));
        }
        return pages;
    }

    private String pathEncode(String path) {
        Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(path);
        path = matcher.replaceAll(""); // 将匹配到的非法字符以空替换
        return path;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getEncodeCollectionName() {
        return pathEncode(this.collectionName);
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getImageCount() {
        return imageCount;
    }

    @Override
    public String toString() {
        return "Collection [collectionName=" + collectionName + ", imageCount=" + imageCount + ", pageCount="
                + pageCount + "]";
    }

}
