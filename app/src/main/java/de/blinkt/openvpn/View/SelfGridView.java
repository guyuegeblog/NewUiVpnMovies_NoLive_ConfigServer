package de.blinkt.openvpn.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/5/9.
 */
public class SelfGridView extends GridView {
    public SelfGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelfGridView(Context context) {
        super(context);
    }

    public SelfGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
