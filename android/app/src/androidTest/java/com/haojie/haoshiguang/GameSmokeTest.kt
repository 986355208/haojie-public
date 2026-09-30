package com.haojie.haoshiguang

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameSmokeTest {
    @Test
    fun localGameLoadsModelsAndRendersOnDevice() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, MainActivity::class.java).putExtra("game_test_mode", true)
        ActivityScenario.launch<MainActivity>(intent).use { scenario ->
            val ready = CountDownLatch(1)
            val result = arrayOf<String?>(null)
            scenario.onActivity { activity ->
                fun poll() {
                    activity.gameView.evaluateJavascript(
                        """(() => { const g = window.__game; if (!g) return null; const s = g.snapshot(); if (!s.models?.includes('rabbit') || !s.models?.includes('pet') || !s.drawCalls || !s.triangles) return null; document.querySelector('#start')?.click(); return JSON.stringify({title: document.title, models: s.models, drawCalls: s.drawCalls, triangles: s.triangles, started: !g.paused}); })()"""
                    ) { value ->
                        if (value != null && value != "null") {
                            result[0] = value
                            ready.countDown()
                        } else {
                            activity.gameView.postDelayed({ poll() }, 500)
                        }
                    }
                }
                poll()
            }
            assertTrue("WebView did not initialize the game and render its 3D scene", ready.await(90, TimeUnit.SECONDS))
            val report = result[0].orEmpty()
            assertTrue("Game page title missing: $report", report.contains("浩劫好时光"))
            assertTrue("Rabbit and pet models were not both loaded: $report", report.contains("rabbit") && report.contains("pet"))
            assertTrue("3D renderer did not draw the scene: $report", report.contains("drawCalls") && report.contains("triangles"))
            assertTrue("Start button did not enter the game: $report", report.contains("started") && report.contains("true"))
        }
    }
}
