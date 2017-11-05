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
package jmorfsdk.form;

import java.util.ArrayList;

public final class InitialForm extends Form {

    private final ArrayList<WordForm> wordFormList = new ArrayList<>();
    private final byte typeOfSpeech;
    private String strInitialForm;

    public InitialForm(String strWord){
        this(strWord, Byte.valueOf("0"), 0L);
    }

    public InitialForm(String strWordform, byte typeOfSpeech, long morfCharacteristics) {
        super(morfCharacteristics);
        this.typeOfSpeech = typeOfSpeech;
        this.strInitialForm = strWordform;
    }

    public void addWordfFormList(WordForm wordform) {
        wordFormList.add(wordform);
    }

    @Override
    public byte getTypeOfSpeech() {
        return typeOfSpeech;
    }

    public long morfCharacteristics() {
        return getMorfCharacteristics();
    }

    public ArrayList<WordForm> getWordFormList() throws Exception {
        throw new Exception("Не реализовано!");
    }

    public void trimToSize() {
        wordFormList.trimToSize();
    }

    public String getStrInitialForm() {
        return strInitialForm;
    }

    @Override
    public int hashCode() {
        return getStrInitialForm().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InitialForm other = (InitialForm) obj;
        return this.hashCode() == other.hashCode();
    }
}
