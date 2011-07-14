import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.*;
import java.util.Calendar;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.GregorianCalendar;

import com.itextpdf.text.Image;
import com.itextpdf.text.Jpeg;
import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.webharvest.runtime.variables.Variable;


public class CarScraperYandex implements CarScraper {
	public static String url = "http://auto.yandex.ru/search.xml?cluster_id=";
	private String config;
	private String workDir;
	private String proxy;
	private int begin;

	public CarScraperYandex(String config, String workDir, String proxy, int begin) {
		this.config = config;
		this.workDir = workDir;
		this.proxy = proxy;
		this.begin = begin;
	}

	public ArrayList<Car> scrape(int count) throws Exception {
		int end = begin + count;
		if (begin <= 0 || begin >= end) {
			throw new Exception("Wrong begin or end");
		}
		ArrayList<Car> cars = new ArrayList<Car>();

		ScraperConfiguration config =
				new ScraperConfiguration(this.config);
		Scraper scraper = new Scraper(config, workDir);
		scraper.setDebug(true);
		if (proxy != null)
			scraper.getHttpClientManager().setHttpProxy(proxy, 80);
		scraper.addVariableToContext("now", begin);
		scraper.addVariableToContext("end", end);
		scraper.addVariableToContext("pageUrl", url);
		scraper.execute();

		int countScrapedCars = ((Variable) scraper.getContext().get("count")).toInt();
		for (Integer i = 1; i <= countScrapedCars; ++i) {
			String priceStr = scraper.getContext().get("price" + i.toString()).toString();
			String id = scraper.getContext().get("carYandexId" + i.toString()).toString();
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

			dateLoc = dateLoc.replaceAll(",", "");
			dateLoc = dateLoc.replaceAll("['\\u00A0''\\u2007''\\u202F']", " ");
			int index = dateLoc.indexOf(' ');
			String city = dateLoc.substring(0, index);
			Date date = getDate(dateLoc.substring(index + 1));
			if (!isImgUrlValid(img))
				img = null;
			Image image = downloadImage(img);
			cars.add(new Car(id, model, year, price, img, retailer, info, engineCap, mileage, city, date, image));
		}
		return cars;
	}

	private static boolean isImgUrlValid(String img) {
		//	boolean a = img.length() < 1;
		//	boolean b = img.charAt(0) == '/';
		return (img != null && img.length() >= 1 && img.charAt(0) != '/');
	}

	private static int toInt(String str) {
		String digits = str.replaceAll("\\D", "");
		return Integer.valueOf(digits);
	}

	private static Integer getMileageFromInfo(String info) {
		Pattern pattern = Pattern.compile("(\\d)+ км");
		Matcher matcher = pattern.matcher(info);
		if (matcher.find()) {
			String mileage = matcher.group();
			return toInt(mileage);
		}
		return null;
	}

	private static Double getEngineCap(String info) {
		Pattern pattern = Pattern.compile("(\\d)+(\\.)*(\\d)* л");
		Matcher matcher = pattern.matcher(info);
		if (matcher.find()) {
			String engineCapStr = matcher.group();
			String digits = engineCapStr.replaceAll(" [^0-9.]", "");
			return Double.parseDouble(digits);
		}
		return null;
	}

	private static Date getDate(String dateStr) {
		if (dateStr == null)
			return null;
		dateStr = dateStr.replaceAll("( )+$", "");
		if (dateStr.equals("сегодня")) {
			return new Date();
		}
		GregorianCalendar calendar = new GregorianCalendar();
		if (dateStr.equals("вчера")) {
			calendar.add(Calendar.DATE, -1);
			return calendar.getTime();
		}

		String[] russianMonth =
				{
						"января",
						"февраля",
						"марта",
						"апреля",
						"мая",
						"июня",
						"июля",
						"августа",
						"сентября",
						"октября",
						"ноября",
						"декабря"
				};
		Locale local = new Locale("ru", "RU");
		DateFormatSymbols russSymbol = new DateFormatSymbols(local);
		russSymbol.setMonths(russianMonth);
		SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy", russSymbol);

		Date date = null;
		try {
			date = sdf.parse(dateStr + " " + calendar.get(Calendar.YEAR));
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return date;
	}

	private static Image downloadImage(String imgUrl) {
		Image image = null;
		try {
			image = new Jpeg(new URL(imgUrl));
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return image;
	}

	private static String deleteBadSymbols(String str) {
		String ans = str.replaceAll("\n", " ");
		ans = ans.replaceAll("( )+", " ");
		return ans;
	}
}
