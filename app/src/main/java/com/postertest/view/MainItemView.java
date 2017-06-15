package com.postertest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;



public class MainItemView extends RelativeLayout {
    private ImageView a;

    public MainItemView(Context context) {
        this(context, null, 0);
    }

    public MainItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MainItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
//        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.main_item_view, this, true);
//        this.a = (ImageView) inflate.findViewById(R.id.iv);
//        TextView textView = (TextView) inflate.findViewById(R.id.tv_desc);
//        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, h.settingItemAttrs);
//        int resourceId = obtainStyledAttributes.getResourceId(0, R.drawable.setting_icon);
//        this.a.setBackgroundResource(obtainStyledAttributes.getResourceId(2, R.drawable.main_item_clip_bg_selector));
//        CharSequence string = obtainStyledAttributes.getString(1);
//        this.a.setImageResource(resourceId);
//        textView.setText(string);
//        obtainStyledAttributes.recycle();
    }

    public void setOnClickLisentener(OnClickListener onClickListener) {
        this.a.setOnClickListener(onClickListener);
    }
}
