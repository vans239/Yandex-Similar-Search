
import java.io.IOException;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.BadElementException;
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
	String dateLoc;

	String colour = null;
	String data = null;
	Image image = null;
	Car(String id, String model, String year, String price, String img, String retailer, String dateLoc, String info){
		this.id = id;
		this.model = deleteBadSymbols(model);
		this.year = toInt(year);
		this.price = toInt(price);
		this.img = img;
		this.retailer = deleteBadSymbols(retailer);
		this.dateLoc = deleteBadSymbols(dateLoc);
		this.info = deleteBadSymbols(info);
		this.mileage = getMileageFromInfo();
		this.engineCap = getEngineCap();
		if(img != null)
			this.downloadImage();
	}
	public boolean isSimilar(Car car){
		if(this.year != car.year || !isModelSimilar(car) || !isEngineCapSimilar(car) )
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
		str.append(dateLoc);
		str.append('\n');
		return str.toString();
	}
	static private int toInt(String str){
		String digits = str.replaceAll("\\D", "");
		return Integer.valueOf(digits);
	}
	static private String deleteBadSymbols(String str){
		String ans = str.replaceAll("\n", " ");
		ans = ans.replaceAll("( )+", " ");

		return ans;
	}
	private Integer getMileageFromInfo(){
		Pattern pattern = Pattern.compile("(\\d)+ км");
		Matcher matcher = pattern.matcher(info);
		if(matcher.find()){
			String mileage = matcher.group();
			return toInt(mileage);
		}
		return null;
	}
	private Double getEngineCap(){
		Pattern pattern = Pattern.compile("(\\d)+(\\.)*(\\d)* л");
		Matcher matcher = pattern.matcher(info);
		if(matcher.find()){
			String engineCapStr = matcher.group();
			String digits = engineCapStr.replaceAll(" [^0-9.]", "");
			return Double.parseDouble(digits);
		}
		return null;
	}
	private void downloadImage(){
		try{
    		image = new Jpeg(new URL(img));
		} catch(Exception exp){
			exp.printStackTrace();
		}
	}

}
