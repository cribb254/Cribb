package ke.co.movein.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ke.co.movein.R;


public class CustomDialog extends DialogFragment{

    public CustomDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        TextView tvMsg = (TextView) dialogView.findViewById(R.id.tv_msg);
        Button btnDismiss = (Button) dialogView.findViewById(R.id.btn_dismiss);

        tvTitle.setText(getArguments().getString("title"));
        tvMsg.setText(getArguments().getString("message"));
        btnDismiss.setText(getArguments().getString("action"));

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(dialogView);

        return builder.create();
    }

    public CustomDialog newInstance(String title, String message, String action) {
        CustomDialog f = new CustomDialog();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("action", action);
        f.setArguments(args);

        return f;
    }

}