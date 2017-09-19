package com.benio.training.class17;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benio.training.R;

public class LayoutChangesActivity extends AppCompatActivity {
    /**
     * A static list of country names.
     */
    private static final String[] COUNTRIES = new String[]{
            "Belgium", "France", "Italy", "Germany", "Spain",
            "Austria", "Russia", "Poland", "Croatia", "Greece",
            "Ukraine",
    };
    /**
     * The container view which has layout change animations turned on. In this sample, this view
     * is a {@link android.widget.LinearLayout}.
     */
    private ViewGroup mContainerView;

    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_changes);

        mContainerView = (ViewGroup) findViewById(R.id.container);
        mContainerView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                mEmptyView.setVisibility(View.GONE);
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                // If there are no rows remaining, show the empty view.
                if (mContainerView.getChildCount() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                }
            }
        });
        mEmptyView = findViewById(android.R.id.empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(Menu.NONE, R.id.action_share, Menu.NONE, "add");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                addItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addItem() {
        // Instantiate a new "row" view.
        final View itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_layout_changes, mContainerView, false);

        // Set the text in the new row to a random country.
        TextView textView = (TextView) itemView.findViewById(android.R.id.text1);
        textView.setText(COUNTRIES[(int) (Math.random() * COUNTRIES.length)]);

        // Set a click listener for the "X" button in the row that will remove the row.
        itemView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the row from its parent (the container view).
                // Because mContainerView has android:animateLayoutChanges set to true,
                // this removal is automatically animated.
                mContainerView.removeView(itemView);
            }
        });

        // Because mContainerView has android:animateLayoutChanges set to true,
        // adding this view is automatically animated.
        mContainerView.addView(itemView, 0);
    }
}
