package com.iicemeta.wheat.viewmodel

enum class WheatStage {
    SEED,       // 种子 - 尚未打卡
    SPROUT,     // 幼苗 - 连击 1-2 天
    GROWING,    // 成长 - 连击 3-6 天
    MATURE,     // 抽穗 - 连击 7-13 天
    HARVEST     // 丰收 - 连击 14 天+
}
