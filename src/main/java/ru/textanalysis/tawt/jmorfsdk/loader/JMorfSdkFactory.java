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
package ru.textanalysis.tawt.jmorfsdk.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.textanalysis.tawt.jmorfsdk.JMorfSdk;

import static ru.textanalysis.tawt.ms.internal.Property.PATH_ZIP_DICTIONARY;

/**
 * Load JMorfSdk with parameters or by default.
 * Lazy initialization once.
 */
public abstract class JMorfSdkFactory {
    private static Logger log = LoggerFactory.getLogger(JMorfSdkFactory.class);

    private static JMorfSdk jMorfSdk = null;
    private static final boolean IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT = true;

    /**
     * load JMorfSdk
     * @return JMorfSdk
     */
    public static JMorfSdk loadFullLibrary() {
        return loadFullLibrary(PATH_ZIP_DICTIONARY, IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT);
    }

    /**
     * load JMorfSdk
     * @param isOutputMessagesToConsole - is output messages to console
     * @return JMorfSdk
     */
    public static JMorfSdk loadFullLibrary(boolean isOutputMessagesToConsole) {
        return loadFullLibrary(PATH_ZIP_DICTIONARY, isOutputMessagesToConsole);
    }

    /**
     * load JMorfSdk
     * @param pathZipFile - path to dictionary
     * @return JMorfSdk
     */
    public static JMorfSdk loadFullLibrary(String pathZipFile) {
        return loadFullLibrary(pathZipFile, IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT);
    }

    /**
     * load JMorfSdk
     * @param pathZipFile - path to dictionary
     * @param isOutputMessagesToConsole - is output messages to console
     * @return JMorfSdk
     */
    public static JMorfSdk loadFullLibrary(String pathZipFile, boolean isOutputMessagesToConsole) {
        return loadJMorfSdk(pathZipFile, isOutputMessagesToConsole);
    }

    private synchronized static JMorfSdk loadJMorfSdk(String pathZipFile, boolean isOutputMessagesToConsole) {
        if (jMorfSdk == null) {
            try {
                outputMessagesToConsole("Старт загрузки библиотеки", isOutputMessagesToConsole);
                jMorfSdk = LoaderFromFileAndBD.loadInAnalysisMode(pathZipFile);
                System.gc();
                Runtime.getRuntime().gc();
                outputMessagesToConsole("Библиотека готова к работе.", isOutputMessagesToConsole);
                return jMorfSdk;
            } catch (Exception ex) {
                log.warn(ex.getMessage(), ex);
                return JMorfSdk.getEmptyJMorfSdk();
            }
        }
        return jMorfSdk;
    }

    private static void outputMessagesToConsole(String messages, boolean isOutputMessagesToConsole) {
        if (isOutputMessagesToConsole) {
            System.out.println(messages);
        }
        log.info(messages);
    }
}
