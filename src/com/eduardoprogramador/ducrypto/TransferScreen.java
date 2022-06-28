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

public class TransferScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler, ChangeListener {
    //declare
    private Label labelTitle, labelSubtitle, labelIp, labelPort, labelMsg, labelMode;
    private RadioButton rbClient, rbServer;
    private TextField fieldIp, fieldPort, fieldMsg;
    private TextArea textArea;
    private Button buttonStart, buttonMsg;
    private ScrollPane scrollPane;
    private LayoutHelper layoutHelper;
    private Stage stage;
    private NetImplementator netImplementator;
    private byte[] startUp;
    private String file;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("Transferência de Arquivos");
        labelSubtitle = new Label("Transfere arquivos de forma criptografada entre dois host com Diffie-Hellman");
        layoutHelper.setTitleFont(labelTitle);
        layoutHelper.setNormalFont(labelSubtitle);

        labelMode = new Label("Modo:");
        rbClient = new RadioButton("Cliente");
        rbServer = new RadioButton("Servidor");
        rbServer.setSelected(true);
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(rbClient,rbServer);
        HBox hBoxOne = new HBox();
        hBoxOne.getChildren().addAll(labelMode,rbClient,rbServer);
        layoutHelper.setDefaultHbox(hBoxOne);

        labelIp = new Label("IP:");
        fieldIp = new TextField();
        labelPort = new Label("Porta:");
        fieldPort = new TextField();
        buttonStart = new Button("Iniciar");
        buttonStart.setDisable(true);
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelIp,fieldIp,labelPort,fieldPort,buttonStart);
        layoutHelper.setDefaultHbox(hBoxTwo);

        textArea = new TextArea();
        textArea.setMinWidth(1400);
        scrollPane = new ScrollPane(textArea);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        labelMsg = new Label("Arquivo:");
        fieldMsg = new TextField();
        buttonMsg = new Button("Salvar");
        HBox hBoxThree = new HBox();
        hBoxThree.getChildren().addAll(labelMsg,fieldMsg,buttonMsg);
        layoutHelper.setDefaultHbox(hBoxThree);

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,hBoxOne,hBoxTwo,scrollPane,hBoxThree,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        this.stage.setScene(scene);
        layoutHelper.loadDefaultStage(this.stage,image);

        //listenning
        buttonMsg.setOnAction(this::handle);
        buttonStart.setOnAction(this::handle);
        toggleGroup.selectedToggleProperty().addListener(this::changed);
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if(newValue.equals(rbClient)) {
            buttonMsg.setText("Abrir");
        } else if(newValue.equals(rbServer)) {
            buttonMsg.setText("Salvar");
        }
    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonStart)) {

            buttonMsg.setDisable(true);

            String ip = fieldIp.getText();
            int port = Integer.valueOf(fieldPort.getText());

            if(rbClient.isSelected()) {

                Runnable runnable = () -> {

                try {
                    byte[] bytesOriginal = OpenClasses.readFile(file);

                    netImplementator = NetImplementator.getInstance(NetImplementator.CLIENT_MODE);
                    netImplementator.setIp(ip);
                    netImplementator.setPort(port);
                    textArea.appendText("[*] Tentando conexão com o servidor: " + ip + ":" + port + "\n");
                    netImplementator.initialize();
                    textArea.appendText("[*] Conectado ao servidor:" + netImplementator.getServerInfo() + "\n");

                    CloseClasses.createDH(NetImplementator.CLIENT_MODE);
                    netImplementator.sendBytes(CloseClasses.getClientPublicKey());
                    textArea.appendText("[*] Enviada chave pública ao servidor\n");
                    startUp = netImplementator.receiveBytes();
                    textArea.appendText("[*] Recebida a chave pública do servidor: " + OpenClasses.get64(startUp) + "\n");
                    scrollPane.setVvalue(scrollPane.getVmax());
                    CloseClasses.exChangeDH(NetImplementator.CLIENT_MODE,startUp);
                    textArea.appendText("[*] Handshake completo. Chave secreta criada: " + OpenClasses.get64(CloseClasses.getSecret()) + "\n");
                    scrollPane.setVvalue(scrollPane.getVmax());

                    textArea.appendText("[*] Iniciando criptografia do arquivo: " + file + "\n");
                    scrollPane.setVvalue(scrollPane.getVmax());
                    byte[] bE = CloseClasses.toDHEncrypt(bytesOriginal);
                    textArea.appendText("[*] Arquivo criptogrado com sucesso. Enviando....\n");
                    scrollPane.setVvalue(scrollPane.getVmax());

                    netImplementator.sendBytesByPieces(bE);
                    netImplementator.setOk(NetImplementator.CLIENT_MODE);
                    textArea.appendText("[*] Arquivo enviado com sucesso\n");
                    scrollPane.setVvalue(scrollPane.getVmax());

                } catch (Exception ex) {
                    textArea.appendText("[*] Falha de Envio. Não foi possível enviar o arquivo criptografado ao servidor de destino\n");
                    scrollPane.setVvalue(scrollPane.getVmax());
                }

            };
                new Thread(runnable).start();


            } else if(rbServer.isSelected()) {

                Runnable runnable = () -> {
                    try {
                        netImplementator = NetImplementator.getInstance(NetImplementator.SERVER_MODE);
                        netImplementator.setPort(port);
                        textArea.appendText("[*] Servidor aberto na porta: " + port + ". Aguardando conexão...\n");
                        netImplementator.initialize();

                        if(NetImplementator.clientAttached) {
                            textArea.appendText("[*] Novo cliente conectado: " + netImplementator.getClientInfo() + "\n");

                            CloseClasses.createDH(NetImplementator.SERVER_MODE);
                            startUp = netImplementator.receiveBytes();
                            textArea.appendText("[*] Recebida a chave pública do cliente: " + OpenClasses.get64(startUp) + "\n");
                            scrollPane.setVvalue(scrollPane.getVmax());
                            netImplementator.sendBytes(CloseClasses.getServerPublicKey());
                            textArea.appendText("[*] Chave pública enviada ao cliente\n");
                            CloseClasses.exChangeDH(NetImplementator.SERVER_MODE,startUp);
                            textArea.appendText("[*] Handshake completo. Chave secreta criada: " + OpenClasses.get64(CloseClasses.getSecret()) + "\n");
                            scrollPane.setVvalue(scrollPane.getVmax());

                            byte[] bytesRcv = netImplementator.receiveBytesByPieces();
                            textArea.appendText("[*] Arquivo criptografado recebido. Descriptografando...\n");
                            scrollPane.setVvalue(scrollPane.getVmax());

                            byte[] decBytes = CloseClasses.toDHDecrypt(bytesRcv);
                            textArea.appendText("[*] Arquivo descriptografado com sucesso. Escrevendo arquivo em: " + file + "\n");
                            scrollPane.setVvalue(scrollPane.getVmax());

                            OpenClasses.writeFile(file,decBytes);
                            netImplementator.setOk(NetImplementator.SERVER_MODE);

                            textArea.appendText("[*] Arquivo recebido e descriptografado com sucesso.\n");
                            scrollPane.setVvalue(scrollPane.getVmax());

                        }


                    } catch (Exception ex) {
                        textArea.appendText("[*] Falha de recebimento. Um erro ocorreu durante o recebimento do arquivo criptografado\n");
                        scrollPane.setVvalue(scrollPane.getVmax());
                    }
                };
                new Thread(runnable).start();
            }

        } else if(event.getTarget().equals(buttonMsg)) {
            if((file = (rbClient.isSelected()) ? getFile(stage) : saveFile(stage)) != null) {
                fieldMsg.setText(file);
                textArea.appendText("[ARQUIVO SELECIONADO] -> " + file + "\n");
                buttonStart.setDisable(false);
            }

        }

    }
}
