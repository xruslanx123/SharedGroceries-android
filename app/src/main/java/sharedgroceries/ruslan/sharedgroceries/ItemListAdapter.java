package sharedgroceries.ruslan.sharedgroceries;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ruslan on 29/06/2017.
 */

public class ItemListAdapter extends ArrayAdapter {

    private ArrayList<Item> list;
    private MainActivity activity;

    public ItemListAdapter(MainActivity activity, ArrayList<Item> list) {
        super(activity, R.layout.item_layout, list);
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = activity.getLayoutInflater().inflate(R.layout.item_layout, parent, false);
        }
        Item item = list.get(position);
        String title = item.title;
        if(item.count > 1)
            title = title+" x"+item.count;
        ((TextView) convertView.findViewById(R.id.title)).setText(title);
        Button button = (Button) convertView.findViewById(R.id.removeButton);
        button.setTag(position);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.removeItem((int)v.getTag());
            }
        });
        return convertView;
    }
}
