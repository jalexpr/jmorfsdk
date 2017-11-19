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
package jmorfsdk;

import java.util.ArrayList;
import jmorfsdk.form.InitialForm;
import java.util.HashMap;
import java.util.LinkedList;
import jmorfsdk.form.Form;

public final class JMorfSdk implements JMorfSdkAccessInterface {

    //!NB InitialForm в omoForms НЕ ДУБЛИРУЮТСЯ!!!!
    private HashMap<Integer, LinkedList<Form>> omoForms = new HashMap();

    public void addInitialForm(InitialForm mf) {
        addForm(mf.hashCode(), mf);
    }

    public void addForm(int hashCode, Form form) {
        if (isOmoFormExistForForm(hashCode)) {
            getOmoFormByForm(hashCode).add(form);
        } else {
            addNewOmoForm(hashCode, form);
        }
    }

    private void addNewOmoForm(int hashCode,Form form) {
        LinkedList<Form> omoForm = new LinkedList<>();
        omoForm.add(form);
        omoForms.put(hashCode, omoForm);
    }

    private boolean isOmoFormExistForForm(int hashCode) {
        return omoForms.containsKey(hashCode);
    }

    private LinkedList<Form> getOmoFormByForm(int hashCode) {
        return omoForms.get(hashCode);
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
    public boolean isInitialForm(String strForm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Byte> getTypeOfSpeechs(String strForm) {

        ArrayList<Byte> typeOfSpeechsList = new ArrayList<>();
        LinkedList<Form> formList = getListFormByHachCode(strForm.hashCode());

        for (Form form : formList) {
            typeOfSpeechsList.add(form.getTypeOfSpeech());
        }
        return typeOfSpeechsList;
    }

    private LinkedList<Form> getListFormByHachCode(int hashCode) {

        LinkedList<Form> formList;

        if (omoForms.containsKey(hashCode)) {
            formList = omoForms.get(hashCode);
        } else {
            formList = new LinkedList<>();
        }

        return formList;
    }

    @Override
    public ArrayList<Long> getMorfologyCharacteristics(String strForm) {

        ArrayList<Long> morfologyCharacteristics = new ArrayList<>();
        LinkedList<Form> formList = getListFormByHachCode(strForm.hashCode());

        for (Form form : formList) {
            morfologyCharacteristics.add(form.getMorfCharacteristics());
        }
        return morfologyCharacteristics;
    }

    @Override
    public ArrayList<String> getStringInitialForm(String strForm) {

        ArrayList<String> stringFormList = new ArrayList<>();
        LinkedList<Form> formList = getListFormByHachCode(strForm.hashCode());

        for (Form form : formList) {
            stringFormList.add(form.getStringInitialForm());
        }

        return stringFormList;
    }

    @Override
    public ArrayList<AllCharacteristicsOfForm> getAllCharacteristicsOfForm(String strForm) {

        ArrayList<AllCharacteristicsOfForm> list = new ArrayList<>();
        LinkedList<Form> formList = getListFormByHachCode(strForm.hashCode());
        AllCharacteristicsOfForm characteristicsOfForm;

        for (Form form : formList) {
            characteristicsOfForm = new AllCharacteristicsOfForm(form.getStringInitialForm(),
                                                                    form.getTypeOfSpeech(),
                                                                    form.getMorfCharacteristics());
            list.add(characteristicsOfForm);
        }

        return list;
    }
}
