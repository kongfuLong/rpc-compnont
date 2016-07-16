package com.core.remoteModules;

import com.enums.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2016/7/16.
 */
@Component
public class RemoteFactory {

    @Autowired
    private RmiModule rmiModule;

    @Autowired
    private HessainModule hessainModule;


    public RemoteInterfaceModule getRemoteModule(RemoteType type){
        switch (type){
            case RMI:
                return rmiModule;
            case HESSAIN:
                return hessainModule;
            default:
                return rmiModule;
        }
    }

}
