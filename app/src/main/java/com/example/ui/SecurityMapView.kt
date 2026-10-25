package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LoanRequest
import com.example.ui.theme.SipemoAmber
import com.example.ui.theme.SipemoEmerald
import com.example.ui.theme.SipemoEmeraldDark
import com.example.ui.theme.SipemoNavyBorder
import com.example.ui.theme.SipemoNavyCard
import com.example.ui.theme.SipemoNavyDark
import com.example.ui.theme.SipemoRose
import kotlin.math.atan2

data class NavigationStep(
    val time: String,
    val title: String,
    val distance: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

@Composable
fun SecurityMapView(
    loans: List<LoanRequest>,
    selectedLoan: LoanRequest?,
    modifier: Modifier = Modifier
) {
    val activeOutsideLoans = loans.filter { it.securityStatus == "DI_LUAR" }
    val currentLoan = selectedLoan ?: activeOutsideLoans.firstOrNull() ?: loans.firstOrNull()

    // Interactive Camera Zoom & Pan states
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    // Simulation play / pause state
    var isSimulating by remember { mutableStateOf(true) }
    var isFollowCarMode by remember { mutableStateOf(true) }
    var isStepsExpanded by remember { mutableStateOf(false) }

    // Pulse animation for GPS marker
    val infiniteTransition = rememberInfiniteTransition(label = "nav_anim")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    // Animated car progress along the route (0.0 to 1.0)
    val carProgress by infiniteTransition.animateFloat(
        initialValue = 0.50f,
        targetValue = if (isSimulating) 0.88f else 0.50f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "car_progress"
    )

    // Navigation Turn-by-Turn Steps
    val navSteps = listOf(
        NavigationStep("07:35 WIB", "Keluar dari Gerbang Utama SRT 1 Kediri", "0 km", isCompleted = true, isCurrent = false),
        NavigationStep("07:55 WIB", "Masuk Jl. Raya Kediri - Kertosono", "18 km", isCompleted = true, isCurrent = false),
        NavigationStep("08:20 WIB", "Melewati Gerbang Tol Kertosono (Tol Trans Jawa)", "28 km", isCompleted = true, isCurrent = false),
        NavigationStep("08:45 WIB", "Jalur Cepat Tol Jombang - Mojokerto KM 682", "42 km", isCompleted = false, isCurrent = true),
        NavigationStep("09:15 WIB", "Lintas Gerbang Tol Waru - Surabaya", "76 km", isCompleted = false, isCurrent = false),
        NavigationStep("09:45 WIB", "Tiba di Kantor Dinas Pendidikan Jatim, Surabaya", "84 km", isCompleted = false, isCurrent = false)
    )

    Column(modifier = modifier.fillMaxSize()) {

        // 1. TOP MANEUVER HUD BANNER (Mapbox Navigation Signature Style)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF042F2E), // Mapbox Navigation Night Dark Emerald
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SipemoEmerald)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Turn maneuver icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SipemoEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TurnRight,
                            contentDescription = null,
                            tint = SipemoNavyDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "350 m",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SipemoAmber.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SipemoAmber)
                            ) {
                                Text(
                                    text = "TOL TRANS JAWA",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SipemoAmber,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Tetap di Jalur Kanan Tol Jombang Menuju Surabaya",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA7F3D0)
                        )
                    }

                    // Speedometer pill
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SipemoEmerald)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "65",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = " km/h",
                                fontSize = 10.sp,
                                color = Color(0xFFA7F3D0),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Normal GPS",
                            fontSize = 10.sp,
                            color = SipemoEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ETA & Distance Sub-bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sisa: 42 mnt • 36,4 km • Estimasi Tiba 09:45 WIB",
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.clickable { isStepsExpanded = !isStepsExpanded },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isStepsExpanded) "Tutup Rute" else "Panduan Rute",
                            fontSize = 11.sp,
                            color = SipemoAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isStepsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = SipemoAmber,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Expandable Waypoints / Directions List
        AnimatedVisibility(visible = isStepsExpanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = SipemoNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "RUTE PERJALANAN KEDIRI - SURABAYA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SipemoAmber,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    navSteps.forEach { step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (step.isCompleted) SipemoEmerald
                                        else if (step.isCurrent) SipemoAmber
                                        else Color(0xFF334155)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (step.isCompleted) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = SipemoNavyDark, modifier = Modifier.size(12.dp))
                                } else if (step.isCurrent) {
                                    Icon(Icons.Default.Navigation, contentDescription = null, tint = SipemoNavyDark, modifier = Modifier.size(10.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = step.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (step.isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (step.isCurrent) Color.White else Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = "${step.time} • Jarak: ${step.distance}",
                                    fontSize = 10.sp,
                                    color = if (step.isCurrent) SipemoAmber else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. INTERACTIVE MAPBOX NAVIGATION NIGHT CARTOGRAPHIC CANVAS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A)) // Night map background
                .border(1.dp, SipemoNavyBorder, RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.7f, 3.5f)
                        panOffsetX = (panOffsetX + pan.x).coerceIn(-400f, 400f)
                        panOffsetY = (panOffsetY + pan.y).coerceIn(-400f, 400f)
                        isFollowCarMode = false
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw cartographic terrain styling
                drawCartographicBackground(w, h, zoomScale, panOffsetX, panOffsetY)

                // Define geographic anchors in East Java
                val baseKediri = Offset(w * 0.28f * zoomScale + panOffsetX, h * 0.72f * zoomScale + panOffsetY)
                val ptKertosono = Offset(w * 0.42f * zoomScale + panOffsetX, h * 0.54f * zoomScale + panOffsetY)
                val ptJombangTol = Offset(w * 0.56f * zoomScale + panOffsetX, h * 0.44f * zoomScale + panOffsetY)
                val ptMojokertoTol = Offset(w * 0.70f * zoomScale + panOffsetX, h * 0.38f * zoomScale + panOffsetY)
                val hubSurabaya = Offset(w * 0.86f * zoomScale + panOffsetX, h * 0.28f * zoomScale + panOffsetY)
                val hubMalang = Offset(w * 0.58f * zoomScale + panOffsetX, h * 0.86f * zoomScale + panOffsetY)
                val hubBlitar = Offset(w * 0.36f * zoomScale + panOffsetX, h * 0.90f * zoomScale + panOffsetY)
                val hubNganjuk = Offset(w * 0.16f * zoomScale + panOffsetX, h * 0.52f * zoomScale + panOffsetY)

                // Draw secondary connecting roads
                val secRoadColor = Color(0xFF334155)
                drawLine(secRoadColor, baseKediri, hubBlitar, strokeWidth = 3f * zoomScale)
                drawLine(secRoadColor, baseKediri, hubMalang, strokeWidth = 3f * zoomScale)
                drawLine(secRoadColor, baseKediri, hubNganjuk, strokeWidth = 3f * zoomScale)
                drawLine(secRoadColor, hubNganjuk, ptKertosono, strokeWidth = 3f * zoomScale)

                // Draw Highway (Tol Trans Jawa) Bed
                val tollPath = Path().apply {
                    moveTo(baseKediri.x, baseKediri.y)
                    quadraticTo(ptKertosono.x, ptKertosono.y, ptJombangTol.x, ptJombangTol.y)
                    quadraticTo(ptMojokertoTol.x, ptMojokertoTol.y, hubSurabaya.x, hubSurabaya.y)
                }

                // Road bed outer casing
                drawPath(
                    path = tollPath,
                    color = Color(0xFF1E293B),
                    style = Stroke(width = 14f * zoomScale)
                )

                // Road surface asphalt
                drawPath(
                    path = tollPath,
                    color = Color(0xFF334155),
                    style = Stroke(width = 10f * zoomScale)
                )

                // Mapbox Navigation Route Highlight (Glowing Emerald / Cyan)
                drawPath(
                    path = tollPath,
                    color = SipemoEmerald,
                    style = Stroke(width = 5f * zoomScale)
                )

                // Moving animated route dashes
                drawPath(
                    path = tollPath,
                    color = Color.White.copy(alpha = 0.6f),
                    style = Stroke(
                        width = 2f * zoomScale,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), pulseRadius * 2)
                    )
                )

                // Calculate real-time car position along the parametric quadratic bezier curve
                val t = carProgress
                val carPos = if (t < 0.5f) {
                    val subT = t * 2f
                    quadraticBezierPoint(baseKediri, ptKertosono, ptJombangTol, subT)
                } else {
                    val subT = (t - 0.5f) * 2f
                    quadraticBezierPoint(ptJombangTol, ptMojokertoTol, hubSurabaya, subT)
                }

                // Calculate direction angle of the car
                val nextT = (t + 0.02f).coerceAtMost(1f)
                val nextPos = if (nextT < 0.5f) {
                    val subT = nextT * 2f
                    quadraticBezierPoint(baseKediri, ptKertosono, ptJombangTol, subT)
                } else {
                    val subT = (nextT - 0.5f) * 2f
                    quadraticBezierPoint(ptJombangTol, ptMojokertoTol, hubSurabaya, subT)
                }

                // Draw Regional City Hubs
                drawMapPin(center = hubSurabaya, label = "Surabaya", color = Color(0xFFEF4444), isDestination = true)
                drawMapPin(center = hubMalang, label = "Malang", color = Color(0xFF64748B))
                drawMapPin(center = hubBlitar, label = "Blitar", color = Color(0xFF64748B))
                drawMapPin(center = hubNganjuk, label = "Nganjuk", color = Color(0xFF64748B))
                drawMapPin(center = ptJombangTol, label = "Jombang (KM 682)", color = Color(0xFF38BDF8))

                // Draw Base Camp: Sekolah Rakyat SRT 1 Kediri
                drawCircle(SipemoAmber.copy(alpha = 0.25f), 18f * zoomScale, baseKediri)
                drawCircle(SipemoAmber, 8f * zoomScale, baseKediri)

                // Draw Live Vehicle GPS Radar Pulse
                drawCircle(
                    color = SipemoEmerald.copy(alpha = (1f - (pulseRadius / 28f)).coerceIn(0f, 0.7f)),
                    radius = pulseRadius * zoomScale,
                    center = carPos
                )

                // Vehicle Arrow Head & Dot
                drawCircle(Color.White, 7f * zoomScale, carPos)
                drawCircle(SipemoEmerald, 5f * zoomScale, carPos)

                // Navigation Arrow Heading
                val angle = atan2(nextPos.y - carPos.y, nextPos.x - carPos.x)
                val arrowTip = Offset(carPos.x + 14f * kotlin.math.cos(angle), carPos.y + 14f * kotlin.math.sin(angle))
                drawLine(Color.White, carPos, arrowTip, strokeWidth = 3f)
            }

            // Map Overlays: Badges & HUD Chips on Map
            // 1. Base Camp Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SipemoNavyDark.copy(alpha = 0.92f))
                    .border(1.dp, SipemoAmber, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = SipemoAmber, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SRT 1 Kediri (Markas Gerbang)",
                        color = SipemoAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. Live Moving Car Floating Label
            if (activeOutsideLoans.isNotEmpty()) {
                val movingCar = currentLoan ?: activeOutsideLoans.first()
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SipemoNavyDark.copy(alpha = 0.95f))
                        .border(1.dp, SipemoEmerald, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SipemoEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🚗 ${movingCar.carPlate} • Tol KM 682",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Destinasi Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SipemoNavyDark.copy(alpha = 0.92f))
                    .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Surabaya (Dinas Jatim)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 4. Mapbox Branding Stamp in Bottom Right (Authentic Mapbox Signature)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "mapbox navigasi",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // 5. On-Map Interactive Camera Controls (Right side floating pill)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Zoom In
                MapControlButton(icon = Icons.Default.Add, description = "Zoom In") {
                    zoomScale = (zoomScale * 1.25f).coerceAtMost(3.5f)
                }

                // Zoom Out
                MapControlButton(icon = Icons.Default.Remove, description = "Zoom Out") {
                    zoomScale = (zoomScale / 1.25f).coerceAtLeast(0.7f)
                }

                // Focus Follow Car
                MapControlButton(
                    icon = Icons.Default.NearMe,
                    description = "Ikuti Mobil",
                    tint = if (isFollowCarMode) SipemoEmerald else Color.White
                ) {
                    isFollowCarMode = true
                    panOffsetX = 0f
                    panOffsetY = 0f
                    zoomScale = 1.15f
                }

                // Reset Camera / Overview
                MapControlButton(icon = Icons.Default.CenterFocusStrong, description = "Lihat Semua") {
                    panOffsetX = 0f
                    panOffsetY = 0f
                    zoomScale = 1.0f
                }

                // Play / Pause Telemetry Simulation
                MapControlButton(
                    icon = if (isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                    description = "Simulasi Perjalanan",
                    tint = SipemoAmber
                ) {
                    isSimulating = !isSimulating
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. VEHICLE TELEMETRY CARD
        if (currentLoan != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SipemoNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SipemoEmerald.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SipemoEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${currentLoan.carName} • ${currentLoan.carPlate}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Pengemudi: ${currentLoan.userName} (${currentLoan.userJabatan})",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (currentLoan.securityStatus == "DI_LUAR") Color(0xFF0369A1).copy(alpha = 0.3f) else SipemoAmber.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (currentLoan.securityStatus == "DI_LUAR") Color(0xFF38BDF8) else SipemoAmber
                            )
                        ) {
                            Text(
                                text = if (currentLoan.securityStatus == "DI_LUAR") "DI PERJALANAN" else "STANDBY GERBANG",
                                color = if (currentLoan.securityStatus == "DI_LUAR") Color(0xFF38BDF8) else SipemoAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Lokasi Terkini:", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(currentLoan.locationName, fontSize = 12.sp, color = SipemoEmerald, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Tujuan:", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(currentLoan.destination, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(SipemoNavyDark.copy(alpha = 0.9f))
            .border(1.dp, SipemoNavyBorder, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun DrawScope.drawCartographicBackground(
    w: Float,
    h: Float,
    scale: Float,
    panX: Float,
    panY: Float
) {
    // Subtle map coordinate grid
    val gridColor = Color(0xFF1E293B).copy(alpha = 0.6f)
    val step = 60f * scale
    var x = panX % step
    while (x < w) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, h), 1f)
        x += step
    }
    var y = panY % step
    while (y < h) {
        drawLine(gridColor, Offset(0f, y), Offset(w, y), 1f)
        y += step
    }

    // Draw stylized flowing Sungai Brantas (Natural geographic landmark of Kediri)
    val riverPath = Path().apply {
        moveTo(w * 0.22f * scale + panX, h * 0.95f * scale + panY)
        cubicTo(
            w * 0.32f * scale + panX, h * 0.70f * scale + panY,
            w * 0.38f * scale + panX, h * 0.58f * scale + panY,
            w * 0.44f * scale + panX, h * 0.50f * scale + panY
        )
        cubicTo(
            w * 0.58f * scale + panX, h * 0.46f * scale + panY,
            w * 0.68f * scale + panX, h * 0.42f * scale + panY,
            w * 0.82f * scale + panX, h * 0.32f * scale + panY
        )
    }
    drawPath(
        path = riverPath,
        color = Color(0xFF0C4A6E).copy(alpha = 0.4f),
        style = Stroke(width = 8f * scale)
    )
}

private fun DrawScope.drawMapPin(
    center: Offset,
    label: String,
    color: Color,
    isDestination: Boolean = false
) {
    if (isDestination) {
        drawCircle(color.copy(alpha = 0.3f), 14f, center)
        drawCircle(color, 6f, center)
    } else {
        drawCircle(color.copy(alpha = 0.5f), 7f, center)
        drawCircle(Color.White, 3f, center)
    }
}

/**
 * Calculates a point on a Quadratic Bezier curve at progress t (0.0 to 1.0)
 */
private fun quadraticBezierPoint(p0: Offset, p1: Offset, p2: Offset, t: Float): Offset {
    val u = 1f - t
    val tt = t * t
    val uu = u * u
    val x = uu * p0.x + 2 * u * t * p1.x + tt * p2.x
    val y = uu * p0.y + 2 * u * t * p1.y + tt * p2.y
    return Offset(x, y)
}
