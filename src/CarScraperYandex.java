import java.net.URL;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Calendar;
import java.util.Locale;
import java.util.GregorianCalendar;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;

import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.webharvest.runtime.variables.Variable;

import com.itextpdf.text.Image;
import com.itextpdf.text.Jpeg;

public class CarScraperYandex implements CarScraper {
	//public static String url = "http://auto.yandex.ru/search.xml?cluster_id=";
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

	public void scrape(String url, int count, Database db) throws Exception {
		int end = begin + count;
		if (begin <= 0 || begin >= end) {
			throw new Exception("Wrong begin or end");
		}

		ScraperConfiguration config =
				new ScraperConfiguration(this.config);
		Scraper scraper = new Scraper(config, workDir);
		scraper.setDebug(true);
		if (proxy != null)
			scraper.getHttpClientManager().setHttpProxy(proxy, 80);
		scraper.addVariableToContext("now", begin);
		scraper.addVariableToContext("end", end);
		scraper.addVariableToContext("count", count);
		scraper.addVariableToContext("pageUrl", url);
		scraper.execute();

		int countScrapedCars = ((Variable) scraper.getContext().get("count")).toInt();
		for (Integer i = 1; i <= countScrapedCars; ++i) {
			try {
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

				//System.out.println("date before format:" + dateLoc);
				dateLoc = dateLoc.replaceAll("['\\u00A0''\\u2007''\\u202F']", " ");
				int index = dateLoc.indexOf(',');
				String city = dateLoc.substring(0, index);
				String dateStr = dateLoc.substring(index + 1);
				dateStr = dateStr.replaceAll("['\\u00A0''\\u2007''\\u202F'',']", " ");
				Date date = CarScraperYandex.getDate(dateStr);
				String colour = getColour(info);
				if (!isImgUrlValid(img))
					img = null;
				//System.out.println("site:" + img);

				Car car = new Car(id, model, year, price, img, retailer, info, engineCap, mileage, city, date, colour, null);
				if (!db.isExist(car.carYandexId))
					db.addCar(car);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean isImgUrlValid(String imgUrl) {

		return (imgUrl != null && imgUrl.length() >= 1 && imgUrl.charAt(0) != '/');
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

	public static Date getDate(String dateStr) {
		if (dateStr == null)
			return null;
		dateStr = dateStr.replaceAll("( )+$", "");
		if (dateStr.contains("сегодня")) {
			return new Date();
		}
		GregorianCalendar calendar = new GregorianCalendar();
		if (dateStr.contains("вчера")) {
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

	private static String getColour(String info) {
		Pattern pattern = Pattern.compile("цвет (.)*?,");
		Matcher matcher = pattern.matcher(info);
		if (matcher.find()) {
			String colour = matcher.group();
			colour = colour.replaceAll("['\\u00A0''\\u2007''\\u202F'','' ']", "");
			colour = colour.replaceAll("цвет", "");
			return colour;
		}
		return null;
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
