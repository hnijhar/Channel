package com.sharkhunter.channel;

import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ChannelCookie {
	
	private static long parseTTD(String expStr) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		java.util.Date d;
		try {
			d = sdfDate.parse(expStr);
			return d.getTime();
		} catch (ParseException e) {
			return System.currentTimeMillis()+(24*60*60*2*1000);
		}
	}
	
	public static String getCookie(String url) {
		ChannelAuth a=Channels.getCookie(ChannelUtil.trimURL(url));
		if(a==null)
			return null;
		return a.authStr;
	}
	
	public static String parseCookie(URLConnection connection,ChannelAuth a,String url) throws Exception {
		return parseCookie(connection,a,url,false);
	}
	
	public static String parseCookie(URLConnection connection,ChannelAuth a,String url,boolean skipUpdate) throws Exception {
		String hName="";
		long ttd=System.currentTimeMillis()+(24*60*60*2*1000);
		boolean update=false;
		for (int j=1; (hName = connection.getHeaderFieldKey(j))!=null; j++) {
		 	String cStr=connection.getHeaderField(j);
		 	if (!hName.equalsIgnoreCase("Set-Cookie")) 
		 		continue;
		 	String[] fields = cStr.split(";\\s*");
	 		String cookie=fields[0];
	 		if(ChannelUtil.empty(cookie))
	 			continue;
	 		int pos;
	 		if((pos=cookie.indexOf(";"))!=-1)
	 			cookie = cookie.substring(0, pos);
	 		Channels.debug("cookie "+cookie);
	 		if(a!=null&&ChannelUtil.cookieMethod(a.method))
	 			if(!ChannelUtil.empty(a.authStr)&&cookie.equals(a.authStr))
	 				continue;
	 		if(fields.length>1)
	 			if(fields[1].contains("expires")) {
	 				String[] exp=fields[1].split(",");
	 				if(exp.length>1)
	 					ttd=parseTTD(exp[1]);
	 			}
	 		if(a==null)
	 			a=new ChannelAuth();
	 		a.method=ChannelLogin.SIMPLE_COOKIE;
	 		a.authStr=ChannelUtil.append(a.authStr,"; ",cookie);
	 		a.ttd=ttd;
	 		if(skipUpdate)
	 			return a.authStr;
	 		Channels.debug("adding (stat) cookie "+cookie+" to url "+ChannelUtil.trimURL(url));
	 		update|=fixCookie(ChannelUtil.trimURL(url), cookie);	
		}
		if(update)
			Channels.mkCookieFile();
		return null;
	}
	
	private static boolean fixCookie(String url, String cookie) {
		ChannelAuth b=new ChannelAuth();
		b.authStr=cookie;
		return Channels.addCookie(url, b);
	}
}
