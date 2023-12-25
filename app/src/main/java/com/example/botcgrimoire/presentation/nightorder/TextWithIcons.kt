package com.example.botcgrimoire.presentation.nightorder

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import com.example.botcgrimoire.R

/**
 * @author Valeriy Minnulin
 */
@Composable
fun TextWithIcons(text: String) {
    val keys = inlineContentMap.keys

    val annotatedString = buildAnnotatedString {
        var currentIndex = 0
        var nextIcon = text.findAnyOf(keys, startIndex = currentIndex)
        while (nextIcon != null) {
            append(text.substring(currentIndex, nextIcon.first))
            appendInlineContent(id = nextIcon.second)
            currentIndex = nextIcon.first + nextIcon.second.length
            nextIcon = text.findAnyOf(keys, startIndex = currentIndex)
        }
        append(text.substring(currentIndex, text.length))
    }
    Text(annotatedString, inlineContent = inlineContentMap)
}

val inlineContentMap = mapOf(
    "[ICON: eye_closed]" to InlineTextContent(
        Placeholder(20.sp, 20.sp, PlaceholderVerticalAlign.TextCenter)
    ) {
        val color = MaterialTheme.colorScheme.onBackground
        Icon(painterResource(id = R.drawable.ic_eye_closed_24), contentDescription = null, tint = color)
    },
    "[ICON: eye_open]" to InlineTextContent(
        Placeholder(20.sp, 20.sp, PlaceholderVerticalAlign.TextCenter)
    ) {
        val color = MaterialTheme.colorScheme.onBackground
        Icon(painterResource(id = R.drawable.ic_eye_open_24), contentDescription = null, tint = color)
    },
    "[ICON: circle]" to InlineTextContent(
        Placeholder(20.sp, 20.sp, PlaceholderVerticalAlign.TextCenter)
    ) {
        val color = MaterialTheme.colorScheme.onBackground
        Icon(painterResource(id = R.drawable.ic_circle_24), contentDescription = null, tint = color)
    },
)