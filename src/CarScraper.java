import java.util.ArrayList;
import java.util.regex.*;

import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.webharvest.runtime.variables.Variable;

public class CarScraper {
	public static String url = "http://auto.yandex.ru/search.xml?cluster_id=";
	private String config;
	private String workDir;

	public CarScraper(String config, String workDir) {
		this.config = config;
		this.workDir = workDir;
	}

	public ArrayList<Car> scrape(int begin, int end) throws Exception {
		if (begin <= 0 || begin >= end) {
			throw new Exception("Wrong begin or end");
		}
		ArrayList<Car> cars = new ArrayList<Car>();

		ScraperConfiguration config =
				new ScraperConfiguration(this.config);
		Scraper scraper = new Scraper(config, workDir);
		scraper.setDebug(true);
		scraper.getHttpClientManager().setHttpProxy("200.68.43.235", 80);
		scraper.addVariableToContext("now", begin);
		scraper.addVariableToContext("end", end);
		scraper.addVariableToContext("pageUrl", url);
		scraper.execute();

		int count = ((Variable) scraper.getContext().get("count")).toInt();
		for (Integer i = 1; i <= count; ++i) {
			String price = scraper.getContext().get("price" + i.toString()).toString();
			String id = scraper.getContext().get("id" + i.toString()).toString();
			String img = scraper.getContext().get("img" + i.toString()).toString();
			String retailer = scraper.getContext().get("retailer" + i.toString()).toString();
			String year = scraper.getContext().get("year" + i.toString()).toString();
			String model = scraper.getContext().get("model" + i.toString()).toString();
			String info = scraper.getContext().get("info" + i.toString()).toString();
			String dateLoc = scraper.getContext().get("dateLoc" + i.toString()).toString();
			if (!isImgUrlValid(img))
				img = null;
			cars.add(new Car(id, model, year, price, img, retailer, dateLoc, info));
		}
		return cars;
	}

	private static boolean isImgUrlValid(String img) {
		if (img.length() < 1 || img.charAt(0) == '/')
			return false;
		return true;
	}

	public static void main(String argv[]) throws Exception {
		int begin = 77221319;
		int end = 77221321;
		String config = "config.xml";
		String workDir = "C:\\My Dropbox\\programms\\Java\\YaSimilarSearch\\temp";
		CarScraper cs = new CarScraper(config, workDir);
		ArrayList<Car> cars = cs.scrape(begin, end);


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

		PDFGenerator cp = new PDFGenerator();
		cp.createPdf("content.pdf", cars, ds);
		return;
	}
}
