import java.sql.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.Date;

public class Database implements Iterable<Car> {
	private Connection con;

	public Database(String driver, String url, Properties properties) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		Class.forName(driver).newInstance();
		con = DriverManager.getConnection(url, properties);
		if (!con.isClosed())
			System.out.println("Successfully connected to MySQL server using TCP/IP...");
	}

	public Iterator<Car> iterator() {
		ResultSet rs = null;
		try {
			Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_UPDATABLE);
			rs = statement.executeQuery("SELECT * FROM Car");
		} catch (SQLException e) {
			e.printStackTrace();
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
		System.out.println(car.date.toString());
		//rs.updateDate("date", Date.valueOf(car.date.toString()));
		rs.insertRow();
		rs.close();
		statement.close();
	}

	public int size() throws SQLException {
		Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) As count FROM Car");
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

	public void setSimilarCar(String carYandexId, String carSimilarYandexId) throws SQLException {
		Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_UPDATABLE);
		String sqlRequest = "UPDATE Car SET similarCar = '" + carSimilarYandexId +
				"' WHERE (carYandexId = '" + carYandexId + "' OR similarCar = '" + carYandexId + "');";
		statement.execute(sqlRequest);
	}

	public Car getCarByYandexId(String carYandexId) throws SQLException {
		Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = statement.executeQuery("SELECT * FROM Car WHERE carYandexId = '" + carYandexId + "';");
		rs.first();

		return getCar(rs);
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
		//Image image = rs.getString("carYandexId");
		return new Car(carYandexId, model, year, price, imgUrl, retailer, info, engineCap, mileage, city, date, null);
	}


}
