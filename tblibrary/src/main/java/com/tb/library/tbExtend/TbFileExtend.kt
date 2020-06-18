package com.tb.library.tbExtend


import com.tb.library.base.TbApplication
import java.io.File
import java.text.DecimalFormat

/**
 *@作者：tb
 *@时间：2019/7/5
 *@描述：File扩展
 */
/*file重命名*/
fun File?.tbFileRename(newFileName: String?): Boolean {
    // 文件为空返回false
    if (this == null) return false
    // 文件不存在返回false
    if (!this.exists()) return false
    // 新的文件名为空返回false
    if (newFileName.isNullOrEmpty()) return false
    // 如果文件名没有改变返回true
    if (newFileName == this.name) return true
    val newFile = File(this.parent!! + File.separator + newFileName)
    return !newFile.exists() && this.renameTo(newFile)
}

/*file创建*/
fun Any.tbFileCreate(filePath: String, fileName: String): File {
    val file = File(filePath + File.separator + fileName)
    if (!file.exists()) {
        file.createNewFile()
    }
    return file
}

/*根据路径获取file*/
fun Any.tbFileFromPath(filePath: String?): File? {
    if (filePath.isNullOrEmpty()) return null
    return File(filePath)
}

/*file删除单个*/
fun Any.tbFileDelete(filePath: String, fileName: String): Boolean {
    val file = File(filePath + File.separator + fileName)
    return file.exists() && file.delete()
}

/*file删除目录*/
fun File?.tbFileDeleteDir(): Boolean {
    if (this == null) return false
    if (!exists()) return false
    if (isFile) return false
    listFiles()?.forEach {
        if (it.isFile) {
            it.delete()
        } else if (it.isDirectory) {
            it.tbFileDeleteDir()
        }
    }
    return delete()
}

/*计算某个file或者文件夹的大小*/
fun File?.tbFileDirSize(): Long {
    if (this == null) return 0
    if (!exists()) return 0
    var size: Long = 0
    if (isFile) {
        size += length()
    } else {
        this.listFiles()!!.forEach {
            size += if (it.isFile) {
                it.length()
            } else {
                it.tbFileDirSize()
            }
        }
    }
    return size
}

/* 转换文件大小*/
fun Long.tbFileSizeFormet(pattern: String = "0.00"): String {
    val df = DecimalFormat(pattern)
    return when {
        this < 1024 -> "${df.format(this)}B"
        this < 1048576 -> "${df.format(this / 1024)}KB"
        this < 1073741824 -> "${df.format(this / 1048576)}MB"
        else -> "${df.format(this / 1073741824)}GB"
    }
}

/*清除缓存*/
fun Any.tbFileCleanCache(): Boolean {
    return TbApplication.mApplicationContext.cacheDir.tbFileDeleteDir()
}
