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

import com.resala.mosharkaty.AdminAddSessionActivity;
import com.resala.mosharkaty.CourseDescriptionActivity;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.Course;
import com.resala.mosharkaty.utility.classes.UtilityClass;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ExploreCoursesAdapter extends RecyclerView.Adapter<ExploreCoursesAdapter.ViewHolder> {
    private final ArrayList<Course> cousreItems;
    private final Context context;

    public ExploreCoursesAdapter(ArrayList<Course> cousreItems, Context context) {
        this.cousreItems = cousreItems;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link ExploreCoursesAdapter.ViewHolder} of the given type
     * to represent an item. This new ViewHolder should be constructed with a new View that can
     * represent the items of the given type. You can either create a new View manually or inflate it
     * from an XML layout file.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an
     *                 adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ExploreCoursesAdapter.ViewHolder, int)
     */
    @NonNull
    @Override
    public ExploreCoursesAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.course_explore_item, parent, false);
        return new ExploreCoursesAdapter.ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update
     * the contents of the {@link ExploreCoursesAdapter.ViewHolder#itemView} to reflect the item at
     * the given position.
     *
     * <p>Note that unlike {@link ListView}, RecyclerView will not call this method again if the
     * position of the item changes in the data set unless the item itself is invalidated or the new
     * position cannot be determined. For this reason, you should only use the <code>position</code>
     * parameter while acquiring the related data item inside this method and should not keep a copy
     * of it. If you need the position of an item later on (e.g. in a click listener), use {@link
     * ExploreCoursesAdapter.ViewHolder#getAdapterPosition()} which will have the updated adapter
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at
     *                 the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ExploreCoursesAdapter.ViewHolder holder, int position) {
        Course item = cousreItems.get(position);
        holder.title.setText(item.name);
        holder.course_level.setText(item.level);
        holder.inst.setText(item.instructor);
        String url = UtilityClass.coursesImages.get(item.style);
        Picasso.get().load(url).into(holder.imageView);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return cousreItems.size();
    }

    /**
     * ****************************************************************************
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView title;
        TextView course_level;
        TextView inst;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.course_img);
            title = itemView.findViewById(R.id.title);
            course_level = itemView.findViewById(R.id.course_level);
            inst = itemView.findViewById(R.id.inst);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Course itemClicked = cousreItems.get(position);
            Intent intent;
            if (isAdmin)
                intent = new Intent(context, AdminAddSessionActivity.class); // add session or delete
            else intent = new Intent(context, CourseDescriptionActivity.class); // show details
            intent.putExtra("title", itemClicked.name);
            intent.putExtra("instructor", itemClicked.instructor);
            intent.putExtra("level", itemClicked.level);
            intent.putExtra("description", itemClicked.description);
            intent.putExtra("image", UtilityClass.coursesImages.get(itemClicked.style));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }
}
