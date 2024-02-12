
import chiseltest._
import chisel3._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec
import scala.reflect.runtime.universe._
import java.io.PrintWriter


//测试一下scala的打印和各种语法 主打一个把玩相关的语法内容
class testScala extends AnyFlatSpec with ChiselScalatestTester{
    behavior of "SigmoidTest"
    it should "pass" in {
        val sigmoidDepth = 4096
        val connectNum = 4
        test(new SigmoidLUT(actWidth = 16, sigmoidDepth = sigmoidDepth,
                connectNum = connectNum)).withAnnotations(
            Seq( VerilatorBackendAnnotation,WriteVcdAnnotation)) { dut =>
            dut.clock.setTimeout(0)
            val sigmoidSource = scala.io.Source.fromResource("tansigDataChiselTest2.dat")
            val sigmoidData   = try sigmoidSource.getLines().toList.map(_.toInt) finally sigmoidSource.close()
            // sigmoidData.foreach(println)
            //    sigmoidData.take(100).foreach(println()) 
            // 每一个入口都访问同样的数据
            // 目前负数部分的测试是错误的
            // sigmid的输出是会打拍2个小节的
            // for(i <- 0 until sigmoidDepth/256)
            // 创建一个array的变量

            val dataArray = new Array[Int](30*connectNum)
            for(i <- 0 until 30)
            {
                for(j <- 0 until connectNum){
                    val inputaddr = i * 8
                    dut.io.inAddr(j).poke(inputaddr.U)
                val regshift = i-2
                // println(s"第${j}轮的相关信息") //使用大括号来框选特定的变量信息 并且通过添加s和$ 来打印我们需要的变量信息
                // println(regshift)
                // println(typeOf[regshift.type])
                // println(sigmoidData.getClass())
                if(regshift<=0)
                    {
                        dut.io.outData(j).expect(sigmoidData(0).S)
                    }
                else
                    {
                        dut.io.outData(j).expect(sigmoidData(regshift).S)
                    }
                val int_out = dut.io.outData(j).peek().litValue.toInt
                dataArray(i*connectNum+j) = int_out
                // println(int_out)              
                }
                dut.clock.step()
            }
            for(i <- 0 until 30*connectNum)
            {
                println(dataArray(i))
            }


            // 将 dataArray 写入文件
            val writer = new PrintWriter("dataArray.dat")
            try {
            for (data <- dataArray) {
                writer.println(data)
            }
            } finally {
            writer.close()
            }


            println("Test of SigmoidLUT success!")
        }
    }
}