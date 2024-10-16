@file:OptIn(ExperimentalFoundationApi::class)

package com.raineru.panatilihin.tempo

import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class DragAnchors {
    Start,
    End,
}

@OptIn(ExperimentalFoundationApi::class)
class AnchoredDraggableDemo {

    @Preview
    @Composable
    fun AnchoredDraggableDemoMain() {
        val density = LocalDensity.current
        val decayAnimationSpec = rememberSplineBasedDecay<Float>()

        // 1
        val state = remember {
            AnchoredDraggableState(
                // 2
                initialValue = DragAnchors.Start,
                // 3
                positionalThreshold = { distance: Float -> distance * 0.5f },
                // 4
                velocityThreshold = { with(density) { 100.dp.toPx() } },
                // 5
                snapAnimationSpec = spring(),
                decayAnimationSpec = decayAnimationSpec
            ).apply {
                // 6
                updateAnchors(
                    // 7
                    DraggableAnchors {
                        DragAnchors.Start at 0f
                        DragAnchors.End at 400f
                    }
                )
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Red)
        ) {
            Box(
                modifier = Modifier
                    // 1
                    .offset {
                        IntOffset(
                            // 2
                            x = state
                                .requireOffset()
                                .roundToInt(),
                            y = 0,
                        )
                    }
                    // 3
                    .anchoredDraggable(state, Orientation.Horizontal)
                    .clickable { }
                    .background(Color.Gray)
                    .size(50.dp),
            )

            Text("offset: ${state.requireOffset()}", modifier = Modifier.align(Alignment.Center))
        }
    }
}

private enum class BoxAnchors {
    START, END
}

class AnchoredDraggableSelfDemo {

    @Preview
    @Composable
    fun AnchoredDraggableSelfDemoUsingConstructor() {
        val density = LocalDensity.current
        val decayAnimationSpec = rememberSplineBasedDecay<Float>()

        val anchordDraggableState = remember {
            AnchoredDraggableState(
                initialValue = BoxAnchors.START,
                anchors = DraggableAnchors {
                    BoxAnchors.START at 0f
                    BoxAnchors.END at 400f
                },
                positionalThreshold = { distance: Float -> distance * 0.5f },
                velocityThreshold = { with(density) { 100.dp.toPx() } },
                snapAnimationSpec = spring(),
                decayAnimationSpec = decayAnimationSpec
            )
        }

        val coroutineScope = rememberCoroutineScope()

        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .offset {
                        IntOffset(
                            x = anchordDraggableState
                                .requireOffset()
                                .roundToInt(),
                            y = 0
                        )
                    }
                    .size(50.dp)
                    .background(Color.Gray)
                    .anchoredDraggable(
                        state = anchordDraggableState,
                        orientation = Orientation.Horizontal
                    )
            )
            Text(
                "offset: ${anchordDraggableState.requireOffset()}",
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    coroutineScope.launch {
                        if (anchordDraggableState.currentValue == BoxAnchors.START) {
                            anchordDraggableState.animateTo(BoxAnchors.END)
                        } else {
                            anchordDraggableState.animateTo(BoxAnchors.START)
                        }
                    }
                }
            ) {
                Text("Snap to start")
            }
        }
    }

    @Preview
    @Composable
    fun AnchoredDraggableSelfDemoUsingUpdateAnchors() {
        val density = LocalDensity.current
        val decayAnimationSpec = rememberSplineBasedDecay<Float>()

        val anchoredDraggableState = remember {
            AnchoredDraggableState(
                initialValue = BoxAnchors.START,
                positionalThreshold = { distance: Float -> distance * 0.5f },
                velocityThreshold = { with(density) { 100.dp.toPx() } },
                snapAnimationSpec = spring(),
                decayAnimationSpec = decayAnimationSpec
            )
        }

        val coroutineScope = rememberCoroutineScope()

        Box(
            Modifier
                .onSizeChanged {
                    anchoredDraggableState.updateAnchors(
                        DraggableAnchors {
                            BoxAnchors.START at 0f
                            BoxAnchors.END at it.width.toFloat()
                        }
                    )
                }
                .fillMaxSize()
        ) {
            Box(
                Modifier
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        orientation = Orientation.Horizontal
                    )
                    .offset {
                        val x = (anchoredDraggableState
                            .requireOffset() - 50.dp.toPx())
                            .roundToInt()
                            .coerceAtLeast(0)
                        IntOffset(
                            x = x,
                            y = 0
                        )
                    }
                    .size(50.dp)
                    .background(Color.Gray)
            )
            Text(
                "offset: ${anchoredDraggableState.offset}",
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    coroutineScope.launch {
                        if (anchoredDraggableState.currentValue == BoxAnchors.START) {
                            anchoredDraggableState.animateTo(BoxAnchors.END)
                        } else {
                            anchoredDraggableState.animateTo(BoxAnchors.START)
                        }
                    }
                }
            ) {
                Text("Snap to start")
            }
        }
    }
}