package de.blinkt.openvpn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.Entity.Hot;
import de.blinkt.openvpn.Interface.hotInterface;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.Utils.Util;

/**
 * Created by Administrator on 2016/6/7.
 */
public class HotAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context cxt;
    private Util util;
    private hotInterface hotInterface;

    public HotAdapter(Context context) {
        this.cxt = context;
        this.inflater = LayoutInflater.from(cxt);
        util = new Util(context);
    }

    public de.blinkt.openvpn.Interface.hotInterface getHotInterface() {
        return hotInterface;
    }

    public void setHotInterface(de.blinkt.openvpn.Interface.hotInterface hotInterface) {
        this.hotInterface = hotInterface;
    }

    private List<Hot> list = new ArrayList<>();

    public void setList(List<Hot> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.media_item, null);
            viewHolder.table_item_image = (ImageView) convertView.findViewById(R.id.table_item_image);
            viewHolder.table_item_name = (TextView) convertView.findViewById(R.id.table_item_name);
            viewHolder.umeventid = (TextView) convertView.findViewById(R.id.umeventid);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setDataToUI(convertView, viewHolder, parent, position);
        return convertView;
    }

    private void setDataToUI(View convertView, ViewHolder viewHolder, ViewGroup parent, int position) {
        final Hot hot = list.get(position);
        x.image().bind(viewHolder.table_item_image, hot.getLogo_url(), util.getOptions());
        viewHolder.table_item_name.setText(hot.getWeb_name());
        viewHolder.umeventid.setText(hot.getId());
    }

    public class ViewHolder {
        ImageView table_item_image;//
        TextView table_item_name;//
        TextView umeventid;
    }
}
