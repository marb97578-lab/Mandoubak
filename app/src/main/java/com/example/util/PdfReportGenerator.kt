package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.entity.ClientEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.PurchaseInvoiceEntity
import com.example.data.entity.SaleInvoiceEntity
import com.example.data.entity.SupplierEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    private val fileDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    /**
     * Common header drawer for professional reports
     */
    private fun drawReportHeader(
        canvas: Canvas,
        pageWidth: Int,
        title: String,
        subTitle: String,
        companyName: String,
        repName: String,
        headerColor: Int = Color.rgb(27, 43, 65) // Mandoubak Navy
    ): Float {
        val paint = Paint().apply { isAntiAlias = true }

        // Top banner
        paint.color = headerColor
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 110f, paint)

        // Title
        paint.color = Color.WHITE
        paint.textSize = 21f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(title, pageWidth / 2f, 45f, paint)

        paint.textSize = 12.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("$companyName - $subTitle", pageWidth / 2f, 72f, paint)

        val dateStr = dateFormat.format(Date())
        paint.textSize = 10f
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawText("تاريخ الإصدار: $dateStr  |  المندوب: $repName", pageWidth / 2f, 95f, paint)

        return 130f
    }

    private fun drawFooter(canvas: Canvas, pageWidth: Int, pageHeight: Int) {
        val paint = Paint().apply { isAntiAlias = true }
        val footerY = (pageHeight - 45).toFloat()

        paint.color = Color.rgb(226, 232, 240)
        canvas.drawLine(30f, footerY, (pageWidth - 30).toFloat(), footerY, paint)

        paint.color = Color.rgb(148, 163, 184)
        paint.textSize = 9f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("منظومة مـنـدوبـك التجارية - تقرير رسمي معتمد 100% Offline", pageWidth / 2f, footerY + 22f, paint)
    }

    /**
     * 1. PDF: Purchase Invoice Document (فاتورة شراء واردة رسمية)
     */
    fun generateAndSharePurchaseInvoicePdf(
        context: Context,
        purchase: PurchaseInvoiceEntity,
        companyName: String,
        representativeName: String,
        currencySymbol: String
    ): File? {
        try {
            val pdfDir = File(context.cacheDir, "purchases").apply { mkdirs() }
            val pdfFile = File(pdfDir, "${purchase.invoiceNumber}.pdf")

            val document = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint().apply { isAntiAlias = true }

            var yPos = drawReportHeader(
                canvas,
                pageWidth,
                "فاتورة مشتريات وتوريد واردة",
                "سند استلام بضائع وتوريد مخزني",
                companyName,
                representativeName,
                Color.rgb(13, 148, 136) // CardPurchasesAccent Teal
            )

            // Info Card
            paint.color = Color.rgb(240, 253, 250)
            canvas.drawRoundRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 65f, 8f, 8f, paint)

            val invoiceDate = dateFormat.format(Date(purchase.dateMillis))

            paint.color = Color.rgb(27, 43, 65)
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("رقم الفاتورة: ${purchase.invoiceNumber}", (pageWidth - 45).toFloat(), yPos + 25f, paint)
            canvas.drawText("المورد: ${purchase.supplierName}", (pageWidth - 45).toFloat(), yPos + 48f, paint)

            paint.textAlign = Paint.Align.LEFT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("التاريخ: $invoiceDate", 45f, yPos + 25f, paint)
            canvas.drawText("حالة السداد: ${if (purchase.isCredit) "آجل على المنشأة" else "مسددة بالكامل"}", 45f, yPos + 48f, paint)

            // Items Table Header
            yPos += 85f
            paint.color = Color.rgb(13, 148, 136)
            canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 28f, paint)

            paint.color = Color.WHITE
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("الأصناف والكميات المستلمة", (pageWidth - 45).toFloat(), yPos + 18f, paint)
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("طريقة الدفع: ${purchase.paymentMethod}", 45f, yPos + 18f, paint)

            // Items List
            yPos += 30f
            paint.color = Color.rgb(51, 65, 85)
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            val items = purchase.itemsSummary.split("،").map { it.trim() }.filter { it.isNotBlank() }
            for (item in items) {
                yPos += 24f
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText("• $item", (pageWidth - 45).toFloat(), yPos, paint)

                paint.color = Color.rgb(226, 232, 240)
                canvas.drawLine(30f, yPos + 8f, (pageWidth - 30).toFloat(), yPos + 8f, paint)
                paint.color = Color.rgb(51, 65, 85)
            }

            // Summary Card
            yPos += 45f
            paint.color = Color.rgb(248, 250, 252)
            canvas.drawRoundRect((pageWidth - 260).toFloat(), yPos, (pageWidth - 30).toFloat(), yPos + 120f, 10f, 10f, paint)

            paint.color = Color.rgb(27, 43, 65)
            paint.textSize = 11f
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("إجمالي المشتريات:", (pageWidth - 45).toFloat(), yPos + 25f, paint)
            canvas.drawText("الخصم المكتسب:", (pageWidth - 45).toFloat(), yPos + 50f, paint)
            canvas.drawText("الصافي المستحق:", (pageWidth - 45).toFloat(), yPos + 75f, paint)
            canvas.drawText("المبلغ المدفوع للمورد:", (pageWidth - 45).toFloat(), yPos + 98f, paint)

            paint.textAlign = Paint.Align.LEFT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val subtotal = if (purchase.subtotalAmount > 0) purchase.subtotalAmount else purchase.totalAmount
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", subtotal, currencySymbol), (pageWidth - 245).toFloat(), yPos + 25f, paint)
            paint.color = Color.rgb(5, 150, 105)
            canvas.drawText(String.format(Locale.getDefault(), "- %.1f %s", purchase.discountAmount, currencySymbol), (pageWidth - 245).toFloat(), yPos + 50f, paint)
            paint.color = Color.rgb(13, 148, 136)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", purchase.totalAmount, currencySymbol), (pageWidth - 245).toFloat(), yPos + 75f, paint)
            paint.color = Color.rgb(37, 99, 235)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", purchase.paidAmount, currencySymbol), (pageWidth - 245).toFloat(), yPos + 98f, paint)

            // Signatures
            yPos += 160f
            paint.color = Color.rgb(226, 232, 240)
            canvas.drawLine(30f, yPos, (pageWidth - 30).toFloat(), yPos, paint)

            yPos += 28f
            paint.color = Color.rgb(27, 43, 65)
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("توقيع / ختم المورد:", (pageWidth - 45).toFloat(), yPos, paint)
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("المستلم (المندوب): $representativeName", 45f, yPos, paint)

            drawFooter(canvas, pageWidth, pageHeight)
            document.finishPage(page)

            val fos = FileOutputStream(pdfFile)
            document.writeTo(fos)
            fos.close()
            document.close()

            sharePdfFile(context, pdfFile, "فاتورة شراء واردة ${purchase.invoiceNumber}")
            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 2. PDF: Comprehensive Financial & Profit Report (تقرير الأرباح والمبيعات الشامل)
     */
    fun generateAndShareComprehensiveReportPdf(
        context: Context,
        periodName: String,
        totalSales: Double,
        totalPurchases: Double,
        totalExpenses: Double,
        grossProfit: Double,
        netProfit: Double,
        invoicesCount: Int,
        companyName: String,
        representativeName: String,
        currencySymbol: String
    ): File? {
        try {
            val pdfDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val pdfFile = File(pdfDir, "Comprehensive_Report_${fileDateFormat.format(Date())}.pdf")

            val document = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint().apply { isAntiAlias = true }

            var yPos = drawReportHeader(
                canvas,
                pageWidth,
                "تقرير الأداء المالي والأرباح",
                "الفترة: $periodName",
                companyName,
                representativeName,
                Color.rgb(27, 43, 65)
            )

            // KPI Grid (Sales, Purchases, Expenses, Net Profit)
            val boxW = (pageWidth - 75f) / 2f
            val boxH = 65f

            // Box 1: Sales
            paint.color = Color.rgb(239, 246, 255)
            canvas.drawRoundRect(30f, yPos, 30f + boxW, yPos + boxH, 8f, 8f, paint)
            paint.color = Color.rgb(37, 99, 235)
            paint.textSize = 11f
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("إجمالي المبيعات المحققة", 30f + boxW - 12f, yPos + 22f, paint)
            paint.textSize = 15f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", totalSales, currencySymbol), 30f + boxW - 12f, yPos + 48f, paint)

            // Box 2: Purchases
            val box2X = 30f + boxW + 15f
            paint.color = Color.rgb(240, 253, 250)
            canvas.drawRoundRect(box2X, yPos, box2X + boxW, yPos + boxH, 8f, 8f, paint)
            paint.color = Color.rgb(13, 148, 136)
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("إجمالي المشتريات والتوريد", box2X + boxW - 12f, yPos + 22f, paint)
            paint.textSize = 15f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", totalPurchases, currencySymbol), box2X + boxW - 12f, yPos + 48f, paint)

            // Row 2 Boxes: Expenses & Net Profit
            yPos += boxH + 12f
            // Box 3: Expenses
            paint.color = Color.rgb(254, 242, 242)
            canvas.drawRoundRect(30f, yPos, 30f + boxW, yPos + boxH, 8f, 8f, paint)
            paint.color = Color.rgb(220, 38, 38)
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("المصروفات التشغيلية والنثريات", 30f + boxW - 12f, yPos + 22f, paint)
            paint.textSize = 15f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", totalExpenses, currencySymbol), 30f + boxW - 12f, yPos + 48f, paint)

            // Box 4: Net Profit
            paint.color = Color.rgb(236, 253, 245)
            canvas.drawRoundRect(box2X, yPos, box2X + boxW, yPos + boxH, 8f, 8f, paint)
            paint.color = Color.rgb(5, 150, 105)
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("صافي الأرباح المحققة (Net Profit)", box2X + boxW - 12f, yPos + 22f, paint)
            paint.textSize = 15f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", netProfit, currencySymbol), box2X + boxW - 12f, yPos + 48f, paint)

            // Accounting Calculation Table
            yPos += boxH + 25f
            paint.color = Color.rgb(27, 43, 65)
            canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 28f, paint)
            paint.color = Color.WHITE
            paint.textSize = 11.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("جدول التدفق المالي وحساب الأرباح المحاسبي", (pageWidth - 45).toFloat(), yPos + 18f, paint)
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("عدد الفواتير: $invoicesCount", 45f, yPos + 18f, paint)

            // Table Rows
            val tableRows = listOf(
                Pair("إجمالي إيرادات المبيعات (Revenue)", totalSales),
                Pair("ناقص (-) تكلفة المشتريات والبضاعة", -totalPurchases),
                Pair("المجمل التجاري (Gross Profit)", grossProfit),
                Pair("ناقص (-) إجمالي المصروفات والنثريات", -totalExpenses),
                Pair("صافي الربح الفعلي النهائي (Net Income)", netProfit)
            )

            yPos += 28f
            for ((index, item) in tableRows.withIndex()) {
                val isNet = index == tableRows.lastIndex
                paint.color = if (isNet) Color.rgb(240, 253, 244) else if (index % 2 == 0) Color.rgb(248, 250, 252) else Color.WHITE
                canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 32f, paint)

                paint.color = if (isNet) Color.rgb(5, 150, 105) else Color.rgb(51, 65, 85)
                paint.textSize = if (isNet) 12f else 11f
                paint.typeface = Typeface.create(Typeface.DEFAULT, if (isNet) Typeface.BOLD else Typeface.NORMAL)
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText(item.first, (pageWidth - 45).toFloat(), yPos + 21f, paint)

                paint.textAlign = Paint.Align.LEFT
                val amountStr = String.format(Locale.getDefault(), "%.1f %s", item.second, currencySymbol)
                canvas.drawText(amountStr, 45f, yPos + 21f, paint)

                yPos += 32f
            }

            // Calculation Explanation Formula note
            yPos += 30f
            paint.color = Color.rgb(241, 245, 249)
            canvas.drawRoundRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 48f, 8f, 8f, paint)
            paint.color = Color.rgb(71, 85, 105)
            paint.textSize = 10.5f
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("معادلة الاحتساب المحاسبي: صافي الربح = إجمالي المبيعات - إجمالي المشتريات - المصروفات", pageWidth / 2f, yPos + 28f, paint)

            drawFooter(canvas, pageWidth, pageHeight)
            document.finishPage(page)

            val fos = FileOutputStream(pdfFile)
            document.writeTo(fos)
            fos.close()
            document.close()

            sharePdfFile(context, pdfFile, "تقرير الأداء المالي والأرباح ($periodName)")
            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 3. PDF: Inventory Stock Valuation & Health Report (تقرير تقييم المخزون)
     */
    fun generateAndShareInventoryReportPdf(
        context: Context,
        products: List<ProductEntity>,
        companyName: String,
        representativeName: String,
        currencySymbol: String
    ): File? {
        try {
            val pdfDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val pdfFile = File(pdfDir, "Inventory_Report_${fileDateFormat.format(Date())}.pdf")

            val document = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint().apply { isAntiAlias = true }

            var yPos = drawReportHeader(
                canvas,
                pageWidth,
                "تقرير جرد وتقييم المخزون",
                "حالة المستودع وحركة الأصناف",
                companyName,
                representativeName,
                Color.rgb(234, 88, 12) // CardInventoryAccent Orange
            )

            // Stock KPI Stats
            val totalQty = products.sumOf { it.stockQuantity }
            val totalCost = products.sumOf { it.costPrice * it.stockQuantity }
            val totalSale = products.sumOf { it.salePrice * it.stockQuantity }
            val expectedProfit = totalSale - totalCost

            val boxW = (pageWidth - 75f) / 3f
            val boxH = 50f

            // Total Cost
            paint.color = Color.rgb(255, 247, 237)
            canvas.drawRoundRect(30f, yPos, 30f + boxW, yPos + boxH, 8f, 8f, paint)
            paint.color = Color.rgb(234, 88, 12)
            paint.textSize = 10f
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("قيمة المخزون بسعر التكلفة", 30f + boxW - 8f, yPos + 18f, paint)
            paint.textSize = 13f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", totalCost, currencySymbol), 30f + boxW - 8f, yPos + 38f, paint)

            // Total Sales Value
            val b2X = 30f + boxW + 7.5f
            paint.color = Color.rgb(239, 246, 255)
            canvas.drawRoundRect(b2X, yPos, b2X + boxW, yPos + boxH, 8f, 8f, paint)
            paint.color = Color.rgb(37, 99, 235)
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("قيمة المخزون بسعر البيع", b2X + boxW - 8f, yPos + 18f, paint)
            paint.textSize = 13f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", totalSale, currencySymbol), b2X + boxW - 8f, yPos + 38f, paint)

            // Expected Profit
            val b3X = b2X + boxW + 7.5f
            paint.color = Color.rgb(236, 253, 245)
            canvas.drawRoundRect(b3X, yPos, b3X + boxW, yPos + boxH, 8f, 8f, paint)
            paint.color = Color.rgb(5, 150, 105)
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("الأرباح المتوقعة عند البيع", b3X + boxW - 8f, yPos + 18f, paint)
            paint.textSize = 13f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", expectedProfit, currencySymbol), b3X + boxW - 8f, yPos + 38f, paint)

            // Table Header
            yPos += boxH + 20f
            paint.color = Color.rgb(27, 43, 65)
            canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 26f, paint)

            paint.color = Color.WHITE
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("الصنف / التصنيف", (pageWidth - 40).toFloat(), yPos + 17f, paint)
            canvas.drawText("الرصيد", 310f, yPos + 17f, paint)
            canvas.drawText("التكلفة", 210f, yPos + 17f, paint)
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("سعر البيع", 45f, yPos + 17f, paint)

            // Table Content (top 15 products or all)
            val displayProducts = products.take(15)
            yPos += 26f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            for ((i, p) in displayProducts.withIndex()) {
                paint.color = if (i % 2 == 0) Color.rgb(248, 250, 252) else Color.WHITE
                canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 24f, paint)

                paint.color = if (p.stockQuantity <= 0) Color.rgb(220, 38, 38)
                else if (p.stockQuantity <= p.minStockThreshold) Color.rgb(217, 119, 6)
                else Color.rgb(30, 41, 59)

                paint.textSize = 10f
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText("${p.name} (${p.category})", (pageWidth - 40).toFloat(), yPos + 16f, paint)
                canvas.drawText("${p.stockQuantity} ${p.unit}", 310f, yPos + 16f, paint)
                canvas.drawText(String.format(Locale.getDefault(), "%.1f", p.costPrice), 210f, yPos + 16f, paint)

                paint.textAlign = Paint.Align.LEFT
                canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", p.salePrice, currencySymbol), 45f, yPos + 16f, paint)

                yPos += 24f
            }

            drawFooter(canvas, pageWidth, pageHeight)
            document.finishPage(page)

            val fos = FileOutputStream(pdfFile)
            document.writeTo(fos)
            fos.close()
            document.close()

            sharePdfFile(context, pdfFile, "تقرير جرد وتقييم المخزون")
            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun sharePdfFile(context: Context, pdfFile: File, title: String) {
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "تم إصدار $title عبر تطبيق مندوبك التجاري")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "مشاركة أو طباعة الملف عبر...").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}
