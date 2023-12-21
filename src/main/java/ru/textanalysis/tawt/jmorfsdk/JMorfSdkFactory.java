package ru.textanalysis.tawt.jmorfsdk;

import lombok.extern.slf4j.Slf4j;

/**
 * Load JMorfSdk with parameters or by default.
 * Lazy initialization once.
 */
@Slf4j
public class JMorfSdkFactory {

	private static final boolean IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT = true;
	private static final boolean IS_LOAD_INFO_TO_PREDICT_CHARACTERISTICS = false;

	private static JMorfSdk jMorfSdk = null;

	private JMorfSdkFactory() {
	}

	/**
	 * load JMorfSdk
	 *
	 * @return JMorfSdk
	 */
	public static JMorfSdk loadFullLibrary() {
		return loadFullLibrary(IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT, IS_LOAD_INFO_TO_PREDICT_CHARACTERISTICS);
	}

	public static JMorfSdk loadFullLibrary(boolean isOutputMessagesToConsole) {
		return loadJMorfSdk(isOutputMessagesToConsole, IS_LOAD_INFO_TO_PREDICT_CHARACTERISTICS);
	}

	/**
	 * load JMorfSdk
	 *
	 * @param isOutputMessagesToConsole - is output messages to console
	 *
	 * @return JMorfSdk
	 */
	public static JMorfSdk loadFullLibrary(boolean isOutputMessagesToConsole, boolean isLoadInfoToPredictCharacteristics) {
		return loadJMorfSdk(isOutputMessagesToConsole, isLoadInfoToPredictCharacteristics);
	}

	//todo
	/**
	 * load JMorfSdk
	 *
	 * @param pathZipFile - path to dictionary
	 *
	 * @return JMorfSdk
	 */
//	public static JMorfSdk loadFullLibrary(String pathZipFile) {
//		return loadFullLibrary(IS_OUTPUT_MESSAGES_TO_CONSOLE_DEFAULT);
//	}

	//todo

	/**
	 * load JMorfSdk
	 * <p>
	 * //	 * @param pathZipFile               - path to dictionary
	 *
	 * @param isOutputMessagesToConsole - is output messages to console
	 *
	 * @return JMorfSdk
	 */
//	public static JMorfSdk loadFullLibrary(String pathZipFile, boolean isOutputMessagesToConsole) {
//		return loadJMorfSdk(pathZipFile, isOutputMessagesToConsole);
//	}
	private synchronized static JMorfSdk loadJMorfSdk(/*String pathZipFile,*/ boolean isOutputMessagesToConsole, boolean isLoadInfoToPredictCharacteristics) {
		if (jMorfSdk == null) {
			LoaderFromFileAndBD loaderFromFileAndBD = new LoaderFromFileAndBD();
			try {
				outputMessagesToConsole("Старт загрузки библиотеки", isOutputMessagesToConsole);
				jMorfSdk = loaderFromFileAndBD.load(isLoadInfoToPredictCharacteristics);
				System.gc();
				Runtime.getRuntime().gc();
				outputMessagesToConsole("Библиотека готова к работе.", isOutputMessagesToConsole);
				return jMorfSdk;
			} catch (Exception ex) {
				log.warn(ex.getMessage(), ex);
				return loaderFromFileAndBD.getEmptyJMorfSdk();
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
