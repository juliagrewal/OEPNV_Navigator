package wada1028.info3.oepnv_navigator.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hier ist eine Erweiterung möglich");
    }

    public LiveData<String> getText() {
        return mText;
    }
}