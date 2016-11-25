package com.example.shivam.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.example.shivam.xyzreader.R;
import com.example.shivam.xyzreader.data.XYZProvider;
import com.example.shivam.xyzreader.extras.ArticleLoader;

import static com.example.shivam.xyzreader.extras.ArticleLoader.Query._ID;

public class DetailActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor> {

    private int mStartPosition;
    private long mStartId;
    private Cursor mCursor;
    private ViewPager mViewPager;
    private FragmentAdapter mPagerAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mContext = this;

        getLoaderManager().initLoader(0, null, this);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new FragmentAdapter(getFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mViewPager.setPageMarginDrawable(new ColorDrawable(Color.GRAY));

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = XYZProvider.XYZ.getItemId(getIntent().getData());
                mStartPosition = getIntent().getIntExtra(MainActivity.ITEM_POSITION, 0);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.getAllArticlesInstance(mContext);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mPagerAdapter.notifyDataSetChanged();

        if(mStartId >= 0 && mStartPosition >= 0 && mCursor != null) {
            mViewPager.setCurrentItem(mStartPosition, false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    public class FragmentAdapter extends FragmentStatePagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return DetailFragment.newInstance(mCursor.getLong(_ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}

