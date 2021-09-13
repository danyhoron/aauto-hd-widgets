package ro.horon.android.auto.hdwidgets.car;

import static ro.horon.android.auto.hdwidgets.utils.Helpers.ARG_WidgetIndex;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ro.horon.android.auto.hdwidgets.R;
import ro.horon.android.auto.hdwidgets.events.WidgetAddedEvent;
import ro.horon.android.auto.hdwidgets.events.WidgetRemovedEvent;
import ro.horon.android.auto.hdwidgets.utils.Helpers;


/**
 * Created by ns130291 on 27.01.2018.
 */

public class WidgetFragment extends CarFragment {

    private static final String TAG = WidgetFragment.class.getName();

    private FrameLayout mWidgetContainer;

    private AppWidgetManager mAppWidgetManager;
    private AppWidgetHost mAppWidgetHost;
    private AppWidgetHostView mHostView;

    private int widgetID = -1;
    private int widgetIndex = -1;

    public WidgetFragment() {
        setTitle("WidgetViewer");
    }

    public static WidgetFragment newInstance(int index) {
        WidgetFragment fragment = new WidgetFragment();
        fragment.widgetIndex = index;
        Bundle args = new Bundle();
        args.putInt(ARG_WidgetIndex, index);
        fragment.setArguments(args);
        return fragment;
    }

//    public WidgetFragment(int idx) {
//
//        widgetIndex = idx;
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d(TAG, "onAttach");

        mAppWidgetManager = AppWidgetManager.getInstance(context);
        mAppWidgetHost = new AppWidgetHost(context, 123456 + widgetIndex);

        widgetID = Helpers.GetWidgetId(context, widgetIndex);

//        widgetID = context.getSharedPreferences(ConfigurationActivity.PREFS_NAME, Context.MODE_PRIVATE)
//                .getInt(ConfigurationActivity.WIDGET_ID, -1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWidgetAdded(WidgetAddedEvent event) {
        if (widgetIndex != event.WidgetIndex) {
            return;
        }
        Context context = getContext();
        widgetID = Helpers.GetWidgetId(context, widgetIndex);
        createWidget(widgetID);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWidgetRemoved(WidgetRemovedEvent event) {
        if (widgetIndex != event.WidgetIndex) {
            return;
        }
        mWidgetContainer.removeAllViews();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_widget, container, false);

        mWidgetContainer = view.findViewById(R.id.widget_container);

        /*int widgetID = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(ConfigurationActivity.WIDGET_ID, -1);
        if(widgetID != -1) {
            createWidget(widgetID);
        } else {
            Log.d(TAG, "No widget selected");
        }*/

        /*FragmentActivity activity = getActivity();
        if(activity != null) {
            widgetID = activity.getPreferences(Context.MODE_PRIVATE).getInt(ConfigurationActivity.WIDGET_ID, -1);
            Log.d(TAG, "widget ID: " + widgetID);
            createWidget(widgetID);
        } else {
            Log.e(TAG, "activity is null!");
        }*/
        if (widgetID != -1) {
            Log.d(TAG, "widget ID: " + widgetID);
            createWidget(widgetID);
        } else {
            Log.d(TAG, "no widget selected");
        }

        Log.i(TAG, "Frame size: " + mWidgetContainer.getWidth() + "*" + mWidgetContainer.getHeight());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
        mAppWidgetHost.startListening();

        resizeWidget();
    }

    private void resizeWidget() {

        mWidgetContainer.post(new Runnable() {
            @Override
            public void run() {
                if (mHostView == null) {
                    return;
                }
                mHostView.updateAppWidgetSize(null, mWidgetContainer.getWidth() - 100, mWidgetContainer.getHeight() - 100, mWidgetContainer.getWidth() - 100, mWidgetContainer.getHeight() - 100);
                AppWidgetProviderInfo appWidgetInfo = mHostView.getAppWidgetInfo();
                Log.i(TAG, "Frame size: " + mWidgetContainer.getWidth() + "*" + mWidgetContainer.getHeight());
                Log.i(TAG, "The widget start size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
                Log.i(TAG, "The min widget size is: " + appWidgetInfo.minResizeWidth + "*" + appWidgetInfo.minResizeHeight);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
        try {
            if (mAppWidgetHost != null) {
                mAppWidgetHost.stopListening();
            }
        } catch (Exception ex) {
            Log.e("Error", "OnPause", ex);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            widgetIndex = args.getInt(ARG_WidgetIndex);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void createWidget(int appWidgetId) {
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        mHostView = mAppWidgetHost.createView(getContext(), appWidgetId, appWidgetInfo);
        mHostView.setAppWidget(appWidgetId, appWidgetInfo);

        mWidgetContainer.addView(mHostView);

        mAppWidgetHost.startListening();

//        resizeWidget();

        //mHostView.updateAppWidgetSize(null, mWidgetContainer.getWidth(), mWidgetContainer.getHeight(), mWidgetContainer.getWidth(), mWidgetContainer.getHeight());
        Log.i(TAG, "Frame size: " + mWidgetContainer.getWidth() + "*" + mWidgetContainer.getHeight());
        Log.i(TAG, "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
    }
}
