/*
 * Copyright 2022. Eduardo Programador
 * www.eduardoprogramador.com
 * consultoria@eduardoprogramador.com
 *
 * Todos os direitos reservados
 * */

package com.eduardoprogramador.ducrypto;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SignScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    private Label labelTitle, labelSubtitle, labelSignType, labelCertType, labelCertPath, labelAlias, labelPass;
    private Label labelFile, labelSignPath, labelCert, labelMethod;
    private RadioButton rbNew, rbVerify;
    private ComboBox comboBoxSignAlg, comboBoxCertAlg;
    private TextField fieldCertPath, fieldAlias;
    private PasswordField fieldPass;
    private TextField fieldFile, fieldSignPath, fieldCert;
    private Button buttonFindKeystore, buttonFindFile, buttonFindSign, buttonFindCert, buttonSign;
    private Stage stage;
    private LayoutHelper layoutHelper;
    private String keystore, signature, cert, file;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("Assinatura Digital");
        labelSubtitle = new Label("Garante a autenticidade de um arquivo por meio de assinaturas de chaves públicas e privadas");
        layoutHelper.setTitleFont(labelTitle);
        layoutHelper.setNormalFont(labelSubtitle);

        labelMethod = new Label("Método:");
        rbNew = new RadioButton("Nova Assinatura");
        rbVerify = new RadioButton("Conferir Assinatura");
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(rbNew,rbVerify);
        rbNew.setSelected(true);
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelMethod,rbNew,rbVerify);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelSignType = new Label("Tipo de Assinatura:");
        comboBoxSignAlg = new ComboBox();
        comboBoxSignAlg.getItems().addAll("MD5WithRSA","SHA1WithRSA","SHA256WithRSA","SHA512WithRSA","SHA1WithDSA","SHA256WithDSA","SHA512WithDSA");
        comboBoxSignAlg.getSelectionModel().select(2);
        labelCertType = new Label("Tipo de Certificado:");
        comboBoxCertAlg = new ComboBox();
        comboBoxCertAlg.getItems().addAll("PFX","JKS");
        comboBoxCertAlg.getSelectionModel().select(0);
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelSignType,comboBoxSignAlg,labelCertType,comboBoxCertAlg);
        layoutHelper.setDefaultHbox(hBoxTwo);

        labelCertPath = new Label("Local do Certificado:");
        fieldCertPath = new TextField();
        buttonFindKeystore = new Button("Procurar");
        labelAlias = new Label("Alias:");
        fieldAlias = new TextField();
        labelPass = new Label("Senha:");
        fieldPass = new PasswordField();
        HBox hBoxThree = new HBox();
        hBoxThree.getChildren().addAll(labelCertPath,fieldCertPath,buttonFindKeystore,labelAlias,fieldAlias,labelPass,fieldPass);
        layoutHelper.setDefaultHbox(hBoxThree);

        labelFile = new Label("Arquivo a Assinar:");
        fieldFile = new TextField();
        buttonFindFile = new Button("Escolher");
        HBox hBoxFour = new HBox();
        hBoxFour.getChildren().addAll(labelFile,fieldFile,buttonFindFile);
        layoutHelper.setDefaultHbox(hBoxFour);

        labelSignPath = new Label("Local da Assinatura:");
        fieldSignPath = new TextField();
        buttonFindSign = new Button("Informar");
        HBox hBoxFive = new HBox();
        hBoxFive.getChildren().addAll(labelSignPath,fieldSignPath,buttonFindSign);
        layoutHelper.setDefaultHbox(hBoxFive);

        labelCert = new Label("Certificado (.CER):");
        fieldCert = new TextField();
        buttonFindCert = new Button("Carregar");
        HBox hBoxSix = new HBox();
        hBoxSix.getChildren().addAll(labelCert,fieldCert,buttonFindCert);
        layoutHelper.setDefaultHbox(hBoxSix);

        buttonSign = new Button("Assinar");

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,hBoxThree,hBoxFour,hBoxFive,hBoxSix,buttonSign,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);


        //default
        hBoxSix.setDisable(true);
        comboBoxCertAlg.setDisable(false);
        hBoxThree.setDisable(false);

        //CloseClasses.makeSign("SHA256withRSA","PKCS12","C:\\Users\\PMRecife\\Desktop\\eduardoprogramador.pfx","FenixEduWingProgram","eduardoprogramador","C:\\Users\\PMRecife\\Downloads\\Local\\Imagens\\Ayanne\\20200306_101509.jpg","C:\\Users\\PMRecife\\Downloads\\Local\\Imagens\\Ayanne\\20200306_101509.sign");
        //boolean res = CloseClasses.verifySign("SHA256withRSA","C:\\Users\\PMRecife\\Desktop\\eduardoprogramador.cer","C:\\Users\\PMRecife\\Downloads\\Local\\Imagens\\Ayanne\\20200306_101509.sign","C:\\Users\\PMRecife\\Downloads\\Local\\Imagens\\Ayanne\\20200306_101509.jpg");
        //System.out.println(res);

        //listenning
        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue.equals(rbNew)) {
                    hBoxSix.setDisable(true);
                    comboBoxCertAlg.setDisable(false);
                    hBoxThree.setDisable(false);
                } else if(newValue.equals(rbVerify)) {
                    hBoxSix.setDisable(false);
                    comboBoxCertAlg.setDisable(true);
                    hBoxThree.setDisable(true);
                }
            }
        });

        buttonFindKeystore.setOnAction(this::handle);
        buttonFindFile.setOnAction(this::handle);
        buttonFindSign.setOnAction(this::handle);
        buttonFindCert.setOnAction(this::handle);
        buttonSign.setOnAction(this::handle);
    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonFindKeystore)) {
            if((keystore = getFile(stage)) != null)
                fieldCertPath.setText(keystore);
        } else if(event.getTarget().equals(buttonFindFile)) {
            if((file = getFile(stage)) != null)
                fieldFile.setText(file);
        } else if(event.getTarget().equals(buttonFindSign)) {
            if(rbNew.isSelected()) {
                if((signature = saveFile(stage)) != null)
                    fieldSignPath.setText(signature);
            } else if(rbVerify.isSelected()) {
                if((signature = getFile(stage)) != null)
                    fieldSignPath.setText(signature);
            }
        } else if(event.getTarget().equals(buttonFindCert)) {
            if((cert = getFile(stage)) != null)
                fieldCert.setText(cert);
        } else if(event.getTarget().equals(buttonSign)) {

            if(rbVerify.isSelected()) {
                if(fieldSignPath.getText().equalsIgnoreCase("") ||
                fieldFile.getText().equalsIgnoreCase("") ||
                fieldCert.getText().equalsIgnoreCase("")) {
                    displayDialogError("Campos Vazios","Campos Vazios","Os campos do arquivo de assinatura, arquivo a ser assinado e certificado (.cer) não podem estar vazios");
                } else {
                    String signAlg = comboBoxSignAlg.valueProperty().getValue().toString();

                    if(CloseClasses.verifySign(signAlg,cert,signature,file)) {
                        displayDialogInfo("Assinatura Verificada","Assinatura Verificada","Assinatura verificada com sucesso. Foi verificada a autenticidade do arquivo com base no certificado selecionado");
                    } else {
                        displayDialogError("Erro de Conferência","Erro de Conferência","Um erro ocorreu durante a conferência das assinaturas ou a assinatura é inválida");
                    }
                }


            } else if(rbNew.isSelected()) {
                if(fieldCertPath.getText().equalsIgnoreCase("") ||
                fieldAlias.getText().equalsIgnoreCase("") ||
                fieldPass.getText().equalsIgnoreCase("") ||
                fieldFile.getText().equalsIgnoreCase("") ||
                fieldSignPath.getText().equalsIgnoreCase("")) {
                    displayDialogError("Campos Vazios","Campos Vazios","Os campos certificado, alias, senha, arquivo a assinar e assinatura não podem estar vazios");
                } else {
                    String signAlg = comboBoxSignAlg.valueProperty().getValue().toString();
                    String certAlg = comboBoxCertAlg.valueProperty().getValue().toString();
                    certAlg = certAlg.equalsIgnoreCase("JKS") ? "JKS" : "PKCS12";
                    if(CloseClasses.makeSign(signAlg,certAlg,keystore,fieldPass.getText(),fieldAlias.getText(),file,signature)) {
                        displayDialogInfo("Assinatura Criada","Assinatura Criada","Assinatura Criada com Sucesso");
                    } else {
                        displayDialogError("Erro de Assinatura","Erro de Assinatura","Um erro ocorreu ao tentar criar a assinatura");
                    }
                }

            }


        }
    }
}
