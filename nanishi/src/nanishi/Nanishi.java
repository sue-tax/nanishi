/**
 *
 */
package nanishi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

		//java -jar C:\Users\xxxxx\AppData\Roaming\Microsoft\Windows\SendTo\nanishi.jar %1

		String strFileConfig = args[0];
		System.out.println("設定ファイル:" + strFileConfig);
		String strFileOriginal = args[1];
		System.out.println("処理ファイル:" + strFileOriginal);
		// 将来的には複数ファイルを同時処理

		Analysis analysis = new Analysis();

		String strFilePattern = "";

        Path path = Paths.get(strFileConfig);
        try {
            // CSVファイルの読み込み
        	D.dprint(path);
//            List<String> lines = Files.readAllLines(
//            		path, Charset.forName("Shift-JIS"));
            List<String> lines = Files.readAllLines(
            		path, Charset.forName("UTF-8"));
            D.dprint(lines);
            for (int i = 0; i < lines.size(); i++) {
                String[] data = lines.get(i).split(",");
//                D.dprint(i);
//                D.dprint(data.length);
                if (data[0].matches("[1-9]")) {
                	Integer index = Integer.valueOf(data[0]);
                	D.dprint(index);
                	if (data.length >= 4) {
                		analysis.addMapElement(index,
                				data[2], data[3]);
                	} else {
                		// エラー
                	}
                } else if (data[0].equals("0")) {
                	if (data[1].equals("filename")) {
                		D.dprint("filename");
                		D.dprint(data[2]);
                		strFilePattern = data[2];
                	}
                } else {
                	// エラー
                }

            }
        } catch (IOException e) {
        	D.dprint(e);
            System.out.println("ファイル読み込みに失敗");
            assert(false);
        }
        if (strFilePattern == "") {
            System.out.println("ファイル指定なし");
        	assert(false);
        }

		FileProc fileProc = new FileProc(strFileOriginal);
		String text = fileProc.getText();

		Map<Integer, String> map = analysis.getStringList(
				text);
		String aStr[] = new String[10];
		for (int i=1; i<10; i++) {
			aStr[i] = map.get(i);
		}

		String strFileName = String.format(strFilePattern,
				aStr[1], aStr[2], aStr[3], aStr[4],
				aStr[5], aStr[6], aStr[7], aStr[8],
				aStr[9]);
		System.out.println("ファイル名:" + strFileName);

		boolean flag = fileProc.renameFile(strFileName);
		if (! flag) {
			System.out.println("ファイル名変更に失敗しました");
		}
	}
}
