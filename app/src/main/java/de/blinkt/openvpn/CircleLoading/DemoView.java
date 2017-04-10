/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Omada Health, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.blinkt.openvpn.CircleLoading;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.frakbot.jumpingbeans.JumpingBeans;

import de.blinkt.openvpn.R;

/**
 * Created by oliviergoutay on 12/8/14.
 */
public class DemoView extends LinearLayout {
    /**
     * TAG for logging
     */
    private static final String TAG = "HomeUserView";
    private String conn_text_content;

    public String getConn_text_content() {
        return conn_text_content;
    }

    public void setConn_text_content(String conn_text_content) {
        this.conn_text_content = conn_text_content;
    }

    public DemoView(Context context, String conn_content) {
        super(context);
        this.conn_text_content = conn_content;
        initView();
    }


    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout mainV = (LinearLayout) inflater.inflate(R.layout.view_user_info, this);
        TextView tv_conn_content= (TextView) mainV.findViewById(R.id.tv_conn_content);
        tv_conn_content.setText(conn_text_content);
        JumpingBeans.with(tv_conn_content).makeTextJump(0, tv_conn_content.length() - 1)
                .build();
        //TODO init view
    }

}
