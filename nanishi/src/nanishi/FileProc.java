/**
 *
 */
package nanishi;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;


// エラー処理方法は、未確定

/**
 * @author sue-t
 *
 */
public class FileProc {

	private String strFileName;
	
	private String strText;
	
	
	public FileProc( String strFileName ) throws InvalidPasswordException, IOException {
		this.strFileName = strFileName;
		File file = new File(this.strFileName);
        PDDocument document = PDDocument.load(file);

//        System.out.println("総ページ数：" + document.getNumberOfPages());

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(document.getNumberOfPages());
        this.strText = stripper.getText(document);
	}
	
	public String getText() {
		return this.strText;
	}
	
	public void renameFile( String strNewFile ) {
		// 未作成
	}
}
