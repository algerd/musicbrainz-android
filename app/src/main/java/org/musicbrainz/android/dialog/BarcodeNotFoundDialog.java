package org.musicbrainz.android.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.musicbrainz.android.R;
import org.musicbrainz.android.intent.ActivityFactory;

import static org.musicbrainz.android.MusicBrainzApp.oauth;


public class BarcodeNotFoundDialog extends DialogFragment {

    public static final String TAG = "BarcodeNotFoundDialog";

    private static final String BARCODE = "barcode";

    public interface DialogFragmentListener {
        void addBarcode();
    }

    public static BarcodeNotFoundDialog newInstance(String barcode) {
        Bundle args = new Bundle();
        args.putString(BARCODE, barcode);

        BarcodeNotFoundDialog fragment = new BarcodeNotFoundDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.barcode_header, getArguments().getString(BARCODE)));
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> getDialog().cancel());

        if (oauth.hasAccount()) {
            builder.setMessage(R.string.barcode_info_log);
            builder.setNegativeButton(R.string.barcode_cancel,
                    (dialog, which) -> getActivity().finish());
            builder.setPositiveButton(R.string.barcode_btn,
                    (dialog, which) -> ((DialogFragmentListener) getContext()).addBarcode());
        } else {
            builder.setMessage(R.string.barcode_info_nolog);
            builder.setPositiveButton(R.string.login, (dialog, which) -> {
                ActivityFactory.startLoginActivity(getContext());
                getActivity().finish();
            });
        }
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setCanceledOnTouchOutside(false);
    }

}
