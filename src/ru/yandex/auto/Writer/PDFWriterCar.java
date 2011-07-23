package ru.yandex.auto.writer;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.apache.log4j.Logger;
import ru.yandex.auto.Car;
import ru.yandex.auto.database.Database;
import ru.yandex.auto.util.Util;

import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.sql.SQLException;

public class PDFWriterCar implements WriterCar {
	private static Logger log = Logger.getLogger(PDFWriterCar.class);
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


	public void create(Database db)
			throws DocumentException, IOException, SQLException {
		log.info("Start: Creating output...(downloading images for pdf)... ");

		Document document = new Document();
		com.itextpdf.text.pdf.PdfWriter.getInstance(document, new
				FileOutputStream(filename)
		);
		document.open();
		document.add(new Paragraph("Cars: " + db.size() + " items, " + db.unique() + " unique items"));

		String prevSimilarCarYandexId = null;
		for (Iterator<Car> it = db.iteratorSimilarCar(); it.hasNext(); ) {
			Car car = it.next();
			if (!car.similarCarYandexId.equals(prevSimilarCarYandexId)) {
				prevSimilarCarYandexId = car.similarCarYandexId;
				document.add(new LineSeparator(0.5f, 100, null, 0, -5));
				document.add(new Paragraph("Same: \n"));
			}
			document.add(getCarParagraph(car));
		}
		document.close();
		log.info("End:Creating output...(downloading images for pdf)... ");
	}


	private Paragraph getCarParagraph(Car car) throws DocumentException {
		Paragraph p = new Paragraph();
		Image image = Util.downloadImage(car.imgUrl);
		p.add(image);
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
		if (car.colour != null) {
			p.add(new Chunk("\nColour:", BOLD));
			p.add(new Chunk(car.colour.toString(), NORMAL));
		}
		if (car.condition != null) {
			p.add(new Chunk("\nCondition:", BOLD));
			p.add(new Chunk(car.condition.toString(), NORMAL));
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
