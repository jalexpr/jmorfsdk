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
package jmorfsdk;

import java.util.ArrayList;
import jmorfsdk.form.InitialForm;
import jmorfsdk.form.OmoForms;
import jmorfsdk.form.WordForm;
import java.util.HashMap;
import jmorfsdk.form.Form;

public final class JMorfSdk implements JMorfSdkAccessInterface {

    //!NB InitialForm в omoForms НЕ ДУБЛИРУЮТСЯ!!!!
    private HashMap<Integer, OmoForms> omoForms = new HashMap();
    private HashMap<Integer, InitialForm> initialForms = new HashMap();

    public void addInitialForm(InitialForm mf) {
        initialForms.put(mf.hashCode(), mf);
    }

    public void addWordForm(String strWordForm, WordForm wordForm) {
        addWordForm(strWordForm.hashCode(), wordForm);
    }

    public void addWordForm(int hashCode, WordForm wordForm) {
        if (isOmoFormExistForForm(hashCode)) {
            getOmoFormByForm(hashCode).addForm(wordForm);
        } else {
            addOmoForm(new OmoForms(wordForm, hashCode));
        }
    }

    private boolean isOmoFormExistForForm(int hashCode) {
        return omoForms.containsKey(hashCode);
    }

    private OmoForms getOmoFormByForm(int hashCode) {
        return omoForms.get(hashCode);
    }

    private void addOmoForm(OmoForms of) {
        omoForms.put(of.hashCode(), of);
    }

    public void finish() {
        omoForms.clear();
        initialForms.clear();
        omoForms = null;
        initialForms = null;
    }

    public void trimToSize() {
        initialForms.forEach((key, value) -> {
            value.trimToSize();
        });

        omoForms.forEach((key, value) -> {
            value.trimToSize();
        });
    }

    @Override
    public boolean isFormExistsInDictionary(String strForm) {
        boolean isFormExists = omoForms.containsKey(strForm.hashCode())
                || initialForms.containsKey(strForm.hashCode());
        return isFormExists;
    }

    @Override
    public boolean isInitialForm(String strForm) {
        return initialForms.containsKey(strForm.hashCode());
    }

    @Override
    public ArrayList<Byte> getTypeOfSpeechs(String strForm) {

        ArrayList<Byte> typeOfSpeechsList = new ArrayList<>();
        ArrayList<Form> formList = getListFormByHachCode(strForm.hashCode());

        for (Form form : formList) {
            typeOfSpeechsList.add(form.getTypeOfSpeech());
        }
        return typeOfSpeechsList;
    }

    private ArrayList<Form> getListFormByHachCode(int hashCode) {

        ArrayList<Form> formList;

        if (omoForms.containsKey(hashCode)) {
            formList = omoForms.get(hashCode);
        } else {
            formList = new ArrayList<>();
        }

        if (initialForms.containsKey(hashCode)) {
            formList.add(initialForms.get(hashCode));
        }

        return formList;
    }

    @Override
    public ArrayList<Long> getMorfologyCharacteristics(String strForm) {

        ArrayList<Long> morfologyCharacteristics = new ArrayList<>();
        ArrayList<Form> formList = getListFormByHachCode(strForm.hashCode());

        for (Form form : formList) {
            morfologyCharacteristics.add(form.getMorfCharacteristics());
        }
        return morfologyCharacteristics;
    }

    @Override
    public ArrayList<String> getFormInInitialForm(String strForm) {

        ArrayList<String> stringFormList = new ArrayList<>();
        ArrayList<Form> formList = getListFormByHachCode(strForm.hashCode());

        for (Form form : formList) {
            stringFormList.add(form.getStringInitialForm());
        }

        return stringFormList;
    }

    @Override
    public ArrayList<AllCharacteristicsOfForm> getAllCharacteristicsOfForm(String strForm) {

        ArrayList<AllCharacteristicsOfForm> list = new ArrayList<>();
        ArrayList<Form> formList = getListFormByHachCode(strForm.hashCode());
        AllCharacteristicsOfForm characteristicsOfForm;

        for (Form form : formList) {
            characteristicsOfForm = new AllCharacteristicsOfForm(form.getStringInitialForm(),
                                                                    form.getTypeOfSpeech(),
                                                                    form.getMorfCharacteristics());
            list.add(characteristicsOfForm);
        }

        return list;
    }

    public void addStringForm(String stringForm) {
        omoForms.get(stringForm.hashCode()).addStringForm(stringForm);
    }
}
