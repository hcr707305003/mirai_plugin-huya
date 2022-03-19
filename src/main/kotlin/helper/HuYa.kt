package shiroi_plugin.huya.helper

import com.google.gson.Gson
import java.util.regex.Pattern

class HuYa(html:String? = "") {
    //名称
    var name:String = ""
    //id
    lateinit var id:String
    //头像
    var head:String = ""

    init {
        //id

        //名称
        var n = Pattern
            .compile("""<img id=['\"]avatar-img['\"].*alt=([\"'])?(?<alt>[^'\"]+)[^>]*>""", Pattern.CASE_INSENSITIVE)
            .matcher(html)
        while (n.find()) {
            name = n.group("alt")
        }

        //获取头像
        var h = Pattern
            .compile("""<img id=['\"]avatar-img['\"] src=([\"'])?(?<src>[^'\"]+)[^>]*>""", Pattern.CASE_INSENSITIVE)
            .matcher(html)
        while (h.find()) {
            head = h.group("src")
        }
    }

    override fun toString(): String {
        return ("{\"name\": \"$name\", \"id\": \"$id\", \"head\": \"$head\"}")
    }

    fun toObject(json:String) {
        var huya:HuYa = Gson().fromJson(json,HuYa::class.java)
        this.name = huya.name
        this.id = huya.id
        this.head = huya.head
    }
}