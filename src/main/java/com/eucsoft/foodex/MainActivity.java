package com.eucsoft.foodex;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.eucsoft.foodex.view.FoodView;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_screen, new PlaceholderFragment())
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

            Uri uri1 = Uri.parse("android.resource://com.eucsoft.foodex/"+R.drawable.f);
            Uri uri2 = Uri.parse("android.resource://com.eucsoft.foodex/"+R.drawable.f1);

            final View rootView = inflater.inflate(FoodView.getLayoutFragmentResource(container.getContext()), container, false);

            new FoodView(rootView, uri1).display();
            new FoodView(rootView, uri2).display();
            new FoodView(rootView, uri1).display();
            new FoodView(rootView, uri2).display();
            new FoodView(rootView, uri1).display();

            ImageButton takePictureButton = (ImageButton) rootView.findViewById(R.id.cameraButton);
            takePictureButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(rootView.getContext(), TakePictureActivity.class);
                    startActivityForResult(intent, 100);
                }
            });
            return rootView;
        }
    }
}
