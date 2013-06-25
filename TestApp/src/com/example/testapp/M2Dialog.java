package com.example.testapp;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.openerp.ReadExtraAsyncTask;


public class M2Dialog extends DialogFragment {


    private ReadExtraAsyncTask mReadExtraAsyncTask;

    M2Dialog(ReadExtraAsyncTask reat){
        this.mReadExtraAsyncTask = reat;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return null;
    }
}
