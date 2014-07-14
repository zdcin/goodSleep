###goodSleep
这是一个定时让手机静音的应用，目的是防止休息的时候手机收到各种消息提醒，比如微信，短信，通知等发出声音，打扰休息，但是来电不会静音，闹铃不会静音


###bug list
####1. 开机自启不成功
####2. 开机自启后运行会有问题，比如来电话之后不能恢复音量， 之前保存的音量在intent信息中，随着关机，这个信息没有了，应该保存一个备份在配置文件中，并且监听音量改变事件，做合理的处理。
####3. 连续两次保存的时候也会把设置之前的音量信息覆盖掉

###test case
####1. 卸载干净，安装，启动，设置时间段，打电话测试
####2. 与上边一样，连续保存两次，打电话测试
####3. 在上边基础上，重新启动，打电话测试