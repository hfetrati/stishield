package com.hemad.stishield.ui.utilities

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hemad.stishield.R
import com.hemad.stishield.model.common.Message
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.hemad.stishield.ui.theme.lora
import kotlinx.coroutines.delay


val BOT_COLOR = Color(0xFFE0E0E0)
val USER_COLOR = Color(0xFF7DA6CF)
val RISK_USER_COLOR = Color(0xFF53D769)

@Composable
fun MessageBubbleWithButtons(
    modifier: Modifier = Modifier,
    id: Int? = null,
    text:AnnotatedString,
    role: Message.SenderRole,
    imageID: Int,
    buttonTitles:List<String>? = null,
    buttonsEnabledState:Boolean? = null,
    onButtonPressed: ((Int, String) -> Unit)? = null
){

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 14.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(modifier = Modifier
            .clip(shape = CircleShape)
            .size(35.dp),
            painter = painterResource(id = imageID),
            contentDescription = null
        )

        Spacer(modifier = Modifier
            .width(12.dp)
        )

        Column (
            modifier = modifier
                .width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start

        ) {
            Card(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .wrapContentSize(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (role == Message.SenderRole.BOT) BOT_COLOR else RISK_USER_COLOR,
                )

            ) {
                SelectionContainer {
                    Text(modifier = Modifier.padding(12.dp),
                        text = text,
                        textAlign = TextAlign.Justify,
                        color = if (role == Message.SenderRole.BOT) Color.Black else Color.White
                    )
                }
            }

            if (!buttonTitles.isNullOrEmpty()){
                for (title in buttonTitles) {
                    Button(
                        modifier = modifier.fillMaxWidth(),
                        enabled = buttonsEnabledState!!,
                        shape = RoundedCornerShape(8.dp),
                        onClick = {onButtonPressed!!(id!!,title)}
                    ) {
                        Text(
                            text = title,
                            textAlign = TextAlign.Justify

                        )
                    }
                }
            }
        }

    }



}

@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    text:AnnotatedString,
    role: Message.SenderRole,
    imageID:Int
){

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(modifier = Modifier
            .clip(shape = CircleShape)
            .size(35.dp),
            painter = painterResource(id = imageID),
            contentDescription = null
        )
        
        Spacer(modifier = Modifier
            .width(8.dp)
        )
        
        Card(
            modifier = Modifier
                .wrapContentSize(),
            colors = CardDefaults.cardColors(
                containerColor = if (role == Message.SenderRole.BOT) BOT_COLOR else USER_COLOR,
            )

        ) {
            SelectionContainer {
                Text(modifier = Modifier.padding(12.dp),
                    text = text,
                    color = if (role == Message.SenderRole.BOT) Color.Black else Color.White
                )
            }
        }
    }

}

@Composable
fun CustomTextField(
    modifier:Modifier = Modifier,
    placeholder:String,
    text: String, onValueChangeFunc:(String) -> Unit,
    trailingIconFunc:(() -> Unit)? = null,
){

    OutlinedTextField(
        modifier = modifier
            .padding(6.dp),
        value = text,
        onValueChange = onValueChangeFunc,
        placeholder = {
            Text(placeholder)
        },
        shape = RoundedCornerShape(28.dp),
        trailingIcon = {
            if (trailingIconFunc != null && text.isNotEmpty()) {
                IconButton(
                    onClick = trailingIconFunc
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null
                    )
                }
            }
        },
    )
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    icon: ImageVector = Icons.AutoMirrored.Filled.Send,
    onClickFunc:()->Unit
){

    Button(onClick = onClickFunc,
        modifier= modifier
            .padding(8.dp)
            .size(50.dp),
        shape = CircleShape,
        enabled = enabled,
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null)
    }

}

@Composable
fun ErrorPopup(
    error: String,
    onDismiss: () -> Unit
) {

    AlertDialog(
        icon = {
            Icon(imageVector = Icons.Default.Warning, contentDescription = null)
               },
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Error")
                },
        text = {
            Text(text = error)
               },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Dismiss")
            }
        }

    )
}

@Composable
fun NameRow(
    name:String,
    imageID:Int
){

    Row (
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(modifier = Modifier
            .clip(shape = CircleShape)
            .size(45.dp),
            painter = painterResource(id = imageID),
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.Start
        ){
            Text(
                modifier = Modifier.padding(2.dp),
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
            Text(
                modifier = Modifier.padding(2.dp),
                text = "Online",
                color = Color(0xFF2da62d),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterImageAppBar(
    imageResId: Int,
    backgroundColor: Color = Color(0xFFFFFBFF),
    onBackClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null,
    actionIconVector: ImageVector? = null,
    dropdownMenu:(@Composable () -> Unit)? = null
) {
    Box(
        modifier = Modifier.shadow(elevation = 15.dp)
    ) {
        CenterAlignedTopAppBar(
            title = {
                AsyncImage(
                    model = imageResId, // Pass the drawable resource ID
                    contentDescription = null,
                    modifier = Modifier.size(44.dp)
                )
            },
            navigationIcon = {
                if(onBackClick != null){
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            actions = {
                if(onActionClick != null && actionIconVector != null){
                    IconButton(onClick = onActionClick) {
                        Icon(actionIconVector, contentDescription = "null")
                    }
                }
                if(dropdownMenu != null){
                    dropdownMenu()
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = backgroundColor
            ),
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageAppBar(
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    name:String,
    imageID:Int
) {
    Box(
        modifier = Modifier.shadow(elevation = 10.dp)
    ) {
        TopAppBar(
            title = {
                NameRow(name=name, imageID = imageID)
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onRefreshClick) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextAppBar(
    title:String,
    fontFamily: FontFamily = FontFamily.Default,
    fontSize:TextUnit = 18.sp,
    fontWeight: FontWeight = FontWeight.Black,
    backgroundColor: Color = Color(0xFFFFFBFF),
    contentColor: Color = Color.Black,
    textAlign: TextAlign = TextAlign.Center,
    onBackClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null,
    actionIconVector: ImageVector? = null,
    dropdownMenu:(@Composable () -> Unit)? = null
) {
    Box(
        modifier = Modifier.shadow(elevation = 15.dp)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    fontFamily = fontFamily,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    textAlign = textAlign,
                    text = title
                )
            },
            navigationIcon = {
                if(onBackClick != null){
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            actions = {
                if(onActionClick != null && actionIconVector != null){
                    IconButton(onClick = onActionClick) {
                        Icon(actionIconVector, contentDescription = "null")
                    }
                }
                if(dropdownMenu != null){
                    dropdownMenu()
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = backgroundColor,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor
            ),
        )
    }
}



@Composable
fun ScrollTextPopup(
    text: AnnotatedString,
    onDismiss: () -> Unit
) {
    val imageHeight = remember { mutableStateOf(0) }
    val density = LocalDensity.current.density

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .zIndex(1f)
            .pointerInput(Unit) {
                detectTapGestures {}
            }
    ) {
        Box(
            Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth(0.8f)// Popup takes half the screen
                .align(Alignment.Center)
        ) {


            AsyncImage(
                model = R.drawable.scroll_background,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        // Store the height of the image
                        imageHeight.value = coordinates.size.height
                    }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val topPadding = (imageHeight.value/density) / 8 // Convert pixel height to dp
                Spacer(modifier = Modifier.height(topPadding.dp))
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    item {
                        Text(
                            text = text,
                            fontFamily = lora,
                            modifier = Modifier
                                .background(Color.Transparent)
                                .padding(16.dp)
                        )
                    }
                }
                // Transparent dismiss button at the bottom
                TextButton(
                    modifier = Modifier.
                    align(Alignment.CenterHorizontally),
                    onClick = onDismiss,
                ) {
                    Text(
                        fontWeight = FontWeight.Bold,
                        fontFamily = lora,
                        color = Color.Black,
                        text = "Dismiss",
                    )
                }
                Spacer(modifier = Modifier.height(16.dp)) // Adjust the height to control the button's position
            }
        }
    }
}

@Composable
fun WoodenButton(text: String, colors:ButtonColors, onClick:() -> Unit) {
    val image: Painter = painterResource(id = R.drawable.wood_button)

    Button(
        onClick = onClick,
        colors = colors,
        shape = RectangleShape,
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()

    ) {
        Box() {
            Image(
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
            )
            Text(
                fontSize = 17.sp,
                fontFamily = lora,
                fontWeight = FontWeight.Black,
                text = text,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun CustomCard2(
    title: String,
    scoreString:String = "",
    imageResId: Int,
    showIcon:Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(85.dp)
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )


    ) {
        Row (
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                modifier = Modifier
                    .padding(18.dp)
                    .size(40.dp),
                painter = painterResource(id = imageResId),
                alpha = if(showIcon) 1f else 0f,
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            Text(
                text = title,
                fontWeight = FontWeight.Bold,

                )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = scoreString,
                fontWeight = FontWeight.Normal
            )
            Image(
                modifier = Modifier
                    .padding(16.dp)
                    .size(16.dp),
                painter = painterResource(id = R.drawable.right_arrow),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )


        }
    }
}

@Composable
fun CustomCard(
    title: String,
    imageResId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(85.dp)
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )


    ) {
        Row (
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = imageResId, // Provide the image resource ID
                contentDescription = null,
                modifier = Modifier
                    .padding(18.dp)
                    .size(40.dp), // Apply the same padding and size
                contentScale = ContentScale.Fit // Maintain the content scale
            )
            Text(
                text = title,
                fontWeight = FontWeight.Light,

                )
            Spacer(modifier = Modifier.weight(1f))
            AsyncImage(
                model = R.drawable.right_arrow, // Provide the drawable resource
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .size(16.dp),
                contentScale = ContentScale.Fit // Keep the content scale as Fit
            )


        }
    }
}



@Composable
fun TypingAnimation(
    modifier: Modifier = Modifier,
    circleSize: Dp = 10.dp,
    circleColor: Color = Color.Black,
    spaceBetween: Dp = 7.dp,
    travelDistance: Dp = 12.dp
) {
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 using LinearOutSlowInEasing
                        1.0f at 300 using LinearOutSlowInEasing
                        0.0f at 600 using LinearOutSlowInEasing
                        0.0f at 1200 using LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val circleValues = circles.map { it.value }
    val distance = with(LocalDensity.current) { travelDistance.toPx() }

    Row(
        modifier = modifier.padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(spaceBetween)
    ) {
        circleValues.forEach { value ->
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .graphicsLayer {
                        translationY = -value * distance
                    }
                    .background(
                        color = circleColor,
                        shape = CircleShape
                    )
            )
        }
    }

}

@Composable
fun ProgressIndicator() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(color = Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 3.dp
        )
    }
}

@Composable
fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        repeat(pageCount){
            IndicatorSingleDot(isSelected = it == currentPage )
        }


    }
}

@Composable
fun IndicatorSingleDot(isSelected: Boolean) {

    val width = animateDpAsState(targetValue = if (isSelected) 35.dp else 15.dp, label = "")
    Box(modifier = Modifier
        .padding(2.dp)
        .height(15.dp)
        .width(width.value)
        .clip(CircleShape)
        .background(if (isSelected) Color(0xFF1E2FE9) else Color(0x251E2FE9))
    )
}





