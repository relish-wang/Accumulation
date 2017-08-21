package wang.relish.accumulation.util

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

/**
 * MD5
 * @author Jupiter-Z (2014-11-06 14:16)
 */
object MD5 {
    // MD5変換
    fun md5(str: String?): String? {
        var str = str
        if (str != null && str != "") {
            try {
                val md5 = MessageDigest.getInstance("MD5")
                val HEX = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
                val md5Byte = md5.digest(str.toByteArray(charset("UTF8")))
                val sb = StringBuilder()
                for (aMd5Byte in md5Byte) {
                    sb.append(HEX[(aMd5Byte and 0xff.toByte()) / 16])
                    sb.append(HEX[(aMd5Byte and 0xff.toByte()) % 16])
                }
                str = sb.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                AppLog.e("MD5", "md5", "NoSuchAlgorithmException")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                AppLog.e("MD5", "md5", "UnsupportedEncodingException")
            }

        }
        return str
    }

}
