package pl.rdors.follow_me3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.rdors.follow_me3.model.User;

public class MyCustomAdapter extends ArrayAdapter<User> {

    private List<User> countryList;
    private Context context;

    public MyCustomAdapter(Context context, int textViewResourceId, List<User> countryList) {
        super(context, textViewResourceId, countryList);
        this.context = context;
        this.countryList = new ArrayList<User>();
        this.countryList.addAll(countryList);
    }

    private class ViewHolder {
        TextView code;
        CheckBox name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_item, null);

            holder = new ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.label);
            holder.name = (CheckBox) convertView.findViewById(R.id.check);
            convertView.setTag(holder);

            holder.name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    User country = (User) cb.getTag();
                    Toast.makeText(context,
                            "Clicked on Checkbox: " + cb.getText() +
                                    " is " + cb.isChecked(),
                            Toast.LENGTH_LONG).show();
                    //country.setSelected(cb.isChecked());
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User country = countryList.get(position);
        holder.code.setText(" (" + country.getName() + ")");
        holder.name.setText(country.getName());
        holder.name.setChecked(false);
        holder.name.setTag(country);

        return convertView;

    }

}