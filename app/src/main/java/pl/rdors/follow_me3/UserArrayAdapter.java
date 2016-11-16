package pl.rdors.follow_me3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.rdors.follow_me3.rest.model.User;

public class UserArrayAdapter extends ArrayAdapter<User> {

    private List<User> users;
    private Context context;

    public UserArrayAdapter(Context context, int textViewResourceId, List<User> users) {
        super(context, textViewResourceId, users);
        this.context = context;
        this.users = new ArrayList<>();
        this.users.addAll(users);
    }

    private class ViewHolder {
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
            holder.name = (CheckBox) convertView.findViewById(R.id.check);
            convertView.setTag(holder);

            holder.name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    User user = (User) cb.getTag();
                    Toast.makeText(context,
                            "Clicked on Checkbox: " + cb.getText() +
                                    " is " + cb.isChecked(),
                            Toast.LENGTH_LONG).show();
                    user.setSelected(cb.isChecked());
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = users.get(position);
        holder.name.setText(user.getUsername());
        holder.name.setChecked(false);
        holder.name.setTag(user);

        return convertView;

    }

    public List<User> getUsers() {
        return users;
    }
}