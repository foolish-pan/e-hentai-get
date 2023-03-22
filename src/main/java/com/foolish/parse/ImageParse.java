package com.foolish.parse;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foolish.conn.HttpsUtil;

public class ImageParse {

    public static void main(String[] args) throws IOException {
        HttpsUtil.trustEveryone();

        Collection collection = new Collection("https://e-hentai.org/g/1888355/910fb02854/");

        String downloadPath = "D:/" + collection.getEncodeCollectionName();
        File floder = new File(downloadPath);
        if (!floder.exists()) {
            floder.mkdirs();
        }

        List<ImagePage> imagePages = new ArrayList<ImagePage>();

        List<PageGroup> pageGroups = collection.parsePageGroup();
        for (PageGroup pageGroup : pageGroups) {
            imagePages.addAll(pageGroup.parseImagePage());
        }

        Map<String, ImagePage> retry = new HashMap<String, ImagePage>();
        for (ImagePage imagePage : imagePages) {
            boolean success = imagePage.parseImage().download(downloadPath);
            if (!success) {
                retry.put(imagePage.getImageUrl(), imagePage);
            }
        }

        int retryCount = 1;
        while (retryCount <= 5 && retry.size() > 0) {
            System.out.println("Retry faild, count:" + retry.size() + ", retry:" + retryCount);
            for (Map.Entry<String, ImagePage> entry : retry.entrySet()) {
                boolean retryRes = entry.getValue().parseImage().download(downloadPath);
                if (retryRes) {
                    retry.remove(entry.getKey());
                }
            }
        }

        // new ImageParse("https://e-hentai.org/g/2151935/97702402c2/").parse();

        System.out.println("Download finish, total:" + imagePages.size() + ", faild:" + retry.size());

        Toolkit.getDefaultToolkit().beep();
    }

    // private String url;

    // private Proxy proxy;

    // private Map<String, String> cookies;

    // public ImageParse(String url) {
    // this.url = url;

    // this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",
    // 3426));

    // this.cookies = new HashMap<String, String>();
    // cookies.put("ipb_member_id", "4577796");
    // cookies.put("ipb_pass_hash", "be15fb7b25a7f8ccd28cd98d7a70078b");
    // cookies.put("sk", "c8l2qs2rag60lzbs5mjbf1la3vso");
    // cookies.put("nw", "1");
    // cookies.put("event", "1637316771");
    // }

    // public ParseResult parse() throws IOException {

    // Document mainPage = this.genConnection(url).get();

    // // 解析基础信息
    // ParseResult parseResult = this.parseBaseInfo(mainPage);

    // // 解析分页
    // List<String> pageLinks = this.genPageLinks(url, parseResult.getPageCount());

    // // 解析所有详情页连接
    // List<String> imgDetailLinks = new ArrayList<String>();
    // for (String pageLink : pageLinks) {
    // List<String> pageDetailLinks = this.parsePageDetailLinks(pageLink);
    // imgDetailLinks.addAll(pageDetailLinks);
    // }

    // File path = new File("D:/" + pathEncode(parseResult.getCollectionName()));
    // if (!path.exists()) {
    // path.mkdirs();
    // }

    // // 解析图片详细信息
    // List<Image> imgList = new ArrayList<Image>();
    // Map<String, Image> retryImg = new HashMap<String, Image>();
    // for (String detailLink : imgDetailLinks) {
    // Image image = this.parseImage(detailLink);
    // imgList.add(image);

    // try {
    // Response response =
    // this.genConnection(image.getDownloadUrl()).ignoreContentType(true).execute();
    // BufferedInputStream imgStream = response.bodyStream();
    // FileUtils.copyInputStreamToFile(imgStream, new File(path,
    // image.getFileName()));
    // } catch (Exception e) {
    // System.out.println("download error:" + e.getMessage());
    // retryImg.put(image.getFileName(), image);
    // }
    // }

    // while (retryImg.size() > 0) {
    // System.out.println("retry count:" + retryImg.size());
    // for (Map.Entry<String, Image> entry : retryImg.entrySet()) {
    // try {
    // Image image = entry.getValue();
    // Response response =
    // this.genConnection(image.getDownloadUrl()).ignoreContentType(true).execute();
    // BufferedInputStream imgStream = response.bodyStream();
    // FileUtils.copyInputStreamToFile(imgStream, new File(path,
    // image.getFileName()));
    // } catch (Exception e) {
    // System.out.println("download error:" + e.getMessage());
    // }
    // }
    // }

    // parseResult.setImgList(imgList);

    // return parseResult;
    // }

    // /**
    // * 生成连接信息，附加代理和cookies
    // *
    // * @param url URL
    // * @return 连接对象
    // */
    // private Connection genConnection(String url) {
    // System.out.println("connect url: " + url);
    // return Jsoup.connect(url).proxy(this.proxy).cookies(this.cookies);
    // }

    // /**
    // * 解析基本信息
    // *
    // * @param pageDocument 页面DOM对象
    // * @return 基本信息对象
    // */
    // private ParseResult parseBaseInfo(Document pageDocument) {
    // ParseResult parseResult = new ParseResult();

    // Element body = pageDocument.body();

    // Element titleElement = body.selectFirst("#gd2 #gn");
    // parseResult.setCollectionName(titleElement.text());

    // Elements pagesElem = body.select("table.ptt tr td");
    // parseResult.setPageCount(pagesElem.size() - 2);

    // Element imgCountElem = body.selectFirst("#gdd > table > tbody >
    // tr:nth-child(6) > td.gdt2");
    // System.out.println(imgCountElem);
    // int imgCount = Integer.parseInt(imgCountElem.text().split(" ")[0]);
    // parseResult.setImgCount(imgCount);

    // return parseResult;
    // }

    // /**
    // * 生成分页连接
    // *
    // * @param baseUrl 基础连接
    // * @param pageCount 分页数量
    // * @return 分页连接集合
    // */
    // private List<String> genPageLinks(String baseUrl, int pageCount) {
    // List<String> pageLinks = new ArrayList<String>();
    // pageLinks.add(baseUrl);

    // for (int i = 1; i < pageCount; i++) {
    // pageLinks.add(baseUrl + "?p=" + i);
    // }
    // return pageLinks;
    // }

    // /**
    // * 解析页面内详情页的链接
    // *
    // * @param pageUrl 页面链接
    // * @return 详情链接集合
    // * @throws IOException
    // */
    // private List<String> parsePageDetailLinks(String pageUrl) throws IOException
    // {
    // Document document = this.genConnection(pageUrl).get();
    // Element body = document.body();

    // List<String> imgDetailLinks = new ArrayList<String>();
    // Elements imagesLink = body.select("#gdt .gdtl a");

    // for (Element element : imagesLink) {
    // String detailLink = element.attr("href");
    // imgDetailLinks.add(detailLink);
    // }

    // return imgDetailLinks;
    // }

    // /**
    // * 解析图片详情
    // *
    // * @param detailLink 详情链接
    // * @return 图片详情
    // * @throws IOException
    // */
    // private Image parseImage(String detailLink) throws IOException {
    // Document document = this.genConnection(detailLink).get();
    // Element body = document.body();

    // Image image = new Image();

    // Element imgInfoElem = body.selectFirst("#i2>div:nth-child(2)");
    // String[] tempInfo = imgInfoElem.text().split("::");
    // image.setFileName(tempInfo[0].trim());

    // Element originLink = body.selectFirst("#i7 a");

    // if (null == originLink) {
    // originLink = body.selectFirst("#img");
    // image.setDownloadUrl(originLink.attr("src"));
    // image.setFileSize(tempInfo[2].trim());
    // image.setResolution(tempInfo[1].trim());

    // } else {
    // image.setDownloadUrl(originLink.attr("href"));

    // String[] temp = originLink.text().split(" ");
    // image.setFileSize(temp[5] + temp[6]);
    // image.setResolution(temp[2] + temp[3] + temp[4]);
    // }

    // return image;
    // }

    // private String pathEncode(String path) {
    // Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
    // Matcher matcher = pattern.matcher(path);
    // path = matcher.replaceAll(""); // 将匹配到的非法字符以空替换
    // return path;
    // }
}
