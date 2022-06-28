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
import sun.misc.BASE64Encoder;

public class CryptoChatScreen extends Routines implements LayoutHelper.OnLayoutStart, EventHandler {
    //declare
    private Label labelTitle, labelSubtitle, labelIp, labelPort, labelMsg, labelMode;
    private RadioButton rbClient, rbServer;
    private TextField fieldIp, fieldPort, fieldMsg;
    private TextArea textArea;
    private Button buttonStart, buttonMsg, buttonFinish;
    private ScrollPane scrollPane;
    private LayoutHelper layoutHelper;
    private Stage stage;
    private NetImplementator netImplementator;
    private byte[] startUp;

    @Override
    public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image) {
        layoutHelper = LayoutHelper.getInstance();
        this.stage = stage;

        labelTitle = new Label("Chat Criptográfico");
        labelSubtitle = new Label("Inicia chat criptografado com o protocolo Diffie-Hellman");
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
        buttonFinish = new Button("Encerrar");
        buttonFinish.setDisable(true);
        HBox hBoxTwo = new HBox();
        hBoxTwo.getChildren().addAll(labelIp,fieldIp,labelPort,fieldPort,buttonStart,buttonFinish);
        layoutHelper.setDefaultHbox(hBoxTwo);

        textArea = new TextArea();
        textArea.setMinWidth(1400);
        scrollPane = new ScrollPane(textArea);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        labelMsg = new Label("Mensagem:");
        fieldMsg = new TextField();
        buttonMsg = new Button("Enviar");
        buttonMsg.setDisable(true);
        HBox hBoxThree = new HBox();
        hBoxThree.getChildren().addAll(labelMsg,fieldMsg,buttonMsg);
        layoutHelper.setDefaultHbox(hBoxThree);

        VBox root = new VBox();
        root.getChildren().addAll(menuBar,hBoxOne,hBoxTwo,scrollPane,hBoxThree,l1,l2);
        layoutHelper.setDefaultVbox(root);

        Scene scene = new Scene(root,700,660);
        stage.setScene(scene);
        layoutHelper.loadDefaultStage(stage,image);

        //listenning
        buttonMsg.setOnAction(this::handle);
        buttonStart.setOnAction(this::handle);
        buttonFinish.setOnAction(this::handle);
    }

    @Override
    public void handle(Event event) {
        if(event.getTarget().equals(buttonStart)) {
            String ip = fieldIp.getText();
            int port = Integer.valueOf(fieldPort.getText());

            if(rbClient.isSelected()) {

                Runnable runnable = () -> {
                    try {

                        CloseClasses.createDH(NetImplementator.CLIENT_MODE);
                        textArea.appendText("[*] Novo par de chaves assimétricas criado\n");
                        textArea.appendText("Chave Pública: " + new BASE64Encoder().encode(CloseClasses.getClientPublicKey()) + "\n");
                        textArea.appendText("Chave Privada: ***\n");

                        netImplementator = NetImplementator.getInstance(NetImplementator.CLIENT_MODE);
                        netImplementator.setIp(ip);
                        netImplementator.setPort(port);
                        netImplementator.initialize();
                        textArea.appendText("[*] Conectado ao servidor: " + netImplementator.getServerInfo() + "\n");

                        netImplementator.sendBytes(CloseClasses.getClientPublicKey());
                        textArea.appendText("[*] Chave pública enviada ao servidor.\n");
                        startUp = netImplementator.receiveBytes();
                        textArea.appendText("[*] Recebida a chave pública do servidor: " + new BASE64Encoder().encode(startUp) + "\n");

                        CloseClasses.exChangeDH(NetImplementator.CLIENT_MODE,startUp);
                        textArea.appendText("[*] Chave secreta gerada: *** Iniciado chat seguro\n");
                        scrollPane.setVvalue(scrollPane.getVmax());

                        buttonMsg.setDisable(false);
                        buttonStart.setDisable(true);
                        buttonFinish.setDisable(false);

                        startRcv();

                    } catch (Exception ex) {
                        buttonStart.setDisable(false);
                        buttonFinish.setDisable(true);
                        textArea.appendText("[*] Chat Finalizado\n");
                        scrollPane.setVvalue(scrollPane.getVmax());
                    }
                };
                new Thread(runnable).start();



            } else if(rbServer.isSelected()) {

                Runnable runnable = () -> {
                    try {

                        CloseClasses.createDH(NetImplementator.SERVER_MODE);
                        textArea.appendText("[*] Novo par de chaves assimétricas criado\n");
                        textArea.appendText("Chave Pública: " + new BASE64Encoder().encode(CloseClasses.getServerPublicKey()) + "\n");
                        textArea.appendText("Chave Privada: ***\n");

                        netImplementator = NetImplementator.getInstance(NetImplementator.SERVER_MODE);
                        netImplementator.setPort(port);
                        if(!ip.equalsIgnoreCase(""))
                            netImplementator.setIp(ip);
                        textArea.appendText("[*] Aguardando Conexão na porta: " + port + "\n");
                        netImplementator.initialize();

                        if(NetImplementator.clientAttached) {
                            textArea.appendText("[*] Conectado ao cliente: " + netImplementator.getClientInfo() + "\n");

                            startUp = netImplementator.receiveBytes();
                            textArea.appendText("[*] Recebida a chave pública do cliente: " + new BASE64Encoder().encode(startUp) + "\n");
                            netImplementator.sendBytes(CloseClasses.getServerPublicKey());
                            textArea.appendText("[*] Chave pública enviada ao cliente.\n");

                            CloseClasses.exChangeDH(NetImplementator.SERVER_MODE,startUp);
                            textArea.appendText("[*] Chave secreta gerada: *** Iniciado chat seguro\n");
                            scrollPane.setVvalue(scrollPane.getVmax());

                            buttonMsg.setDisable(false);
                            buttonStart.setDisable(true);
                            buttonFinish.setDisable(false);

                            startRcv();
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        buttonStart.setDisable(false);
                        buttonFinish.setDisable(true);
                        textArea.appendText("[*] Chat Finalizado\n");
                        scrollPane.setVvalue(scrollPane.getVmax());
                    }
                };
                new Thread(runnable).start();
            }

        } else if(event.getTarget().equals(buttonMsg)) {
            Runnable runnable = () -> {
                try {
                    String msg = fieldMsg.getText();
                    byte[] bytesMsg = CloseClasses.toDHEncrypt(msg.getBytes());
                    netImplementator.sendBytes(bytesMsg);
                    textArea.appendText("[VOCÊ] -> " + msg + "\n");
                    fieldMsg.setText("");
                } catch (Exception ex) {
                    buttonStart.setDisable(false);
                    buttonFinish.setDisable(true);
                    textArea.appendText("[*] Chat Finalizado\n");
                }
            };
            new Thread(runnable).start();

        } else if(event.getTarget().equals(buttonFinish)) {
            try {
                netImplementator.endOfConnection();
                buttonStart.setDisable(false);
                buttonFinish.setDisable(true);
            } catch (Exception ex) {
                //null
            }
        }

    }

    private void startRcv() {

        Runnable runnable = () -> {
            try {
                while (true) {
                    byte[] received = netImplementator.receiveBytes();
                    byte[] normal = CloseClasses.toDHDecrypt(received);
                    String recv = new String(normal);
                    textArea.appendText("[PARCEIRO] -> " + recv + "\n");
                }
            } catch (Exception ex) {
                buttonStart.setDisable(false);
                buttonFinish.setDisable(true);
                textArea.appendText("[*] Chat Finalizado\n");
            }
        };
        new Thread(runnable).start();
    }
}
