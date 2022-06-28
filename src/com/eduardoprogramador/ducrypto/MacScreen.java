/*
 * Copyright 2022. Eduardo Programador
 * www.eduardoprogramador.com
 * consultoria@eduardoprogramador.com
 *
 * Todos os direitos reservados
 * */

package com.eduardoprogramador.ducrypto;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MacScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle, labelCertPath, labelFilePath;
    private TextField fieldCertPath, fieldFilePath;
    private Button buttonCert, buttonFile, buttonCalc;
    private TextArea textArea;
    private LayoutHelper layoutHelper;
    private Stage stage;
    private String cert, file;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("MAC (Message Authentication Digest)");
        labelSubtitle = new Label("Confere a integridade e a autenticidade de um arquivo combinando um Hash com uma chave privada");
        layoutHelper.setTitleFont(labelTitle);
        layoutHelper.setNormalFont(labelSubtitle);

        labelCertPath = new Label("Chave:");
        fieldCertPath = new TextField();
        buttonCert = new Button("Buscar");
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelCertPath,fieldCertPath,buttonCert);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelFilePath = new Label("Arquivo de Origem (MAC):");
        fieldFilePath = new TextField();
        buttonFile = new Button("Buscar");
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelFilePath,fieldFilePath,buttonFile);
        layoutHelper.setDefaultHbox(hBoxTwo);

        buttonCalc = new Button("Calcular");
        textArea = new TextArea();

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,buttonCalc,textArea,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        buttonCert.setOnAction(this::handle);
        buttonFile.setOnAction(this::handle);
        buttonCalc.setOnAction(this::handle);

    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonCert)) {
            if((cert = getFile(stage)) != null)
                fieldCertPath.setText(cert);
        } else if(event.getTarget().equals(buttonFile)) {
            if((file = getFile(stage)) != null)
                fieldFilePath.setText(file);
        } else if(event.getTarget().equals(buttonCalc)) {

            ComboBox comboBoxKey = new ComboBox();
            comboBoxKey.getItems().addAll("AES","DES","DESede");
            comboBoxKey.getSelectionModel().select(0);
            String key = fromComboBoxInputs("Algoritmo da Chave","Especifique o tipo de algoritmo da chave selecionada","Algoritmo da Chave",comboBoxKey);

            ComboBox comboBoxAlg = new ComboBox();
            comboBoxAlg.getItems().addAll("HmacMD5","HmacSHA1","HmacSHA224","HmacSHA256","HmacSHA386","HmacSHA512");
            String alg = fromComboBoxInputs("Algoritmo MAC","Selecione o algoritmo MAC de sua preferência","Algoritmo",comboBoxAlg);

            if(fieldCertPath.getText().equalsIgnoreCase("")) {
                displayDialogError("Nenhuma Chave","Nenhuma chave","Nenhuma chave selecionada. Você não poderá calcular o MAC de um arquivo sem a chave contida em um arquivo");
            } else {
                if(fieldFilePath.getText().equalsIgnoreCase("")) {
                    String str = singleStringInput("Frase para MAC","Você não selecionou um  um arquivo. Digite uma frase para calcular o MAC.","Frase","Calcular");
                    if(!str.equalsIgnoreCase("")) {
                        String res = CloseClasses.macHash(alg,cert,str,OpenClasses.HASH_STRING,key);
                        if(res != null) {
                            textArea.setText(res);
                        } else {
                            displayDialogError("Falha de MAC","Falha de MAC","Um erro aconteceu ao tentar calcular o MAC do arquivo informado com a chave especificada");
                        }
                    }
                } else {

                    String res = CloseClasses.macHash(alg,cert,file,OpenClasses.HASH_FILE,key);
                    if(res != null) {
                        textArea.setText(res);
                    } else {
                        displayDialogError("Falha de MAC","Falha de MAC","Um erro aconteceu ao tentar calcular o MAC do arquivo informado com a chave especificada");
                    }
                }
            }


        }
    }
}
