package shiroi_plugin.huya.helper

import java.io.*


class FileHelper(filePath: String, suffix:String = ".json") {
    //保存的目录
    private var filePath:String = ""

    private var suffix:String

    init {
        filePath.also {
            this.filePath = it
            createDir(this.filePath)
        }
        suffix.also { this.suffix = it }
    }

    //获取文件内容
    fun get(fileName:String): String {
        val fip = FileInputStream(File(filePath + fileName + suffix))
        // 构建FileInputStream对象
        val reader = InputStreamReader(fip, "UTF-8")
        // 构建InputStreamReader对象,编码与写入相同
        val sb = StringBuffer()
        while (reader.ready()) {
            sb.append(reader.read().toChar())
        }
        return sb.toString()
    }

    //文件是否存在
    fun exists(fileName: String): Boolean {
        return File(filePath + fileName + suffix).exists()
    }

    //文件创建
    fun create(fileName:String, message: String){
        val fop = FileOutputStream(File(filePath + fileName + suffix))
        // 构建FileOutputStream对象,文件不存在会自动新建
        val writer = OutputStreamWriter(fop, "UTF-8")
        // 写入到缓冲区
        writer.append(message)
        writer.close()
        fop.close()
    }

    fun delete(fileName: String): Boolean {
        val file = File(filePath + fileName + suffix)
        return if(file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    fun getFileList(type:String = "file"): MutableList<String> {
        var files = File(filePath).listFiles()
        var list = mutableListOf<String>()
        for(file in files) {
            if(file.isFile) {
                list.add(file.name.substring(0,file.name.lastIndexOf(".")))
            } else {
                list.add(file.name)
            }
        }
        return list
    }

    fun createDir(dir:String) {
        File(dir).mkdirs()
    }
}