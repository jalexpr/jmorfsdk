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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import jmorfsdk.BDSqlite;
import jmorfsdk.JMorfSdk;
import jmorfsdk.form.InitialForm;
import jmorfsdk.form.WordForm;

public final class LoadFromFileAndBD implements Load {

    private static final int CONTROLVALUE = -1;//new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    @Override
    public JMorfSdk loadFullLibrary() {
        return loadLibraryForSearchInitialForm(true);
    }

    @Override
    public JMorfSdk loadLibraryForSearchInitialForm() {
        return loadLibraryForSearchInitialForm(false);
    }

    public JMorfSdk loadLibraryForSearchInitialForm(boolean isLoadGenerationdMode) {
        ZipInputStream streamHashAndMorfCharacteristic = null;
        Scanner scannerInitialFormString = null;
        try {
            streamHashAndMorfCharacteristic = FileOpen.openZipFile(Property.pathZipDictionary, Property.pathHashAndMorfCharacteristics);
            scannerInitialFormString = FileOpen.openScannerFromZipFile(Property.pathZipDictionary, Property.pathInitialFormString, Property.encoding);
            return loadJMorfSdk(streamHashAndMorfCharacteristic, scannerInitialFormString, Property.pathBD, isLoadGenerationdMode);
        } catch (Exception ex) {
            Logger.getLogger(LoadFromFileAndBD.class.getName()).log(Level.SEVERE, null, ex);
            return new JMorfSdk();
        } finally {
            FileOpen.closeFile(streamHashAndMorfCharacteristic);
            FileOpen.closeFile(scannerInitialFormString);
        }
    }

    private JMorfSdk loadJMorfSdk(InputStream inputStreamHashAndMorfCharacteristics, Scanner scannerInitialFormString, String nameBD, boolean isLoadFormInInitialForm) {

        JMorfSdk jMorfSdk = new JMorfSdk();
        HashMap<Integer, String> initialFormString = loadInitialFormString(scannerInitialFormString);
        try (BufferedInputStream inputStream = new BufferedInputStream(inputStreamHashAndMorfCharacteristics)) {
            while (inputStream.available() > 0) {
                InitialForm initialForm = createInitialForm(inputStream, initialFormString);
                jMorfSdk.addInitialForm(initialForm);
                addWordForm(jMorfSdk, initialForm, inputStream, isLoadFormInInitialForm);
            }
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(LoadFromFileAndBD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            //Конец Файл, ничего не делаем
        } finally {
            initialFormString.clear();
            initialFormString = null;
        }

        if (isLoadFormInInitialForm) {
            jMorfSdk.addBD(new BDSqlite(nameBD));
        }

        return jMorfSdk;
    }

    private InitialForm createInitialForm(BufferedInputStream inputStream, HashMap<Integer, String> initialFormString) throws IOException, Exception {
        int nextHashCode = getHashCodeFromBytes(inputStream);
        if(nextHashCode == -1) {
            throw new Exception("Конец файла");
        }
        String formString = initialFormString.get(nextHashCode);
        if(formString == null) {
            throw new IOException(String.format("nextHashCode = %d;",nextHashCode));
        }
        InitialForm initialForm = new InitialForm(initialFormString.get(nextHashCode),
                getTypeOfSpeechFromBytes(inputStream),
                getMorfCharacteristicsFromBytes(inputStream));
        return initialForm;
    }

    private void addWordForm(JMorfSdk jMorfSdk, InitialForm initialForm, InputStream inputStream, boolean isLoadFormInInitialForm) {
        int nextHashCode = getHashCodeFromBytes(inputStream);
        while (nextHashCode != CONTROLVALUE) {
            WordForm wordForm = new WordForm(getMorfCharacteristicsFromBytes(inputStream), getIdForm(inputStream), initialForm);
            jMorfSdk.addForm(nextHashCode, wordForm);
            if (isLoadFormInInitialForm) {
                initialForm.addWordfFormInList(wordForm);
            }
            nextHashCode = getHashCodeFromBytes(inputStream);
        }
        initialForm.trimToSize();
    }

    private HashMap<Integer, String> loadInitialFormString(Scanner readerFormHashAndString) {
        HashMap<Integer, String> initialForm = new HashMap<>();
        while (readerFormHashAndString.hasNext()) {
            String formString = readerFormHashAndString.nextLine();
            initialForm.put(formString.hashCode(), formString);
        }

        return initialForm;
    }

    private int getIdForm(InputStream inputStream) {
        return (int) getValueCodeFromBytes(inputStream, 4);
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
            Logger.getLogger(LoadFromFileAndBD.class.getName()).log(Level.SEVERE, "Не ожиданное окончание файла, проверте целостность файлов!", ex);
        }

        return hashCode;
    }

    @Override
    public JMorfSdk loadLibraryForSearchForFormByMorphologicalCharacteristics() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
