package com.tann.vattana.currencyexchangeapplication;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomSpinner extends ArrayAdapter {

    private String[] countryName;
    private Integer[] countryImg;
    private Context context;

    public CustomSpinner(Context context, String[] countryName, Integer[] countryImg) {
        super(context, R.layout.custom_spinner_row);
        this.countryName = countryName;
        this.countryImg = countryImg;
        this.context = context;
    }

    static class ViewContainer {
        public ImageView flagImg;
        public TextView currencyName;
    }

    @Override
    public int getCount() {
        return countryName.length;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewContainer viewContainer;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_spinner_row, null);

            viewContainer = new ViewContainer();
            viewContainer.flagImg = (ImageView)
                    convertView.findViewById(R.id.countryFlag);
            viewContainer.currencyName = (TextView)
                    convertView.findViewById(R.id.countryName);

            convertView.setTag(viewContainer);
        } else {
            viewContainer = (ViewContainer) convertView.getTag();
        }

        viewContainer.flagImg.setImageResource(countryImg[position]);
        viewContainer.currencyName.setText(countryName[position]);

        return convertView;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
