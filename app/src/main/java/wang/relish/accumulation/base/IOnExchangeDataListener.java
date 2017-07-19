package wang.relish.accumulation.base;

/**
 * Fragment和Activity通信的接口
 * Created by wangxina on 2017/2/22.
 */
public interface IOnExchangeDataListener {
    /**
     * 在fragment 中得到Activity 存储的资料
     *
     * @param key key
     * @return Object
     */
    Object onGetValueByKey(String key);

    /**
     * 在fragment中，往activity中传消息
     *
     * @param object key
     */
    void onSendMessage(Object object);
}
