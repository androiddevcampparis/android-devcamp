package com.pongme.utils;


import java.util.List;

import com.pongme.model.POIData;

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

	public static void replacePOI( List<POIData> list, POIData data, int maxSize ){
		for( int i = 0; i<list.size(); i++ ){
			if( list.get(i).getId().equals( data.getId() ) ){
				list.set( i, data );
				break;
			}
		}
	}

	public static boolean contains( List<POIData> list, POIData data ){
		for( POIData poi : list ){
			if( poi.getId().equals( data.getId() ) ){
				return true;
			}
		}
		return false;
		
	}

}
