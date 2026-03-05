package com.github.ryand6.sudokuweb.enums;

import com.github.ryand6.sudokuweb.util.StringUtils;

public enum GameMode {

    CLASSIC, DOMINATION ,TIMEATTACK;

    public String getProperCase() {
        return StringUtils.getProperCase(name());
    }

}
