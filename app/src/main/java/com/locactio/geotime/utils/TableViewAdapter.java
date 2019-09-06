package com.locactio.geotime.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.locactio.geotime.R;
import com.locactio.geotime.entities.Clocking;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class TableViewAdapter extends ArrayAdapter<Clocking> {

    private LayoutInflater infltater;
    private ArrayList<Clocking> items;

    public TableViewAdapter (Context context, int textViewResourceId, ArrayList<Clocking> items)
    {
        super(context, textViewResourceId, items);
        this.items = items;
        infltater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void renewData(ArrayList<Clocking> data)
    {
        this.items.clear();
        this.items.addAll(data);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
        {
            convertView = infltater.inflate(R.layout.hour_row, null);
        }

        try{
            final Clocking clocking = items.get(position);

            ImageView image = convertView.findViewById(R.id.img);
            TextView fecha = convertView.findViewById(R.id.fecha);
            TextView hora = convertView.findViewById(R.id.hora);

            Calendar c = Calendar.getInstance();
            c.setTime(clocking.getMomento());

            switch (clocking.getTipo())
            {
                case 0:
                    image.setImageResource(R.drawable.ic_start);
                    break;
                case 1:
                    image.setImageResource(R.drawable.ic_stop);
                    break;
                case 2:
                    image.setImageResource(R.drawable.ic_pause);
                    break;
                case 3:
                    image.setImageResource(R.drawable.ic_return);
                    break;
                case 6:
                    image.setImageResource(R.drawable.beach);
                    break;
            }

            fecha.setText(String.format("%02d-%02d-%d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR)));
            hora.setText(String.format("%02d:%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)));

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return convertView;
    }
}
