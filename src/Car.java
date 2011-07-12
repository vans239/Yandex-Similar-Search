
import java.net.*;
import java.util.*;

import com.itextpdf.text.Jpeg;
import com.itextpdf.text.Image;
public class Car {
	String id;
	String model;
	int year;
	int price;
	Integer mileage = null;
	Double engineCap = null;
	String info;
	String img;
	String retailer;
	String city;
	Date date;

	String colour = null;
	Image image = null;
	Car(String id, String model, int year, int price, String img, String retailer,
		String info, Double engineCap, Integer mileage, String city, Date date){
		this.id = id;
		this.model = model;
		this.year = year;
		this.price = price;
		this.img = img;
		this.retailer = retailer;
		this.info = info;
		this.mileage = mileage;
		this.engineCap = engineCap;
		this.city = city;
		this.date = date;
		//if(img != null)
		//	this.downloadImage();
	}
	public boolean isSimilar(Car car){
		if(this.year != car.year || !isModelSimilar(car) || !isEngineCapSimilar(car) || !this.city.equals(car.city)
				|| !isDateSimilar(car))
			return false;

		if(isImgSimilar(car)){
			return true;
		}
		return true;
	}
	private boolean isImgSimilar(Car car){
		if(this.img == null || car.img == null)
			return false;
		if(this.img.equals(car.img))
			return true;
		//opencv will be here))
		return false;
	}
	private boolean isDateSimilar(Car car){
		if(this.date == null || car.date == null)
			return true;
		long diff = this.date.getTime() - car.date.getTime();
		final long day = 24 * 60 * 60 * 1000;
		return diff < 2 * day;
	}
	private boolean isModelSimilar(Car car){
		// may be Levenshtein  distance  will be better

		if(this.model.length() > car.model.length())
			return car.isModelSimilar(this);
		String str = car.model.substring(0, this.model.length());
		return str.equals(model);
	}
	private boolean isEngineCapSimilar(Car car){
		return car.engineCap == null || this.engineCap == null || car.engineCap.equals(this.engineCap);
	}
	public String toString(){
		StringBuilder str = new StringBuilder(model);
		str.append('\n');
		str.append(img);
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


	private void downloadImage(){
		try{
    		image = new Jpeg(new URL(img));
		} catch(Exception exp){
			exp.printStackTrace();
		}
	}

}
