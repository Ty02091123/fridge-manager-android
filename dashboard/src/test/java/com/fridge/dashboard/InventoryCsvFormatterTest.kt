package com.fridge.dashboard

import com.fridge.dashboard.data.FoodItem
import com.fridge.dashboard.data.InventoryCsvFormatter
import org.junit.Assert.assertTrue
import org.junit.Test

class InventoryCsvFormatterTest {
    @Test
    fun csvContainsBomHeaderAndRows() {
        val csv = InventoryCsvFormatter.build(
            listOf(FoodItem(1, "西红柿", "蔬菜", 3, "2026-12-01"))
        )

        assertTrue(csv.startsWith('\uFEFF'))
        assertTrue(csv.contains("编号,名称,分类,数量,过期日期"))
        assertTrue(csv.contains("\"西红柿\""))
        assertTrue(csv.contains("\"3\""))
    }

    @Test
    fun commasAndQuotesAreEscaped() {
        val csv = InventoryCsvFormatter.build(
            listOf(FoodItem(2, "番茄,\"大果\"", "蔬菜", 1, "2026-12-01"))
        )

        assertTrue(csv.contains("\"番茄,\"\"大果\"\"\""))
    }

    @Test
    fun spreadsheetFormulaIsNeutralized() {
        val csv = InventoryCsvFormatter.build(
            listOf(FoodItem(3, "=SUM(A1:A2)", "其他", 1, "2026-12-01"))
        )

        assertTrue(csv.contains("\"'=SUM(A1:A2)\""))
    }
}
