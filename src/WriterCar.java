import java.util.Map;

public interface WriterCar {
	void create(Database db, DisjointSets ds, Map<String, Integer> map)
			throws Exception;
}
