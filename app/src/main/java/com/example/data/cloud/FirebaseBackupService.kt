package com.example.data.cloud

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.dao.AppDao
import com.example.util.BackupManager
import com.example.util.BackupSettingsData
import com.example.util.BackupValidationResult
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CloudBackupRecord(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val backupType: String = "CLOUD", // "MANUAL", "DAILY", "WEEKLY", "CLOUD"
    val timestamp: Long = 0L,
    val formattedDate: String = "",
    val totalRecords: Int = 0,
    val productsCount: Int = 0,
    val clientsCount: Int = 0,
    val suppliersCount: Int = 0,
    val invoicesCount: Int = 0,
    val purchasesCount: Int = 0,
    val receiptsCount: Int = 0,
    val expensesCount: Int = 0,
    val stockMovementsCount: Int = 0,
    val payloadJson: String = "",
    val checksumSha256: String = "",
    val formattedSize: String = ""
)

/**
 * Service integrating Firebase Firestore as the secure cloud backup provider for Mandoubak.
 *
 * ARCHITECTURAL INVARIANT:
 * - Room Database remains the primary local database and single source of truth for daily operations.
 * - Firebase Firestore is used ONLY for Cloud Backup and Restore.
 * - Works completely offline during regular daily use.
 */
class FirebaseBackupService(
    private val context: Context,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // Secondary constructor resolving custom databaseId from xml resource
    constructor(context: Context) : this(
        context = context.applicationContext,
        db = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        auth = FirebaseAuth.getInstance()
    )

    private val displayDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun getCurrentUserEmail(): String? = auth.currentUser?.email ?: auth.currentUser?.displayName

    suspend fun signInWithGoogle(activityContext: Context): Result<FirebaseUser> {
        return try {
            val credentialManager = CredentialManager.create(activityContext)
            val serverClientId = activityContext.getString(R.string.default_web_client_id)
            val googleIdOption = GetSignInWithGoogleOption.Builder(
                serverClientId = serverClientId
            ).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("لم يتم العثور على بيانات المستخدم بعد تسجيل الدخول."))
                }
            } else {
                Result.failure(Exception("نوع بيانات الاعتماد غير مدعوم."))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("تم إلغاء عملية تسجيل الدخول."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun requireUserId(): String {
        return auth.currentUser?.uid ?: error("يجب تسجيل الدخول باستخدام حساب Google أولاً للوصول إلى النسخ السحابي.")
    }

    /**
     * Upload local Room Database state to Firebase Cloud Firestore.
     * All Room data is packed into a verified package and saved under user's private collection.
     */
    suspend fun uploadCloudBackup(
        appDao: AppDao,
        settings: BackupSettingsData,
        backupType: String = "CLOUD",
        customTitle: String? = null
    ): Result<CloudBackupRecord> {
        return try {
            val uid = requireUserId()
            val now = System.currentTimeMillis()
            val pkg = BackupManager.generateBackupPackage(appDao, settings, backupType, now)

            val backupId = "cloud_${now}"
            val title = customTitle ?: "نسخة سحابية - ${displayDateFormat.format(Date(now))}"
            val formattedDate = displayDateFormat.format(Date(now))
            val sizeBytes = pkg.rootJsonString.toByteArray(Charsets.UTF_8).size.toLong()
            val formattedSize = when {
                sizeBytes < 1024 -> "$sizeBytes بايت"
                sizeBytes < 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f ك.ب", sizeBytes / 1024f)
                else -> String.format(Locale.getDefault(), "%.1f م.ب", sizeBytes / (1024f * 1024f))
            }

            val record = CloudBackupRecord(
                id = backupId,
                userId = uid,
                title = title,
                backupType = backupType,
                timestamp = now,
                formattedDate = formattedDate,
                totalRecords = pkg.totalRecords,
                productsCount = pkg.productsCount,
                clientsCount = pkg.clientsCount,
                suppliersCount = pkg.suppliersCount,
                invoicesCount = pkg.invoicesCount,
                purchasesCount = pkg.purchasesCount,
                receiptsCount = pkg.receiptsCount,
                expensesCount = pkg.expensesCount,
                stockMovementsCount = pkg.stockMovementsCount,
                payloadJson = pkg.rootJsonString,
                checksumSha256 = pkg.checksumSha256,
                formattedSize = formattedSize
            )

            val docMap = hashMapOf<String, Any>(
                "id" to record.id,
                "userId" to record.userId,
                "title" to record.title,
                "backupType" to record.backupType,
                "timestamp" to record.timestamp,
                "formattedDate" to record.formattedDate,
                "totalRecords" to record.totalRecords,
                "productsCount" to record.productsCount,
                "clientsCount" to record.clientsCount,
                "suppliersCount" to record.suppliersCount,
                "invoicesCount" to record.invoicesCount,
                "purchasesCount" to record.purchasesCount,
                "receiptsCount" to record.receiptsCount,
                "expensesCount" to record.expensesCount,
                "stockMovementsCount" to record.stockMovementsCount,
                "payloadJson" to record.payloadJson,
                "checksumSha256" to record.checksumSha256
            )

            db.collection("users")
                .document(uid)
                .collection("backups")
                .document(backupId)
                .set(docMap)
                .await()

            Result.success(record)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieve list of cloud backups stored in Firestore for the current user.
     */
    suspend fun getCloudBackupsList(): Result<List<CloudBackupRecord>> {
        return try {
            val uid = requireUserId()
            val querySnapshot = db.collection("users")
                .document(uid)
                .collection("backups")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val list = querySnapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.getString("id") ?: doc.id
                    val userId = doc.getString("userId") ?: uid
                    val title = doc.getString("title") ?: "نسخة سحابية"
                    val backupType = doc.getString("backupType") ?: "CLOUD"
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    val formattedDate = doc.getString("formattedDate") ?: if (timestamp > 0) displayDateFormat.format(Date(timestamp)) else ""
                    val totalRecords = (doc.getLong("totalRecords") ?: 0L).toInt()
                    val productsCount = (doc.getLong("productsCount") ?: 0L).toInt()
                    val clientsCount = (doc.getLong("clientsCount") ?: 0L).toInt()
                    val suppliersCount = (doc.getLong("suppliersCount") ?: 0L).toInt()
                    val invoicesCount = (doc.getLong("invoicesCount") ?: 0L).toInt()
                    val purchasesCount = (doc.getLong("purchasesCount") ?: 0L).toInt()
                    val receiptsCount = (doc.getLong("receiptsCount") ?: 0L).toInt()
                    val expensesCount = (doc.getLong("expensesCount") ?: 0L).toInt()
                    val stockMovementsCount = (doc.getLong("stockMovementsCount") ?: 0L).toInt()
                    val payloadJson = doc.getString("payloadJson") ?: ""
                    val checksumSha256 = doc.getString("checksumSha256") ?: ""

                    val sizeBytes = payloadJson.toByteArray(Charsets.UTF_8).size.toLong()
                    val formattedSize = when {
                        sizeBytes < 1024 -> "$sizeBytes بايت"
                        sizeBytes < 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f ك.ب", sizeBytes / 1024f)
                        else -> String.format(Locale.getDefault(), "%.1f م.ب", sizeBytes / (1024f * 1024f))
                    }

                    CloudBackupRecord(
                        id = id,
                        userId = userId,
                        title = title,
                        backupType = backupType,
                        timestamp = timestamp,
                        formattedDate = formattedDate,
                        totalRecords = totalRecords,
                        productsCount = productsCount,
                        clientsCount = clientsCount,
                        suppliersCount = suppliersCount,
                        invoicesCount = invoicesCount,
                        purchasesCount = purchasesCount,
                        receiptsCount = receiptsCount,
                        expensesCount = expensesCount,
                        stockMovementsCount = stockMovementsCount,
                        payloadJson = payloadJson,
                        checksumSha256 = checksumSha256,
                        formattedSize = formattedSize
                    )
                } catch (_: Exception) {
                    null
                }
            }

            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Restore from a cloud backup directly into local Room Database.
     * Firebase does not replace Room; it serves strictly as the backup and restore source.
     */
    suspend fun restoreFromCloudBackup(
        backup: CloudBackupRecord,
        appDao: AppDao
    ): Result<BackupValidationResult> {
        return try {
            val payloadString = if (backup.payloadJson.isNotEmpty()) {
                backup.payloadJson
            } else {
                val uid = requireUserId()
                val doc = db.collection("users")
                    .document(uid)
                    .collection("backups")
                    .document(backup.id)
                    .get()
                    .await()
                doc.getString("payloadJson") ?: error("محتوى النسخة السحابية فارغ أو غير متوفر.")
            }

            val validation = BackupManager.validateBackupContent(payloadString)
            if (!validation.isValid) {
                return Result.failure(Exception(validation.errorMessage ?: "فشل التحقق من سلامة النسخة السحابية."))
            }

            val payload = validation.parsedPayload
                ?: return Result.failure(Exception("تعذر استخراج بيانات النسخة السحابية."))

            // Atomic restore to Room Database
            BackupManager.restoreFromPayload(appDao, payload).getOrThrow()

            Result.success(validation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete a cloud backup from Firestore.
     */
    suspend fun deleteCloudBackup(backupId: String): Result<Unit> {
        return try {
            val uid = requireUserId()
            db.collection("users")
                .document(uid)
                .collection("backups")
                .document(backupId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
