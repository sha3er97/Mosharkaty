package com.resala.mosharkaty.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.MosharkaItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.resala.mosharkaty.LoginActivity.isAdmin;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.MessagesReadActivity.isManager;

public class MosharkatAdapter extends RecyclerView.Adapter<MosharkatAdapter.ViewHolder> {
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

  /**
   * **************************************************************************
   */
  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView name;
    TextView type;
    TextView date;
    ImageButton delete_btn;
    FirebaseDatabase database;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      name = itemView.findViewById(R.id.ListvolName);
      type = itemView.findViewById(R.id.ListmosharkaType);
      date = itemView.findViewById(R.id.ListMosharkadate);
      delete_btn = itemView.findViewById(R.id.delete_btn);
      delete_btn.setOnClickListener(this);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      int position = getAdapterPosition();
      MosharkaItem itemClicked = mosharkatItems.get(position);

      if (view.getId() == delete_btn.getId()) {
          if (isManager || !isAdmin) {
              database = FirebaseDatabase.getInstance();
            final DatabaseReference MosharkatRef =
                    database.getReference("mosharkat").child(userBranch);
            final Calendar cldr = Calendar.getInstance(Locale.US);
            int month = cldr.get(Calendar.MONTH) + 1;
              MosharkatRef.child(String.valueOf(month)).child(itemClicked.getKey()).setValue(null);
          } else {
              Toast.makeText(context, "illegal action : متقدرش تلغي المشاركة دي", Toast.LENGTH_SHORT)
                      .show();
          }
      } else {
        // do nothing till now
      }
    }
  }
}
