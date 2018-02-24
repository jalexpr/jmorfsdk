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
 * Благодарим Сергея и Екатерину Полицыных за оказание помощи в разработке библиотеки.
 */
package jmorfsdk;

import jmorfsdk.form.InitialForm;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import jmorfsdk.form.Form;
import jmorfsdk.form.NumberForm;
import grammeme.MorfologyParametersHelper;
import java.util.Map;

import load.BDFormString;
import morphologicalstructures.NumberOmoForm;
import morphologicalstructures.OmoForm;
import storagestructures.OmoFormList;

public final class JMorfSdk implements JMorfSdkAccessInterface {

    private Map<Integer, List<Form>> omoForms = new ConcurrentHashMap();

    private JMorfSdk() {}

    public static JMorfSdk getEmptyJMorfSdk() {
        return new JMorfSdk();
    }

    public void addForm(int hashCode, Form form) {
        try {
            getListFormByHachCode(hashCode).add(form);
        } catch (Exception ex) {
            addNewOmoForm(hashCode, form);
        }
    }

    private void addNewOmoForm(int hashCode, Form form) {
        LinkedList<Form> omoForm = new LinkedList<>();
        omoForm.add(form);
        omoForms.put(hashCode, omoForm);
    }

    public void finish() {
        omoForms.clear();
        omoForms = null;
    }

    @Override
    public boolean isFormExistsInDictionary(String strForm) {
        return omoForms.containsKey(strForm.hashCode());
    }

    @Override
    public byte isInitialForm(String strForm) throws Exception {
        boolean isContainsInitialForm = false;
        boolean isContainsNotInitialForm = false;
        for (Form form : getListFormByString(strForm)) {
            if (form.isInitialForm()) {
                isContainsInitialForm = true;
            } else {
                isContainsNotInitialForm = true;
            }
        }

        if (isContainsInitialForm && isContainsNotInitialForm) {
            return 0;
        } else if (isContainsInitialForm) {
            return 1;
        } else if (isContainsNotInitialForm) {
            return -1;
        } else {
            return -2;
        }
    }

    @Override
    public LinkedList<Byte> getTypeOfSpeechs(String strForm) throws Exception {

        LinkedList<Byte> typeOfSpeechsList = new LinkedList<>();

        for (Form form : getListFormByString(strForm)) {
            typeOfSpeechsList.add(form.getTypeOfSpeech());
        }
        return typeOfSpeechsList;
    }

    private LinkedList<Form> getListFormByString(String strForm) throws Exception {
        try {
            return getListFormByHachCode(strForm.hashCode());
        } catch (Exception ex) {
            if(strForm.matches("[0-9]+")) {
                LinkedList listNumber = new LinkedList<>();
                listNumber.add(new NumberForm(strForm));
                return listNumber;
            }
            throw new Exception(String.format("%s Слово: %s", ex.getMessage(), strForm));
        }
    }

    private LinkedList<Form> getListFormByHachCode(int hashCode) throws Exception {

        LinkedList<Form> formList;

        if (omoForms.containsKey(hashCode)) {
            formList = (LinkedList<Form>) omoForms.get(hashCode);
        } else {
            throw new Exception("Подходящие слово не было найдено в словаре библиотеки!");
        }

        return formList;
    }

    @Override
    public LinkedList<Long> getMorfologyCharacteristics(String strForm) throws Exception {

        LinkedList<Long> morfologyCharacteristics = new LinkedList<>();

        for (Form form : getListFormByString(strForm)) {
            morfologyCharacteristics.add(form.getMorfCharacteristics());
        }
        return morfologyCharacteristics;
    }

    @Override
    public LinkedList<String> getStringInitialForm(String strForm) throws Exception {

        LinkedList<String> stringFormList = new LinkedList<>();

        for (Form form : getListFormByString(strForm)) {
            stringFormList.add(form.getInitialFormString());
        }

        return stringFormList;
    }

    @Override
    public OmoFormList getAllCharacteristicsOfForm(String strForm) throws Exception {

        OmoFormList list = new OmoFormList();

        for (Form form : getListFormByString(strForm)) {
            OmoForm characteristicsOfForm;
            try {
                characteristicsOfForm = new OmoForm(form.getInitialFormKey(), form.getMyFormKey(),
                    form.getTypeOfSpeech(),
                    form.getMorfCharacteristics());
            } catch (UnsupportedOperationException ex) {
                characteristicsOfForm = new NumberOmoForm(form.getInitialFormString());
            }
            list.add(characteristicsOfForm);
        }

        return list;
    }

    @Override
    public List<String> getDerivativeForm(String stringInitialForm, long morfCharacteristics) throws Exception {
        try {
            return selectByMorfCharacteristics(getInitialFormList(stringInitialForm), morfCharacteristics);
        } catch (Exception ex) {
            throw new Exception(String.format("В словаре отсутствует производное слов, слова: %s с характеристиками: %s", stringInitialForm, morfCharacteristics));
        }
    }

    private List<String> selectByMorfCharacteristics(List<InitialForm> initialFormList, long morfCharacteristics) throws Exception {

        long mask = getMask(morfCharacteristics);

        List<String> listWordString = new LinkedList<>();

        initialFormList.forEach((initialForm) -> {
            initialForm.getWordFormList().forEach((wordForm) -> {
                if ((wordForm.getMorfCharacteristics() & mask) == morfCharacteristics) {
                    listWordString.add(BDFormString.getStringById(wordForm.getMyFormKey(), false));
                }
            });
        });

        if (listWordString.isEmpty()) {
            throw new Exception(String.format("В словаре отсутствует производное слов с характеристиками: %s", morfCharacteristics));
        }

        return listWordString;
    }

    private long getMask(long morfCharacteristics) {

        long mask = 0;

        for (long identifier : MorfologyParametersHelper.getIdentifiers()) {
            if ((morfCharacteristics & identifier) != 0) {
                mask |= identifier;
            }
        }

        return mask;
    }

    @Override
    public List<String> getDerivativeForm(String stringInitialForm, byte typeOfSpeech, long morfCharacteristics) throws Exception {

        List<InitialForm> initialFormList = selectInitialFormByTypeOfSpeech(getInitialFormList(stringInitialForm), typeOfSpeech);

        try {
            return selectByMorfCharacteristics(initialFormList, morfCharacteristics);
        } catch (Exception ex) {
            throw new Exception(String.format("В словаре отсутствует производные слова, слова: %s с характеристиками: %s", stringInitialForm, morfCharacteristics));
        }
    }

    private List<InitialForm> selectInitialFormByTypeOfSpeech(List<InitialForm> initialFormList, byte typeOfSpeech) {

        for (InitialForm form : initialFormList) {
            if (form.getTypeOfSpeech() != typeOfSpeech) {
                initialFormList.remove(form);
            }
        }

        return initialFormList;
    }

    private List<InitialForm> getInitialFormList(String stringInitialForm) throws Exception {

        List<InitialForm> initialForms = new LinkedList<>();

        for (Form form : getListFormByString(stringInitialForm)) {
            if (form instanceof InitialForm) {
                initialForms.add((InitialForm) form);
            }
        }

        if (initialForms == null) {
            throw new Exception(String.format("String = %s Данный текст не является начальной формой слова!", stringInitialForm));
        }

        return initialForms;
    }

    @Override
    public List<String> getDerivativeForm(String stringInitialForm, byte typeOfSpeech) throws Exception {

        List<String> wordStringList = new LinkedList<>();

        getInitialFormList(stringInitialForm).forEach((initialForm) -> {
            initialForm.getWordFormList().forEach((wordForm) -> {
                if (wordForm.getTypeOfSpeech() == typeOfSpeech) {
                    wordStringList.add(BDFormString.getStringById(wordForm.getMyFormKey(), false));
                }
            });
        });

        if (wordStringList.isEmpty()) {
            throw new Exception(String.format("В словаре отсутствует производное слов, слова: %s с частью речи: %s", stringInitialForm, typeOfSpeech));
        }

        return wordStringList;
    }
}
