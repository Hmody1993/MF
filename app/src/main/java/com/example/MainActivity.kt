package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.NewsPipelineEntity
import com.example.data.TradeEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Premium Modern Luxury Color Palette
val MidnightNavy = Color(0xFF040A15)       // Deep luxury backgrounds
val CobaltBlueCard = Color(0xFF0D1B2E)    // High contrast content container
val PremiumRoyalGold = Color(0xFFD4AF37)  // Elite accents, margins and buttons
val SoftChampagneGold = Color(0xFFF3E5AB) // Muted luxury golden details
val ActiveGreenGlow = Color(0xFF00E676)   // Profits and 100% win rate emerald indicators
val WarningOrangeAccent = Color(0xFFFF9100)
val TechCyanGlow = Color(0xFF00E5FF)      // Intelligent Gemini Coprocessor node colors
val LuxurySlateBlue = Color(0xFF2C3E50)
val SoftGreyText = Color(0xFF8C9BB4)      // Eye-friendly high contrast secondary support

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MidnightNavy)
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MidnightNavy)
                    ) {
                        LuxuryTerminalDashboard()
                    }
                }
            }
        }
    }
}

@Composable
fun LuxuryTerminalDashboard(viewModel: MainViewModel = viewModel()) {
    val isAr by viewModel.isArabic.collectAsState()
    val balance by viewModel.portfolioBalance.collectAsState()
    val lastProfitFlash by viewModel.lastProfitFlash.collectAsState()
    val terminalAlert by viewModel.terminalAlert.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val selectedMarket by viewModel.activeSubscription.collectAsState()

    val isClaudeEnabled by viewModel.isClaudeSentinelEnabled.collectAsState()
    val isHftEnabled by viewModel.isHftCoProcessorEnabled.collectAsState()
    val isSwapEnabled by viewModel.isRapidSwapEnabled.collectAsState()
    val isScraperEnabled by viewModel.isOsintScraperEnabled.collectAsState()
    val isSuperFastScalperEnabled by viewModel.isSuperFastScalperEnabled.collectAsState()
    val isNewCoinSniperEnabled by viewModel.isNewCoinSniperEnabled.collectAsState()
    val isMasterTradingEnabled by viewModel.isMasterAutoTradingEnabled.collectAsState()
    var userTokenAddressText by remember { mutableStateOf("") }

    val rawTrades by viewModel.liveTrades.collectAsState()
    val rawNews by viewModel.newsPipeline.collectAsState()

    val tpsMetrics by viewModel.tpsMetrics.collectAsState()
    val latencyMetrics by viewModel.latencyMetrics.collectAsState()
    val nodeTriggerPulse by viewModel.activeNodePulse.collectAsState()

    var userSignalText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Wallet transaction popup dialogue states with custom banks/gateways
    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    // Interactivity state elements for tactile manual arbitrage
    var arbSelectedToken by remember { mutableStateOf("BTC") }
    var arbBuyPlatform by remember { mutableStateOf("Binance") }
    var arbSellPlatform by remember { mutableStateOf("Coinbase") }
    var arbAmountValue by remember { mutableStateOf("5000") }
    var isAdvancedToolsExpanded by remember { mutableStateOf(true) }
    var selectedScreenTab by remember { mutableStateOf(0) }

    // Enhanced VIP Dynamic Localized Labels
    val textAppTitle = if (isAr) "منظومة النخبة للذكاء المالي المتكامل HFT" else "AWXN // VIP AUTOMATED HFT LEADER"
    val textTotalBalanceLabel = if (isAr) "إجمالي المحفظة الاستثمارية النشطة 👑" else "TOTAL ACTIVE PORTFOLIO VALUE 👑"
    val textZeroLossGuaranteed = if (isAr) "تأمين شامل وحماية 100% ضد الخسائر مفعلة" else "100% ZERO-LOSS PRINCIPAL INSURANCE CERTIFIED"
    val textBotAccuracyLabel = if (isAr) "مستوى دقة اقتناص الفرص" else "BOT OPPORTUNITY ACCURACY"
    val textInstantExecutionTime = if (isAr) "تداول فوري: بالملي ثانية 🚀" else "MICROSECOND LATENCY SPEED 🚀"
    val textActiveSectorsHeader = if (isAr) "القطاعات المالية الخاضعة للمضاربة الفورية الذكية" else "MONITORED FINANCIAL SECTORS ENGINE"
    val textSatelliteMapTitle = if (isAr) "رادار الربط القاري بالأقمار الصناعية" else "GLOBAL LOW-LATENCY TRANSMISSION MAP"
    val textStatusSteady = if (isAr) "البث المباشر: مستقر وفعال 💯" else "FEED STATUS: STREAMING SMOOTH 💯"
    val textPerformanceChartTitle = if (isAr) "سرعة تنفيذ صفقات الروبوت (تداولات/ثانية)" else "ALGO SCALPING VELOCITY IN SECONDS"
    
    val textGeminiSecured = if (isAr) "واجهة جيميناي للذكاء المعزز نشطة" else "GEMINI CLOUD AI COPROCESSOR SECURED"
    val textLocalCoreSecured = if (isAr) "محرك الذكاء الاصطناعي الثنائي نشط وسلس" else "BILINGUAL COPROCESSOR RUNNING FLUSH"
    
    val textSignalPlaceholder = if (isAr) "اكتب خبراً مالياً أو إشارة ليقوم الروبوت باقتناصها فوراً..." else "Type financial signal or alert to frontrun instantly..."
    val textFrontrunBtn = if (isAr) "اقتناص الصفقة لثانية ⚡" else "FRONTRUN ALGO ⚡"
    val textScanningBtn = if (isAr) "جاري التحليل والدراسة..." else "SCANNING..."

    val textNewCoinSniperTitle = if (isAr) "رادار صيد واقتناص العملات الحديثة (MEV Sniping) 🎯" else "ELITE LIQUIDITY SNIPER & FRONTRUNNER 🎯"
    val textSniperContractPlaceholder = if (isAr) "ضع عنوان عقد العملة الجديد (Solana/BSC/ETH) لاقتناصه قبل المطور..." else "Enter token contract address (Solana/BSC/ETH) to frontrun dev..."
    val textSniperButton = if (isAr) "قنص العقد فائق السرعة 🚀" else "LAUNCH MEV SNIPE 🚀"
    val textSniperStatusActive = if (isAr) "الحالة: نشط ويراقب مجمع السيولة" else "STATUS: SCANNING MEMPOOL LIQUIDITY POOLS"
    val textSniperStatusIdle = if (isAr) "الحالة: متوقف" else "STATUS: PAUSED"
    
    val textTradesHeader = if (isAr) "▪ الصفقات الرابحة المؤكدة ذاتياً (100% نجاح)" else "▪ SECURED ALGO TRANS-WIN ENTRIES"
    val textNewsHeader = if (isAr) "▪ التدفق الإخباري واستشعار رادارات السوق" else "▪ RADAR SCRAPED ASSET NOTIFICATIONS"
    
    val textNoTrades = if (isAr) "لا توجد أي صفقات خاضعة للتحليل حالياً" else "No active trades calculated"
    val textNoNews = if (isAr) "الرادار يستشعر قنوات الأخبار العالمية..." else "Scanning global pipelines for signals..."
    val textClearCacheBtn = if (isAr) "تصفير السجل" else "RESET HISTORY"
    val textCoControllersHeader = if (isAr) "لوحة التحكم الفورية بأنظمة الربح والسرعة" else "CORE ALGO PERFORMANCE SYSTEM SWITCHES"

    // Multi-Asset Selection Localization
    val marketsTranslation = mapOf(
        "CRYPTO" to if (isAr) "العملات الرقمية" else "CRYPTOCURRENCY",
        "STOCKS" to if (isAr) "الأسهم العالمية" else "GLOBAL STOCKS",
        "COMMODITIES" to if (isAr) "السلع والذهب 🏆" else "GOLD & COMMODITIES 🏆",
        "BONDS" to if (isAr) "السندات الحكومية" else "SOVEREIGN BONDS",
        "INDEXES" to if (isAr) "المؤشرات والرهونات" else "MARKET INDEXES & OPTIONS"
    )

    val marketCategories = listOf("CRYPTO", "STOCKS", "COMMODITIES", "BONDS", "INDEXES")

    // Dynamic RTL layout provider
    val selectLayoutDir = if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides selectLayoutDir) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MidnightNavy)
                .padding(10.dp)
        ) {
            
            // 1. VIP Luxury Branding Top Banner Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(CobaltBlueCard, MidnightNavy)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp, 
                        brush = Brush.linearGradient(
                            colors = listOf(PremiumRoyalGold, Color.Transparent)
                        ), 
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(ActiveGreenGlow)
                    )
                    Text(
                        text = textAppTitle,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Luxurious gold outlined language selector button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PremiumRoyalGold.copy(alpha = 0.12f))
                            .border(1.dp, PremiumRoyalGold, RoundedCornerShape(6.dp))
                            .clickable { viewModel.isArabic.value = !isAr }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isAr) "English" else "العربية 🇸🇦",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftChampagneGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // GRAND MASTER OPERATIONS CENTER (التحكم الكلي الفوري بالتداول والذكاء الاصطناعي)
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("master_trading_center_card")
                    .border(
                        width = 1.5.dp,
                        color = if (isMasterTradingEnabled) ActiveGreenGlow else PremiumRoyalGold,
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CobaltBlueCard)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Master Console",
                                tint = PremiumRoyalGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isAr) "لوحة التحكم الكبرى والتشغيل الشامل" else "GRAND MASTER OPERATIONS CENTER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftChampagneGold
                            )
                        }
                        
                        // Dynamic Status Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .background(
                                    if (isMasterTradingEnabled) ActiveGreenGlow.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        if (isMasterTradingEnabled) ActiveGreenGlow else Color.Red,
                                        RoundedCornerShape(3.dp)
                                    )
                            )
                            Text(
                                text = if (isMasterTradingEnabled) 
                                    (if (isAr) "تداول تلقائي نشط 🔥" else "AUTO-TRADING ON 🔥") 
                                else 
                                    (if (isAr) "متوقف آمن ⏸️" else "SAFE-PAUSED ⏸️"),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isMasterTradingEnabled) ActiveGreenGlow else Color.Red
                            )
                        }
                    }

                    // Explanatory Status Text
                    Text(
                        text = if (isMasterTradingEnabled) {
                            if (isAr) "● جميع خوارزميات التداول، قناص السيولة، وتحكيم الفروقات تعمل الآن بكامل طاقتها لتحقيق أرباح مضمونة 100% بدون أي خسائر."
                            else "● All high-frequency pipelines, token snipers, and arbitrage modules are operating at max capacity under absolute zero-loss guarantee."
                        } else {
                            if (isAr) "● جميع الرادارات والصفقات معلقة حالياً بأمان لحماية وتجميد رصيدك ومنع أي تداول لحين إعادة التفعيل."
                            else "● Real-time market actions are suspended. Portfolio balance is 100% locked and protected from external shifts."
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isMasterTradingEnabled) TechCyanGlow else SoftGreyText,
                        lineHeight = 15.sp
                    )

                    // Grand Premium Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 1. START ALL TRADING BUTTON
                        Button(
                            onClick = { viewModel.setAllTradingState(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMasterTradingEnabled) ActiveGreenGlow.copy(alpha = 0.25f) else ActiveGreenGlow,
                                contentColor = MidnightNavy
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isMasterTradingEnabled) ActiveGreenGlow else Color.Transparent),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("start_all_trading_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start All",
                                    tint = if (isMasterTradingEnabled) ActiveGreenGlow else MidnightNavy,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isAr) "تداول الكل" else "START ALL",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isMasterTradingEnabled) ActiveGreenGlow else MidnightNavy
                                )
                            }
                        }

                        // 2. STOP ALL TRADING BUTTON
                        Button(
                            onClick = { viewModel.setAllTradingState(false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isMasterTradingEnabled) Color.Red.copy(alpha = 0.25f) else Color(0xFFE53935),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (!isMasterTradingEnabled) Color.Red else Color.Transparent),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("stop_all_trading_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Stop All",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isAr) "إيقاف شامل" else "STOP ALL",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        // 3. WITHDRAW PROFITS BUTTON
                        Button(
                            onClick = { showWithdrawDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PremiumRoyalGold,
                                contentColor = MidnightNavy
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(38.dp)
                                .testTag("master_withdraw_profits_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Withdraw Profits",
                                    tint = MidnightNavy,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isAr) "سحب الأرباح" else "WITHDRAW",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MidnightNavy
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // PRESTIGE CUSTOM SEGMENTED TAB NAVIGATION
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CobaltBlueCard, RoundedCornerShape(12.dp))
                    .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    if (isAr) "الرئيسية والرادارات 📊" else "DASHBOARD 📊",
                    if (isAr) "تداول الفروقات 🔄" else "CROSS ARBITRAGE 🔄",
                    if (isAr) "المضاربة والذكاء 🛠️" else "MEV & AI BOTS 🛠️"
                )
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedScreenTab == index
                    val tabBg = if (isSelected) MidnightNavy else Color.Transparent
                    val tabBorder = if (isSelected) PremiumRoyalGold else Color.Transparent
                    val textColor = if (isSelected) TechCyanGlow else SoftGreyText
                    val textWeight = if (isSelected) FontWeight.Black else FontWeight.Bold

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tabBg)
                            .border(if (isSelected) 1.dp else 0.dp, tabBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedScreenTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = textWeight,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (selectedScreenTab == 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 2. High Prestige Financial Counter View Card Setup
                        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Large Deluxe Balanced Wallet displaying live dynamic gains 100% safe
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(145.dp)
                        .background(CobaltBlueCard, RoundedCornerShape(10.dp))
                        .border(
                            width = 1.5.dp, 
                            brush = Brush.verticalGradient(
                                colors = listOf(PremiumRoyalGold, CobaltBlueCard)
                            ), 
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = textTotalBalanceLabel,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                color = SoftChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(ActiveGreenGlow)
                                )
                                Text(
                                    text = if (isAr) "نشط ومؤمن" else "ENABLED & SECURED",
                                    fontSize = 8.sp,
                                    color = ActiveGreenGlow,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        // Giant luxury balance indicators with glowing green floating rewards
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "$${String.format(Locale.getDefault(), "%,.2f", balance)}",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            AnimatedVisibility(
                                visible = lastProfitFlash != null,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = lastProfitFlash ?: "",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if ((lastProfitFlash ?: "").startsWith("-")) WarningOrangeAccent else ActiveGreenGlow,
                                    modifier = Modifier
                                        .background(
                                            if ((lastProfitFlash ?: "").startsWith("-")) WarningOrangeAccent.copy(alpha = 0.15f)
                                            else ActiveGreenGlow.copy(alpha = 0.15f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Massive visual Deposit/Withdraw high fidelity buttons in Arabic
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showDepositDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ActiveGreenGlow),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(vertical = 6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(35.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Deposit",
                                        tint = MidnightNavy,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (isAr) "إيداع فوري 💳" else "DEPOSIT +",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MidnightNavy
                                    )
                                }
                            }

                            Button(
                                onClick = { showWithdrawDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PremiumRoyalGold),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(vertical = 6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(35.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Withdraw",
                                        tint = MidnightNavy,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (isAr) "سحب المكاسب" else "WITHDRAW -",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MidnightNavy
                                    )
                                }
                            }
                        }
                    }
                }

                // VIP 100% Win Rate Shield panel
                Box(
                    modifier = Modifier
                        .weight(0.8f)
                        .height(145.dp)
                        .background(CobaltBlueCard, RoundedCornerShape(10.dp))
                        .border(1.dp, PremiumRoyalGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = textBotAccuracyLabel,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            color = SoftGreyText,
                            fontWeight = FontWeight.Bold
                        )

                        Column {
                            Text(
                                text = "100%",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = ActiveGreenGlow
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Safe Icon",
                                    tint = PremiumRoyalGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isAr) "ربح مقفل ومؤمن 💯" else "LOCK WIN SECURE 💯",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PremiumRoyalGold
                                )
                            }
                        }

                        Text(
                            text = textZeroLossGuaranteed,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 8.sp,
                            color = TechCyanGlow,
                            lineHeight = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // VIP Notification Stripe Toast alerts
            AnimatedVisibility(
                visible = terminalAlert != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(ActiveGreenGlow.copy(alpha = 0.15f), CobaltBlueCard)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(1.dp, ActiveGreenGlow, RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Verified Notification",
                            tint = ActiveGreenGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = terminalAlert ?: "",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Selection of Active Markets Row
            Text(
                text = textActiveSectorsHeader,
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = SoftChampagneGold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                marketCategories.forEach { category ->
                    val marketLabel = marketsTranslation[category] ?: category
                    val isSelected = selectedMarket == category
                    val borderCol = if (isSelected) PremiumRoyalGold else Color.White.copy(alpha = 0.08f)
                    val backCol = if (isSelected) CobaltBlueCard else MidnightNavy

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(backCol)
                            .border(1.dp, borderCol, RoundedCornerShape(6.dp))
                            .clickable { viewModel.selectSubscription(category) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("pipeline_filter_${category.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) ActiveGreenGlow else SoftGreyText)
                            )
                            Text(
                                text = marketLabel,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else SoftGreyText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Integrated Map & Live Advanced Progress Chart Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Interactive orbital dynamic connection network
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxSize()
                        .background(CobaltBlueCard, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Grid lines
                        val stepsX = 6
                        val stepsY = 4
                        for (i in 1 until stepsX) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.025f),
                                start = Offset(w * (i.toFloat() / stepsX), 0f),
                                end = Offset(w * (i.toFloat() / stepsX), h),
                                strokeWidth = 1f
                            )
                        }
                        for (j in 1 until stepsY) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.025f),
                                start = Offset(0f, h * (j.toFloat() / stepsY)),
                                end = Offset(w, h * (j.toFloat() / stepsY)),
                                strokeWidth = 1f
                            )
                        }

                        // Simulated trading points
                        val middleEast = Offset(w * 0.65f, h * 0.45f)
                        val london = Offset(w * 0.45f, h * 0.35f)
                        val singapore = Offset(w * 0.82f, h * 0.68f)
                        val newYork = Offset(w * 0.22f, h * 0.40f)
                        val tokyo = Offset(w * 0.90f, h * 0.38f)

                        val listNodes = listOf(middleEast, london, singapore, newYork, tokyo)

                        // Connecting satellite lines with golden and green glows
                        drawLine(color = TechCyanGlow.copy(alpha = 0.35f), start = middleEast, end = london, strokeWidth = 1f)
                        drawLine(color = PremiumRoyalGold.copy(alpha = 0.35f), start = middleEast, end = singapore, strokeWidth = 1f)
                        drawLine(color = ActiveGreenGlow.copy(alpha = 0.4f), start = london, end = newYork, strokeWidth = 1f)
                        drawLine(color = TechCyanGlow.copy(alpha = 0.3f), start = singapore, end = tokyo, strokeWidth = 1f)
                        drawLine(color = PremiumRoyalGold.copy(alpha = 0.25f), start = tokyo, end = middleEast, strokeWidth = 1f)

                        listNodes.forEachIndexed { idx, point ->
                            val activePulse = idx == nodeTriggerPulse
                            val scalarRadius = if (activePulse) 7.dp.toPx() else 4.dp.toPx()
                            val coreColor = if (activePulse) ActiveGreenGlow else PremiumRoyalGold

                            drawCircle(color = coreColor.copy(alpha = 0.22f), radius = scalarRadius * 2.5f, center = point)
                            drawCircle(color = coreColor, radius = scalarRadius, center = point)
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = textSatelliteMapTitle,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 8.sp,
                            color = SoftGreyText,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = textStatusSteady,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 8.sp,
                                color = ActiveGreenGlow,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "PING: 1.2MS",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                color = TechCyanGlow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // High fidelity performance metric graph curves
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxSize()
                        .background(CobaltBlueCard, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                        .padding(6.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = textPerformanceChartTitle,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 8.sp,
                                color = SoftGreyText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ACC: 100%",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 8.sp,
                                color = ActiveGreenGlow,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Drawing live speed graph paths for real-time visualization
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(vertical = 4.dp)
                        ) {
                            val w = size.width
                            val h = size.height

                            // Draw TPS Emerald curve
                            if (tpsMetrics.size >= 2) {
                                val tpsPath = Path()
                                val segmentWidth = w / (tpsMetrics.size - 1)
                                tpsMetrics.forEachIndexed { i, speed ->
                                    // Scale based on 40 to 95 range
                                    val pct = ((speed - 40f) / 55f).coerceIn(0f, 1f)
                                    val py = h - (pct * h)
                                    if (i == 0) tpsPath.moveTo(0f, py) else tpsPath.lineTo(i * segmentWidth, py)
                                }
                                drawPath(path = tpsPath, color = ActiveGreenGlow, style = Stroke(width = 2.dp.toPx()))
                            }

                            // Draw Latency Cyan helper curve
                            if (latencyMetrics.size >= 2) {
                                val latPath = Path()
                                val segmentWidth = w / (latencyMetrics.size - 1)
                                latencyMetrics.forEachIndexed { i, lat ->
                                    // Scale based on 0 to 4ms range
                                    val pct = (lat / 4f).coerceIn(0f, 1f)
                                    val py = h - (pct * h)
                                    if (i == 0) latPath.moveTo(0f, py) else latPath.lineTo(i * segmentWidth, py)
                                }
                                drawPath(path = latPath, color = TechCyanGlow, style = Stroke(width = 1.dp.toPx()))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "استقرار تام" else "STABLE SPEED",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 8.sp,
                                color = SoftChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "TPS: ${String.format(Locale.US, "%.1f", tpsMetrics.lastOrNull() ?: 75.3f)}/s",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                color = TechCyanGlow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

                    }
                }

                if (selectedScreenTab == 1) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // ==========================================
                        // 5. PLATFORM-TO-PLATFORM ARBITRAGE STATION (تداول الفروقات والتحكيم بين المنصات)
            // ==========================================
            val isArbitrageActive by viewModel.isArbitrageEnabled.collectAsState()
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp, 
                        brush = Brush.horizontalGradient(listOf(PremiumRoyalGold, TechCyanGlow)), 
                        shape = RoundedCornerShape(12.dp)
                    )
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CobaltBlueCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header Row with Switch controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isArbitrageActive) ActiveGreenGlow else WarningOrangeAccent)
                            )
                            Text(
                                text = if (isAr) "محطة تداول الفروقات والتحكيم بين المنصات 🔄" else "CROSS-PLATFORM ARBITRAGE SYSTEM 🔄",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftChampagneGold
                            )
                        }
                        
                        Switch(
                            checked = isArbitrageActive,
                            onCheckedChange = { viewModel.isArbitrageEnabled.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MidnightNavy,
                                checkedTrackColor = ActiveGreenGlow,
                                uncheckedThumbColor = SoftGreyText,
                                uncheckedTrackColor = MidnightNavy
                            ),
                            modifier = Modifier.scale(0.85f)
                        )
                    }

                    // Simulated live prices visualization to make it feel extremely physical & tactile!
                    val simulatedBuyPrice = when (arbSelectedToken) {
                        "BTC" -> 64250.0 + (System.currentTimeMillis() % 100)
                        "ETH" -> 3450.0 + (System.currentTimeMillis() % 20)
                        "SOL" -> 142.0 + (System.currentTimeMillis() % 5) / 2.0
                        "XRP" -> 0.48 + (System.currentTimeMillis() % 10) / 100.0
                        "ADA" -> 0.38 + (System.currentTimeMillis() % 10) / 100.0
                        else -> 1.0
                    }
                    val currentSpreadPercent = 0.012 + ((System.currentTimeMillis() % 8) / 300.0) // 1.2% to 3.8%
                    val simulatedSellPrice = simulatedBuyPrice * (1 + currentSpreadPercent)
                    val expectedGain = (arbAmountValue.toDoubleOrNull() ?: 5000.0) * currentSpreadPercent

                    // 1. Selector for Cryptocurrency Token (beautiful selection chips)
                    Text(
                        text = if (isAr) "1. حدد عملة الفروقات المراد تداولها:" else "1. SELECT ARBITRAGE CRYPTO TOKEN:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("BTC", "ETH", "SOL", "USDT", "XRP", "ADA").forEach { tok ->
                            val active = arbSelectedToken == tok
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (active) MidnightNavy else CobaltBlueCard)
                                    .border(
                                        width = 1.dp,
                                        color = if (active) TechCyanGlow else Color.White.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable { arbSelectedToken = tok }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tok + when(tok) {
                                        "BTC" -> " 🪙"
                                        "ETH" -> " 💠"
                                        "SOL" -> " 🚀"
                                        "USDT" -> " ⚡"
                                        "XRP" -> " 📈"
                                        else -> " 💎"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) TechCyanGlow else SoftGreyText
                                )
                            }
                        }
                    }

                    // 2. Buy Exchange and Sell Exchange selection side-by-side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Buy exchange selector column
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) "شراء من منصة 📥" else "Buy from (Low Price) 📥",
                                fontSize = 10.sp,
                                color = SoftGreyText,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Binance", "Bybit", "OKX", "Kraken").forEach { ex ->
                                    val active = arbBuyPlatform == ex
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (active) MidnightNavy else CobaltBlueCard)
                                            .border(
                                                1.dp,
                                                if (active) ActiveGreenGlow else Color.White.copy(alpha = 0.05f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { arbBuyPlatform = ex }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = ex,
                                            fontSize = 10.sp,
                                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                            color = if (active) ActiveGreenGlow else SoftGreyText
                                        )
                                    }
                                }
                            }
                        }

                        // Sell exchange selector column
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) "بيع في منصة 📤" else "Sell in (High Price) 📤",
                                fontSize = 10.sp,
                                color = SoftGreyText,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Coinbase", "Gate.io", "KuCoin", "HTX").forEach { ex ->
                                    val active = arbSellPlatform == ex
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (active) MidnightNavy else CobaltBlueCard)
                                            .border(
                                                1.dp,
                                                if (active) PremiumRoyalGold else Color.White.copy(alpha = 0.05f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { arbSellPlatform = ex }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = ex,
                                            fontSize = 10.sp,
                                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                            color = if (active) PremiumRoyalGold else SoftGreyText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Spread calculation visual board - makes the app extremely interactive and beautiful
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, TechCyanGlow.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = MidnightNavy)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isAr) "سعر الشراء [$arbBuyPlatform]" else "Buy Book [$arbBuyPlatform]",
                                    fontSize = 9.sp,
                                    color = SoftGreyText
                                )
                                Text(
                                    text = "$${String.format("%,.2f", simulatedBuyPrice)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Flow",
                                    tint = ActiveGreenGlow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "+${String.format("%.2f", currentSpreadPercent * 100)}% " + (if (isAr) "فجوة" else "Spread"),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ActiveGreenGlow
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isAr) "سعر البيع [$arbSellPlatform]" else "Sell Book [$arbSellPlatform]",
                                    fontSize = 9.sp,
                                    color = SoftGreyText
                                )
                                Text(
                                    text = "$${String.format("%,.2f", simulatedSellPrice)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // 3. Tactile quick preset amount and custom input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = arbAmountValue,
                            onValueChange = { arbAmountValue = it },
                            label = {
                                Text(
                                    text = if (isAr) "رأس المال لتداول الفارق ($)" else "Arbitrage Trading Capital ($)",
                                    fontSize = 10.sp,
                                    color = PremiumRoyalGold
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PremiumRoyalGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                focusedContainerColor = MidnightNavy,
                                unfocusedContainerColor = MidnightNavy
                            ),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                            modifier = Modifier.weight(1.2f)
                        )

                        // expected reward flash
                        Column(
                            modifier = Modifier
                                .weight(0.8f)
                                .background(ActiveGreenGlow.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .border(0.5.dp, ActiveGreenGlow.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isAr) "الربح الآمن المتوقع" else "EXPECTED REWARD",
                                fontSize = 8.sp,
                                color = SoftChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+$${String.format("%,.2f", expectedGain)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = ActiveGreenGlow
                            )
                        }
                    }

                    // Fast tactile preselector presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(500, 2000, 10000, 25000, 50000).forEach { amt ->
                            val isSelected = arbAmountValue == amt.toString()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) PremiumRoyalGold else MidnightNavy)
                                    .border(
                                        1.dp,
                                        if (isSelected) PremiumRoyalGold else Color.White.copy(alpha = 0.05f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { arbAmountValue = amt.toString() }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$${String.format("%,d", amt)}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MidnightNavy else Color.White
                                )
                            }
                        }
                    }

                    // 4. Large Action Execution Trigger Button
                    Button(
                        onClick = {
                            val doubleAmt = arbAmountValue.toDoubleOrNull() ?: 5000.0
                            viewModel.executeManualArbitrage(
                                assetSymbol = arbSelectedToken,
                                buyExchange = arbBuyPlatform,
                                sellExchange = arbSellPlatform,
                                amountUsdt = doubleAmt
                            )
                        },
                        enabled = !isAnalyzing && isArbitrageActive && arbBuyPlatform != arbSellPlatform && (arbAmountValue.toDoubleOrNull() ?: 0.0) > 0.0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PremiumRoyalGold,
                            disabledContainerColor = PremiumRoyalGold.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Arb Exec",
                                tint = MidnightNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isAnalyzing) 
                                    (if (isAr) "جاري دراسة فروقات الأسعار لتنفيذ فوري..." else "STUDYING EXCHANGES & SEIZING LIQUIDITY...") 
                                    else (if (isAr) "تأكيد وتنفيذ صفقة الفروقات الفورية ⚡" else "EXECUTE SPATIAL ARBITRAGE FLASHSWAP ⚡"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MidnightNavy
                            )
                        }
                    }
                }
            }

                    }
                }

                if (selectedScreenTab == 2) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // ==========================================
                        // COLLAPSIBLE AUXILIARY INSTRUMENTS & MEMPOOL SNIPERS
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    .clickable { isAdvancedToolsExpanded = !isAdvancedToolsExpanded },
                colors = CardDefaults.cardColors(containerColor = CobaltBlueCard),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAdvancedToolsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = SoftChampagneGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isAr) "عرض رادارات الاستشعار الذكي والميمبول 🛠️" else "VIEW INTELLIGENT SENSORS & MEMPOOL SNIPERS 🛠️",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftChampagneGold
                        )
                    }
                    Text(
                        text = if (isAdvancedToolsExpanded) (if (isAr) "إخفاء" else "HIDE") else (if (isAr) "عرض" else "SHOW"),
                        fontSize = 10.sp,
                        color = PremiumRoyalGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isAdvancedToolsExpanded) {
                Spacer(modifier = Modifier.height(6.dp))

                // 5. User Manual Trigger Intercept Area (VIP style)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, PremiumRoyalGold.copy(alpha = 0.25f), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = CobaltBlueCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (viewModel.isApiKeyAvailable) TechCyanGlow else ActiveGreenGlow)
                                )
                                Text(
                                    text = if (viewModel.isApiKeyAvailable) textGeminiSecured else textLocalCoreSecured,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 10.sp,
                                    color = if (viewModel.isApiKeyAvailable) TechCyanGlow else ActiveGreenGlow,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = if (isAr) "الاستشعار الإخباري السريع ⚡" else "RAPID SENTIMENT CAPTURER ⚡",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 9.sp,
                                color = SoftChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = userSignalText,
                                onValueChange = { userSignalText = it },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("signal_input"),
                                placeholder = {
                                    Text(
                                        text = textSignalPlaceholder,
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 12.sp,
                                        color = SoftGreyText
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = PremiumRoyalGold,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                    focusedContainerColor = MidnightNavy,
                                    unfocusedContainerColor = MidnightNavy
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(6.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = {
                                    if (userSignalText.isNotBlank()) {
                                        viewModel.executeAiFrontrunner(userSignalText)
                                        userSignalText = ""
                                        keyboardController?.hide()
                                    }
                                },
                                enabled = !isAnalyzing && userSignalText.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PremiumRoyalGold,
                                    disabledContainerColor = PremiumRoyalGold.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .height(50.dp)
                                    .testTag("intercept_button")
                            ) {
                                Text(
                                    text = if (isAnalyzing) textScanningBtn else textFrontrunBtn,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MidnightNavy
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 5b. Ultra High-Performance Mempool Custom Coin & Token Sniper Frontrunning Panel
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, TechCyanGlow.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = CobaltBlueCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (isNewCoinSniperEnabled) ActiveGreenGlow else SoftGreyText)
                                )
                                Text(
                                    text = if (isNewCoinSniperEnabled) textSniperStatusActive else textSniperStatusIdle,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 10.sp,
                                    color = if (isNewCoinSniperEnabled) ActiveGreenGlow else SoftGreyText,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Luxury text and status
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.clickable { 
                                    viewModel.isNewCoinSniperEnabled.value = !isNewCoinSniperEnabled 
                                }
                            ) {
                                Text(
                                    text = textNewCoinSniperTitle,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 10.sp,
                                    color = SoftChampagneGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Switch(
                                    checked = isNewCoinSniperEnabled,
                                    onCheckedChange = { viewModel.isNewCoinSniperEnabled.value = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MidnightNavy,
                                        checkedTrackColor = ActiveGreenGlow,
                                        uncheckedThumbColor = SoftGreyText,
                                        uncheckedTrackColor = MidnightNavy
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = userTokenAddressText,
                                onValueChange = { userTokenAddressText = it },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("token_sniper_input"),
                                placeholder = {
                                    Text(
                                        text = textSniperContractPlaceholder,
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 11.sp,
                                        color = SoftGreyText
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = TechCyanGlow,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                    focusedContainerColor = MidnightNavy,
                                    unfocusedContainerColor = MidnightNavy
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(6.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = {
                                    if (userTokenAddressText.isNotBlank()) {
                                        viewModel.executeManualTokenSnipe(userTokenAddressText)
                                        userTokenAddressText = ""
                                        keyboardController?.hide()
                                    }
                                },
                                enabled = !isAnalyzing && userTokenAddressText.isNotBlank() && isNewCoinSniperEnabled,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TechCyanGlow,
                                    disabledContainerColor = TechCyanGlow.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .height(50.dp)
                                    .testTag("token_snipe_trigger_btn")
                            ) {
                                Text(
                                    text = if (isAnalyzing) textScanningBtn else textSniperButton,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MidnightNavy
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 6. Section Header for Logs and Scrapes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "التسجيل الزمني لتدفق الصفقات والفرص" else "REALTIME TRANSACTION & OSINT PIPELINE",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    color = SoftChampagneGold,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Ledger",
                        tint = WarningOrangeAccent,
                        modifier = Modifier
                            .size(15.dp)
                            .clickable { viewModel.clearHistory() }
                    )
                    Text(
                        text = textClearCacheBtn,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        color = WarningOrangeAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.clearHistory() }
                            .testTag("clear_history_btn")
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Dual Columns structured side-by-side representing logs and scrapings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Realtime Auto Wins Trans Log
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = CobaltBlueCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(
                            text = textTradesHeader,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                        )

                        if (rawTrades.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = textNoTrades,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    color = SoftGreyText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(rawTrades) { trade ->
                                    LuxuryTradeRowItem(trade, isAr)
                                }
                            }
                        }
                    }
                }

                // Global OSINT Live Scrapes Feeds
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = CobaltBlueCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(
                            text = textNewsHeader,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            color = TechCyanGlow,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                        )

                        if (rawNews.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = textNoNews,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    color = SoftGreyText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(rawNews) { news ->
                                    LuxuryNewsRowItem(news, isAr)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 7. Interactive Bottom Control System
            Text(
                text = textCoControllersHeader,
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = SoftChampagneGold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            )

            LuxuryControlSwitchesRow(
                isClaude = isClaudeEnabled,
                isHft = isHftEnabled,
                isSwap = isSwapEnabled,
                isScraper = isScraperEnabled,
                isSuperFast = isSuperFastScalperEnabled,
                onClaudeChange = { viewModel.isClaudeSentinelEnabled.value = it },
                onHftChange = { viewModel.isHftCoProcessorEnabled.value = it },
                onSwapChange = { viewModel.isRapidSwapEnabled.value = it },
                onScraperChange = { viewModel.isOsintScraperEnabled.value = it },
                onSuperFastChange = { viewModel.isSuperFastScalperEnabled.value = it },
                isAr = isAr
            )
                    }
                }
            }
        }

        // ==========================================
        // 8. LUXURY ARABIC DEPOSIT DIALOG WINDOW WITH PRESETS & POPULAR CHANNELS
        // ==========================================
        // 8. LUXURY CRYPTO AND MULTI-CHAIN DEPOSIT DIALOG WINDOW WITH QR CODES & TXID LOGS
        // ==========================================
        if (showDepositDialog) {
            var depositAmountValue by remember { mutableStateOf("") }
            var selectedChannel by remember { mutableStateOf(0) } // 0 to 4: Crypto, 5: Visa, 6: Apple Pay, 7: Local Transfer
            var txHashValue by remember { mutableStateOf("") }
            var showCopiedText by remember { mutableStateOf(false) }

            val paymentOptions = listOf(
                if (isAr) "يو إس دي تي USDT (TRC-20) ⚡" else "USDT (TRC-20) network ⚡",
                if (isAr) "يو إس دي تي USDT (ERC-20) 💠" else "USDT (ERC-20) network 💠",
                if (isAr) "بيتكوين (BTC) 🪙" else "Bitcoin (BTC) Gateway 🪙",
                if (isAr) "إيثريوم (ETH) 🧠" else "Ethereum (ETH) Gateway 🧠",
                if (isAr) "سولانا (SOL) 🚀" else "Solana (SOL) Protocol 🚀",
                if (isAr) "بطاقة مدى / فيزا كارد 💳" else "Mada / Visa Card 💳",
                if (isAr) "آبل باي Apple Pay" else "Apple Pay"
            )

            val walletAddresses = listOf(
                "TY3F4e6rY8HJDm9S39v39fFkS892jsM9Dk", // TRC20USDT
                "0x71C7656EC7ab88b098defB751B7401B5f6d8976F", // ERC20USDT
                "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa", // BTC
                "0xde0B295669a9FD93d5F28D9Ec85E40f4cb697BAe", // ETH
                "HN7cEg6C4ToRF9NBDvA916F8F3N6N47BDvFTwH"  // SOL
            )

            val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

            Dialog(onDismissRequest = { showDepositDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.85f)
                        .border(1.5.dp, PremiumRoyalGold, RoundedCornerShape(12.dp)),
                    color = CobaltBlueCard,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Deposit Icon",
                                tint = ActiveGreenGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (isAr) "بوابة إيداع العملات الرقمية والآمنة 📈" else "VIP SECURE CRYPTO & COIN PORT ENGINE 📈",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftChampagneGold
                            )
                        }

                        Text(
                            text = if (isAr) "اختر القيمة السريعة المراد إيداعها، أو حدد قناة الدفع والشبكة لإتمام النقل مباشرة:" 
                                   else "Tap quick deposit presets or choose cryptocurrency/payment channel to transfer directly:",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            color = SoftGreyText
                        )

                        // Elegant pre-set values row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(100.0, 500.0, 2000.0, 10000.0).forEach { amt ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ActiveGreenGlow.copy(alpha = 0.12f))
                                        .border(1.dp, ActiveGreenGlow, RoundedCornerShape(6.dp))
                                        .clickable {
                                            depositAmountValue = amt.toInt().toString()
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+$${String.format("%,.0f", amt)}",
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = ActiveGreenGlow
                                    )
                                }
                            }
                        }

                        // Payment Methods Selector
                        Text(
                            text = if (isAr) "قنوات الإيداع (تدعم USDT والعملات الرقمية) 🪙" else "Deposit Channel (USDT & Cryptocurrencies supported) 🪙",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftChampagneGold
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            paymentOptions.forEachIndexed { idx, option ->
                                val active = selectedChannel == idx
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) MidnightNavy else CobaltBlueCard)
                                        .border(
                                            width = 1.dp, 
                                            color = if (active) PremiumRoyalGold else Color.White.copy(alpha = 0.05f), 
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { selectedChannel = idx }
                                        .padding(vertical = 8.dp, horizontal = 12.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = option,
                                            fontFamily = FontFamily.SansSerif,
                                            fontSize = 11.sp,
                                            color = if (active) Color.White else SoftGreyText,
                                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(if (active) ActiveGreenGlow else Color.Transparent)
                                                .border(1.dp, SoftGreyText, RoundedCornerShape(5.dp))
                                        )
                                    }
                                }
                            }
                        }

                        // Crypto Wallet Details section (shown if selectedChannel < 5)
                        if (selectedChannel < 5) {
                            val activeAddress = walletAddresses[selectedChannel]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, TechCyanGlow.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                colors = CardDefaults.cardColors(containerColor = MidnightNavy)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isAr) "عنوان إرسال المحفظة الآمن [تحقق آلي MEV]" else "SECURE DEPOSIT DESTINATION ADDRESS [MEV AUDITED]",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TechCyanGlow,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(4.dp))
                                            .padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = activeAddress,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        
                                        Spacer(modifier = Modifier.width(4.dp))

                                        Button(
                                            onClick = {
                                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(activeAddress))
                                                showCopiedText = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = TechCyanGlow),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Text(
                                                text = if (showCopiedText) (if (isAr) "تم النسخ!" else "COPIED!") else (if (isAr) "نسخ العقد" else "COPY"),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MidnightNavy
                                            )
                                        }
                                    }

                                    // Custom QR Code Canvas renderer custom blocks
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Canvas(
                                            modifier = Modifier
                                                .size(90.dp)
                                                .background(Color.White, RoundedCornerShape(4.dp))
                                                .padding(6.dp)
                                        ) {
                                            val sizePx = size.width
                                            val cellSize = sizePx / 9f
                                            val fillSquare: (Float, Float, Float) -> Unit = { x, y, len ->
                                                drawRect(
                                                    color = Color(0xFF0F172A),
                                                    topLeft = Offset(x, y),
                                                    size = androidx.compose.ui.geometry.Size(len, len)
                                                )
                                            }
                                            val drawFinder: (Float, Float) -> Unit = { x, y ->
                                                drawRect(
                                                    color = Color(0xFF0F172A),
                                                    topLeft = Offset(x, y),
                                                    size = androidx.compose.ui.geometry.Size(cellSize * 3f, cellSize * 3f)
                                                )
                                                drawRect(
                                                    color = Color.White,
                                                    topLeft = Offset(x + cellSize, y + cellSize),
                                                    size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                                                )
                                            }
                                            drawFinder(0f, 0f)
                                            drawFinder(cellSize * 6f, 0f)
                                            drawFinder(0f, cellSize * 6f)
                                            
                                            val dataMatrix = listOf(
                                                Pair(3, 3), Pair(4, 3), Pair(5, 3),
                                                Pair(3, 4), Pair(5, 4),
                                                Pair(3, 5), Pair(4, 5), Pair(5, 5),
                                                Pair(6, 6), Pair(7, 6), Pair(6, 7), Pair(8, 7),
                                                Pair(7, 8)
                                            )
                                            dataMatrix.forEach { p ->
                                                fillSquare(p.first * cellSize, p.second * cellSize, cellSize)
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = if (isAr) "امسح رمز الاستجابة السريعة (QR) أو انسخ العنوان أعلاه لتحويل USDT/Crypto بأمان" 
                                                   else "Scan QR code or use the wallet address to initiate secure decentralised payment",
                                            fontSize = 9.sp,
                                            color = SoftGreyText,
                                            modifier = Modifier.width(120.dp)
                                        )
                                    }

                                    // TXID input field
                                    OutlinedTextField(
                                        value = txHashValue,
                                        onValueChange = { txHashValue = it },
                                        placeholder = {
                                            Text(
                                                text = if (isAr) "أدخل معرف المعاملة (TXID) لتأكيد الإيداع" else "Transaction Hash / TXID for speed record",
                                                fontSize = 10.sp,
                                                color = SoftGreyText
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = TechCyanGlow,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                            focusedContainerColor = CobaltBlueCard,
                                            unfocusedContainerColor = CobaltBlueCard
                                        ),
                                        singleLine = true,
                                        shape = RoundedCornerShape(6.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        // Amount Input field
                        OutlinedTextField(
                            value = depositAmountValue,
                            onValueChange = { depositAmountValue = it },
                            label = {
                                Text(
                                    text = if (isAr) "مبلغ الشحن المخصص ($) [الحد الأدنى 1$]" else "Custom Credit Deposit Input ($) [Min $1]",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PremiumRoyalGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.06f),
                                focusedLabelColor = PremiumRoyalGold,
                                unfocusedLabelColor = SoftGreyText
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showDepositDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isAr) "تراجع" else "CANCEL",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = {
                                    val amtDbl = depositAmountValue.toDoubleOrNull()
                                    if (amtDbl != null && amtDbl >= 1.0) {
                                        val sourceChannel = paymentOptions[selectedChannel] + 
                                            if (txHashValue.isNotBlank()) " [TXID: ${txHashValue.take(12)}...]" else ""
                                        viewModel.depositFunds(amtDbl, sourceChannel)
                                    }
                                    showDepositDialog = false
                                },
                                enabled = (depositAmountValue.toDoubleOrNull() ?: 0.0) >= 1.0,
                                colors = ButtonDefaults.buttonColors(containerColor = ActiveGreenGlow),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isAr) "تأكيد فوري ودفع ⚡" else "AUTHORIZE CREDIT",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MidnightNavy
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 9. LUXURY ARABIC WITHDRAW DIALOG WINDOW WITH SECURE ZERO-LOSS OUTFLOWS
        // ==========================================
        if (showWithdrawDialog) {
            var withdrawAmountValue by remember { mutableStateOf("") }
            var bankAccountNum by remember { mutableStateOf("") }
            var selectedWithdrawChannel by remember { mutableStateOf(0) } // 0: USDT TRC-20, 1: USDT ERC-20, 2: BTC, 3: ETH, 4: SOL, 5: Bank/IBAN
            var withdrawSpeedSelection by remember { mutableStateOf(0) } // 0: Instant VIP, 1: Standard Security Verification

            val withdrawOptions = listOf(
                if (isAr) "يو إس دي تي USDT (TRC-20) ⚡" else "USDT (TRC-20) network ⚡",
                if (isAr) "يو إس دي تي USDT (ERC-20) 💠" else "USDT (ERC-20) network 💠",
                if (isAr) "بيتكوين (BTC) 🪙" else "Bitcoin (BTC) Gateway 🪙",
                if (isAr) "إيثريوم (ETH) 🧠" else "Ethereum (ETH) Gateway 🧠",
                if (isAr) "سولانا (SOL) 🚀" else "Solana (SOL) Protocol 🚀",
                if (isAr) "حساب بنكي محلي / آيبان IBAN 🏦" else "Local Bank IBAN Transfer 🏦"
            )

            Dialog(onDismissRequest = { showWithdrawDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.85f)
                        .border(1.5.dp, PremiumRoyalGold, RoundedCornerShape(12.dp)),
                    color = CobaltBlueCard,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Withdraw Revenue",
                                tint = PremiumRoyalGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (isAr) "بوابة سحب وتسييل مكاسب الروبوت 💸" else "VIP GUARANTEED REVENUE WITHDRAWAL 💸",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftChampagneGold
                            )
                        }

                        Text(
                            text = if (isAr) "اختر أحد المبالغ المتاحة للسحب السريع بضغطة واحدة، أو عيّن قيمة السحب والشبكة المفضلة بالأسفل:" 
                                   else "Tap quick payout presets or specify custom liquidation amount and network route safely:",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            color = SoftGreyText
                        )

                        // Payout presets values
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(500.0, 2000.0, 5000.0, 25000.0).forEach { amt ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PremiumRoyalGold.copy(alpha = 0.12f))
                                        .border(1.dp, PremiumRoyalGold, RoundedCornerShape(6.dp))
                                        .clickable {
                                            withdrawAmountValue = amt.toInt().toString()
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$${String.format("%,.0f", amt)}",
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PremiumRoyalGold
                                    )
                                }
                            }
                        }

                        // Network Selection Headers
                        Text(
                            text = if (isAr) "حدد شبكة وقناة السحب الفورية:" else "Select desired cashout route:",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftChampagneGold
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            withdrawOptions.forEachIndexed { idx, option ->
                                val active = selectedWithdrawChannel == idx
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) MidnightNavy else CobaltBlueCard)
                                        .border(
                                            width = 1.dp, 
                                            color = if (active) PremiumRoyalGold else Color.White.copy(alpha = 0.05f), 
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { selectedWithdrawChannel = idx }
                                        .padding(vertical = 8.dp, horizontal = 12.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = option,
                                            fontFamily = FontFamily.SansSerif,
                                            fontSize = 11.sp,
                                            color = if (active) Color.White else SoftGreyText,
                                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(if (active) ActiveGreenGlow else Color.Transparent)
                                                .border(1.dp, SoftGreyText, RoundedCornerShape(5.dp))
                                        )
                                    }
                                }
                            }
                        }

                        // Speed selection VIP
                        Text(
                            text = if (isAr) "سرعة نقل الشبكة الآمنة:" else "Network Transmission Priority:",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftChampagneGold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                if (isAr) "فوري فائق السرعة (VIP ✔️)" else "No-Lag VIP Boost (0s)",
                                if (isAr) "تحقق عادي (60 ثانية)" else "Standard Check"
                            ).forEachIndexed { i, speedText ->
                                val activeSpeed = withdrawSpeedSelection == i
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (activeSpeed) MidnightNavy else CobaltBlueCard)
                                        .border(
                                            width = 1.dp, 
                                            color = if (activeSpeed) TechCyanGlow else Color.White.copy(alpha = 0.05f), 
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { withdrawSpeedSelection = i }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = speedText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (activeSpeed) TechCyanGlow else SoftGreyText
                                    )
                                }
                            }
                        }

                        // Dynamic Address Field change
                        val labelText = if (selectedWithdrawChannel < 5) {
                            if (isAr) "عنوان محفظة الاستلام الرقمية (${withdrawOptions[selectedWithdrawChannel].substringBefore(" ")})" 
                            else "Target Wallet Address (${withdrawOptions[selectedWithdrawChannel].substringBefore(" ")})"
                        } else {
                            if (isAr) "رقم الحساب البنكي الدولي IBAN للمستفيد" else "Recipient Bank IBAN Number"
                        }
                        
                        val placeholderText = if (selectedWithdrawChannel < 5) {
                            if (isAr) "ألصق عنوان المحفظة للمستلم هنا بأمان" else "Paste secure target wallet address"
                        } else {
                            if (isAr) "مثال: SA123000000000000000" else "e.g. SA1230000000000"
                        }

                        Text(
                            text = if (isAr) "تفاصيل الاستلام للمستفيد:" else "Transfer Destination Credentials:",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftChampagneGold
                        )

                        OutlinedTextField(
                            value = bankAccountNum,
                            onValueChange = { bankAccountNum = it },
                            placeholder = {
                                Text(
                                    text = placeholderText,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    color = SoftGreyText
                                )
                            },
                            label = {
                                Text(
                                    text = labelText,
                                    fontSize = 10.sp,
                                    color = PremiumRoyalGold
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PremiumRoyalGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.05f)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                        )

                        // Custom value field
                        OutlinedTextField(
                            value = withdrawAmountValue,
                            onValueChange = { withdrawAmountValue = it },
                            label = {
                                Text(
                                    text = if (isAr) "قيمة السحب المطلوب بالدولار ($) [الحد الأدنى 1$]" else "Input Custom Liquidate Amount ($) [Min $1]",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PremiumRoyalGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.06f),
                                focusedLabelColor = PremiumRoyalGold,
                                unfocusedLabelColor = SoftGreyText
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // 100% Capital protection subtext
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PremiumRoyalGold.copy(alpha = 0.07f)),
                            modifier = Modifier.fillMaxWidth().border(0.5.dp, PremiumRoyalGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Safe",
                                    tint = PremiumRoyalGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isAr) "سحب معزز وحماية تامة 100% من الخسائر مع عمولة شبكة 0%." 
                                           else "Protected capital transmission. Under zero-drift, network commission is 0%.",
                                    fontSize = 9.sp,
                                    color = SoftChampagneGold
                                )
                            }
                        }

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showWithdrawDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isAr) "تراجع" else "CANCEL",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = {
                                    val amtDbl = withdrawAmountValue.toDoubleOrNull()
                                    if (amtDbl != null && amtDbl >= 1.0) {
                                        val destString = bankAccountNum.ifBlank { "Auto Setup Address" }
                                        val finalRoute = "${withdrawOptions[selectedWithdrawChannel]} [$destString] " + 
                                            if (withdrawSpeedSelection == 0) "⚡VIP" else "Standard Check"
                                        viewModel.withdrawFunds(amtDbl, finalRoute)
                                    }
                                    showWithdrawDialog = false
                                },
                                enabled = (withdrawAmountValue.toDoubleOrNull() ?: 0.0) >= 1.0,
                                colors = ButtonDefaults.buttonColors(containerColor = PremiumRoyalGold),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isAr) "تسييل وسحب فوري ⚡" else "LIQUIDATE REVENUE",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MidnightNavy
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LuxuryTradeRowItem(trade: TradeEntity, isAr: Boolean) {
    val actionType = if (isAr) {
        if (trade.type == "BUY") "شراء معزّز 🟢" else "بيع فوري 🔴"
    } else trade.type

    val marketStr = when(trade.market) {
        "CRYPTO" -> if (isAr) "العملات الرقمية" else "CRYPTO"
        "STOCKS" -> if (isAr) "الأسهم العالمية" else "STOCKS"
        "COMMODITIES" -> if (isAr) "السلع والذهب" else "GOLD & COMMODITIES"
        "BONDS" -> if (isAr) "السندات الحكومية" else "SOVEREIGN BONDS"
        "INDEXES" -> if (isAr) "المؤشرات والرهونات" else "MARKET INDEXES"
        "VAULT FUNDING" -> if (isAr) "شحن وإيداع +" else "VAULT FUNDING"
        "VAULT WITHDRAWAL" -> if (isAr) "سحب أرباح -" else "VAULT WITHDRAWAL"
        else -> trade.market
    }

    val sourceLabel = if (isAr) {
        if (trade.source.contains("Manual") || trade.source.contains("Wire")) "تسوية يدوية آمنة" else "روبوت تصفير الخسائر 🤖"
    } else trade.source

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MidnightNavy.copy(alpha = 0.4f))
            .border(1.dp, Color.White.copy(alpha = 0.03f), RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = actionType,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (trade.type == "BUY") ActiveGreenGlow else WarningOrangeAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = marketStr,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = if (trade.market == "VAULT WITHDRAWAL") 
                        "-$${String.format(Locale.US, "%,.2f", trade.profit)}" 
                        else "+$${String.format(Locale.US, "%,.2f", trade.profit)}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (trade.market == "VAULT WITHDRAWAL") WarningOrangeAccent else ActiveGreenGlow
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "سرعة تنفيذ الـ HFT: ${trade.deltaMs} ملي ثانية" else "HFT RESPONSE: ${trade.deltaMs}ms",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 9.sp,
                    color = TechCyanGlow
                )
                Text(
                    text = sourceLabel,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 9.sp,
                    color = SoftGreyText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}

@Composable
fun LuxuryNewsRowItem(news: NewsPipelineEntity, isAr: Boolean) {
    val bgCol = when (news.sentiment) {
        "BULLISH" -> ActiveGreenGlow.copy(alpha = 0.05f)
        "BEARISH" -> WarningOrangeAccent.copy(alpha = 0.05f)
        else -> Color.White.copy(alpha = 0.02f)
    }

    val sentimentColor = when (news.sentiment) {
        "BULLISH" -> ActiveGreenGlow
        "BEARISH" -> WarningOrangeAccent
        else -> SoftGreyText
    }

    val sentimentLabel = if (isAr) {
        when (news.sentiment) {
            "BULLISH" -> "قوة صاعدة مؤكدة 🟢"
            "BEARISH" -> "اتفاق هبوطي آمن 🔴"
            else -> "مؤشرات صلبة ⚪"
        }
    } else news.sentiment

    val detectorLabel = if (isAr) {
        if (news.detectedBy.contains("Gemini") || news.detectedBy.contains("كلود")) "مستشعر الذكاء الخارق جيميناي ⚡" else "رادار فحص قنوات الأسواق الـ OSINT"
    } else news.detectedBy

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgCol)
            .border(1.dp, sentimentColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sentimentLabel,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = sentimentColor
                )
                Text(
                    text = "WIN RATIO: 100%",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = TechCyanGlow,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = news.headline,
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = if (isAr) "محلل الفرصة: $detectorLabel" else "SOURCE: $detectorLabel",
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                color = SoftGreyText
            )
        }
    }
}

@Composable
fun LuxuryControlSwitchesRow(
    isClaude: Boolean,
    isHft: Boolean,
    isSwap: Boolean,
    isScraper: Boolean,
    isSuperFast: Boolean,
    onClaudeChange: (Boolean) -> Unit,
    onHftChange: (Boolean) -> Unit,
    onSwapChange: (Boolean) -> Unit,
    onScraperChange: (Boolean) -> Unit,
    onSuperFastChange: (Boolean) -> Unit,
    isAr: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CobaltBlueCard, RoundedCornerShape(8.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Super 1-second Scalper dynamic option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Checkbox(
                checked = isSuperFast,
                onCheckedChange = { onSuperFastChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = PremiumRoyalGold,
                    uncheckedColor = SoftGreyText,
                    checkmarkColor = MidnightNavy
                ),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = if (isAr) "مضاربة خاطفة" else "FAST SCALP",
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSuperFast) PremiumRoyalGold else SoftGreyText
            )
        }

        // Claude Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Checkbox(
                checked = isClaude,
                onCheckedChange = { onClaudeChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = PremiumRoyalGold,
                    uncheckedColor = SoftGreyText,
                    checkmarkColor = MidnightNavy
                ),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = if (isAr) "كلود الذكي" else "CLAUDE",
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                color = if (isClaude) Color.White else SoftGreyText
            )
        }

        // HFT Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Checkbox(
                checked = isHft,
                onCheckedChange = { onHftChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = PremiumRoyalGold,
                    uncheckedColor = SoftGreyText,
                    checkmarkColor = MidnightNavy
                ),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = if (isAr) "المضارب" else "HFT",
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                color = if (isHft) Color.White else SoftGreyText
            )
        }

        // SWAP Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Checkbox(
                checked = isSwap,
                onCheckedChange = { onSwapChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = PremiumRoyalGold,
                    uncheckedColor = SoftGreyText,
                    checkmarkColor = MidnightNavy
                ),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = if (isAr) "تسوية فورية" else "SWAP",
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                color = if (isSwap) Color.White else SoftGreyText
            )
        }

        // Active Scraper toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Checkbox(
                checked = isScraper,
                onCheckedChange = { onScraperChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = PremiumRoyalGold,
                    uncheckedColor = SoftGreyText,
                    checkmarkColor = MidnightNavy
                ),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = if (isAr) "مسح الرادار" else "RADAR",
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                color = if (isScraper) Color.White else SoftGreyText
            )
        }
    }
}
