package com.randmcnally.bb.poc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randmcnally.bb.poc.view.BaseView;

public abstract class BaseFragment extends Fragment implements BaseView{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(getLayoutID(), container, false);

        initializeUIComponents(view);
        initializePresenter();

        return view;
    }

    @LayoutRes
    protected abstract int getLayoutID();

    protected abstract void initializeUIComponents(View view);

    @Override
    public Context getContext() {
        return getActivity();
    }
}
