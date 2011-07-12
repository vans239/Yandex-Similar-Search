import java.sql.*;

public class Database {
	static Car getCar(ResultSet rs) throws SQLException {
		int carId = rs.getInt("carId");
		String model = rs.getString("model");
		int year = rs.getInt("year");
		int price = rs.getInt("price");
		String imgUrl = rs.getString("imgUrl");
		String retailer = rs.getString("retailer");
		Integer mileage = rs.getInt("mileage");
		Double engineCap = rs.getDouble("engineCap");
		String info = rs.getString("info");
		String city = rs.getString("city");
		Date date = rs.getDate("dateSale");

		Car car = new Car("", model, year, price, imgUrl, retailer, info, engineCap, mileage, city, date);
		return car;
	}

	public static void main(String argv[]) {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://localhost:3306/test";
			con = DriverManager.getConnection(url, "vans239", "qwerty");
			if (!con.isClosed())
				System.out.println("Successfully connected to MySQL server using TCP/IP...");
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM Car");
			while (rs.next()) {
				Car car = getCar(rs);
				System.out.println(car);
			}
			con.close();
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
}
