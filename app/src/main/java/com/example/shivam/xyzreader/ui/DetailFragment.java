package com.example.shivam.xyzreader.ui;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shivam.xyzreader.R;
import com.example.shivam.xyzreader.extras.ArticleLoader;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;
import static com.example.shivam.xyzreader.extras.ArticleLoader.Query.AUTHOR;
import static com.example.shivam.xyzreader.extras.ArticleLoader.Query.BODY;
import static com.example.shivam.xyzreader.extras.ArticleLoader.Query.PHOTO_URL;
import static com.example.shivam.xyzreader.extras.ArticleLoader.Query.PUBLISHED_DATE;
import static com.example.shivam.xyzreader.extras.ArticleLoader.Query.TITLE;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ITEM_ID = "item_id";

    private View mRootView;
    private long mItemId;
    private Cursor mCursor;
    private ImageView mPhotoView;

    private String fabMessage = "";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }
    }

    public DetailFragment() {
    }

    public static DetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        } else {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mPhotoView = (ImageView) mRootView.findViewById(R.id.article_image);

        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(fabMessage)
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        final Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar_detail);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        return mRootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.getInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;

        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        bindViews();
    }

    public DetailActivity getActivityCast() {
        return (DetailActivity) getActivity();
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        TextView titleView = (TextView) mRootView.findViewById(R.id.article_title);
        TextView bylineView = (TextView) mRootView.findViewById(R.id.article_byline);
        bylineView.setMovementMethod(new LinkMovementMethod());
        TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            String titleViewText = mCursor.getString(TITLE);

            String bylineViewText = Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by <font color='#ffffff'>"
                            + mCursor.getString(AUTHOR)
                            + "</font>").toString();

            String bodyViewText = Html.fromHtml(mCursor.getString(BODY)).toString();

            titleView.setText(titleViewText);
            bylineView.setText(bylineViewText);
            bodyView.setText(bodyViewText);

            fabMessage = titleViewText + "\n" + bylineViewText + "\n\n" + bodyViewText;

            String photoUrl = mCursor.getString(PHOTO_URL);

            try {
                Picasso.with(getActivity())
                        .load(photoUrl)
                        .into(mPhotoView);


            } catch (Exception e) {}
        } else {
            mRootView.setVisibility(View.INVISIBLE);
            titleView.setText("N/A");
            bylineView.setText("N/A");
            bodyView.setText("N/A");
        }
    }
}
