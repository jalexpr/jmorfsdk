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
package jmorfsdk.load;

import java.util.logging.Level;
import java.util.logging.Logger;
import jmorfsdk.JMorfSdk;

import static morphological.structures.internal.Property.PATH_ZIP_DICTIONARY;

public abstract class JMorfSdkLoad {

    private static final boolean IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT = true;

    public static JMorfSdk loadFullLibrary() {
        return loadFullLibrary(PATH_ZIP_DICTIONARY, IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT);
    }

    public static JMorfSdk loadFullLibrary(boolean isOutputMessagesToConsole) {
        return loadFullLibrary(PATH_ZIP_DICTIONARY, isOutputMessagesToConsole);
    }

    public static JMorfSdk loadFullLibrary(String pathZipFile) {
        return loadFullLibrary(pathZipFile, IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT);
    }

    public static JMorfSdk loadFullLibrary(String pathZipFile, boolean isOutputMessagesToConsole) {
        return loadJMorfSdk(pathZipFile, isOutputMessagesToConsole, true);
    }

    @Deprecated
    public static JMorfSdk loadInGenerationMode() {
        return loadInGenerationMode(IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT);
    }

    @Deprecated
    public static JMorfSdk loadInGenerationMode(boolean isOutputMessagesToConsole) {
        return loadFullLibrary(isOutputMessagesToConsole);
    }

    public static JMorfSdk loadInAnalysisMode() {
        return loadInAnalysisMode(PATH_ZIP_DICTIONARY, IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT);
    }

    public static JMorfSdk loadInAnalysisMode(boolean isOutputMessagesToConsole) {
        return loadInAnalysisMode(PATH_ZIP_DICTIONARY, isOutputMessagesToConsole);
    }

    public static JMorfSdk loadInAnalysisMode(String pathZipFile) {
        return loadInAnalysisMode(pathZipFile, IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT);
    }


    public static JMorfSdk loadInAnalysisMode(String pathZipFile, boolean isOutputMessagesToConsole) {
        return loadJMorfSdk(pathZipFile, isOutputMessagesToConsole, false);
    }

    private static JMorfSdk loadJMorfSdk(String pathZipFile, boolean isOutputMessagesToConsole, boolean isLoadGenerationMode) {
        JMorfSdk jMorfSdk;
        try {
            outputMessagesToConsole("Старт загрузки библиотеки", isOutputMessagesToConsole);
            jMorfSdk = LoadFromFileAndBD.loadInAnalysisMode(pathZipFile, isLoadGenerationMode);
            System.gc();
            Runtime.getRuntime().gc();
            outputMessagesToConsole("Библиотека готова к работе.", isOutputMessagesToConsole);
            return jMorfSdk;
        } catch (Exception ex) {
            Logger.getLogger(JMorfSdkLoad.class.getName()).log(Level.WARNING, null, ex);
            return JMorfSdk.getEmptyJMorfSdk();
        }
    }

    private static void outputMessagesToConsole(String messages, boolean isOutputMessagesToConsole) {
        if (isOutputMessagesToConsole) {
            System.out.println(messages);
        }
    }
}
