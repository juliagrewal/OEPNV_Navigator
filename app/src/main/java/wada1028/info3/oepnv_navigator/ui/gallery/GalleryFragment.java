package wada1028.info3.oepnv_navigator.ui.gallery;

import android.content.Intent;
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
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
                emailIntent.setType("vnd.android.cursor.item/email");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"OEPNVnavigator@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Email Subject");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "My email content");
                startActivity(Intent.createChooser(emailIntent, "E-mail senden mit..."));
            }
        });


        return root;
    }
}