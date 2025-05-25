package com.example.bubBubbleService.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bubBubbleService.R


@Composable
fun TestBubbleCompose() {
    val circleModifier = Modifier.shadow(
        elevation = 4.dp,
        spotColor = Color.Black.copy(0.4f),
        ambientColor = Color(0x0A001A41),
        shape = RoundedCornerShape(40.dp)
    )

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Surface(modifier = circleModifier.clickable {}) {
        Box(
            modifier = Modifier
                .padding(2.dp)
                .clip(CircleShape)
                .background(Color(0xFF275CB0))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_flutter),
                contentDescription = "Ai chat",
                modifier = Modifier
                    .size(((screenWidth * 3) / 50))
                    .padding(6.dp),
                tint = Color.White
            )
        }
    }
}