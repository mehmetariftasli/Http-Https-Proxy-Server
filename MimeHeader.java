package networkProject;

import java.util.*;

public class MimeHeader extends HashMap<String, String> {

	public MimeHeader() {

	}

	public MimeHeader(String s) {
		parse(s);
	}

	private void parse(String data) {
		StringTokenizer st = new StringTokenizer(data, "\r\n");

		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			int myIndex = s.indexOf(":");
			String key = s.substring(0, myIndex);
			String value = s.substring(myIndex + 2);
			put(key, value);
		}
	}

	@Override
	public String toString() {
		String str = "";
		Iterator<String> e = keySet().iterator();
		while (e.hasNext()) {
			String key = e.next();
			String val = get(key);
			str += key + ": " + val + "\r\n";
		}
		return str;
	}

}