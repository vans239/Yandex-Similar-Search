package ru.yandex.auto;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.util.*;

import org.apache.log4j.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import ru.yandex.auto.writer.WriterCar;
import ru.yandex.auto.database.Database;
import ru.yandex.auto.scraper.CarScraper;
import ru.yandex.auto.util.DisjointSets;

public class Main {
	private static Logger log = Logger.getLogger(Main.class);

	public static void main(String argv[]) {
		log.info("Programm starts");
		ApplicationContext ctx =
				new FileSystemXmlApplicationContext("resources/spring.xml");

		Database db = (Database) ctx.getBean("database");
		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream("resources/project.properties"));
			boolean isScrape = Boolean.valueOf(properties.getProperty("isScrape"));
			if (isScrape) {
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
			{
				log.info("Finding similars:");
				ResultSet rs1 = db.getCars();
				ResultSet rs2 = db.getCars();
				Car car1 = null;
				Car car2 = null;
				while (rs1.next()) {
					car1 = Database.getCar(rs1);
					rs2.absolute(rs1.getRow());
					int dsNumber1 = map.get(car1.carYandexId);
					while (rs2.next()) {
						car2 = Database.getCar(rs2);
						if (!car1.carYandexId.equals(car2.carYandexId) && car1.isSimilar(car2)) {
							int dsNumber2 = map.get(car2.carYandexId);
							ds.unite(dsNumber1, dsNumber2);
							log.debug(car1.carYandexId + " " + car2.carYandexId);
						}
					}
				}
				rs1.close();
				rs2.close();
				rs1 = null;
				rs2 = null;
			}

			db.setSimilars(ds, map);
			WriterCar writer = (WriterCar) ctx.getBean("writerCar");
			writer.create(db);
			log.info("Programm finished");
		} catch (Exception e) {
			log.error("Cause of ending of programm", e);
		}
	}
}
//storing  image in database

// SELECT carYandexId, similarCarYandexId, model, year, price, mileage, datesale, engineCap From Car ORDER by similarCarYandexId;

//check car-ad-79697272

//  25
//   24
// 49