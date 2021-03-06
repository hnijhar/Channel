version=0.40

channel SVTPlay {
	img=http://svtplay.se/img/brand/svt-play.png
	var {
		disp_name=Bitrate 1
		var_name=br1
		values=2400,1400,850,320
		suffix=Kbps
	}
	var {
		disp_name=Bitrate 2
		var_name=br2
		values=850,1400,320
		suffix=Kbps
	}
	folder {
		name=Live
		url=http://www.svtplay.se/?live=1
		folder {
			matcher=a href=\"(/live[^\"]+)\" class=\"svtXColorWhite [^\"]+\">.*?<img  class=\"svtMediaBlockFig-L playBroadcastThumbnail\" alt=\"([^\"]+)\" src=\"([^\"]+)\"/>
			order=url,name,thumb
			prop=matcher_dotall
			url=http://www.svtplay.se/
			folder {
				type=empty
				matcher=data-json-href=\"([^\"]+)\"
				url=http://www.svtplay.se/
				prop=append_url=?output=json
				media {
					matcher=\"url\":\"(rtmp[^\"]+)\",\"bitrate\":(@#br1@#|@#br2@#)
					order=url,dummy
					prop=only_first,live
					put=swfVfy=http://www.svtplay.se/public/swf/video/svtplayer-2012.15.swf
				}
			}
		}
	}
	folder {
		name=A-Z
		type=ATZ
		url=http://www.svtplay.se/program
		folder {
			 matcher=<li class=\"playListItem\"><a href=\"([^\"]+)\" class=\"playLetterLink\">([^<]+)</a></li>
			 order=url,name
			 url=http://www.svtplay.se/
			 folder {
				url=http://www.svtplay.se/
		  		matcher=<a href=\"([^\"]+)\" class=\"playLink playFloatLeft playBox-Padded"\>.*?<img class=\"playGridThumbnail\" alt=\"([^\"]+)\" src=\"([^\"]+)\"/>
				order=url,name,thumb
				prop=matcher_dotall
				folder {
					type=empty
					matcher=data-json-href=\"([^\"]+)\"
					url=http://www.svtplay.se/
					prop=append_url=?output=json
					media {
						matcher=\"url\":\"(rtmp[^\"]+)\",\"bitrate\":(@#br1@#|@#br2@#)
						order=url,dummy
						prop=only_first
						put=swfVfy=http://www.svtplay.se/public/swf/video/svtplayer-2012.15.swf
					}
				}
			}
		}
	}
}
			