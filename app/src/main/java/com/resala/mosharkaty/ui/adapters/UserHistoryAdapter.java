package com.resala.mosharkaty.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.UserHistoryItem;

import java.util.ArrayList;

public class UserHistoryAdapter extends RecyclerView.Adapter<UserHistoryAdapter.ViewHolder> {
    private ArrayList<UserHistoryItem> userHistoryItems;
    private Context context;

    public UserHistoryAdapter(ArrayList<UserHistoryItem> userHistoryItems, Context context) {
        this.userHistoryItems = userHistoryItems;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link UserHistoryAdapter.ViewHolder} of the given type to
     * represent an item. This new ViewHolder should be constructed with a new View that can represent
     * the items of the given type. You can either create a new View manually or inflate it from an
     * XML layout file.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an
     *                 adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(UserHistoryAdapter.ViewHolder, int)
     */
    @NonNull
    @Override
    public UserHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.user_history_item, parent, false);
        return new UserHistoryAdapter.ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update
     * the contents of the {@link UserHistoryAdapter.ViewHolder#itemView} to reflect the item at the
     * given position.
     *
     * <p>Note that unlike {@link ListView}, RecyclerView will not call this method again if the
     * position of the item changes in the data set unless the item itself is invalidated or the new
     * position cannot be determined. For this reason, you should only use the <code>position</code>
     * parameter while acquiring the related data item inside this method and should not keep a copy
     * of it. If you need the position of an item later on (e.g. in a click listener), use {@link
     * UserHistoryAdapter.ViewHolder#getAdapterPosition()} which will have the updated adapter
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at
     *                 the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull UserHistoryAdapter.ViewHolder holder, int position) {
        UserHistoryItem item = userHistoryItems.get(position);
        holder.Username.setText(item.getUsername());
        holder.count.setText(String.valueOf(item.getCount()));
        holder.history.setText(item.getHistory());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return userHistoryItems.size();
    }

    /**
     * ****************************************************************************
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Username;
        TextView count;
        TextView history;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Username = itemView.findViewById(R.id.userName);
            count = itemView.findViewById(R.id.userCount);
            history = itemView.findViewById(R.id.userHistory);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            UserHistoryItem itemClicked = userHistoryItems.get(position);
            // do nothing till now
        }
    }
}
