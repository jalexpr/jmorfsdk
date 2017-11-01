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

import java.util.Objects;

public class Form {

    private static long amountForm = 0;
    private String strWordform;
    private int hashCode;
    private final long morfCharacteristics;

    public Form(String strWordform, long morfCharacteristics) {
        Form.amountForm++;
        this.strWordform = strWordform;
        this.morfCharacteristics = morfCharacteristics;
    }

    public Form(int hashCode, long morfCharacteristics) {
        Form.amountForm++;
        this.hashCode = hashCode;
        this.morfCharacteristics = morfCharacteristics;
    }

    public String getStringForm() {
        return new String(strWordform);
    }

    public int getHashCode() {
        return hashCode;
    }

    @Override
    public int hashCode() {
        if(strWordform != null) {
            return strWordform.hashCode();
        } else {
            return hashCode;
        }
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
        final Form other = (Form) obj;
        if(strWordform != null) {
            return Objects.equals(this.strWordform, other.strWordform);
        } else {
            return Objects.equals(this.hashCode, other.hashCode);
        }
    }

    public long getMorfCharacteristic(long morfIdentifier) {
        return morfCharacteristics & morfIdentifier;
    }

    public long getMorfCharacteristic() {
        return morfCharacteristics;
    }
}
