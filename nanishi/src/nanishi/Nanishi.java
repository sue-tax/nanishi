/**
 *
 */
package nanishi;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;


/**
 * @author sue-t
 *
 */
public class Nanishi {

	static boolean flagRename = true;

	static Analysis analysis;

	static int indexDatetime = 0;
	static String strDatetime;
	static String strDatetimeFormat;


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

		analysis = new Analysis();

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
                		D.dprint("*"+data[2]+"*");
                		if (! data[2].equals("")) {
	                		analysis.addMapElement(index,
	                				data[2], data[3]);
                		} else {
                			// 日時、連番、元ファイルの処理
                			if (data[3].matches("(m|c|a|p)")) {
                				// ファイル更新日時等
                				indexDatetime = index;
                				strDatetime = data[3];
                				strDatetimeFormat = data[4];
                			}
                		}
                	} else {
                		// エラー
                	}
                } else if (data[0].equals("0")) {
                	if (data[1].equals("filename")) {
                		D.dprint("filename");
                		D.dprint(data[2]);
                		strFilePattern = data[2];
                	} else if (data[1].equals("option")) {
                		if (data[2].equals("rename")) {
                			flagRename = true;
	                	} else if (data[2].equals("copy")) {
	                		flagRename = false;
	                	}
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

        D.dprint(args);
        D.dprint(args[1]);
		String strFileOriginal;
		for (int iFile=1; iFile<args.length; iFile++) {
			strFileOriginal = args[iFile];
			System.out.println("処理ファイル:" + strFileOriginal);

			FileProc fileProc = new FileProc(strFileOriginal);
			String text = fileProc.getText();

			System.out.println("PDFファイル内のテキスト");
			System.out.println("==============================");
			System.out.println(text);
			System.out.println("==============================");
			Map<Integer, String> map = analysis.getStringList(
					text);
			String aStr[] = new String[10];
			for (int i=1; i<10; i++) {
				aStr[i] = map.get(i);
			}
			if (indexDatetime != 0) {
				if (strDatetime.equals("m")) {
					File file = new File(strFileOriginal);
					BasicFileAttributes attrs
							= Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				    FileTime time = attrs.creationTime();
				    D.dprint(time);
				    SimpleDateFormat simpleDateFormat
				    		= new SimpleDateFormat(strDatetimeFormat);
				    aStr[indexDatetime]
				    		= simpleDateFormat.format(
				    				new Date(time.toMillis()));
				} else if (strDatetime.equals("p")){
			        LocalDateTime nowDate
			        		= LocalDateTime.now();
			        D.dprint(nowDate);
			        D.dprint(strDatetimeFormat);
			        DateTimeFormatter dtf1 =
			            DateTimeFormatter.ofPattern(strDatetimeFormat);
			        aStr[indexDatetime] = dtf1.format(nowDate);
				}
			}

			String strFileName = String.format(strFilePattern,
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
