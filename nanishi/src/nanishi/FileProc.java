/**
 *
 */
package nanishi;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * @author sue-t
 *
 */
public class FileProc {

	private String strFileName;

	public FileProc( String strFileName ) {
		this.strFileName = strFileName;
		File file = new File(this.strFileName);
        PDDocument document = PDDocument.load(file);

//        System.out.println("総ページ数：" + document.getNumberOfPages());

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(document.getNumberOfPages());
        String result = stripper.getText(document);

	}
}
