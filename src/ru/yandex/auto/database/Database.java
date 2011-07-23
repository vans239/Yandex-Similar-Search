package ru.yandex.auto.database;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import ru.yandex.auto.Car;
import ru.yandex.auto.util.DisjointSets;
import ru.yandex.auto.util.Util;

import java.util.Iterator;
import java.util.Date;
import java.util.Map;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database implements Iterable<Car> {
	private static Logger log = Logger.getLogger(Database.class);
	private Connection con;

	public Database(BasicDataSource bds) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		Class.forName(bds.getDriverClassName()).newInstance();
		con = DriverManager.getConnection(bds.getUrl(), bds.getUsername(), bds.getPassword());
		if (!con.isClosed())
			log.info("Successfully connected to MySQL server using TCP/IP...");
	}
	public ResultSet getCars(){
		ResultSet rs = null;
		try {
			Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_UPDATABLE);
			rs = statement.executeQuery("SELECT * FROM Car ORDER BY similarCarYandexId");
		} catch (SQLException e) {
			log.error("Something wrong with database", e);
		}
		return rs;
	}
	public Iterator<Car> iterator() {
		ResultSet rs = null;
		try {
			log.debug("before:" + Runtime.getRuntime().freeMemory());
			Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			statement.setFetchDirection(ResultSet.FETCH_FORWARD);
			rs = statement.executeQuery("SELECT * FROM Car");
			log.debug("after:" + Runtime.getRuntime().freeMemory());
		} catch (SQLException e) {
			log.error("Something wrong with database", e);
		}
		return new DatabaseIterator(rs);
	}

	public Iterator<Car> iteratorSimilarCar() {
		ResultSet rs = null;
		try {
			Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_UPDATABLE);
			rs = statement.executeQuery("SELECT * FROM Car ORDER BY similarCarYandexId");
		} catch (SQLException e) {
			log.error("Something wrong with database", e);
		}
		return new DatabaseIterator(rs);
	}

	public void addCar(Car car) throws SQLException {
		Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = statement.executeQuery("SELECT * FROM Car");
		rs.moveToInsertRow();
		if (car.carYandexId != null)
			rs.updateString("carYandexId", car.carYandexId);
		if (car.model != null)
			rs.updateString("model", car.model);
		rs.updateInt("year", car.year);
		rs.updateInt("price", car.price);
		if (car.imgUrl != null)
			rs.updateString("imgUrl", car.imgUrl);
		if (car.retailer != null)
			rs.updateString("retailer", car.retailer);
		if (car.mileage != null)
			rs.updateInt("mileage", car.mileage);
		if (car.engineCap != null)
			rs.updateDouble("engineCap", car.engineCap);
		if (car.info != null)
			rs.updateString("info", car.info);
		if (car.city != null)
			rs.updateString("city", car.city);
		//if (car.image != null){
		//	rs.updateObject("image", car.image);
		//}
		if (car.colour != null)
			rs.updateString("colour", car.colour);
		if (car.condition != null)
			rs.updateString("conditionCar", car.condition);
		if (car.date != null)
			rs.updateDate("dateSale", new java.sql.Date(car.date.getTime()));
		if (car.similarCarYandexId != null)
			rs.updateString("similarCarYandexId", car.similarCarYandexId);
		rs.insertRow();
		rs.close();
		statement.close();
	}

	public int size() throws SQLException {
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) As count FROM Car");
		rs.first();
		int size = rs.getInt("count");
		rs.close();
		statement.close();
		return size;
	}

	public int unique() throws SQLException {
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("SELECT COUNT(Distinct similarCarYandexId) As count FROM Car");
		rs.first();
		int size = rs.getInt("count");
		rs.close();
		statement.close();
		return size;
	}

	public void clearTable() throws SQLException {
		Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_UPDATABLE);
		statement.execute("DELETE FROM Car");
		statement.close();
	}

	public void setSimilars(DisjointSets ds, Map<String, Integer> map) throws SQLException {
		log.info("Start: Updating similars... ");
		Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_UPDATABLE);
		String sqlRequest = "SELECT * FROM CAR;";
		ResultSet rs = statement.executeQuery(sqlRequest);

		while (rs.next()) {
			Car car = getCar(rs);
			int i = map.get(car.carYandexId);
			int root = ds.root(i);
			String carYandexId = Util.getKeyByValue(map, root);
			rs.updateString("similarCarYandexId", carYandexId);
			rs.updateRow();
		}
		log.info("End: Updating similars... ");
	}

	public Car getCarByYandexId(String carYandexId) throws SQLException {
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM Car WHERE carYandexId = '" + carYandexId + "';");
		rs.first();
		return getCar(rs);
	}

	public boolean isExist(String carYandexId) throws SQLException {
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) As count FROM Car WHERE carYandexId = '" + carYandexId + "';");
		rs.first();
		int count = rs.getInt("count");
		return count != 0;
	}

	public static Car getCar(ResultSet rs) throws SQLException {
		String model = rs.getString("model");
		int year = rs.getInt("year");
		int price = rs.getInt("price");
		String imgUrl = rs.getString("imgUrl");
		if (rs.wasNull())
			imgUrl = null;
		String retailer = rs.getString("retailer");
		if (rs.wasNull())
			retailer = null;
		Integer mileage = rs.getInt("mileage");
		if (rs.wasNull())
			mileage = null;
		Double engineCap = rs.getDouble("engineCap");
		if (rs.wasNull())
			engineCap = null;
		String info = rs.getString("info");
		if (rs.wasNull())
			info = null;
		String city = rs.getString("city");
		if (rs.wasNull())
			city = null;
		Date date = rs.getDate("dateSale");
		if (rs.wasNull())
			date = null;
		String carYandexId = rs.getString("carYandexId");
		if (rs.wasNull())
			carYandexId = null;
		String colour = rs.getString("colour");
		if (rs.wasNull())
			colour = null;
		String condition = rs.getString("conditionCar");
		if (rs.wasNull())
			condition = null;
		//Image image = rs.getString("carYandexId");
		String similarCarYandexId = rs.getString("similarCarYandexId");
		if (rs.wasNull())
			similarCarYandexId = null;
		return new Car(carYandexId, model, year, price, imgUrl, retailer,
				info, engineCap, mileage, city, date, colour, condition, similarCarYandexId);
	}
}
