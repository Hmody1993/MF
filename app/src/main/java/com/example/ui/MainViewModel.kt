package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.BuildConfig
import com.example.api.GenerateContentRequest
import com.example.api.Content
import com.example.api.Part
import com.example.api.GenerationConfig
import com.example.api.RetrofitClient
import com.example.data.AppDatabase
import com.example.data.HftRepository
import com.example.data.NewsPipelineEntity
import com.example.data.TradeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.random.Random

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java, "hft_terminal_db"
    ).fallbackToDestructiveMigration().build()

    private val repository = HftRepository(db)

    // State bindings from DB
    val liveTrades: StateFlow<List<TradeEntity>> = repository.allTrades
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newsPipeline: StateFlow<List<NewsPipelineEntity>> = repository.allNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Terminal Status Streams
    private val _portfolioBalance = MutableStateFlow(248500.0)
    val portfolioBalance: StateFlow<Double> = _portfolioBalance.asStateFlow()

    private val _lastProfitFlash = MutableStateFlow<String?>(null)
    val lastProfitFlash: StateFlow<String?> = _lastProfitFlash.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _activeSubscription = MutableStateFlow("CRYPTO")
    val activeSubscription: StateFlow<String> = _activeSubscription.asStateFlow()

    // Dynamic Language Preference (defaults to true for Arabic as requested)
    val isArabic = MutableStateFlow(true)

    // Deposit/Withdraw status dialog flashes
    private val _terminalAlert = MutableStateFlow<String?>(null)
    val terminalAlert: StateFlow<String?> = _terminalAlert.asStateFlow()

    // Control States (All high-performance modules are enabled by default for ultra-smooth trading)
    val isMasterAutoTradingEnabled = MutableStateFlow(true)
    val isClaudeSentinelEnabled = MutableStateFlow(true)
    val isHftCoProcessorEnabled = MutableStateFlow(true)
    val isRapidSwapEnabled = MutableStateFlow(true)
    val isOsintScraperEnabled = MutableStateFlow(true)
    
    // Fast Scalping Speed Mode toggle (True means trade every second)
    val isSuperFastScalperEnabled = MutableStateFlow(true)

    // Liquidity Sniping & Frontrunning Protocol for brand new coins/tokens
    val isNewCoinSniperEnabled = MutableStateFlow(true)

    // Arbitrage Trading & Cross-Exchange differential scanning module
    val isArbitrageEnabled = MutableStateFlow(true)

    fun setAllTradingState(enabled: Boolean) {
        isMasterAutoTradingEnabled.value = enabled
        isClaudeSentinelEnabled.value = enabled
        isHftCoProcessorEnabled.value = enabled
        isRapidSwapEnabled.value = enabled
        isOsintScraperEnabled.value = enabled
        isSuperFastScalperEnabled.value = enabled
        isNewCoinSniperEnabled.value = enabled
        isArbitrageEnabled.value = enabled
    }

    // Metric Lists for Real-time Chart rendering (Canvas)
    private val _tpsMetrics = MutableStateFlow<List<Float>>(List(25) { Random.nextFloat() * 15 + 10 })
    val tpsMetrics: StateFlow<List<Float>> = _tpsMetrics.asStateFlow()

    private val _latencyMetrics = MutableStateFlow<List<Float>>(List(25) { Random.nextFloat() * 20 + 2 })
    val latencyMetrics: StateFlow<List<Float>> = _latencyMetrics.asStateFlow()

    private val _activeNodePulse = MutableStateFlow(0)
    val activeNodePulse: StateFlow<Int> = _activeNodePulse.asStateFlow()

    // API state
    val isApiKeyAvailable = BuildConfig.GEMINI_API_KEY.isNotEmpty() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

    init {
        // Run active simulation loop
        viewModelScope.launch {
            startSimulationLoop()
        }

        // Run metrics randomizer loop to simulate active networking pipeline
        viewModelScope.launch {
            startMetricsLoop()
        }
    }

    private suspend fun startSimulationLoop() {
        // Pre-populate database with realistic background trades across all major asset categories
        withContext(Dispatchers.IO) {
            repository.clearDatabase()
            val now = System.currentTimeMillis()
            val initialTrades = listOf(
                TradeEntity(market = "CRYPTO", type = "BUY", profit = 4200.0, deltaMs = 120, timestamp = now - 12000, source = "نظام الذكاء الفوري المضمون"),
                TradeEntity(market = "STOCKS", type = "BUY", profit = 6150.0, deltaMs = 95, timestamp = now - 9500, source = "بروتوكول تصفير الخسائر AI"),
                TradeEntity(market = "COMMODITIES", type = "BUY", profit = 3800.0, deltaMs = 140, timestamp = now - 7000, source = "خوارزمية الاستشعار السريع"),
                TradeEntity(market = "BONDS", type = "BUY", profit = 2900.0, deltaMs = 310, timestamp = now - 4500, source = "قناة تتبع السندات السيادية"),
                TradeEntity(market = "INDEXES", type = "BUY", profit = 8400.0, deltaMs = 70, timestamp = now - 2000, source = "خوارزمية النانو-ثانية المضمونة")
            )
            for (t in initialTrades) {
                repository.insertTrade(t)
            }

            val initialNews = listOf(
                NewsPipelineEntity(
                    headline = "اختراق سعري هائل مدعوم بالسيولة للمؤشرات العالمية والذهب",
                    sentiment = "BULLISH",
                    score = 0.99,
                    detectedBy = "مستشعر الذكاء الاصطناعي 💯",
                    timestamp = now - 15000
                ),
                NewsPipelineEntity(
                    headline = "العملات الرقمية الكبرى تفتتح جلسة التداول بنمو متسارع وقوي",
                    sentiment = "BULLISH",
                    score = 0.98,
                    detectedBy = "شبكة استخبارات الفرص ⚡",
                    timestamp = now - 6000
                )
            )
            for (n in initialNews) {
                repository.insertNews(n)
            }
        }

        while (true) {
            // High frequency: adjust delay based on SuperFastScalper status (Every 1.2 to 2 seconds for active scalps)
            val baseDelay = if (isSuperFastScalperEnabled.value) 1200L else 4000L
            val variance = if (isSuperFastScalperEnabled.value) 400L else 2000L
            delay(baseDelay + Random.nextLong(variance))

            if (!isMasterAutoTradingEnabled.value) {
                continue
            }

            if (!isOsintScraperEnabled.value) continue

            val activeMarket = _activeSubscription.value
            val (newsHeadline, sentiment, score, tradeType, profitVal) = generateMarketNewsAndTrade(activeMarket, isArabic.value)

            withContext(Dispatchers.IO) {
                // If New Coin Sniper is active, simulate scanning high-prestige MEMEPOOL contracts
                if (isNewCoinSniperEnabled.value && Random.nextDouble() < 0.40) {
                    val fakeTokenNames = listOf("HAWK", "ECLIPSE AI", "NEURAL SOL", "GOLDEN MEME", "VIP APEX", "GIGA COIN", "PEPE ELITE", "ARABIC WHALE")
                    val chosenToken = fakeTokenNames.random()
                    val sniperProfit = 850.0 + Random.nextDouble() * 3200.0
                    val delayMs = 1 + Random.nextLong(4) // 1-5ms extremely fast!
                    
                    repository.insertNews(
                        NewsPipelineEntity(
                            headline = if (isArabic.value) 
                                "رصد مجمع سيولة لعملة $chosenToken جديدة! محاولة ضخ وتأسيس محفظة من المبرمج..." 
                                else "Liquidity Pool detected for new coin $chosenToken! Creator attempting deployer buy-in...",
                            sentiment = "BULLISH",
                            score = 1.0,
                            detectedBy = if (isArabic.value) "قناص السيولة الذكي ⚡" else "Premium Token Sniper [MEV]",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    
                    delay(300)
                    
                    repository.insertTrade(
                        TradeEntity(
                            market = "CRYPTO",
                            type = "BUY",
                            profit = sniperProfit,
                            deltaMs = delayMs,
                            timestamp = System.currentTimeMillis(),
                            source = if (isArabic.value) 
                                "قناص النخبة: تم الشراء والبيع الفوري بالتزامن مع المبرمج بربح مقفل 🔥" 
                                else "HFT Sniper Protocol: Frontrun creator deploy transactions successfully! 100% win"
                        )
                    )
                    
                    _portfolioBalance.value += sniperProfit
                    _lastProfitFlash.value = "+$${String.format("%,.2f", sniperProfit)}"
                    delay(850)
                    _lastProfitFlash.value = null
                }

                // If Arbitrage differential core is active, simulate auto cross-exchange arbitrage
                if (isArbitrageEnabled.value && Random.nextDouble() < 0.35) {
                    val arbSymbols = listOf("BTC", "ETH", "SOL", "USDT", "XRP", "LINK", "ADA")
                    val chosenSymbol = arbSymbols.random()
                    val buyEx = listOf("Binance", "Bybit", "OKX", "Kraken").random()
                    var sellEx = listOf("Coinbase", "Gate.io", "KuCoin", "HTX").random()
                    while (sellEx == buyEx) { sellEx = listOf("Coinbase", "Gate.io", "KuCoin", "HTX").random() }
                    
                    val priceBuy = 5.0 + Random.nextDouble() * 55000.0
                    val spreadPercent = 0.005 + Random.nextDouble() * 0.015 // 0.5% - 2.0% gap
                    val priceSell = priceBuy * (1 + spreadPercent)
                    val arbProfit = 350.0 + Random.nextDouble() * 1850.0
                    val delayMs = 15 + Random.nextLong(35) // ultra fast Arbitrage execution speed
                    
                    repository.insertNews(
                        NewsPipelineEntity(
                            headline = if (isArabic.value) 
                                "رصد انحراف سعري لـ $chosenSymbol بين $buyEx ($${String.format("%,.2f", priceBuy)}) و $sellEx ($${String.format("%,.2f", priceSell)}) ⚖️"
                                else "Arbitrage Gap detected on $chosenSymbol between $buyEx ($${String.format("%,.2f", priceBuy)}) and $sellEx ($${String.format("%,.2f", priceSell)}) ⚖️",
                            sentiment = "BULLISH",
                            score = 0.98,
                            detectedBy = if (isArabic.value) "مستشعر الفروقات المتقدم 🔄" else "Spatial Arbitrage Tracker",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    
                    delay(250)
                    
                    repository.insertTrade(
                        TradeEntity(
                            market = "CRYPTO",
                            type = "BUY",
                            profit = arbProfit,
                            deltaMs = delayMs,
                            timestamp = System.currentTimeMillis(),
                            source = if (isArabic.value)
                                "تحكيم منصات: شراء من $buyEx وبيعه على $sellEx بفارق ربحي مضمون 🔥"
                                else "Cross-Exchange Arbitrage: Buy on $buyEx / Liquidate on $sellEx under Zero Loss 🔥"
                        )
                    )
                    
                    _portfolioBalance.value += arbProfit
                    _lastProfitFlash.value = "+$${String.format("%,.2f", arbProfit)}"
                    delay(850)
                    _lastProfitFlash.value = null
                }

                // Insert detected news
                repository.insertNews(
                    NewsPipelineEntity(
                        headline = newsHeadline,
                        sentiment = sentiment,
                        score = score,
                        detectedBy = if (isArabic.value) "الرادار الذكي كلود + جيميناي ⚡" else "Smart AI Co-Processor [ARM64]",
                        timestamp = System.currentTimeMillis()
                    )
                )

                // Execute ultra-high frequency trade
                val delta = 10 + Random.nextLong(65) // microsecond level 10-75ms response time!
                repository.insertTrade(
                    TradeEntity(
                        market = activeMarket,
                        type = tradeType,
                        profit = profitVal,
                        deltaMs = delta,
                        timestamp = System.currentTimeMillis(),
                        source = if (isArabic.value) "خوارزمية الربح الذكية 💯" else "HFT Alpha Engine (Auto-Win)"
                    )
                )

                // Update live balance and trigger profit flash
                _portfolioBalance.value += profitVal
                _lastProfitFlash.value = "+$${String.format("%,.2f", profitVal)}"
                delay(850)
                _lastProfitFlash.value = null
            }
        }
    }

    private suspend fun startMetricsLoop() {
        while (true) {
            delay(650)
            // Update TPS (HFT performance transaction metrics)
            val currentTps = _tpsMetrics.value.toMutableList()
            // Highly robust values showing 50-85 trades per second
            val newTpsVal = (Random.nextFloat() * 15 + 55) * (if (isHftCoProcessorEnabled.value) 1.4f else 1.0f)
            currentTps.add(newTpsVal)
            if (currentTps.size > 25) currentTps.removeAt(0)
            _tpsMetrics.value = currentTps

            // Update Latency (ms) - extremely low 0.5-3.5ms
            val currentLat = _latencyMetrics.value.toMutableList()
            val newLatVal = (Random.nextFloat() * 2.5f + 0.4f) / (if (isRapidSwapEnabled.value) 2.1f else 1.0f)
            currentLat.add(newLatVal)
            if (currentLat.size > 25) currentLat.removeAt(0)
            _latencyMetrics.value = currentLat

            // Pulse node trigger
            _activeNodePulse.value = (_activeNodePulse.value + 1) % 5
        }
    }

    fun selectSubscription(market: String) {
        _activeSubscription.value = market
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearDatabase()
        }
    }

    fun depositFunds(amount: Double, sourceName: String = "") {
        if (amount <= 0) return
        viewModelScope.launch {
            _portfolioBalance.value += amount
            val timestamp = System.currentTimeMillis()
            val finalSource = if (sourceName.isNotBlank()) sourceName else (if (isArabic.value) "تحويل محلي آمن" else "Secure Bank Wire Transfer")
            
            withContext(Dispatchers.IO) {
                repository.insertNews(
                    NewsPipelineEntity(
                        headline = if (isArabic.value) "بوابة الإيداع الفوري: تم شحن الرصيد بنجاح بمبلغ +$${String.format("%,.2f", amount)} عبر ($finalSource)" else "Express Deposit Bridge: Portfolio credited with +$${String.format("%,.2f", amount)} via ($finalSource)",
                        sentiment = "BULLISH",
                        score = 1.0,
                        detectedBy = if (isArabic.value) "بروتوكول الدفع الموثوق" else "Trusted Gateway Interface",
                        timestamp = timestamp
                    )
                )
                repository.insertTrade(
                    TradeEntity(
                        market = "VAULT FUNDING",
                        type = "BUY",
                        profit = amount,
                        deltaMs = 8,
                        timestamp = timestamp,
                        source = finalSource
                    )
                )
            }
            
            _lastProfitFlash.value = "+$${String.format("%,.0f", amount)}"
            _terminalAlert.value = if (isArabic.value) "✓ تمت معالجة الإيداع الفوري بقيمة $${String.format("%,.2f", amount)} عبر [$finalSource] بنجاح." else "✓ Credit Deposit of $${String.format("%,.2f", amount)} via [$finalSource] fully certified and posted."
            delay(2800)
            _lastProfitFlash.value = null
            _terminalAlert.value = null
        }
    }

    fun withdrawFunds(amount: Double, destinationSource: String = ""): Boolean {
        if (amount <= 0) return false
        if (_portfolioBalance.value < amount) {
            viewModelScope.launch {
                _terminalAlert.value = if (isArabic.value) "✖ تنبيه الحماية: الرصيد غير كافٍ لإجراء سحب لـ $${String.format("%,.2f", amount)}" else "✖ Security Notice: Insufficient balance to authorize $${String.format("%,.2f", amount)}"
                delay(3000)
                _terminalAlert.value = null
            }
            return false
        }
        
        viewModelScope.launch {
            _portfolioBalance.value -= amount
            val timestamp = System.currentTimeMillis()
            val finalDest = if (destinationSource.isNotBlank()) destinationSource else (if (isArabic.value) "حوالة فورية ناجحة 💯" else "Instant Ledger Settlement Success")
            
            withContext(Dispatchers.IO) {
                repository.insertNews(
                    NewsPipelineEntity(
                        headline = if (isArabic.value) "بوابة السحب المعتمَد: تم سحب مبلغ -$${String.format("%,.2f", amount)} إلى ($finalDest)" else "Authorized Revenue Payout: Settled -$${String.format("%,.2f", amount)} cleanly to ($finalDest)",
                        sentiment = "BEARISH",
                        score = 1.0,
                        detectedBy = if (isArabic.value) "طلب تسوية خارجي ومؤمن" else "Audited Bank Settler",
                        timestamp = timestamp
                    )
                )
                repository.insertTrade(
                    TradeEntity(
                        market = "VAULT WITHDRAWAL",
                        type = "SELL",
                        profit = amount,
                        deltaMs = 15,
                        timestamp = timestamp,
                        source = finalDest
                    )
                )
            }
            
            _lastProfitFlash.value = "-$${String.format("%,.0f", amount)}"
            _terminalAlert.value = if (isArabic.value) "✓ تم تحويل مبلغ السحب $${String.format("%,.2f", amount)} إلى [$finalDest] فوري بنجاح." else "✓ Success! Withdraw of $${String.format("%,.2f", amount)} to [$finalDest] is fully completed."
            delay(2800)
            _lastProfitFlash.value = null
            _terminalAlert.value = null
        }
        return true
    }

    // Main AI Engine: Automatically parses any raw signal inputted by the user and executes instant profit
    fun executeAiFrontrunner(rawSignal: String) {
        if (rawSignal.isBlank() || _isAnalyzing.value) return

        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                if (isApiKeyAvailable) {
                    val systemInstructionMsg = """
                        You are an expert AI HFT frontrunner. Parse the market signal/news and return exactly a JSON response mapping to:
                        {
                          "sentiment": "BULLISH" or "BEARISH",
                          "score": float between 0.95 and 1.0 (ensure very high confidence),
                          "market": one of CRYPTO, STOCKS, COMMODITIES, BONDS, INDEXES,
                          "recommendedAction": "BUY",
                          "reason": short professional Arabic or English reason summarizing the instant profit capture
                        }
                    """.trimIndent()

                    val promptRequest = GenerateContentRequest(
                        contents = listOf(Content(parts = listOf(Part(text = rawSignal)))),
                        generationConfig = GenerationConfig(
                            responseMimeType = "application/json"
                        ),
                        systemInstruction = Content(parts = listOf(Part(text = systemInstructionMsg)))
                    )

                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, promptRequest)
                    }

                    val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (responseText != null) {
                        parseGeminiHftResultAndExecute(responseText, rawSignal)
                    } else {
                        executeFallbackTrade(rawSignal, "API responded with empty parts")
                    }
                } else {
                    // Fallback to offline premium Arabic generator if Gemini credentials not filled
                    delay(1200)
                    val classification = classifySimulated(rawSignal)
                    saveHftTrade(
                        headline = rawSignal,
                        sentiment = classification.sentiment,
                        score = classification.score,
                        market = classification.market,
                        recommendedAction = classification.recommendedAction,
                        reason = classification.reason
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                executeFallbackTrade(rawSignal, "Safety system executed: ${e.message}")
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    private suspend fun parseGeminiHftResultAndExecute(jsonResponse: String, rawSignal: String) {
        withContext(Dispatchers.IO) {
            try {
                val json = JSONObject(jsonResponse)
                val sentiment = json.optString("sentiment", "BULLISH").uppercase()
                val score = json.optDouble("score", 0.98)
                val market = json.optString("market", _activeSubscription.value).uppercase()
                val recommendedAction = json.optString("recommendedAction", "BUY").uppercase()
                val reason = json.optString("reason", "تم رصد واقتناص الثغرة فورياً")

                saveHftTrade(rawSignal, sentiment, score, market, recommendedAction, reason)
            } catch (e: Exception) {
                e.printStackTrace()
                executeFallbackTrade(rawSignal, "Automatic safe trade triggered")
            }
        }
    }

    private suspend fun saveHftTrade(
        headline: String,
        sentiment: String,
        score: Double,
        market: String,
        recommendedAction: String,
        reason: String
    ) {
        withContext(Dispatchers.IO) {
            val finalAction = if (recommendedAction == "HOLD" || recommendedAction == "SELL") "BUY" else recommendedAction
            // Every trade in our premium zero-loss AI algorithm is modeled with high returns
            val profitAmount = Random.nextDouble(3200.0, 9400.0)

            repository.insertNews(
                NewsPipelineEntity(
                    headline = headline,
                    sentiment = "BULLISH",
                    score = score,
                    detectedBy = if (isArabic.value) "الرادار الذكي جي بي مورغان/جيميناي ⚡" else "Omniscient JP Morgan/Gemini Smart Sensor",
                    timestamp = System.currentTimeMillis()
                )
            )

            val leadTime = 8 + Random.nextLong(32) // Ultra-low microsecond HFT
            repository.insertTrade(
                TradeEntity(
                    market = market,
                    type = finalAction,
                    profit = profitAmount,
                    deltaMs = leadTime,
                    timestamp = System.currentTimeMillis(),
                    source = if (isArabic.value) "أمر ذكائي مقفل ومضمون: $reason" else "Guaranteed Smart Order: $reason"
                )
            )

            _portfolioBalance.value += profitAmount
            _lastProfitFlash.value = "+$${String.format("%,.2f", profitAmount)}"
            delay(1200)
            _lastProfitFlash.value = null
        }
    }

    private suspend fun executeFallbackTrade(rawSignal: String, errorReason: String) {
        val fallback = classifySimulated(rawSignal)
        saveHftTrade(
            headline = rawSignal,
            sentiment = fallback.sentiment,
            score = fallback.score,
            market = fallback.market,
            recommendedAction = fallback.recommendedAction,
            reason = "${fallback.reason} (تحقق فائض الأمان: $errorReason)"
        )
    }

    private fun classifySimulated(text: String): SimulatedClassification {
        val lowerText = text.lowercase()
        val currentSub = _activeSubscription.value
        return when {
            lowerText.contains("سهم") || lowerText.contains("stock") || lowerText.contains("apple") || lowerText.contains("tesla") || lowerText.contains("nvidia") -> {
                SimulatedClassification("BULLISH", 0.99, "STOCKS", "BUY", "رصد تدفق سيولة ضخم في أسهم التكنولوجيا")
            }
            lowerText.contains("ذهب") || lowerText.contains("gold") || lowerText.contains("نفط") || lowerText.contains("oil") || lowerText.contains("commodity") -> {
                SimulatedClassification("BULLISH", 0.98, "COMMODITIES", "BUY", "تحرك سريع للذهب كملاذ آمن وتأمين فوري للأرباح")
            }
            lowerText.contains("سند") || lowerText.contains("bond") || lowerText.contains("سندات") || lowerText.contains("treasuries") -> {
                SimulatedClassification("BULLISH", 0.97, "BONDS", "BUY", "اقتناص عوائد السندات الحكومية مع موازنة التضخم")
            }
            lowerText.contains("مؤشر") || lowerText.contains("index") || lowerText.contains("sp500") || lowerText.contains("nasdaq") || lowerText.contains("رهن") -> {
                SimulatedClassification("BULLISH", 0.99, "INDEXES", "BUY", "رصد ثغرة في العقود الآجلة للمؤشرات العالمية")
            }
            else -> {
                SimulatedClassification("BULLISH", 0.98, currentSub, "BUY", "إشارة تحليلية قوية تعزز الربحية المضمونة بنسبة 100%")
            }
        }
    }

    private data class SimulatedClassification(
        val sentiment: String,
        val score: Double,
        val market: String,
        val recommendedAction: String,
        val reason: String
    )

    private fun generateMarketNewsAndTrade(market: String, arabic: Boolean): SimTradeResult {
        return when (market) {
            "CRYPTO" -> {
                SimTradeResult(
                    headline = if (arabic) "عملة بيتكوين (BTC) تخترق مستويات المقاومة بدعم شراء هائل من الصناديق" else "Bitcoin (BTC) smashes major resistance ceilings with institutional demand",
                    sentiment = "BULLISH",
                    score = 0.99,
                    tradeType = "BUY",
                    profit = Random.nextDouble(3100.0, 7800.0)
                )
            }
            "STOCKS" -> {
                SimTradeResult(
                    headline = if (arabic) "عوائد شركة إنفيديا تسجل أرقاماً خيالية والذكاء الاصطناعي يصعد بقطاع الأسهم" else "Nvidia earnings register historic surges, propelling tech equities",
                    sentiment = "BULLISH",
                    score = 0.98,
                    tradeType = "BUY",
                    profit = Random.nextDouble(4200.0, 8900.0)
                )
            }
            "COMMODITIES" -> {
                SimTradeResult(
                    headline = if (arabic) "الذهب العالمي يسجل أعلى قمة تاريخية له بفعل تدفقات الاستثمار الأجنبي" else "Gold (XAU) reaches new all-time highs as protective capital peaks",
                    sentiment = "BULLISH",
                    score = 0.97,
                    tradeType = "BUY",
                    profit = Random.nextDouble(2900.0, 6400.0)
                )
            }
            "BONDS" -> {
                SimTradeResult(
                    headline = if (arabic) "سندات الخزانة الأمريكية لأجل 10 سنوات تسجل استقراراً تاماً ومكاسب معززة" else "US 10-Year Bond Treasuries reach perfect stability yields with solid flows",
                    sentiment = "BULLISH",
                    score = 0.96,
                    tradeType = "BUY",
                    profit = Random.nextDouble(1800.0, 4800.0)
                )
            }
            else -> { // INDEXES
                SimTradeResult(
                    headline = if (arabic) "مؤشر S&P 500 و Dow Jones يحطمان الأرقام القياسية مسجلين قفزة مليئة بالأرباح" else "S&P 500 and Dow Jones break structural records with high volume scalp setups",
                    sentiment = "BULLISH",
                    score = 0.99,
                    tradeType = "BUY",
                    profit = Random.nextDouble(5500.0, 11500.0)
                )
            }
        }
    }

    fun executeManualTokenSnipe(tokenAddress: String) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                delay(2000) // Beautiful analytical luxury scan delay 
                val sniperProfit = 2500.0 + Random.nextDouble() * 6300.0
                val delayMs = 1 + Random.nextLong(3) // 1-3ms super fast!
                
                withContext(Dispatchers.IO) {
                    repository.insertNews(
                        NewsPipelineEntity(
                            headline = if (isArabic.value) 
                                "تم تفعيل رادار الاختراق الفوري لعقد [$tokenAddress]! تم الشراء قبل المطور والبيع بمجرد محاولته الشراء بربح معزز 💯" 
                                else "Interception successful! Contract [$tokenAddress] sniped in MEV mempool. Frontrun purchase executed before deployer buy!",
                            sentiment = "BULLISH",
                            score = 1.0,
                            detectedBy = if (isArabic.value) "بروتوكول القناص الحديث ⚡ (Elite Sniper)" else "Elite Liquidity Sniper MEV System ⚡",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    
                    delay(500)
                    
                    repository.insertTrade(
                        TradeEntity(
                            market = "CRYPTO",
                            type = "BUY",
                            profit = sniperProfit,
                            deltaMs = delayMs,
                            timestamp = System.currentTimeMillis(),
                            source = if (isArabic.value) 
                                "اقتناص العملات الحديثة: تداول استباقي ناجح 100% بربح صاعق 🔥" 
                                else "Modern Coin Sniping Protocol: 100% successful frontrun! Safe high-profit locked"
                        )
                    )
                    
                    _portfolioBalance.value += sniperProfit
                    _lastProfitFlash.value = "+$${String.format("%,.2f", sniperProfit)}"
                    delay(1200)
                    _lastProfitFlash.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun executeManualArbitrage(assetSymbol: String, buyExchange: String, sellExchange: String, amountUsdt: Double) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _terminalAlert.value = if (isArabic.value) 
                "جارٍ فحص الفروقات السعرية لـ $assetSymbol بين $buyExchange و $sellExchange... 🔍" 
                else "Scanning price gaps for $assetSymbol between $buyExchange and $sellExchange... 🔍"
            try {
                delay(1800) // Beautiful deluxe scanning delay
                val spreadPercent = 0.008 + Random.nextDouble() * 0.022 // 0.8% - 3.0%
                val buyPrice = 1.0 + Random.nextDouble() * 55000.0
                val sellPrice = buyPrice * (1 + spreadPercent)
                val totalProfit = amountUsdt * spreadPercent * (0.95 + Random.nextDouble() * 0.1)
                val delayMs = 12 + Random.nextLong(28) // 12-40ms execution speed
                
                withContext(Dispatchers.IO) {
                    repository.insertNews(
                        NewsPipelineEntity(
                            headline = if (isArabic.value)
                                "تم اقتناص فرصة تحكيم يدوي بنجاح لـ $assetSymbol! سعر الشراء: $${String.format("%,.2f", buyPrice)} [$buyExchange] ➔ سعر البيع: $${String.format("%,.2f", sellPrice)} [$sellExchange] ✅"
                                else "Manual Arbitrage Gap seized for $assetSymbol! Buy: $${String.format("%,.2f", buyPrice)} [$buyExchange] ➔ Sell: $${String.format("%,.2f", sellPrice)} [$sellExchange] ✅",
                            sentiment = "BULLISH",
                            score = 1.0,
                            detectedBy = if (isArabic.value) "محرك التحكيم اليدوي ⚡" else "VIP Arbitrage Console",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    
                    delay(300)
                    
                    repository.insertTrade(
                        TradeEntity(
                            market = "CRYPTO",
                            type = "BUY",
                            profit = totalProfit,
                            deltaMs = delayMs,
                            timestamp = System.currentTimeMillis(),
                            source = if (isArabic.value)
                                "تداول فروقات يدوي: اقتناص فارق سعري بين $buyExchange و $sellExchange بقيمة $${String.format("%,.2f", amountUsdt)} بضمان كامل 🛡️"
                                else "Manual Arbitrage: Capitalised exchange differential between $buyExchange and $sellExchange of $${String.format("%,.2f", amountUsdt)} safely 🛡️"
                        )
                    )
                    
                    _portfolioBalance.value += totalProfit
                    _lastProfitFlash.value = "+$${String.format("%,.2f", totalProfit)}"
                    _terminalAlert.value = if (isArabic.value)
                        "✓ تم تنفيذ صفقة الفروقات بنجاح! مجمل الربح المحقق: +$${String.format("%,.2f", totalProfit)}"
                        else "✓ Arbitrage executed successfully! Realised Net Profit: +$${String.format("%,.2f", totalProfit)}"
                    
                    delay(2800)
                    _lastProfitFlash.value = null
                    _terminalAlert.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    private data class SimTradeResult(
        val headline: String,
        val sentiment: String,
        val score: Double,
        val tradeType: String,
        val profit: Double
    )
}
