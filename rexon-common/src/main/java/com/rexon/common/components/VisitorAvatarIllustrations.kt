package com.rexon.common.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rexon.common.R
import com.rexon.common.models.VisitorCategory

/**
 * Resolves the 3D asset drawable resource for each category
 * (Guest, Delivery, Services, Cab)
 */
@DrawableRes
fun getCategoryDrawableRes(category: VisitorCategory): Int {
    return when (category) {
        VisitorCategory.GUEST -> R.drawable.ic_category_guest
        VisitorCategory.DELIVERY -> R.drawable.ic_category_delivery
        VisitorCategory.SERVICE -> R.drawable.ic_category_service
        VisitorCategory.CAB -> R.drawable.ic_category_cab
    }
}

/**
 * 3D Category Logo badge matching user-provided high-definition assets:
 * - Guest: 3D avatar with sunglasses on cyan rounded badge
 * - Delivery: 3D blue scooter with delivery box and large pink map pin
 * - Service: 3D hand holding yellow & red gear cogs
 * - Cab: 3D hand holding smartphone with yellow taxi & pin
 */
@Composable
fun VisitorIllustrationAvatar(
    category: VisitorCategory,
    modifier: Modifier = Modifier.size(52.dp),
    isCircular: Boolean = true,
    padding: Dp = 3.dp
) {
    val shape = if (isCircular) CircleShape else RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), shape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = getCategoryDrawableRes(category)),
            contentDescription = "${category.label} 3D Logo",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Category 3D Logo Card for selection panels, dialpad footer cards, and pre-approval options
 */
@Composable
fun CategoryLogoBadge(
    category: VisitorCategory,
    modifier: Modifier = Modifier.size(72.dp),
    cornerRadius: Dp = 16.dp,
    padding: Dp = 3.dp
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), shape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = getCategoryDrawableRes(category)),
            contentDescription = "${category.label} Logo",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentScale = ContentScale.Fit
        )
    }
}
