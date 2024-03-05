import chiseltest._
import chisel3._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec
import scala.reflect.runtime.universe._
import java.io.PrintWriter

class PRVTDNNTest2 extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "RVTDNNTest"
  it should "pass" in {
    test(new PRVTDNNTop).withAnnotations(
        Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) { dut =>
          dut.clock.setTimeout(0)
          // dut.io.bramIF.enable.poke(true.B)
          val bramSource = scala.io.Source.fromResource("bramData_12.dat")
          val bramData  = try bramSource.getLines().toList.map(_.toInt) finally bramSource.close()
          val actSource = scala.io.Source.fromResource("actData_hyx.dat")
          val actData   = try actSource.getLines().toList.map(_.toInt) finally actSource.close()

          for(i <- 0 until dut.loadModule.bramDepth){
              dut.io.bramIF.wrAddr.poke(i.U)
              dut.io.bramIF.wrData.poke(bramData(i).S)
              dut.io.bramIF.wrEn.poke(true.B)
              dut.clock.step()
          }
          dut.io.bramIF.wrEn.poke(false.B)
          var bramOutput: List[Int] = List()
          for(i <- 0 until dut.loadModule.bramDepth){
              dut.io.bramIF.rdAddr.poke(i.U)
              dut.clock.step()
              dut.io.bramIF.rdData.expect(bramData(i).S)
              // 也可以直接 println(dut.io.bramIF.rdData.peek())，不需要考虑类型
              bramOutput = dut.io.bramIF.rdData.peek().litValue.toInt +: bramOutput
          }
          println("load Weight and Bias finished! ")
          println("Test of Bram success!")
          dut.io.load.poke(true.B)
          dut.clock.step(100)
          dut.io.load.poke(false.B)
          dut.clock.step(1000)

          val dataArray = new Array[Int](actData.length)
          var datareg1 = dut.io.outAct(0).peek().litValue.toInt
          var datareg2 = dut.io.outAct(0).peek().litValue.toInt
          var writecount = 0
          var flag = 0
          for(i <-0 until (actData.length/8+800)){
            // input data
            if((i*8)<actData.length)
            {
              for(j <- 0 until dut.polyphase) {
                dut.io.inAct(j).poke((actData(i*dut.polyphase + j ).S))
              }
            }

            datareg2 = dut.io.outAct(0).peek().litValue.toInt
            
            if(datareg2 != datareg1)
            {
                flag = 1
            }

            
            if((flag==1)&&(((writecount+1)*8)<=actData.length))
            {
                for(k <- 0 until dut.polyphase) 
                {
                   dataArray(writecount*dut.polyphase+k) = dut.io.outAct(k).peek().litValue.toInt
                }
                writecount = writecount + 1
            }

            //output delay control
            // current 4096 20 10 10 8 sigmoid
            //input to output delay is 61 steps
            // if((i>=62)&&(((i-62+1)*8)<=actData.length))
            // {
            //   // record output data to array int
            //     val output_count = i - 62 
            //     for(k <- 0 until dut.polyphase) 
            //     {
            //        dataArray(output_count*dut.polyphase+k) = dut.io.outAct(k).peek().litValue.toInt
            //     }
            // }

              dut.clock.step()
          }

            // 将 dataArray 写入文件
            val writer = new PrintWriter("output_dataArray2.dat")
            try {
            for (data <- dataArray) {
                writer.println(data)
            }
            } finally {
            writer.close()
            }

          
    }
  }
}

