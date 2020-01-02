package wada1028.info3.oepnv_navigator.ui.home;

import android.app.Application;

import java.util.HashMap;
import java.util.List;

class GlobalApplication extends Application {
    List<HashMap> journeyList = null;
    public List<HashMap> getJourneyList() {
        return journeyList;
    }
    public void setJourneyList(List<HashMap> journeyList) {
        this.journeyList = journeyList;
    }
}
