package com.example.mosharkaty;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private ArrayList<MessageItem> messageItems;
    private Context context;

    public MessagesAdapter(ArrayList<MessageItem> messageItems, Context context) {
        this.messageItems = messageItems;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link MessagesAdapter.ViewHolder} of the given type to represent an item.
     * This new ViewHolder should be constructed with a new View that can represent the items of the
     * given type. You can either create a new View manually or inflate it from an XML layout file.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     *     adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(MessagesAdapter.ViewHolder, int)
     */
    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessagesAdapter.ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update
     * the contents of the {@link MessagesAdapter.ViewHolder#itemView} to reflect the item at the given position.
     *
     * <p>Note that unlike {@link ListView}, RecyclerView will not call this method again if the
     * position of the item changes in the data set unless the item itself is invalidated or the new
     * position cannot be determined. For this reason, you should only use the <code>position</code>
     * parameter while acquiring the related data item inside this method and should not keep a copy
     * of it. If you need the position of an item later on (e.g. in a click listener), use {@link
     * MessagesAdapter.ViewHolder#getAdapterPosition()} which will have the updated adapter position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at
     *     the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        MessageItem item = messageItems.get(position);
        holder.author.setText(item.getAuthor());
        holder.date.setText(item.getDate());
        holder.content.setText(item.getContent());

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return messageItems.size();
    }

    /** ***************************************************************************** */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView author;
        TextView date;
        TextView content;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.ListMessageAuthor);
            content = itemView.findViewById(R.id.ListMessagecontent);
            date = itemView.findViewById(R.id.ListMessageDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            MessageItem itemClicked = messageItems.get(position);
            //do nothing
        }
    }
}
