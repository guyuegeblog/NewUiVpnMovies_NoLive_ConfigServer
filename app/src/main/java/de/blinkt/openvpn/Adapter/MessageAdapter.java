package de.blinkt.openvpn.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.Entity.MessageInfo;
import de.blinkt.openvpn.Interface.messageInterface;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.Utils.CircleTransform;
import de.blinkt.openvpn.Utils.Util;
import de.blinkt.openvpn.View.SelfListView;

/**
 * Created by Administrator on 2016/6/7.
 */
public class MessageAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity cxt;
    private Util util;
    private messageInterface messageInterface;

    public de.blinkt.openvpn.Interface.messageInterface getMessageInterface() {
        return messageInterface;
    }

    public void setMessageInterface(de.blinkt.openvpn.Interface.messageInterface messageInterface) {
        this.messageInterface = messageInterface;
    }

    public MessageAdapter(Activity context, SelfListView selfListView) {
        this.cxt = context;
        this.inflater = LayoutInflater.from(cxt);
        util = new Util(context);
    }

    private List<MessageInfo> list = new ArrayList<>();

    public void setList(List<MessageInfo> list) {
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
            convertView = inflater.inflate(R.layout.message_item, null);
            viewHolder.web_logo = (ImageView) convertView.findViewById(R.id.web_logo);
            viewHolder.user_logo = (ImageView) convertView.findViewById(R.id.user_logo);
            viewHolder.me_content = (TextView) convertView.findViewById(R.id.me_content);
            viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.like_count = (TextView) convertView.findViewById(R.id.like_count);
            viewHolder.me_date = (TextView) convertView.findViewById(R.id.me_date);
            viewHolder.me_item = (LinearLayout) convertView.findViewById(R.id.me_item);
            viewHolder.tuijiandu = (TextView) convertView.findViewById(R.id.tuijiandu);
            viewHolder.umeventid = (TextView) convertView.findViewById(R.id.umeventid);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setDataToUI(convertView, viewHolder, parent, position);
        return convertView;
    }

    private void setDataToUI(View convertView, ViewHolder viewHolder, ViewGroup parent, int position) {
        final MessageInfo messageInfo = list.get(position);
        Glide.with(cxt).load(messageInfo.getPic_address()).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(viewHolder.web_logo);
        Glide.with(cxt).load(messageInfo.getPic_link())
                .asBitmap().transform(new CircleTransform(cxt))
                .placeholder(R.drawable.imgerror)
                .error(R.drawable.imgerror)
                .into(viewHolder.user_logo);
        viewHolder.user_name.setText(messageInfo.getNickName());
        viewHolder.like_count.setText(messageInfo.getCounts());
        viewHolder.me_content.setText(messageInfo.getContext());
        viewHolder.me_content.scrollTo(0, 0);
        //推荐:   86%
        viewHolder.tuijiandu.setText("推荐:   " + util.createTranslateRandom() + "%");
        viewHolder.me_date.setText(messageInfo.getCreate_Time());
        viewHolder.umeventid.setText(messageInfo.getId());
        viewHolder.me_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageInterface.itemClick(view, messageInfo.getLink(), messageInfo.getId());
            }
        });
        viewHolder.me_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageInterface.itemClick(view, messageInfo.getLink(), messageInfo.getId());
            }
        });

    }

    public class ViewHolder {
        ImageView web_logo;
        TextView me_content;
        ImageView user_logo;
        TextView user_name;
        TextView like_count;
        TextView me_date;
        TextView umeventid;
        TextView tuijiandu;
        LinearLayout me_item;
    }
}
