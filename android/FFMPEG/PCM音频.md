######FFmpeg对音频数据重采样生成PCM数据
```
基本概念：
采样率：每秒对音频数据采样的个数（44100hz）
采样位数：存储采样数据的位数(16bit 2字节)
输出声道：单声道、立体声（左右声道）等

重采样：就是把目标音频按照一定的格式重新采样编码成新的音频数据，方便统一处理，一般的采样标准是：44100HZ、16bit、双声道
采样对象：解码出来的音频帧（AVframe）
使用函数：
    SwrContext swr_ctx = swr_alloc_set_opts(
        struct SwrContext *s, // 传NULL
        int64_t out_ch_layout, // 输出声道布局
        enum AVSampleFormat out_sample_fmt, //输出采样位数格式
        int out_sample_rate, //输出采样率
        int64_t  in_ch_layout,  // 输入声道布局
        enum AVSampleFormat  in_sample_fmt, //输入采样位数格式
        int  in_sample_rate, //输入采样率
        int log_offset, // NULL
        void *log_ctx); // NULL
    Int nb = swr_convert(swr_ctx, 
        &out_buffer, //转码后输出的PCM数据大小
        frame->nb_samples, //输出采样个数
        (const uint8_t **) frame->data, //原始压缩数据
        frame->nb_samples); //输入采样个数

计算PCM数据大小：
size = 采样个数 * 声道数 * 单个采样点大小
如：44100HZ、16bit、立体声（2个声道）
size = 44100 * 2 * (16/8)
```
###### FFmpeg + OPenSL ES 实现音频播放
```
在OPenSL ES的回调函数中获取音频的PCM数据和大小，并播放。
int buffersize = wlAudio->resampleAudio();
if(buffersize > 0)
{
      (* wlAudio-> pcmBufferQueue)->Enqueue(
	wlAudio-> pcmBufferQueue, 
	(char *) wlAudio-> buffer, 
	buffersize);
}
```
###### 添加加载、暂停、播放功能
```
加载：判断队列里面是否有数据，没有的话就是加载状态，否则是播放状态。主要是C++调用Java方法。
暂停：(*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay,  SL_PLAYSTATE_PAUSED);
播放：(*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay,  SL_PLAYSTATE_PLAYING);
```
###### 变调变速 SoundTouch

```
变调：就是改变声音的音调
变速：就是改变声音播放速度

OpenSL ES可以实现变速播放，但是再改变速度的同时也改变了音调，这
种体验是不好的，所以我们不采用这种方式。

SoundTouch（ http://www.surina.net/soundtouch/ ）：
一个开源的声音处理库，可以直接对PCM数据进行处理，可单独改变声音
的音调和播放速度。
1、到官网下载最新源码（2.0）
2、提取出include头文件和SoundTouch源码
3、在Android Studio中集成SoundTouch源码

1、声明SoundTouch对象和内存变量：
	SoundTouch *soundTouch = NULL;
	SAMPLETYPE *sampleBuffer = NULL;
2、初始化对象和内存：
	soundTouch = new SoundTouch();
	sampleBuffer =(malloc(samplerate * 2 * 2 * 2 / 3));
3、设置音频数据参数：
	soundTouch->setSampleRate(samplerate);
   	soundTouch->setChannels(2);
4、把PCM数据给SoundTouch处理：
	soundTouch->putSamples(sampleBuffer, nb);
5、循环得到处理后的PCM数据：
    num = soundTouch->receiveSamples(sampleBuffer, data_size / 4);
6、设置变速和变调：
	soundTouch->setPitch(1.0); //变调
	soundTouch->setTempo(1.5);//变速
```
###### 8bitPCM转16bitPCM

```
因为FFmpeg解码出来的PCM数据是8bit （uint8）的，而SoundTouch中最低
是16bit（ 16bit integer samples ），所以我们需要将8bit的数据转换成16bit
后再给SoundTouch处理。
处理方式：
由于PCM数据在内存中是顺序排列的，所以我们先将第一个8bit的数据复制
到16bit内存的前8位，然后后8bit的数据再复制给16bit内存的后8bit，就能把
16bit的内存填满，然后循环复制，直到把8bit的内存全部复制到16bit的内存
中，计算公式如下：

for(int i = 0; i < data_size / 2 + 1; i++)
{
	sampleBuffer[i] = (buffer[i * 2] | ((buffer[i * 2 + 1]) << 8));
}

```
###### AAC音频格式
```
ADTS头部信息：
参考资料：https://blog.csdn.net/jay100500/article/details/52955232

```