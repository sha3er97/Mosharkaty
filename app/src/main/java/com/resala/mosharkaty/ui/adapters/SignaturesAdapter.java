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
import com.resala.mosharkaty.utility.classes.Sig;

import java.util.ArrayList;

public class SignaturesAdapter extends RecyclerView.Adapter<SignaturesAdapter.ViewHolder> {
    private ArrayList<Sig> signatureitems;
    private Context context;

    public SignaturesAdapter(ArrayList<Sig> signatureitems, Context context) {
        this.signatureitems = signatureitems;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link SignaturesAdapter.ViewHolder} of the given type to
     * represent an item. This new ViewHolder should be constructed with a new View that can represent
     * the items of the given type. You can either create a new View manually or inflate it from an
     * XML layout file.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an
     *                 adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(SignaturesAdapter.ViewHolder, int)
     */
    @NonNull
    @Override
    public SignaturesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.signature_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update
     * the contents of the {@link SignaturesAdapter.ViewHolder#itemView} to reflect the item at the
     * given position.
     *
     * <p>Note that unlike {@link ListView}, RecyclerView will not call this method again if the
     * position of the item changes in the data set unless the item itself is invalidated or the new
     * position cannot be determined. For this reason, you should only use the <code>position</code>
     * parameter while acquiring the related data item inside this method and should not keep a copy
     * of it. If you need the position of an item later on (e.g. in a click listener), use {@link
     * SignaturesAdapter.ViewHolder#getAdapterPosition()} which will have the updated adapter
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at
     *                 the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull SignaturesAdapter.ViewHolder holder, int position) {
        Sig item = signatureitems.get(position);
        holder.name.setText(item.volName);
        holder.time.setText(item.signatureDate);
        //        if (item.getComment().isEmpty()) holder.comment.setVisibility(View.GONE);
        //        else holder.comment.setText(item.getComment());
        holder.comment.setText(item.comment);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return signatureitems.size();
    }

    /**
     * **************************************************************************
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView comment;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userName);
            comment = itemView.findViewById(R.id.comment);
            time = itemView.findViewById(R.id.time);
        }
    }
}
