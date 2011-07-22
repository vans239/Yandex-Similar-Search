import com.itextpdf.text.Image;
import com.itextpdf.text.Jpeg;

import java.net.URL;
import java.util.Map;

public class Util {
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Map.Entry<T, E> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static Image downloadImage(String imgUrl) {
		Image image = null;
		if (imgUrl == null)
			return null;
		try {
			image = new Jpeg(new URL(imgUrl));

		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return image;
	}
}
