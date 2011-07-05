import java.util.ArrayList;
import java.util.regex.*;
import java.util.Calendar;
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

	public ArrayList<Car> scrape(int begin, int end, String proxy) throws Exception {
		if (begin <= 0 || begin >= end) {
			throw new Exception("Wrong begin or end");
		}
		ArrayList<Car> cars = new ArrayList<Car>();

		ScraperConfiguration config =
				new ScraperConfiguration(this.config);
		Scraper scraper = new Scraper(config, workDir);
		scraper.setDebug(true);
		if(proxy != null)
			scraper.getHttpClientManager().setHttpProxy(proxy, 80);
		scraper.addVariableToContext("now", begin);
		scraper.addVariableToContext("end", end);
		scraper.addVariableToContext("pageUrl", url);
		scraper.execute();

		int count = ((Variable) scraper.getContext().get("count")).toInt();
		for (Integer i = 1; i <= count; ++i) {
			String priceStr = scraper.getContext().get("price" + i.toString()).toString();
			String id = scraper.getContext().get("id" + i.toString()).toString();
			String img = scraper.getContext().get("img" + i.toString()).toString();
			String retailer = scraper.getContext().get("retailer" + i.toString()).toString();
			String yearStr = scraper.getContext().get("year" + i.toString()).toString();
			String modelStr = scraper.getContext().get("model" + i.toString()).toString();
			String info = scraper.getContext().get("info" + i.toString()).toString();
			String dateLoc = scraper.getContext().get("dateLoc" + i.toString()).toString();

			String model = deleteBadSymbols(modelStr);
			int year = toInt(yearStr);
			int price = toInt(priceStr);
			retailer = deleteBadSymbols(retailer);
			dateLoc = deleteBadSymbols(dateLoc);

			info = deleteBadSymbols(info);
			Integer mileage = getMileageFromInfo(info);
			Double engineCap = getEngineCap(info);

			dateLoc = dateLoc.replace(",", "");
			int index = dateLoc.indexOf(' ');
			String city = dateLoc.substring(0, index);
			String date = dateLoc.substring(index + 1);
			if (!isImgUrlValid(img))
				img = null;
			cars.add(new Car(id, model, year, price, img, retailer, info, engineCap, mileage, city, date));
		}
		return cars;
	}

	private static boolean isImgUrlValid(String img) {
		if (img.length() < 1 || img.charAt(0) == '/')
			return false;
		return true;
	}
	static private int toInt(String str){
		String digits = str.replaceAll("\\D", "");
		return Integer.valueOf(digits);
	}
	private Integer getMileageFromInfo(String info){
		Pattern pattern = Pattern.compile("(\\d)+ км");
		Matcher matcher = pattern.matcher(info);
		if(matcher.find()){
			String mileage = matcher.group();
			return toInt(mileage);
		}
		return null;
	}
	private Double getEngineCap(String info){
		Pattern pattern = Pattern.compile("(\\d)+(\\.)*(\\d)* л");
		Matcher matcher = pattern.matcher(info);
		if(matcher.find()){
			String engineCapStr = matcher.group();
			String digits = engineCapStr.replaceAll(" [^0-9.]", "");
			return Double.parseDouble(digits);
		}
		return null;
	}

	static private String deleteBadSymbols(String str){
		String ans = str.replaceAll("\n", " ");
		ans = ans.replaceAll("( )+", " ");

		return ans;
	}
	public static void main(String argv[]) throws Exception {
		// begin and end of index of searched pages
		int begin = 77221319;
		int end = 77221321;
		String proxy = "200.68.43.235";
		String config = "config.xml";
		String workDir = "C:\\My Dropbox\\programms\\Java\\YaSimilarSearch\\temp";
		CarScraper cs = new CarScraper(config, workDir);
		ArrayList<Car> cars = cs.scrape(begin, end, proxy);


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
