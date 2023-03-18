/**
 *
 */
package nanishi;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;




/**
 * @author sue-t
 *
 */
public class Nanishi {

	static final float VERSION = 0.10f;

	
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

		//java -jar C:\Users\xxxxx\AppData\Roaming\Microsoft\Windows\SendTo\nanishi.jar %1

		System.out.println(String.format("Nanishi version %.2f",
				VERSION));
		
		String strFileConfig = args[0];
		System.out.println("設定ファイル:" + strFileConfig);

		Analysis analysis = new Analysis();
		ConfigProc configProc = new ConfigProc(
				strFileConfig, analysis);
		String strRet = configProc.readConfig();
		if (strRet != null) {
			System.out.println(strRet);
			return;
		}
		Float configVersion = configProc.getVersion();
		if (configVersion == 0.0f) {
			System.out.println(String.format(
					"設定ファイルのバージョン%.2fが低い",
					configVersion));
			System.out.println(
					"設定ファイルの内容を確認してください");
			return;
		}

        D.dprint(args);
        D.dprint(args[1]);
		String strFileFormat = configProc.getFileFormat();
		boolean flagRename = configProc.getFlagRename();
		for (int iFile=1; iFile<args.length; iFile++) {
			String strFileOriginal = args[iFile];
			System.out.println("処理ファイル:" + strFileOriginal);

			FileProc fileProc = new FileProc(strFileOriginal);
			strRet = fileProc.readFile();
			if (strRet != null) {
				System.out.println(strRet);
				continue;
			}
			String strText = fileProc.getText();

			System.out.println("PDFファイル内のテキスト");
			System.out.println("==============================");
			System.out.println(strText);
			System.out.println("==============================");

			String aStr[] = configProc.getMatchString(
					iFile, strText, fileProc);

			if (aStr[0] != null) {
				System.out.println(aStr[0]);
				continue;
			}
			String strFileName = String.format(strFileFormat,
					aStr[1], aStr[2], aStr[3], aStr[4],
					aStr[5], aStr[6], aStr[7], aStr[8],
					aStr[9]);
			strFileName = fileProc.modifyFileName(strFileName);
			System.out.println("ファイル名:" + strFileName);

			if (flagRename) {
				boolean flag = fileProc.renameFile(strFileName);
				if (! flag) {
					System.out.println("ファイル名変更に失敗しました");
				}
			} else {
				boolean flag = fileProc.copyFile(strFileName);
				if (! flag) {
					System.out.println("ファイルのコピーに失敗しました");
				}
			}
		}
	}
}
