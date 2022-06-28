/*
 * Copyright 2022. Eduardo Programador
 * www.eduardoprogramador.com
 * consultoria@eduardoprogramador.com
 *
 * Todos os direitos reservados
 * */

package com.eduardoprogramador.ducrypto;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InitScreen extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //declare
    private Label labelTitle, labelSubtitle, labelVersion, labelCreditsOne, labelCreditsTwo;
    private Image imageIco;
    private ImageView imageView;
    private MenuBar menuBar;
    private Stage stage;
    private Scene scene;
    private VBox root;
    LayoutHelper layoutHelper;
    private Loader loader;


    public InitScreen() {

    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.stage = primaryStage;
        layoutHelper = LayoutHelper.getInstance();

        labelTitle = new Label("DuCrypto");
        layoutHelper.setTitleFont(labelTitle);
        labelSubtitle = new Label("A sua proteção criptografada no Cybermundo");
        layoutHelper.setTitleFont(labelSubtitle);
        labelVersion = new Label("Versão PRÓ 1.0");
        layoutHelper.setNormalFont(labelVersion);
        labelCreditsOne = new Label("Copyright 2022. Eduardo Programador. Todos os direitos reservados.");
        layoutHelper.setFooterFont(labelCreditsOne);
        labelCreditsTwo = new Label("www.eduardoprogramador.com / consultoria@eduardoprogramador.com");
        layoutHelper.setFooterFont(labelCreditsTwo);
        imageIco = layoutHelper.getIco();
        imageView = new ImageView(imageIco);
        imageView.setImage(imageIco);
        menuBar = layoutHelper.processAppMenu();

        root = new VBox();
        layoutHelper.setDefaultVbox(root);
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,imageView,labelVersion,labelCreditsOne,labelCreditsTwo);

        scene = new Scene(root,700,660);

        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,imageIco);

        loader = new Loader(menuBar,labelCreditsOne,labelCreditsTwo,stage,imageIco);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

}
