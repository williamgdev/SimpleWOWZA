package com.randmcnally.bb.poc.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.randmcnally.bb.poc.R;


public class DialogTextFragment extends DialogFragment {
    private EditText txtEdit;
    private Button bSave;
    private String title;
    ListenerDialogFragment listenerDialogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_text, container,
                false);
        getDialog().setTitle(title != null ? title : "Dialog");
        txtEdit = (EditText) rootView.findViewById(R.id.dialog_text_txt);
        bSave = (Button) rootView.findViewById(R.id.dialog_text_button_save);

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenerDialogFragment != null)
                    listenerDialogFragment.sendText(txtEdit.getText().toString());
                dismiss();
            }
        });

        return rootView;
    }

    public void setListenerDialogFragment(ListenerDialogFragment listenerDialogFragment){
        this.listenerDialogFragment = listenerDialogFragment;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public interface ListenerDialogFragment{
        void sendText(String text);
    }
}
