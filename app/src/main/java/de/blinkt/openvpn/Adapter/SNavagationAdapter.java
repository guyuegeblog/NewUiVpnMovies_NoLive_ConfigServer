package de.blinkt.openvpn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.Entity.NavigationInfo;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.View.GlideRoundTransform;

/**
 * Created by Administrator on 2016/6/7.
 */
public class SNavagationAdapter extends BaseAdapter {
    private List<NavigationInfo> list = new ArrayList<>();
    private LayoutInflater inflater;
    private Context cxt;
    private de.blinkt.openvpn.Interface.navigationInterface navigationInterface;

    public de.blinkt.openvpn.Interface.navigationInterface getNavigationInterface() {
        return navigationInterface;
    }

    public void setNavigationInterface(de.blinkt.openvpn.Interface.navigationInterface navigationInterface) {
        this.navigationInterface = navigationInterface;
    }

    public SNavagationAdapter(Context context) {
        this.cxt = context;
        this.inflater = LayoutInflater.from(cxt);
    }

    public List<NavigationInfo> getList() {
        return list;
    }

    public void setList(List<NavigationInfo> list) {
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
        sViewHolder sViewHolder = null;
        if (convertView == null) {
            sViewHolder = new sViewHolder();
            convertView = inflater.inflate(R.layout.navigation_item, null);
            sViewHolder.iv_navi = (ImageView) convertView.findViewById(R.id.iv_navi);
            convertView.setTag(sViewHolder);
        } else {
            sViewHolder = (sViewHolder) convertView.getTag();
        }
        final NavigationInfo info = list.get(position);
        Glide.with(cxt).load(info.getLogoUrl()).transform(new GlideRoundTransform(cxt)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(sViewHolder.iv_navi);
        sViewHolder.iv_navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, info);
            }
        });
        return convertView;
    }


    public class sViewHolder {
        ImageView iv_navi;//

    }
}
