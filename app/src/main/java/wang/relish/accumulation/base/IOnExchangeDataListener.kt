package wang.relish.accumulation.base

/**
 * Fragment和Activity通信的接口
 * Created by wangxina on 2017/2/22.
 */
interface IOnExchangeDataListener {
    /**
     * 在fragment 中得到Activity 存储的资料

     * @param key key
     * *
     * @return Object
     */
    fun onGetValueByKey(key: String): Any?

    /**
     * 在fragment中，往activity中传消息

     * @param object key
     */
    fun onSendMessage(`object`: Any)
}
