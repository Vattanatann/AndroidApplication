package com.tann.vattana.currencyexchangeapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListView extends BaseAdapter {

    private Context context;
    private String[] currencyName;
    private String[] currencyFullName;
    private String[] value;
    private Integer[] countryFlag;

    public CustomListView (Context context, String[] currencyName, String[] currencyFullName, String[] value, Integer[] countryFlag) {
        this.context = context;
        this.currencyName = currencyName;
        this.currencyFullName = currencyFullName;
        this.value = value;
        this.countryFlag = countryFlag;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewContainer {
        public ImageView flagImg;
        public TextView currencyName;
        public TextView currencyFullName;
        public TextView value;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewContainer viewContainer;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_row, null);

            viewContainer = new ViewContainer();
            viewContainer.flagImg = (ImageView)
                    view.findViewById(R.id.flagImg);
            viewContainer.currencyName = (TextView)
                    view.findViewById(R.id.currencyName);
            viewContainer.currencyFullName = (TextView)
                    view.findViewById(R.id.currencyFullName);
            viewContainer.value = (TextView)
                    view.findViewById(R.id.valueTxt);

            view.setTag(viewContainer);
        }
        else {
            viewContainer = (ViewContainer) view.getTag();
        }

        viewContainer.flagImg.setImageResource(countryFlag[i]);
        viewContainer.currencyName.setText(currencyName[i]);
        viewContainer.currencyFullName.setText(currencyFullName[i]);
        viewContainer.value.setText(value[i]);

        return view;
    }
}
