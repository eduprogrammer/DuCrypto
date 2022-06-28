/*
 * Copyright 2022. Eduardo Programador
 * www.eduardoprogramador.com
 * consultoria@eduardoprogramador.com
 *
 * Todos os direitos reservados
 * */

package com.eduardoprogramador.ducrypto;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class OpenClasses extends Routines {
    //declare
    public static final int HASH_STRING = 0;
    public static final int HASH_FILE = 1;


    //constructors
    public OpenClasses() {

    }

    //methods
    public static String getCertificateContent(String certPath, String password, String alias,String certType) {

        String res = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(certType);
            FileInputStream fileInputStream = new FileInputStream(certPath);
            keyStore.load(fileInputStream,password.toCharArray());
            X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
            String issuerDN = x509Certificate.getIssuerDN().getName();
            String subjectDN = x509Certificate.getSubjectDN().getName();
            String dateFrom = x509Certificate.getNotBefore().toString();
            String dateTo = x509Certificate.getNotAfter().toString();
            String sigAlg = x509Certificate.getSigAlgName();
            String serial = x509Certificate.getSerialNumber().toString();
            int version = x509Certificate.getVersion();

            res = "Emissor do Certificado: " + issuerDN + "\n";
            res += "Sujeito do Certificado: " + subjectDN + "\n";
            res += "Expedido Em: " + dateFrom + "\n";
            res += "Válido Até: " + dateTo + "\n";
            res += "Algoritmo de Assinatura: " + sigAlg + "\n";
            res += "Serial: " + serial + "\n";
            res += "Versão do Certificado: " + version + "\n";

            PublicKey publicKey = x509Certificate.getPublicKey();
            String publicKeyAlgorith = publicKey.getAlgorithm();
            String publicKeyFormat = publicKey.getFormat();
            int publicKeyLength = publicKey.getEncoded().length;
            int publicKeyLengthBits = publicKeyLength / 8;

            res += "Algoritmo da Chave Pública: " + publicKeyAlgorith + "\n";
            res += "Formato da Chave Pública: " + publicKeyFormat + "\n";
            res += "Tamanho da Chave Pública: " + publicKeyLength + " Bytes || " + publicKeyLengthBits + " bits\n";

            byte[] bytesCert = x509Certificate.getEncoded();
            String hashMD5 = hashFingerPrint(HASH_MD5,bytesCert);
            String hashSha1 = hashFingerPrint(HASH_SHA_1,bytesCert);
            String hashSha256 = hashFingerPrint(HASH_SHA_256,bytesCert);

            res += "\nFingerPrints do Certificado:\n\n";

            if(hashMD5 != null)
                res += hashMD5.toUpperCase() + "\n";
            else
                res += "Não Disponível\n";

            if(hashSha1 != null)
                res += hashSha1.toUpperCase() + "\n";
            else
                res += "Não Disponível\n";

            if(hashSha256 != null)
                res += hashSha256.toUpperCase() + "\n";
            else
                res += "Não Disponível\n";

            return res;

        } catch (KeyStoreException ex) {
            //unkown format - try with other type
            return CERT_WRONG_TYPE;
        } catch (CertificateException ex) {
            //wrong password - build dialog
            return CERT_WRONG_PASS;
        } catch (NoSuchAlgorithmException ex) {
            //internal error
            return  null;
        } catch (FileNotFoundException ex) {
            //internal error
            return null;
        } catch (IOException ex) {
            //internal error
            return null;
        }
    }

    public static String hashFingerPrint(String algorithm, byte[] input) {
        String res = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(input);
            byte[] digest = messageDigest.digest();

            for (int i = 0; i < digest.length; i++) {
                int b = digest[i] & 0xff;
                String hex = Integer.toHexString(b);
                if(i == 0) {
                    res = hex;
                } else {
                    res += ":" + hex;
                }
            }

            return algorithm + ": " + res;

        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean encryptWithPassphrase(String password, String filePath,String alg, String outPath) {
        try {

            String hash = null;
            if(alg.equalsIgnoreCase("PBEWithMD5AndDES"))
                hash = "MD5";
            else if(alg.equalsIgnoreCase("PBEWithMD5AndTripleDES"))
                hash = "MD5";
            else if(alg.equalsIgnoreCase("PBEWithSHA1AndDESede"))
                hash = "SHA";
            else if(alg.equalsIgnoreCase("PBEWithHmacSHA1AndAES_128"))
                hash = "HmacSHA1";
            else if(alg.equalsIgnoreCase("PBEWithHmacSHA1AndAES_256"))
                hash = "HmacSHA1";
            else if(alg.equalsIgnoreCase("PBEWithHmacSHA256AndAES_128"))
                hash = "HmacSHA256";
            else if(alg.equalsIgnoreCase("PBEWithHmacSHA256AndAES_256"))
                hash = "HmacSHA256";
            else if(alg.equalsIgnoreCase("PBEWithHmacSHA512AndAES_128"))
                hash = "HmacSHA512";
            else if(alg.equalsIgnoreCase("PBEWithHmacSHA512AndAES_256"))
                hash = "HmacSHA512";

            //generate secret key
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(alg);
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

            //generate salt and iterations
            FileInputStream fileInputStream = new FileInputStream(filePath);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            byte[] bytesOriginal = new byte[(int) new File(filePath).length()];
            dataInputStream.readFully(bytesOriginal);
            byte[] bytesSalt = new byte[8];

            byte[] digest = null;
            if(hash.equalsIgnoreCase("MD5") || hash.equalsIgnoreCase("SHA")) {
                //proceed with hash
                MessageDigest messageDigest = MessageDigest.getInstance(hash);
                messageDigest.update(password.getBytes());
                messageDigest.update(bytesOriginal);
                digest = messageDigest.digest();
                System.arraycopy(digest,0,bytesSalt,0,8);

            } else {
                //proceed with MAC
                Mac mac = Mac.getInstance(hash);
                mac.init(secretKey);
                mac.update(password.getBytes());
                mac.update(bytesOriginal);
                digest = mac.doFinal();
                System.arraycopy(digest,0,bytesSalt,0,8);
            }

            //encryption
            Cipher cipher = Cipher.getInstance(alg);
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(bytesSalt,20);
            cipher.init(Cipher.ENCRYPT_MODE,secretKey,pbeParameterSpec);
            byte[] bytesEncrypted = cipher.doFinal(bytesOriginal);
            FileOutputStream fileOutputStream = new FileOutputStream(outPath);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            if(!hash.equalsIgnoreCase("MD5") || !hash.equalsIgnoreCase("SHA")) {
                byte[] iv = cipher.getIV();
                dataOutputStream.writeInt(iv.length);
                dataOutputStream.write(iv);
            } else {
                dataOutputStream.writeInt(0);
            }

            dataOutputStream.write(bytesSalt);
            dataOutputStream.writeInt(bytesEncrypted.length);
            dataOutputStream.write(bytesEncrypted);
            dataOutputStream.flush();
            dataOutputStream.close();
            fileOutputStream.close();
            dataInputStream.close();
            fileInputStream.close();

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean uncryptWithPassphrase(String password, String filePath,String alg, String outPath) {
        try {
            //recover secret key
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(alg);
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

            //read salt and IV
            FileInputStream fileInputStream = new FileInputStream(filePath);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            byte[] bytesSalt = new byte[8];
            int ivLen = dataInputStream.readInt();
            byte[] iv = new byte[ivLen];
            if(ivLen != 0)
                dataInputStream.read(iv);

            dataInputStream.read(bytesSalt);
            byte[] bytesEncrypted = new byte[dataInputStream.readInt()];
            dataInputStream.read(bytesEncrypted);

            //decryption
            Cipher cipher = Cipher.getInstance(alg);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(bytesSalt,20,ivParameterSpec);
            cipher.init(Cipher.DECRYPT_MODE,secretKey,pbeParameterSpec);
            byte[] bytesDecrypted = cipher.doFinal(bytesEncrypted);
            FileOutputStream fileOutputStream = new FileOutputStream(outPath);
            fileOutputStream.write(bytesDecrypted);
            fileOutputStream.flush();
            fileOutputStream.close();
            fileInputStream.close();

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static String hashCalc(String alg, int mode, String src) {
        try {
            byte[] bytesSource = null;
            MessageDigest messageDigest = MessageDigest.getInstance(alg);

            switch (mode) {
                case HASH_FILE:
                    FileInputStream fileInputStream = new FileInputStream(src);
                    DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                    bytesSource = new byte[(int) new File(src).length()];
                    dataInputStream.readFully(bytesSource);
                    dataInputStream.close();
                    fileInputStream.close();
                    break;

                case HASH_STRING:
                    bytesSource = src.getBytes();
                    break;

                default:
                    bytesSource = src.getBytes();
                    break;
            }

            messageDigest.update(bytesSource);
            byte[] digest = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                int b = digest[i] & 0xff;
                String hex = Integer.toHexString(b);
                stringBuilder.append(hex);
            }

            return stringBuilder.toString();

        } catch (Exception ex) {
            return null;
        }
    }

    public static byte[] readFile(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            byte[] bytes = new byte[(int) new File(path).length()];
            dataInputStream.readFully(bytes);
            dataInputStream.close();
            fileInputStream.close();
            return bytes;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String readStringFile(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean writeFile(String path, byte[] bytes) {
        try {

            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String get64(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }

    public static byte[] put64(String s64) {
        try {
            return new BASE64Decoder().decodeBuffer(s64);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void copyToClipBoard(String src) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(src);
        clipboard.setContents(stringSelection,null);
    }

    public static void openWeb(String site) {
        try {
            Desktop.getDesktop().browse(URI.create("http://" + site));
        } catch (Exception ex) {
            //nothing
        }
    }

}
