package com.pingme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pingme.R;
import com.pingme.model.Category;

public class CategoriesAdapter extends BaseAdapter {

	@Override
	public int getCount() {
		return Category.getCategories().size();
	}

	@Override
	public Object getItem(int arg0) {
		return Category.getCategories().get(arg0);
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
			cell = layoutInflater.inflate(R.layout.item_preferences, null);
		}
		
		//Build view
		Category pref = (Category)getItem(position);
		setStatusIcon(cell, pref);
		
		ImageView image = (ImageView) cell.findViewById(R.id.imageItem);
		image.setImageResource(pref.getIdRes());
		
		TextView text = (TextView) cell.findViewById(R.id.textItem);
		text.setText(pref.getName());
		
		return cell;
	}
	
	public static void setStatusIcon(View view, Category pref){
		ImageView imageStatus = (ImageView) view.findViewById(R.id.statusItem);
		imageStatus.setImageResource(pref.isChecked() ? R.drawable.pref_on : R.drawable.pref_off);
	}

}
