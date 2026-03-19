package com.hemad.stishield.ui.utilities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.hemad.stishield.R
import com.hemad.stishield.model.common.UserDataRepository

fun beautifyMarkdownString(text: String): AnnotatedString {
    return buildAnnotatedString {
        // Regex pattern to match bold, italic, bullet points, numbered lists, and numbered paragraphs
        val regex = "(\\*\\*([^*]+)\\*\\*)|(\\*([^*]+)\\*)|(-\\s)|((\\d+)\\s\\.)|^(\\d+\\.\\s)".toRegex(RegexOption.MULTILINE)
        var lastIndex = 0

        regex.findAll(text).forEach { matchResult ->
            val boldText = matchResult.groups[1]?.value
            val italicText = matchResult.groups[3]?.value
            val bulletPoint = matchResult.groups[4]?.value
            val numberedText = matchResult.groups[6]?.value
            val numberedParagraph = matchResult.groups[8]?.value

            append(text.substring(lastIndex, matchResult.range.first))

            when {
                boldText != null -> {
                    val boldContent = boldText.removeSurrounding("**")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(boldContent)
                    }
                }
                italicText != null -> {
                    val italicContent = italicText.removeSurrounding("*")
                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(italicContent)
                    }
                }
                bulletPoint != null -> {
                    append("• ")
                }
                numberedText != null -> {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("$numberedText. ")
                    }
                }
                numberedParagraph != null -> {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(numberedParagraph)
                    }
                }
            }

            lastIndex = matchResult.range.last + 1
        }

        append(text.substring(lastIndex))
    }
}





fun formatTextWithHtmlTags(input: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val regex = "<(/?b|/?i)>".toRegex()
    val tags = mutableListOf<Pair<String, Int>>()

    var lastIndex = 0
    var matchResult = regex.find(input, lastIndex)

    while (matchResult != null) {
        val tag = matchResult.value
        val startIndex = matchResult.range.first

        // Append text before the tag
        if (startIndex > lastIndex) {
            val text = input.substring(lastIndex, startIndex)
            builder.append(text)
        }

        when (tag) {
            "<b>" -> tags.add(Pair("b", builder.length))
            "</b>" -> {
                val (tagName, start) = tags.last { it.first == "b" }
                tags.remove(Pair(tagName, start))
                builder.addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, builder.length)
            }
            "<i>" -> tags.add(Pair("i", builder.length))
            "</i>" -> {
                val (tagName, start) = tags.last { it.first == "i" }
                tags.remove(Pair(tagName, start))
                builder.addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, builder.length)
            }
        }

        lastIndex = matchResult.range.last + 1
        matchResult = regex.find(input, lastIndex)
    }

    if (lastIndex < input.length) {
        builder.append(input.substring(lastIndex))
    }

    return builder.toAnnotatedString()
}

fun sendEmail(context: Context){

    val email = context.getString(R.string.support_email)
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, "Report a Problem")
        putExtra(Intent.EXTRA_TEXT, "Describe your issue in detail...")
    }

    if (emailIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(emailIntent)
    }
}

fun NavController.popBackStackOrIgnore() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    val userDataRepository = UserDataRepository(context)
    val isPermissionGranted = remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            isPermissionGranted.value = isGranted

            if (isGranted) {
                Log.d("permissions","Notification permission granted.")
            } else {
                Log.d("permissions","Notification permission denied.")
            }
            userDataRepository.isRequestNotificationPermissionDone = true

        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isPermissionGranted.value && !userDataRepository.isRequestNotificationPermissionDone) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}