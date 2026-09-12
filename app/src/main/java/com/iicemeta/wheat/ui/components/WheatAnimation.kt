package com.iicemeta.wheat.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.iicemeta.wheat.ui.theme.AutumnGreen
import com.iicemeta.wheat.ui.theme.AutumnOrange
import com.iicemeta.wheat.ui.theme.AutumnYellow
import com.iicemeta.wheat.ui.theme.Wheat400
import com.iicemeta.wheat.ui.theme.Wheat500
import com.iicemeta.wheat.ui.theme.Wheat800
import com.iicemeta.wheat.viewmodel.WheatStage

/**
 * 习惯驱动的小麦动效：高度与麦粒数只随坚持天数（stage）变化，与金额无关。
 */
@Composable
fun WheatAnimation(
    stage: WheatStage,
    modifier: Modifier = Modifier
) {
    val targetHeight = when (stage) {
        WheatStage.SEED -> 0.12f
        WheatStage.SPROUT -> 0.32f
        WheatStage.GROWING -> 0.55f
        WheatStage.MATURE -> 0.8f
        WheatStage.HARVEST -> 1.0f
    }

    val animatedHeight by animateFloatAsState(
        targetValue = targetHeight,
        animationSpec = tween(durationMillis = 1200, easing = LinearEasing),
        label = "wheat_height"
    )

    val grainPairs = when (stage) {
        WheatStage.SEED -> 0
        WheatStage.SPROUT -> 0
        WheatStage.GROWING -> 2
        WheatStage.MATURE -> 3
        WheatStage.HARVEST -> 4
    }

    val grainColor = when (stage) {
        WheatStage.GROWING -> Wheat400
        WheatStage.MATURE -> Wheat500
        WheatStage.HARVEST -> AutumnYellow
        else -> Wheat400
    }

    Canvas(modifier = modifier.size(200.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        val groundY = h * 0.88f

        // 土壤
        drawOval(
            color = Wheat800.copy(alpha = 0.25f),
            topLeft = Offset(cx - 95f, groundY - 8f),
            size = Size(190f, 22f)
        )
        drawOval(
            color = Wheat800.copy(alpha = 0.45f),
            topLeft = Offset(cx - 65f, groundY - 5f),
            size = Size(130f, 14f)
        )

        // 麦秆（微弯曲线）
        val stemTop = groundY - h * 0.62f * animatedHeight
        val stalkPath = Path().apply {
            moveTo(cx, groundY)
            cubicTo(cx - 14f, groundY - (groundY - stemTop) * 0.4f, cx + 10f, stemTop + 60f, cx, stemTop)
        }
        drawPath(path = stalkPath, color = Wheat800, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 7f))

        // 叶子（种子期不画）
        if (stage != WheatStage.SEED) {
            val leafY = groundY - (groundY - stemTop) * 0.35f
            val leafLen = 62f * animatedHeight + 18f
            val leftLeaf = Path().apply {
                moveTo(cx, leafY)
                cubicTo(cx - leafLen * 0.5f, leafY - 8f, cx - leafLen * 0.8f, leafY - 26f, cx - leafLen, leafY - 18f)
                cubicTo(cx - leafLen * 0.7f, leafY + 2f, cx - leafLen * 0.3f, leafY + 6f, cx, leafY + 4f)
                close()
            }
            drawPath(leftLeaf, AutumnGreen)
            val rightLeaf = Path().apply {
                val y = leafY + 26f
                moveTo(cx, y)
                cubicTo(cx + leafLen * 0.5f, y - 6f, cx + leafLen * 0.8f, y - 24f, cx + leafLen, y - 16f)
                cubicTo(cx + leafLen * 0.7f, y + 4f, cx + leafLen * 0.3f, y + 8f, cx, y + 4f)
                close()
            }
            drawPath(rightLeaf, AutumnGreen)
        }

        // 麦粒（成对椭圆，倾斜 ±22°）
        repeat(grainPairs) { i ->
            val y = stemTop + 18f + i * 26f
            val gw = 30f * animatedHeight + 12f
            val gh = 44f * animatedHeight + 16f
            withTransform({ rotate(-22f, pivot = Offset(cx - 12f, y)) }) {
                drawOval(
                    color = if (i == grainPairs - 1) grainColor else Wheat500,
                    topLeft = Offset(cx - 12f - gw / 2, y - gh / 2),
                    size = Size(gw, gh)
                )
            }
            withTransform({ rotate(22f, pivot = Offset(cx + 12f, y)) }) {
                drawOval(
                    color = grainColor,
                    topLeft = Offset(cx + 12f - gw / 2, y - gh / 2),
                    size = Size(gw, gh)
                )
            }
        }

        // 顶端芒尖（抽穗期以后）
        if (stage == WheatStage.MATURE || stage == WheatStage.HARVEST) {
            drawLine(
                color = AutumnOrange,
                start = Offset(cx, stemTop - 6f),
                end = Offset(cx - 4f, stemTop - 26f),
                strokeWidth = 4f
            )
        }

        // 种子期：土里一颗种子
        if (stage == WheatStage.SEED) {
            drawCircle(color = Wheat800, radius = 9f, center = Offset(cx, groundY - 4f))
        }
    }
}
