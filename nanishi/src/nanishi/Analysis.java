package nanishi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析用データ
 * @author sue-t
 *
 */
public class Analysis {

	class Item {
		private String strRegex;	// (高|髙)橋\s*直美
		private String strName;		// (1)橋直美
		private Pattern pattern;

		public Item( String strRegex, String strName ) {
			this.strRegex = strRegex;
			this.strName = strName;
			this.pattern = null;
		}

		public boolean compile() {
			try {
				this.pattern = Pattern.compile(strRegex);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		
		public String match( String text ) {
			Matcher m = this.pattern.matcher(text);
			if (m.find()) {
				// this.strName
				// 固定文字列の場合もあるが、
				// (1)abc(2)のようなパターンもある
				// (1),(2)は、正規表現のm.group(1),group(2)
				
			} else {
				return null;
			}
		}

	}

	private Map<Integer, List<Item>> mapAnal;

	public Analysis() {
		mapAnal = new HashMap<>();
	}

/**
ファイル名, %2$s_%3$s_%1$yyyymmdd_%4$d_%5$d

日時、書類名などは参考
必要なのは、最初の番号のみ

1,日時,,p mはファイルの作成日　pは処理日　　　　これには対応不要

2,書類名,法人税申告書,1_法人税申告書

2,書類名,納税額一覧表,9_納税額一覧表

3,顧問先名,佐藤\s*太郎,佐藤太郎

3,顧問先名,田中工業,田中工業

3,顧問先名,(高|髙)橋\s*直美,(1)橋直美

4,連番,,#   これには対応不要

5,金額,(0-9,)*円,(1) 1は(0-9,)を示す、正規表現の処理で対応
 */

	/**
	 *
	 * @param intMap 
	 */
	public void createMap( Integer intMap ) {
		List<Item>emptyList = new ArrayList<Item>();
		mapAnal.put(intMap, emptyList);
	}

	public boolean addMapElement( Integer intMap,
			String strRegex, String strName ) {
		Item item = new Item(strRegex, strName);
		boolean bSuccess = item.compile();
		if( ! bSuccess ) {
			return false;
		}
		List<Item>itemList = mapAnal.get(intMap);
		itemList.add(item);
		mapAnal.put(intMap, itemList);
		return true;
	}
	
	public Map<Integer, String> getStringList( String text ) {
		Map<Integer, String> map = new HashMap<>();
		mapAnal.forEach((k, itemList) -> {
//		    System.out.println(k);
//		    System.out.println(v);
			Iterator<Item> iter = itemList.iterator();
			while(iter.hasNext())
			{
			    Item item = (Item)iter.next();
			    // 正規表現で合致するのを探す
			    String strMatch = item.match(text);
			    // 合致したら、変換後の文字列を作成
			    if (strMatch != null) {
			    	map.put(k, strMatch);
			    }
			    // mapに追加する
			}
		});
		return map;
	}

}
