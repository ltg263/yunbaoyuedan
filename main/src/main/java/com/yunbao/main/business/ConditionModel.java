package com.yunbao.main.business;

import com.yunbao.common.bean.ConditionLevel;

public class ConditionModel {
    public static final String DEFAULT_ID="0";
    private static ConditionLevel[] SEX_LEVEL;
    private static ConditionLevel[] AGE_LEVEL;

    public static ConditionLevel[] getSexLevel(String defaultContent) {
        return new ConditionLevel[]{
                    new ConditionLevel(DEFAULT_ID,defaultContent),
                    new ConditionLevel("1","男生"),
                    new ConditionLevel("2","女生"),
        };
    }

    public static ConditionLevel[] getAgeLevel(String defaultContent) {

        return new ConditionLevel[]{
                    new ConditionLevel(DEFAULT_ID,defaultContent),
                    new ConditionLevel("1","70后"),
                    new ConditionLevel("2","80后"),
                    new ConditionLevel("3","90后"),
                    new ConditionLevel("4","00后"),
                    new ConditionLevel("5","10后")};

    }

    public static ConditionLevel defaultLevel(String content){
      return new ConditionLevel(DEFAULT_ID,content);
    }


}
