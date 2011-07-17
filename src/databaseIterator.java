import java.util.Iterator;
import java.util.NoSuchElementException;

import java.sql.ResultSet;
import java.sql.SQLException;
public class DatabaseIterator implements Iterator{
	private ResultSet rs;
	private boolean hasNext = true;
	public DatabaseIterator(ResultSet rs){
		this.rs = rs;
	}
	public void remove(){
		throw new UnsupportedOperationException();
	}
	public Car next(){
		try{
			if(hasNext)
				return Database.getCar(rs);
		} catch(SQLException e){
			e.printStackTrace();
		}
		throw new NoSuchElementException();
	}

	public boolean hasNext(){
		try{
			hasNext = rs.next();
			return hasNext;
		} catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}
}
