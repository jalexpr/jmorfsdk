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
package ru.textanalysis.tawt.jmorfsdk;

import ru.textanalysis.tawt.ms.model.jmorfsdk.Form;

import java.util.List;

public interface JMorfSdk {

	boolean isFormExistsInDictionary(String literal);

	boolean isFormExistsInDictionary(byte[] literal);

	boolean isPostfixExistsInDictionary(String literal);

	/**
	 * @param literal - String формы
	 *
	 * @return 0 - если форма может быть и в начальной, и в не начальной формой,
	 * 1 - форма является начальной, -1 - форма является не начальной, -2 -
	 * такого слова нет
	 */
	byte isInitialForm(String literal);

	boolean containsTrie(String word);

	String findLongestWordPrefix(String word);

	String findLongestWordPostfix(String word);

	List<String> findAllWordPrefixes(String word);

	List<String> findAllWordPostfixes(String word);

	List<Byte> getTypeOfSpeeches(String literal);

	List<Long> getMorphologyCharacteristics(String literal);

	List<String> getStringInitialForm(String literal);

	List<String> getDerivativeFormLiterals(String stringInitialForm, byte typeOfSpeech);

	List<String> getDerivativeFormLiterals(String stringInitialForm, long morfCharacteristics);

	List<String> getDerivativeFormLiterals(String stringInitialForm, byte typeOfSpeech, long morfCharacteristics);

	List<Form> getOmoForms(String literal);

	void finish();
}
