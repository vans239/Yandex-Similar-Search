package ru.yandex.auto;

import ru.yandex.auto.util.LevenshteinDist;

import java.util.Date;

public class Metrics {
	public static double getDistance(Car a, Car b) {
		return getDistanceYear(a.year, b.year)
				+ getDistanceMileage(a, b)
				+ getDistanceEngineCap(a.engineCap, b.engineCap)
				+ getDistanceDate(a.date, b.date)
				+ getDistanceColour(a.colour, b.colour)
				+ getDistanceCondition(a.condition, b.condition)
				+ getDistanceModel(a.model, b.model)
				+ getDistancePrice(a.price, b.price)
				+ getDistanceCity(a.city, b.city);
	}

	public static double getDistanceYear(int a, int b) {
		if (a == b)
			return 0.0;
		return 0.8;
	}

	public static double getDistancePrice(int a, int b) {
		if (a == b)
			return 0.0;
		if (Math.abs(a - b) < 10000)
			return 0.2;
		if (Math.abs(a - b) < 40000)
			return 0.4;
		return 0.5;
	}

	public static double getDistanceEngineCap(Double a, Double b) {
		if (a == null || b == null)
			return 0.5;

		if (a.equals(b))
			return 0.0;

		return 0.6;
	}

	public static double getDistanceMileage(Car a, Car b) {
		//    mileage == price
		if (a.mileage == null || b.mileage == null)
			return 0.5;
		if (a.mileage == a.price || b.mileage == b.price)
			return 0.2;
		if (a.mileage < 1000)
			a.mileage *= 1000;
		if (b.mileage < 1000)
			b.mileage *= 1000;
		if (a.mileage.equals(b.mileage))
			return 0.0;
		if (Math.abs(a.mileage - b.mileage) < 5000)
			return 0.2;
		if (Math.abs(a.mileage - b.mileage) < 20000)
			return 0.4;
		return 0.5;
	}

	public static double getDistanceDate(Date a, Date b) {
		assert (a != null && b != null);
		long diff = a.getTime() - b.getTime();
		final long day = 24 * 60 * 60 * 1000;
		if (diff == 0)
			return 0.0;
		if (diff / day < 15)
			return 0.3;
		return 0.5;
	}


	public static double getDistanceColour(String a, String b) {
		if (a == null || b == null)
			return 0.1;
		if (a.equals(b))
			return 0.0;
		return 0.6;
	}

	public static double getDistanceCondition(String a, String b) {
		if (a == null || b == null)
			return 0.0;
		if (a.equals(b))
			return 0.0;
		return 0.3;
	}


	public static double getDistanceModel(String a, String b) {
		/*
			Mercedes-Benz E-Класс
			Mercedes-Benz E-Klasse
		 */
		assert (a != null && b != null);

		if (a.length() > b.length())
			return getDistanceModel(b, a);
		String str = b.substring(0, a.length());

		if (a.equals(b))
			return 0.0;
		if (a.equals(str))
			return 0.1;
		return Math.min(LevenshteinDist.getDist(str, a), LevenshteinDist.getDist(b, a)) / a.length();
	}

	public static double getDistanceCity(String a, String b) {
		if (a == null || b == null)
			return 0.2;
		if (a.equals(b))
			return 0.0;
		return 0.7;
	}
}
// logging!!!
// asserts!!