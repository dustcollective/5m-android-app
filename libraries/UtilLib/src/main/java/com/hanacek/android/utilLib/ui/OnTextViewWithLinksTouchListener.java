package com.hanacek.android.utilLib.ui;

import android.text.Layout;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class OnTextViewWithLinksTouchListener implements View.OnTouchListener{
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean ret = false;
        CharSequence text = ((TextView) view).getText();
        Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
        TextView widget = (TextView) view;
        int action = motionEvent.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = stext.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    if (!onLinkClicked( ((URLSpan) link[0]).getURL())) {
                        link[0].onClick(widget);
                    }
                }
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Allows implementing classes to react on the click
     * @param url URL of the clicked link
     * @return whether the click has been handled - if true, then the parent class will not perform any action, otherwise
     * the parent class will handle the click (and will open the url in browser).
     */
    public boolean onLinkClicked(String url) {
        return false;
    }
}
