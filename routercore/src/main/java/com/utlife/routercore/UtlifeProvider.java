package com.utlife.routercore;

import java.util.HashMap;

/**
 * Created by wanglei on 2016/11/29.
 */

public abstract class UtlifeProvider {
    //TODO this field is used for control the provider on and off
    private boolean mValid = true;
    private HashMap<String,UtlifeAction> mActions;
    public UtlifeProvider(){
        mActions = new HashMap<>();
    }
    protected void registerAction(String actionName,UtlifeAction action){
        mActions.put(actionName,action);
    }

    public UtlifeAction findAction(String actionName){
        return mActions.get(actionName);
    }

    public boolean isValid(){
        return mValid;
    }

    protected abstract String getName();
}
