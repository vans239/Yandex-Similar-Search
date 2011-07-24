package ru.yandex.auto;

import ru.yandex.auto.util.LevenshteinDist;

import java.util.Date;

public class Car {
	public String carYandexId;
	public String model;
	public int year;
	public int price;
	public Integer mileage = null;
	public Double engineCap = null;
	public String info;
	public String imgUrl;
	public String retailer;
	public String city;
	public Date date;
	public String similarCarYandexId;
	public String colour;
	public String condition;

	public Car(String carYandexId, String model, int year, int price, String imgUrl, String retailer,
			   String info, Double engineCap, Integer mileage, String city, Date date, String colour, String condition, String similarCarYandexId) {
		this.carYandexId = carYandexId;
		this.model = model;
		this.year = year;
		this.price = price;
		this.imgUrl = imgUrl;
		this.retailer = retailer;
		this.info = info;
		this.mileage = mileage;
		this.engineCap = engineCap;
		this.city = city;
		this.date = date;
		this.colour = colour;
		this.condition = condition;
		if (similarCarYandexId != null)
			this.similarCarYandexId = similarCarYandexId;
		else
			this.similarCarYandexId = this.carYandexId;
	}

	public boolean isSimilar(Car car) {
		return Metrics.getDistance(this, car) < 1.0;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(carYandexId);
		str.append('\n');
		str.append(model);
		str.append('\n');
		str.append(imgUrl);
		str.append('\n');
		str.append(retailer);
		str.append('\n');
		str.append(year);
		str.append('\n');
		str.append(price);
		str.append('\n');
		str.append(info);
		str.append('\n');
		str.append(city);
		str.append('\n');
		str.append(date);
		str.append('\n');
		return str.toString();
	}
}
