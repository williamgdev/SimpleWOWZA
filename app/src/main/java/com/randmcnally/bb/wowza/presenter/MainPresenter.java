package com.randmcnally.bb.wowza.presenter;

import com.randmcnally.bb.wowza.BasePresenter;
import com.randmcnally.bb.wowza.view.MainView;

public interface MainPresenter extends BasePresenter<MainView> {
    void loadData();
}
