package com.pingme.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pingme.PingMeApplication;
import com.pingme.R;
import com.pingme.model.POIData;

public class POIAdapter extends BaseAdapter {
	
	private List<POIData> datas;

	public POIAdapter(List<POIData> datas) {
		super();
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View cell = convertView;
		if (cell == null) {
			final LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			cell = layoutInflater.inflate(R.layout.item_data, null);
		}
		
		//Build view
		POIData data = (POIData)getItem(position);
		
		ImageView image = (ImageView) cell.findViewById(R.id.imageItem);
		PingMeApplication.getImageDownloader().download(data.getUrl_image(), image, null, "Adapter");
		
		TextView text = (TextView) cell.findViewById(R.id.textItem);
		text.setText(data.getTitle());
		
		TextView textDetail = (TextView) cell.findViewById(R.id.detailsItem);
		textDetail.setText(data.getDescription());
		
		return cell;
	}
}
