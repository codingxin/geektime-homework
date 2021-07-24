相同堆内存下,堆内存增大的变化
		
	GC次数:
		串行GC < CMS GC < 并行GC < G1 GC 	1G堆内存
		串行GC < CMS GC < 并行GC < G1 GC 	2G堆内存
		
		串行GC < 并行GC < CMS GC < G1 GC 	4G堆内存
		
	GC平均耗时:
		串行GC > CMS GC > 并行GC > G1 GC 	1G堆内存
		串行GC > CMS GC > 并行GC > G1 GC 	2G堆内存
		串行GC > CMS GC > 并行GC > G1 GC 	4G堆内存
		
	GC总耗时
		串行GC > CMS GC > 并行GC > G1 GC	1G堆内存
		串行GC > CMS GC > 并行GC > G1 GC 	2G堆内存
		
		CMS GC > G1 GC > 串行GC > 并行GC 	4G堆内存
		
 总结:
	随着堆内存的增加
	1.GC的次数整体都是趋于减少的
	2.平均GC耗时整体都是趋于增加的
	3.串行GC和并行GC的总耗时都是趋于减少的,而CMS GC是趋于增加的,G1 GC有增加的也有减少的
	
	压测总结:
		RPS(每秒请求数):
			并行GC > G1 GC > CMS GC > 串行GC 	默认堆大小
			并行GC > 串行GC > CMS GC > G1 GC	4g堆大小
		平均响应时间:
		    并行GC < G1 GC < CMS GC < 串行GC    默认堆大小
			并行GC < G1 GC = 串行GC < CMS GC    4g堆大小
			
		使用并行GC,可以得到比其他GC很的吞吐量,整体开来并行GC都又不错的表现,
		当堆内存增加后,CMS GC就表现为和串行GC差不多的RPS,而G1 GC的RPS是最小的
		
		
		
		