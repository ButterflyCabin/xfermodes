# xfermodes
下图的区别在于dst和src的实际相交部分区域的不同。  
图一是dst和src的大小区域是完全相交的，图一其余部分都是透明的。  
图二是dst和src只有三分之一相交，即dst【圆】的右下角和src【矩形】的左上角处是相交的。  
图一和图二的主要区别在资源和src混合时绘制的起始点不一样。  
资源图：就是图一、图二的第一行的两图dst【黄色图】和 src【蓝色图】  
src混合时绘制的起始点分别是 (0，0) 和 (w/3，h/3)   
总结：xfermode时实际操作的是src所在的区域；图一的src区域（0，0，w，h）而图二的src区域是（w/3，h/3，w，h），src透明部分对部分操作的计算效果有影响。  
网上大多说Google的xfermode的Demo和自己实际的Demo的混合效果有些出入。实则是对xfermode的有效混合区域的误解。  
如理解有出入，请各位大佬多多指点。  
图三是根据上述理论进行的实战。图二的src区域是（w/5，h/5，w，h）



图一![图一](https://github.com/ButterflyCabin/xfermodes/blob/master/png/png1.png)图二![图二](https://github.com/ButterflyCabin/xfermodes/blob/master/png/png2.png)图三![图三](https://github.com/ButterflyCabin/xfermodes/blob/master/png/png3.png)
