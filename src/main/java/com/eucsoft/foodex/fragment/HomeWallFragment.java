package com.eucsoft.foodex.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.view.FoodView;

import java.util.List;

public class HomeWallFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(FoodView.getLayoutFragmentResource(container.getContext()), container, false);

        List<FoodPair> foods = new FoodDAO(container.getContext()).getAllFoodPairs();

        for (FoodPair foodPair : foods) {
            new FoodView(rootView, foodPair).display();
        }
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
