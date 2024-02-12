import chiseltest._
import chisel3._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec

// FIXME: 因为文件交互的原因，这个test是跑不下去的
class SigmoidTest extends AnyFlatSpec with ChiselScalatestTester{
    behavior of "SigmoidTest"
    it should "pass" in {
        val sigmoidDepth = 4096
        val connectNum = 35
        test(new SigmoidLUT(actWidth = 16, sigmoidDepth = sigmoidDepth,
                connectNum = connectNum)).withAnnotations(
            Seq( VerilatorBackendAnnotation,WriteVcdAnnotation)) { dut =>
            dut.clock.setTimeout(0)
            val sigmoidSource = scala.io.Source.fromResource("tansigDataChiselTest.dat")
            val sigmoidData   = try sigmoidSource.getLines.toList.map(_.toInt) finally sigmoidSource.close()
            // sigmoidData.foreach(println)
            //    sigmoidData.take(100).foreach(println()) 
            // 每一个入口都访问同样的数据
            // 目前负数部分的测试是错误的
            // sigmid的输出是会打拍2个小节的
            for(i <- 0 until sigmoidDepth/2){
                for(j <- 0 until connectNum){
                    val inputaddr = i * 8
                    dut.io.inAddr(j).poke(inputaddr.U)
                val regshift = i-2
                if(regshift<=0)
                    {
                        dut.io.outData(j).expect(sigmoidData(0).S)
                    }
                else
                    {
                        dut.io.outData(j).expect(sigmoidData(regshift).S)
                    }                
                }
                dut.clock.step()
            }

            println("Test of SigmoidLUT success!")
        }
    }
}