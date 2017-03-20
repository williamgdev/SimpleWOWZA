package com.randmcnally.bb.poc.presenter;

import com.randmcnally.bb.poc.view.BaseView;

public interface BasePresenter <V extends BaseView>{
    void attachView(V v);
    void detachView();
}
