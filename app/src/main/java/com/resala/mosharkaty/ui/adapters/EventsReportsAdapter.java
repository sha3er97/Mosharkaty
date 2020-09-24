package com.resala.mosharkaty.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.resala.mosharkaty.EventReportDescriptionActivity;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.EventReport;

import java.util.ArrayList;

public class EventsReportsAdapter extends RecyclerView.Adapter<EventsReportsAdapter.ViewHolder> {
  private ArrayList<EventReport> eventReports;
  private Context context;

  public EventsReportsAdapter(ArrayList<EventReport> eventReports, Context context) {
    this.eventReports = eventReports;
    this.context = context;
  }

  /**
   * Called when RecyclerView needs a new {@link EventsReportsAdapter.ViewHolder} of the given type
   * to represent an item. This new ViewHolder should be constructed with a new View that can
   * represent the items of the given type. You can either create a new View manually or inflate it
   * from an XML layout file.
   *
   * @param parent   The ViewGroup into which the new View will be added after it is bound to an
   *                 adapter position.
   * @param viewType The view type of the new View.
   * @return A new ViewHolder that holds a View of the given view type.
   * @see #getItemViewType(int)
   * @see #onBindViewHolder(EventsReportsAdapter.ViewHolder, int)
   */
  @NonNull
  @Override
  public EventsReportsAdapter.ViewHolder onCreateViewHolder(
          @NonNull ViewGroup parent, int viewType) {
    View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_item, parent, false);
    return new EventsReportsAdapter.ViewHolder(view);
  }

  /**
   * Called by RecyclerView to display the data at the specified position. This method should update
   * the contents of the {@link EventsReportsAdapter.ViewHolder#itemView} to reflect the item at the
   * given position.
   *
   * <p>Note that unlike {@link ListView}, RecyclerView will not call this method again if the
   * position of the item changes in the data set unless the item itself is invalidated or the new
   * position cannot be determined. For this reason, you should only use the <code>position</code>
   * parameter while acquiring the related data item inside this method and should not keep a copy
   * of it. If you need the position of an item later on (e.g. in a click listener), use {@link
   * EventsReportsAdapter.ViewHolder#getAdapterPosition()} which will have the updated adapter
   * position.
   *
   * @param holder   The ViewHolder which should be updated to represent the contents of the item at
   *                 the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   */
  @Override
  public void onBindViewHolder(@NonNull EventsReportsAdapter.ViewHolder holder, int position) {
    EventReport item = eventReports.get(position);
    holder.type.setText(item.type);
    holder.countTV.setText(item.count);
    holder.locationTV.setText(item.location);
    holder.dateTV.setText(item.date);
  }

  /**
   * Returns the total number of items in the data set held by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override
  public int getItemCount() {
    return eventReports.size();
  }

  /**
   * **************************************************************************
   */
  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView type;
    TextView countTV;
    TextView locationTV;
    TextView dateTV;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      type = itemView.findViewById(R.id.meetingType);
      locationTV = itemView.findViewById(R.id.reasonTV);
      dateTV = itemView.findViewById(R.id.dateTV);
      countTV = itemView.findViewById(R.id.countTV);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      int position = getAdapterPosition();
      EventReport itemClicked = eventReports.get(position);
      Intent intent;
      intent = new Intent(context, EventReportDescriptionActivity.class);
      intent.putExtra("type", itemClicked.type);
      intent.putExtra("location", itemClicked.location);
      intent.putExtra("money", itemClicked.money);
      intent.putExtra("count", itemClicked.count);
      intent.putExtra("date", itemClicked.date);
      intent.putExtra("head", itemClicked.head);
      intent.putExtra("description", itemClicked.description);
      intent.putExtra("key", itemClicked.getKey());
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      context.startActivity(intent);
    }
  }
}
