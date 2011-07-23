package ru.yandex.auto.database;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import ru.yandex.auto.Car;
import ru.yandex.auto.util.DisjointSets;
import ru.yandex.auto.util.Util;
import sun.misc.JavaUtilJarAccess;
import sun.nio.cs.ext.DBCS_IBM_EBCDIC_Decoder;

import java.sql.*;
import java.util.Iterator;
import java.util.Date;
import java.util.Map;

public class Database implements Iterable<Car> {
	private static Logger log = Logger.getLogger(Database.class);
	private Connection con;

	public Database(BasicDataSource bds) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		Class.forName(bds.getDriverClassName()).newInstance();
		con = DriverManager.getConnection(bds.getUrl(), bds.getUsername(), bds.getPassword());
		if (!con.isClosed())
			log.info("Successfully connected to MySQL server using TCP/IP...");
	}

	public ResultSet getCars() throws SQLException {
		Statement statement = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = statement.executeQuery("SELECT * FROM Car ORDER BY similarCarYandexId");
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
		String sql = "insert into Car (carYandexId, model, year, price, imgUrl, retailer," +
				" mileage, engineCap, info, city, colour, conditionCar, dateSale, similarCarYandexId ) " +
				"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, car.carYandexId);
		pstmt.setString(2, car.model);
		pstmt.setInt(3, car.year);
		pstmt.setInt(4, car.price);
		if (car.imgUrl != null)
			pstmt.setString(5, car.imgUrl);
		else
			pstmt.setNull(5, Types.VARCHAR);
		if (car.retailer != null)
			pstmt.setString(6, car.retailer);
		else
			pstmt.setNull(6, Types.VARCHAR);
		if (car.mileage != null)
			pstmt.setInt(7, car.mileage);
		else
			pstmt.setNull(7, Types.INTEGER);
		if (car.engineCap != null)
			pstmt.setDouble(8, car.engineCap);
		else
			pstmt.setNull(8, Types.DOUBLE);
		if (car.info != null)
			pstmt.setString(9, car.info);
		else
			pstmt.setNull(9, Types.VARCHAR);
		if (car.city != null)
			pstmt.setString(10, car.city);
		else
			pstmt.setNull(10, Types.VARCHAR);
		if (car.colour != null)
			pstmt.setString(11, car.colour);
		else
			pstmt.setNull(11, Types.VARCHAR);
		if (car.condition != null)
			pstmt.setString(12, car.condition);
		else
			pstmt.setNull(12, Types.VARCHAR);
		if (car.date != null)
			pstmt.setDate(13, new java.sql.Date(car.date.getTime()));
		else
			pstmt.setNull(13, Types.DATE);
		if (car.similarCarYandexId != null)
			pstmt.setString(14, car.similarCarYandexId);
		else
			pstmt.setNull(14, Types.VARCHAR);
		pstmt.executeUpdate();
		pstmt.close();
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

	public void setSimilarsPrep(DisjointSets ds, Map<String, Integer> map) throws SQLException {
		log.info("Start: Updating similars... ");
		String sql = "UPDATE Car SET similarCarYandexId = ? WHERE carYandexId = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		for(int i = 0; i < size(); ++i){
			int root = ds.root(i);
			String carYandexId = Util.getKeyByValue(map, i);
			String parentCarYandexId = Util.getKeyByValue(map, root);
			pstmt.setString(1, parentCarYandexId);
			pstmt.setString(2, carYandexId);
			pstmt.executeUpdate();
		}
		pstmt.close();
		log.info("End: Updating similars... ");
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
