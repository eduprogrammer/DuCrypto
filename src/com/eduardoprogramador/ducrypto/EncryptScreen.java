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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EncryptScreen extends Routines implements LayoutHelper.OnLayoutStart {
    //declare
    private Label labelTitle, labelSubtitle, labelMethod, labelFileInput, labelFileOutput, labelAlg, labelKeyPath;
    private RadioButton rbPassphrase, rbAlg;
    private ToggleGroup toggleGroup;
    private TextField fieldFileInput, fieldFileOutput, fieldKeyPath;
    private Button buttonFileIn, buttonFileOut, buttonKey, buttonEncrypt;
    private LayoutHelper layoutHelper;
    private Stage stage;
    private String key, fileIn, fileOut;
    private ComboBox comboBoxAlg;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {

        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("Nova Criptografia");
        layoutHelper.setTitleFont(labelTitle);
        labelSubtitle = new Label("Proteger arquivos com algoritmos de criptografia");
        layoutHelper.setNormalFont(labelSubtitle);

        labelMethod = new Label("Método Criptográfico");
        rbPassphrase = new RadioButton("Palavra Chave");
        rbPassphrase.setSelected(true);
        rbAlg = new RadioButton("Algoritmo Tradicional");
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(rbPassphrase,rbAlg);
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelMethod,rbPassphrase,rbAlg);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelFileInput = new Label("Arquivo de Origem:");
        fieldFileInput = new TextField();
        buttonFileIn     = new Button("Buscar");
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelFileInput,fieldFileInput,buttonFileIn);
        layoutHelper.setDefaultHbox(hBoxTwo);

        labelFileOutput = new Label("Arquivo de Destino:");
        fieldFileOutput = new TextField();
        buttonFileOut = new Button("Salvar");
        HBox hBoxOut = new HBox();
        hBoxOut.getChildren().addAll(labelFileOutput,fieldFileOutput,buttonFileOut);
        layoutHelper.setDefaultHbox(hBoxOut);

        labelAlg = new Label("Algoritmo:");
        comboBoxAlg = new ComboBox();
        comboBoxAlg.getItems().addAll("AES","DES","DESede");
        comboBoxAlg.getSelectionModel().select(0);
        HBox hBoxThree = new HBox();
        hBoxThree.getChildren().addAll(labelAlg,comboBoxAlg);
        hBoxThree.setDisable(true);
        layoutHelper.setDefaultHbox(hBoxThree);

        labelKeyPath = new Label("Arquivo de Chave:");
        fieldKeyPath = new TextField();
        buttonKey = new Button("Abrir");
        HBox hBoxFour = new HBox();
        hBoxFour.getChildren().addAll(labelKeyPath,fieldKeyPath,buttonKey);
        hBoxFour.setDisable(true);
        layoutHelper.setDefaultHbox(hBoxFour);

        buttonEncrypt = new Button("Criptografar");

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,hBoxOut,hBoxThree,hBoxFour,buttonEncrypt,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue.equals(rbPassphrase)) {
                    hBoxThree.setDisable(true);
                    hBoxFour.setDisable(true);
                } else if(newValue.equals(rbAlg)) {
                    hBoxThree.setDisable(false);
                    hBoxFour.setDisable(false);
                }
            }
        });

        buttonFileIn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if((fileIn = getFile(stage)) != null) {
                    fieldFileInput.setText(fileIn);
                }
            }
        });

        buttonFileOut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if((fileOut = saveFile(stage)) != null) {
                    fieldFileOutput.setText(fileOut);
                }
            }
        });

        buttonKey.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if((key = getFile(stage)) != null)
                    fieldKeyPath.setText(key);
            }
        });


        buttonEncrypt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(rbAlg.isSelected()) {
                    //build traditional method
                    if(!fieldFileInput.getText().equalsIgnoreCase("") &&
                            !fieldFileOutput.getText().equalsIgnoreCase("") &&
                    !fieldKeyPath.getText().equalsIgnoreCase("")) {
                        //proceed
                        String alg = getAlgorith();
                        String ivPath = yesOrNoFileSaveDialog("Utilizar IV","Deseja utilizar um vetor de inicialização (IV) com o modo CBC de criptografia?",stage);
                        if(ivPath != null) {
                            //build iv
                            if(CloseClasses.encrypt(alg,fileIn,fileOut,key,ivPath)) {
                                displayDialogInfo("Arquivo Criptografado","Arquivo Criptografado","O arquivo que você selecionou foi criptografado com sucesso");
                            } else {
                                displayDialogError("Erro de Criptografia","Erro de Criptografia","Um erro ocorreu durante a tentativa de criptografar o arquivo selecionado");
                            }
                        } else {
                            if(CloseClasses.encrypt(alg,fileIn,fileOut,key)) {
                                displayDialogInfo("Arquivo Criptografado","Arquivo Criptografado","O arquivo que você selecionou foi criptografado com sucesso");
                            } else {
                                displayDialogError("Erro de Criptografia","Erro de Criptografia","Um erro ocorreu durante a tentativa de criptografar o arquivo selecionado");
                            }
                        }


                    } else {
                        displayDialogError("Parâmetros Insuficientes","Parâmetros Insuficientes","Para iniciar a criptografia, é necessário informar um arquivo de origem e uma chave de criptografia já criada");
                    }

                } else if(rbPassphrase.isSelected()) {
                    //build passphrase method
                    if(!fieldFileInput.getText().equalsIgnoreCase("") &&
                    !fieldFileOutput.getText().equalsIgnoreCase("")) {
                        //proceed
                        String pass = singlePasswordInput("Senha","Informe a Palavra Chave");
                        if(pass == null || pass.equalsIgnoreCase("")) {
                            displayDialogError("Erro de Senha","Erro de Senha","A senha não pode ser vazia");
                        } else if(pass.length() < 8) {
                            displayDialogError("Senha Insuficiente","Senha Insuficiente","A senha não pode conter menos de 8 (oito) caracteres");
                        } else {
                            //proceed
                            ComboBox comboBoxPassPhrase = new ComboBox();
                            comboBoxPassPhrase.getItems().addAll("PBEWithMD5AndDES","PBEWithMD5AndTripleDES","PBEWithSHA1AndDESede","PBEWithHmacSHA1AndAES_128","PBEWithHmacSHA1AndAES_256","PBEWithHmacSHA256AndAES_128","PBEWithHmacSHA256AndAES_256","PBEWithHmacSHA512AndAES_128","PBEWithHmacSHA512AndAES_256");
                            String passphfraseMethod = fromComboBoxInputs("Algoritmo de Palavra Chave","Algoritmo de Palavra Chave","Algoritmo",comboBoxPassPhrase);
                            if(OpenClasses.encryptWithPassphrase(pass,fileIn,passphfraseMethod,fileOut))
                                displayDialogInfo("Arquivo Criptografado","Arquivo Criptografado","O arquivo que você selecionou foi criptografado com sucesso");

                            else
                                displayDialogError("Erro de Criptografia","Erro de Criptografia","Um erro ocorreu durante a tentativa de criptografar o arquivo selecionado");
                        }
                    } else {
                        displayDialogError("Nenhum arquivo selecionado","Nenhum arquivo selecionado","Para prosseguir com a criptografia por palavra chave, é necessário ao menos informar um arquivo de origem e destino");
                    }
                }
            }
        });

    }

    private String getAlgorith() {
        String brute = comboBoxAlg.valueProperty().getValue().toString();
        return brute;
    }

}
