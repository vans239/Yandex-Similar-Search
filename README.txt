Программа поиска одинаковых объявлений на сайте avto.yandex.ru

Написана на java c использованием spring, itextpdf, web-harvest, mySql.

Программа собирает объявления с сайта и ищет идентичные.
Учитываются:
1. Цена
2. Модель
3. Год
4. Пробег
5. Дата размещения объявления
6. Город размещения
7. Размер двигателя.
8. Цвет

Решено не учитывать на данном этапе:
1. Комплектация
потому что в большинстве объявлениях информация неполна.

В планах, как наиболее перспективные направления:
1. Учет размера города и дальнейшее изменение требований
2. Учет популярности марки и модели (года модели)
3. Сравнение фоток

Алгоритм:
1. Сбор информации с помощью web-harvest. Занесение в базу данных.
2. Поиск одинаковых объявлений и объединение в систему непересекающихся множеств.
3. Занесение информации в базу данных.
4. Вывод в файл из базы данных.

Вопросы по установке и запуску рассмотрены в wiki.
https://github.com/vans239/Yandex-Similar-Search

Таблица вида:

create table Car
(
carId int(11) not Null AUTO_INCREMENT,
model varchar(100) NOT NULL,
year int(11) NOT NULL,
price int(11) NOT NULL,
imgUrl VARCHAR(200),
retailer VARCHAR(100),
info VARCHAR(1000),
mileage int(11),
engineCap DOUBLE,
city VARCHAR(100),
dateSale Date,
carYandexId VARCHAR(100) NOT NULL,
image blob,
similarCarYandexId VARCHAR(100),
colour VARCHAR(100),
conditionCar VARCHAR(100),
PRIMARY KEY(carId),
UNIQUE (carYandexId)
) DEFAULT CHARSET=utf8;