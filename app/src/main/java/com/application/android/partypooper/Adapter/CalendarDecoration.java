package com.application.android.partypooper.Adapter;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.android.partypooper.R;

/**
 * https://gist.github.com/saber-solooki/edeb57be63d2a60ef551676067c66c71
 */

public class CalendarDecoration extends RecyclerView.ItemDecoration {

    private View header;
    private TextView text;

    private final Section section;

    private final int offset;

    public CalendarDecoration(int height, @NonNull Section section) {
        offset = height;
        this.section = section;
    }

    @Override
    public void getItemOffsets(Rect rect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(rect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);

        if (section.isHeader(pos)) {
            rect.top = offset;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (header == null) {
            header = inflateHeaderView(parent);
            text = header.findViewById(R.id.item_calendar_header_date);
            fixLayoutSize(header, parent);
        }

        CharSequence previousHeader = "";
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            final int position = parent.getChildAdapterPosition(child);

            CharSequence title = section.getSectionHeader(position);
            text.setText(title);

            if (!previousHeader.equals(title) || section.isHeader(position)) {
                previousHeader = title;
                drawHeader(c, child, header);
            }
        }
    }

    private void drawHeader(Canvas c, View current, View next) {
        c.save();
        c.translate(0, Math.max(0, current.getTop() - next.getHeight()));
        next.draw(c);
        c.restore();
    }

    private View inflateHeaderView(RecyclerView parent) {
        return LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_calendar_header, parent, false);
    }

    /**
     * Properly measures and layouts the top sticky description.
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private void fixLayoutSize(View view, ViewGroup parent) {
        // Specs for parent (RecyclerView)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Specs for children (headers)
        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

        view.measure(childWidth, childHeight);

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }
}
