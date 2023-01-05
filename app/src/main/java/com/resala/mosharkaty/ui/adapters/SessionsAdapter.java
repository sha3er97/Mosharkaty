package com.resala.mosharkaty.ui.adapters;

import static com.resala.mosharkaty.utility.classes.UtilityClass.isAdmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.resala.mosharkaty.AdminEditSessionActivity;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.ViewSpecificSessionActivity;
import com.resala.mosharkaty.utility.classes.Session;

import java.util.ArrayList;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder> {
    private ArrayList<Session> sessionsItems;
    private Context context;
    private static final int VIEW_TYPE_TOP = 0;
    private static final int VIEW_TYPE_MIDDLE = 1;
    private static final int VIEW_TYPE_BOTTOM = 2;

    public SessionsAdapter(ArrayList<Session> sessionsItems, Context context) {
        this.sessionsItems = sessionsItems;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link SessionsAdapter.ViewHolder} of the given type to
     * represent an item. This new ViewHolder should be constructed with a new View that can represent
     * the items of the given type. You can either create a new View manually or inflate it from an
     * XML layout file.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an
     *                 adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(SessionsAdapter.ViewHolder, int)
     */
    @NonNull
    @Override
    public SessionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, parent, false);
        return new SessionsAdapter.ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update
     * the contents of the {@link SessionsAdapter.ViewHolder#itemView} to reflect the item at the
     * given position.
     *
     * <p>Note that unlike {@link ListView}, RecyclerView will not call this method again if the
     * position of the item changes in the data set unless the item itself is invalidated or the new
     * position cannot be determined. For this reason, you should only use the <code>position</code>
     * parameter while acquiring the related data item inside this method and should not keep a copy
     * of it. If you need the position of an item later on (e.g. in a click listener), use {@link
     * SessionsAdapter.ViewHolder#getAdapterPosition()} which will have the updated adapter position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at
     *                 the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull SessionsAdapter.ViewHolder holder, int position) {
        Session item = sessionsItems.get(position);
        holder.mItemTitle.setText("Session " + item.getSession_num());
        holder.mItemSubtitle.setText(item.description);
        if (item.isFinished()) {
            holder.imageView.setImageResource(R.drawable.check);
            holder.mItemTitle.setTextColor(context.getResources().getColor(R.color.green));
            switch (getItemViewType(position)) {
                case VIEW_TYPE_TOP:
                    // The top of the line has to be rounded
                    holder.mItemLine.setBackgroundResource(R.drawable.line_bg_top_green);
                    break;
                case VIEW_TYPE_MIDDLE:
                    // Only the color could be enough
                    // but a drawable can be used to make the cap rounded also here
                    holder.mItemLine.setBackgroundResource(R.drawable.line_bg_middle_green);
                    break;
                case VIEW_TYPE_BOTTOM:
                    holder.mItemLine.setBackgroundResource(R.drawable.line_bg_bottom_green);
                    break;
            }
        } else {
            holder.imageView.setImageResource(R.drawable.not_yet);
            holder.mItemTitle.setTextColor(context.getResources().getColor(R.color.new_blue_dark));
            switch (getItemViewType(position)) {
                case VIEW_TYPE_TOP:
                    // The top of the line has to be rounded
                    holder.mItemLine.setBackgroundResource(R.drawable.line_bg_top);
                    break;
                case VIEW_TYPE_MIDDLE:
                    // Only the color could be enough
                    // but a drawable can be used to make the cap rounded also here
                    holder.mItemLine.setBackgroundResource(R.drawable.line_bg_middle);
                    break;
                case VIEW_TYPE_BOTTOM:
                    holder.mItemLine.setBackgroundResource(R.drawable.line_bg_bottom);
                    break;
            }
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return sessionsItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_TOP;
        else if (position == sessionsItems.size() - 1) return VIEW_TYPE_BOTTOM;
        else return VIEW_TYPE_MIDDLE;
    }

    /**
     * ****************************************************************************
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView mItemTitle;
        TextView mItemSubtitle;
        FrameLayout mItemLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView2);
            mItemTitle = itemView.findViewById(R.id.item_title);
            mItemSubtitle = itemView.findViewById(R.id.item_description);
            mItemLine = itemView.findViewById(R.id.item_line);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Session itemClicked = sessionsItems.get(position);
            Intent intent;
            if (isAdmin)
                intent = new Intent(context, AdminEditSessionActivity.class); // edit session
            else intent = new Intent(context, ViewSpecificSessionActivity.class); // show details

            intent.putExtra("course_name", itemClicked.getParentCourse());
            intent.putExtra("session_num", itemClicked.getSession_num());
            intent.putExtra("link", itemClicked.link);
            intent.putExtra("description", itemClicked.description);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }
}
