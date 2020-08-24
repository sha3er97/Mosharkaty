package com.resala.mosharkaty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class ClosingAdapter extends RecyclerView.Adapter<ClosingAdapter.ViewHolder> {
    private ArrayList<ClosingItem> closingItems;
    private Context context;

    public ClosingAdapter(ArrayList<ClosingItem> closingItems, Context context) {
        this.closingItems = closingItems;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link ClosingAdapter.ViewHolder} of the given type to
     * represent an item. This new ViewHolder should be constructed with a new View that can represent
     * the items of the given type. You can either create a new View manually or inflate it from an
     * XML layout file.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an
     *                 adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ClosingAdapter.ViewHolder, int)
     */
    @NonNull
    @Override
    public ClosingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.closing_item, parent, false);
        return new ClosingAdapter.ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update
     * the contents of the {@link ClosingAdapter.ViewHolder#itemView} to reflect the item at the given
     * position.
     *
     * <p>Note that unlike {@link ListView}, RecyclerView will not call this method again if the
     * position of the item changes in the data set unless the item itself is invalidated or the new
     * position cannot be determined. For this reason, you should only use the <code>position</code>
     * parameter while acquiring the related data item inside this method and should not keep a copy
     * of it. If you need the position of an item later on (e.g. in a click listener), use {@link
     * ClosingAdapter.ViewHolder#getAdapterPosition()} which will have the updated adapter position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at
     *                 the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ClosingAdapter.ViewHolder holder, int position) {
        ClosingItem item = closingItems.get(position);
        holder.day.setText(item.getDay());
        if (item.getIsClosed() == 1) {
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.tick));
            holder.imageView.setColorFilter(
                    ContextCompat.getColor(context, android.R.color.holo_green_dark),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.delete));
            holder.imageView.setColorFilter(
                    ContextCompat.getColor(context, android.R.color.holo_red_dark),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return closingItems.size();
    }

    /**
     * ****************************************************************************
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView day;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewClosing);
            day = itemView.findViewById(R.id.dayTV);
        }
    }
}
