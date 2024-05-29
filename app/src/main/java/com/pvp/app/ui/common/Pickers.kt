package com.pvp.app.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.pvp.app.common.CollectionUtil.indexOfOrNull
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PickerState<T>(initialValue: T) {

    var value by mutableStateOf<T>(initialValue)

    companion object {

        @Composable
        fun <T> rememberPickerState(initialValue: T) = remember(initialValue) {
            PickerState(initialValue)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> Picker(
    dividerColor: Color = LocalContentColor.current,
    items: List<T>,
    label: @Composable (T) -> String = { it.toString() },
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    state: PickerState<T>,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    visibleItemsCount: Int = 3,
    onChange: (T) -> Unit = {}
) {
    val itemsInMiddle = visibleItemsCount / 2
    val scrollCount = Integer.MAX_VALUE
    val scrollMiddle = scrollCount / 2
    val indexStart = scrollMiddle - scrollMiddle % items.size - itemsInMiddle + startIndex
    val stateList = rememberLazyListState(initialFirstVisibleItemIndex = indexStart)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = stateList)
    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val itemHeightDp = itemHeightPixels.intValue.pixelsToDp()

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
            .collect { item ->
                state.value = item
                onChange(item)
            }
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
fun <T1, T2> PickerPair(
    divider: @Composable RowScope.() -> Unit = {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = ":"
        )
    },
    itemsFirst: List<T1>,
    itemsSecond: List<T2>,
    labelFirst: @Composable (T1) -> String = { it.toString() },
    labelSecond: @Composable (T2) -> String = { it.toString() },
    modifier: Modifier = Modifier,
    onChange: (first: T1, second: T2) -> Unit,
    stateFirst: PickerState<T1>,
    stateSecond: PickerState<T2>,
    visibleItemsCount: Int = 3
) {
    Row(modifier = modifier) {
        Picker(
            items = itemsFirst,
            label = labelFirst,
            state = stateFirst,
            visibleItemsCount = visibleItemsCount,
            modifier = Modifier.weight(1f),
            startIndex = itemsFirst.indexOfOrNull(stateFirst.value) ?: 0,
            onChange = {
                onChange(
                    it,
                    stateSecond.value
                )
            }
        )

        divider()

        Picker(
            items = itemsSecond,
            label = labelSecond,
            state = stateSecond,
            visibleItemsCount = visibleItemsCount,
            modifier = Modifier.weight(1f),
            startIndex = itemsSecond.indexOfOrNull(stateSecond.value) ?: 0,
            onChange = {
                onChange(
                    stateFirst.value,
                    it
                )
            }
        )
    }
}

@Composable
fun PickerTime(
    modifier: Modifier = Modifier,
    selectedHour: PickerState<Int>,
    selectedMinute: PickerState<Int>,
    onChange: (hour: Int, minute: Int) -> Unit = { _, _ -> }
) {
    PickerPair(
        itemsFirst = (0..23).toList(),
        itemsSecond = (0..59).toList(),
        labelFirst = { "$it" },
        labelSecond = {
            it
                .toString()
                .padStart(
                    2,
                    '0'
                )
        },
        modifier = modifier,
        onChange = onChange,
        stateFirst = selectedHour,
        stateSecond = selectedMinute,
        visibleItemsCount = 3
    )
}