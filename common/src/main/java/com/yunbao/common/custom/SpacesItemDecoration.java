package com.yunbao.common.custom;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Sky.L on 2022-05-10
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpacesItemDecoration(int space) {

        this.space = space;

    }

    @Override

    public void getItemOffsets(Rect outRect, View view,

                               RecyclerView parent, RecyclerView.State state) {

        outRect.right = space;



    }

}