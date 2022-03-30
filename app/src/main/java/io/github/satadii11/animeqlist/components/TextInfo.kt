package io.github.satadii11.animeqlist.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextInfo(modifier: Modifier, message: String) {
    Box(modifier) {
        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            fontSize = 18.sp,
            color = MaterialTheme.colors.error
        )
    }
}

@Composable
@Preview
private fun TextInfoMatchParent() {
    TextInfo(Modifier.fillMaxSize(), "Anime \"One Piece Naruto Bleach\" gak ketemu nih.")
}