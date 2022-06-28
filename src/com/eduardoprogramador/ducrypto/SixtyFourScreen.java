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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;


public class SixtyFourScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle, labelFile, labelString;
    private TextField fieldFile;
    private RadioButton rbEncode, rbDecode;
    private Button buttonFile, buttonString, buttonCopy;
    private TextArea textArea;
    private ScrollPane scrollPane;
    private LayoutHelper layoutHelper;
    private Stage stage;
    private String file, s64;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("Base 64");
        labelSubtitle = new Label("Converte bytes em algoritmo Base64 e vice-versa");
        layoutHelper.setTitleFont(labelTitle);
        layoutHelper.setNormalFont(labelSubtitle);

        rbEncode = new RadioButton("Codificar");
        rbEncode.setSelected(true);
        rbDecode = new RadioButton("Decodificar");
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(rbEncode,rbDecode);
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(rbEncode,rbDecode);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelFile = new Label("Arquivo:");
        fieldFile = new TextField();
        buttonFile = new Button("Selecionar");
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelFile,fieldFile,buttonFile);
        layoutHelper.setDefaultHbox(hBoxTwo);

        textArea = new TextArea();
        textArea.setMinWidth(1400);
        scrollPane = new ScrollPane(textArea);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        buttonCopy = new Button("Copiar");
        buttonCopy.setDisable(true);

        labelString = new Label("Não deseja operar com arquivos? Clique em trabalhar com texto.");
        buttonString = new Button("Trabalhar com Texto");
        HBox hBoxThree = new HBox();
        hBoxThree.getChildren().addAll(labelString,buttonString);
        layoutHelper.setDefaultHbox(hBoxThree);

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,labelTitle,labelSubtitle,hBoxOne,hBoxTwo,scrollPane,buttonCopy,hBoxThree,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        buttonFile.setOnAction(this::handle);
        buttonString.setOnAction(this::handle);
        buttonCopy.setOnAction(this::handle);
    }

    @Override
    public void handle(Event event) {

        if(event.getTarget().equals(buttonFile)) {
            if(rbEncode.isSelected()) {

                if((file = getFile(stage)) != null) {
                    fieldFile.setText(file);
                    Runnable runnable = () -> {

                        textArea.appendText("[*] Codificando bytes do arquivo em Base64...\n");
                        byte[] bytesSrc = OpenClasses.readFile(file);
                        if(bytesSrc == null)
                            buttonCopy.setDisable(true);
                        else
                            buttonCopy.setDisable(false);
                        s64 = OpenClasses.get64(bytesSrc);
                        textArea.appendText("\n- - - - - - BASE64 BY EDUARDO PROGRAMADOR - - - - -\n");
                        textArea.appendText(s64 + "\n");
                        textArea.appendText("\n- - - - - - BASE64 BY EDUARDO PROGRAMADOR - - - - -\n");
                    };
                    new Thread(runnable).start();
                }

            } else {

                if((file = getFile(stage)) != null) {

                    buttonCopy.setDisable(true);



                            fieldFile.setText(file);
                            textArea.appendText("[*] Decodificando bytes do arquivo texto de Base64...\n");
                            String sSrc = OpenClasses.readStringFile(file);
                            byte[] bytesOrigin = OpenClasses.put64(sSrc);
                            if(bytesOrigin == null) {
                                textArea.appendText("[*] Falha na decodificação do arquivo para Base64\n");
                            } else {
                                String toSave = saveFile(stage);
                                if(toSave == null) {
                                    textArea.appendText("[*] Nenhum arquivo selecionado\n");
                                } else {
                                    textArea.appendText("[*] Escrevendo conteúdo decodificado no arquivo: " + toSave + "...\n");
                                    OpenClasses.writeFile(toSave,bytesOrigin);
                                    textArea.appendText("[*] Arquivo texto em Base64 transformado em arquivo de Bytes com sucesso.\n");

                                }
                            }
                }

            }
        } else if(event.getTarget().equals(buttonString)) {

            if(rbEncode.isSelected()) {

                String input = singleStringInput("Texto para Base64","Texto para Base64","Texto","Codificar");
                s64 = OpenClasses.get64(input.getBytes());
                textArea.appendText("\n- - - - - - BASE64 BY EDUARDO PROGRAMADOR - - - - -\n");
                textArea.appendText(s64 + "\n");
                textArea.appendText("\n- - - - - - BASE64 BY EDUARDO PROGRAMADOR - - - - -\n");
                buttonCopy.setDisable(false);

            } else {
                String input = singleStringInput("Base64 para Texto","Base64 para Texto","Base64","Decodificar");
                byte[] inputB = OpenClasses.put64(input);
                if(inputB == null) {
                    textArea.appendText("[*] Caracteres em Base64 inválidos\n");
                    buttonCopy.setDisable(true);
                } else {
                    s64 = new String(inputB, StandardCharsets.UTF_8);
                    textArea.appendText("[*] Texto recuperado: " + s64 + "\n");
                    buttonCopy.setDisable(false);
                }


            }

        } else if(event.getTarget().equals(buttonCopy)) {
            OpenClasses.copyToClipBoard(s64);
        }
    }
}
