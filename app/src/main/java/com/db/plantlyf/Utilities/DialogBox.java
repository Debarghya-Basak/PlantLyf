package com.db.plantlyf.Utilities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;

import com.db.plantlyf.R;

import java.util.Objects;

public class DialogBox {

    private Context context;
    @LayoutRes private int layoutResID;
    @DrawableRes private int drawableResID = R.drawable.global_loading_dialog_box_transparentbg;
    private Dialog dialog;

    public DialogBox(Context context, @LayoutRes int layoutResID){

        this.context = context;
        this.layoutResID = layoutResID;

        dialog = new Dialog(context);
        dialog.setContentView(layoutResID);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(context.getDrawable(drawableResID));
        dialog.setCancelable(false);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void showDialog(){
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }

}
