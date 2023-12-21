package ru.textanalysis.tawt.jmorfsdk;

import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.jmorfsdk.predicter.JMorfSdkPredicter;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;
import ru.textanalysis.tawt.ms.loader.LoadHelper;
import ru.textanalysis.tawt.ms.model.jmorfsdk.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.textanalysis.tawt.ms.loader.LoadHelper.getHashCode;

@Slf4j
final class JMorfSdkImpl implements JMorfSdk {

	private Map<Integer, List<Form>> allForms = new ConcurrentHashMap<>();
	private final JMorfSdkPredicter jMorfSdkPredicter = new JMorfSdkPredicter(allForms);

	JMorfSdkImpl() {
	}

	void addForm(int hashCode, Form form) {
		allForms.computeIfAbsent(hashCode, i -> new ArrayList<>()).add(form);
	}

	void addPrefix(String prefix, List<PrefixMorfCharacteristicsChanges> characteristicsChanges) {
		jMorfSdkPredicter.addPrefix(prefix, characteristicsChanges);
	}

	void addPostfix(String postfix, List<PostfixMorfCharacteristics> postfixMorfCharacteristics) {
		jMorfSdkPredicter.addPostfix(postfix, postfixMorfCharacteristics);
	}

	void setPredictSetting(boolean isLoadInfoToPredictCharacteristics) {
		jMorfSdkPredicter.setPredictSetting(isLoadInfoToPredictCharacteristics);
	}

	@Override
	public void finish() {
		allForms.clear();
		allForms = null;
		jMorfSdkPredicter.finish();
	}

	@Override
	public boolean containsTrie(String word) {
		return jMorfSdkPredicter.containsTrie(word);
	}

	@Override
	public String findLongestWordPrefix(String word) {
		return jMorfSdkPredicter.findLongestWordPrefix(word);
	}

	@Override
	public String findLongestWordPostfix(String word) {
		return jMorfSdkPredicter.findLongestWordPostfix(word);
	}

	@Override
	public List<String> findAllWordPrefixes(String word) {
		return jMorfSdkPredicter.findAllWordPrefixes(word);
	}

	@Override
	public List<String> findAllWordPostfixes(String word) {
		return jMorfSdkPredicter.findAllWordPostfixes(word);
	}

	@Override
	public boolean isFormExistsInDictionary(String literal) {
		if (isNumber(literal)) {
			return true;
		}
		int hashCode = LoadHelper.getHashCode(literal);
		return allForms.containsKey(hashCode);
	}

	@Override
	public boolean isPostfixExistsInDictionary(String literal) {
		return jMorfSdkPredicter.isPostfixExistsInDictionary(literal);
	}

	public List<PostfixMorfCharacteristics> getPostfixInfoByPostfix(String literal) {
		return jMorfSdkPredicter.getPostfixInfoByPostfix(literal);
	}

	@Override
	public byte isInitialForm(String literal) {
		boolean isContainsInitialForm = false;
		boolean isContainsNotInitialForm = false;
		for (Form form : getFormsByString(literal)) {
			if (form.isInitialForm()) {
				isContainsInitialForm = true;
			} else {
				isContainsNotInitialForm = true;
			}
		}

		if (isContainsInitialForm && isContainsNotInitialForm) {
			return 0;
		} else if (isContainsInitialForm) {
			return 1;
		} else if (isContainsNotInitialForm) {
			return -1;
		} else {
			return -2;
		}
	}

	@Override
	public List<Byte> getTypeOfSpeeches(String literal) {
		return getFormsByString(literal).stream()
			.map(Form::getTypeOfSpeech)
			.collect(Collectors.toList());
	}

	private List<Form> getFormsByString(String literal) {
		if (isNumber(literal)) {
			return List.of(new NumberForm(literal));
		} else {
			return createListFormByString(literal);
		}
	}

	private boolean isNumber(String literal) {
		return literal.matches("[0-9]+[:.,-]?[0-9]*");
	}

	private List<Form> createListFormByString(String literal) {
		int hashCode = getHashCode(literal);
		String word = null;
		String wordPrefix = null;
		boolean isOutPrefixedFormFound = false;
		if (!allForms.containsKey(hashCode)) {
			if (jMorfSdkPredicter.getPredictSetting()) {
				if (literal.contains("-")) {
					int serviceWordPart = jMorfSdkPredicter.checkComplicatedWordForServiceParts(literal);
					String[] complicatedWordParts = literal.split("-");
					if (serviceWordPart != -1) {
						List<Form> wordWithServicePartForms = jMorfSdkPredicter.getFormsOfWordWithServicePart(literal, serviceWordPart, complicatedWordParts);
						if (!wordWithServicePartForms.isEmpty()) {
							return wordWithServicePartForms;
						}
					} else {
						List<Form> wordWithIndependentPartsForms = jMorfSdkPredicter.getFormsOfWordWithIndependentParts(literal, complicatedWordParts);
						if (!wordWithIndependentPartsForms.isEmpty()) {
							return wordWithIndependentPartsForms;
						}
					}
					return List.of(new UnfamiliarForm(literal));
				} else {
					List<String> wordPrefixes = findAllWordPrefixes(literal);
					for (String prefix : wordPrefixes) {
						word = literal.replaceFirst(Pattern.quote(prefix), "");
						if (isFormExistsInDictionary(word)) {
							wordPrefix = prefix;
							isOutPrefixedFormFound = true;
							hashCode = getHashCode(word);
							break;
						}
					}
				}
			} else {
				return List.of(new UnfamiliarForm(literal));
			}
		}
		String finalWord = word == null ? literal : word;
		if (allForms.containsKey(hashCode)) {
			List<Form> resultForms = allForms.get(hashCode).stream()
				.filter(form -> form.isFormSameByControlHash(finalWord))
				.collect(Collectors.toList());
			for (int i = resultForms.size() - 1; i >= 0; i--) {
				if (resultForms.get(i).getTypeOfSpeech() == 0) {
					long link = resultForms.get(i).getLink();
					long linkHashCode = link >> 32;
					resultForms.addAll(allForms.get((int) linkHashCode).stream()
						.filter(form -> form.getMyFormKey() == (int) link)
						.collect(Collectors.toList()));
					resultForms.remove(i);
				}
			}
			if (isOutPrefixedFormFound) {
				return jMorfSdkPredicter.getWordFormsWithOutPrefixedAnalysis(literal, wordPrefix, resultForms);
			}
			return resultForms;
		} else {
			return jMorfSdkPredicter.getWordFormsWithPostfixAnalysis(literal);
		}
	}

	@Override
	public List<Long> getMorphologyCharacteristics(String literal) {
		return getFormsByString(literal).stream()
			.map(Form::getMorphCharacteristics)
			.collect(Collectors.toList());
	}

	@Override
	public List<String> getStringInitialForm(String literal) {
		return getFormsByString(literal).stream()
			.map(Form::getInitialFormString)
			.collect(Collectors.toList());
	}

	@Override
	public List<String> getDerivativeFormLiterals(String initialFormLiteral, long morfCharacteristics) {
		List<InitialForm> initialForms = selectOnlyInitialFormsByString(initialFormLiteral);

		List<String> derivativeFormLiterals = selectByMorfCharacteristics(initialForms, morfCharacteristics);
		if (derivativeFormLiterals.isEmpty()) {
			log.debug("В словаре отсутствует производные слова, слова: {} с характеристиками: {}", initialFormLiteral, morfCharacteristics);
		}
		return derivativeFormLiterals;
	}

	@Override
	public List<String> getDerivativeFormLiterals(String initialFormLiteral, byte typeOfSpeech, long morfCharacteristics) {
		List<InitialForm> initialForms = selectOnlyInitialFormsByString(initialFormLiteral).stream()
			.filter(form -> form.getTypeOfSpeech() == typeOfSpeech)
			.collect(Collectors.toList());

		List<String> derivativeFormLiterals = selectByMorfCharacteristics(initialForms, morfCharacteristics);
		if (derivativeFormLiterals.isEmpty()) {
			log.debug("В словаре отсутствует производные слова, слова: {} с характеристиками: {}", initialFormLiteral, morfCharacteristics);
		}
		return derivativeFormLiterals;
	}

	@Override
	public List<String> getDerivativeFormLiterals(String literalInitialForm, byte typeOfSpeech) {
		List<String> literals = selectOnlyInitialFormsByString(literalInitialForm).stream()
			.filter(form -> form.getTypeOfSpeech() == typeOfSpeech)
			.map(InitialForm::getDerivativeForms)
			.flatMap(Collection::stream)
			.map(Form::getMyString)
			.collect(Collectors.toList());
		if (literals.isEmpty()) {
			log.debug("В словаре отсутствует производное слов, слова: {} с частью речи: {}", literalInitialForm, typeOfSpeech);
		}
		return literals;
	}

	private List<String> selectByMorfCharacteristics(List<InitialForm> initialForms, long morfCharacteristics) {
		long mask = getMask(morfCharacteristics);
		List<String> literals = initialForms.stream()
			.map(InitialForm::getDerivativeForms)
			.flatMap(Collection::stream)
			.filter(derivative -> (derivative.getMorphCharacteristics() & mask) == morfCharacteristics)
			.map(Form::getMyString)
			.collect(Collectors.toList());
		if (literals.isEmpty()) {
			log.debug("В словаре отсутствует производное слов с характеристиками: {}", morfCharacteristics);
		}
		return literals;
	}

	private long getMask(long morfCharacteristics) {
		long mask = 0;
		for (long identifier : MorfologyParametersHelper.getIdentifiers()) {
			if ((morfCharacteristics & identifier) != 0) {
				mask |= identifier;
			}
		}
		return mask;
	}

	private List<InitialForm> selectOnlyInitialFormsByString(String literalInitialFrom) {
		List<InitialForm> forms = getFormsByString(literalInitialFrom).stream()
			.filter(form -> form instanceof InitialForm)
			.map(form -> (InitialForm) form)
			.collect(Collectors.toList());
		if (forms.isEmpty()) {
			log.debug("В словаре начальные формы для литерала: {}", literalInitialFrom);
		}
		return forms;
	}

	@Override
	public List<Form> getOmoForms(String literal) {
		return new ArrayList<>(getFormsByString(literal.toLowerCase(Locale.ROOT)));
	}
}
