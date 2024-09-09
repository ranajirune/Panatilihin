package com.raineru.panatilihin.tempo.google.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
class NestedScrollDemo {

    @Preview
    @Composable
    fun NestedScrollDemoMain() {
        // here we use LazyColumn that has build-in nested scroll, but we want to act like a
        // parent for this LazyColumn and participate in its nested scroll.
        // Let's make a collapsing toolbar for LazyColumn
        val toolbarHeight = 48.dp
        val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
        // our offset to collapse toolbar
        var toolbarOffsetHeightPx by remember { mutableFloatStateOf(0f) }
        // now, let's create connection to the nested scroll system and listen to the scroll
        // happening inside child LazyColumn
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    // try to consume before LazyColumn to collapse toolbar if needed, hence pre-scroll
                    val delta = available.y
                    val newOffset = toolbarOffsetHeightPx + delta
                    toolbarOffsetHeightPx = newOffset.coerceIn(-toolbarHeightPx, 0f)
                    // here's the catch: let's pretend we consumed 0 in any case, since we want
                    // LazyColumn to scroll anyway for good UX
                    // We're basically watching scroll without taking it
                    return Offset.Zero
                }
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                // attach as a parent to the nested scroll system
                .nestedScroll(nestedScrollConnection)
        ) {
            // our list with build in nested scroll support that will notify us about its scroll
            LazyColumn(contentPadding = PaddingValues(top = toolbarHeight)) {
                items(100) { index ->
                    Text("I'm item $index", modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp))
                }
            }
            TopAppBar(
                modifier =
                Modifier
                    .height(toolbarHeight)
                    .offset {
                        IntOffset(x = 0, y = toolbarOffsetHeightPx.roundToInt())
                    },
                title = { Text("toolbar offset is $toolbarOffsetHeightPx") }
            )
            Text("toolbar offset is $toolbarOffsetHeightPx", modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}