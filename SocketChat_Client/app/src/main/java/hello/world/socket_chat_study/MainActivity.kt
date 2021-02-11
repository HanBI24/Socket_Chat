package hello.world.socket_chat_study

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hello.world.socket_chat_study.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding ?= null
    private val mBinding get() = binding!!
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        mBinding.button.setOnClickListener {
            editor.putString("name", mBinding.editText.text.toString()).apply()
            startActivity(Intent(this, ChatRoomActivity::class.java))
        }
    }

}