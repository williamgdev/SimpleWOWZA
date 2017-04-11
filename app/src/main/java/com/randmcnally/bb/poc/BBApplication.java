package com.randmcnally.bb.poc;

import android.app.Application;
import android.content.Context;

import com.randmcnally.bb.poc.interactor.DatabaseInteractor;


public class BBApplication extends Application {
    DatabaseInteractor interactor;

    public DatabaseInteractor getDatabaseInteractor(Context context) {
        interactor = new DatabaseInteractor(context);
        return interactor;
    }
}
