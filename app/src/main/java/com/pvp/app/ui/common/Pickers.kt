package com.pvp.app.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PickerState<T>(initialValue: T) {

    var value by mutableStateOf<T>(initialValue)

    companion object {

        @Composable
        fun <T> rememberPickerState(initialValue: T) = remember { PickerState(initialValue) }
    }
}

@Composable
fun <T : Number> NumberPicker(
    dividersColor: Color = MaterialTheme.colorScheme.primary,
    indexStart: Int = 0,
    items: List<T>,
    label: (T) -> String = {
        it.toString()
    },
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    state: PickerState<T>
) {
    Picker(
        dividerColor = dividersColor,
        items = items,
        label = label,
        modifier = modifier,
        startIndex = indexStart,
        textModifier = Modifier,
        textStyle = textStyle,
        state = state
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> Picker(
    dividerColor: Color = LocalContentColor.current,
    items: List<T>,
    label: (T) -> String = { it.toString() },
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    state: PickerState<T>,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    visibleItemsCount: Int = 3,
) {
    val itemsInMiddle = visibleItemsCount / 2
    val scrollCount = Integer.MAX_VALUE
    val scrollMiddle = scrollCount / 2
    val indexStart = scrollMiddle - scrollMiddle % items.size - itemsInMiddle + startIndex
    val stateList = rememberLazyListState(initialFirstVisibleItemIndex = indexStart)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = stateList)
    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels.intValue)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.5f to Color.Black,
            1f to Color.Transparent
        )
    }

    fun getItem(index: Int) = items[index % items.size]

    LaunchedEffect(stateList) {
        snapshotFlow { stateList.firstVisibleItemIndex }
            .map { index -> getItem(index + itemsInMiddle) }
            .distinctUntilChanged()
            .collect { item -> state.value = item }
    }

    Box(modifier = modifier) {
        LazyColumn(
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient),
            state = stateList
        ) {
            items(scrollCount) { index ->
                Text(
                    maxLines = 1,
                    modifier = Modifier
                        .onSizeChanged { size -> itemHeightPixels.intValue = size.height }
                        .then(textModifier),
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    text = label(getItem(index))
                )
            }
        }

        HorizontalDivider(
            color = dividerColor,
            modifier = Modifier.offset(y = itemHeightDp * itemsInMiddle)
        )

        HorizontalDivider(
            color = dividerColor,
            modifier = Modifier.offset(y = itemHeightDp * (itemsInMiddle + 1))
        )
    }
}

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }