package ru.yandex.auto.scraper;

import ru.yandex.auto.database.Database;

public interface CarScraper {
	void scrape(String url, int count, Database add) throws Exception;
}
