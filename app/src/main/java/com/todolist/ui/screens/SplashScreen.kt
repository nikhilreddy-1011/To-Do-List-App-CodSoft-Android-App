package com.todolist.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todolist.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    // Animation values
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    
    // Pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // Launch animation on composition
    LaunchedEffect(key1 = true) {
        // Start all animations concurrently
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                )
            )
        }
        
        launch {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 1200,
                    easing = FastOutSlowInEasing
                )
            )
        }
        
        // Wait for splash screen display duration
        delay(2500)
        
        // Navigate to home screen
        onSplashComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Logo with pulsing effect after initial animation
            Image(
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value * if (scale.value == 1f) pulse else 1f)
                    .graphicsLayer {
                        rotationZ = rotation.value
                    }
                    .alpha(alpha.value)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Animated Text with gradient effect
            Text(
                text = "To-Do List",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.alpha(alpha.value),
                style = MaterialTheme.typography.headlineLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Attribution Text
            Text(
                text = "(Internship Project by Nikhil K)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black.copy(alpha = 0.4f),
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}
