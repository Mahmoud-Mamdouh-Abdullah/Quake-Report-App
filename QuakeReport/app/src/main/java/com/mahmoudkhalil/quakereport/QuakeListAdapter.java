package com.mahmoudkhalil.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuakeListAdapter extends RecyclerView.Adapter<QuakeListAdapter.QuakeViewHolder> {

    private List<EarthQuake> quakeList;
    private Context context;
    private static final String LOCATION_SEPARATOR = " of ";
    private onItemClickListener mOnItemClickListener;

    public QuakeListAdapter(List<EarthQuake> quakeList, Context context) {
        this.quakeList = quakeList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent,false);
        return new QuakeViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuakeViewHolder holder, int position) {
        String formattedMagnitude = formatMagnitude(quakeList.get(position).getMagnitude());
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitudeTextView.getBackground();
        int magnitudeColor = getMagnitudeColor(quakeList.get(position).getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);
        holder.magnitudeTextView.setText(formattedMagnitude);

        String originalLocation = quakeList.get(position).getLocation();
        String primaryLocation;
        String locationOffset;
        if (originalLocation.contains(LOCATION_SEPARATOR)) {
            String[] parts = originalLocation.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            locationOffset = context.getString(R.string.near_the);
            primaryLocation = originalLocation;
        }
        holder.locationOffsetTextView.setText(locationOffset);
        holder.primaryLocationTextView.setText(primaryLocation);

        Date dateObject = new Date(quakeList.get(position).getTimeInMilliseconds());
        String dateToDisplay = formatDate(dateObject);
        String timeToDisplay = formatTime(dateObject);
        holder.dateTextView.setText(dateToDisplay);
        holder.timeTextView.setText(timeToDisplay);
    }

    @Override
    public int getItemCount() {
        return quakeList.size();
    }

    public interface onItemClickListener {
        public void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public static class QuakeViewHolder extends RecyclerView.ViewHolder {
        TextView magnitudeTextView, locationOffsetTextView, primaryLocationTextView, dateTextView, timeTextView;
        public QuakeViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            magnitudeTextView = itemView.findViewById(R.id.magnitude);
            locationOffsetTextView = itemView.findViewById(R.id.location_offset);
            primaryLocationTextView = itemView.findViewById(R.id.primary_location);
            dateTextView = itemView.findViewById(R.id.date);
            timeTextView = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy", Locale.getDefault());
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return timeFormat.format(dateObject);
    }

    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceID;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceID = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceID = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceID = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceID = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceID = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceID = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceID = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceID = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceID = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceID = R.color.magnitude10plus;
                break;

        }
        return ContextCompat.getColor(context, magnitudeColorResourceID);
    }
}
