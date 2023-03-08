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
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(document.getNumberOfPages());
        String text = stripper.getText(document);

        // 参考 https://www.informe.co.jp/useful/character/character27/
//        もっとも一般的といえる半角（欧文）スペースは、ASCIIコードで20、ユニコードではU+0020という
//        コードポイントが与えられています。
//        全角スペースはU+3000（シフトJISでは0x8140）です。
//
//        この2つの基本的なスペースのほかに、
//        ユニコードでは、
//        ENスペース（U+2002）、EMスペース（U+2003）、
//        EMの3分の1（U+2004）、EMの4分の1（U+2005）、
//        EMの6分の1（U+2006）、フィギュアスペース（U+2007）、
//        約物幅のスペース（U+2008）、EMの5分の1（U+2009）、
//        極細スペース（U+200A）、幅がゼロのスペース（U+200B）
//        といったスペースが定義されています。
        text = text.replaceAll("(\\u2002|\\u2003)", "　");
        this.strText = text.replaceAll(
        		"(\\u2004|\\u2005|\\u2006|\\u2007)",
        		" ");
        document.close();
	}

	public String getText() {
		return this.strText;
	}

	public void renameFile( String strNewFile ) {
        File fOld = new File(this.strFileName);
        String strName = fOld.getName();
        String strExt = strName.substring(
        		strName.lastIndexOf("."));
        String strDir = fOld.getParent();
        String strFile = strDir + "\\" + strNewFile + strExt;

        D.dprint(strFile);
        File fNew = new File(strFile);
        boolean flag = fOld.renameTo(fNew);
        // TODO エラーメッセージ
        D.dprint(flag);
	}
}
