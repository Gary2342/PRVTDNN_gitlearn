// import chisel3._
// import chisel3.util._
// // import chisel3.tester._
// // import chisel3.tester.RawTester.test
// import scala.collection._
// // import chisel3.experimental.{withClock, withReset, withClockAndReset}

// class MyDemo extends Module {
//   val io = IO(new Bundle {
//     val in = Input(Bool())
//     val out = Output(UInt())
//   })

//     val mem = SyncReadMem(256, UInt(8.W))
//     val resultReg = Reg(UInt())
//     val addrReg = RegInit(0.U(8.W))

//     when(io.in ){
//       addrReg := addrReg + 1.U
//       resultReg := mem.read(addrReg)
//     }

//     io.out := resultReg
   
// }

// object MovingAverage extends App {
//   println("Hello World, I will now generate the Verilog file!")
//   // println(getVerilog(new Passthrough))
//   emitVerilog(new PRVTDNNTop())
// }


