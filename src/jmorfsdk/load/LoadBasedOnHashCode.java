/*
 * Copyright (C) 2017  Alexander Porechny alex.porechny@mail.ru
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0) as published by the Creative Commons.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * for more details.
 *
 * You should have received a copy of the Attribution-NonCommercial-ShareAlike
 * 3.0 Unported (CC BY-SA 3.0) along with this program.
 * If not, see <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 *
 * Copyright (C) 2017 Александр Поречный alex.porechny@mail.ru
 *
 * Эта программа свободного ПО: Вы можете распространять и / или изменять ее
 * в соответствии с условиями Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0), опубликованными Creative Commons.
 *
 * Эта программа распространяется в надежде, что она будет полезна,
 * но БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ; без подразумеваемой гарантии
 * КОММЕРЧЕСКАЯ ПРИГОДНОСТЬ ИЛИ ПРИГОДНОСТЬ ДЛЯ ОПРЕДЕЛЕННОЙ ЦЕЛИ.
 * См. Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * для более подробной информации.
 *
 * Вы должны были получить копию Attribution-NonCommercial-ShareAlike 3.0
 * Unported (CC BY-SA 3.0) вместе с этой программой.
 * Если нет, см. <Https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 */
package jmorfsdk.load;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jmorfsdk.JMorfSdk;
import jmorfsdk.form.MainForm;
import jmorfsdk.form.WordForm;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LoadBasedOnHashCode implements LoadFromFile {

    private int pointer = 0;
    public BufferedReader inHashCodeAndString;
    public BufferedReader inHashCodeAndMainFormString;
    private static String pathHashAndMorfCharacteristics = "dictionary.format.hash+morfCharacteristic.txt";
    private static String pathHashAndMainFormString = "dictionary.format.hash+mainFormString.txt";
    private static String pathHashAndWordFormString = "dictionary.format.hash+wordFormString.txt";
    private static String encoding = "Windows-1251";
    private static final Integer CONTROLVALUE = 0;//new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    @Override
    public JMorfSdk loadLibraryForSearchInitialForm() throws Exception {
        JMorfSdk jMorfSdk = new JMorfSdk();
        loadFromFileHashCodeAndMorfCharacteristic(jMorfSdk);
        return jMorfSdk;
    }

    @Override
    public JMorfSdk loadLibraryForSearchForFormByMorphologicalCharacteristics() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JMorfSdk loadFullLibrary() throws Exception {
        return loadLibraryForSearchInitialForm();
    }

    public void loadFromFileHashCodeAndMorfCharacteristic(JMorfSdk jMorfSdk) {
        try {
            FileInputStream inHashCodeAndMorfCharacteristics = openFileInputStreamFromFile(pathHashAndMorfCharacteristics);
            loadFromFileHashString(inHashCodeAndMorfCharacteristics, jMorfSdk);
            closeStream(inHashCodeAndMorfCharacteristics);
            inHashCodeAndMorfCharacteristics = null;
        } catch (Exception ex) {
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.SEVERE, "", ex);
        }
    }

    private FileInputStream openFileInputStreamFromFile(String pathLibrary) throws Exception {
        try {
            return new FileInputStream(pathLibrary);
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s, в случае отсуствия скачайте с репозитория %s\r\n", pathLibrary, MYREPOSITORY);
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.SEVERE, messages);
            throw new Exception();
        }
    }

    private void loadFromFileHashString(FileInputStream inHashCodeAndMorfCharacteristics, JMorfSdk jMorfSdk) {

        byte[] bytesFile;
        try {
            bytesFile = new byte[inHashCodeAndMorfCharacteristics.available()];

            inHashCodeAndMorfCharacteristics.read(bytesFile);

            while (pointer < bytesFile.length) {

                int nextHashCode = getHashCodeFromBytes(bytesFile);
                byte typeOfSpeech = getTypeOfSpeechFromBytes(bytesFile);
                long morfCharacteristics = getMorfCharacteristicsFromBytes(bytesFile);

                MainForm mainForm = new MainForm(nextHashCode, typeOfSpeech, morfCharacteristics);
                jMorfSdk.addMainForm(mainForm);
                
                nextHashCode = getHashCodeFromBytes(bytesFile);
                while (nextHashCode != CONTROLVALUE) {
                    morfCharacteristics = getMorfCharacteristicsFromBytes(bytesFile);
                    jMorfSdk.addWordForm(createWordForm(nextHashCode, morfCharacteristics, mainForm));
                    nextHashCode = getHashCodeFromBytes(bytesFile);
                }
                
                pointer += 4;
            }

        } catch (IOException ex) {
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            bytesFile = null;
        }
    }

    private int getHashCodeFromBytes(byte[] bytesFile) {
        return (int) getValueCodeFromBytes(bytesFile, 4);
    }
    
    private long getValueCodeFromBytes(byte[] bytesFile, int countByte) {
        int hashCode = 0;
        for (int i = 0; i < countByte; i++) {
            int f = 0xFF & bytesFile[pointer + countByte - i - 1];
            int g1 = f << (8 * i);
            hashCode |= g1;
        }
        pointer += countByte;
        return hashCode;
    }

    private byte getTypeOfSpeechFromBytes(byte[] bytesFile) {
        byte typeOfSpeech = bytesFile[pointer];
        pointer++;
        return typeOfSpeech;
    }

    private long getMorfCharacteristicsFromBytes(byte[] bytesFile) {
        return getValueCodeFromBytes(bytesFile, 8);
    }

    private WordForm createWordForm(int hashCode, long morfCharacteristics, MainForm mainForm) {
        return new WordForm(hashCode, morfCharacteristics, mainForm);
    }

    private void closeStream(FileInputStream inHashCodeAndMorfCharacteristics) {
        try {
            inHashCodeAndMorfCharacteristics.close();
        } catch (IOException ex) {
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            inHashCodeAndMorfCharacteristics = null;
        }
    }

    public static void loadProperty() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("property.xml");
            Node root = document.getDocumentElement();
            readProperty(root);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            String messages = "Не удается найти property.xml\r\nПрименены параметры по умолчанию!\r\n";
            Logger.getLogger(LoadBasedOnHashCode.class.getName()).log(Level.WARNING, messages);
        }
    }

    public static void readProperty(Node root) {

        NodeList propertys = root.getChildNodes();
        for (int i = 0; i < propertys.getLength(); i++) {
            Node node = propertys.item(i);
            if (node.getNodeType() != Node.TEXT_NODE) {
                switch (node.getNodeName()) {
                    case "pathLibrary":
//                        pathLibrary = node.getChildNodes().item(0).getTextContent();
                        break;
                    case "encoding":
                        encoding = node.getChildNodes().item(0).getTextContent();
                        break;
                    default:
                        readProperty(node);
                }
            }
        }
    }
}
