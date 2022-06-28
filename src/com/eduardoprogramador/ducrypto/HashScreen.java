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

public class HashScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle, labelAlg, labelDetails, labelFile;
    private ComboBox comboBoxAlg;
    private TextField fieldFile;
    private Button buttonSearch, buttonCalc;
    private TextArea textArea;
    private Stage stage;
    private LayoutHelper layoutHelper;
    private String file;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        this.stage = stage;
        layoutHelper = LayoutHelper.getInstance();

        labelTitle = new Label("Algoritmo Hash (Digest)");
        layoutHelper.setTitleFont(labelTitle);
        labelSubtitle = new Label("Gerar e conferir algoritmos de hash para a verificação da integridade de arquivos");
        layoutHelper.setNormalFont(labelSubtitle);

        labelAlg = new Label("Algoritmo:");
        comboBoxAlg = new ComboBox();
        comboBoxAlg.getItems().addAll("MD2","MD5","SHA-1","SHA-224","SHA-256","SHA-384","SHA-512");
        comboBoxAlg.getSelectionModel().select(1);
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelAlg,comboBoxAlg);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelFile = new Label("*Arquivo:");
        fieldFile = new TextField();
        buttonSearch = new Button("Buscar");
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelFile,fieldFile,buttonSearch);
        layoutHelper.setDefaultHbox(hBoxTwo);

        buttonCalc = new Button("Calcular");
        labelDetails = new Label("*Obs: Se desejar apenas calcular o algoritmo Hash de uma frase, ignore esse campo e clique em Calcular. Na janela que se abrirá, digite a frase e clique em Prosseguir.");
        textArea = new TextArea();

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,buttonCalc,labelDetails,textArea,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        buttonSearch.setOnAction(this::handle);
        buttonCalc.setOnAction(this::handle);

    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonSearch)) {
            if((file = getFile(stage)) != null)
                fieldFile.setText(file);
        } else if(event.getTarget().equals(buttonCalc)) {

            String alg = comboBoxAlg.valueProperty().getValue().toString();

            if(fieldFile.getText().equalsIgnoreCase("")) {
                //hash from string
                String strToHash = singleStringInput("Digite uma frase","Digite uma palavra ou frase para calcular o algoritmo Hash","Frase","Calcular");
                if(!strToHash.equalsIgnoreCase("")) {
                    String res = OpenClasses.hashCalc(alg,OpenClasses.HASH_STRING,strToHash);
                    if(res != null) {
                        textArea.setText(res);
                        displayDialogInfo("Hash Retornado","Hash Retornado","Algoritmo hash calculado com sucesso. Confira a integridade da frase com o algoritmo selecionado");
                    } else {
                        displayDialogError("Falha de Hash","Falha de Hash","Não foi possivel calcular o hash da frase selecionada");
                    }
                }
            } else {
                //hash from file
                String res = OpenClasses.hashCalc(alg,OpenClasses.HASH_FILE,file);
                if(res != null) {
                    textArea.setText(res);
                    displayDialogInfo("Hash Retornado","Hash Retornado","Algoritmo hash calculado com sucesso. Confira a integridade do arquivo com o algoritmo selecionado");
                } else {
                    displayDialogError("Falha de Hash","Falha de Hash","Não foi possivel calcular o hash do arquivo selecionado");
                }
            }
        }
    }
}
