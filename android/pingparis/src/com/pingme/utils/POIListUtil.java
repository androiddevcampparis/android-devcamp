package com.pingme.utils;


import java.util.List;

import com.pingme.model.POI_Data;

public class POIListUtil {
	
	public static void enqueuePOI( List<POI_Data> list, POI_Data data, int maxSize ){
		for( POI_Data poi : list ){
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

	public static boolean contains( List<POI_Data> list, POI_Data data ){
		for( POI_Data poi : list ){
			if( poi.getId() == data.getId() ){
				return true;
			}
		}
		return false;
		
	}

}
