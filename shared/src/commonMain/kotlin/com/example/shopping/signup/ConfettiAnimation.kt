package com.example.shopping.signup

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val size: Float,
    val velocityX: Float,
    val velocityY: Float,
    val gravity: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val color: Color,
    val delay: Float
)

@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier
) {
    val particles = remember {
        List(180) {
            ConfettiParticle(
                startX = Random.nextFloat(),
                startY = -Random.nextFloat() * 0.4f,

                size = Random.nextFloat() * 12f + 8f,

                velocityX =
                    Random.nextFloat() * 700f - 350f,

                velocityY =
                    Random.nextFloat() * 250f + 80f,

                gravity =
                    Random.nextFloat() * 500f + 500f,

                rotation =
                    Random.nextFloat() * 360f,

                rotationSpeed =
                    Random.nextFloat() * 720f - 360f,

                color = listOf(
                    Color(0xFF7F52FF),
                    Color(0xFFFFC107),
                    Color(0xFFE91E63),
                    Color(0xFF2196F3),
                    Color(0xFF4CAF50),
                    Color(0xFFFF5722),
                    Color(0xFF00BCD4),
                    Color(0xFFFF4081)
                ).random(),

                delay =
                    Random.nextFloat() * 0.08f
            )
        }
    }

    val progress = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 5000
            )
        )
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        particles.forEach { particle ->

            val particleProgress =
                (
                        (progress.value - particle.delay) /
                                (1f - particle.delay)
                        ).coerceIn(0f, 1f)

            if (particleProgress <= 0f) {
                return@forEach
            }

            val time = particleProgress * 3.2f

            val x =
                particle.startX * size.width +
                        particle.velocityX * time

            val y =
                particle.startY * size.height +
                        particle.velocityY * time +
                        0.5f *
                        particle.gravity *
                        time *
                        time

            val rotation =
                particle.rotation +
                        particle.rotationSpeed * time

            val alpha =
                when {
                    particleProgress < 0.7f -> 1f
                    else ->
                        1f -
                                ((particleProgress - 0.7f) / 0.3f)
                }

            drawContext.canvas.save()

            drawContext.canvas.translate(
                dx = x,
                dy = y
            )

            drawContext.canvas.rotate(
                degrees = rotation
            )

            drawRect(
                color = particle.color.copy(
                    alpha = alpha
                ),
                topLeft = Offset(
                    x = -particle.size,
                    y = -particle.size / 2f
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = particle.size * 1.8f,
                    height = particle.size
                )
            )

            drawContext.canvas.restore()
        }
    }
}