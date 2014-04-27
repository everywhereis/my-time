package com.qvdev.apps.everywhereis;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            createMockData();

            return rootView;
        }

        private void createMockData() {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            List<EventItem> items = new ArrayList<EventItem>();
            fetchCallLog(items);
            fetchImages(items);
            fetchTextMessages(items);
            fetchVideo(items);

            EventListAdapter adapter = new EventListAdapter(getActivity(), inflater, items);
            setListAdapter(adapter);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


        private String[] getSelectionArgs() {

            Calendar cal = Calendar.getInstance();


            cal.setTime(new Date()); // compute start of the day for the timestamp
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);


            Date startDate = new Date();
            startDate.setTime(cal.getTimeInMillis());


            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date endDate = new Date();


            Log.d(getClass().getSimpleName(), "DAY::" + String.valueOf(startDate.getTime()) + " END::" + String.valueOf(endDate.getTime()));

            String[] selectionArgs = new String[]{String.valueOf(startDate.getTime()), String.valueOf(endDate.getTime())};


            return selectionArgs;
        }

        private String prettyDate(String epochTime) {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd-MMM-yyyy HH:mm");

            String prettyDate = formatter.format(new Date(Long
                    .parseLong(epochTime)));

            return prettyDate;
        }

        private void fetchCallLog(List<EventItem> items) {


            Cursor c = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null,
                    CallLog.Calls.DATE + ">? AND " + CallLog.Calls.DATE + "<?",
                    getSelectionArgs(),
                    CallLog.Calls.DEFAULT_SORT_ORDER);

            Log.d(getClass().getSimpleName(), "Count::" + (c == null ? 0 : c.getCount()));
            int nrPos = c.getColumnIndex("name");
            int datePos = c.getColumnIndex("date");


            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                String dateString = prettyDate(c.getString(datePos));

                Log.d(getClass().getSimpleName(), /*"CALL::" + c.getString(nrPos) + */" DATE::" + c.getString(datePos));
                items.add(new CallEventItem(c.getString(nrPos), dateString));

            }

            c.close();

        }

        private void fetchTextMessages(List<EventItem> items) {
            Uri allMessages = Uri.parse("content://sms/");
            Cursor cursor = getActivity().getContentResolver().query(allMessages,
                    null,
                    CallLog.Calls.DATE + ">? AND " + CallLog.Calls.DATE + "<?",
                    getSelectionArgs(),
                    null);


            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String date = prettyDate(cursor.getString(cursor
                        .getColumnIndex("date")));
                Log.d(getClass().getSimpleName(), "DATE::" + date);

                String body = cursor.getString(cursor
                        .getColumnIndex("body"));
                Log.d(getClass().getSimpleName(), "BODY::" + body);

                String address = cursor.getString(cursor
                        .getColumnIndex("address"));
                Log.d(getClass().getSimpleName(), "ADDRESS::" + address);

                items.add(new TextEventItem(address, date));
            }
        }

        private void fetchImages(List<EventItem> items) {

            final String[] imageColumns = {
                    MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_TAKEN
            };

            final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
            Cursor imageCursor = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageColumns,
                    MediaStore.Images.Media.DATE_TAKEN + ">? AND " + MediaStore.Images.Media.DATE_TAKEN + "<?",
                    getSelectionArgs(),
                    imageOrderBy);

            for (imageCursor.moveToFirst(); !imageCursor.isAfterLast(); imageCursor.moveToNext()) {
                String id = imageCursor.getString(imageCursor
                        .getColumnIndex(MediaStore.Images.Media._ID));
                String date = prettyDate(imageCursor.getString(imageCursor
                        .getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));
                Log.d(getClass().getSimpleName(), id);

                items.add(new PhotoEventItem(id, date));
            }
        }

        private void fetchVideo(List<EventItem> items) {

            final String[] imageColumns = {
                    MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_TAKEN
            };

            final String imageOrderBy = MediaStore.Video.Media._ID + " DESC";
            Cursor imageCursor = getActivity().getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    imageColumns,
                    MediaStore.Video.Media.DATE_TAKEN + ">? AND " + MediaStore.Video.Media.DATE_TAKEN + "<?",
                    getSelectionArgs(),
                    imageOrderBy);

            for (imageCursor.moveToFirst(); !imageCursor.isAfterLast(); imageCursor.moveToNext()) {
                String id = imageCursor.getString(imageCursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                String date = prettyDate(imageCursor.getString(imageCursor
                        .getColumnIndex(MediaStore.Video.Media.DATE_TAKEN)));
                Log.d(getClass().getSimpleName(), id);

                items.add(new VideoEventItem(id, date));
            }
        }

    }

}
