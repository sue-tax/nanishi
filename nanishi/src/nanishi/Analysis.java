package nanishi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 解析用データ
 * @author sue-t
 *
 */
public class Analysis {

	class Item {
		private String strRegex;	// 前田( |　)*敦子
		private String strName;		// 前田敦子
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

	}

	private Map<String, List<Item>> mapAnal;

	public Analysis() {
		mapAnal = new HashMap<>();
	}

	/**
	 *
	 * @param strMap "顧問先名", "書類名"などを想定
	 */
	public void createMap( String strMap ) {
		List<Item>emptyList = new ArrayList<Item>();
		mapAnal.put(strMap, emptyList);
	}

	public boolean addMapElement( String strMap,
			String strRegex, String strName ) {
		Item item = new Item(strRegex, strName);
		boolean bSuccess = item.compile();
		if( ! bSuccess ) {
			return false;
		}
		List<Item>itemList = mapAnal.get(strMap);
		itemList.add(item);
		mapAnal.put(strMap, itemList);
		return true;
	}

}
