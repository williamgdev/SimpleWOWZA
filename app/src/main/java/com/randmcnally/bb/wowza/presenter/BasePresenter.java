package com.randmcnally.bb.wowza.presenter;

import com.randmcnally.bb.wowza.view.BaseView;

public interface BasePresenter <V extends BaseView>{
    void attachView(V v);
    void detachView();
}
