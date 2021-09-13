package ro.horon.android.auto.hdwidgets.car;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.google.android.apps.auto.sdk.CarActivity;

import ro.horon.android.auto.hdwidgets.R;

public class MainCarActivity extends CarActivity {
    private static final String TAG = "MainCarActivity";

    static final String MENU_HOME = "home";
    static final String MENU_DEBUG = "debug";
    static final String MENU_DEBUG_LOG = "log";
    static final String MENU_DEBUG_TEST_NOTIFICATION = "test_notification";
    static final String MENU_LISTVIEW = "listview";
    static final String MENU_WIDGET = "widget";

    private static final String FRAGMENT_DEMO = "demo";
    private static final String FRAGMENT_LOG = "log";
    private static final String FRAGMENT_LISTVIEW = "listview";
    private static final String FRAGMENT_WIDGET = "widget";

    private static final String CURRENT_FRAGMENT_KEY = "app_current_fragment";

    private static final int TEST_NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "car";

    private String mCurrentFragmentTag;
//    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle bundle) {
        setTheme(R.style.AppTheme_Car);
        super.onCreate(bundle);

        setContentView(R.layout.activity_car_main);

//        CarUiController carUiController = getCarUiController();
//        StatusBarController controller = carUiController.getStatusBarController();


//        SearchController search = carUiController.getSearchController();
//        if (search != null) {
//            try {
//                search.hideSearchBox();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        WidgetFragment widgetFragment01 = WidgetFragment.newInstance(1);
        WidgetFragment widgetFragment02 = WidgetFragment.newInstance(2);
        WidgetFragment widgetFragment03 = WidgetFragment.newInstance(3);
        WidgetFragment widgetFragment04 = WidgetFragment.newInstance(4);

        fragmentManager.beginTransaction()
                .add(R.id.carFrame01, widgetFragment01, GetTAG(1))
                .add(R.id.carFrame02, widgetFragment02, GetTAG(2))
                .add(R.id.carFrame03, widgetFragment03, GetTAG(3))
                .add(R.id.carFrame04, widgetFragment04, GetTAG(4))
                .commitNow();


        //MenuController menuController = carUiController.getMenuController();
        //menuController.setRootMenuAdapter(mainMenu);
        //menuController.showMenuButton();
//        carUiController.getStatusBarController();

//        StatusBarController statusBarController = carUiController.getStatusBarController();
        //statusBarController.setAppBarAlpha(1f);
        //statusBarController.setAppBarBackgroundColor(getResources().getColor(R.color.car_accent));
//        statusBarController.setTitle(getResources().getText(R.string.app_name));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private String GetTAG(int index) {
        return "widget_" + index;
    }
}
