package wada1028.info3.oepnv_navigator.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import wada1028.info3.oepnv_navigator.R;

public class Dialog extends android.app.Dialog implements DialogInterface.OnClickListener {
    
    public Activity activity;
    public Dialog dialog;
    
    public Dialog(@NonNull Context context) {
        super(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
    }
}
