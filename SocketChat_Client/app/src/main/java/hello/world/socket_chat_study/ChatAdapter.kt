package hello.world.socket_chat_study

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class ChatAdapter(
    private val context: Context,
    private val arrayList: ArrayList<ChatModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var preferences: SharedPreferences

    fun addItem(item: ChatModel) {//아이템 추가
        arrayList.add(item)
    }

    // 순서: 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        //getItemViewType 에서 뷰타입 1을 리턴받았다면 내채팅레이아웃을 받은 Holder를 리턴
        return if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_my_chat, parent, false)
            Holder(view)
        }
        //getItemViewType 에서 뷰타입 2을 리턴받았다면 상대채팅레이아웃을 받은 Holder2를 리턴
        else {
            view = LayoutInflater.from(context).inflate(R.layout.item_your_chat, parent, false)
            Holder2(view)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size

    }

    // 순서: 3
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        //onCreateViewHolder에서 리턴받은 뷰홀더가 Holder라면 내 채팅, item_my_chat의 뷰들을 초기화 해줌
        if (viewHolder is Holder) {
            viewHolder.chatText.text = arrayList[i].script
            viewHolder.chatTime.text = arrayList[i].data_time
        }
        //onCreateViewHolder에서 리턴받은 뷰홀더가 Holder2라면 상대의 채팅, item_your_chat의 뷰들을 초기화 해줌
        else if (viewHolder is Holder2) {
            viewHolder.chatYouImage.setImageResource(R.mipmap.ic_launcher)
            viewHolder.chatYouName.text = arrayList[i].name
            viewHolder.chatText.text = arrayList[i].script
            viewHolder.chatTime.text = arrayList[i].data_time
        }

    }

    // 내가 친 채팅 뷰홀더
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //친구목록 모델의 변수들 정의하는부분
        val chatText: TextView = itemView.findViewById(R.id.chat_Text)
        val chatTime: TextView = itemView.findViewById(R.id.chat_Time)
    }

    // 상대가 친 채팅 뷰홀더
    inner class Holder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //친구목록 모델의 변수들 정의하는부분
        val chatYouImage: ImageView = itemView.findViewById(R.id.chat_You_Image)
        val chatYouName: TextView = itemView.findViewById(R.id.chat_You_Name)
        val chatText: TextView = itemView.findViewById(R.id.chat_Text)
        val chatTime: TextView = itemView.findViewById(R.id.chat_Time)
    }

    // 순서: 1
    // 상황에 따라 뷰를 나눠서 보여줄 수 있음
    //여기서 뷰타입을 1, 2로 바꿔서 지정해줘야 내채팅 상대방 채팅을 바꾸면서 쌓을 수 있음
    override fun getItemViewType(position: Int): Int {
        preferences = context.getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)

        //내 아이디와 arraylist의 name이 같다면 내꺼 아니면 상대꺼
        return if (arrayList[position].name == preferences.getString("name", "")) {
            1
        } else {
            2
        }
    }
}