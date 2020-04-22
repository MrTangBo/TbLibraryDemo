package com.tb.library.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.util.*


/**
 * @author: TB
 * @package: com.tb.design.library.tbActivityUtil
 * @description:
 * @date: 2018/11/14
 * @time: 16:51
 */
class ActivityManagerUtil {

    private var mActivities: Stack<Activity> = Stack()

    companion object {
        fun getInstance() = Holder.instance
    }

    private object Holder {
        val instance: ActivityManagerUtil = ActivityManagerUtil()
    }

    //添加activity
    fun addActivity(activity: Activity) {
        if (mActivities.contains(activity)) return
        mActivities.add(activity)
    }

    //关闭栈顶的Activity
    fun removeActivity(activity: Activity?) {
        if (activity != null) {
            if (mActivities.contains(activity)) {
                mActivities.remove(activity)
            }
            activity.finish()
        }
    }

    //将activity全部关闭掉
    fun clearAll() {
        for (activity in mActivities) {
            activity.finish()
        }
        mActivities.clear()
    }

    //关闭掉指定的activity,activityName
    fun clearOther(activityName: String) {
        for (activity in mActivities) {
            if (activity.javaClass.simpleName == activityName) {
                continue
            }
            activity.finish()
        }
    }

    //获取Activity栈
    fun getStack(): Stack<Activity> {
        return mActivities
    }

    //判断某一个类是否存在任务栈里面
    fun isExistMainActivity(activity: Class<*>, context: Context): Boolean {
        val intent = Intent(context, activity)
        val cmpName = intent.resolveActivity(context.packageManager)
        return cmpName != null
    }

    /*把某个activity压入栈顶*/
    fun pushActivtyTop(activity: Activity) {
        mActivities.push(activity)
    }

    //返回对象在堆栈中的位置
    fun getActivityIndex(activity: Activity): Int {
        return mActivities.search(activity)
    }


}
