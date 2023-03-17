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
//	private String strFileMatchPattern;
	private Pattern patternFile;
	private String strFileExchFormat;

	private int indexDirName = 0;
//	private String strDirMatchPattern;
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
			strRet = String.format(
					"InvalidFileName_Paths.get_%s_"
					+ "ファイル名が正しくない",
					strFileConfig);
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
			strRet = String.format(
					"CannotReadFile_Files.readAllLines_%s"
					+ "ファイルが読めませんでした",
					strFileConfig);
			D.dprint(strRet);
			D.dprint_method_end();
			return strRet;
		}
//        D.dprint(lines);
        for (int iLine = 0; iLine < lines.size(); iLine++) {
        	D.dprint(lines.get(iLine));
            String[] aData = lines.get(iLine).split(",");
            D.dprint(aData[0]);
            if (aData[0].matches("[1-9]")) {
            	strRet = readMatch(iLine, aData);
        		if (strRet != null) {
        			D.dprint(strRet);
        			D.dprint_method_end();
        			return strRet;
        		}
            } else if (aData[0].equals("0")) {
            	if (aData.length < 2) {
        			strRet = String.format(ERROR_TOOFEW_COLUMN,
        					iLine+1, 2);
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
            	} else {
            		strRet = String.format(
            				"Invalid_Line-%d_%s_"
            				+ "２列目が正しくない",
            				iLine+1, aData[1]);
        			D.dprint(strRet);
        			D.dprint_method_end();
        			return strRet;
            	}
            } else {
        		strRet = String.format(
        				"InvalidMatchNumber_Line-%d_%s_"
        				+ "１列目のマッチ番号が正しくない",
        				iLine+1, aData[0]);
        		continue;
//    			D.dprint(strRet);
//    			D.dprint_method_end();
//    			return strRet;
            }
        }
        if (strFileFormat == null) {
    		strRet = String.format(
    				"NoFileFormat_"
    				+ "変更ファイル名フォーマットの指定がない");
			D.dprint(strRet);
			D.dprint_method_end();
			return strRet;
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
    	D.dprint("*"+aData[3]+"*");
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
//				patternFile = Pattern.compile(aData[4]);
				strDatetimeFormat = aData[4];
			} else if (aData[2].equals("f")) {
				D.dprint("f");
				D.dprint(index);
				indexFileName = index;
//				strFileMatchPattern = aData[4];
				D.dprint(aData[4]);
				patternFile = Pattern.compile(aData[4]);
				strFileExchFormat = aData[5];
				D.dprint(aData[5]);
			} else if (aData[2].equals("d")) {
				indexDirName = index;
//				strDirMatchPattern = aData[4];
				patternDir = Pattern.compile(aData[4]);
				strDirExchFormat = aData[5];
			} else if (aData[2].equals("#")) {
				indexNumbering = index;
			} else {
	    		strRet = String.format(
	    				"InvalidMatch_Line-%d_%s_"
	    				+ "マッチ指定が正しくない",
	    				iLine+1, aData[2]);
	    		D.dprint(strRet);
	    		D.dprint_method_end();
				return strRet;
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
    				iLine+1, aData[2]);
			return strRet;
    	}
		this.strFileFormat = aData[2];
		return strRet;
	}


	public String getFileFormat() {
		return strFileFormat;
	}

	public boolean getFlagRename() {
		return flagRename;
	}


	public String[] getMatchString( int iFile, String strText,
			FileProc fileProc ) {
		D.dprint_method_start();
		String strRet = null;
		String strFileOrginal = fileProc.getFileName();
		File file = new File(strFileOrginal);
		Map<Integer, String> map = analysis.getStringList(
				strText);
		String aStr[] = new String[10];
		for (int i=1; i<10; i++) {
			aStr[i] = map.get(i);
		}
		if (indexDatetime != 0) {
			if (strDatetime.equals("m")) {
				BasicFileAttributes attrs;
				try {
					attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				} catch (IOException e) {
					strRet = String.format(
							"CannotReadFile_Files.readAttributes_%s"
							+ "ファイルが読めませんでした",
							strFileOrginal);
					D.dprint(strRet);
					aStr[0] = strRet;
					D.dprint_method_end();
					return aStr;
				}
			    FileTime time = attrs.lastModifiedTime();
			    D.dprint(time);
			    SimpleDateFormat simpleDateFormat;
				try {
					simpleDateFormat = new SimpleDateFormat(
							strDatetimeFormat);
				} catch (Exception e) {
					strRet = String.format(
							"InvalidDatetimeFormat_%s"
							+ "日時のフォーマットが間違っています",
							strDatetimeFormat);
					D.dprint(strRet);
					aStr[0] = strRet;
					D.dprint_method_end();
					return aStr;
				}
			    aStr[indexDatetime]
			    		= simpleDateFormat.format(
			    				new Date(time.toMillis()));
			} else if (strDatetime.equals("c")) {
				BasicFileAttributes attrs;
				try {
					attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				} catch (IOException e) {
					strRet = String.format(
							"CannotReadFile_Files.readAttributes_%s"
							+ "ファイルが読めませんでした",
							strFileOrginal);
					D.dprint(strRet);
					aStr[0] = strRet;
					D.dprint_method_end();
					return aStr;
				}
			    FileTime time = attrs.creationTime();
			    D.dprint(time);
			    SimpleDateFormat simpleDateFormat;
				try {
					simpleDateFormat = new SimpleDateFormat(
							strDatetimeFormat);
				} catch (Exception e) {
					strRet = String.format(
							"InvalidDatetimeFormat_%s"
							+ "日時のフォーマットが間違っています",
							strDatetimeFormat);
					D.dprint(strRet);
					aStr[0] = strRet;
					D.dprint_method_end();
					return aStr;
				}
			    aStr[indexDatetime]
			    		= simpleDateFormat.format(
			    				new Date(time.toMillis()));
			}  else if (strDatetime.equals("a")) {
				BasicFileAttributes attrs;
				try {
					attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				} catch (IOException e) {
					strRet = String.format(
							"CannotReadFile_Files.readAttributes_%s"
							+ "ファイルが読めませんでした",
							strFileOrginal);
					D.dprint(strRet);
					aStr[0] = strRet;
					D.dprint_method_end();
					return aStr;
				}
			    FileTime time = attrs.lastAccessTime();
			    D.dprint(time);
			    SimpleDateFormat simpleDateFormat;
				try {
					simpleDateFormat = new SimpleDateFormat(
							strDatetimeFormat);
				} catch (Exception e) {
					strRet = String.format(
							"InvalidDatetimeFormat_%s"
							+ "日時のフォーマットが間違っています",
							strDatetimeFormat);
					D.dprint(strRet);
					aStr[0] = strRet;
					D.dprint_method_end();
					return aStr;
				}
			    aStr[indexDatetime]
			    		= simpleDateFormat.format(
			    				new Date(time.toMillis()));
			} else if (strDatetime.equals("p")){
		        LocalDateTime nowDate
		        		= LocalDateTime.now();
		        D.dprint(nowDate);
		        D.dprint(strDatetimeFormat);
		        DateTimeFormatter dtf1;
				try {
					dtf1 = DateTimeFormatter.ofPattern(
							strDatetimeFormat);
				} catch (Exception e) {
					strRet = String.format(
							"InvalidDatetimeFormat_%s"
							+ "日時のフォーマットが間違っています",
							strDatetimeFormat);
					D.dprint(strRet);
					aStr[0] = strRet;
					D.dprint_method_end();
					return aStr;
				}
		        aStr[indexDatetime] = dtf1.format(nowDate);
			}
		}
		if (indexFileName != 0) {
			aStr[indexFileName] = fileProc
					.getExchFileName(patternFile,
					strFileExchFormat);
		}
		if (indexDirName != 0) {
			aStr[indexDirName] = fileProc
					.getExchDirName(patternDir,
					strDirExchFormat);
		}
		if (indexNumbering != 0) {
			aStr[indexNumbering] = Integer.toString(iFile);
		}
		D.dprint(aStr);
		D.dprint_method_end();
		return aStr;
	}
}
