package com.yunbao.common.bean.commit;

public class ObservableString extends BaseObservableField<String>{

    public ObservableString(CommitEntity commitEntity) {
        super(commitEntity);
    }

    @Override
    public String changeData(String s) {
        return s;
    }
}
