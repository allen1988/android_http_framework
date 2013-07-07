package com.allen.http;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/**
 * pull parse the xml
 */
public class MyParser {
	public static class Item {
		private String loc;
		private String lastmod;
		private String changefreq;

		public String getLoc() {
			return loc;
		}

		public void setLoc(String loc) {
			this.loc = loc;
		}

		public String getLastmod() {
			return lastmod;
		}

		public void setLastmod(String lastmod) {
			this.lastmod = lastmod;
		}

		public String getChangefreq() {
			return changefreq;
		}

		public void setChangefreq(String changefreq) {
			this.changefreq = changefreq;
		}

		@Override
		public String toString() {
			return "Item [loc=" + loc + ", lastmod=" + lastmod
					+ ", changefreq=" + changefreq + "]";
		}

	}

	public List<Item> parse(InputStream is, String encode) throws Exception {
		List<Item> list = null;
		Item item = null;
		XmlPullParser parse = Xml.newPullParser();
		parse.setInput(is, encode);
		// get the enent type
		int event = parse.getEventType();
		// if the event has not over
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:// start parse the xml
				list = new ArrayList<Item>();
				break;
			case XmlPullParser.START_TAG:// start element
				String name = parse.getName();// get the element name
				if ("url".equals(name)) {
					item = new Item();
				} else if ("loc".equals(name)) {
					item.setLoc(parse.nextText());
				} else if ("lastmod".equals(name)) {
					item.setLastmod(parse.nextText());
				} else if ("changefreq".equals(name)) {
					item.setChangefreq(parse.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if ("url".equals(parse.getName()))
					list.add(item);
				break;
			}
			event = parse.next();// get next event
		}
		return list;
	}
}
