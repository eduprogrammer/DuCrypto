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
import java.util.ArrayList;

public class CreateCertScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle;
    private Label labelKeyAlg, labelKeyLength, labelKeySign;
    private ComboBox comboBoxKeyAlg, comboBoxKeyLength, comboBoxKeySign;
    private HBox hBoxOne;
    private Label labelCN, labelOU, labelO;
    private TextField fieldCN, fieldOU, fieldO;
    private HBox hBoxTwo;
    private Label labelL, labelST, labelC;
    private TextField fieldL, fieldST, fieldC;
    private HBox hBoxThree;
    private Label labelAlias, labelPass, labelType;
    private TextField fieldAlias;
    private PasswordField fieldPass;
    private ComboBox comboBoxType;
    private HBox hBoxFour;
    private Label labelOut;
    private TextField fieldOut;
    private Button buttonSay;
    private HBox hBoxFive;
    private Button buttonCreate;
    private Label labelValidity;
    private DatePicker pickerValidity;
    private HBox hBoxSix;
    private Stage stage;
    private String outPath;

    //constructors

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {

        LayoutHelper layoutHelper = LayoutHelper.getInstance();

        labelTitle = new Label("Criar Certificado");
        layoutHelper.setTitleFont(labelTitle);
        labelSubtitle = new Label("Criar um certificado válido para garantia de autenticação e identidade");

        labelKeyAlg = new Label("Algoritmo da Chave:");
        comboBoxKeyAlg = new ComboBox();
        comboBoxKeyAlg.getItems().addAll("RSA","DSA");
        labelKeyLength = new Label("Comprimento da Chave:");
        comboBoxKeyLength = new ComboBox();
        comboBoxKeyLength.getItems().addAll("1024 bits","2048 bits","4096 bits");
        labelKeySign = new Label("Algoritmo de Assinatura:");
        comboBoxKeySign = new ComboBox();
        comboBoxKeySign.getItems().addAll("SHA256WithRSA","SHA512WithRSA","SHA256WithDSA","SHA512WithDSA");
        hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelKeyAlg,comboBoxKeyAlg,labelKeyLength,comboBoxKeyLength,labelKeySign,comboBoxKeySign);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelCN = new Label("Emissor (CN):");
        fieldCN = new TextField();
        labelOU = new Label("Setor (OU):");
        fieldOU = new TextField();
        labelO = new Label("Organização (O):");
        fieldO = new TextField();
        hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelCN,fieldCN,labelOU,fieldOU,labelO,fieldO);
        layoutHelper.setDefaultHbox(hBoxTwo);

        labelL = new Label("Local (L):");
        fieldL = new TextField();
        labelST = new Label("Estado:");
        fieldST = new TextField();
        labelC = new Label("País:");
        fieldC = new TextField();
        hBoxThree = new HBox();
        hBoxThree.getChildren().addAll(labelL,fieldL,labelST,fieldST,labelC,fieldC);
        layoutHelper.setDefaultHbox(hBoxThree);

        labelAlias = new Label("Alias:");
        fieldAlias = new TextField();
        labelPass = new Label("Senha:");
        fieldPass = new PasswordField();
        labelType = new Label("Tipo:");
        comboBoxType = new ComboBox();
        comboBoxType.getItems().addAll("JKS","PFX");
        hBoxFour = new HBox();
        hBoxFour.getChildren().addAll(labelAlias,fieldAlias,labelPass,fieldPass,labelType,comboBoxType);
        layoutHelper.setDefaultHbox(hBoxFour);

        labelOut = new Label("Arquivo de Saída:");
        fieldOut = new TextField();
        buttonSay = new Button("Informar");
        hBoxFive = new HBox();
        hBoxFive.getChildren().addAll(labelOut,fieldOut,buttonSay);
        layoutHelper.setDefaultHbox(hBoxFive);

        labelValidity = new Label("Válido Até:");
        pickerValidity = new DatePicker();
        hBoxSix = new HBox();
        hBoxSix.getChildren().addAll(labelValidity,pickerValidity);
        layoutHelper.setDefaultHbox(hBoxSix);

        buttonCreate = new Button("Criar");

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,hBoxThree,hBoxFour,hBoxFive,hBoxSix,buttonCreate,l1,l2);
        layoutHelper.setDefaultVbox(root);

        comboBoxType.getSelectionModel().select(0);
        comboBoxKeySign.getSelectionModel().select(0);
        comboBoxKeyLength.getSelectionModel().select(0);
        comboBoxKeyAlg.getSelectionModel().select(0);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        this.stage = stage;

        //listenning
        buttonCreate.setOnAction(this::handle);
        buttonSay.setOnAction(this::handle);


    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonCreate)) {
            String keyAlg = comboBoxKeyAlg.valueProperty().get().toString();
            String keyAlgSize = comboBoxKeyLength.valueProperty().get().toString().split(" ")[0];
            String signAlg = comboBoxKeySign.valueProperty().get().toString();

            ArrayList<String> certNames = new ArrayList<>();
            String cn = fieldCN.getText(); certNames.add(cn); //0
            String ou = fieldOU.getText(); certNames.add(ou); //1
            String o = fieldO.getText(); certNames.add(o); //2
            String l = fieldL.getText(); certNames.add(l);//3
            String st = fieldST.getText(); certNames.add(st);//4
            String c = fieldC.getText(); certNames.add(c);//5

            String alias = fieldAlias.getText();
            String pass = fieldPass.getText();
            String type = (comboBoxType.valueProperty().get().toString().equalsIgnoreCase("JKS")) ? "JKS" : "PKCS12";
            String bruteTime = pickerValidity.getEditor().getText();

            if(keyAlg.equalsIgnoreCase("") ||
            keyAlgSize.equalsIgnoreCase("") ||
            signAlg.equalsIgnoreCase("") ||
            cn.equalsIgnoreCase("") ||
            ou.equalsIgnoreCase("") ||
            o.equalsIgnoreCase("") ||
            l.equalsIgnoreCase("") ||
            st.equalsIgnoreCase("") ||
            c.equalsIgnoreCase("") ||
            alias.equalsIgnoreCase("") ||
            pass.equalsIgnoreCase("") ||
            type.equalsIgnoreCase("") ||
            fieldOut.getText().equalsIgnoreCase("") ||
            bruteTime.equalsIgnoreCase("")) {
                //no empty
                displayDialogError("Campos Vazios","Campos Vazios","Nehum campo pode estar vazio");
            } else {
                //proceed
                long timeCert = getTimeFromString(bruteTime);

                    if(timeCert != 0) {
                        //create certificate with days supplied by the user
                        boolean isCertOk = CloseClasses.createNewCert(type,keyAlg,signAlg,certNames,pass,alias,Integer.valueOf(keyAlgSize),timeCert * 24 * 60 * 60,outPath);
                        if(isCertOk) {
                            displayDialogInfo("Certificado criado com sucesso","Certificado criado com sucesso","O Certificado que você criou foi armazenado com êxito no arquivo especificado");
                        } else {
                            displayDialogError("Erro na geração do certificado","Erro na geração do certificado","Ocorreu um erro interno na geração do certificado");
                        }
                    } else {
                        //create certificate with default days: 365 days
                        boolean isCertOk = CloseClasses.createNewCert(type,keyAlg,signAlg,certNames,pass,alias,Integer.valueOf(keyAlgSize),365 * 24 * 60 * 60,outPath);
                        if(isCertOk) {
                            displayDialogInfo("Certificado criado com sucesso","Certificado criado com sucesso","O Certificado que você criou foi armazenado com êxito no arquivo especificado");
                        } else {
                            displayDialogError("Erro na geração do certificado","Erro na geração do certificado","Ocorreu um erro interno na geração do certificado");
                        }
                    }




            }
        } else if(event.getTarget().equals(buttonSay)) {
            if((outPath = saveFile(stage)) != null) {
                fieldOut.setText(outPath);
            }
        }
    }
    //methods
}
