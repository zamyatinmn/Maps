package com.zamyatinmn.maps

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zamyatinmn.maps.ui.theme.MapsTheme

@Composable
fun OpenApp(viewModel: MapViewModel) {
    when (viewModel.activeScreen) {
        Screen.Map -> Map(viewModel)
        Screen.Markers -> MarkerList(viewModel)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MarkerList(viewModel: MapViewModel) {
    LazyColumn(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        stickyHeader {
            Button(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(0.5f),
                onClick = { viewModel.changeScreen(Screen.Map) }) {
                Text(text = stringResource(R.string.to_map_btn))
            }
        }
        itemsIndexed(items = viewModel.markers, { _, m -> m.coordinates }) { i, marker ->

            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                viewModel.markers.remove(marker)
            }

            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier
                    .padding(vertical = Dp(1f)),
                directions = setOf(
                    DismissDirection.EndToStart
                ),
                dismissThresholds = { direction ->
                    FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.1f else 0.05f)
                },
                background = {
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.Default -> Color.White
                            else -> Color.Red
                        }
                    )
                    val alignment = Alignment.CenterEnd
                    val icon = Icons.Default.Delete

                    val scale by animateFloatAsState(
                        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = Dp(20f)),
                        contentAlignment = alignment
                    ) {
                        Icon(
                            icon,
                            contentDescription = stringResource(R.string.delete_icon_description),
                            modifier = Modifier.scale(scale)
                        )
                    }
                },
                dismissContent = {

                    Card(
                        elevation = animateDpAsState(
                            if (dismissState.dismissDirection != null) 6.dp else 2.dp
                        ).value,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth()
                            .align(alignment = Alignment.CenterVertically),
                    ) {
                        Column(Modifier.padding(10.dp)) {
                            OutlinedTextField(
                                value = marker.title,
                                onValueChange = {
                                    viewModel.changeMarkerState(
                                        index = i,
                                        title = it
                                    )
                                },
                                placeholder = { Text(text = stringResource(R.string.title_hint)) },
                                singleLine = true,
                            )
                            Text(text = "${marker.coordinates.latitude} ${marker.coordinates.longitude}")
                            OutlinedTextField(
                                value = marker.snippet,
                                onValueChange = {
                                    viewModel.changeMarkerState(
                                        index = i,
                                        snippet = it
                                    )
                                },
                                placeholder = { Text(text = stringResource(R.string.description_hint)) })
                        }
                    }
                }
            )
        }


    }
}

@Preview
@Composable
fun MarkersListPreview() {
    MapsTheme {
        MarkerList(MapViewModel())
    }
}