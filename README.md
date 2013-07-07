android_http_framework
======================

An open source asynchronous network data processing framework that supports json, xml data format, 
you can also custom extensions TaskHandler, to achieve their business

Features

Make asynchronous HTTP requests, handle responses in anonymous callbacks
HTTP requests happen outside the UI thread
Requests use a threadpool to cap concurrent resource usage
GET/POST params builder (RequestParams)

for example:
you must init the HttpManager or HttpAsyncTaskManager
  HttpAsyncTaskManager http= new HttpAsyncTaskManager(this);
  
  1),if you want parse response to string,you can use it as follows
  
      String url = "http://www.12306.cn/mormhweb/kyfw/ypcx/";
			http.request(url, new StringTaskHandler() {
				@Override
				public void onNetError() {
					// TODO Auto-generated method stub
					System.out.println("---------neterror---------");
				}

				@Override
				public void onSuccess(String result) {
					// TODO Auto-generated method stub
					System.out.println("#####" + result);
				}

				@Override
				public void onFail() {
					// TODO Auto-generated method stub
					System.out.println("---------onFail---------");
				}
			});
  2)parse response to jsonObject
  
       String url2 = "https://www.googleapis.com/customsearch/v1?key=AIzaSyBmSXUzVZBKQv9FJkTpZXn0dObKgEQOIFU&cx=014099860786446192319:t5mr0xnusiy&q=AndroidDev&alt=json&searchType=image";
  	 	http.request(url2, new JsonObjectTaskHandler() {

				@Override
				public void onSuccess(JSONObject result) {
					// TODO Auto-generated method stub
					try {
						System.out.println("----" + result.getString("kind"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				@Override
				public void onNetError() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onFail() {
					// TODO Auto-generated method stub

				}
			});
      
   3) if you want to use xmlpullparse ,you should create a function to parse the xml 
   
   
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



then you can use it as follows


 
               String url3 = "http://www.11huaiyun.com/sitemap.xml";
  		http.request(url3, new InputStreamTaskHandler() {

				@Override
				public void onSuccess(InputStream result) {
					// TODO Auto-generated method stub
					try {
						List<Item> list = new MyParser().parse(result, "utf-8");
						System.out.println("****"+list);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onNetError() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onFail() {
					// TODO Auto-generated method stub

				}
			});
