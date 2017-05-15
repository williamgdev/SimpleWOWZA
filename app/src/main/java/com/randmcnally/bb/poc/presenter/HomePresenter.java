package com.randmcnally.bb.poc.presenter;

import com.randmcnally.bb.poc.interactor.DatabaseInteractor;
import com.randmcnally.bb.poc.util.OpenFireServer;
import com.randmcnally.bb.poc.view.HomeView;

public interface HomePresenter extends BasePresenter<HomeView> {

    void setOpenFireServer(OpenFireServer openFireServer);

    void setDatabaseInteractor(DatabaseInteractor databaseInteractor);
}
