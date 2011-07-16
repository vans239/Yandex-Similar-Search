import java.util.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Main {
	public static void main(String argv[]) throws Exception {
		ApplicationContext ctx =
				new FileSystemXmlApplicationContext("spring.xml");

		// 77221319
		String url = "jdbc:mysql://localhost:3306/test";
		String driver = "com.mysql.jdbc.Driver";
		String user = "vans239";
		String password = "qwerty";
		Properties properties = new Properties();
		properties.setProperty("user", user);
		properties.setProperty("password", password);
		properties.setProperty("useUnicode", "true");
		properties.setProperty("characterEncoding", "UTF-8");
		Database db = new Database(driver, url, properties);

		//db.clearTable();

		//int count = 15;
		//CarScraper cs = (CarScraper) ctx.getBean("carScraper");
		//cs.scrape(count, db);

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
		for (Iterator<Car> it1 = db.iterator(); it1.hasNext(); ) {
			Car car1 = (Car) it1.next();
			for (Iterator<Car> it2 = db.iterator(); it2.hasNext(); ) {
				Car car2 = it2.next();
				if (car1.isSimilar(car2)) {
					int dsNumber1 = map.get(car1.carYandexId);
					int dsNumber2 = map.get(car2.carYandexId);
					ds.unite(dsNumber1, dsNumber2);
					db.setSimilarCar(car1.carYandexId, car2.carYandexId);
					System.out.println(car1.carYandexId + " " + car2.carYandexId);
				}
			}
		}

		WriterCar writer = (WriterCar) ctx.getBean("writerCar");
		writer.create(db, ds, map);
	}
}
//disjoints vs spring ??
//other properties
