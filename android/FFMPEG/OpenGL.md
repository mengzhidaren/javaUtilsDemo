###### OpenGL ES、OpenSL ES
```
OpenGL ES ：一个嵌入式的（2D/3D）图形处理库
    第一步： 继承GLSurfaceView
    第二步： 实现接口GLSurfaceView.Renderer{
                void onSurfaceCreated(GL10 gl, EGLConfig config);
                void onSurfaceChanged(GL10 gl, int width, int height);
                void onDrawFrame(GL10 gl);
            }
    第三步： 编写glsl脚本（render）


EGL：
是OpenGL ES和本地窗口系统的接口，不同平台上EGL配置是不一样的，而
OpenGL的调用方式是一致的，就是说：OpenGL跨平台就是依赖于EGL接口。

为什么要自己创建EGL环境？
当我们需要把同一个场景渲染到不同的Surface上时，此时系统GLSurfaceView
就不能满足需求了，所以我们需要自己创建EGL环境来实现渲染操作。

注意：OpenGL整体是一个状态机，通过改变状态就能改变后续的渲染方式，而
EGLContext（EgL上下文）就保存有所有状态，因此可以通过共享EGLContext
来实现同一场景渲染到不同的Surface上



OpenSL ES   
简单来说是一个嵌入式、跨平台、免费的、音频 处理库。

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

###### 为什么用OpenGL来处理YUVP颜色格式视频？
```
OpenGL中是不能直接渲染YUV数据的，但是我们可以用3个纹理来分别获取Y、U和V的值，然后根据公式：
r = y + 1.403 * v;
g = y - 0.344 * u - 0.714 * v;
b = y + 1.770 * u;
转为rgb颜色格式显示出来。这个转换过程是在GPU中完成的，计算效率比在CPU中计算高很多倍！
```
###### OpenGL ES加载Shader
```
1、创建shader（着色器：顶点或片元）
	int shader = GLES20.glCreateShader(shaderType);
2、加载shader源码并编译shader
	GLES20.glShaderSource(shader, source);
	GLES20.glCompileShader(shader);
3、检查是否编译成功：
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
4、创建一个渲染程序：
	int program = GLES20.glCreateProgram();
5、将着色器程序添加到渲染程序中：
	GLES20.glAttachShader(program, vertexShader);
6、链接源程序：
	GLES20.glLinkProgram(program);
9、检查链接源程序是否成功
    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
10、得到着色器中的属性：
    int aPositionHandl  = GLES20.glGetAttribLocation(programId, "av_Position");
11、使用源程序：
	GLES20.glUseProgram(programId);
12、使顶点属性数组有效：
	GLES20.glEnableVertexAttribArray(aPositionHandl);	
13、为顶点属性赋值：
    GLES20.glVertexAttribPointer(aPositionHandl, 2, GLES20.GL_FLOAT, false, 8,vertexBuffer);
14、绘制图形：
	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);


```






























