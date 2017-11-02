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
package jmorfsdk.load;

import java.util.logging.Level;
import java.util.logging.Logger;
import jmorfsdk.JMorfSdk;

public class LoadJMorfSdk {

    private static LoadFromFile loadFromFile = new LoadBasedOnHashCode();
    private static final boolean ISOUTPUTMESSAGESTOCONSOLEDEFAULT = true;

    public static JMorfSdk loadFullLibrary() {
        return loadFullLibrary(ISOUTPUTMESSAGESTOCONSOLEDEFAULT);
    }

    public static JMorfSdk loadFullLibrary(boolean isOutputMessagesToConsole) {
        JMorfSdk jMorfSdk;
        try {
            outputMessagesToConsole("Старт загрузки библиотеки", isOutputMessagesToConsole);
            jMorfSdk = loadFromFile.loadFullLibrary();
            outputMessagesToConsole("Библиотека готова к работе.", isOutputMessagesToConsole);
//            System.gc();
//            Runtime.getRuntime().gc();
            return jMorfSdk;
        } catch (Exception ex) {
            Logger.getLogger(LoadJMorfSdk.class.getName()).log(Level.WARNING, null, ex);
            return new JMorfSdk();
        }
    }

    private static void outputMessagesToConsole(String messages, boolean isOutputMessagesToConsole) {
        if (isOutputMessagesToConsole) {
            System.out.println(messages);
        }
    }

    public static void setLoadFromFile(LoadFromFile loadFromFile) {
        LoadJMorfSdk.loadFromFile = loadFromFile;
    }
}
