package com.github.vazzzx.prison.Cache;


import com.github.vazzzx.prison.Components.PlayerAccount;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter

public class PlayerAccountCache {

    private final Map<String, PlayerAccount> blockedPlayerCache;

    public PlayerAccountCache() {
        this.blockedPlayerCache = new HashMap<>();
    }

}
