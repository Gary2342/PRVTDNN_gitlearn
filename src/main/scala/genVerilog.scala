import chisel3.stage.ChiselGeneratorAnnotation
import circt.stage.{ChiselStage, FirtoolOption}

object genVerilog extends App{
  (new ChiselStage).execute(
    Array("--target", "verilog"),
    Seq(ChiselGeneratorAnnotation(() => new PRVTDNNTop),
      FirtoolOption("--disable-all-randomization")))
}
