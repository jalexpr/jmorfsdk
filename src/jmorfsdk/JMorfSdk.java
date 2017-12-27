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

import load.BDInitialFormString;
import jmorfsdk.form.InitialForm;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmorfsdk.form.Form;
import jmorfsdk.form.WordForm;
import jmorfsdk.load.LoadFromFileAndBD;
import morphologicalstructures.OmoForm;
import storagestructures.OmoFormList;

public final class JMorfSdk implements JMorfSdkAccessInterface {

    //!NB InitialForm в omoForms НЕ ДУБЛИРУЮТСЯ!!!!
    private HashMap<Integer, LinkedList<Form>> omoForms = new HashMap();

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
    public LinkedList<Byte> getTypeOfSpeechs(String strForm) {

        LinkedList<Byte> typeOfSpeechsList = new LinkedList<>();
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
    public LinkedList<Long> getMorfologyCharacteristics(String strForm) {

        LinkedList<Long> morfologyCharacteristics = new LinkedList<>();
        LinkedList<Form> formList = getListFormByHachCode(strForm.hashCode());

        for (Form form : formList) {
            morfologyCharacteristics.add(form.getMorfCharacteristics());
        }
        return morfologyCharacteristics;
    }

    @Override
    public LinkedList<String> getStringInitialForm(String strForm) {

        LinkedList<String> stringFormList = new LinkedList<>();
        LinkedList<Form> formList = getListFormByHachCode(strForm.hashCode());

        for (Form form : formList) {
            stringFormList.add(form.getInitialFormString());
        }

        return stringFormList;
    }

    @Override
    public OmoFormList getAllCharacteristicsOfForm(String strForm) {

        OmoFormList list = new OmoFormList();
        List<Form> formList = getListFormByHachCode(strForm.hashCode());
        OmoForm characteristicsOfForm;

        for (Form form : formList) {
            characteristicsOfForm = new OmoForm(form.getInitialFormKey(),
                                                                    form.getTypeOfSpeech(),
                                                                    form.getMorfCharacteristics());
            list.add(characteristicsOfForm);
        }

        return list;
    }

    @Override
    public String getDerivativeForm(String initialFormString, long morfCharacteristics) throws Exception {

        InitialForm initialForm = null;
        List<Form> listForm = omoForms.get(initialFormString.hashCode());
        if(listForm == null) {
            Logger.getLogger(LoadFromFileAndBD.class.getName()).log(Level.SEVERE, String.format("String = %s Данный текст не найден в словаре!", initialFormString));
            throw new Exception();
        }

        for(Form form : listForm) {
            if(form instanceof InitialForm) {
                initialForm = (InitialForm) form;
                break;
            }
        }
        listForm = null;

        if(initialForm == null) {
            Logger.getLogger(LoadFromFileAndBD.class.getName()).log(Level.SEVERE, String.format("String = %s Данный текст не является начальной формой слова!", initialFormString));
            throw new Exception();
        }

        for(WordForm wordForm : initialForm.getWordFormList()) {
            if (((wordForm.getMorfCharacteristics() ^ morfCharacteristics) & morfCharacteristics) == 0) {
                System.out.println("Подходящая форма String = " + BDInitialFormString.getStringById(wordForm.getMyId(), false));
            }
        }

        return null;
    }
}
