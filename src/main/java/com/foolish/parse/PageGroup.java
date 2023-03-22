package com.foolish.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.foolish.conn.ConnectionFactory;
import com.foolish.conn.CookieFactory;
import com.foolish.conn.ProxyFactory;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageGroup {

    private String pageUrl;

    public PageGroup(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public List<ImagePage> parseImagePage() throws IOException {
        System.out.println("Start parse image page from url: " + this.pageUrl);

        Connection connection = ConnectionFactory.conn(this.pageUrl, ProxyFactory.defaultProxy(),
                CookieFactory.defaultCookie());

        Document document = connection.get();
        Element body = document.body();

        Elements pageInfo = body.select("body > div.gtb > p.gpc");
        String pageInfoStr = pageInfo.get(0).text();
        String[] pageInfoTemp = pageInfoStr.split(" ");

        int startNo = Integer.parseInt(pageInfoTemp[1]);
        // int endNo = Integer.parseInt(pageInfoTemp[3]);

        List<ImagePage> imgDetails = new ArrayList<ImagePage>();
        Elements imagesLink = body.select("#gdt .gdtl a");

        for (Element element : imagesLink) {
            String detailLink = element.attr("href");

            Element imagElement = element.selectFirst("img");
            String imageTitle = imagElement.attr("title");
            String[] imageTitleTemp = imageTitle.split(":");

            imgDetails.add(new ImagePage(startNo++, imageTitleTemp[1].trim(), detailLink));
        }

        System.out.println("Parse success, generate " + imgDetails.size() + " image page");
        return imgDetails;
    }

}
