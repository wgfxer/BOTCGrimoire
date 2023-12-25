package com.example.botcgrimoire.presentation.grimoire

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * @author Valeriy Minnulin
 */
@Composable
fun GrimoireRolesLayout(
    roles: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    sizeType: CircleSizeType = CircleSizeType.BigAsPossible,
    positionType: CirclePositionType = CirclePositionType.Symmetrically
) {
    Layout(modifier = modifier, content = roles) { measurables, constraints ->
        val radius = minOf(constraints.maxHeight, constraints.maxWidth) / 2
        val centerY = constraints.maxHeight / 2
        val centerX = constraints.maxWidth / 2
        val n = measurables.size
        val angle = if (positionType == CirclePositionType.Symmetrically) { 360f / n } else {
            val circleInnerRadius = radius / 3f
            val angleCos = circleInnerRadius / (2 * radius)
            (180 - 2 * Math.toDegrees(acos(angleCos.toDouble())).toFloat()) * 2
        }
        // 1: 0
        // 2: - 0.5 angle
        // 3: - angle
        // 4: - 1.5 angle
        // 5: - 2angle
        var currentAngle = if (positionType == CirclePositionType.Symmetrically) 90f else {
            270f - angle * (n / 2f - 0.5f)
        }
        var innerRadius = 0f
        val circles = mutableListOf<CircleData>()
        val padding = 8.dp.toPx()
        for (i in 1..n) {
            val x = centerX + (radius * cos(Math.toRadians(currentAngle.toDouble())))
            val y = centerY + (radius * sin(Math.toRadians(currentAngle.toDouble())))
            val offset = Offset(x.toFloat(), y.toFloat())

            innerRadius = if (i == n && sizeType == CircleSizeType.BigAsPossible) {
                val lastPoint = circles.last()
                val distance = sqrt( (x - lastPoint.offset.x.toDouble()).pow(2) + (y - lastPoint.offset.y.toDouble()).pow(2))
                distance.toFloat() / 2f - padding
            } else {
                radius / 3f
            }
            currentAngle += angle
            android.util.Log.i("MYTAG_OFFSET", "i=$i for count $n $offset")
            circles.add(CircleData(offset, 1))
        }
        val sizeOfCircle = (innerRadius * 2).toInt()
        val placeables = measurables.map {
            it.measure(Constraints.fixed(sizeOfCircle, sizeOfCircle))
        }
        this.layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.zip(circles) { placeable, circle ->
                placeable.placeRelative((circle.offset.x - placeable.width / 2).roundToInt(), (circle.offset.y - placeable.height / 2).roundToInt())
            }
        }
    }
}

@Composable
fun BluePrint(n: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        this.drawCircle(Color.Blue, style = Stroke())
        val radius = this.size.minDimension / 2
        val angle = 360f / n
        val center = this.center
        val centerX = center.x
        val centerY = center.y
        var currentAngle = 90f
        for (i in 1..n) {
            val x = centerX + (radius * cos(Math.toRadians(currentAngle.toDouble()))).toInt()
            val y = centerY + (radius * sin(Math.toRadians(currentAngle.toDouble()))).toInt()
            this.drawCircle(Color.Red, radius = 30f, center = Offset(x, y))
            currentAngle += angle
        }
    }
}

enum class CircleSizeType {
    BigAsPossible,
    ParentQuarter
}

enum class CirclePositionType {
    Symmetrically,
    OnTop
}

private data class CircleData(
    val offset: Offset, val radius: Int
)