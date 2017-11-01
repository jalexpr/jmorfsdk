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
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

public class OmoForms extends ArrayList<Form> {

    public OmoForms(Form wf) {
        super();
        add(wf);
    }

    public OmoForms(OmoForms source) {
        super(source);
    }

    public String getStringOmoform() {
        try {
            return getForm().getStringForm();
        } catch (Exception e) {
            return "";
        }
    }

    private Form getForm() throws Exception{
        try {
            return get(0);
        } catch (IndexOutOfBoundsException e) {
            Logger.getLogger("jmorfsdk.Omoform").log(Level.WARNING, "Омоформа не имеет значений");
            throw new Exception();
        }
    }

    public boolean addForm(Form wordform) {
        return add(wordform);
    }

    @Override
    public int hashCode() {
        String stringOmoform = getStringOmoform();
        if(stringOmoform != null) {
            return getStringOmoform().hashCode();
        } else {
            try {
                return getForm().getHashCode();
            } catch (Exception ex) {
                Logger.getLogger("jmorfsdk.Omoform").log(Level.WARNING, "", ex);
                return 0;
            }
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
        final OmoForms other = (OmoForms) obj;
        String stringOmoform = getStringOmoform();
        if(stringOmoform != null) {
            return Objects.equals(this.getStringOmoform(), other.getStringOmoform());
        } else {
            return Objects.equals(this.hashCode(), other.hashCode());
        }
    }
}
