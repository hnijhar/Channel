package com.sharkhunter.channel;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import net.pms.dlna.DLNAResource;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.dlna.virtual.VirtualVideoAction;

public class ChannelPMSFolder extends VirtualFolder implements ChannelFilter{
	
		private ChannelFolder cf;
		private String filter;
		private String url;
		private String imdb;
		
		private boolean thumbScriptRun;
		private boolean favorized;
		
		public ChannelPMSFolder(ChannelFolder cf,char ch) {
			this(cf,String.valueOf(ch),String.valueOf(ch),"",cf.getThumb());
		}
		
		public ChannelPMSFolder(ChannelFolder cf,String name) {
			this(cf,name,null,"",cf.getThumb());
		}
		
		public ChannelPMSFolder(ChannelFolder cf,char ch,String url) {
			this(cf,String.valueOf(ch),String.valueOf(ch),url,cf.getThumb());
		}
		
		public ChannelPMSFolder(ChannelFolder cf,String name,String filter,String url,String thumb) {
			super(name==null?"":StringEscapeUtils.unescapeHtml(name),thumb);
			this.cf=cf;
			this.filter=filter;
			this.url=url;
			favorized=false;
			thumbScriptRun=false;
		}
		
		public void setImdb(String imdb) {
			this.imdb=imdb;
		}
		
		public void discoverChildren() {
			try {
				if(!thumbScriptRun) {
					thumbnailIcon=ChannelScriptMgr.runScript(cf.thumbScript(), thumbnailIcon,cf.getChannel());
					thumbScriptRun=true;
				}
				if(!cf.ignoreFav()) {
					// Add bookmark action
					final ChannelPMSFolder cb=this;
					addChild(new VirtualVideoAction("Add to favorite",true) { //$NON-NLS-1$
						public boolean enable() {
							cb.bookmark();
							return true;
						}
					});
				}
				else if(cf.isFavItem()) {
					// Add 'remove bookmark' action
					final ChannelPMSFolder cb=this;
					addChild(new VirtualVideoAction("Remove from favorite",true) { //$NON-NLS-1$
						public boolean enable() {
							cb.unbookmark();
							return true;
						}
					});
				}
				cf.match(this,this,url,thumbnailIcon,name,imdb);
				cf.addMovieInfo(this, imdb,thumbnailIcon);
			} catch (Exception e) {
			}
		}
		
		public void resolve() {
			this.discovered=false;
			this.getChildren().clear();
		}
		
		public boolean isTranscodeFolderAvailable() {
			return false;
		}
		
		public boolean filter(String str) {
			if(ChannelUtil.empty(filter))
				return true;
			if(filter.equalsIgnoreCase("#")) {
				return cf.otherChar(str.charAt(0));
			}
			return str.startsWith(filter);
		}
		
		public String getThumb() {
			return (thumbnailIcon);
		}
		
		public ChannelFolder getFolder() {
			return cf;
		}
		
		public InputStream getThumbnailInputStream() {
			try {
				//thumbnailIcon=ChannelNaviXProc.simple(thumbnailIcon, cf.thumbScript());
				thumbnailIcon=ChannelScriptMgr.runScript(cf.thumbScript(), thumbnailIcon,cf.getChannel());
				thumbScriptRun=true;
				return downloadAndSend(thumbnailIcon,true);
			}
			catch (Exception e) {
				return super.getThumbnailInputStream();
			}
		}
		
		public void bookmark() {
			if(cf.ignoreFav()||favorized) 
				return;
			favorized=true;
			String data=cf.mkFav(url,name,thumbnailIcon,imdb);
			if(!ChannelUtil.empty(data))
				ChannelUtil.addToFavFile(data,name);
		}
		
		public void unbookmark() {
			ChannelUtil.RemoveFromFavFile(name,cf.getURL());
			cf.remove();
		}
		
		public boolean lastThumb() {
			return (cf.getProp("last_thumb")!=null);
		}
}
