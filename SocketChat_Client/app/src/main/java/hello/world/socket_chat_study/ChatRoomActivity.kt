package hello.world.socket_chat_study

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import hello.world.socket_chat_study.databinding.ActivityChatRoomBinding
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var preferences: SharedPreferences
    var arrayList = arrayListOf<ChatModel>()
    private val mAdapter = ChatAdapter(this, arrayList)
    private var binding: ActivityChatRoomBinding? = null
    private val mBinding get() = binding!!

    private var hasConnection: Boolean = false
    private var thread2: Thread? = null
    private var startTyping = false
    private var time = 2
    // http://Your IP address:5000(port number)
    private var mSocket: Socket = IO.socket("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)

        val lm = LinearLayoutManager(this)
        mBinding.chatRecyclerview.apply {
            adapter = mAdapter
            layoutManager = lm
            // 아이템이 추가 및 삭제 시 크기에서 오류가 발생하지 않도록
            setHasFixedSize(true)
        }

        if (savedInstanceState != null) hasConnection = savedInstanceState.getBoolean("hasConnection")
        if (hasConnection){} else {
            mSocket.connect()
            Log.d("connected", mSocket.connect().connected().toString())

            mSocket.on("connect user", onNewUser)
            mSocket.on("chat message", onNewMessage)

            val userId = JSONObject()
            try {
                // roomName: 채팅방 이름
                // Node.js는 key를 통해 특정 key를 가진 사람들끼리만 대화 가능할 수 있음
                // roomName을 입력하도록 하여 1:N도 가능
                userId.put("username", preferences.getString("name", "") + " Connected")
                userId.put("roomName", "room_example")
                Log.e("username",preferences.getString("name", "") + " Connected")

                //socket.emit은 메세지 전송임
                mSocket.emit("connect user", userId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        hasConnection = true

        mBinding.chatSendButton.setOnClickListener(this)
    }

    private var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val name: String
            val script: String
            val profileImage: String
            val dateTime: String
            try {
                Log.e("asdasd", data.toString())
                name = data.getString("name")
                script = data.getString("script")
                profileImage = data.getString("profile_image")
                dateTime = data.getString("date_time")

                val format = ChatModel(name, script, profileImage, dateTime)
                mAdapter.apply {
                    addItem(format)
                    notifyDataSetChanged()
                }
                Log.d("대화내용", script)
                Log.e("new me", name)
            } catch (e: Exception) {
                return@Runnable
            }
        })
    }

    //어플 키자마자 서버에  connect user 하고 프로젝트에 on new user 실행
    private var onNewUser: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val length = args.size
            if (length == 0) {
                return@Runnable
            }
            //Here i'm getting weird error..................///////run :1 and run: 0
            var username = args[0].toString()
            try {
                val `object` = JSONObject(username)
                username = `object`.getString("username")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        })
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it) {
                mBinding.chatSendButton -> {
                    // 아이템 추가
                    sendMessage()
                }
            }
        }
    }

    private fun sendMessage() {
        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val date = Date(now)
        // Locale.Nation_name: 해당 국가에 대해 맞춰짐
        val sdf = SimpleDateFormat("yyyy-MM-dd\nHH:mm", Locale.getDefault())
        val getTime = sdf.format(date)

        val message = mBinding.chatingText.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(message)) {
            return
        }
        mBinding.chatingText.setText("")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", preferences.getString("name", ""))
            jsonObject.put("script", message)
            jsonObject.put("profile_image", "example")
            jsonObject.put("date_time", getTime)
            jsonObject.put("roomName", "room_example")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e("챗룸", "sendMessage: 1" + mSocket.emit("chat message", jsonObject))
//        preferences.getString("name", "")?.let { Log.e("sendmmm", it) }

    }
}