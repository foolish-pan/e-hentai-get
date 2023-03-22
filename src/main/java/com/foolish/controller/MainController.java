package com.foolish.controller;

import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import com.foolish.parse.Collection;
import com.foolish.parse.ImagePage;
import com.foolish.parse.ImagePage.Status;
import com.foolish.parse.PageGroup;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController implements Initializable {

    @FXML
    private MenuItem menuItemSetProxy;

    @FXML
    private TextField txtMainUrl;

    @FXML
    private Button btnStart;

    @FXML
    private TableView<ImagePage> tableData;

    @FXML
    private TableColumn<ImagePage, Integer> colNo;

    @FXML
    private TableColumn<ImagePage, String> colFileName;

    @FXML
    private TableColumn<ImagePage, String> colResolution;

    @FXML
    private TableColumn<ImagePage, String> colFileSize;

    @FXML
    private TableColumn<ImagePage, String> colStatus;

    @FXML
    private Label labelProgress;

    @FXML
    private Label labelCollectionName;

    @FXML
    private Label labelImgCount;

    @FXML
    public void onParseStart(ActionEvent event) {
        String mainUrl = txtMainUrl.getText();
        if (mainUrl.length() > 0) {
            ObservableList<ImagePage> data = FXCollections.observableArrayList();
            data.clear();
            tableData.setItems(data);

            CompletableFuture.runAsync(() -> {
                try {
                    this.setProcessLabel("parse collection info ...");
                    Collection collection = new Collection(mainUrl);
                    Platform.runLater(() -> {
                        labelCollectionName.setText(collection.getCollectionName());
                        labelImgCount.setText(String.valueOf(collection.getImageCount()));
                    });

                    String downloadPath = "D:/" + collection.getEncodeCollectionName();
                    File floder = new File(downloadPath);
                    if (!floder.exists()) {
                        floder.mkdirs();
                    }

                    this.setProcessLabel("parse image page info ...");
                    List<PageGroup> pageGroups = collection.parsePageGroup();
                    for (PageGroup pageGroup : pageGroups) {
                        data.addAll(pageGroup.parseImagePage());
                        tableData.refresh();
                    }

                    long successCount = data.stream().filter(item -> item.getStatus() == Status.SUCCESS).count();

                    while (successCount != data.size()) {

                        for (ImagePage imagePage : data) {
                            if (imagePage.getStatus() != Status.SUCCESS) {
                                // imagePage.setStatus(Status.DOWNLOADING);

                                this.setProcessLabel("parse image download url : [" + imagePage.getImgNo() + "] "
                                        + imagePage.getFileName());
                                imagePage.parseImage();

                                this.setProcessLabel("downloading image : [" + imagePage.getImgNo() + "] "
                                        + imagePage.getFileName());
                                // boolean success =
                                imagePage.download(downloadPath);
                                // if (success) {
                                // imagePage.setStatus(Status.SUCCESS);
                                // } else {
                                // imagePage.setStatus(Status.FAILD);
                                // }
                            }
                        }
                    }

                    this.setProcessLabel("all image download success");
                    Toolkit.getDefaultToolkit().beep();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void setProcessLabel(String tips) {
        Platform.runLater(() -> {
            labelProgress.setText(tips);
        });
    }

    @FXML
    public void settingProxy(ActionEvent event) {
        System.out.println("menu set proxy click");
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        PropertyValueFactory<ImagePage, Integer> valNo = new PropertyValueFactory<ImagePage, Integer>("imgNo");
        colNo.setCellValueFactory(valNo);

        PropertyValueFactory<ImagePage, String> valFileName = new PropertyValueFactory<ImagePage, String>("fileName");
        colFileName.setCellValueFactory(valFileName);

        PropertyValueFactory<ImagePage, String> valResolution = new PropertyValueFactory<ImagePage, String>(
                "resolution");
        colResolution.setCellValueFactory(valResolution);

        PropertyValueFactory<ImagePage, String> valFileSize = new PropertyValueFactory<ImagePage, String>("fileSize");
        colFileSize.setCellValueFactory(valFileSize);

        PropertyValueFactory<ImagePage, String> valStatus = new PropertyValueFactory<ImagePage, String>("status");
        colStatus.setCellValueFactory(valStatus);
    }

}
