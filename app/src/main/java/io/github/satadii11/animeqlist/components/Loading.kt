package io.github.satadii11.animeqlist.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Loading(modifier: Modifier) {
    Box(modifier) {
        CircularProgressIndicator(
            Modifier
                .align(Alignment.Center)
                .padding(top = 8.dp, bottom = 8.dp)
        )
    }
}

@Composable
@Preview
private fun LoadingMatchParent() {
    Loading(Modifier.fillMaxSize())
}

@Composable
@Preview
private fun LoadingWrapContent() {
    Loading(Modifier.fillMaxWidth())
}