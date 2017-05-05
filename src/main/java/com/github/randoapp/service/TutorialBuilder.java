package com.github.randoapp.service;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.ViewSwitcher;

import com.github.randoapp.R;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;
import com.wooplr.spotlight.utils.SpotlightSequence;

import static com.github.randoapp.MainActivity.activity;

public class TutorialBuilder {

    private Activity activity;
    private static final int delay = 400;
    private static final int showCaseDelay = 1000;

    public TutorialBuilder(Activity activity) {
        this.activity = activity;
    }

    private boolean isTutorialMode () {
        return true;
    }

    public TutorialBuilder learnTakeARando() {
        if (!isTutorialMode()) {
            return this;
        }

        boolean isCameraButtonShown = false;
        if (activity != null && activity.findViewById(R.id.camera_button) != null && activity.findViewById(R.id.camera_button).getVisibility() == View.VISIBLE) {
            isCameraButtonShown = true;
        }

        if (isCameraButtonShown) {
            showTutorial(R.id.camera_button, "Take your first rando", "Tap to rando button\nfor taking your first rando");
        }
        return this;
    }

    public TutorialBuilder learnClickToUploadedRandoToSeeLandingAnimation () {
        if (!isTutorialMode()) {
            return this;
        }

        boolean isOneRando = false;

        if (activity != null && activity.findViewById(R.id.colums_pager) != null && activity.findViewById(R.id.colums_pager).findViewWithTag("viewSwitcher") != null
            && ((ViewSwitcher) activity.findViewById(R.id.colums_pager).findViewWithTag("viewSwitcher")).getCurrentView() != null
                && ((ViewSwitcher) activity.findViewById(R.id.colums_pager).findViewWithTag("viewSwitcher")).getCurrentView().getVisibility() == View.VISIBLE) {
            isOneRando = true;
        }

        if (isOneRando) {
            final View targetView = ((ViewSwitcher) activity.findViewById(R.id.colums_pager).findViewWithTag("viewSwitcher")).getCurrentView();

            showTutorial(targetView, "Click to see map", "");
        }
        return this;
    }

    public TutorialBuilder learnWaitForUploading () {
        if (!isTutorialMode()) {
            return this;
        }

        final View targetView = ((ViewSwitcher) activity.findViewById(R.id.colums_pager).findViewWithTag("viewSwitcher")).getCurrentView();
        showTutorial(targetView, "This rando is uploading. Please wait", "", false);
/*
        PreferencesManager preferencesManager = new PreferencesManager(activity.getApplicationContext());
        boolean takeArRandoLearned = preferencesManager.isDisplayed(getViewNameByResId(R.id.camera_button));
        takeArRandoLearned = true;
        if (takeArRandoLearned) {
            boolean isOneRandoUploading = false;

            if (activity != null && activity.findViewById(R.id.colums_pager) != null && activity.findViewById(R.id.colums_pager).findViewWithTag("viewSwitcher") != null
                    && ((ViewSwitcher) activity.findViewById(R.id.colums_pager).findViewWithTag("viewSwitcher")).getCurrentView() != null
                    && ((ViewSwitcher) activity.findViewById(R.id.colums_pager).findViewWithTag("viewSwitcher")).getCurrentView().getVisibility() == View.VISIBLE) {
                isOneRandoUploading = true;
            }

            if (isOneRandoUploading) {
                final View targetView = ((ViewSwitcher) activity.findViewById(R.id.colums_pager).findViewWithTag("viewSwitcher")).getCurrentView();

                showTutorial(targetView, "This rando is uploading. Please wait", "", false);
            }
        }*/
        return this;
    }

    public TutorialBuilder learnCaptureImageWithTips() {
        if (!isTutorialMode()) {
            return this;
        }

        showTutorial(R.id.capture_button, "Take a image", "1. Don't be boring\n2.Try multiple times for best shot");
        return this;
    }

    public TutorialBuilder learnSwitchCameraToFrontForSelfie() {
        if (!isTutorialMode()) {
            return this;
        }

        showTutorial(R.id.camera_switch_button, "You can switch camera to front/back", "");
        return this;
    }

    public TutorialBuilder learnActivateRuleOfThird() {
        if (!isTutorialMode()) {
            return this;
        }

        showTutorial(R.id.grid_button, "Show grid for rule of third", "Rule of third help you compose scene");
        return this;
    }


    public TutorialBuilder learnHowToUploadImage() {
        PreferencesManager preferencesManager = new PreferencesManager(activity.getApplicationContext());
        preferencesManager.resetAll();

        if (!isTutorialMode()) {
            return this;
        }

        final View targetView = activity.findViewById(R.id.upload_button);
        showTutorial(targetView, "Click to upload", "1. Upload will be in background automatically");
        return this;
    }

    public TutorialBuilder learnSeeRecievedImageFromStranger() {
        return this;
    }

    public TutorialBuilder learnMapOfRecievedRando() {
        return this;
    }

    public TutorialBuilder learnLandedMap() {
        return this;
    }

    public TutorialBuilder learnActivateRuleOfThirdAndSwitchCameraToFrontForSelfie () {
        if (!isTutorialMode()) {
            return this;
        }

        PreferencesManager preferencesManager = new PreferencesManager(activity.getApplicationContext());
        boolean isCaptureImageLearned = preferencesManager.isDisplayed(getViewNameByResId(R.id.capture_button));
        if (isCaptureImageLearned) {
            final View gridButton = activity.findViewById(R.id.grid_button);
            final View cameraSwitchButton = activity.findViewById(R.id.camera_switch_button);

            SpotlightSequence.getInstance(activity, null)
                    .addSpotlight(gridButton, "Show grid for rule of third", "Rule of third help you compose scene", "gridButton")
                    .addSpotlight(cameraSwitchButton, "You can switch camera to front/back", "", "cameraSwitchButton")
                    .startSequence();
        }
        return this;
    }

    private void showTutorial(final int viewId, final String title, final String text) {
        showTutorial(activity.findViewById(viewId), title, text);
    }

    private void showTutorial(final View targetView, final String title, final String text) {
        showTutorial(targetView, title, text, true);
    }

    private void showTutorial(final View targetView, final String title, final String text, final boolean showArc) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new SpotlightView.Builder(activity)
                                .introAnimationDuration(delay)
                                .enableRevealAnimation(true)
                                .performClick(true)
                                .fadeinTextDuration(delay)
                                .headingTvColor(Color.WHITE)
                                .headingTvColor(Color.parseColor("#eb273f"))
                                .headingTvSize(32)
                                .headingTvText(title)
                                .subHeadingTvColor(Color.parseColor("#ffffff"))
                                .subHeadingTvSize(16)
                                .subHeadingTvText(text)
                                .maskColor(Color.parseColor("#dc000000"))
                                .target(targetView)
                                .lineAnimDuration(delay)
                                .lineAndArcColor(Color.parseColor("#eb273f"))
                                .dismissOnTouch(true)
                                .dismissOnBackPress(true)
                                .enableDismissAfterShown(true)
                                .showTargetArc(showArc)
                                .usageId(getViewName(targetView)) //UNIQUE ID
                                .show();
                    }}, showCaseDelay);

            }});
    }

    private String getViewNameByResId(int resId) {
        View view = activity.findViewById(resId);
        return getViewName(view);
    }

    private String getViewName(View view) {
        if (view == null || view.getTag() == null) {
            return "empty_tag";
        }

        return view.getTag().toString();
    }

}
