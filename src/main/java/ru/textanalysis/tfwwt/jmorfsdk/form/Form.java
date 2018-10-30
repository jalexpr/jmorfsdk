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
package ru.textanalysis.tfwwt.jmorfsdk.form;

import ru.textanalysis.tfwwt.morphological.structures.load.BDFormString;

import static ru.textanalysis.tfwwt.morphological.structures.load.LoadHelper.getControlHashCode;
import static ru.textanalysis.tfwwt.morphological.structures.load.LoadHelper.getControlValue;

public abstract class Form {

    public static int formCount = 0;
    private final long morfCharacteristics;
    private final int formKeyInBD;

    public Form(long morfCharacteristics, int formKey) {
        this.morfCharacteristics = morfCharacteristics;
        this.formKeyInBD = formKey;
        formCount++;
    }

    public long getMorfCharacteristics() {
        return morfCharacteristics;
    }

    public int getMyFormKey() {
        return formKeyInBD;
    }

    public abstract byte getTypeOfSpeech();

    public abstract String getInitialFormString();

    public abstract int getInitialFormKey();

    public abstract boolean isInitialForm();

    public String getMyString() {
        return BDFormString.getStringById(getMyFormKey(), false);
    }

    public boolean isFormSameByControlHash(String string) {
        return getMyControlValue() == getControlHashCode(string);
    }

    private int getMyControlValue() {
        return getControlValue(getMyFormKey());
    }

    public abstract Form getInitialForm();

}
