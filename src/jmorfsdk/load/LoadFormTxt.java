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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jmorfsdk.JMorfSdk;
import jmorfsdk.form.InitialForm;
import jmorfsdk.form.WordForm;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Deprecated
public class LoadFormTxt implements LoadFromFile {

    private BufferedReader buffInput;
    private JMorfSdk jMorfSdk;
    private static String pathLibrary = "dictionary.format.number.txt";
    private static String encoding = "Windows-1251";

    static {
        loadProperty();
    }

    @Override
    public JMorfSdk loadLibraryForSearchInitialForm() throws Exception {
        return loadFullLibrary();
    }

    @Override
    public JMorfSdk loadLibraryForSearchForFormByMorphologicalCharacteristics() throws Exception {
        return loadFullLibrary();
    }

    @Override
    public JMorfSdk loadFullLibrary() throws Exception {
        jMorfSdk = new JMorfSdk();
        buffInput = openBufferedReaderStreamFromFile();
        loadFromFileStringAndMorfCharacteristic();
        closeBufferedReader();
        JMorfSdk outJMorfSdk = jMorfSdk;
        jMorfSdk = null;
        return outJMorfSdk;
    }

    private BufferedReader openBufferedReaderStreamFromFile() {

        buffInput = null;
        try {
            buffInput = new BufferedReader(new InputStreamReader(new FileInputStream(pathLibrary), encoding));
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s, в случае отсуствия скачайте с репозитория %s\r\n", pathLibrary, MYREPOSITORY);
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, messages);
        } catch (UnsupportedEncodingException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\n1)Проверте кодировку %s в соотвевствии с параметрами в property.xml.\r\n2)При отсутствии property.xml кодировка по умолчанию %s\r\n3)В случае отсуствия файлов, скачайте с репозитория %s\r\n",
                    pathLibrary, encoding, MYREPOSITORY);
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, messages);
        }

        return buffInput;
    }


    private void loadFromFileStringAndMorfCharacteristic() {

        try {
            int count = 0;
            buffInput.readLine();
            while (buffInput.ready() && count < 1) {
                addLemma(buffInput.readLine());
            }
        } catch (IOException ex) {
            String messages = String.format("Проблема чтения с файла: %s\r\nСкачайте файл с репозитория %s", pathLibrary, MYREPOSITORY);
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, messages);
        }
    }

    private void addLemma(String strLemma) {
        InitialForm initialForm = createInitialForm(strLemma);
        jMorfSdk.addInitialForm(initialForm);
        addWordForms(strLemma, initialForm);
    }

    private InitialForm createInitialForm(String strForms) {
        String initialWordForm;
        if (strForms.contains("\"")) {
            initialWordForm = strForms.substring(0, strForms.indexOf("\""));
        } else {
            initialWordForm = strForms;
        }
        String[] initialWordParameters = initialWordForm.split(" ");
        return new InitialForm (initialWordParameters[0], Byte.decode("0x" + initialWordParameters[1]),
                new BigInteger (initialWordParameters[2], 16).longValue());
    }

    private void addWordForms(String strLemma, InitialForm initialForm) {

        String[] arrayWordForms = strLemma.split("\"");

        for (int i = 1; i < arrayWordForms.length; i++) {
            String[] initialWordParam = arrayWordForms[i].split(" ");
            jMorfSdk.addWordForm(initialWordParam[0], createWordForm(arrayWordForms[1], initialForm));
        }
    }

    private WordForm createWordForm(String strMorfCharacteristics, InitialForm initialForm) {
        return new WordForm(new BigInteger(strMorfCharacteristics, 16).longValue(), initialForm);
    }

    private void closeBufferedReader() {
        try {
            buffInput.close();
        } catch (IOException ex) {
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void loadProperty() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("property.xml");
            Node root = document.getDocumentElement();
            readProperty(root);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            String messages = "Не удается найти property.xml\r\nПрименены параметры по умолчанию!\r\n";
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.WARNING, messages);
        }
    }

    private static void readProperty(Node root) {

        NodeList propertys = root.getChildNodes();
        for (int i = 0; i < propertys.getLength(); i++) {
            Node node = propertys.item(i);
            if (node.getNodeType() != Node.TEXT_NODE) {
                switch (node.getNodeName()) {
                    case "pathLibrary":
                        pathLibrary = node.getChildNodes().item(0).getTextContent();
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
