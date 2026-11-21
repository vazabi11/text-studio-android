# Text Studio 1.0 — Android

โปรเจกต์ Android Studio แบบ Java + WebView ใช้ editor HTML ที่แนบมาใน assets โดยไม่มีสิทธิ์ INTERNET หรือสิทธิ์เข้าถึงพื้นที่จัดเก็บทั้งหมด ใช้ Storage Access Framework ของ Android เพื่อเปิด/บันทึกไฟล์ที่ผู้ใช้เลือกเอง

## สร้าง APK
1. แตก ZIP และเปิดโฟลเดอร์ที่มี settings.gradle ใน Android Studio
2. ใช้ JDK 17, Android SDK 35, Gradle 8.9 และ Android Gradle Plugin 8.7.3 (ต้องดาวน์โหลด dependency ครั้งแรก)
3. Sync Project with Gradle Files แล้วเลือก Build > Generate App Bundles or APKs > Generate APKs (ชื่อเมนูอาจต่างกันตามรุ่น) หรือใช้คำสั่ง gradle :app:assembleDebug จากรากโครงการเมื่อมี Gradle 8.9
4. ไฟล์ debug APK อยู่ที่ app/build/outputs/apk/debug/app-debug.apk ติดตั้งบน Android 8 ขึ้นไป และอนุญาตแหล่งติดตั้งที่ใช้หากจำเป็น

## ข้อจำกัด
- ต้นฉบับ Android ยังไม่ได้คอมไพล์และยังไม่ได้ทดสอบบนอุปกรณ์จริง เนื่องจากไม่มี SDK/Gradle ในระบบสร้างไฟล์นี้
- รองรับไฟล์ข้อความขนาดไม่เกิน 5 MiB; ไฟล์ใหญ่หรือมีบรรทัดมากอาจทำงานช้า
- การบันทึกทับขึ้นกับสิทธิ์และ document provider; หากบันทึกทับไม่ได้ให้ใช้ Save As
- การตรวจ encoding อัตโนมัติเป็นการเดา: UTF-8/UTF-16 และ fallback Windows-874; เลือกรหัสเองได้
- Markdown preview รองรับองค์ประกอบพื้นฐาน ไม่ใช่ CommonMark แบบสมบูรณ์; ไฟล์ HTML ไม่ถูก execute
- หากไฟล์มี line endings หลายแบบ จะปรับเป็นรูปแบบหลักเมื่อบันทึก; การแก้ไขและบันทึก Unicode ต้องใช้ encoding ที่รองรับอักขระ
- ร่างเก็บในพื้นที่ของแอปเมื่อเปิดตัวเลือกเท่านั้น; ไม่ควรใช้แทนการบันทึกจริง
- WebView ของระบบควรเป็นรุ่นปัจจุบัน
