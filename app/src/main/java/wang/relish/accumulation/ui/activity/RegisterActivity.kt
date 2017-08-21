package wang.relish.accumulation.ui.activity

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import wang.relish.accumulation.App
import wang.relish.accumulation.R
import wang.relish.accumulation.base.BaseActivity
import wang.relish.accumulation.entity.User
import wang.relish.accumulation.util.PhoneUtils
import wang.relish.accumulation.util.SPUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*


class RegisterActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_register
    }

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {
        mToolbar?.setTitle(R.string.register)
    }

    internal var rl_head: RelativeLayout? = null
    internal var iv_head: ImageView? = null
    internal var iv_camera: ImageView? = null
    internal var et_name: EditText? = null
    internal var et_mobile: EditText? = null
    internal var et_code: EditText? = null
    internal var et_pwd: EditText? = null
    internal var et_repeat_pwd: EditText? = null
    internal var btn_get_verify_code: Button? = null

    private var photo: String? = null
    private var mobile: String? = null
    private var name: String? = null
    private var verify_code = ""
    private var password: String? = null
    private var repeat_pwd: String? = null

    override fun parseIntent(intent: Intent) {
        super.parseIntent(intent)
        photo = checkStringNull(intent.getStringExtra(PHOTO))
        name = checkStringNull(intent.getStringExtra(NAME))
        mobile = checkStringNull(intent.getStringExtra(MOBILE))
        verify_code = checkStringNull(intent.getStringExtra(VERIFY_CODE))
        password = checkStringNull(intent.getStringExtra(PASSWORD))
        repeat_pwd = checkStringNull(intent.getStringExtra(REPEAT_PWD))
    }

    override fun initViews(savedInstanceState: Bundle?) {
        rl_head = findViewById(R.id.rl_head) as RelativeLayout
        iv_head = findViewById(R.id.iv_head) as ImageView
        iv_camera = findViewById(R.id.iv_camera) as ImageView
        et_name = findViewById(R.id.et_name) as EditText
        et_mobile = findViewById(R.id.et_mobile) as EditText
        et_code = findViewById(R.id.et_code) as EditText
        et_pwd = findViewById(R.id.et_pwd) as EditText
        et_repeat_pwd = findViewById(R.id.et_repeat_pwd) as EditText
        btn_get_verify_code = findViewById(R.id.btn_get_verify_code) as Button
        if (TextUtils.isEmpty(photo)) {
            Glide.with(this).load(photo)
        }
        et_name?.setText(name)
        et_mobile?.setText(mobile)
        et_code?.setText(verify_code)
        et_pwd?.setText(password)
        et_repeat_pwd?.setText(repeat_pwd)

        rl_head?.setOnClickListener(this)
        btn_get_verify_code?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rl_head -> if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                setHeadPhoto()
            }
            R.id.btn_get_verify_code -> if (!TextUtils.isEmpty(et_mobile?.text.toString().trim { it <= ' ' })) {
                if (et_mobile?.text.toString().trim { it <= ' ' }.length == 11) {
                    mobile = et_mobile?.text.toString().trim { it <= ' ' }
                    if (mobile!!.matches(PhoneUtils.MOBILE_PATTERN.toRegex())) {
                        verify_code = generateCode()
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 2)
                        } else {
                            sendSms()
                        }
                    } else {
                        Toast.makeText(this, R.string.phone_number_error, Toast.LENGTH_LONG).show()
                        et_mobile?.requestFocus()
                    }
                } else {
                    Toast.makeText(this, R.string.phone_number_error, Toast.LENGTH_LONG).show()
                    et_mobile?.requestFocus()
                }
            } else {
                Toast.makeText(this, R.string.phone_number_empty, Toast.LENGTH_LONG).show()
                et_mobile?.requestFocus()
            }
        }
    }


    private fun sendSms() {
        sendNotification("新消息", getString(R.string.send_verfiy_code_message, verify_code))
    }

    private fun setHeadPhoto() {
        AlertDialog.Builder(this)
                ?.setItems(R.array.set_head_click) { dialog, which ->
                    when (which) {
                        0 -> takePhoto()
                        1 -> openAlbum()
                        2 -> dialog.dismiss()
                    }
                }
                .create()
                .show()

    }

    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_PHOTO)
    }

    private fun takePhoto() {
        val outputImage = File(externalCacheDir, "output_image.jpg")
        try {
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this,
                    "wang.relish.accumulation.fileprovider", outputImage)
        } else {
            uri = Uri.fromFile(outputImage)
        }
        //启动相机程序
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, TAKE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                //将拍摄的照片显示出来
                try {
                    photo = uri!!.path
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri!!))
                    runOnUiThread {
                        iv_head?.scaleType = ImageView.ScaleType.CENTER_CROP
                        iv_head?.setImageBitmap(bitmap)
                        iv_camera?.visibility = View.GONE
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            }
            CHOOSE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    var imagePath: String? = null
                    val uri = data.data
                    if (DocumentsContract.isDocumentUri(this, uri)) {
                        //如果是Document类型的uri，则通过document id处理
                        val docId = DocumentsContract.getDocumentId(uri)
                        if (TextUtils.equals("com.android.providers.media.documents",
                                uri.authority)) {
                            val id = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]//解析出数字格式的id
                            val selection = MediaStore.Images.Media._ID + "=" + id
                            imagePath = getImagePath(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
                        } else if (TextUtils.equals("com.android.providers.downloads.documents",
                                uri.authority)) {
                            val contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"),
                                    java.lang.Long.valueOf(docId)!!)
                            imagePath = getImagePath(contentUri, null)
                        }
                    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                        //如果是content类型的Uri，则使用普通方法处理
                        imagePath = getImagePath(uri, null)
                    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                        //如果是file类型的Uri，直接获取图片路径即可
                        imagePath = uri.path
                    }
                    photo = imagePath
                    displayImage(imagePath!!)
                } else {
                    val uri = data.data
                    val imagePath = getImagePath(uri, null)
                    photo = imagePath
                    displayImage(imagePath!!)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                1 -> openAlbum()
                2 -> sendSms()
            }
        } else {
            showMessage(R.string.permission_denied_message)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.register, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.register -> {
                //注册
                val name = et_name?.text.toString()
                val mobile = et_mobile?.text.toString()
                val code = et_code?.text.toString()
                val pwd = et_pwd?.text.toString()
                val repeat_pwd = et_repeat_pwd?.text.toString()
                if (TextUtils.isEmpty(name)) {
                    showMessage("名字不得为空")
                    return false
                }
                if (TextUtils.isEmpty(mobile)) {
                    showMessage("手机号不得为空")
                    return false
                }
                if (!mobile.matches(PhoneUtils.MOBILE_PATTERN.toRegex())) {
                    showMessage("请填写正确的手机号码")
                    return false
                }
                if (TextUtils.isEmpty(code)) {
                    showMessage("验证码不得为空")
                    return false
                }
                if (!TextUtils.equals(code, verify_code)) {
                    if (et_code?.text.toString().trim { it <= ' ' }.length == 4) {
                        val iCord = et_code?.text.toString().trim { it <= ' ' }
                        if (!TextUtils.equals(verify_code, iCord)) {
                            Toast.makeText(this, R.string.verify_code_error, Toast.LENGTH_LONG).show()
                            return false
                        }
                    } else {
                        Toast.makeText(this, R.string.please_input_correct_verify_code, Toast.LENGTH_LONG).show()
                        et_code?.requestFocus()
                    }

                }
                if (TextUtils.isEmpty(pwd)) {
                    showMessage("密码不得为空")
                    return false
                }
                if (!TextUtils.equals(pwd, repeat_pwd)) {
                    showMessage("两次密码输入不一致")
                    return false
                }
                val user = User()
                user.name = name
                user.photo = photo
                user.mobile = mobile
                user.password = pwd
                RegisterTask().execute(user)
            }
        }
        return true
    }


    private inner class RegisterTask : AsyncTask<User, Void, User>() {

        override fun doInBackground(vararg params: User): User? {
            params[0].save()
            return User.findByMobile(params[0].mobile!!)
        }

        override fun onPostExecute(user: User?) {
            super.onPostExecute(user)
            if (user != null) {
                SPUtil.putString("mobile", user.mobile)
                AlertDialog.Builder(this@RegisterActivity)
                        .setTitle("注册成功")
                        ?.setMessage("恭喜用户【" + user.name + "】注册成功！")
                        ?.setPositiveButton("回到登录页",
                         { dialog, which ->
                            App.USER = user
                            setResult(Activity.RESULT_OK)
                            finish()
                        })?.create()?.show()
            } else {
                showMessage("注册失败，请重试")
            }
        }
    }

    /**
     * 通过Uri和selection来获取真实的图片路径

     * @param uri       uri
     * *
     * @param selection selection
     * *
     * @return 真实图片路径
     */
    private fun getImagePath(uri: Uri, selection: String?): String? {
        var path: String? = null
        val cursor = contentResolver.query(uri, null, selection, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            cursor.close()
        }
        return path
    }

    private fun displayImage(imagePath: String) {
        if (!TextUtils.isEmpty(imagePath)) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            iv_head?.scaleType = ImageView.ScaleType.CENTER_CROP
            iv_head?.setImageBitmap(bitmap)
            iv_camera?.visibility = View.GONE
        } else {
            Toast.makeText(this, "设置头像失败，请重试", Toast.LENGTH_SHORT).show()
        }
    }


    protected fun sendNotification(title: String, message: String) {
        Thread(Runnable {
            runOnUiThread { showMessage("发送验证码成功！") }
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            val builder = NotificationCompat.Builder(activity)
            builder?.setSmallIcon(R.mipmap.icon_transparent)
                    ?.setContentText(message)
                    ?.setContentTitle(title)
                    ?.setTicker("新消息")
                    ?.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS)
                    ?.setAutoCancel(true)
                    ?.setPriority(Notification.PRIORITY_MAX)
                    ?.setOnlyAlertOnce(true)
                    ?.setShowWhen(true)
                    ?.setWhen(System.currentTimeMillis())
            val intent = Intent(activity, RegisterActivity::class.java)
            val name = et_name?.text.toString()
            val mobile = et_mobile?.text.toString()
            val password = et_pwd?.text.toString()
            val repeat_pwd = et_repeat_pwd?.text.toString()
            intent.putExtra(PHOTO, photo)
            intent.putExtra(NAME, name)
            intent.putExtra(MOBILE, mobile)
            intent.putExtra(VERIFY_CODE, verify_code)
            intent.putExtra(PASSWORD, password)
            intent.putExtra(REPEAT_PWD, repeat_pwd)
            val pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            builder?.setContentIntent(pendingIntent)
            val notification = builder.build()
            val manager = NotificationManagerCompat.from(this@RegisterActivity)
            manager.notify(1, notification)
            runOnUiThread { et_code?.setText(verify_code) }
        }).start()
    }

    companion object {

        private val PHOTO = "photo"
        private val NAME = "name"
        private val MOBILE = "mobile"
        private val VERIFY_CODE = "verify_code"
        private val PASSWORD = "password"
        private val REPEAT_PWD = "repeat_pwd"

        val TAKE_PHOTO = 1
        val CHOOSE_PHOTO = 2


        private fun generateCode(): String {
            val sb = StringBuilder()
            val r = Random()
            for (i in 0..3) {
                sb.append(r.nextInt(10))
            }
            return sb.toString()
        }

        private var uri: Uri? = null

        private fun checkStringNull(str: String?): String {
            return if (TextUtils.isEmpty(str)) "" else str!!
        }
    }
}
