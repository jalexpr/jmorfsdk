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
package ru.textanalysis.tawt.jmorfsdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.textanalysis.tawt.jmorfsdk.exceptions.JMorfSdkRuntimeException;
import ru.textanalysis.tawt.jmorfsdk.form.InitialForm;
import ru.textanalysis.tawt.jmorfsdk.form.NumberForm;
import ru.textanalysis.tawt.jmorfsdk.form.UnfamiliarForm;
import ru.textanalysis.tawt.jmorfsdk.loader.JMorfSdkFactory;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;
import ru.textanalysis.tawt.ms.interfaces.jmorfsdk.IJMorfSdk;
import ru.textanalysis.tawt.ms.internal.NumberOmoForm;
import ru.textanalysis.tawt.ms.internal.OmoForm;
import ru.textanalysis.tawt.ms.internal.UnfamiliarOmoForm;
import ru.textanalysis.tawt.ms.internal.form.Form;
import ru.textanalysis.tawt.ms.internal.form.GetCharacteristics;
import ru.textanalysis.tawt.ms.internal.ref.RefOmoFormList;
import ru.textanalysis.tawt.ms.loader.BDFormString;
import ru.textanalysis.tawt.ms.loader.LoadHelper;
import ru.textanalysis.tawt.ms.storage.OmoFormList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.textanalysis.tawt.ms.loader.LoadHelper.getHashCode;

public final class JMorfSdk implements IJMorfSdk {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<Integer, List<Form>> omoForms = new ConcurrentHashMap();

    private JMorfSdk() {
    }

    public static JMorfSdk getEmptyJMorfSdk() {
        return new JMorfSdk();
    }

    public void addForm(int hashCode, Form form) {
        try {
            omoForms.get(hashCode).add(form);
        } catch (Exception ex) {
            addNewOmoForm(hashCode, form);
        }
    }

    private void addNewOmoForm(int hashCode, Form form) {
        List<Form> omoForm = new LinkedList<>();
        omoForm.add(form);
        omoForms.put(hashCode, omoForm);
    }

    public void finish() {
        omoForms.clear();
        omoForms = null;
    }

    @Override
    public boolean isFormExistsInDictionary(String strForm) {
        int hashCode = LoadHelper.getHashCode(strForm);
        return omoForms.containsKey(hashCode);
    }

    @Override
    public byte isInitialForm(String strForm) {
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
    public List<Byte> getTypeOfSpeeches(String strForm) {
        List<Byte> typeOfSpeechesList = new LinkedList<>();
        getListFormByString(strForm).forEach(form ->
                typeOfSpeechesList.add(form.getTypeOfSpeech())
        );
        return typeOfSpeechesList;
    }

    private List<Form> getListFormByString(String strForm) {
        try {
            if (isNumber(strForm)) {
                return getListNumber(strForm);
            } else {
                return createListFormByString(strForm);
            }
        } catch (Exception ex) {
            return new LinkedList<>();
        }
    }

    private boolean isNumber(String strForm) {
        return strForm.matches("[0-9]+");
    }

    private List<Form> getListNumber(String strForm) throws Exception {
        if (strForm.matches("[0-9]+")) {
            LinkedList<Form> listNumber = new LinkedList<>();
            listNumber.add(new NumberForm(strForm));
            return listNumber;
        } else {
            throw new Exception();
        }
    }

    private List<Form> createListFormByString(String strForm) throws Exception {
        int hashCode = getHashCode(strForm);
        List<Form> listForm = new LinkedList<>();
        if (omoForms.containsKey(hashCode)) {
            omoForms.get(hashCode).forEach((form) -> {
                if (form.isFormSameByControlHash(strForm)) {
                    listForm.add(form);
                }
            });
        } else {
            listForm.add(new UnfamiliarForm(strForm));
        }
        return listForm;
    }

    @Override
    public List<Long> getMorphologyCharacteristics(String strForm) {
        List<Long> morphologyCharacteristics = new LinkedList<>();
        getListFormByString(strForm).forEach(form ->
                morphologyCharacteristics.add(form.getMorphCharacteristics())
        );
        return morphologyCharacteristics;
    }

    @Override
    public List<String> getStringInitialForm(String strForm) {
        List<String> stringFormList = new LinkedList<>();
        getListFormByString(strForm).forEach(form ->
                stringFormList.add(form.getInitialFormString())
        );
        return stringFormList;
    }

    @Override
    public OmoFormList getAllCharacteristicsOfForm(String strForm) {
        OmoFormList list = new OmoFormList();
        getListFormByString(strForm).forEach(form -> {
            OmoForm characteristicsOfForm;
            switch (form.isTypeForm()) {
                case INITIAL:
                case WORD:
                    characteristicsOfForm = new OmoForm(
                            form.getInitialFormKey(),
                            form.getMyFormKey(),
                            form.getTypeOfSpeech(),
                            form.getMorphCharacteristics());
                    break;
                case NUMBER:
                    characteristicsOfForm = new NumberOmoForm(
                            form.getInitialFormString(),
                            -1,
                            -1,
                            -1);
                    break;
                case UNFAMILIAR:
                    characteristicsOfForm = new UnfamiliarOmoForm(
                            form.getInitialFormString(),
                            -1,
                            -1,
                            -1);
                    break;
                default:
                    log.warn("Cannot impl for {}", form.isTypeForm());
                    throw new JMorfSdkRuntimeException("Cannot impl for " + form.isTypeForm());
            }
            list.add(characteristicsOfForm);
        });
        return list;
    }

    @Override
    public List<String> getDerivativeForm(String stringInitialForm, long morfCharacteristics) throws Exception {
        try {
            return selectByMorfCharacteristics(selectOnlyInitialFormListByString(stringInitialForm), morfCharacteristics);
        } catch (Exception ex) {
            throw new Exception(String.format("В словаре отсутствует производное слов, слова: %s с характеристиками: %s", stringInitialForm, morfCharacteristics));
        }
    }

    private List<String> selectByMorfCharacteristics(List<InitialForm> initialFormList, long morfCharacteristics) throws Exception {
        long mask = getMask(morfCharacteristics);
        List<String> listWordString = new LinkedList<>();
        initialFormList.forEach((initialForm) -> {
            initialForm.getWordFormList().forEach((wordForm) -> {
                if ((wordForm.getMorphCharacteristics() & mask) == morfCharacteristics) {
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
        List<InitialForm> initialFormList = selectInitialFormByTypeOfSpeech(selectOnlyInitialFormListByString(stringInitialForm), typeOfSpeech);
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

    private List<InitialForm> selectOnlyInitialFormListByString(String stringInitialForm) {
        List<InitialForm> initialForms = new LinkedList<>();
        getListFormByString(stringInitialForm).forEach(form -> {
            if (form instanceof InitialForm) {
                initialForms.add((InitialForm) form);
            }
        });
        return initialForms;
    }

    @Override
    public List<String> getDerivativeForm(String stringInitialForm, byte typeOfSpeech) throws Exception {
        List<String> wordStringList = new LinkedList<>();
        selectOnlyInitialFormListByString(stringInitialForm).forEach((initialForm) -> {
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

    public static void createSmallDictionary(String nameDictionary, String[] words) {
        JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();
        List<GetCharacteristics> initialForm = getListInitialFormForListForm(getFormsByWords(jMorfSdk, words));
        //todo
    }

    private static List<GetCharacteristics> getListInitialFormForListForm(List<Form> forms) {
        List<GetCharacteristics> initialForms = new ArrayList<>();
        forms.forEach((form) -> {
            initialForms.add(form.getInitialForm());
        });
        return initialForms;
    }

    private static List<Form> getFormsByWords(JMorfSdk jMorfSdk, String[] words) {
        List<Form> forms = new ArrayList<>();
        for (String word : words) {
            forms.addAll(jMorfSdk.getListFormByString(word));
        }
        return forms;
    }

    @Override
    public RefOmoFormList getRefOmoFormList(String strForm) {
        return new RefOmoFormList(getListFormByString(strForm));
    }
}
