package ru.yandex.auto;

import java.io.FileInputStream;
import java.util.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import ru.yandex.auto.writer.WriterCar;
import ru.yandex.auto.database.Database;
import ru.yandex.auto.scraper.CarScraper;
import ru.yandex.auto.util.DisjointSets;

public class Main {
	public static void main(String argv[]) throws Exception {
		System.out.println("Start time:" + new Date());
		ApplicationContext ctx =
				new FileSystemXmlApplicationContext("resources/spring.xml");

		Database db = (Database) ctx.getBean("database");

		Properties properties = new Properties();
		properties.load(new FileInputStream("resources/project.properties"));
		boolean isScrape = Boolean.valueOf(properties.getProperty("isScrape"));
		if (isScrape) {
			System.out.println("Scraping cars.. " + new Date());
			db.clearTable();
			int count = Integer.valueOf(properties.getProperty("webharvest.count"));
			String pageUrl = properties.getProperty("webharvest.url");
			CarScraper cs = (CarScraper) ctx.getBean("carScraper");
			cs.scrape(pageUrl, count, db);
		}
		int size = db.size();

		DisjointSets ds = new DisjointSets(size);
		Map<String, Integer> map = new HashMap<String, Integer>();
		int dsNumber = 0;
		for (Iterator it = db.iterator(); it.hasNext(); ) {
			Car car = (Car) it.next();
			if (!map.containsKey(car.carYandexId)) {
				map.put(car.carYandexId, dsNumber);
				++dsNumber;
			}
		}
		System.out.println("Finding similar cars..." + new Date());


		for (Iterator<Car> it1 = db.iterator(); it1.hasNext(); ) {
			Car car1 = it1.next();
			for (Iterator<Car> it2 = db.iterator(); it2.hasNext(); ) {
				Car car2 = it2.next();
				if (!car1.carYandexId.equals(car2.carYandexId) && car1.isSimilar(car2)) {
					int dsNumber1 = map.get(car1.carYandexId);
					int dsNumber2 = map.get(car2.carYandexId);
					ds.unite(dsNumber1, dsNumber2);
					System.out.println(car1.carYandexId + " " + car2.carYandexId);
				}
			}
		}
		System.out.println("Updating similars..." + new Date());
		db.setSimilars(ds, map);
		System.out.println("Creating output...(downloading images for pdf)..." + new Date());

		WriterCar writer = (WriterCar) ctx.getBean("writerCar");
		writer.create(db);
		System.out.println("End time:" + new Date());

	}
}
//storing  image in database


// SELECT carYandexId, similarCarYandexId, model, year, price, mileage, datesale, engineCap From ru.yandex.auto.Car ORDER by similarCarYandexId;
/*
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
<hibernate-mapping>
  <class name="ru.yandex.auto.Car" table="HibernateCar">
    <id name="carYandexId" column="carYandexId"/>
    <property name="model" />
    <property name="year" />
    <property name="price" />
    <property name="mileage" />
    <property name="engineCap" />
    <property name="info" />
    <property name="imgUrl" />
    <property name="retailer" />
    <property name="city" />
    <property name="date" />
    <property name="similarCarYandexId"/>
  </class>
 
</hibernate-mapping>
*/
