package ro.horon.android.auto.hdwidgets;

import static android.content.ContentValues.TAG;

import static ro.horon.android.auto.hdwidgets.utils.Helpers.ARG_WidgetIndex;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import ro.horon.android.auto.hdwidgets.events.WidgetAddedEvent;
import ro.horon.android.auto.hdwidgets.events.WidgetRemovedEvent;
import ro.horon.android.auto.hdwidgets.utils.Helpers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WidgetPreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WidgetPreviewFragment extends Fragment {

    private static final int REQUEST_PICK_APPWIDGET = 0;
    public static final int REQUEST_CREATE_APPWIDGET = 10;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_PARAM2 = "param2";

    public static final String WIDGET_ID = "widget";
    public static final String PREFS_NAME = "widgetviewer";

    // TODO: Rename and change types of parameters
    private int WidgetIndex;
    private AppWidgetManager mAppWidgetManager;
    private AppWidgetHost mAppWidgetHost;

    private Button mButtonAddWidget;
    private Button mButtonRemoveWidget;
    private FrameLayout mWidgetContainer;

    private int widgetId = -1;


    public WidgetPreviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param widgetIndex   Parameter 1.
     * @param widgetManager Parameter 2.
     * @return A new instance of fragment WidgetPreviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WidgetPreviewFragment newInstance(int widgetIndex, AppWidgetManager widgetManager) {
        WidgetPreviewFragment fragment = new WidgetPreviewFragment();
        fragment.WidgetIndex = widgetIndex;
        fragment.mAppWidgetManager = widgetManager;
        Bundle args = new Bundle();
        args.putInt(ARG_WidgetIndex, widgetIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            WidgetIndex = args.getInt(ARG_WidgetIndex);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppWidgetHost.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (mAppWidgetHost != null) {
                mAppWidgetHost.stopListening();
            }
        } catch (Exception ex) {
            Log.e("OnPause", "Error stop listening", ex);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_widget_preview, container, false);

        mAppWidgetHost = new AppWidgetHost(getActivity(), 123456 + WidgetIndex);

        mWidgetContainer = view.findViewById(R.id.previewWidgetContainer);

        mButtonAddWidget = view.findViewById(R.id.previewWidgetAdd);
        mButtonRemoveWidget = view.findViewById(R.id.previewWidgetRemove);

        mButtonAddWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectWidget();
            }
        });

        mButtonRemoveWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeWidget();
            }
        });

        restoreWidgetId();

        if (widgetId != -1) {
            createWidget(widgetId, null);
        } else {
            setButtonsVisibility(false);
        }

        return view;
    }

    /**
     * Checks if the widget needs any configuration. If it needs, launches the
     * configuration activity.
     */
    public void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = -1;
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        }
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            //Toast.makeText(this, "Configure is not supported", Toast.LENGTH_SHORT).show();
            //removeWidget();
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            getActivity().startActivityForResult(intent, REQUEST_CREATE_APPWIDGET + WidgetIndex);
        } else {
            createWidget(data, appWidgetInfo);
        }
    }

    public void createWidget(Intent data, AppWidgetProviderInfo info) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            storeWidgetId();
        }
        createWidget(widgetId, info);

        EventBus.getDefault().post(new WidgetAddedEvent(WidgetIndex));
    }

    private String GetWidgetId() {
//        return "Widget_" + WidgetIndex;
        return Helpers.GetWidgetIdStorageKey(WidgetIndex);
    }

    public void SetAppWidgetManager(AppWidgetManager manager) {
        mAppWidgetManager = manager;
    }

    public void selectWidget() {
        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        pickIntent.putExtra(ARG_WidgetIndex, WidgetIndex);
        getActivity().startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET + WidgetIndex);
    }

    /**
     * Creates the widget and adds to our view layout.
     */
    private void createWidget(int appWidgetId, AppWidgetProviderInfo info) {
        if (info == null) {
            info = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        }
//        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        Activity activity = getActivity();
        if (activity != null) {
            AppWidgetHostView hostView = mAppWidgetHost.createView(activity.getApplicationContext(), appWidgetId, info);
            hostView.setAppWidget(appWidgetId, info);

            restoreWidgetId();

            mWidgetContainer.addView(hostView);

            setButtonsVisibility(true);
            Log.i(TAG, "The widget size is: " + info.minWidth + "*" + info.minHeight);
        }
        Log.i(TAG, "Could not get parent activity");
    }

    private void removeWidget() {
        mWidgetContainer.removeAllViews();
        if (widgetId != -1) {
            mAppWidgetHost.deleteAppWidgetId(widgetId);
        }
        widgetId = -1;
        storeWidgetId();
        setButtonsVisibility(false);
        EventBus.getDefault().post(new WidgetRemovedEvent(WidgetIndex));
    }

    private void setButtonsVisibility(boolean hasWidget) {
        if (hasWidget) {
            mButtonAddWidget.setVisibility(View.GONE);
            mButtonRemoveWidget.setVisibility(View.VISIBLE);
        } else {
            mButtonAddWidget.setVisibility(View.VISIBLE);
            mButtonRemoveWidget.setVisibility(View.GONE);
        }
    }

    private void restoreWidgetId() {
        Context context = getActivity();

        if (context != null) {
            widgetId = Helpers.GetWidgetId(context, WidgetIndex);
        }
    }

    private void storeWidgetId() {
        Context context = getActivity();

        if (context != null)
            context
                    .getSharedPreferences(GetWidgetId(), Context.MODE_PRIVATE)
                    .edit()
                    .putInt(GetWidgetId(), widgetId)
                    .apply();
    }

    /**
     * This avoids a bug in the com.android.settings.AppWidgetPickActivity,
     * which is used to select widgets. This just adds empty extras to the
     * intent, avoiding the bug.
     * <p>
     * See more: http://code.google.com/p/android/issues/detail?id=4272
     */
    void addEmptyData(Intent pickIntent) {
        ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }

}