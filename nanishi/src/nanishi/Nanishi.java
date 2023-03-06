/**
 *
 */
package nanishi;

import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;


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

		FileProc fileProc = new FileProc("サンプルPDF.pdf");
		String text = fileProc.getText();
		System.out.println(text);

		Analysis analysis = new Analysis();
		analysis.createMap(2);
		analysis.addMapElement(2,
				"確定申告書", "01_確定申告書");
		analysis.createMap(3);
		analysis.addMapElement(3,
				"(高|髙)橋(\\s*)直美", ",%1$橋%2d直美");


		Map<Integer, String> map = analysis.getStringList(text);
		String str2 = map.get(2);
		System.out.println(str2);
		String str3 = map.get(3);
		System.out.println(str3);

//		File file = new File("サンプルPDF.pdf");
//
//        PDDocument document = PDDocument.load(file);
//        System.out.println("総ページ数：" + document.getNumberOfPages());
//
//        PDFTextStripper stripper = new PDFTextStripper();
//        stripper.setStartPage(1);
//        stripper.setEndPage(document.getNumberOfPages());
//        String result = stripper.getText(document);
//        System.out.println(result);


	}
}
