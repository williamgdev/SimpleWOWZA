package com.randmcnally.bb.wowza;

public interface BasePresenter <V extends BaseView>{
    void attachView(V v);
    void detachView();
}
