package nanishi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
		private String strName;		// %1$s橋直美
		private Pattern pattern;
		private String strExch;

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

		public int match( String text ) {
			D.dprint_method_start();
			D.dprint(this.strRegex);
			D.dprint(this.strName);
			int start = Integer.MAX_VALUE;
			Matcher m = this.pattern.matcher(text);
			if (m.find()) {
				D.dprint(m.group(0));
				int i = m.groupCount();
				if (i == 0) {
					strExch = this.strName;
				} else if (i == 1) {
					strExch = String.format(this.strName,
							m.group(1));
				} else if (i == 2) {
					strExch = String.format(this.strName,
							m.group(1), m.group(2));
				} else {
					strExch = this.strName;
				}
				start = m.start();
			} else {
				strExch = null;
			}
			D.dprint(start);
			D.dprint_method_end();
			return start;
		}

		public String getExch() {
			return strExch;
		}

	}

	private Map<Integer, List<Item>> mapAnal;

	private Set<Integer> setAnal;

	public Analysis() {
		mapAnal = new HashMap<Integer, List<Item>>();
		setAnal = new HashSet<Integer>();
	}

	public boolean addMapElement( Integer intMap,
			String strRegex, String strName ) {
		D.dprint_method_start();
		D.dprint(intMap);
		D.dprint(strRegex);
		D.dprint(strName);
		Item item = new Item(strRegex, strName);
		boolean bSuccess = item.compile();
		if( ! bSuccess ) {
			D.dprint(false);
			D.dprint_method_end();
			return false;
		}
		if (! setAnal.contains(intMap)) {
			List<Item>itemList = new ArrayList<Item>();
			itemList.add(item);
			mapAnal.put(intMap, itemList);
			setAnal.add(intMap);
		} else {
			List<Item>itemList = mapAnal.get(intMap);
			itemList.add(item);
			mapAnal.put(intMap, itemList);
		}
		D.dprint(true);
		D.dprint_method_end();
		return true;
	}

	public Map<Integer, String> getStringList( String text ) {
		D.dprint_method_start();
		D.dprint(text);
		Map<Integer, String> map = new HashMap<>();
		mapAnal.forEach((k, itemList) -> {
			D.dprint(k);
			D.dprint(itemList);
			int minIndex = Integer.MAX_VALUE;
			Item minItem = null;
			int index;
			Iterator<Item> iter = itemList.iterator();
			while(iter.hasNext())
			{
			    Item item = (Item)iter.next();
			    // 正規表現で合致するのを探す
			    index = item.match(text);
			    if (index < minIndex) {
			    	minIndex = index;
			    	minItem = item;
			    }
			}
			if (minItem != null) {
				// 合致した中で、前にあるものを採用
				String strMatch = minItem.getExch();
		    	map.put(k, strMatch);
		    }
		});
		D.dprint_method_end();
		return map;
	}

}
