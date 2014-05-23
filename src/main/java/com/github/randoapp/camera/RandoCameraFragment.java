package com.github.randoapp.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.commonsware.cwac.camera.acl.CameraFragment;

public class RandoCameraFragment extends CameraFragment {
    private static final String KEY_USE_FFC=
            "com.commonsware.cwac.camera.demo.USE_FFC";
    /*private MenuItem singleShotItem=null;
    private MenuItem autoFocusItem=null;
    private MenuItem takePictureItem=null;*/
    private boolean singleShotProcessing=false;

    public static RandoCameraFragment newInstance(boolean useFFC) {
        RandoCameraFragment f=new RandoCameraFragment();
        Bundle args=new Bundle();

        args.putBoolean(KEY_USE_FFC, useFFC);
        f.setArguments(args);

        return(f);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setHasOptionsMenu(true);
        setHost(new DemoCameraHost(getActivity()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*inflater.inflate(R.menu.camera, menu);
        takePictureItem=menu.findItem(R.id.camera);
        singleShotItem=menu.findItem(R.id.single_shot);
        singleShotItem.setChecked(getContract().isSingleShotMode());
        autoFocusItem=menu.findItem(R.id.autofocus);*/
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                if (singleShotItem.isChecked()) {
                    singleShotProcessing=true;
                    takePictureItem.setEnabled(false);
                }

                takePicture();

                return(true);

            case R.id.autofocus:
                takePictureItem.setEnabled(false);
                autoFocus();

                return(true);

            case R.id.single_shot:
                item.setChecked(!item.isChecked());
                getContract().setSingleShotMode(item.isChecked());

                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }*/

    boolean isSingleShotProcessing() {
        return(singleShotProcessing);
    }

    Contract getContract() {
        return((Contract)getActivity());
    }

    interface Contract {
        boolean isSingleShotMode();

        void setSingleShotMode(boolean mode);
    }

    class DemoCameraHost extends SimpleCameraHost {
        public DemoCameraHost(Context _ctxt) {
            super(_ctxt);
        }

        @Override
        public boolean useFrontFacingCamera() {
            return(getArguments().getBoolean(KEY_USE_FFC));
        }

        /*@Override
        public boolean useSingleShotMode() {
            return(singleShotItem.isChecked());
        }*/

        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {
            if (useSingleShotMode()) {
                singleShotProcessing=false;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //takePictureItem.setEnabled(true);
                    }
                });

               /* DisplayActivity.imageToShow=image;
                startActivity(new Intent(getActivity(), DisplayActivity.class));*/
            }
            else {
                super.saveImage(xact, image);
            }
        }

      /*  @Override
        public void autoFocusAvailable() {
            autoFocusItem.setEnabled(true);
        }*/

       /* @Override
        public void autoFocusUnavailable() {
            autoFocusItem.setEnabled(false);
        }*/

        @Override
        public void onCameraFail(CameraHost.FailureReason reason) {
            super.onCameraFail(reason);

            Toast.makeText(getActivity(),
                    "Sorry, but you cannot use the camera now!",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        @TargetApi(16)
        public void onAutoFocus(boolean success, Camera camera) {
            super.onAutoFocus(success, camera);

            //takePictureItem.setEnabled(true);
        }

    }
}