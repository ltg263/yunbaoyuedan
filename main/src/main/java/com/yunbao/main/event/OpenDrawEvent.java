package com.yunbao.main.event;

import com.yunbao.main.bean.commit.DressingCommitBean;

public class OpenDrawEvent {
    private DressingCommitBean mDressingCommitBean;

    public OpenDrawEvent(DressingCommitBean dressingCommitBean) {
        mDressingCommitBean = dressingCommitBean;
    }

    public DressingCommitBean getDressingCommitBean() {
        return mDressingCommitBean;
    }
}
