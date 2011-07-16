import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

public interface WriterCar {
	void create(Database db, DisjointSets ds, Map<String, Integer> map)
			throws Exception;
}
