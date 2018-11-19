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
    
OpenSL ES   简单来说是一个嵌入式、跨平台、免费的、音频 处理库。

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






























