import java.util.ArrayList;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Main {
	public static void main(String argv[]) throws Exception {
		// begin  index of searched pages and count of cars
		ApplicationContext ctx =
				new FileSystemXmlApplicationContext("spring.xml");

/*		int begin = 77221319;
		String proxy = "200.68.43.235";
		String config = "config.xml";
		String workDir = "C:\\My Dropbox\\programms\\Java\\YaSimilarSearch";*/
		//CarScraper cs = new CarScraperYandex(config, workDir, proxy, begin);

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
		db.clearTable();
		int count = 2;
		CarScraper cs = (CarScraper) ctx.getBean("carScraper");

		ArrayList<Car> cars = cs.scrape(count, db);

		DisjointSets ds = new DisjointSets(cars.size());
		for (int i = 0; i < cars.size(); ++i) {
			Car c1 = cars.get(i);
			for (int j = i + 1; j < cars.size(); ++j) {
				Car c2 = cars.get(j);
				if (c1.isSimilar(c2)) {
					ds.unite(i, j);
					System.out.println(c1.carYandexId + " " + c2.carYandexId);
				}
			}
		}
		WriterCar writer = (WriterCar) ctx.getBean("writerCar");
		writer.create(cars, ds);
		db.print();
	}
}
//disjoints vs spring ??
//other properties
