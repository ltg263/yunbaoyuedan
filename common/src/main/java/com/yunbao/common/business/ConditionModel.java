package com.yunbao.common.business;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.ConditionLevel;

public class ConditionModel {
    public static final String DEFAULT_ID="0";

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





        public static ConditionLevel[] getPrice(String defaultContent) {
        return new ConditionLevel[]{
                new ConditionLevel(DEFAULT_ID,defaultContent),
                new ConditionLevel("10","10"+ CommonAppConfig.getInstance().getCoinName()),
                new ConditionLevel("15","15"+ CommonAppConfig.getInstance().getCoinName()),
                new ConditionLevel("20","20"+ CommonAppConfig.getInstance().getCoinName()),
                new ConditionLevel("25","25"+ CommonAppConfig.getInstance().getCoinName()),
                new ConditionLevel("30","30"+ CommonAppConfig.getInstance().getCoinName())};

    }




    public static ConditionLevel defaultLevel(String content){
      return new ConditionLevel(DEFAULT_ID,content);
    }


}
