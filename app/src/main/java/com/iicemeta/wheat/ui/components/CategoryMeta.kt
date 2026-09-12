package com.iicemeta.wheat.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.iicemeta.wheat.ui.theme.CategoryFood
import com.iicemeta.wheat.ui.theme.CategoryFoodBg
import com.iicemeta.wheat.ui.theme.CategoryFun
import com.iicemeta.wheat.ui.theme.CategoryFunBg
import com.iicemeta.wheat.ui.theme.CategoryOther
import com.iicemeta.wheat.ui.theme.CategoryOtherBg
import com.iicemeta.wheat.ui.theme.CategoryShopping
import com.iicemeta.wheat.ui.theme.CategoryShoppingBg
import com.iicemeta.wheat.ui.theme.CategoryStudy
import com.iicemeta.wheat.ui.theme.CategoryStudyBg
import com.iicemeta.wheat.ui.theme.CategoryTransport
import com.iicemeta.wheat.ui.theme.CategoryTransportBg

data class CategoryStyle(
    val label: String,
    val icon: ImageVector,
    val fg: Color,
    val bg: Color
)

/** 兼容新旧分类名，统一映射到 html 原型中的三类 + 扩展类。 */
fun categoryStyle(category: String): CategoryStyle = when (category) {
    "餐饮美食", "餐饮" -> CategoryStyle("餐饮美食", Icons.Default.Restaurant, CategoryFood, CategoryFoodBg)
    "学习进修", "学习" -> CategoryStyle("学习进修", Icons.AutoMirrored.Filled.MenuBook, CategoryStudy, CategoryStudyBg)
    "交通出行", "交通" -> CategoryStyle("交通出行", Icons.Default.DirectionsBus, CategoryTransport, CategoryTransportBg)
    "购物消费", "购物" -> CategoryStyle("购物消费", Icons.Default.ShoppingBag, CategoryShopping, CategoryShoppingBg)
    "休闲娱乐", "娱乐" -> CategoryStyle("休闲娱乐", Icons.Default.Movie, CategoryFun, CategoryFunBg)
    else -> CategoryStyle("其他", Icons.Default.MoreHoriz, CategoryOther, CategoryOtherBg)
}

val recordCategories = listOf("餐饮美食", "学习进修", "交通出行", "购物消费", "休闲娱乐", "其他")
