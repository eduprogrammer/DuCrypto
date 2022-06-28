/*
 * Copyright 2022. Eduardo Programador
 * www.eduardoprogramador.com
 * consultoria@eduardoprogramador.com
 *
 * Todos os direitos reservados
 * */

package com.eduardoprogramador.ducrypto;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.InputStream;

public abstract class LayoutHelper {
    //declare
    private static LayoutHelper layoutHelper;
    private MenuBar menuBar;
    private Label l1, l2;
    private Stage stage;
    private Image image;
    private static MenuItem itemSeeCert, itemCreateCert, convertCert, itemCertRequest, itemSignCert, itemImportCert;
    private static MenuItem itemNewCrypto, itemUndoCrypto, itemCreateKey, itemHash, itemMac, itemSign, itemChat;
    private static MenuItem itemTransfer, itemBase64, itemToImage, itemFromImage;


    //constructor

    //methods
    public static LayoutHelper getInstance() {
        layoutHelper = new Loader();
        return layoutHelper;
    }

    public void setTitleFont(Label label) {
        label.setFont(new Font("calibri",20));
    }

    public void setNormalFont(Label label) {
        label.setFont(new Font("arial",18));
    }

    public void setFooterFont(Label label) {
        label.setFont(new Font("arial",15));
    }

    public Image getIco() throws Exception {
        /*
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("com/eduardoprogramador/ducrypto/ico_app.png");
        return new Image(inputStream);
         */
        return new Image("https://eduardoprogramador.com/img_/logos/logo.png");
    }

    public MenuBar processAppMenu() {
        menuBar = new MenuBar();
        Menu menuCert = new Menu("Certificados");
        Menu menuCrypto = new Menu("Criptografia");
        Menu menuModule = new Menu("Módulos");
        Menu menuStegan = new Menu("Esteganografia");
        Menu menuAbout = new Menu("Sobre");
        itemSeeCert = new MenuItem("Ver Certificado");
        itemCreateCert = new MenuItem("Criar Certificado");
        convertCert = new MenuItem("Converter Certificado");
        itemCertRequest = new MenuItem("Requisição de Certificado");
        itemSignCert = new MenuItem("Assinar Certificado (CA)");
        itemImportCert = new MenuItem("Importar Certificado");
        itemNewCrypto = new MenuItem("Nova Criptografia");
        itemUndoCrypto = new MenuItem("Descriptografar Arquivos");
        itemCreateKey = new MenuItem("Criar Chave");
        itemHash = new MenuItem("Algoritmo Hash (Digest)");
        itemMac = new MenuItem("MAC");
        itemSign = new MenuItem("Assinatura Digital");
        itemChat = new MenuItem("Chat Criptográfico");
        itemTransfer = new MenuItem("Transferidor de Arquivos");
        itemBase64 = new MenuItem("Base64");
        itemToImage = new MenuItem("Embutir em Imagem");
        itemFromImage = new MenuItem("Retirar de Imagem");
        MenuItem itemAboutSoft = new MenuItem("Sobre o DuCrypto");
        MenuItem itemBuy = new MenuItem("-----");
        MenuItem itemDeveloper = new MenuItem("Sobre o Desenvolvedor");
        MenuItem itemSite = new MenuItem("Site do Desenvolvedor");

        menuCert.getItems().addAll(itemSeeCert,itemCreateCert,convertCert,itemCertRequest,itemSignCert,itemImportCert);
        menuCrypto.getItems().addAll(itemNewCrypto,itemUndoCrypto,itemCreateKey);
        menuModule.getItems().addAll(itemHash,itemMac,itemSign,itemChat,itemTransfer,itemBase64);
        menuStegan.getItems().addAll(itemToImage,itemFromImage);
        menuAbout.getItems().addAll(itemAboutSoft,itemBuy,itemDeveloper,itemSite);

        menuBar.getMenus().addAll(menuCert,menuCrypto,menuModule,menuStegan,menuAbout);

        return menuBar;
    }

    public void setDefaultVbox(VBox vBox) {
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10,10,10,10));
    }

    public void setDefaultHbox(HBox hbox) {
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER);
    }

    public void loadDefaultStage(Stage stage, Image image) {
        stage.setTitle("DuCrypto");
        stage.getIcons().addAll(image);
        stage.hide();
        stage.setMaximized(true);
        stage.show();
    }

    //new inferfaces

    @FunctionalInterface
    public interface OnLayoutStart {

        public void start(MenuBar menuBar, Label l1, Label l2, Stage stage, Image image);

    }

    //new classes

    public class Certificate {
        //declare
        private MenuBar menuBar;
        private Label l1, l2;
        private Stage stage;
        private Image image;

        //constructors
        public Certificate(MenuBar menuBar,Label l1, Label l2, Stage stage, Image image) {
            this.menuBar = menuBar;
            this.l1 = l1;
            this.l2 = l2;
            this.stage = stage;
            this.image = image;
        }

        //methods
        public void initSee() {
            CertificateScreen certificateScreen = new CertificateScreen();
            certificateScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initCreate() {
            CreateCertScreen createCertScreen = new CreateCertScreen();
            createCertScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initConvert() {
            ConvertCertScreen convertCertScreen = new ConvertCertScreen();
            convertCertScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initRequest() {
            RequestScreen requestScreen = new RequestScreen();
            requestScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initCA() {
            SignCertScreen signCertScreen = new SignCertScreen();
            signCertScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initImport() {
            ImportScreen importScreen = new ImportScreen();
            importScreen.start(menuBar,l1,l2,stage,image);
        }
    }

    public class Crypto {
        //declare
        private MenuBar menuBar;
        private Label l1, l2;
        private Stage stage;
        private Image image;

        //constructors
        public Crypto(MenuBar menuBar,Label l1, Label l2, Stage stage, Image image) {
            this.menuBar = menuBar;
            this.l1 = l1;
            this.l2 = l2;
            this.stage = stage;
            this.image = image;
        }

        //methods

        public void initEncrypt() {
            EncryptScreen encryptScreen = new EncryptScreen();
            encryptScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initUnencrypt() {
            UnencryptScreen unencryptScreen = new UnencryptScreen();
            unencryptScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initCreateKey() {
            CreateKeyScreen createKeyScreen = new CreateKeyScreen();
            createKeyScreen.start(menuBar,l1,l2,stage,image);
        }

    }

    public class Modules {
        //declare
        private MenuBar menuBar;
        private Label l1, l2;
        private Stage stage;
        private Image image;

        //constructors
        public Modules(MenuBar menuBar,Label l1, Label l2, Stage stage, Image image) {
            this.menuBar = menuBar;
            this.l1 = l1;
            this.l2 = l2;
            this.stage = stage;
            this.image = image;
        }

        //methods

        public void initHash() {
            HashScreen hashScreen = new HashScreen();
            hashScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initMac() {
            MacScreen macScreen = new MacScreen();
            macScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initSign() {
            SignScreen signScreen = new SignScreen();
            signScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initChat() {
            CryptoChatScreen cryptoChatScreen = new CryptoChatScreen();
            cryptoChatScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initTransfer() {
            TransferScreen transferScreen = new TransferScreen();
            transferScreen.start(menuBar,l1,l2,stage,image);
        }

        public void initBase64() {
            SixtyFourScreen sixtyFourScreen = new SixtyFourScreen();
            sixtyFourScreen.start(menuBar,l1,l2,stage,image);
        }

    }

    public class Stega {
        //declare
        private MenuBar menuBar;
        private Label l1, l2;
        private Stage stage;
        private Image image;

        //constructors
        public Stega(MenuBar menuBar,Label l1, Label l2, Stage stage, Image image) {
            this.menuBar = menuBar;
            this.l1 = l1;
            this.l2 = l2;
            this.stage = stage;
            this.image = image;
        }

        //methods
        public void toImage() {
            StegaScreen stegaScreen = new StegaScreen();
            stegaScreen.start(menuBar,l1,l2,stage,image);
        }

        public void fromImage() {
            FromStegaScreen fromStegaScreen = new FromStegaScreen();
            fromStegaScreen.start(menuBar,l1,l2,stage,image);
        }
    }
}
