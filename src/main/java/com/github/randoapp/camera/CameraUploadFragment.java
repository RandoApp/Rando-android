package com.github.randoapp.camera;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.task.CropImageTask;
import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.BitmapUtil;
import com.makeramen.RoundedImageView;

import java.util.Map;

import static com.github.randoapp.Constants.CAMERA_BROADCAST_EVENT;

public class CameraUploadFragment extends SherlockFragment {

    private String picFileName;
    private ImageView preview;
    private ImageView uploadPictureButton;
    private int displayWidth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.camera_upload, container, false);

        preview = (RoundedImageView) rootView.findViewById(R.id.preview);

        Bundle bundle = getArguments();
        String fileToCrop = bundle.getString(Constants.FILEPATH);

        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        displayWidth = display.getWidth();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(displayWidth, displayWidth);
        preview.setLayoutParams(layoutParams);

        prepareForUpload(fileToCrop);

        uploadPictureButton = (ImageView) rootView.findViewById(R.id.upload_button);
        uploadPictureButton.setOnClickListener(new UploadButtonListner());
        return rootView;
    }


    private void prepareForUpload(String fileToCrop) {
        new CropImageTask(fileToCrop)
                .onOk(new OnOk() {
                    @Override
                    public void onOk(Map<String, Object> data) {
                        picFileName = (String) data.get(Constants.FILEPATH);
                        preview.setImageBitmap(BitmapUtil.decodeSampledBitmap(picFileName, displayWidth, displayWidth));
                    }
                })
                .onError(new OnError() {
                    @Override
                    public void onError(Map<String, Object> data) {
                    }
                })
                .onDone(new OnDone() {
                    @Override
                    public void onDone(Map<String, Object> data) {

                    }
                })
                .execute();
    }

    private class UploadButtonListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO: return to Home screen and start UploadService;
            Intent intent = new Intent(CAMERA_BROADCAST_EVENT);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    }
}
