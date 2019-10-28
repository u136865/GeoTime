package com.locactio.geotime.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.locactio.geotime.R;
import com.locactio.geotime.fragments.MainFragment;

public class CustomConfirmDialog extends Dialog {
    private Fragment c;
    private String action;

    public CustomConfirmDialog(Fragment a, String action) {
        super(a.getActivity());
        this.c = a;
        this.action = action;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_confirm_dialog);
        TextView b = findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomConfirmDialog.this.dismiss();
                ((MainFragment)c).callClock(action);
            }
        });
    }
}
