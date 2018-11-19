######  FFmpeg解码流程   
```
---------------初始化FFMPEG线程  prepare(String source)------------------
1、注册解码器并初始化网络
    av_register_all();
    avformat_network_init();
2、打开文件或网络流
    AVFormatContext *mAvFormatContext = avformat_alloc_context();
    avformat_open_input(&mAvFormatContext, url, NULL, NULL)
3、获取流信息
    avformat_find_stream_info(mAvFormatContext, NULL)
4、获取音视频流
     for (int i = 0; i < mAvFormatContext->nb_streams; i++) {
        pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO//匹配音频流信息
        pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO//匹配视频流信息
     }
5、获取解码器
    AVCodec *mAvCodec = avcodec_find_decoder(audio->codecpar->codec_id);
6、利用解码器创建解码器上下文(环境)
    AVCodecContext *avCodecContext = avcodec_alloc_context3(dec);
    avcodec_parameters_to_context(audio->avCodecContext, audio->codecpar)
7、打开(创建对应的)解码器
    avcodec_open2(audio->avCodecContext, dec, 0)
    avcodec_open2(video->avCodecContext, dec, 0)
---------------初始化FFMPEG线程结束---------------------
-----------------player.start()-----------------------------
8、读取音视频帧
    supportMediaCodecHardware(){//硬解码检查
        const char *codecName = ((const AVCodec *) video->avCodecContext->codec)->name; //获取解码器名字
        bool supportMediacodec = callJava->onCallIsSupportHardware(codecName);          //java层Mediacodec支侍硬解码
            旧版：av_bitstream_filter_filter    可以实现头信息添加，但是容易造成内存泄漏，处理比较麻烦，给AVPacket添加指定解码格式的头信息。
            新版：AVBitStreamFilter    使用简单，没有内存泄漏问题（测试过）。
        AVBitStreamFilter bsFilter = av_bsf_get_by_name("h264_mp4toannexb");            //找到相应解码器的过滤器
        av_bsf_alloc(bsFilter, &video->abs_ctx)                                         //过滤器分配内存
        avcodec_parameters_copy(video->abs_ctx->par_in, video->avCodecParameters)       //添加解码器属性
        video->abs_ctx->time_base_in = video->time_base                                 //重置过滤器时间
        av_bsf_init(...)                                                                //初始化过滤器上下文
        supportMediacodec = callJava->onCallInitMediacodec(                             //初始化Mediacodec 和头信息
                        codecName,
                        video->avCodecContext->width,
                        video->avCodecContext->height,
                        video->avCodecContext->extradata_size,  //头信息csd_0 
                        video->avCodecContext->extradata_size,  //头信息csd_1
                        video->avCodecContext->extradata,       //头信息csd_0
                        video->avCodecContext->extradata        //头信息csd_1
    }
    audio->play();//音频线程播放
    video->play();//视频线程播放
    while (playStatus != NULL && !playStatus->exit) {//解码视频流,循环获取一帧帧数据AVpacket
        AVPacket *packet = av_packet_alloc();
        av_read_frame(pFormatCtx, packet);
         //C++队列缓存AVpacket   循环读取 av_read_frame  加入 std::queue<AVpacket>队列
         //因为解码获取AVpakcet需要耗费一定的时间，为了达到更好地流畅度）需要把解码出来的AVpacket先缓存到队列中，播放时直接从队里里面取。
        if(avPacket->stream_index == audio->streamIndex){   
            audio->queue->putAvpacket(avPacket);
        }else if(avPacket->stream_index == video->streamIndex){
            video->queue->putAvpacket(avPacket);
        }
    }
9.1 音频播放：
FFmpeg解码原始分组数据包(avPacket)为音频帧（AVframe） 
    FFmpeg对音频数据重采样生成PCM数据流程：
    1.FFmpeg  输入采样数据 比如(null,AV_CH_LAYOUT_SURROUND,AV_SAMPLE_FMT_S16,avFrame->sample_rate,avFrame...)
        SwrContext *swr_ctx = swr_alloc_set_opts(......)     
    2.FFmpeg  (转码)输出重采样生成的PCM数据 PcmBuffer
        int channelnb =swr_convert(swr_ctx,&PcmBuffer,avFrame->nb_samples,...) 
    3.soundTouch 重采样    
        1->把PCM数据给SoundTouch处理：
            soundTouch->putSamples(sampletypeBuffer, channelnb);//sampletypeBuffer这里要从8位转换为16位
        2->循环得到处理后的PCM数据：
            soundTouch->receiveSamples(sampletypeBuffer, buffersize / 4)
    4.播放PCM音频采样数据sampletypeBuffer
        pcmBufferQueue->Enqueue(audio->pcmBufferQueue,                      
                                (char *) audio->sampletypeBuffer,
                                bufferSize * 2 * 2);
9.1 视频播放：
    if(硬解码){
        //MediaCodec解码流程
        av_bsf_send_packet(abs_ctx, avPacket)    送入AVPacket过滤 添加解码头信息
        av_bsf_receive_packet(abs_ctx, avPacket) 接收过滤后的AVPacket
        av_usleep(getDelayTime(diff) * 1000 * 1000);//同步音频帧
        callJava->onCallDecodeAVPacket(avPacket->size, avPacket->data);
        
    }else{
        FFmpeg解码原始分组数据包(avPacket)为视频帧（AVframe） 
        if (avFrame->format == AV_PIX_FMT_YUV420P){//直接jni渲染yuv
         av_usleep(time * 1000 * 1000);//同步音频帧
         callJava->onCallRenderYUV(
                                avCodecContext->width,
                                avCodecContext->height,
                                avFrame->data[0],   //Y
                                avFrame->data[1],   //U
                                avFrame->data[2]);  //V
        }else{//转换格式 为 YUV420P
            AVFrame *avFrameYUV420P = av_frame_alloc();//声明一个空的YUV420P帧
            av_image_get_buffer_size(AV_PIX_FMT_YUV420P....)//给定参数存储图像所需的数据量的大小
            av_image_fill_arrays(avFrameYUV420P)//根据指定的图像*参数和提供的数组设置数据指针和行数
            SwsContext *swsContext = sws_getContext()
            sws_scale(swsContext,avFrameYUV420P,aVframe....)
            //在srcSlice中缩放图像切片，并将得到的缩放*切片放在dst中的图像中。切片是图像中连续*行的序列
         av_usleep(getDelayTime(diff) * 1000 * 1000);//同步音频帧
         callJava->onCallRenderYUV(                     //直接jni渲染yuv
                                avCodecContext->width,
                                avCodecContext->height,
                                avFrameYUV420P->data[0],    //Y
                                avFrameYUV420P->data[1],    //U
                                avFrameYUV420P->data[2]);   //V
        }
        
    }
    
10 停止并回收资源
    //.....
    if (mAvFormatContext != NULL) {
            avformat_close_input(&mAvFormatContext);
            avformat_free_context(mAvFormatContext);
            mAvFormatContext = NULL;
    }
```
###### FFmpeg获取视频AVFrame   通过AVpacket转换成AVFrame方法
```
步骤一：    avcodec_send_packet(avPacket)输入 原始分组数据包(avPacket) 给解码器
	AVPacket *avPacket = av_packet_alloc();
	queue->getAvpacket(avPacket);
	avcodec_send_packet(avCodecContext, avPacket);
步骤二：avcodec_receive_frame(avFrame)  从解码器返回解码的输出数据(avFrame)。
	AVFrame *avFrame = av_frame_alloc();
	avcodec_receive_frame(avCodecContext, avFrame);
解码完成后：
    1、释放avPacket队列  delete(queue);
    2、释放解码器上下文
        avcodec_close(avCodecContext);
        avcodec_free_context(&avCodecContext);

```
###### MediaCodec
```
MediaCodec： 是Android（api>=16）提供的一个多媒体硬解编码库，能实现音视频的编解码。
工作原理：
        其内部有2个队列，一个是输入队列，一个是输出队列。输入队列负责存储编解码前的原始数据存储，并输送给MediaCodec处理；
        输出队列负责存储编解码后的新数据，可以直接处理或保存到文件中。
```
######  FFmpeg  MediaCodec视频硬解码流程
```
硬解码逻辑流程：
1、检测视频是否可以被硬解
    根据FFmpeg中视频解码器的名称找到对应手机硬解码器，如果存在则可以硬解码，走硬解码流程；不存在就只能走软解码流程。
2、硬解码：
    使用MediaCodec直接解码AVpacket，此时需要对AVPacket进行格式过滤，然后MediaCodec解码后的数据用OpenGL ES渲染出来。
3、软解码：
    直接用OpenGL ES 渲染YUV数据。

硬解码流程1：     检测视频是否可以被硬解
    1、FFmpeg视频解码器名称获取
        ((const AVCodec*)(video->avCodecContext->codec))->name;
    2、测试找出FFmpeg视频解码器对应的硬解码器名称，如："h264“  "video/avc“
    3、遍历手机解码器查找是否存在： MediaCodecList

硬解码流程2：     AVPacket添加解码头信息
        FFmpeg解码获得的AVPacket只包含视频压缩数据，并没有包含相关的解码信息（比如：h264的sps pps头信息，AAC的adts头信息），
        没有这些编码头信息，解码器（MediaCodec）是识别不到不能解码的。在FFmpeg中，这些头信息是保存在解码器上下文
        （AVCodecContext）的extradata中的，所以我们需要为每一种格式的视频添加相应的解码头信息，这样解码器（MediaCodec）才能正
        确解析每一个AVPacket里的视频数据。
    解码头信息添加过程:
        旧版：av_bitstream_filter_filter    可以实现头信息添加，但是容易造成内存泄漏，处理比较麻烦，给AVPacket添加指定解码格式的头信息。
        新版：AVBitStreamFilter    使用简单，没有内存泄漏问题（测试过）。
        1、找到相应解码器的过滤器      bsfilter = av_bsf_get_by_name("h264_mp4toannexb");
        2、初始化过滤器上下文：        av_bsf_alloc(bsfilter, &bsf_ctx); //AVBSFContext;
        3、添加解码器属性：           avcodec_parameters_copy(bsf_ctx->par_in, video->codecpar); 
        4、初始化过滤器上下文         av_bsf_init(video->bsf_ctx)；
        5、送入AVPacket过滤          av_bsf_send_packet(bsf_ctx, avPacket)；
        6、接收过滤后的AVPacket：     av_bsf_receive_packet(bsf_ctx, avPacket);
        7、释放资源：                 av_bsf_free(&bsf_ctx);

硬解码流程3：     MediaCodec解码AVpacket(有头信息的)  
    1、初始化MediaCodec：解码器类型（mime），视频宽度（width），视频高度（height），最大数据输入大小(max_input_size)，csd-0，csd-1。
        ----------------在player.start()线程开始时初始化----------------
        mediaFormat = MediaFormat.createVideoFormat(mime, width, height);
        mediaFormat.setInteger(MediaFormat.KEY_WIDTH, width);
        mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, height);
        mediaFormat.setLong(MediaFormat.KEY_MAX_INPUT_SIZE, width * height);
        mediaFormat.setByteBuffer(“csd-0”, ByteBuffer.wrap(csd0)); //avCodecContext->extradata //添加相应的解码头信息
        mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(csd1)); //avCodecContext->extradata //添加相应的解码头信息
    2、MediaCodec开始解码：
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(10);
        if(inputBufferIndex >= 0)
         {
            ByteBuffer byteBuffer = mediaCodec.getInputBuffers()[inputBufferIndex];
            byteBuffer.clear();
            byteBuffer.put(bytes);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, size, pts, 0);
        }
            int index = mediaCodec.dequeueOutputBuffer(info, 10);
            while (index >= 0) {
            mediaCodec.releaseOutputBuffer(index, true);
            index = mediaCodec.dequeueOutputBuffer(info, 10);
        }
        size:avPacket->size， bytes :avPacket->data

硬解码流程4：     OpenGL渲染MediaCodec解码数据
    1、OpenGL生成纹理
    2、纹理绑定到SurfaceTexture上
    3、用SurfaceTexture做参数创建Surface
    4、MediaCodec解码的视频就往Surface发送，就显示出画面了

```


###### 什么是YUV格式？
```
YUV，是一种颜色编码方法。Y表示明亮度，也就是灰度值。U和V则是色度、浓度，作用是描述影像色彩及饱和度，用于指定像素的颜色。
主要用于电视系统以及模拟视频领域，它将亮度信息（Y）与色彩信息（UV）分离，没有UV信息一样可以显示完整的图像，显示出来将是黑白效果。
```
###### 什么是YUV420？什么是YUV420P？
```
YUV420是指：Y : UV = 4 : 1
YUV420P是指：YUV的排列方式，先将Y排列完，再将U排列完，最后将
V排列完。如：
    YYYYYYYYYYYYYYYY UUUU VVVV
    
FFmpeg解码出来的视频YUV数据是存储在AVFrame中的data里面，我们以YUV420P为视频数据给OpenGL渲染。
Y分量：frame->data[0]
U分量：frame->data[1]
V分量：frame->data[2]
绝大多数视频都是YUV420P格式的，对于不是YUV420P格式的，我们先将其转换（sws_scale）为YUV420P后再给OpenGL渲染。
```


######音视频同步方法：
```
第一种：音频线性播放，视频同步到音频上。
第二种：视频线性播放，音频同步到视频上。
第三种：用一个外部线性时间，音频和视频都同步到这个外部时间上。

音频播放时间和视频播放时间
    音频播放：
        音频播放的时长是PCM数据决定的，根据数据大小和采样率、通道数和位深度就能计算出播放的时长。只要采样率、通道数、位深度不变，
        扬声器播放    同一段PCM数据的时间就是固定不变的。
    视频播放：
        视频其实没有播放时长的概念，只有相邻视频画面帧直接的时间间隔，调整时间间隔就能改变视频画面的渲染速度，来实现视频的快慢控制。
音视频同步实现：
    1、PTS和time_base
        PTS即显示时间戳，这个时间戳用来告诉播放器该在什么时候显示这一帧的数据。
        time_base即时间度量单位（时间基），可以类比：米、千克这种单位。
    2、分别获取音频和视频的PTS（播放时间戳）：
        PTS = avFrame->pts * av_q2d(avStream->time_base);
    3、获取音视频PTS差值，根据差值来设置视频的睡眠时间达到和音频的相对同步。
        视频快了就休眠久点，视频慢了就休眠少点，来达到同步。

```
###### 视频seek功能
````
1、Seek函数：avformat_seek_file(pFormatCtx, -1, INT64_MIN, relsecds, INT64_MAX, 0);
relsecds单位： int64_t
2、Seek后还需要清除音频和视频的buffer： avcodec_flush_buffers(avCodecContext);
注：此时需要给avCodecContext添加线程锁，不然avcodec_send_packet和avcodec_receive_frame也会操作avCodecContext，导致崩溃。

````
###### 播放时间计算
```
*总时长： duration = pFormatCtx->duration / AV_TIME_BASE;
*当前AVframe时间：
	AVRational time_base = pFormatCtx->streams[i]->time_base
	now_time = frame->pts * av_q2d(time_base);
*当前播放时间：
	公式：PCM实际数据大小 / 每秒理论PCM大小；
	clock += buffersize / ((double)(sample_rate * 2 * 2));
```
###### SDK优化
````
由于解码用到了while循环，而不加睡眠的while循环会使CPU使用率提高30%左右，
因此我们需要为解码线程加上一定的睡眠时间来降低CPU使用率。

停止时回收创建的内存空间。

````






















