package ru.textanalysis.tawt.jmorfsdk.predicter;

import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters;
import ru.textanalysis.tawt.ms.model.jmorfsdk.*;
import ru.textanalysis.tawt.ms.model.jmorfsdk.command.PredictedFormCreateCommand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static ru.textanalysis.tawt.ms.loader.LoadHelper.getHashCode;

public class JMorfSdkPredicter {

	private final Map<Integer, List<Form>> allForms;
	private Map<String, List<PrefixMorfCharacteristicsChanges>> prefixes = new ConcurrentHashMap<>();
	private Map<String, List<PostfixMorfCharacteristics>> postfixes = new ConcurrentHashMap<>();
	private Trie postfixTrie = new PostfixTrie();
	private Trie prefixTrie = new PrefixTrie();
	private boolean isPredictEnabled = false;

	public JMorfSdkPredicter(Map<Integer, List<Form>> allForms) {
		this.allForms = allForms;
	}

	public void finish() {
		prefixes.clear();
		prefixes = null;
		postfixes.clear();
		postfixes = null;
		postfixTrie = null;
		prefixTrie = null;
	}

	public void addPrefix(String prefix, List<PrefixMorfCharacteristicsChanges> characteristicsChanges) {
		prefixes.put(prefix, characteristicsChanges);
		prefixTrie.insert(prefix);
	}

	public void addPostfix(String postfix, List<PostfixMorfCharacteristics> postfixMorfCharacteristics) {
		postfixes.put(postfix, postfixMorfCharacteristics);
		postfixTrie.insert(postfix);
	}

	public void setPredictSetting(boolean isLoadInfoToPredictCharacteristics) {
		isPredictEnabled = isLoadInfoToPredictCharacteristics;
	}

	public boolean getPredictSetting() {
		return isPredictEnabled;
	}

	public boolean containsTrie(String word) {
		return postfixTrie.contains(word);
	}

	public String findLongestWordPrefix(String word) {
		return prefixTrie.findLongest(word);
	}

	public String findLongestWordPostfix(String word) {
		return postfixTrie.findLongest(word);
	}

	public List<String> findAllWordPrefixes(String word) {
		return prefixTrie.findAll(word);
	}

	public List<String> findAllWordPostfixes(String word) {
		return postfixTrie.findAll(word);
	}

	public boolean isPostfixExistsInDictionary(String literal) {
		return postfixes.containsKey(literal);
	}

	public List<PostfixMorfCharacteristics> getPostfixInfoByPostfix(String literal) {
		return postfixes.getOrDefault(literal, null);
	}

	public List<Form> getWordFormsWithOutPrefixedAnalysis(String literal, String wordPrefix, List<Form> resultForms) {
		List<Form> predictedForms = new ArrayList<>();
		for (Form resultForm : resultForms) {
			InitialForm resultInitialForm = resultForm.getInitialForm();
			long formMorphCharacteristics = getFormMorphCharacteristics(wordPrefix, resultForm);
			predictedForms.add(buildPredictedDerivativeForm(PredictedFormCreateCommand.builder()
				.derivativeFormString(literal)
				.derivativeFormMorphCharacteristics(formMorphCharacteristics)
				.initialFormString(wordPrefix + resultInitialForm.getInitialFormString())
				.typeOfSpeech(resultForm.getTypeOfSpeech())
				.initialFormMorphCharacteristics(resultInitialForm.getMorphCharacteristics())
				.build())
			);
		}
		return predictedForms;
	}

	public List<Form> getWordFormsWithPostfixAnalysis(String literal) {
		List<Form> predictedForms = new ArrayList<>();
		String postfix = findLongestWordPostfix(literal);
		List<PostfixMorfCharacteristics> characteristics = postfixes.get(postfix);
		if (characteristics != null) {
			for (PostfixMorfCharacteristics characteristic : characteristics) {
				String wordPrefix = findLongestWordPrefix(literal);
				long formMorphCharacteristics = getFormMorphCharacteristics(wordPrefix, characteristic.getTypeOfSpeech(), characteristic.getPostfixFormTags());
				predictedForms.add(buildPredictedDerivativeForm(PredictedFormCreateCommand.builder()
					.derivativeFormString(literal)
					.derivativeFormMorphCharacteristics(formMorphCharacteristics)
					.initialFormString(literal.replaceFirst("(?s)(.*)" + wordPrefix, "$1" + characteristic.getInitialFormPostfix()))
					.typeOfSpeech(characteristic.getTypeOfSpeech())
					.initialFormMorphCharacteristics(characteristic.getInitialFormTags())
					.build())
				);
			}
			return predictedForms;
		} else {
			return List.of(new UnfamiliarForm(literal));
		}
	}

	public List<Form> getFormsOfWordWithIndependentParts(String literal, String[] complicatedWordParts) {
		if (!allForms.containsKey(getHashCode(complicatedWordParts[1]))) {
			return List.of(new UnfamiliarForm(literal));
		}
		List<Form> resultForms = allForms.get(getHashCode(complicatedWordParts[1])).stream()
			.filter(form -> form.isFormSameByControlHash(complicatedWordParts[1]))
			.collect(Collectors.toList());
		if (!resultForms.isEmpty()) {
			return createPredictedFormsForWordWithDash(literal, complicatedWordParts, resultForms);
		}
		return Collections.emptyList();
	}

	public List<Form> getFormsOfWordWithServicePart(String literal, String[] complicatedWordParts) {
		if (!allForms.containsKey(getHashCode(complicatedWordParts[1]))) {
			return List.of(new UnfamiliarForm(literal));
		}
		List<Form> resultForms = allForms.get(getHashCode(complicatedWordParts[1])).stream()
			.filter(form -> form.isFormSameByControlHash(complicatedWordParts[1]))
			.collect(Collectors.toList());
		if (!resultForms.isEmpty()) {
			for (Form resultForm : resultForms) {
				if (MorfologyParameters.TypeOfSpeech.ADJECTIVE_FULL == resultForm.getTypeOfSpeech() &&
					resultForm.isContainsMorphCharacteristic(MorfologyParameters.Case.class, MorfologyParameters.Case.DATIVE)) {
					return List.of(buildPredictedInitialForm(PredictedFormCreateCommand.builder()
						.initialFormString(literal)
						.typeOfSpeech(MorfologyParameters.TypeOfSpeech.ADVERB)
						.initialFormMorphCharacteristics(0L)
						.build())
					);
				}
			}
		}
		return List.of(new UnfamiliarForm(literal));
	}

	private List<Form> createPredictedFormsForWordWithDash(String literal, String[] complicatedWordParts, List<Form> resultForms) {
		List<Form> predictedForms = new ArrayList<>();
		for (Form resultForm : resultForms) {
			if (resultForm.isInitialForm()) {
				predictedForms.add(buildPredictedInitialForm(PredictedFormCreateCommand.builder()
					.initialFormString(literal)
					.typeOfSpeech(resultForm.getTypeOfSpeech())
					.initialFormMorphCharacteristics(resultForm.getInitialForm().getMorphCharacteristics())
					.build())
				);
			} else {
				String initialFormString = complicatedWordParts[0] + "-" + resultForm.getMyString();
				predictedForms.add(buildPredictedDerivativeForm(PredictedFormCreateCommand.builder()
					.derivativeFormString(literal)
					.derivativeFormMorphCharacteristics(resultForm.getMorphCharacteristics())
					.initialFormString(initialFormString)
					.typeOfSpeech(resultForm.getTypeOfSpeech())
					.initialFormMorphCharacteristics(resultForm.getInitialForm().getMorphCharacteristics())
					.build())
				);
			}
		}
		return predictedForms;
	}

	public boolean checkComplicatedWordForServiceParts(String literal) {
		String[] complicatedWordParts = literal.split("-");
		return complicatedWordParts[0].equals("по");
	}

	private Form buildPredictedDerivativeForm(PredictedFormCreateCommand command) {
		return PredictedDerivativeForm.builder()
			.word(command.getDerivativeFormString())
			.morfCharacteristics(command.getDerivativeFormMorphCharacteristics())
			.link(0L)
			.initialForm(new PredictedInitialForm(
				command.getInitialFormString(),
				command.getTypeOfSpeech(),
				command.getInitialFormMorphCharacteristics(),
				0L
			))
			.build();
	}

	private Form buildPredictedInitialForm(PredictedFormCreateCommand command) {
		return new PredictedInitialForm(
			command.getInitialFormString(),
			command.getTypeOfSpeech(),
			command.getInitialFormMorphCharacteristics(),
			0L
		);
	}

	private long getFormMorphCharacteristics(String wordPrefix, byte typeOfSpeech, long morphCharacteristics) {
		long formMorphCharacteristics = morphCharacteristics;
		if ((MorfologyParameters.TypeOfSpeech.VERB == typeOfSpeech ||
			MorfologyParameters.TypeOfSpeech.INFINITIVE == typeOfSpeech) && prefixes.containsKey(wordPrefix)) {
			PrefixMorfCharacteristicsChanges prefixCharacteristics = prefixes.get(wordPrefix).stream()
				.filter(prefix -> typeOfSpeech == prefix.getTypeOfSpeech())
				.findFirst().orElse(null);
			if (prefixCharacteristics != null) {
				formMorphCharacteristics = formMorphCharacteristics | prefixCharacteristics.getAddCharacteristics();
				List<Byte> bits = prefixCharacteristics.getDeleteCharacteristicsPositions();
				for (Byte bit : bits) {
					formMorphCharacteristics = formMorphCharacteristics & ~(1L << bit);
				}
			}
		}
		return formMorphCharacteristics;
	}

	private long getFormMorphCharacteristics(String wordPrefix, Form resultForm) {
		long formMorphCharacteristics = resultForm.getMorphCharacteristics();
		if (MorfologyParameters.TypeOfSpeech.VERB == resultForm.getTypeOfSpeech() ||
			MorfologyParameters.TypeOfSpeech.INFINITIVE == resultForm.getTypeOfSpeech()) {
			PrefixMorfCharacteristicsChanges prefixCharacteristics = prefixes.get(wordPrefix).stream()
				.filter(prefix -> resultForm.getTypeOfSpeech() == prefix.getTypeOfSpeech())
				.findFirst().orElse(null);
			if (prefixCharacteristics != null) {
				formMorphCharacteristics = formMorphCharacteristics | prefixCharacteristics.getAddCharacteristics();
				List<Byte> bytes = prefixCharacteristics.getDeleteCharacteristicsPositions();
				for (Byte aByte : bytes) {
					formMorphCharacteristics = formMorphCharacteristics & ~(1L << aByte);
				}
			}
		}
		return formMorphCharacteristics;
	}
}
