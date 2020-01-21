package dean.tryhard.project.baseproject.utils;

import android.bluetooth.BluetoothDevice;

import org.greenrobot.eventbus.EventBus;

public class EventBusUtils {

    /**
     * 绑定 接受者
     * @param subscriber
     */
    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    /**
     * 解绑定
     * @param subscriber
     */
    public static void unregister(Object subscriber){
        EventBus.getDefault().unregister(subscriber);
    }

    /**
     * 发送消息(事件)
     * @param eventId
     */
    public static void sendEvent(Integer eventId){
        EventBus.getDefault().post(eventId);
    }

    public static void sendEventNewBleDevice(BluetoothDevice bleDevice){
        EventBus.getDefault().post(bleDevice);
    }

    /**
     * 发送 粘性 事件
     *
     * 粘性事件，在注册之前便把事件发生出去，等到注册之后便会收到最近发送的粘性事件（必须匹配）
     * 注意：只会接收到最近发送的一次粘性事件，之前的会接受不到。
     * @param eventId
     */
    public static void sendStickyEvent(Integer eventId){
        EventBus.getDefault().postSticky(eventId);
    }


    /**
     * 移除 粘性 事件
     *
     * @param eventId
     */
    public static void removeStickyEvent(Integer eventId){
        EventBus.getDefault().removeStickyEvent(eventId);
    }
}