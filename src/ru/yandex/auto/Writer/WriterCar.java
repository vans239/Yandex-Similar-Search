package ru.yandex.auto.writer;

import ru.yandex.auto.database.Database;

public interface WriterCar {
	void create(Database db)
			throws Exception;
}
