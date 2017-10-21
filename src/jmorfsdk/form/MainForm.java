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

/**
 * Начальная словоформа слова.
 *
 * @author AlexP
 */
public class MainForm extends Form {

    private final ArrayList<Form> wordFormList = new ArrayList<>();
    private final byte typeOfSpeech;

    //если нужно вернуть пустую структуру
    public MainForm(String strWord){
        this(strWord, Byte.valueOf("0"), 0L);
    }

    public MainForm(String strForm, String typeOfSpeech, String morfCharacteristics) {
        this(strForm, Byte.valueOf(typeOfSpeech, 16), Long.getLong(morfCharacteristics, 0x0));
    }

    public MainForm(String strForm, byte typeOfSpeech, long morfCharacteristics) {
        super(strForm.toLowerCase(), morfCharacteristics);
        this.typeOfSpeech = typeOfSpeech;
    }

    public void addWordfFormList(Form wordform) {
        wordFormList.add(wordform);
    }

    public byte getTypeOfSpeech() {
        return typeOfSpeech;
    }

    public ArrayList<Form> getWordFormMap() {
        return wordFormList;
    }
}
