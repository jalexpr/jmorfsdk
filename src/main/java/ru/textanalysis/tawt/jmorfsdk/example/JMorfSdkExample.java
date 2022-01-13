package ru.textanalysis.tawt.jmorfsdk.example;

import ru.textanalysis.tawt.jmorfsdk.JMorfSdk;
import ru.textanalysis.tawt.jmorfsdk.JMorfSdkFactory;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters.*;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;
import ru.textanalysis.tawt.ms.loader.DatabaseFactory;
import ru.textanalysis.tawt.ms.loader.DatabaseStrings;
import ru.textanalysis.tawt.ms.model.jmorfsdk.Form;

import java.util.List;

public class JMorfSdkExample {

	public static void main(String[] args) throws Exception {
		//Пример загрузки библиотеки
		JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();
//        BDInitialFormString.printAll(true);

//        System.err.println("");
//        {
//            Long start = System.currentTimeMillis();
//            for (long i = 0; i < 900_000; i++) {
//                jMorfSdk.getAllCharcteristicsOfForm("стол");
//            }
//            long finish = System.currentTimeMillis();
//            System.out.println(finish - start);
//        }
//        {
//            Long start = System.currentTimeMillis();
//            for(long i = 0; i < 900_000;i++) {
//                jMorfSdk.getDerivativeForm("село", TypeOfSpeech.NOUN);
//            }
//            long finish = System.currentTimeMillis();
//            System.out.println(finish - start);
//        }
		//Пример получения характеристик заданной формы

		jMorfSdk.getOmoForms("мама").forEach(System.out::println);

		jMorfSdk.getOmoForms("мыла").forEach((form) -> {
			//Пример поиска формы в родительном падеже
			if (form.getMorfCharacteristicsByIdentifier(Case.IDENTIFIER) == Case.GENITIVE) {
				System.out.println("Форма в родительном падеже " + form);
			}

			//Пример поиска формы с частью речи глагол
			if (form.getTypeOfSpeech() == TypeOfSpeech.VERB) {
				System.out.println("Форма с глаголом найдена " + form);
			}
		});

		jMorfSdk.getOmoForms("замок").forEach(System.out::println);

		jMorfSdk.getOmoForms("по").forEach(System.out::println);

		//Пример проверки слов в словаре
		if (jMorfSdk.isFormExistsInDictionary("че")) {
			System.out.println("Слово \"че\" найдено");
		} else {
			System.out.println("Слово \"че\" не найдено");
		}

		if (jMorfSdk.isFormExistsInDictionary("чтотакое")) {
			System.out.println("Слово \"чтотакое\" найдено");
		} else {
			System.out.println("Слово \"чтотакое\" не найдено");
		}

//        try{
//            List<IOmoForm> characteristics3 = jMorfSdk.getOmoForms("чтотакое");
//                characteristics3.forEach((form) -> {
//                System.out.println(form);
//            });
//        } catch (Exception ex) {
//            Logger.getLogger(Running.class.getName()).log(Level.SEVERE, null, ex);
//        }

		jMorfSdk.getOmoForms("мыл").forEach(System.out::println);

		List<Form> omoFormMama = jMorfSdk.getOmoForms("мыла");
		System.out.println("печать из встроенной структуры");
		System.out.println(omoFormMama.get(0).getMyString());
		System.out.println(omoFormMama.get(0).getTypeForm());
		System.out.println(omoFormMama.get(0).getInitialFormString());

		List<Form> omoFormMilo = jMorfSdk.getOmoForms("мылo");
		System.out.println("печать из встроенной структуры");
		System.out.println(omoFormMilo.get(0).getMyString());
		System.out.println(omoFormMilo.get(0).getTypeForm());
		System.out.println(omoFormMilo.get(0).getInitialFormString());

		List<Form> omoFormMilO = jMorfSdk.getOmoForms("мыло");
		System.out.println("печать из встроенной структуры");
		System.out.println(omoFormMilO.get(0).getMyFormKey());
		System.out.println(omoFormMilO.get(0).getTypeForm());
		System.out.println(omoFormMilO.get(0).getInitialFormString());

		int formKey = omoFormMama.get(0).getMyFormKey();
		int formInitialFormKey = omoFormMama.get(0).getInitialFormKey();

		System.out.println("печать из встроенного класса для работы с БД");
		DatabaseStrings databaseStrings = DatabaseFactory.getInstanceDatabaseStrings();
		System.out.println(databaseStrings.getLiteralById(formKey));
		System.out.println(databaseStrings.getLiteralById(formInitialFormKey));

		System.out.println("_____");

		Form omoForm;
		long morfCharacteristic;

		omoForm = jMorfSdk.getOmoForms("выпит").get(0);
		System.out.println("получение конкретной характеристики по идентификатору");
		morfCharacteristic = omoForm.getMorfCharacteristicsByIdentifier(Voice.IDENTIFIER);
		System.out.println(MorfologyParametersHelper.getParametersName(morfCharacteristic));

		System.out.println("получение конкретной характеристики по классу");
		morfCharacteristic = omoForm.getMorfCharacteristicsByIdentifier(Voice.class);
		System.out.println(MorfologyParametersHelper.getParametersName(morfCharacteristic));

		System.out.println("_____");

		jMorfSdk.getOmoForms("дорогой").forEach((form) -> {
			if (form.isContainsTypeOfSpeech(TypeOfSpeech.NOUN)) {
				System.out.println(form);
			}
			if (form.isContainsMorphCharacteristic(Case.class, Case.GENITIVE)) {
				System.out.println(form);
			}
		});

		System.out.println(Time.PAST);

		System.out.println("_____");

		for (String s : jMorfSdk.getDerivativeFormLiterals("мыло", TypeOfSpeech.NOUN)) {
			System.out.println(s);
		}

		System.out.println("_____");

		jMorfSdk.getDerivativeFormLiterals("мыло", TypeOfSpeech.NOUN, Numbers.SINGULAR).forEach(System.out::println);

		jMorfSdk.getOmoForms("что-то").forEach(System.out::println);

		jMorfSdk.getOmoForms("123").forEach(System.out::println);

		jMorfSdk.getOmoForms("что-нибудь").forEach(System.out::println);

		jMorfSdk.getOmoForms("подает").forEach(System.out::println);

		jMorfSdk.getOmoForms("424132").forEach(System.out::println);

		System.out.println("________________________________________");
		jMorfSdk.getOmoForms("мама").forEach((form) -> System.out.println(form.getInitialFormString()));

		jMorfSdk.finish();
	}
}
