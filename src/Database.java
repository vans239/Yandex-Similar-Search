import java.sql.*;

public class Database {
	static Car getCar(ResultSet rs) throws SQLException {
		int carId = rs.getInt("carId");
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
		return new Car(carYandexId, model, year, price, imgUrl, retailer, info, engineCap, mileage, city, date, null);
	}

	static void addCar(ResultSet rs, Car car) throws SQLException {
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
		//rs.updateDate("dateSale", car.date);
		rs.insertRow();
	}

	static void clearTable(Statement stmnt) throws SQLException {

		stmnt.execute("DELETE FROM Car;");
	}

	public static void main(String argv[]) {
		Connection con;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://localhost:3306/test";
			con = DriverManager.getConnection(url, "vans239", "qwerty");
			if (!con.isClosed())
				System.out.println("Successfully connected to MySQL server using TCP/IP...");
			Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_UPDATABLE);

			ResultSet rs = statement.executeQuery("SELECT * FROM Car");
			while (rs.next()) {
				Car car = getCar(rs);
				System.out.println(car);
			}
			//addCar(rs, new Car( "wrrwwwer", "qwer", 0, 0, null, null, null, null, null, null, null, null));
			clearTable(statement);
			statement.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception: " + e.getMessage());
		}
	}
}
