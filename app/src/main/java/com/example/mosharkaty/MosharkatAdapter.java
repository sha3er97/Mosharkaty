package com.example.mosharkaty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class MosharkatAdapter extends RecyclerView.Adapter<MosharkatAdapter.ViewHolder> {
  private ArrayList<MosharkaItem> mosharkatItems;
  private Context context;

  public MosharkatAdapter(ArrayList<MosharkaItem> mosharkatItems, Context context) {
    this.mosharkatItems = mosharkatItems;
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
  public MosharkatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.mosharka_item, parent, false);
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
  public void onBindViewHolder(@NonNull MosharkatAdapter.ViewHolder holder, int position) {
    MosharkaItem item = mosharkatItems.get(position);
    holder.name.setText(item.getVolname());
    holder.date.setText(item.getMosharkaDate());
    holder.type.setText(item.getMosharkaType());
  }

  /**
   * Returns the total number of items in the data set held by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override
  public int getItemCount() {
    return mosharkatItems.size();
  }

  /** ***************************************************************************** */
  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView name;
    TextView type;
    TextView date;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      name = itemView.findViewById(R.id.ListvolName);
      type = itemView.findViewById(R.id.ListmosharkaType);
      date = itemView.findViewById(R.id.ListMosharkadate);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      int position = getAdapterPosition();
      MosharkaItem itemClicked = mosharkatItems.get(position);
      // do nothing till now
    }
  }
}
