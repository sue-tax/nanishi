/**
 *
 */
package nanishi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


	public FileProc( String strFileName ) {
		this.strFileName = strFileName;
	}

	public String readFile() {
		String strRet = null;
		File file = new File(this.strFileName);
        PDDocument document;
		try {
			document = PDDocument.load(file);
		} catch (InvalidPasswordException e) {
			strRet = String.format(
					"InvalidPassword_%s", this.strFileName);
			return strRet;
		} catch (IOException e) {
			strRet = String.format(
					"CantReadFile_%s_ファイルが読めません",
					this.strFileName);
			return strRet;
		}
        PDFTextStripper stripper;
		try {
			stripper = new PDFTextStripper();
		} catch (IOException e) {
			strRet = String.format(
					"CantReadFile_%s_ファイルが読めません",
					this.strFileName);
			return strRet;
		}
        stripper.setStartPage(1);
        stripper.setEndPage(document.getNumberOfPages());
        String text = "";
		try {
			text = stripper.getText(document);
		} catch (IOException e) {
			strRet = String.format(
					"InvalidFile_%s_ファイルが正しくない",
					this.strFileName);
			return strRet;
		}

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
        try {
			document.close();
		} catch (IOException e) {
			strRet = String.format(
					"CantCloseFile_%s_"
					+ "ファイルを閉じることができませんでした",
					this.strFileName);
			return strRet;
		}
        return strRet;
	}


	public String getText() {
		return this.strText;
	}

	public String getFileName() {
		return this.strFileName;
	}


	public String getExchFileName( Pattern patternMatch,
			String strExchFormat ) {
		D.dprint_method_start();
        File fOld = new File(this.strFileName);
        String strName = fOld.getName();
        String strFile = strName.substring(0,
        		strName.lastIndexOf("."));
        D.dprint(strFile);
        String strExch;
        Matcher m = patternMatch.matcher(strFile);
        if (m.find()) {
			D.dprint(m.group(0));
			int i = m.groupCount();
			if (i == 0) {
				strExch = strExchFormat;
			} else if (i == 1) {
				strExch = String.format(strExchFormat,
						m.group(1));
			} else if (i == 2) {
				strExch = String.format(strExchFormat,
						m.group(1), m.group(2));
			} else if (i == 3) {
				strExch = String.format(strExchFormat,
						m.group(1), m.group(2),
						m.group(3));
			} else {
				strExch = strExchFormat;
			}
        } else {
        	strExch = "";
        }
        D.dprint(strExch);
        D.dprint_method_end();
        return strExch;
	}


	public String getExchDirName( Pattern patternMatch,
			String strExchFormat ) {
		D.dprint_method_start();
        File fOld = new File(this.strFileName);
        String strDir = fOld.getParent();
        D.dprint(strDir);
        String strExch;
        Matcher m = patternMatch.matcher(strDir);
        if (m.find()) {
			D.dprint(m.group(0));
			int i = m.groupCount();
			if (i == 0) {
				strExch = strExchFormat;
			} else if (i == 1) {
				strExch = String.format(strExchFormat,
						m.group(1));
			} else if (i == 2) {
				strExch = String.format(strExchFormat,
						m.group(1), m.group(2));
			} else if (i == 3) {
				strExch = String.format(strExchFormat,
						m.group(1), m.group(2),
						m.group(3));
			} else {
				strExch = strExchFormat;
			}
        } else {
        	strExch = "";
        }
        D.dprint(strExch);
        D.dprint_method_end();
        return strExch;
	}



	public boolean renameFile( String strNewFile ) {
        File fOld = new File(this.strFileName);
        String strName = fOld.getName();
        String strExt = strName.substring(
        		strName.lastIndexOf("."));
        String strDir = fOld.getParent();
        String strFile = strDir + "\\" + strNewFile + strExt;

        D.dprint(strFile);
        File fNew = new File(strFile);
        boolean flag = true;
		try {
			flag = fOld.renameTo(fNew);
		} catch (Exception e) {
			flag = false;
		}
        D.dprint(flag);
        return flag;
	}

	public boolean copyFile( String strCopyFile ) {
        File fOld = new File(this.strFileName);
        String strName = fOld.getName();
        String strExt = strName.substring(
        		strName.lastIndexOf("."));
        String strDir = fOld.getParent();
        String strFile = strDir + "\\" + strCopyFile + strExt;
		Path pOld = Paths.get(this.strFileName);
		Path pCopy = Paths.get(strFile);
		boolean flag = true;
		try{
			Files.copy(pOld, pCopy);
		}catch(IOException e){
			flag = false;
		}
		D.dprint(flag);
		return flag;
	}


	public String modifyFileName( String strOriginal ) {
		String strModify = strOriginal.replaceAll(
				"(\\|/|:|,|;|\\*|\\?|\"|,|>|\\|)", "_");
		return strModify;
	}
}
