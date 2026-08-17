package com.hima.alwarsha.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import com.hima.alwarsha.data.entity.CarEntity
import com.hima.alwarsha.data.entity.ServiceLogEntity
import com.hima.alwarsha.data.model.CarHealthSummary
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Builds a one-page PDF summary of the car's real logged maintenance history for resale purposes. */
object PdfReportGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40

    fun generate(
        context: Context,
        car: CarEntity,
        healthSummary: CarHealthSummary?,
        serviceLogs: List<ServiceLogEntity>,
        itemNameFor: (Long) -> String
    ): File {
        val document = PdfDocument()
        val page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create())
        val canvas = page.canvas

        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 14f
            color = android.graphics.Color.BLACK
        }

        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
        val text = buildString {
            appendLine("تقرير حالة السيارة — الورشة")
            appendLine("تاريخ الإصدار: ${dateFormat.format(Date())}")
            appendLine()
            appendLine("السيارة: ${car.brand} ${car.model} (${car.year})")
            appendLine("نوع الفتيس: ${car.transmissionType}")
            appendLine("قراءة العداد الحالية: ${car.currentOdometer} كم")
            healthSummary?.let { appendLine("نسبة الحالة العامة: ${it.healthScore}% — ${it.statusTextAr}") }
            appendLine()
            appendLine("حالة بنود الصيانة:")
            healthSummary?.itemsByCategory?.values?.flatten()?.forEach { status ->
                val statusText = if (status.remainingKm <= 0) "متأخرة" else "متبقي ${status.remainingKm} كم"
                appendLine("- ${status.itemNameAr}: $statusText (آخر تغيير عند ${status.lastChangeOdometer} كم)")
            }
            appendLine()
            appendLine("آخر عمليات الصيانة المسجلة (${serviceLogs.size} إجمالاً):")
            serviceLogs.take(15).forEach { log ->
                val logDate = dateFormat.format(Date(log.performedDateEpoch))
                appendLine("- ${itemNameFor(log.itemId)} عند ${log.performedOdometer} كم بتاريخ $logDate")
            }
        }

        val layout = StaticLayout.Builder
            .obtain(text, 0, text.length, paint, PAGE_WIDTH - 2 * MARGIN)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setLineSpacing(4f, 1.1f)
            .build()

        canvas.save()
        canvas.translate(MARGIN.toFloat(), MARGIN.toFloat())
        layout.draw(canvas)
        canvas.restore()

        document.finishPage(page)

        val reportsDir = File(context.getExternalFilesDir(null), "reports").apply { mkdirs() }
        val file = File(reportsDir, "resale_report_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()
        return file
    }
}
