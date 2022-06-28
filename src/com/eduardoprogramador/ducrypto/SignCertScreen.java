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

public class SignCertScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle;
    private Label labelKeyAlg, labelKeyLength, labelKeySign;
    private ComboBox comboBoxKeyAlg, comboBoxKeyLength, comboBoxKeySign;
    private HBox hBoxOne;
    private Label labelAlias, labelPass;
    private TextField fieldAlias;
    private PasswordField fieldPass;
    private HBox hBoxFour;
    private Label labelOut;
    private TextField fieldOut;
    private Button buttonSay;
    private HBox hBoxFive;
    private Button buttonCreate;
    private Label labelValidity;
    private DatePicker pickerValidity;
    private Stage stage;
    private String intPath, caPath,outPath;
    private Label labelIn, labelCA;
    private Button buttonIn, buttonCA;
    private TextField fieldIn, fieldCA;
    private HBox hBoxTwo,hBoxThree;

    //constructors

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {

        LayoutHelper layoutHelper = LayoutHelper.getInstance();

        labelTitle = new Label("Assinar Certificado");
        layoutHelper.setTitleFont(labelTitle);
        labelSubtitle = new Label("Assinar requisição de certificado (CSR) reconhecido por uma Autoridade Certificadora (CA)");
        layoutHelper.setNormalFont(labelSubtitle);

        labelIn = new Label("Requisição de Certificado (CSR):");
        fieldIn = new TextField();
        buttonIn = new Button("Carregar");
        hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelIn,fieldIn,buttonIn);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelCA = new Label("Certificado da CA:");
        fieldCA = new TextField();
        buttonCA = new Button("Carregar");
        labelAlias = new Label("Alias:");
        fieldAlias = new TextField();
        labelPass = new Label("Senha:");
        fieldPass = new PasswordField();
        hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelCA,fieldCA,buttonCA,labelAlias,fieldAlias,labelPass,fieldPass);
        layoutHelper.setDefaultHbox(hBoxTwo);

        Label labelDetails = new Label("Detalhes do Novo Certificado");
        layoutHelper.setNormalFont(labelDetails);

        labelOut = new Label("Arquivo de Saída:");
        fieldOut = new TextField();
        buttonSay = new Button("Informar");
        hBoxThree = new HBox();
        hBoxThree.getChildren().addAll(labelOut,fieldOut,buttonSay);
        layoutHelper.setDefaultHbox(hBoxThree);

        labelKeyAlg = new Label("Algoritmo da Chave:");
        comboBoxKeyAlg = new ComboBox();
        comboBoxKeyAlg.getItems().addAll("RSA","DSA");
        labelKeyLength = new Label("Comprimento da Chave:");
        comboBoxKeyLength = new ComboBox();
        comboBoxKeyLength.getItems().addAll("1024 bits","2048 bits","4096 bits");
        labelKeySign = new Label("Algoritmo de Assinatura:");
        comboBoxKeySign = new ComboBox();
        comboBoxKeySign.getItems().addAll("SHA256WithRSA","SHA512WithRSA","SHA256WithDSA","SHA512WithDSA");
        hBoxFour = new HBox();
        hBoxFour.getChildren().addAll(labelKeyAlg,comboBoxKeyAlg,labelKeyLength,comboBoxKeyLength,labelKeySign,comboBoxKeySign);
        layoutHelper.setDefaultHbox(hBoxFour);

        labelValidity = new Label("Válido Até:");
        pickerValidity = new DatePicker();
        hBoxFive = new HBox();
        hBoxFive.getChildren().addAll(labelValidity,pickerValidity);
        layoutHelper.setDefaultHbox(hBoxFive);

        buttonCreate = new Button("Criar");

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,labelDetails,hBoxThree,hBoxFour,hBoxFive,buttonCreate);
        layoutHelper.setDefaultVbox(root);

        comboBoxKeySign.getSelectionModel().select(0);
        comboBoxKeyLength.getSelectionModel().select(0);
        comboBoxKeyAlg.getSelectionModel().select(0);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        this.stage = stage;

        //listenning
        buttonIn.setOnAction(this::handle);
        buttonCA.setOnAction(this::handle);
        buttonSay.setOnAction(this::handle);
        buttonCreate.setOnAction(this::handle);
    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonCreate)) {

            String keyAlg = comboBoxKeyAlg.valueProperty().get().toString();
            String keyAlgSize = comboBoxKeyLength.valueProperty().get().toString().split(" ")[0];
            String signAlg = comboBoxKeySign.valueProperty().get().toString();

            String alias = fieldAlias.getText();
            String pass = fieldPass.getText();
            String bruteTime = pickerValidity.getEditor().getText();

            if(keyAlg.equalsIgnoreCase("") ||
                    keyAlgSize.equalsIgnoreCase("") ||
                    signAlg.equalsIgnoreCase("") ||
                    alias.equalsIgnoreCase("") ||
                    pass.equalsIgnoreCase("") ||
                    fieldIn.getText().equalsIgnoreCase("") ||
                    fieldCA.getText().equalsIgnoreCase("") ||
                    fieldOut.getText().equalsIgnoreCase("") ||
                    bruteTime.equalsIgnoreCase("")) {
                //no empty
                displayDialogError("Campos Vazios","Campos Vazios","Nehum campo pode estar vazio");
            } else {
                //proceed
                if(CloseClasses.signCertificateByCA(intPath,caPath,keyAlg,alias,pass,outPath,bruteTime)) {
                    displayDialogInfo("Certificado Assinado","Certificado Assinado","Certificado assinado com sucesso. Um arquivo com a extensão .cer foi gerado com êxito. Proceda à importação do novo certificado em um certificado pré-existente");
                } else {
                    displayDialogError("Erro de Assinatura","Erro de Assinatura","Um erro ocorreu ao tentar realizar-se a assinatura do certificado");
                }

            }
        } else if(event.getTarget().equals(buttonSay)) {
            if((outPath = saveFile(stage)) != null) {
                fieldOut.setText(outPath);
            }
        } else if(event.getTarget().equals(buttonIn)) {
            if((intPath = getFile(stage)) != null) {
                fieldIn.setText(intPath);
            }
        } else if(event.getTarget().equals(buttonCA)) {
            if((caPath = getFile(stage)) != null) {
                fieldCA.setText(caPath);
            }
        }
    }
    //methods
}
