package hk.hku.examoverflowhku.Token;

import android.os.AsyncTask;
import android.util.Log;


public class SendEmailTask extends AsyncTask {

    private String destination;
    private String tokenCode;

    public SendEmailTask(String destination, String tokenCode) {
        this.destination = destination;
        this.tokenCode = tokenCode;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        SendEmail sendEmail = new SendEmail();
        sendEmail.sendMail(this.destination, this.tokenCode);
        Log.i("SendEmailTask", "Sent successfully");
        return null;
    }

}
