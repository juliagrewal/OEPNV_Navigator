package wada1028.info3.oepnv_navigator.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import wada1028.info3.oepnv_navigator.R;

public class GalleryFragment extends Fragment {


    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textViewAnschrift = root.findViewById(R.id.text_anschrift);
        final TextView textViewKontakt =root.findViewById(R.id.text_kontakt);
        final TextView textViewEmailemail= root.findViewById(R.id.text_emailemail);
        final TextView textViewEmail= root.findViewById(R.id.text_email);
        final TextView textViewRechtsform= root.findViewById(R.id.text_rechtsform);


        return root;
    }
}