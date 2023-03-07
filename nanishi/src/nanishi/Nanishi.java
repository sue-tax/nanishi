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

		Analysis analysis = new Analysis();
		
		String strFilePattern = "";

        Path path = Paths.get("config.csv");
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
                D.dprint(i);
                D.dprint(data.length);
                if (data.length > 3) {
                    // 読み込んだCSVファイルの内容を出力
                    System.out.print(data[0] + ",");
                    System.out.print(data[1] + ",");
                    System.out.print(data[2] + ",");
                    System.out.println(data[3]);
                }
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
        }
        if (strFilePattern == "") {
            System.out.println("ファイル指定なし");
        	assert(false);
        }

		FileProc fileProc = new FileProc("サンプルPDF.pdf");
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
		D.dprint(strFileName);

		fileProc.renameFile(strFileName);


	}
}
