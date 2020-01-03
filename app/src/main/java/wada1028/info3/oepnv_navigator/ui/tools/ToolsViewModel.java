package wada1028.info3.oepnv_navigator.ui.tools;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ToolsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ToolsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hier ist eine Erweiterung möglich");
    }

    public LiveData<String> getText() {
        return mText;
    }
}