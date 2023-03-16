/**
 * 設定ファイルの処理
 * 設定ファイルは、utf-8で記載されたCSVファイル
 */

/*
0, Version, 0
0, Option, Rename or Copy
0, FilenameFormat, %2$s_%1$2
【マッチ番号】,【マッチ名称】,【変換文字列フォーマット】,【マッチパターン】,【マッチオプション】
 */
/* プロトタイプと順番が入れ替わっている点に注意 */

package nanishi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author sue-t
 *
 */
public class ConfigProc {

	static final String VERSION = "Version";

	static final String OPTION = "Option";
	static final String OPTION_RENAME = "Rename";
	static final String OPTION_COPY = "Copy";

	static final String FILENAME_FORMAT = "FilenameFormat";

	static final String ERROR_TOOFEW_COLUMN
			= "TooFewColumn_Line-%d_not_less_than_%d_"
			+ "その行の項目数が少ない";
	/**
	 * 設定ファイルのファイル名
	 */
	private String strFileConfig;

	/**
	 * ファイル変更名のフォーマット文字列
	 * formatメソッドで利用する文字列で指定します。
	 * 例：%2$s_%1$s
	 */
	private String strFileFormat;

	/**
	 * ファイル名変更をリネームで行なうか、コピーして行なうか
	 */
	private boolean flagRename = true;


	private Analysis analysis;

	private int indexDatetime = 0;
	private String strDatetime;
	private String strDatetimeFormat;

	private int indexFileName = 0;
	private String strFileMatchPattern;
	private Pattern patternFile;
	private String strFileExchFormat;

	private int indexDirName = 0;
	private String strDirMatchPattern;
	private Pattern patternDir;
	private String strDirExchFormat;

	private int indexNumbering;


	public ConfigProc( String strFileConfig, Analysis analysis ) {
		this.strFileConfig = strFileConfig;
		this.analysis = analysis;
	}

	public String readConfig() {
		D.dprint_method_start();
		String strRet = null;
		Path path;
		try {
			path = Paths.get(strFileConfig);
		} catch (Exception e1) {
			strRet = "InvalidFileName_Paths.get_"
					+"ファイル名が正しくない";
			D.dprint(strRet);
			D.dprint_method_end();
			return strRet;
		}
    	D.dprint(path);
        // CSVファイルの読み込み
        List<String> lines;
		try {
			lines = Files.readAllLines(
					path, Charset.forName("UTF-8"));
		} catch (IOException e1) {
			strRet = "CannotReadFile_Files.readAllLines_"
					+"ファイルが読めませんでした";
			D.dprint(strRet);
			D.dprint_method_end();
			return strRet;
		}
        D.dprint(lines);
        for (int iLine = 0; iLine < lines.size(); iLine++) {
            String[] aData = lines.get(iLine).split(",");
            if (aData[0].matches("[1-9]")) {
            	Integer index = Integer.valueOf(aData[0]);
            	D.dprint(index);
            	if (aData.length >= 4) {
            		D.dprint("*"+aData[2]+"*");
            		if (! aData[2].equals("")) {
            			if (aData[2].equals("+")) {
            				// マッチパターンは
            				// 変換文字列フォーマットと一致
	                		analysis.addMapElement(index,
	                				aData[3], aData[3]);
            			} else if (aData[2].equals("*")) {
            				// マッチパターンは
            				// 変換文字列フォーマットの
            				// 各文字の間にスペース等を挿入
            				StringBuilder stringBuilder = new StringBuilder();
            				for (int j=0; j<aData[3].length(); j++) {
            					stringBuilder.append(aData[3].charAt(j));
            					stringBuilder.append("(\\s|　)*");
            				}
            				String strMatch = stringBuilder.toString();
            				D.dprint(strMatch);
	                		analysis.addMapElement(index,
	                				strMatch, aData[3]);
            			} else {
	                		analysis.addMapElement(index,
	                				aData[2], aData[3]);
            			}
            		} else {
            			// 日時、連番、元ファイルの処理
            			if (aData[3].matches("(m|c|a|p)")) {
            				// ファイル更新日時等
            				indexDatetime = index;
            				strDatetime = aData[3];
            				patternFile = Pattern.compile(aData[3]);
            				strDatetimeFormat = aData[4];
            			} else if (aData[3].equals("f")) {
            				D.dprint("f");
            				D.dprint(index);
            				indexFileName = index;
            				strFileMatchPattern = aData[4];
            				D.dprint(aData[4]);
            				patternFile = Pattern.compile(aData[4]);
            				strFileExchFormat = aData[5];
            				D.dprint(aData[5]);
            			} else if (aData[3].equals("d")) {
            				indexDirName = index;
            				strDirMatchPattern = aData[4];
            				patternDir = Pattern.compile(aData[4]);
            				strDirExchFormat = aData[5];
            			} else if (aData[3].equals("#")) {
            				indexNumbering = index;
            			}
            		}
            	} else {
            		// エラー
            	}
            } else if (aData[0].equals("0")) {
            	if (aData.length < 2) {
        			strRet = String.format(ERROR_TOOFEW_COLUMN,
        					iLine, 2);
        			D.dprint(strRet);
        			D.dprint_method_end();
        			return strRet;
            	}
            	if (aData[1].equals(FILENAME_FORMAT)) {
            		strRet = readFormat(iLine, aData);
            		if (strRet != null) {
            			D.dprint(strRet);
            			D.dprint_method_end();
            			return strRet;
            		}
            	} else if (aData[1].equals(OPTION)) {
            		strRet = readOption(iLine, aData);
            		if (strRet != null) {
            			D.dprint(strRet);
            			D.dprint_method_end();
            			return strRet;
            		}
            	}
            } else {
            	// エラー
            }

        }
        D.dprint(strRet);
        D.dprint_method_end();
        return strRet;
	}


	private String readMatch( int iLine, String[] aData ) {
		D.dprint_method_start();
		String strRet = null;
		if (aData.length < 4) {
			strRet = String.format(ERROR_TOOFEW_COLUMN,
					iLine, 4);
			return strRet;
		}
		Integer index = Integer.valueOf(aData[0]);
    	D.dprint(index);
    	// aData[1]は、処理に関係ない
    	D.dprint("*"+aData[2]+"*");
		if (! aData[3].equals("")) {	// PROTOTYPEと違う
			if (aData[3].equals("+")) {
				// マッチパターンは
				// 変換文字列フォーマットと一致
        		analysis.addMapElement(index,
        				aData[2], aData[2]);
			} else if (aData[3].equals("*")) {
				// マッチパターンは
				// 変換文字列フォーマットの
				// 各文字の間にスペース等を挿入
				StringBuilder stringBuilder = new StringBuilder();
				for (int j=0; j<aData[2].length(); j++) {
					stringBuilder.append(aData[2].charAt(j));
					stringBuilder.append("(\\s|　)*");
				}
				String strMatch = stringBuilder.toString();
				D.dprint(strMatch);
        		analysis.addMapElement(index,
        				strMatch, aData[2]);
			} else {
        		analysis.addMapElement(index,
        				aData[3], aData[2]);
			}
		} else {
			// 日時、連番、元ファイルの処理
			if (aData[2].matches("(m|c|a|p)")) {
				// ファイル更新日時等
				indexDatetime = index;
				strDatetime = aData[2];
				patternFile = Pattern.compile(aData[4]);
				strDatetimeFormat = aData[4];
			} else if (aData[2].equals("f")) {
				D.dprint("f");
				D.dprint(index);
				indexFileName = index;
				strFileMatchPattern = aData[4];
				D.dprint(aData[4]);
				patternFile = Pattern.compile(aData[4]);
				strFileExchFormat = aData[5];
				D.dprint(aData[5]);
			} else if (aData[2].equals("d")) {
				indexDirName = index;
				strDirMatchPattern = aData[4];
				patternDir = Pattern.compile(aData[4]);
				strDirExchFormat = aData[5];
			} else if (aData[2].equals("#")) {
				indexNumbering = index;
			}
		}

		D.dprint(strRet);
		D.dprint_method_end();
		return strRet;
	}


	private String readFormat( int iLine, String[] aData ) {
		String strRet = null;
		if (aData.length < 3) {
			strRet = String.format(ERROR_TOOFEW_COLUMN,
					iLine, 3);
			return strRet;
		}
		this.strFileFormat = aData[2];
		return strRet;
	}

	private String readOption( int iLine, String[] aData ) {
		String strRet = null;
		if (aData.length < 3) {
			strRet = String.format(ERROR_TOOFEW_COLUMN,
					iLine, 3);
			return strRet;
		}
		if (aData[2].equals(OPTION_RENAME)) {
			this.flagRename = true;
    	} else if (aData[2].equals(OPTION_COPY)) {
    		this.flagRename = false;
    	} else {
    		strRet = String.format(
    				"InvalidOption_Line-%d_%s_"
    				+ "オプションが正しくない",
    				iLine, aData[2]);
			return strRet;
    	}
		this.strFileFormat = aData[2];
		return strRet;
	}


}
