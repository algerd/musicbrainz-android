package org.musicbrainz.android.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.musicbrainz.android.R;
import org.musicbrainz.android.communicator.ShowTitleCommunicator;
import org.musicbrainz.android.ui.CustomViewPager;
import org.musicbrainz.android.util.BottomNavigationBehavior;
import org.musicbrainz.android.util.ShowUtil;

public abstract class BaseBottomNavActivity extends BaseOptionsMenuActivity implements
        ShowTitleCommunicator {

    public static final String NAV_VIEW = "NAV_VIEW";

    protected int navViewId;
    protected boolean isLoading;
    protected boolean isError;

    protected BottomNavigationView bottomNavigationView;
    protected TextView topTitle;
    protected TextView bottomTitle;
    protected FrameLayout frameContainer;
    protected View error;
    protected View loading;
    protected CustomViewPager viewPager;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_bottom_nav;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState != null) {
            navViewId = getDefaultNavViewId() != -1 ? savedInstanceState.getInt(NAV_VIEW, getDefaultNavViewId()) : savedInstanceState.getInt(NAV_VIEW);
        } else {
            navViewId = getDefaultNavViewId() != -1 ? getIntent().getIntExtra(NAV_VIEW, getDefaultNavViewId()) : savedInstanceState.getInt(NAV_VIEW);
        }

        error = findViewById(R.id.error);
        loading = findViewById(R.id.loading);
        frameContainer = findViewById(R.id.frame_container);
        viewPager = findViewById(R.id.viewpager);
        topTitle = findViewById(R.id.toolbar_title_top);
        bottomTitle = findViewById(R.id.toolbar_title_bottom);

        onCreateActivity(savedInstanceState);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.inflateMenu(getBottomMenuId());
        bottomNavigationView.setOnNavigationItemSelectedListener(getOnNavigationItemSelectedListener());

        // attaching behaviours - hide / showFloatingActionButton on scroll
        ((CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams()).setBehavior(new BottomNavigationBehavior());

        refreshTokenAndLoad();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_VIEW, navViewId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navViewId = getDefaultNavViewId() != -1 ? savedInstanceState.getInt(NAV_VIEW, getDefaultNavViewId()) : savedInstanceState.getInt(NAV_VIEW);
    }


    protected abstract void onCreateActivity(Bundle savedInstanceState);

    protected abstract int getBottomMenuId();

    protected int getDefaultNavViewId() {
        return -1;
    }

    protected abstract BottomNavigationView.OnNavigationItemSelectedListener getOnNavigationItemSelectedListener();

    protected void refreshTokenAndLoad() {

    }

    protected void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            frameContainer.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            frameContainer.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    protected void viewError(boolean isView) {
        if (isView) {
            isError = true;
            viewPager.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }

    protected void showConnectionWarning(Throwable t) {
        ShowUtil.showError(this, t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> refreshTokenAndLoad());
    }

    protected void loadFragment(Fragment fragment) {
        viewPager.setVisibility(View.INVISIBLE);
        frameContainer.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public TextView getTopTitle() {
        return topTitle;
    }

    @Override
    public TextView getBottomTitle() {
        return bottomTitle;
    }

    public int getNavViewId() {
        return navViewId;
    }
}
