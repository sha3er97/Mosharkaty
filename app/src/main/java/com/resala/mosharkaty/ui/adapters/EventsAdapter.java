package com.resala.mosharkaty.ui.adapters;

import static com.resala.mosharkaty.utility.classes.UtilityClass.isAdmin;

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

import com.resala.mosharkaty.EventDescriptionActivity;
import com.resala.mosharkaty.EventToEditActivity;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.EventItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private ArrayList<EventItem> eventItems;
    private Context context;

    public EventsAdapter(ArrayList<EventItem> eventItems, Context context) {
        this.eventItems = eventItems;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent an item.
     * This new ViewHolder should be constructed with a new View that can represent the items of the
     * given type. You can either create a new View manually or inflate it from an XML layout file.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     *     adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
   * @see #getItemViewType(int)
   * @see #onBindViewHolder(ViewHolder, int)
   */
  @NonNull
  @Override
  public EventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
    return new ViewHolder(view);
  }

  /**
   * Called by RecyclerView to display the data at the specified position. This method should update
   * the contents of the {@link ViewHolder#itemView} to reflect the item at the given position.
   *
   * <p>Note that unlike {@link ListView}, RecyclerView will not call this method again if the
   * position of the item changes in the data set unless the item itself is invalidated or the new
   * position cannot be determined. For this reason, you should only use the <code>position</code>
   * parameter while acquiring the related data item inside this method and should not keep a copy
   * of it. If you need the position of an item later on (e.g. in a click listener), use {@link
   * ViewHolder#getAdapterPosition()} which will have the updated adapter position.
   *
   * @param holder The ViewHolder which should be updated to represent the contents of the item at
   *     the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   */
  @Override
  public void onBindViewHolder(@NonNull EventsAdapter.ViewHolder holder, int position) {
    EventItem item = eventItems.get(position);
    holder.title.setText(item.getTitle());
    holder.date.setText(item.getDay());
    String url = item.getImgUrl();
    Picasso.get().load(url).into(holder.imageView);
  }

  /**
   * Returns the total number of items in the data set held by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override
  public int getItemCount() {
    return eventItems.size();
  }

  /** ***************************************************************************** */
  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imageView;
    TextView title;
    TextView date;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      imageView = itemView.findViewById(R.id.imageViewIMG_Res);
      title = itemView.findViewById(R.id.eventName);
      date = itemView.findViewById(R.id.date);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        EventItem itemClicked = eventItems.get(position);
        Intent intent;
        if (isAdmin) intent = new Intent(context, EventToEditActivity.class);
        else intent = new Intent(context, EventDescriptionActivity.class);
        intent.putExtra("title", itemClicked.getTitle());
        intent.putExtra("date", itemClicked.getDay());
        intent.putExtra("image", itemClicked.getImgUrl());
        intent.putExtra("description", itemClicked.getDescription());
        intent.putExtra("location", itemClicked.getLocation());
        intent.putExtra("time", itemClicked.getTime());
        intent.putExtra("key", itemClicked.getKey());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
  }
}
