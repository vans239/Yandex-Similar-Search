import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PDFWriterCar implements WriterCar {
	private final static String FONT_LOCATION = "ARIAL.ttf";

	private Font NORMAL;
	private Font BOLD;
	private String filename;

	PDFWriterCar() throws IOException, DocumentException {
		BaseFont baseFont = BaseFont.createFont(FONT_LOCATION,
				BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		NORMAL = new Font(baseFont);
		BOLD = new Font(baseFont, Font.DEFAULTSIZE, Font.BOLD);
		filename = "content.pdf";
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename(String filename) {
		return filename;
	}

	public void create(ArrayList<Car> cars, DisjointSets ds)
			throws DocumentException, IOException {
		Document document = new Document();
		com.itextpdf.text.pdf.PdfWriter.getInstance(document, new
				FileOutputStream(filename)
		);
		document.open();
		document.add(new Paragraph("Cars: " + cars.size() + " items " + ds.unique + " unique items"));

		for (int i = 0; i < cars.size(); ++i) {
			List<Integer> list = ds.similars[i];
			if (list.size() > 0) {
				document.add(new LineSeparator(0.5f, 100, null, 0, -5));
				document.add(new Paragraph("Same: \n"));
			}
			for (Integer j : list) {
				document.add(getCarParagraph(cars.get(j)));
			}
		}
		document.close();
	}

	private Paragraph getCarParagraph(Car car) throws DocumentException {
		Paragraph p = new Paragraph();
		p.add(car.image);
		p.add(new Chunk("Id: ", BOLD));
		p.add(new Chunk(car.carYandexId, NORMAL));
		p.add(new Chunk("\nModel: ", BOLD));
		p.add(new Chunk(car.model, NORMAL));
		p.add(new Chunk("\nRetailer: ", BOLD));
		p.add(new Chunk(car.retailer, NORMAL));
		p.add(new Chunk("\nYear: ", BOLD));
		p.add(new Chunk(car.year + "", NORMAL));
		p.add(new Chunk("\nPrice: ", BOLD));
		p.add(new Chunk(car.price + "", NORMAL));
		if (car.mileage != null) {
			p.add(new Chunk("\nMileage: ", BOLD));
			p.add(new Chunk(car.mileage.toString(), NORMAL));
		}
		if (car.engineCap != null) {
			p.add(new Chunk("\nEngine capacity: ", BOLD));
			p.add(new Chunk(car.engineCap.toString(), NORMAL));
		}
		p.add(new Chunk("\nInfo: ", BOLD));
		p.add(new Chunk(car.info, NORMAL));
		if (car.date != null) {
			p.add(new Chunk("\nDate: ", BOLD));
			p.add(new Chunk(car.date.toString(), NORMAL));
		}
		p.add(new Chunk("\nCity: ", BOLD));
		p.add(new Chunk(car.city, NORMAL));
		return p;
	}
}
