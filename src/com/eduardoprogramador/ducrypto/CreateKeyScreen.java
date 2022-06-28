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

public class CreateKeyScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler, ChangeListener {
    //declare
    private Label labelTitle, labelSubtitle, labelPath, labelAlg, labelSize;
    private TextField fieldPath;
    private ComboBox comboBoxAlg, comboBoxSize;
    private Button buttonChoose, buttonCreate;
    private LayoutHelper layoutHelper;
    private Stage stage;
    private String keyPath;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("Gerador de Chave");
        layoutHelper.setTitleFont(labelTitle);
        labelSubtitle = new Label("Criar chaves criptográficas para implementação de criptografia");
        layoutHelper.setNormalFont(labelSubtitle);

        labelPath = new Label("Caminho do Arquivo:");
        fieldPath = new TextField();
        buttonChoose = new Button("Escolher");
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelPath,fieldPath,buttonChoose);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelAlg = new Label("Algoritmo de Criptografia:");
        comboBoxAlg = new ComboBox();
        comboBoxAlg.getItems().addAll("AES","DES","DESede");
        comboBoxAlg.getSelectionModel().select(0);
        labelSize = new Label("Tamanho da Chave:");
        comboBoxSize = new ComboBox();
        comboBoxSize.getItems().addAll("128 bits","192 bits","256 bits");
        comboBoxSize.getSelectionModel().select(0);
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelAlg,comboBoxAlg,labelSize,comboBoxSize);
        layoutHelper.setDefaultHbox(hBoxTwo);

        buttonCreate = new Button("Gerar");

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,buttonCreate,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);

        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        buttonChoose.setOnAction(this::handle);
        buttonCreate.setOnAction(this::handle);
        comboBoxAlg.valueProperty().addListener(this::changed);

    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonChoose)) {
            if((keyPath = saveFile(stage)) != null) {
                fieldPath.setText(keyPath);
            }
        } else if(event.getTarget().equals(buttonCreate)) {
            if(fieldPath.getText().equalsIgnoreCase("")) {
                displayDialogError("Nenhuma Chave a Criar","Nenhuma Chave a Criar","Antes de prosseguir, informe um caminho para que a chave gerada seja salva");
            } else {
                String alg = comboBoxAlg.valueProperty().getValue().toString();
                int keySize = Integer.valueOf(comboBoxSize.valueProperty().getValue().toString().split(" ")[0]);
                if(CloseClasses.createKey(alg,keySize,keyPath)) {
                    displayDialogInfo("Chave Criada","Chave Criada","A chave que você especificou com o algoritmo e comprimento selecionados foi gerada com sucesso");
                } else {
                    displayDialogError("Erro de Criação","Erro de Criação","Um erro ocorreu na tentativa de geração da chave com os parâmetros informados");
                }
            }
        }
    }


    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if(newValue.toString().equalsIgnoreCase("AES")) {
            comboBoxSize.getItems().remove(0,comboBoxSize.getItems().size());
            comboBoxSize.getItems().addAll("128 bits","192 bits","256 bits");
            comboBoxSize.getSelectionModel().select(0);
        } else if(newValue.toString().equalsIgnoreCase("DES")) {
            comboBoxSize.getItems().remove(0,comboBoxSize.getItems().size());
            comboBoxSize.getItems().addAll("56 bits");
            comboBoxSize.getSelectionModel().select(0);
        } else if(newValue.toString().equalsIgnoreCase("DESede")) {
            comboBoxSize.getItems().remove(0,comboBoxSize.getItems().size());
            comboBoxSize.getItems().addAll("112 bits","168 bits");
            comboBoxSize.getSelectionModel().select(0);
        }
    }
}
