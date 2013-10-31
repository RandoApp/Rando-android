package com.eucsoft.foodex;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;
import android.widget.LinearLayout;

import com.eucsoft.foodex.com.eucsoft.foodex.view.FoodLandscapeView;
import com.eucsoft.foodex.com.eucsoft.foodex.view.FoodView;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.homeland, container, false);
            int ORIENTATION_PORTRAIT = 1;
            int ORIENTATION_LANDSCAPE = 2;
            if(getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
                rootView = inflater.inflate(R.layout.homeland2, container, false);
                LinearLayout column1 = (LinearLayout) rootView.findViewById(R.id.column1);
                LinearLayout column2 = (LinearLayout) rootView.findViewById(R.id.column2);
                FoodLandscapeView foodLandscapeView1 = new FoodLandscapeView(column1, column2, R.drawable.f);
                FoodLandscapeView foodLandscapeView2 = new FoodLandscapeView(column1, column2, R.drawable.f1);
                FoodLandscapeView foodLandscapeView3 = new FoodLandscapeView(column1, column2, R.drawable.f);
                FoodLandscapeView foodLandscapeView4 = new FoodLandscapeView(column1, column2, R.drawable.f1);
                FoodLandscapeView foodLandscapeView5 = new FoodLandscapeView(column1, column2, R.drawable.f);
                FoodLandscapeView foodLandscapeView6 = new FoodLandscapeView(column1, column2, R.drawable.f1);

            } else {
                rootView = inflater.inflate(R.layout.home, container, false);
                LinearLayout foodContainer = (LinearLayout) rootView.findViewById(R.id.foodContainer);
                FoodView foodView = new FoodView(rootView.getContext(), R.drawable.f);
                foodContainer.addView(foodView.getView());
                FoodView foodView1 = new FoodView(rootView.getContext(), R.drawable.f1);
                foodContainer.addView(foodView1.getView());
            }
            return rootView;
        }
    }

}
