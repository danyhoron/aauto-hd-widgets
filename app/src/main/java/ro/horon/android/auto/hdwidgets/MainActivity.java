package ro.horon.android.auto.hdwidgets;

import static ro.horon.android.auto.hdwidgets.ConfigurationActivity.REQUEST_PICK_APPWIDGET;
import static ro.horon.android.auto.hdwidgets.WidgetPreviewFragment.REQUEST_CREATE_APPWIDGET;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.EventBus;

import ro.horon.android.auto.hdwidgets.events.WidgetAddedEvent;


public class MainActivity extends AppCompatActivity {

    private WidgetPreviewFragment widget01;
    private WidgetPreviewFragment widget02;
    private WidgetPreviewFragment widget03;
    private WidgetPreviewFragment widget04;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        if (savedInstanceState == null) {
            widget01 = WidgetPreviewFragment.newInstance(1, mAppWidgetManager);
            widget02 = WidgetPreviewFragment.newInstance(2, mAppWidgetManager);
            widget03 = WidgetPreviewFragment.newInstance(3, mAppWidgetManager);
            widget04 = WidgetPreviewFragment.newInstance(4, mAppWidgetManager);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout01, widget01, GetTAG(1))
                    .replace(R.id.frameLayout02, widget02, GetTAG(2))
                    .replace(R.id.frameLayout03, widget03, GetTAG(3))
                    .replace(R.id.frameLayout04, widget04, GetTAG(4))
                    .commitNow();
        } else {
            widget01 = (WidgetPreviewFragment) getSupportFragmentManager().findFragmentByTag(GetTAG(1));
            widget02 = (WidgetPreviewFragment) getSupportFragmentManager().findFragmentByTag(GetTAG(2));
            widget03 = (WidgetPreviewFragment) getSupportFragmentManager().findFragmentByTag(GetTAG(3));
            widget04 = (WidgetPreviewFragment) getSupportFragmentManager().findFragmentByTag(GetTAG(4));

            widget01.SetAppWidgetManager(mAppWidgetManager);
            widget02.SetAppWidgetManager(mAppWidgetManager);
            widget03.SetAppWidgetManager(mAppWidgetManager);
            widget04.SetAppWidgetManager(mAppWidgetManager);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int index = -1;
        int code = -1;
        if (requestCode > REQUEST_CREATE_APPWIDGET) {
            index = requestCode - REQUEST_CREATE_APPWIDGET;
        } else {
            index = requestCode - REQUEST_PICK_APPWIDGET;
        }
        code = requestCode - index;

//        if ((requestCode >= REQUEST_PICK_APPWIDGET && requestCode <= REQUEST_CREATE_APPWIDGET + 4)) {
        if (code == REQUEST_PICK_APPWIDGET || code == REQUEST_CREATE_APPWIDGET)
            if (resultCode == RESULT_OK) {
                WidgetPreviewFragment fragment = null;
//                int index = data.getIntExtra(ARG_WidgetIndex, 0);
//                int code = requestCode - index;
                switch (index) {
                    case 1:
                        fragment = widget01;
                        break;
                    case 2:
                        fragment = widget02;
                        break;
                    case 3:
                        fragment = widget03;
                        break;
                    case 4:
                        fragment = widget04;
                        break;
                }
                if (code == REQUEST_PICK_APPWIDGET) {
                    if (fragment != null) {
                        fragment.configureWidget(data);
                    }
//                    configureWidget(data);
                } else {
                    if (fragment != null) {
                        fragment.createWidget(data, null);
                    }
//                    fragment.createWidget(data, null);
                }
            }
    }

    private String GetTAG(int index) {
        return "Widget_" + index;
    }
}
