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

public class ConvertCertScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle;
    private Label labelMode,labelCertSrc, labelCertOut,labelAlias,labelPass;
    private ComboBox comboBoxMode;
    private TextField fieldCertSrc,fieldCertOut,fieldAlias;
    private PasswordField fieldPass;
    private Button buttonConvert, buttonSrc, buttonOut;
    private LayoutHelper layoutHelper;
    private Stage stage;
    private String srcCert, outCert;
    private boolean canConvert;
    String fromType, toType;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {

        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("Converter Certificados");
        layoutHelper.setTitleFont(labelTitle);
        labelSubtitle = new Label("Converter certificados de PFX para JKS e de JKS para PFX");
        layoutHelper.setNormalFont(labelSubtitle);
        labelMode = new Label("Modo de Conversão:");
        comboBoxMode = new ComboBox();
        comboBoxMode.getItems().addAll("JKS para PFX","PFX para JKS");
        comboBoxMode.getSelectionModel().select(0);
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelMode,comboBoxMode);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelCertSrc = new Label("Certificado de Origem:");
        fieldCertSrc = new TextField();
        buttonSrc = new Button("Carregar");
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelCertSrc,fieldCertSrc,buttonSrc);
        layoutHelper.setDefaultHbox(hBoxTwo);

        labelCertOut = new Label("Certificado de Destino:");
        fieldCertOut = new TextField();
        buttonOut = new Button("Salvar");
        HBox hBoxThree = new HBox();
        hBoxThree.getChildren().addAll(labelCertOut,fieldCertOut,buttonOut);
        layoutHelper.setDefaultHbox(hBoxThree);

        labelAlias = new Label("Alias:");
        fieldAlias = new TextField();
        labelPass = new Label("Senha:");
        fieldPass = new PasswordField();
        HBox hBoxFour = new HBox();
        hBoxFour.getChildren().addAll(labelAlias,fieldAlias,labelPass,fieldPass);
        layoutHelper.setDefaultHbox(hBoxFour);

        buttonConvert = new Button("Converter");

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,hBoxThree,hBoxFour,buttonConvert,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        buttonSrc.setOnAction(this::handle);
        buttonOut.setOnAction(this::handle);
        buttonConvert.setOnAction(this::handle);

    }


    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonSrc)) {
            srcCert = getFile(stage);
            if(srcCert != null)
                fieldCertSrc.setText(srcCert);
            canConvert = outCert != null;
        } else if(event.getTarget().equals(buttonOut)) {
            outCert = saveFile(stage);
            if(outCert != null)
                fieldCertOut.setText(outCert);
            canConvert = srcCert != null;
        } else if(event.getTarget().equals(buttonConvert)) {
            if(canConvert) {
                getCertType();
                String alias = fieldAlias.getText();
                String password = fieldPass.getText();
                if(!alias.equalsIgnoreCase("") && !password.equalsIgnoreCase("")) {
                    if(CloseClasses.convertCert(fromType,srcCert,password,alias,toType,outCert)) {
                        displayDialogInfo("Certificado Convertido","Certificado Convertido","O certificado original com o formato " + fromType + " foi convertido com sucesso para o formato " + toType);
                        fieldCertSrc.setText("");
                        fieldCertOut.setText("");
                        srcCert = null;

                    } else {
                        displayDialogError("Erro de Conversão","Erro de Conversão","Um erro ocorreu ao tentar realizar-se a conversão do certificado para outro formato");
                        fieldCertSrc.setText("");
                        fieldCertOut.setText("");
                        fieldAlias.setText("");
                        fieldPass.setText("");
                        outCert = null;
                    }

                } else {
                    displayDialogError("Campos Vazios","Campos Vazios","Os campos de alias e senha do certificado não podem estar vazios");
                    fieldCertSrc.setText("");
                    fieldCertOut.setText("");
                    fieldAlias.setText("");
                    fieldPass.setText("");
                    outCert = null;
                }
            }
        }
    }

    private void getCertType() {
        String bruteType = comboBoxMode.valueProperty().get().toString();
        String[] parts = bruteType.split(" ");
        fromType = parts[0];
        if(fromType.equalsIgnoreCase("PFX"))
            fromType = "PKCS12";
        toType = parts[2];
        if(toType.equalsIgnoreCase("PFX"))
            toType = "PKCS12";
    }
}
