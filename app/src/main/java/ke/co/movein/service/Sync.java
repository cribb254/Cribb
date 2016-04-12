package ke.co.movein.service;

import android.app.IntentService;
import android.content.Intent;

import ke.co.movein.utility.Functions;

public class Sync extends IntentService {

    public Sync() {
        super("Sync");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            new Functions(getApplicationContext()).autoSync();
        }
    }

}
