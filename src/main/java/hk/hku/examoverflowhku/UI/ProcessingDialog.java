package hk.hku.examoverflowhku.UI;

import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import hk.hku.examoverflowhku.R;

public class ProcessingDialog
{
    AlertDialog alertDialog;
    View rootView;

    /**
     * Constructor, to build a ProcessingDialog instance for further usage
     * @param rootView The view a processing dialog should be bound to
     */
    public ProcessingDialog(View rootView)
    {
        this.rootView = rootView;
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setView(View.inflate(rootView.getContext(), R.layout.processing_dialog,null));
        alertDialog = builder.create();
    }

    /**
     * Constructor, to build a ProcessingDialog instance with customized title
     * @param rootView The view a processing dialog should be bound to
     * @param resId The customized string title
     */
    public ProcessingDialog(View rootView, int resId)
    {
        this.rootView = rootView;
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        View dialogView = View.inflate(rootView.getContext(), R.layout.processing_dialog, null);
        TextView textView = dialogView.findViewById(R.id.progress_text_prompt);
        textView.setText(resId);
        builder.setView(dialogView);
        alertDialog = builder.create();
    }

    /**
     * Display the dialog in the context
     */
    public void show()
    {
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     * Dismiss the dialog
     */
    public void dismiss()
    {
        alertDialog.cancel();
    }

}