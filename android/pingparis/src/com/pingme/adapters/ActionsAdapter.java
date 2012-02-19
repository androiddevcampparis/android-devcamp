package com.pingme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pingme.R;
import com.pingme.model.ActionsDetail;
import com.pingme.model.POIData;
import com.pingme.model.Category;

public class ActionsAdapter extends BaseAdapter {
	
	private POIData data;

	public ActionsAdapter(POIData data) {
		super();
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.getActions().size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.getActions().get(arg0);
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
			cell = layoutInflater.inflate(R.layout.item_action, null);
		}
		
		//Build view
		ActionsDetail action = (ActionsDetail)getItem(position);
		
		ImageView image = (ImageView) cell.findViewById(R.id.imageItem);
		image.setImageResource(action.getIdRes());
		
		TextView text = (TextView) cell.findViewById(R.id.textItem);
		text.setText(action.getName());
		
		return cell;
	}
}
