package com.raineru.panatilihin.tempo

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.raineru.panatilihin.R
import com.raineru.panatilihin.ui.theme.PanatilihinTheme

// https://medium.com/@mortitech/better-previews-in-compose-with-custom-annotations-dc49b94ff579

@Composable
fun LandscapeListItem(
    imageResource: Painter,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Column for Image and Text
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            // Image
            Image(
                painter = imageResource,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(48.dp)
                    .graphicsLayer {
                        // Apply blur if checkbox is checked
                        alpha = if (isChecked) 0.3f else 1f
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text
            Text(
                text = text,
                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
            )
        }

        // Checkbox
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}


@Composable
fun PortraitListItem(
    imageResource: Painter,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image
        Image(
            painter = imageResource,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    // Apply blur if checkbox is checked
                    alpha = if (isChecked) 0.3f else 1f
                }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Text
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Checkbox
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun AdaptiveListItem(
    imageResource: Painter,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        LandscapeListItem(imageResource, text, isChecked, onCheckedChange)
    } else {
        PortraitListItem(imageResource, text, isChecked, onCheckedChange)
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode", showBackground = true, uiMode = UI_MODE_NIGHT_NO)
annotation class ThemePreviews

@ThemePreviews
@Composable
fun PreviewsListItem() {
    PanatilihinTheme {
        Surface {
            PortraitListItem(
                imageResource = painterResource(id = R.drawable.ic_launcher_foreground),
                text = "Write the `Preview` Medium article",
                isChecked = false,
                onCheckedChange = { }
            )
        }
    }
}

@Preview(name = "Landscape Mode", showBackground = true, device = Devices.AUTOMOTIVE_1024p, widthDp = 640)
@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PIXEL_4)
annotation class OrientationPreviews

@OrientationPreviews
@Composable
fun OrientationPreviewsListItem() {
    MaterialTheme {
        Surface {
            AdaptiveListItem(
                imageResource = painterResource(id = R.drawable.ic_launcher_foreground),
                text = "Write the `Preview` Medium article",
                isChecked = false,
                onCheckedChange = { }
            )
        }
    }
}

@Preview(name = "Default Font Size", fontScale = 1f)
@Preview(name = "Large Font Size", fontScale = 1.5f)
annotation class FontScalePreviews

@FontScalePreviews
@Composable
fun FontScalePreviewsListItem() {
    Text("Wakannai yo!")
}

@Composable
fun ListItemPreview() {
    var checkedState by remember { mutableStateOf(false) }

    PortraitListItem(
        imageResource = painterResource(id = R.drawable.ic_launcher_foreground),
        text = "Write the `Preview` Medium article",
        isChecked = checkedState,
        onCheckedChange = { newState ->
            checkedState = newState
        }
    )
}

@Preview(showBackground = true, name = "Interactivity Preview")
@Composable
fun PreviewListItem() {
    ListItemPreview()
}

class TextPreviewProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "Short Text",
        "A bit longer text.",
        "This one is really, really long. Like, really long!"
    )
}

@Composable
@Preview
fun DifferentTextPreviewsListItem(
    @PreviewParameter(TextPreviewProvider::class, limit = 2) text: String
) {
    PanatilihinTheme {
        Surface {
            PortraitListItem(
                imageResource = painterResource(id = R.drawable.ic_launcher_foreground),
                text = text,
                isChecked = false,
                onCheckedChange = { }
            )
        }
    }
}