# JMorfSdk<br>
## Архитектура

<b>JMorfSdk</b> - главный класс, содержит <b>HashMap</b> словоформ (<b>OmoForm</b>). <br>
<br>
<b>Form</b> - форма слова содержит в себе набор характеристик, имеет набор характеристик, которые меняются в зависимости от словоформы (число, падеж и т.д.) <br>
<b>WordForm</b> - словоформа, наследуется от <b>Form</b>, имеет ссылку на <b>InitialForm</b>. <br>
<b>InitialForm</b> наследуется от <b>Form</b> - словоформа в начальной форме слова, имеет ссылки на все производные словоформы (<b>WordfForm</b>). <br>
<b>MorphologyParameters</b> - набор констант для морфологических характеристик. <br>
<br>


## Начало работы с библиотекой 

Загрузка библиотеки:
```
JMorfSdk jMorfSdk = JMorfSdkLoad.loadFullLibrary();
```
##### Пример получения словоформ:
```
List<OmoForm> characteristics5 = jMorfSdk.getAllCharacteristicsOfForm("мыл");
characteristics5.forEach((form) -> {
    System.out.println(form);
});
```
##### Пример получения форм слова с заданными морфологическими характеристками:
```
jMorfSdk.getAllCharacteristicsOfForm("дорогой").forEach((form) -> {
    //Пример поиска формы в родительном падеже
    if (form.getTheMorfCharacteristics(Case.IDENTIFIER) == Case.GENITIVE) {
        System.out.println("Форма в родительном падеже " + form);
    }
    
    if (form.getTypeOfSpeech() == TypeOfSpeech.VERB) {
        System.out.println("Форма с глаголом найдена " + form);
    }
});

jMorfSdk.getAllCharacteristicsOfForm("дорогой").forEach(form -> {
	if (form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.NOUN) {
		System.out.println(form);
	}
});

```
##### Вывод
```
initialFormString = дорогой, typeOfSpeech = 18, morfCharacteristics = 4264
initialFormString = дорога, typeOfSpeech = 17, morfCharacteristics = 363
```
##### Пример фильтрации слов по морфологической характеристике:
```
List<String> words = Arrays.asList("осенний", "осенней", "площадь", "стол", "играть", "конференций", "на", "бежала");
for (String word : words) {
    jMorfSdk.getAllCharacteristicsOfForm(word).forEach(form -> {
        if (form.getTheMorfCharacteristics(MorfologyParameters.Gender.class) == MorfologyParameters.Gender.FEMININ) {
            System.out.println(form + " - " + word);
        }
    });
}
```
##### Вывод
```
initialFormString = площадь, typeOfSpeech = 17, morfCharacteristics = 107 - площадь
initialFormString = площадь, typeOfSpeech = 17, morfCharacteristics = 555 - площадь
initialFormString = стол, typeOfSpeech = 17, morfCharacteristics = 103 - стол
initialFormString = стол, typeOfSpeech = 17, morfCharacteristics = 551 - стол
initialFormString = конференция, typeOfSpeech = 17, morfCharacteristics = 187 - конференций
```

##### Пример генерации словоформ
```
JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();
List<String> forms = jMorfSdk.getDerivativeForm("дерево",
		MorfologyParameters.TypeOfSpeech.NOUN,
		MorfologyParameters.Numbers.SINGULAR);
forms.forEach(System.out::println);
```
##### Вывод:
дерева
дерева
дереву
дереву
дерево
дерево
деревом
деревом
дереве
дереве

Дополнительные пример работы с библиотекой описаны в <b>Running.java</b> <br>

## Версии библиотеки

v 2.10.5 от 12.03.2018<br>
1) Исправлен баг связанных с хэш-кодом:<br>
  а) Добавлен новый алгоритм получения хэш-кода. <br>
  b) Добавлена двойная проверка соответствия формы и строкового представления искомых формы. <br>
2) Все повторяющиеся обертки классов вынесены в проект <b>TemplateWrapperClasses</b> (https://github.com/jalexpr/TemplateWrapperClasses), для уменьшения повторяющихся строк кода. <br>
<br>
<br>
=======<br>
<br>
v 2.10.4 от 04.03.2018<br>
1) Полностью переработана конвертация исходного в словарь в формы JMorfSdk, что значительно фиксит ошибки и небольшие баги, которые оставались при предыдущем способе конвертации.<br>
2) Незначительно изменился интерфейс с библиотекой, поэтому старые методы могут стать красным, но скорее всего нет. Также добавлены методы  упрощающие работы с получением, обработкой и выводом морф. характеристик слова. (методы находятся в классах JMorfSdk и MorfologyParametersHelper.<br>
3) Добавлена усовершенствованная архивация БД и файлов. Для переноса библиотека весит до 43МБ, но при первом запуске будет разворачиваться, поэтому первая загрузка будет чуть дольше. Рабочий размер JMorfSdk 148МБ.<br>
<br>
NB! Было замечено, что встречаются слова с буквой е вместо ё. Такие слова JMorfSdk не распознает, поэтому не стоит пугаться, если не будет найдено слово "пошел".<br>
<br>
<br>
v 2.10.3 от 24.02.2018 <br>
1) Добавил методы упрощающие получения характеристик: теперь достаточно указать класс (или классы) и библиотека сама сгенерирует маску, а также можно передать индикаторы или создать маску самому <br>
2) Вывод характеристик теперь осмысленный (т.е. выводится имя характеристики), а не численный (при условии, что выводится одна характеристика). <br>
<br>
<br>
v 2.10.2 от 30.12.2017 <br>
1. Перенес морф. параметров (MorphologyParameters) из JMorfSdk в проект MorphologicalStructures.<br>
2. БД dictionary.initialFormString и dictionary.wordFormString, которые хранят стринговое представление начальной и производных формы слова, соответственно, перенесены в проект  MorphologicalStructures. Теперь сохранив ключ, полученный входе работы JMorfSdk, можно не хранить стринговое представление в ОП и, когда оно понадобится, получить представление из БД (через проект MorphologicalStructures) при помощи ключа.<br>
<br>
<br>
v 2.10.1 от 30.12.2017 <br>
1) Символьное представление словоформы теперь храниться в базе данных (для начальной формы в <b>dictionary.initialFormString.bd</b>, для производной формы в <b>dictionary.wordFormString.bd</b>, что уменьшает издержки в ОП. <br>
2) База данных символьного представления и морфологические параметры хранятся в отдельные сборки MorphologicalStructures, что позволяет обрабатывать промежуточные результаты без JMorfSdk. <br>
3) Добавлен режим генерации словоформе по начальной форме и наборы морф. характеристик. <br>
4) JMorfSdk стала потокобезопасной. Теперь получать морф. характеристики можно одновременно с нескольких потоков, причем потоки не мешают друг другу и не блокируют библиотеку. <br>
5) Появилась поддержка цифр, теперь число "1945" корректно распознается, как словоформа числа. <br>
6) Теперь слова через дефис распознаются как слово, например, "что-то", "кто-то". <br>
7) Все методы для работы с библиотекой возвращают Exception в случае, если слово не было найдено в словаре или случилось иная ошибка. <br>
8) LoadJMorfSdk переименован в JMorfSdkLoad. <br>
9) Если не нужна генерация слова, то можно удалить базу <b>dictionary.wordFormString.bd</b>, но тогда загружать библиотеку следует с помощью <b>JMorfSdkLoad.loadInAnalysisMode</b> <br>
