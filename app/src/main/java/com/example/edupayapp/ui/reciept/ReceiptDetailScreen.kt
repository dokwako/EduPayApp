package com.example.edupayapp.ui.reciept

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.OutputStream

@Composable
fun ReceiptDetailScreen(
    studentName: String,
    amount: String,
    itemName: String,
    onBack: () -> Unit
) {
    // Grab the context to use for Toast messages and file saving
    val context = LocalContext.current

    // Generate a reference number that matches on both the UI and the PDF
    val refNumber = remember { "EP-${(100000..999999).random()}" }
    val date = "March 13, 2026"

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF9FAFB)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Official Receipt", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Text("EduPay Digital Transaction", color = Color.Gray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(24.dp))

                ReceiptDetailRow("Student Name", studentName)
                ReceiptDetailRow("Description", itemName)
                ReceiptDetailRow("Date", date)
                ReceiptDetailRow("Ref Number", refNumber)

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(24.dp))

                Text("Total Paid", color = Color.Gray, fontSize = 14.sp)
                Text("KES $amount", fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, color = Color(0xFF3B82F6))

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        // Trigger the PDF generation when clicked
                        generateAndSavePdf(context, studentName, itemName, amount, date, refNumber)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Download Receipt (PDF)", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(top = 24.dp)) {
            Text("Back to History", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ReceiptDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF6B7280), fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF111827))
    }
}

// --- CORE LOGIC: NATIVE PDF GENERATION ---
fun generateAndSavePdf(
    context: Context,
    studentName: String,
    itemName: String,
    amount: String,
    date: String,
    refNumber: String
) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Define our text styles (Fonts, sizes, colors)
        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 32f
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
        }
        val textPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = 18f
            color = android.graphics.Color.DKGRAY
        }
        val boldTextPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 18f
            color = android.graphics.Color.BLACK
        }

        // Draw everything onto the invisible PDF canvas
        val centerX = pageInfo.pageWidth / 2f
        canvas.drawText("EduPay Official Receipt", centerX, 80f, titlePaint)

        canvas.drawText("Date: $date", 50f, 150f, textPaint)
        canvas.drawText("Ref No: $refNumber", 50f, 180f, textPaint)
        canvas.drawLine(50f, 210f, 545f, 210f, textPaint) // Divider line

        canvas.drawText("Student Name:", 50f, 260f, textPaint)
        canvas.drawText(studentName, 300f, 260f, boldTextPaint)

        canvas.drawText("Payment For:", 50f, 300f, textPaint)
        canvas.drawText(itemName, 300f, 300f, boldTextPaint)
        canvas.drawLine(50f, 340f, 545f, 340f, textPaint) // Divider line

        titlePaint.textSize = 28f
        titlePaint.color = android.graphics.Color.parseColor("#3B82F6") // Brand Blue
        canvas.drawText("Total Paid: KES $amount", centerX, 410f, titlePaint)

        pdfDocument.finishPage(page)

        // Save PDF to the Android Downloads folder securely
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "EduPay_Receipt_$refNumber.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        if (uri != null) {
            val outputStream: OutputStream? = resolver.openOutputStream(uri)
            outputStream?.use { pdfDocument.writeTo(it) }
            Toast.makeText(context, "Receipt saved to Downloads", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Failed to save receipt", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}