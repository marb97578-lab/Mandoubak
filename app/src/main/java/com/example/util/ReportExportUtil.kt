package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.entity.ClientEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.PaymentReceiptEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.PurchaseInvoiceEntity
import com.example.data.entity.SaleInvoiceEntity
import com.example.data.entity.SupplierEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExportUtil {

    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    private val fileDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    /**
     * Helper to write UTF-8 BOM so Microsoft Excel opens Arabic text cleanly without garbled characters.
     */
    private fun writeUtf8Bom(fos: FileOutputStream) {
        fos.write(0xEF)
        fos.write(0xBB)
        fos.write(0xBF)
    }

    fun exportSalesReportCsv(
        context: Context,
        invoices: List<SaleInvoiceEntity>,
        currencySymbol: String
    ): File? {
        try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val fileName = "Mandoubak_Sales_${fileDateFormat.format(Date())}.csv"
            val file = File(exportDir, fileName)

            val fos = FileOutputStream(file)
            writeUtf8Bom(fos)
            val writer = OutputStreamWriter(fos, Charsets.UTF_8)

            writer.append("رقم الفاتورة,التاريخ,اسم العميل,الأصناف,المجموع الفرعي,الخصم,الإجمالي ($currencySymbol),المدفوع,المتبقي,طريقة الدفع,حالة الفاتورة\n")

            for (inv in invoices) {
                val dateStr = dateFormat.format(Date(inv.dateMillis))
                val status = if (inv.isCredit) "آجل" else "نقداً"
                val cleanedItems = "\"${inv.itemsSummary.replace("\"", "\"\"")}\""
                val client = "\"${inv.clientName.replace("\"", "\"\"")}\""
                writer.append("${inv.invoiceNumber},$dateStr,$client,$cleanedItems,${inv.subtotalAmount},${inv.discountAmount},${inv.totalAmount},${inv.paidAmount},${inv.remainingAmount},${inv.paymentMethod},$status\n")
            }

            writer.flush()
            writer.close()
            fos.close()

            shareFile(context, file, "تقرير المبيعات والفواتير (Excel/CSV)", "text/csv")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportPurchasesReportCsv(
        context: Context,
        purchases: List<PurchaseInvoiceEntity>,
        currencySymbol: String
    ): File? {
        try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val fileName = "Mandoubak_Purchases_${fileDateFormat.format(Date())}.csv"
            val file = File(exportDir, fileName)

            val fos = FileOutputStream(file)
            writeUtf8Bom(fos)
            val writer = OutputStreamWriter(fos, Charsets.UTF_8)

            writer.append("رقم الفاتورة,التاريخ,اسم المورد,الأصناف,المجموع الفرعي,الخصم,الإجمالي ($currencySymbol),المدفوع,المتبقي,طريقة الدفع,حالة الفاتورة\n")

            for (p in purchases) {
                val dateStr = dateFormat.format(Date(p.dateMillis))
                val status = if (p.isCredit) "آجل" else "مسددة"
                val cleanedItems = "\"${p.itemsSummary.replace("\"", "\"\"")}\""
                val supplier = "\"${p.supplierName.replace("\"", "\"\"")}\""
                writer.append("${p.invoiceNumber},$dateStr,$supplier,$cleanedItems,${p.subtotalAmount},${p.discountAmount},${p.totalAmount},${p.paidAmount},${p.remainingAmount},${p.paymentMethod},$status\n")
            }

            writer.flush()
            writer.close()
            fos.close()

            shareFile(context, file, "تقرير المشتريات والتوريد (Excel/CSV)", "text/csv")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportInventoryReportCsv(
        context: Context,
        products: List<ProductEntity>,
        currencySymbol: String
    ): File? {
        try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val fileName = "Mandoubak_Inventory_${fileDateFormat.format(Date())}.csv"
            val file = File(exportDir, fileName)

            val fos = FileOutputStream(file)
            writeUtf8Bom(fos)
            val writer = OutputStreamWriter(fos, Charsets.UTF_8)

            writer.append("الباركود,اسم المنتج,التصنيف,الكمية الحالية,الوحدة,سعر التكلفة ($currencySymbol),سعر البيع ($currencySymbol),قيمة التكلفة الإجمالية,قيمة البيع الإجمالية,الربح المتوقع,حد الطلب الأدنى,تاريخ الصلاحية,المورد\n")

            for (p in products) {
                val costTotal = p.costPrice * p.stockQuantity
                val saleTotal = p.salePrice * p.stockQuantity
                val expectedProfit = saleTotal - costTotal
                val pName = "\"${p.name.replace("\"", "\"\"")}\""
                val category = "\"${p.category.replace("\"", "\"\"")}\""
                val supplier = "\"${p.supplierName.replace("\"", "\"\"")}\""
                writer.append("${p.barcode},$pName,$category,${p.stockQuantity},${p.unit},${p.costPrice},${p.salePrice},$costTotal,$saleTotal,$expectedProfit,${p.minStockThreshold},${p.expirationDate},$supplier\n")
            }

            writer.flush()
            writer.close()
            fos.close()

            shareFile(context, file, "تقرير جرد وتقييم المخزون (Excel/CSV)", "text/csv")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportProfitLossCsv(
        context: Context,
        totalSales: Double,
        totalPurchases: Double,
        totalExpenses: Double,
        grossProfit: Double,
        netProfit: Double,
        currencySymbol: String
    ): File? {
        try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val fileName = "Mandoubak_Profit_Loss_${fileDateFormat.format(Date())}.csv"
            val file = File(exportDir, fileName)

            val fos = FileOutputStream(file)
            writeUtf8Bom(fos)
            val writer = OutputStreamWriter(fos, Charsets.UTF_8)

            writer.append("البند المالي المحاسبي,القيمة المالية ($currencySymbol),النسبة والملاحظة\n")
            writer.append("إجمالي المبيعات المحققة,$totalSales,الإيرادات العامة من الفواتير\n")
            writer.append("إجمالي تكلفة المشتريات,$totalPurchases,تكلفة البضاعة المشتراة\n")
            writer.append("إجمالي المصروفات التشغيلية,$totalExpenses,وقود وصيانة ونثريات\n")
            writer.append("إجمالي الربح التجاري (Gross Profit),$grossProfit,المبيعات - المشتريات\n")
            writer.append("صافي الربح الفعلي (Net Profit),$netProfit,المبيعات - المشتريات - المصروفات\n")

            writer.flush()
            writer.close()
            fos.close()

            shareFile(context, file, "تقرير الأرباح والخسائر المحاسبي (Excel/CSV)", "text/csv")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportClientsReportCsv(
        context: Context,
        clients: List<ClientEntity>,
        currencySymbol: String
    ): File? {
        try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val fileName = "Mandoubak_Clients_${fileDateFormat.format(Date())}.csv"
            val file = File(exportDir, fileName)

            val fos = FileOutputStream(file)
            writeUtf8Bom(fos)
            val writer = OutputStreamWriter(fos, Charsets.UTF_8)

            writer.append("رقم العميل,اسم العميل,رقم الجوال,العنوان والموقع,الرصيد والذمة الحالية ($currencySymbol),ملاحظات\n")

            for (c in clients) {
                val name = "\"${c.name.replace("\"", "\"\"")}\""
                val phone = "\"${c.phone.replace("\"", "\"\"")}\""
                val address = "\"${c.address.replace("\"", "\"\"")}\""
                val notes = "\"${c.notes.replace("\"", "\"\"")}\""
                writer.append("${c.id},$name,$phone,$address,${c.currentBalance},$notes\n")
            }

            writer.flush()
            writer.close()
            fos.close()

            shareFile(context, file, "سجل العملاء والذمم المدينة (Excel/CSV)", "text/csv")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportSuppliersReportCsv(
        context: Context,
        suppliers: List<SupplierEntity>,
        currencySymbol: String
    ): File? {
        try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val fileName = "Mandoubak_Suppliers_${fileDateFormat.format(Date())}.csv"
            val file = File(exportDir, fileName)

            val fos = FileOutputStream(file)
            writeUtf8Bom(fos)
            val writer = OutputStreamWriter(fos, Charsets.UTF_8)

            writer.append("رقم المورد,اسم المورد,الشركة,رقم الجوال,العنوان,الرصيد المستحق للمورد ($currencySymbol),ملاحظات\n")

            for (s in suppliers) {
                val name = "\"${s.name.replace("\"", "\"\"")}\""
                val comp = "\"${s.companyName.replace("\"", "\"\"")}\""
                val phone = "\"${s.phone.replace("\"", "\"\"")}\""
                val address = "\"${s.address.replace("\"", "\"\"")}\""
                val notes = "\"${s.notes.replace("\"", "\"\"")}\""
                writer.append("${s.id},$name,$comp,$phone,$address,${s.outstandingBalance},$notes\n")
            }

            writer.flush()
            writer.close()
            fos.close()

            shareFile(context, file, "سجل الموردين والشركات (Excel/CSV)", "text/csv")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportExpensesReportCsv(
        context: Context,
        expenses: List<ExpenseEntity>,
        currencySymbol: String
    ): File? {
        try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val fileName = "Mandoubak_Expenses_${fileDateFormat.format(Date())}.csv"
            val file = File(exportDir, fileName)

            val fos = FileOutputStream(file)
            writeUtf8Bom(fos)
            val writer = OutputStreamWriter(fos, Charsets.UTF_8)

            writer.append("رقم المصروف,التاريخ,بيان المصروف,التصنيف,المبلغ ($currencySymbol),طريقة الدفع,ملاحظات\n")

            for (e in expenses) {
                val dateStr = dateFormat.format(Date(e.dateMillis))
                val title = "\"${e.title.replace("\"", "\"\"")}\""
                val cat = "\"${e.category.replace("\"", "\"\"")}\""
                val notes = "\"${e.notes.replace("\"", "\"\"")}\""
                writer.append("${e.id},$dateStr,$title,$cat,${e.amount},${e.paymentMethod},$notes\n")
            }

            writer.flush()
            writer.close()
            fos.close()

            shareFile(context, file, "سجل المصروفات التشغيلية (Excel/CSV)", "text/csv")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun shareFile(context: Context, file: File, title: String, mimeType: String) {
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "تم تصدير ملف $title من تطبيق مندوبك")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "مشاركة أو فتح ملف التقرير عبر...").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}
