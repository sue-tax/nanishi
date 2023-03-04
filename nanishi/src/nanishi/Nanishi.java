/**
 *
 */
package nanishi;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;


/**
 * @author sue-t
 *
 */
public class Nanishi {

	public static void main( String[] args ) throws InvalidPasswordException, IOException {

		// http://dalmore.blog7.fc2.com/blog-entry-191.html

		// 右クリックで、
		// args[1] にファイル名 0からかも
		// 複数ファイルも可

		// Windows + R
		// shell:sendto
		// ファイルコピー
		// 設定ファイルも？
		// バッチファイルを作って、設定ファイルを切替える？

		File file = new File("FoNEX.pdf");

        PDDocument document = PDDocument.load(file);
        System.out.println("総ページ数：" + document.getNumberOfPages());

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(document.getNumberOfPages());
        String result = stripper.getText(document);
        System.out.println(result);
	}
}
