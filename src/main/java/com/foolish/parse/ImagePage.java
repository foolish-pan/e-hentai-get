package com.foolish.parse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import com.foolish.conn.ConnectionFactory;
import com.foolish.conn.CookieFactory;
import com.foolish.conn.ProxyFactory;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ImagePage {

    private int imgNo;

    private String imageUrl;

    private String fileName;

    private String downloadUrl;

    private StringProperty fileSize = new SimpleStringProperty();

    private StringProperty resolution = new SimpleStringProperty();

    private StringProperty status = new SimpleStringProperty();

    public ImagePage(int imgNo, String fileName, String imageUrl) {
        this.imgNo = imgNo;
        this.fileName = fileName;
        this.imageUrl = imageUrl;
        this.status.set(Status.WAIT.name());
    }

    public ImagePage parseImage() throws IOException {
        System.out.println("Start parse image info from url: " + this.imageUrl);
        this.setStatus(Status.PARSING);
        try {
            Connection connection = ConnectionFactory.conn(this.imageUrl, ProxyFactory.defaultProxy(),
                    CookieFactory.defaultCookie());

            Document document = connection.get();
            Element body = document.body();

            // Image image = new Image();

            Element imgInfoElem = body.selectFirst("#i2>div:nth-child(2)");
            String[] tempInfo = imgInfoElem.text().split("::");
            this.setFileName(tempInfo[0].trim());

            Element originLink = body.selectFirst("#i7 a");

            if (null == originLink) {
                originLink = body.selectFirst("#img");
                this.setDownloadUrl(originLink.attr("src"));
                this.setFileSize(tempInfo[2].trim());
                this.setResolution(tempInfo[1].trim());

            } else {
                this.setDownloadUrl(originLink.attr("href"));

                String[] temp = originLink.text().split(" ");
                this.setFileSize(temp[5] + temp[6]);
                this.setResolution(temp[2] + temp[3] + temp[4]);
            }

            this.setStatus(Status.DOWNLOADING);
            System.out.println("Parse success:" + this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.setStatus(Status.PARSE_FAILD);
        }
        return this;
    }

    public boolean download(String floderPath) {
        System.out.println("Start download " + this);
        if (Status.DOWNLOADING != this.getStatus()) {
            return false;
        }
        try {
            Connection connection = ConnectionFactory.conn(this.downloadUrl, ProxyFactory.defaultProxy(),
                    CookieFactory.defaultCookie());

            Response response = connection.ignoreContentType(true).execute();
            BufferedInputStream imgStream = response.bodyStream();

            FileUtils.copyInputStreamToFile(imgStream, new File(floderPath, this.fileName));
            System.out.println("Download success ~");

            this.setStatus(Status.SUCCESS);
            return true;
        } catch (Exception e) {
            this.setStatus(Status.FAILD);
            System.out.println("Download error:" + e.getMessage());
            return false;
        }
    }

    public int getImgNo() {
        return imgNo;
    }

    public void setImgNo(int imgNo) {
        this.imgNo = imgNo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileSize() {
        return fileSize.get();
    }

    public void setFileSize(String fileSize) {
        this.fileSize.set(fileSize);
    }

    public String getResolution() {
        return resolution.get();
    }

    public void setResolution(String resolution) {
        this.resolution.set(resolution);
    }

    public Status getStatus() {
        return Status.valueOf(status.get());
    }

    public void setStatus(Status status) {
        this.status.set(status.name());
    }

    public StringProperty resolutionProperty() {
        return resolution;
    }

    public StringProperty fileSizeProperty() {
        return fileSize;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public enum Status {
        WAIT, PARSING, PARSE_FAILD, DOWNLOADING, SUCCESS, FAILD
    }

    @Override
    public String toString() {
        return "ImagePage [downloadUrl=" + downloadUrl + ", fileName=" + fileName + ", fileSize=" + fileSize
                + ", imageUrl=" + imageUrl + ", imgNo=" + imgNo + ", resolution=" + resolution + ", status=" + status
                + "]";
    }

    

}
