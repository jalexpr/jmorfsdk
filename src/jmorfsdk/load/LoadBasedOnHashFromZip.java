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
 * Thanks to Sergey Politsyn and Katherine Politsyn for their help in the development of the library.
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
 * Если нет, см. <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 * Благодарим Полицыных Сергея и Екатерину за оказание помощи в разработке библиотеки.
 */
package jmorfsdk.load;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import jmorfsdk.JMorfSdk;
import jmorfsdk.form.InitialForm;
import jmorfsdk.form.WordForm;

public final class LoadBasedOnHashFromZip implements LoadFromFile {

    private int sizeFileHashAndMorfCharacteristics = 0;
    private static final int CONTROLVALUE = -1;//new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    @Override
    public JMorfSdk loadFullLibrary() {
        return loadLibraryForSearchInitialForm(true);
    }

    @Override
    public JMorfSdk loadLibraryForSearchInitialForm() {
        return loadLibraryForSearchInitialForm(false);
    }

    public JMorfSdk loadLibraryForSearchInitialForm(boolean isLoadFormInInitialForm) {
        ZipInputStream streamHashCodeAndMorfCharacteristic = null;
        Scanner scannerInitialFormString = null;
        try {
            streamHashCodeAndMorfCharacteristic = openZipFile(Property.pathZipDictionary, Property.pathHashAndMorfCharacteristics);
            scannerInitialFormString = openScannerFromZipFile(Property.pathZipDictionary, Property.pathInitialFormString, Property.encoding);
            return loadHashAndString(streamHashCodeAndMorfCharacteristic, scannerInitialFormString, isLoadFormInInitialForm);
        } catch (Exception ex) {
            Logger.getLogger(LoadBasedOnHashFromZip.class.getName()).log(Level.SEVERE, null, ex);
            return new JMorfSdk();
        } finally {
            closeStream(streamHashCodeAndMorfCharacteristic);
            closeScanner(scannerInitialFormString);
        }
    }

    private ZipInputStream openZipFile(String zipPath, String nameLibrary) throws IOException {
        ZipInputStream zin = new ZipInputStream(new FileInputStream(new File(zipPath)));
        for (ZipEntry e; (e = zin.getNextEntry()) != null;) {
            if (e.getName().equals(nameLibrary)) {
                if (sizeFileHashAndMorfCharacteristics == 0) {
                    sizeFileHashAndMorfCharacteristics = (int) e.getSize();
                }
                return zin;
            }
        }
        throw new EOFException("Cannot find " + nameLibrary);
    }

    private  Scanner openScannerFromZipFile(String pathZip, String pathLibrary, String encoding) throws Exception {
        try {
            return new Scanner(openZipFile(pathZip, pathLibrary), encoding);
        } catch (FileNotFoundException ex) {
            String messages = String.format("Ошибка при чтении файла.\r\nПроверте наличие %s, в случае отсуствия скачайте с репозитория %s\r\n", pathLibrary, MYREPOSITORY);
            Logger.getLogger(LoadBasedOnHashFromZip.class.getName()).log(Level.SEVERE, messages);
            throw new Exception();
        }
    }

    private JMorfSdk loadHashAndString(InputStream inputStreamHashAndMorfCharacteristics, Scanner scannerInitialFormString, boolean isLoadFormInInitialForm) {

        JMorfSdk jMorfSdk = new JMorfSdk();
        HashMap<Integer, String> initialFormString = loadInitialFormString(scannerInitialFormString);

        try (BufferedInputStream inputStream = new BufferedInputStream(inputStreamHashAndMorfCharacteristics)) {
//            bytesFile = sun.misc.IOUtils.readFully(inputStreamHashCodeAndMorfCharacteristics, sizeFileHashAndMorfCharacteristics, true);
//            int i;
//            for(i = 0; i < sizeFileHashAndMorfCharacteristics; i++){
//                inputStreamHashCodeAndMorfCharacteristics.read();
//            }
            int nextHashCode = 0;
            while (inputStream.available() > 0) {

                nextHashCode = getHashCodeFromBytes(inputStream);
                InitialForm initialForm = new InitialForm(initialFormString.get(nextHashCode),
                        getTypeOfSpeechFromBytes(inputStream),
                        getMorfCharacteristicsFromBytes(inputStream));
                jMorfSdk.addInitialForm(initialForm);

                nextHashCode = getHashCodeFromBytes(inputStream);
                while (nextHashCode != CONTROLVALUE) {
                    if (isLoadFormInInitialForm) {
                        WordForm wordForm = new WordForm(getMorfCharacteristicsFromBytes(inputStream), initialForm);
                        initialForm.addWordfFormInList(wordForm);
                        jMorfSdk.addForm(nextHashCode, wordForm);
                    } else {
                        jMorfSdk.addForm(nextHashCode, new WordForm(getMorfCharacteristicsFromBytes(inputStream), initialForm));
                    }
                    nextHashCode = getHashCodeFromBytes(inputStream);
                }
                initialForm.trimToSize();
            }
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(LoadBasedOnHashFromZip.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            initialFormString.clear();
            initialFormString = null;
        }
        return jMorfSdk;
    }

    private HashMap<Integer, String> loadInitialFormString(Scanner readerFormHashAndString) {
        HashMap<Integer, String> initialForm = new HashMap<>();
        while (readerFormHashAndString.hasNext()) {
            String formString = readerFormHashAndString.nextLine();
            initialForm.put(formString.hashCode(), formString);
        }

        return initialForm;
    }

    private int getHashCodeFromBytes(InputStream inputStream) {
        return (int) getValueCodeFromBytes(inputStream, 4);
    }

    private byte getTypeOfSpeechFromBytes(InputStream inputStream) throws IOException {
        byte typeOfSpeech = (byte) inputStream.read();
        return typeOfSpeech;
    }

    private long getMorfCharacteristicsFromBytes(InputStream inputStream) {
        return getValueCodeFromBytes(inputStream, 8);
    }

    private long getValueCodeFromBytes(InputStream inputStream, int countByte) {
        int hashCode = 0;
        try {
            for (int i = 0; i < countByte; i++) {
                int f = 0xFF & inputStream.read();
                int g1 = f << (8 * (countByte - 1 - i));
                hashCode |= g1;
            }
        } catch (IOException ex) {
            Logger.getLogger(LoadBasedOnHashFromZip.class.getName()).log(Level.SEVERE, "Не ожиданное окончание файла, проверте целостность файлов!", ex);
        }

        return hashCode;
    }

    private void closeStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(LoadBasedOnHashFromZip.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            inputStream = null;
        }
    }

    private void closeScanner(Scanner scanner) {
        scanner.close();
        scanner = null;
    }

    @Override
    public JMorfSdk loadLibraryForSearchForFormByMorphologicalCharacteristics() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
