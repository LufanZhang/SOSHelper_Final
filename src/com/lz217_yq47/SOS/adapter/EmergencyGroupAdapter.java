/*
 * this class was created by Lufan: 18/11/2014
 */
package com.lz217_yq47.SOS.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.lz217_yq47.SOS.R;

public class EmergencyGroupAdapter extends ArrayAdapter<EMGroup> {
//load the interface dynamically
	private LayoutInflater inflater;

	public EmergencyGroupAdapter(Context context, int res, List<EMGroup> groups) {
		super(context, res, groups);
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	@Override
	public int getItemViewType(int position) {
	    if(position == 0){
	        return 0;
	    }else if(position == getCount() - 1){ 
	        return 1;
	    }else{
	        return 2;
	    }
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if(getItemViewType(position) == 0){
	        if (convertView == null) {
                convertView = inflater.inflate(R.layout.search_bar_with_padding, null);
            }
    	    final EditText query = (EditText) convertView.findViewById(R.id.query);
            final ImageButton clearSearch = (ImageButton) convertView.findViewById(R.id.search_clear);
            query.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    getFilter().filter(s);
                    if (s.length() > 0) {
                        clearSearch.setVisibility(View.VISIBLE);
                    } else {
                        clearSearch.setVisibility(View.INVISIBLE);
                    }
                }
    
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
    
                public void afterTextChanged(Editable s) {
                }
            });
            clearSearch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    query.getText().clear();
                }
            });
	    //In order to remove Create new group option
	    }else if(getItemViewType(position) == 1){
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_group, null);
			}
	    }
	    
	    
	    
	   
		 else {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_group, null);
			}
			
			((TextView)convertView.findViewById(R.id.name)).setText(getItem(position-1).getGroupName());
			
		}
		
		return convertView;
	}

	@Override
	public int getCount() {
		return super.getCount() + 2;
	}

}
