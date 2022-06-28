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

public class RequestScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle, labelNew, labelAlias, labelPass;
    private Label labelKeyAlg, labelKeyLength, labelKeySign;
    private ComboBox comboBoxKeyAlg, comboBoxKeyLength, comboBoxKeySign;
    private HBox hBoxOne, hBoxNew;
    private Label labelCN, labelOU, labelO;
    private TextField fieldCN, fieldOU, fieldO, fieldNew, fieldAlias;
    private PasswordField fieldPass;
    private HBox hBoxTwo;
    private Label labelL, labelST, labelC;
    private TextField fieldL, fieldST, fieldC;
    private HBox hBoxThree;
    private Label labelOut;
    private TextField fieldOut;
    private Button buttonSay, buttonNew;
    private HBox hBoxFive;
    private Button buttonCreate;
    private Stage stage;
    private String outPath, newPath;
    private TextArea textArea;

    //constructors

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {

        LayoutHelper layoutHelper = LayoutHelper.getInstance();

        labelTitle = new Label("Requisição de Certificado");
        layoutHelper.setTitleFont(labelTitle);
        labelSubtitle = new Label("Cria uma requisição de certificado para envio a uma Autoridade Certificadora (CA)");

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

        textArea = new TextArea();

        labelNew = new Label("Certificado Existente (Os dados acima serão ignorados):");
        fieldNew = new TextField();
        buttonNew = new Button("Informar");
        labelAlias = new Label("Alias:");
        fieldAlias = new TextField();
        labelPass = new Label("Senha:");
        fieldPass = new PasswordField();
        hBoxNew = new HBox();
        hBoxNew.getChildren().addAll(labelNew,fieldNew,buttonNew,labelAlias,fieldAlias,labelPass,fieldPass);
        layoutHelper.setDefaultHbox(hBoxNew);

        labelOut = new Label("Arquivo de Saída:");
        fieldOut = new TextField();
        buttonSay = new Button("Informar");
        hBoxFive = new HBox();
        hBoxFive.getChildren().addAll(labelOut,fieldOut,buttonSay);
        layoutHelper.setDefaultHbox(hBoxFive);

        buttonCreate = new Button("Criar");

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,hBoxThree,textArea,hBoxNew,hBoxFive,buttonCreate,l1,l2);
        layoutHelper.setDefaultVbox(root);

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
        buttonNew.setOnAction(this::handle);


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

            if(!fieldNew.getText().equalsIgnoreCase("")) {
                //request from certificate
                String alias = fieldAlias.getText();
                String pass = fieldPass.getText();
                if(alias.equalsIgnoreCase("") || pass.equalsIgnoreCase("")) {
                    displayDialogError("Campos Vazios","Campos Vazios","O alias e a senha não podem estar vazios se você selecionou um certificado de origem");
                } else {
                    if(CloseClasses.createRequest(newPath,keyAlg,pass,alias,signAlg,outPath)) {
                        String base64csr = null;
                        if(( base64csr = CloseClasses.readRequestContent(outPath + ".csr")) != null) {
                            textArea.setText(base64csr);
                        }
                        displayDialogInfo("Requisição Gerada","Requisição Gerada","Requisição de Assinatura de Certificado (CSR) criada com sucesso. Submeta o arquivo a uma Autoridade Certificadora (CA).");

                    } else {
                        displayDialogError("Erro de Requisição","Erro de Requisição","Não foi posível gerar a requisição de assinatura de certificado (CSR) devido a um erro interno");
                    }
                }
            } else {
                //normal request
                if(keyAlg.equalsIgnoreCase("") ||
                        keyAlgSize.equalsIgnoreCase("") ||
                        signAlg.equalsIgnoreCase("") ||
                        cn.equalsIgnoreCase("") ||
                        ou.equalsIgnoreCase("") ||
                        o.equalsIgnoreCase("") ||
                        l.equalsIgnoreCase("") ||
                        st.equalsIgnoreCase("") ||
                        c.equalsIgnoreCase("") ||
                        fieldOut.getText().equalsIgnoreCase("")) {

                    //no empty
                    displayDialogError("Campos Vazios","Campos Vazios","Nehum campo pode estar vazio");
                } else {

                    if(CloseClasses.createRequest(keyAlg,Integer.valueOf(keyAlgSize),signAlg,certNames,outPath)) {
                        String base64csr = null;
                        if(( base64csr = CloseClasses.readRequestContent(outPath + ".csr")) != null) {
                            textArea.setText(base64csr);
                        }
                        displayDialogInfo("Requisição Gerada","Requisição Gerada","Requisição de Assinatura de Certificado (CSR) criada com sucesso. Submeta o arquivo a uma Autoridade Certificadora (CA).");
                    } else {
                        displayDialogError("Erro de Requisição","Erro de Requisição","Não foi posível gerar a requisição de assinatura de certificado (CSR) devido a um erro interno");
                    }

                }
            }

        } else if(event.getTarget().equals(buttonSay)) {
            if((outPath = saveFile(stage)) != null) {
                fieldOut.setText(outPath);
            }
        } else if(event.getTarget().equals(buttonNew)) {
            if((newPath = getFile(stage)) != null) {
                fieldNew.setText(newPath);
            }
        }
    }
    //methods
}
