package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.data.entity.PaymentReceiptEntity
import com.example.data.entity.SaleInvoiceEntity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PdfInvoiceItem(
    val name: String,
    val barcode: String = "",
    val unit: String = "حبة",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0
)

object PdfInvoiceGenerator {

    fun buildInvoicePdfFile(
        context: Context,
        invoice: SaleInvoiceEntity,
        customerPhone: String = "",
        items: List<PdfInvoiceItem>? = null,
        companyName: String,
        representativeName: String,
        taxNumber: String,
        currencySymbol: String
    ): File? {
        try {
            val pdfDir = File(context.cacheDir, "invoices")
            if (!pdfDir.exists()) {
                pdfDir.mkdirs()
            }
            val pdfFile = File(pdfDir, "${invoice.invoiceNumber}.pdf")

            val document = PdfDocument()
            val pageWidth = 595 // Standard A4 points
            val pageHeight = 842
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint().apply {
                isAntiAlias = true
            }

            // Header Background Accent
            paint.color = Color.rgb(27, 43, 65) // Mandoubak Navy
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 120f, paint)

            // Header Title
            paint.color = Color.WHITE
            paint.textSize = 22f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("فاتورة مبيعات ضريبية مبسطة", pageWidth / 2f, 50f, paint)

            paint.textSize = 13f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(companyName, pageWidth / 2f, 75f, paint)

            if (taxNumber.isNotBlank()) {
                paint.textSize = 10f
                canvas.drawText("الرقم الضريبي: $taxNumber  |  المندوب: $representativeName", pageWidth / 2f, 95f, paint)
            }

            // Invoice Info Bar
            var yPos = 140f
            paint.color = Color.rgb(241, 245, 249)
            val infoBoxHeight = if (customerPhone.isNotBlank()) 85f else 68f
            canvas.drawRoundRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + infoBoxHeight, 8f, 8f, paint)

            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            val dateStr = dateFormat.format(Date(invoice.dateMillis))

            paint.color = Color.rgb(27, 43, 65)
            paint.textSize = 11.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("رقم الفاتورة: ${invoice.invoiceNumber}", (pageWidth - 45).toFloat(), yPos + 24f, paint)
            canvas.drawText("العميل: ${invoice.clientName}", (pageWidth - 45).toFloat(), yPos + 46f, paint)
            if (customerPhone.isNotBlank()) {
                canvas.drawText("رقم الجوال: $customerPhone", (pageWidth - 45).toFloat(), yPos + 68f, paint)
            }

            paint.textAlign = Paint.Align.LEFT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("التاريخ والوقت: $dateStr", 45f, yPos + 24f, paint)
            canvas.drawText("طريقة الدفع: ${invoice.paymentMethod}", 45f, yPos + 46f, paint)

            // Resolve items to display
            val tableItems = if (!items.isNullOrEmpty()) {
                items
            } else {
                parseItemsFromSummary(invoice.itemsSummary, invoice.totalAmount)
            }

            // Products Table
            yPos += infoBoxHeight + 20f

            // Table Header Bar
            paint.color = Color.rgb(37, 99, 235) // Mandoubak Blue Header
            canvas.drawRoundRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 28f, 4f, 4f, paint)

            paint.color = Color.WHITE
            paint.textSize = 10.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            // Column Headers (Page Width = 595, Table = 30 to 565, width = 535)
            // Col 1: # (30 to 60)
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("#", 45f, yPos + 18f, paint)

            // Col 2: بيان الصنف (60 to 280)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("بيان الصنف والمواصفات", 270f, yPos + 18f, paint)

            // Col 3: الوحدة (280 to 340)
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("الوحدة", 310f, yPos + 18f, paint)

            // Col 4: الكمية (340 to 400)
            canvas.drawText("الكمية", 370f, yPos + 18f, paint)

            // Col 5: السعر (400 to 475)
            canvas.drawText("السعر ($currencySymbol)", 437f, yPos + 18f, paint)

            // Col 6: الإجمالي (475 to 565)
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("الإجمالي ($currencySymbol)", 485f, yPos + 18f, paint)

            // Table Rows
            yPos += 28f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 10.5f

            tableItems.forEachIndexed { index, item ->
                yPos += 24f

                // Alternating row background
                if (index % 2 == 1) {
                    paint.color = Color.rgb(248, 250, 252)
                    canvas.drawRect(30f, yPos - 16f, (pageWidth - 30).toFloat(), yPos + 8f, paint)
                }

                // Row bottom separator line
                paint.color = Color.rgb(226, 232, 240)
                canvas.drawLine(30f, yPos + 8f, (pageWidth - 30).toFloat(), yPos + 8f, paint)

                paint.color = Color.rgb(51, 65, 85)

                // Col 1: #
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("${index + 1}", 45f, yPos, paint)

                // Col 2: Name
                paint.textAlign = Paint.Align.RIGHT
                val displayName = if (item.name.length > 32) item.name.take(30) + "..." else item.name
                canvas.drawText(displayName, 270f, yPos, paint)

                // Col 3: Unit
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(item.unit.ifBlank { "حبة" }, 310f, yPos, paint)

                // Col 4: Quantity
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("${item.quantity}", 370f, yPos, paint)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

                // Col 5: Unit Price
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(String.format(Locale.getDefault(), "%.1f", item.unitPrice), 437f, yPos, paint)

                // Col 6: Total Price
                paint.textAlign = Paint.Align.LEFT
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.color = Color.rgb(27, 43, 65)
                canvas.drawText(String.format(Locale.getDefault(), "%.1f", item.totalPrice), 485f, yPos, paint)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }

            // Outer border for table
            paint.color = Color.rgb(203, 213, 225)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRect(30f, yPos - (tableItems.size * 24f) - 28f, (pageWidth - 30).toFloat(), yPos + 8f, paint)
            paint.style = Paint.Style.FILL

            // Financial Summary Block
            yPos += 30f
            val summaryHeight = if (invoice.profitAmount > 0) 165f else 145f
            paint.color = Color.rgb(248, 250, 252)
            canvas.drawRoundRect((pageWidth - 300).toFloat(), yPos, (pageWidth - 30).toFloat(), yPos + summaryHeight, 10f, 10f, paint)

            paint.color = Color.rgb(27, 43, 65)
            paint.textSize = 11f
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("المجموع قبل الخصم (Subtotal):", (pageWidth - 45).toFloat(), yPos + 25f, paint)
            canvas.drawText("الخصم الممنوح (Discount):", (pageWidth - 45).toFloat(), yPos + 48f, paint)
            canvas.drawText("المبلغ الصافي النهائي (Net Total):", (pageWidth - 45).toFloat(), yPos + 74f, paint)
            canvas.drawText("المبلغ المدفوع (Paid Amount):", (pageWidth - 45).toFloat(), yPos + 98f, paint)
            canvas.drawText("الرصيد المتبقي (Remaining):", (pageWidth - 45).toFloat(), yPos + 122f, paint)
            if (invoice.profitAmount > 0) {
                paint.color = Color.rgb(5, 150, 105)
                canvas.drawText("ربح الفاتورة (Invoice Profit):", (pageWidth - 45).toFloat(), yPos + 146f, paint)
            }

            paint.textAlign = Paint.Align.LEFT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val subtotal = if (invoice.subtotalAmount > 0) invoice.subtotalAmount else invoice.totalAmount
            paint.color = Color.rgb(27, 43, 65)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", subtotal, currencySymbol), (pageWidth - 285).toFloat(), yPos + 25f, paint)
            paint.color = Color.rgb(220, 38, 38)
            canvas.drawText(String.format(Locale.getDefault(), "- %.1f %s", invoice.discountAmount, currencySymbol), (pageWidth - 285).toFloat(), yPos + 48f, paint)
            paint.color = Color.rgb(37, 99, 235)
            paint.textSize = 12f
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", invoice.totalAmount, currencySymbol), (pageWidth - 285).toFloat(), yPos + 74f, paint)
            paint.color = Color.rgb(5, 150, 105)
            paint.textSize = 11f
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", invoice.paidAmount, currencySymbol), (pageWidth - 285).toFloat(), yPos + 98f, paint)
            paint.color = if (invoice.remainingAmount > 0) Color.rgb(217, 119, 6) else Color.rgb(5, 150, 105)
            canvas.drawText(String.format(Locale.getDefault(), "%.1f %s", invoice.remainingAmount, currencySymbol), (pageWidth - 285).toFloat(), yPos + 122f, paint)
            if (invoice.profitAmount > 0) {
                paint.color = Color.rgb(5, 150, 105)
                canvas.drawText(String.format(Locale.getDefault(), "+ %.1f %s", invoice.profitAmount, currencySymbol), (pageWidth - 285).toFloat(), yPos + 146f, paint)
            }

            // Notes Block
            if (invoice.notes.isNotBlank()) {
                yPos += summaryHeight + 20f
                paint.color = Color.rgb(241, 245, 249)
                canvas.drawRoundRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 35f, 6f, 6f, paint)
                paint.color = Color.rgb(71, 85, 105)
                paint.textSize = 10.5f
                paint.textAlign = Paint.Align.RIGHT
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText("ملاحظات الفاتورة: ${invoice.notes}", (pageWidth - 45).toFloat(), yPos + 22f, paint)
            }

            // Footer & Watermark
            val footerY = (pageHeight - 70).toFloat()
            paint.color = Color.rgb(226, 232, 240)
            canvas.drawLine(30f, footerY, (pageWidth - 30).toFloat(), footerY, paint)

            paint.color = Color.rgb(148, 163, 184)
            paint.textSize = 9.5f
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("تم إصدار هذه الفاتورة إلكترونياً بواسطة منظومة مـنـدوبـك التجارية - 100% Offline-First", pageWidth / 2f, footerY + 25f, paint)
            canvas.drawText("شكراً لتعاملكم معنا", pageWidth / 2f, footerY + 45f, paint)

            document.finishPage(page)

            val outputStream = FileOutputStream(pdfFile)
            document.writeTo(outputStream)
            outputStream.close()
            document.close()

            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun parseItemsFromSummary(itemsSummary: String, invoiceTotal: Double): List<PdfInvoiceItem> {
        if (itemsSummary.isBlank()) {
            return listOf(PdfInvoiceItem(name = "فاتورة مبيعات مباشرة", unit = "خدمة", quantity = 1, unitPrice = invoiceTotal, totalPrice = invoiceTotal))
        }
        val parts = itemsSummary.split("،").map { it.trim() }.filter { it.isNotBlank() }
        return parts.mapIndexed { idx, p ->
            // Try parsing "Name (×Qty Unit)"
            val match = Regex("""^(.*?)\s*\(×(\d+)(?:\s+(.*?))?\)$""").find(p)
            if (match != null) {
                val name = match.groupValues[1].trim()
                val qty = match.groupValues[2].toIntOrNull() ?: 1
                val unit = match.groupValues[3].ifBlank { "حبة" }
                val unitPrice = if (qty > 0) invoiceTotal / (parts.size * qty) else invoiceTotal
                PdfInvoiceItem(name = name, unit = unit, quantity = qty, unitPrice = unitPrice, totalPrice = unitPrice * qty)
            } else {
                val approxPrice = if (parts.isNotEmpty()) invoiceTotal / parts.size else invoiceTotal
                PdfInvoiceItem(name = p, unit = "حبة", quantity = 1, unitPrice = approxPrice, totalPrice = approxPrice)
            }
        }
    }

    fun shareInvoicePdf(context: Context, pdfFile: File, invoice: SaleInvoiceEntity) {
        try {
            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "فاتورة مبيعات ${invoice.invoiceNumber}")
                putExtra(Intent.EXTRA_TEXT, "فاتورة مبيعات رقم ${invoice.invoiceNumber} للعميل ${invoice.clientName}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "مشاركة الفاتورة عبر...").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun printInvoicePdf(context: Context, pdfFile: File, invoice: SaleInvoiceEntity) {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager != null) {
                val printAdapter = object : PrintDocumentAdapter() {
                    override fun onLayout(
                        oldAttributes: PrintAttributes?,
                        newAttributes: PrintAttributes?,
                        cancellationSignal: CancellationSignal?,
                        callback: LayoutResultCallback?,
                        extras: Bundle?
                    ) {
                        if (cancellationSignal?.isCanceled == true) {
                            callback?.onLayoutCancelled()
                            return
                        }
                        val info = PrintDocumentInfo.Builder("${invoice.invoiceNumber}.pdf")
                            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                            .setPageCount(1)
                            .build()
                        callback?.onLayoutFinished(info, true)
                    }

                    override fun onWrite(
                        pages: Array<out PageRange>?,
                        destination: ParcelFileDescriptor?,
                        cancellationSignal: CancellationSignal?,
                        callback: WriteResultCallback?
                    ) {
                        try {
                            val input = FileInputStream(pdfFile)
                            val output = FileOutputStream(destination?.fileDescriptor)
                            val buf = ByteArray(1024)
                            var bytesRead: Int
                            while (input.read(buf).also { bytesRead = it } > 0) {
                                output.write(buf, 0, bytesRead)
                            }
                            input.close()
                            output.close()
                            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                        } catch (e: Exception) {
                            callback?.onWriteFailed(e.message)
                        }
                    }
                }
                val attributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                    .build()
                printManager.print("فاتورة_${invoice.invoiceNumber}", printAdapter, attributes)
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Fallback to share/view intent
        shareInvoicePdf(context, pdfFile, invoice)
    }

    fun generateAndShareInvoicePdf(
        context: Context,
        invoice: SaleInvoiceEntity,
        companyName: String,
        representativeName: String,
        taxNumber: String,
        currencySymbol: String,
        customerPhone: String = "",
        items: List<PdfInvoiceItem>? = null
    ): File? {
        val pdfFile = buildInvoicePdfFile(
            context = context,
            invoice = invoice,
            customerPhone = customerPhone,
            items = items,
            companyName = companyName,
            representativeName = representativeName,
            taxNumber = taxNumber,
            currencySymbol = currencySymbol
        )
        if (pdfFile != null) {
            shareInvoicePdf(context, pdfFile, invoice)
        }
        return pdfFile
    }

    fun generateAndShareReceiptPdf(
        context: Context,
        receipt: PaymentReceiptEntity,
        companyName: String,
        representativeName: String,
        currencySymbol: String
    ): File? {
        try {
            val pdfDir = File(context.cacheDir, "receipts")
            if (!pdfDir.exists()) {
                pdfDir.mkdirs()
            }
            val pdfFile = File(pdfDir, "${receipt.receiptNumber}.pdf")

            val document = PdfDocument()
            val pageWidth = 595
            val pageHeight = 520 // Half A4 landscape voucher format
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint().apply {
                isAntiAlias = true
            }

            // Header Accent
            paint.color = Color.rgb(124, 58, 237) // Mandoubak Violet / Receipt Accent
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 100f, paint)

            paint.color = Color.WHITE
            paint.textSize = 22f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("سند قبض نقدي رسمي", pageWidth / 2f, 45f, paint)

            paint.textSize = 12.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(companyName, pageWidth / 2f, 72f, paint)

            // Receipt Box
            var yPos = 130f
            paint.color = Color.rgb(248, 250, 252)
            canvas.drawRoundRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 60f, 8f, 8f, paint)

            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            val dateStr = dateFormat.format(Date(receipt.dateMillis))

            paint.color = Color.rgb(27, 43, 65)
            paint.textSize = 12.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("رقم السند: ${receipt.receiptNumber}", (pageWidth - 45).toFloat(), yPos + 25f, paint)
            canvas.drawText("المندوب المستلم: $representativeName", (pageWidth - 45).toFloat(), yPos + 48f, paint)

            paint.textAlign = Paint.Align.LEFT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("التاريخ: $dateStr", 45f, yPos + 25f, paint)
            canvas.drawText("طريقة القبض: ${receipt.paymentMethod}", 45f, yPos + 48f, paint)

            // Content Body
            yPos += 90f
            paint.color = Color.rgb(27, 43, 65)
            paint.textSize = 13f
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("استلمنا من المكرم / السادة: ${receipt.clientName}", (pageWidth - 45).toFloat(), yPos, paint)

            yPos += 32f
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = Color.rgb(124, 58, 237)
            canvas.drawText("مبلغاً وقدره: ${String.format(Locale.getDefault(), "%.1f %s", receipt.amount, currencySymbol)}", (pageWidth - 45).toFloat(), yPos, paint)

            yPos += 30f
            paint.color = Color.rgb(51, 65, 85)
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val notesDesc = if (receipt.notes.isNotBlank()) receipt.notes else "دفعة تحت الحساب وتخفيض الذمم"
            canvas.drawText("وذلك عن: $notesDesc", (pageWidth - 45).toFloat(), yPos, paint)

            // Signatures block
            yPos += 65f
            paint.color = Color.rgb(226, 232, 240)
            canvas.drawLine(30f, yPos, (pageWidth - 30).toFloat(), yPos, paint)

            yPos += 35f
            paint.color = Color.rgb(27, 43, 65)
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("توقيع المستلم (المندوب):", (pageWidth - 45).toFloat(), yPos, paint)
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("ختم المؤسسة / التوقيع:", 45f, yPos, paint)

            // Footer
            val footerY = (pageHeight - 40).toFloat()
            paint.color = Color.rgb(148, 163, 184)
            paint.textSize = 9f
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("سند قبض إلكتروني معتمد - تم التوليد بنظام مندوبك التجاري Offline", pageWidth / 2f, footerY, paint)

            document.finishPage(page)

            val outputStream = FileOutputStream(pdfFile)
            document.writeTo(outputStream)
            outputStream.close()
            document.close()

            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "سند قبض ${receipt.receiptNumber}")
                putExtra(Intent.EXTRA_TEXT, "سند قبض رقم ${receipt.receiptNumber} للعميل ${receipt.clientName}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "مشاركة أو طباعة سند القبض عبر...").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })

            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
