package com.pingme.utils;


import java.util.List;

import com.pingme.model.POIData;

public class POIListUtil {
	
	public static void enqueuePOI( List<POIData> list, POIData data, int maxSize ){
		for( POIData poi : list ){
			if( poi.getId() == data.getId() ){
				list.remove( poi );
				break;
			}
		}
		list.add(data);
		if( list.size()>maxSize){
			list.remove(maxSize);
		}
	}

	public static boolean contains( List<POIData> list, POIData data ){
		for( POIData poi : list ){
			if( poi.getId() == data.getId() ){
				return true;
			}
		}
		return false;
		
	}

}
