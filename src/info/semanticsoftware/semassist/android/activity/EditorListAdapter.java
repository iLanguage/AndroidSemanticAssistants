package info.semanticsoftware.semassist.android.activity;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EditorListAdapter extends BaseAdapter
{
    Activity context;
    String names[];

    public EditorListAdapter(Activity context, String[] title) {
        super();
        this.context = context;
        this.names = title;
    }

    public int getCount() {
        
        return names.length;
    }

    public Object getItem(int position) {
        
        return null;
    }

    public long getItemId(int position) {
        
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView t;
        if (convertView == null)
            t = new TextView(parent.getContext());
        else
            t = (TextView) convertView;

        t.setText(this.names[position]);

        convertView = t;

        System.out.println("getView is called");

        return convertView;
    }

    // Added per K-Ballo suggestion
    public int getViewTypeCount(){
    return 1;
}
public int getItemViewType(){
    return 0;
}
}