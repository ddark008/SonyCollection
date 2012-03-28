SonyCollections
==========

Russian Readme Version
----------------------

Описание
-----------

**SonyCollections** это генератор коллекций для Sony PRS-T1, на основании структуры папок.
Если поместить файл программы в папку со следующей структурой:

	SonyCollections
	Лоис Макмастер Буджолд
		Барраярд
			Осколки чести
			Ученик воина
		Бартер
		Истина в дыре

То будут созданы коллекции "Лоис Макмастер Буджолд" и "Лоис Макмастер Буджолд ~ Барраярд"
В коллекцию "Лоис Макмастер Буджолд" будут входить все книги в папке и подпапках, а в "Лоис Макмастер Буджолд ~ Барраярд", только файлы в папке "Барраярд" и её подпаках

Возможности
-----------
* Исключение папок по маске
* Поддержка вывода кирилицы
* Консольное приложение
* Логирование в файл
* Поддержка Windows/Linux/Mac
* Бэкап базы коллекций

Быстрый старт
------------
1. Поместите файлы книг на книгу и проиндексируйте их (подождать пока появятся в списке доступных книг)
2. Перенесите файл программы в папку с будущей коллекцией
3. Запустите из консоли(cmd.exe) с помощью команды "java -jar путь_к_файлу"
4. Наслаждайтесь новыми коллекциями

Настройки
------------
Запустите "java -jar SonyCollectins.jar -?" для просмотра доступных опций 
В файл scignore.txt добавте маски исключений

Changelog
=========

v 1.0.0
-----

* Добавил на GitHub

ToDo List
=========

* Написать документацию
* Дописать бэкап (Стирание старых копий)
* Повысить стабильность и читаемость кода
* Добавить GUI (в очень далёком будущем)
* Добавить шаблонов
* Добавить локализацию
* Портирование на Android (нужна помощь)

License
=========

You are free:

* to Share — to copy, distribute and transmit the work
* o Remix — to adapt the work

-----
You can read more about this license [here](http://creativecommons.org/licenses/by-nc-sa/3.0/)