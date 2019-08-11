package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.Model.Section;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderInterface {

    private ArrayList<Section> items;

    private LayoutInflater mInflater;

    private ItemClickListener mListener;

    public CalendarAdapter (Context context, ArrayList<Section> items) {
        mInflater = LayoutInflater.from(context);
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        switch (i) {
            case 0:
                view = mInflater.inflate(R.layout.item_calendar_header, parent, false);
                return new HeaderViewHolder(view);

            default:
                view = mInflater.inflate(R.layout.item_calendar, parent, false);
                return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) viewHolder).bindData(i);
        } else {
            ((ItemViewHolder) viewHolder).bindData(i);
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setClickListener(ItemClickListener clickListener) {
        mListener = clickListener;
    }

    public Section getItem(int position) {
        return items.get(position);
    }

    public void setItems(List<Section> items) {
        if (items == null) {
            throw new IllegalArgumentException("List to set in the RecyclerView is null");
        }
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public void bindHeaderData(View view, int headerPosition) {
        if (items.get(headerPosition).getViewType() == 0) {
            TextView header = view.findViewById(R.id.item_calendar_header_date);
            header.setText(getDateFormat(items.get(headerPosition).getEvent().getDate_stamp()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.item_calendar_header;
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return items.get(itemPosition).getViewType() == 0;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView date;

        public HeaderViewHolder(@NonNull View view) {
            super(view);

            date = view.findViewById(R.id.item_calendar_header_date);
        }

        public void bindData(int i) {
            String date_stamp = items.get(i).getEvent().getDate_stamp();

            date.setText(getDateFormat(date_stamp));
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /** Image used for the event */
        private ImageView image;

        /** Opens the selected event */
        private ImageView next;

        /** Information about the event */
        private TextView name, host, time;

        /** Firebase authentication */
        private FirebaseAuth mAuth;

        /** Firebase user */
        private FirebaseUser mUser;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();

            image = itemView.findViewById(R.id.item_calendar_image);
            name = itemView.findViewById(R.id.item_calendar_name);
            host = itemView.findViewById(R.id.item_calendar_host);
            time = itemView.findViewById(R.id.item_calendar_time);
            next = itemView.findViewById(R.id.item_calendar_next);
        }

        public void bindData(int i) {
            String owner;
            final Event e = items.get(i).getEvent();

            name.setText(e.getName());

            if (e.getHost().equals(mUser.getUid())) owner = "by You";
            else owner = "by " + e.getHost_username();

            host.setText(owner);
            time.setText(e.getStart_time());

            if (e.getImageURL() != null) {
                Glide.with(image.getContext()).load(e.getImageURL()).into(image);
            } else {
                Glide.with(image.getContext()).load(R.drawable.default_banner).into(image);
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onItemClick(v, getAdapterPosition());
        }
    }

    /**
     * Converts a month in int form to a month in string form.
     * @param date_stamp of the form YYYYMMDD
     * @return the date in a readable format
     */
    private String getDateFormat(String date_stamp) {
        String year = date_stamp.substring(0,4);
        String month = new DateFormatSymbols().getMonths()[Integer.parseInt(date_stamp.substring(4,6))];
        String day = date_stamp.substring(6,8);

        return day + " " + month + " " + year;
    }

}
