import java.util.ArrayList;

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

		int count = 2;
		//CarScraper cs = new CarScraperYandex(config, workDir, proxy, begin);
		CarScraper cs = (CarScraper) ctx.getBean("carScraper");

		ArrayList<Car> cars = cs.scrape(count);

		DisjointSets ds = new DisjointSets(cars.size());
		for (int i = 0; i < cars.size(); ++i) {
			Car c1 = cars.get(i);
			for(int j = i + 1; j < cars.size(); ++j){
				Car c2 = cars.get(j);
				if(c1.isSimilar(c2)){
					ds.unite( i, j);
					System.out.println(c1.id + " " + c2.id);
				}
			}
		}

		WriterCar writer = (WriterCar) ctx.getBean("writerCar");
		writer.create( cars, ds);
	}
}
//disjoints vs spring ??
//other properties
