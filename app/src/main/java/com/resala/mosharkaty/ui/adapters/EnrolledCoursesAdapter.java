package com.resala.mosharkaty.ui.adapters;

import static com.resala.mosharkaty.utility.classes.UtilityClass.myRules;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userId;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.ViewSessionsActivity;
import com.resala.mosharkaty.utility.classes.Course;
import com.resala.mosharkaty.utility.classes.UtilityClass;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EnrolledCoursesAdapter
        extends RecyclerView.Adapter<EnrolledCoursesAdapter.ViewHolder> {
    private final ArrayList<Course> courseItems;
    private final Context context;
    DatabaseReference progressRef;
    DatabaseReference SessionsRef;
    FirebaseDatabase database;

    public EnrolledCoursesAdapter(ArrayList<Course> courseItems, Context context) {
        this.courseItems = courseItems;
        this.context = context;
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Called when RecyclerView needs a new {@link EnrolledCoursesAdapter.ViewHolder} of the given
     * type to represent an item. This new ViewHolder should be constructed with a new View that can
     * represent the items of the given type. You can either create a new View manually or inflate it
     * from an XML layout file.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an
     *                 adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(EnrolledCoursesAdapter.ViewHolder, int)
     */
    @NonNull
    @Override
    public EnrolledCoursesAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.course_enrolled_item, parent, false);
        return new EnrolledCoursesAdapter.ViewHolder(view);
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
    public void onBindViewHolder(@NonNull EnrolledCoursesAdapter.ViewHolder holder, int position) {
        Course item = courseItems.get(position);
        holder.title.setText(item.name);
        String url = UtilityClass.coursesImages.get(item.style);
        Picasso.get().load(url).into(holder.imageView);
        progressRef = database.getReference("progress").child(item.name);

        progressRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int finishedSessions = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.hasChild(userId)) {
                                finishedSessions++;
                            }
                        }
                        calculatePercent(finishedSessions, holder, item);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void calculatePercent(int finishedSessions, ViewHolder holder, Course item) {
        SessionsRef = database.getReference("sessions").child(item.name);
        SessionsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int sessionsCount = (int) dataSnapshot.getChildrenCount();
                        float percentage = (float) finishedSessions / sessionsCount * 100;
                        holder.determinateBar.setProgress(Math.round(percentage));
                        holder.progress_percent.setText(Math.round(percentage) + " %");
                        if (percentage < myRules.attendance_bad) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                holder.determinateBar.setProgressTintList(
                                        ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
                            }
                        } else if (percentage < myRules.attendance_medium) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                holder.determinateBar.setProgressTintList(
                                        ColorStateList.valueOf(
                                                context.getResources().getColor(R.color.new_yellow_light)));
                            }
                        } else { // bigger than both
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                holder.determinateBar.setProgressTintList(
                                        ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return courseItems.size();
    }

    /**
     * ****************************************************************************
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView title;
        TextView progress_percent;
        ProgressBar determinateBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.course_img);
            title = itemView.findViewById(R.id.title);
            progress_percent = itemView.findViewById(R.id.current_percent);
            determinateBar = itemView.findViewById(R.id.determinateBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Course itemClicked = courseItems.get(position);
            Intent intent;
            intent = new Intent(context, ViewSessionsActivity.class); // show sessions
            intent.putExtra("title", itemClicked.name);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }
}
