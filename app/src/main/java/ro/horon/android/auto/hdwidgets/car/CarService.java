package ro.horon.android.auto.hdwidgets.car;

import com.google.android.apps.auto.sdk.CarActivity;
import com.google.android.apps.auto.sdk.CarActivityService;

import ro.horon.android.auto.hdwidgets.car.MainCarActivity;

public class CarService extends CarActivityService {
    public Class<? extends CarActivity> getCarActivity() {
        return MainCarActivity.class;
    }
}
